package org.dstadler.commoncrawl.elasticsearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.LogManager;
import org.dstadler.commoncrawl.datalayer.DataAccess;
import org.dstadler.commoncrawl.datalayer.DataAccessFactory;
import org.dstadler.commoncrawl.datalayer.DatabaseStarter;
import org.dstadler.commoncrawl.jpa.POIStatus;
import org.dstadler.commons.http.HttpClientWrapper;
import org.dstadler.commons.http.NanoHTTPD;
import org.dstadler.commons.logging.jdk.LoggerFactory;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * Reads POIStatus objects from the database and sends them to Elasticsearch.
 *
 * There are some derived classes which read the data from exported files to
 * speed up processing.
 */
public class ElasticsearchWriter {
    private static final Logger log = LoggerFactory.make();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // How much data can be sent in one bulk
    // This is currently defined by the NGINX proxy limit in the test-setup
    private static final int MAX_BULK_SIZE = 9*1024*1024;

    // This is the number of objects that are collected for sending,
    // the actual bulks are then further split up to observe MAX_BULK_SIZE
    // as main limit
    private static final int ES_BULK_SIZE = 10000;

    public static void main(String[] args) throws Exception {
        LoggerFactory.initLogging();

        long start = System.currentTimeMillis();

        if(args.length <= 2) {
            log.severe("Usage: ElasticsearchWriter <eshost> <esuser> <espassword>");
            System.exit(1);
        }

        setupTemplate(args[0], args[1], args[2]);

        Runnable shutdown = DatabaseStarter.ensureDatabase(11527);

        try (DataAccess access = DataAccessFactory.getInstance(DataAccessFactory.DB_PROD)) {
            writeDocuments(access, args[0], args[1], args[2]);
        } finally {
            // ensure that the database is shut down
            shutdown.run();
        }

        log.info("Done after " + (System.currentTimeMillis() - start)/1000 + " seconds");

        LogManager.shutdown();
    }

    protected static void writeDocuments(Iterator<POIStatus> records, String esHost, String esUser, String esPassword) throws IOException, InterruptedException {
        try (HttpClientWrapper httpClient = new HttpClientWrapper(esUser, esPassword, 60_000)) {
            long position = 0;
            while(records.hasNext()) {
                List<POIStatus> results = new ArrayList<>();
                for (int i = 0; i < ES_BULK_SIZE && records.hasNext(); i++) {
                    results.add(records.next());
                }

                log.info(String.format("Handling %d results starting at %d",
                        results.size(), position));

                sendDocuments(esHost, httpClient, results);

                position += results.size();
            }
        }
    }

    private static void writeDocuments(DataAccess access, String esHost, String esUser, String esPassword) throws IOException, InterruptedException {
        try (HttpClientWrapper httpClient = new HttpClientWrapper(esUser, esPassword, 60_000)) {
            CriteriaBuilder criteriaBuilder = access.getEm().getCriteriaBuilder();

            CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
            countQuery.select(criteriaBuilder.count(countQuery.from(POIStatus.class)));
            Long count = access.getEm().createQuery(countQuery).getSingleResult();
            log.info("Found " + count + " items to send");

            CriteriaQuery<POIStatus> criteriaQuery = criteriaBuilder.createQuery(POIStatus.class);
            Root<POIStatus> from = criteriaQuery.from(POIStatus.class);
            CriteriaQuery<POIStatus> select = criteriaQuery.select(from);

            TypedQuery<POIStatus> typedQuery = access.getEm().createQuery(select);

            int pagePosition = 1;
            int pageSize = 400;
            while (pagePosition < count.intValue()) {
                typedQuery.setFirstResult(pagePosition - 1);
                typedQuery.setMaxResults(pageSize);

                List<POIStatus> results = typedQuery.getResultList();

                log.info(String.format("Handling %d results starting at %d of %d items overall, %.4f percent done",
                        results.size(), pagePosition, count, ((double)pagePosition)/count));

                sendDocuments(esHost, httpClient, results);

                pagePosition += pageSize;
            }
        }
    }


    protected static void sendDocuments(String esHost, HttpClientWrapper httpClient, List<POIStatus> results) throws IOException, InterruptedException {
        StringBuilder data = new StringBuilder();
        for (POIStatus result : results) {
            data.append("{ \"index\": { \"_index\": \"poiregression4\", \"_type\": \"status\", \"_id\": \"")
                    .append(StringEscapeUtils.escapeJson(sanitize(result.getFilename()))).append("\"}}\n")
                    .append(objectMapper.writeValueAsString(result)).append("\n");

            // currently the NGINX-proxy denies requests with more than 10MB of body-data
            // thus we do intermediate flushes here if necessary to avoid exceeding this limit
            // when some documents contain many large error messages
            if(data.length() > MAX_BULK_SIZE) {
                // bulk-send and continue
                sendDocumentWithRetry(esHost, httpClient, data);

                data.setLength(0);
            }
        }

        // bulk-send remainder
        if(data.length() > 0) {
            sendDocumentWithRetry(esHost, httpClient, data);
        }
    }

    protected static void sendDocumentWithRetry(String esHost, HttpClientWrapper httpClient, StringBuilder data) throws IOException, InterruptedException {
        int retry = 3;
        while (true) {
            try {
                sendDocument(httpClient.getHttpClient(), esHost + "_bulk", data.toString());
                break;
            } catch (IOException e) {
                retry--;
                if(retry == 0) {
                    throw e;
                }

                log.info("Sleeping a bit before retrying " + retry + ", had " + data.length() + " bytes: " + e);

                Thread.sleep(10_000);
            }
        }
    }

    protected static String sanitize(String key) {
        return key
                .replace("=", "_")
                .replace("?", "_")
                .replace("&", "_")
                .replace("-", "_")
                .replace("/", "_")
                .replace(" ", "_")
                .replace("[", "_")
                .replace("]", "_")
                .replace("\\", "_");
    }

    protected static void sendDocument(CloseableHttpClient httpClient, String url, String json) throws IOException {
        final HttpPut httpPut = new HttpPut(url);
        httpPut.addHeader("Content-Type", NanoHTTPD.MIME_JSON);
        httpPut.setEntity(new StringEntity(json, "UTF-8"));

        try (CloseableHttpResponse response = httpClient.execute(httpPut)) {
            HttpEntity entity = HttpClientWrapper.checkAndFetch(response, url);

            try {
                String result = IOUtils.toString(entity.getContent(), StandardCharsets.UTF_8);
                log.info("Had result when sending document to Elasticsearch at " + url + "(" + json.length() + " chars): " +
                        StringUtils.abbreviate(result, 1024) + ", JSON: " + StringUtils.abbreviate(json, 1024));
                if(result.contains("\"errors\":true")) {
                    throw new IOException("Failed to handle bulk: " + result);
                }
            } finally {
                // ensure all content is taken out to free resources
                EntityUtils.consume(entity);
            }
        } catch (IOException e) {
            throw new IOException("With URL " + url + " and JSON: " + StringUtils.abbreviate(json, 1024), e);
        }
    }

    protected static void setupTemplate(String esHost, String esUser, String esPassword) throws IOException {
        log.info("Updating template on host " + esHost + " and user: " + esUser);

        try (HttpClientWrapper metrics = new HttpClientWrapper(esUser, esPassword, 60_000)) {
            String url = esHost + (esHost.endsWith("/") ? "" : "/") + "_template/template_poi";
            final HttpPut httpPut = new HttpPut(url);
            httpPut.addHeader("Content-Type", "application/json");
            httpPut.setEntity(new FileEntity(new File("src/main/resources/indextemplate.json")));
            try (CloseableHttpResponse response = metrics.getHttpClient().execute(httpPut)) {
                HttpEntity entity = HttpClientWrapper.checkAndFetch(response, url);

                try {
                    log.info("Had result when setting index template at " + url + " in Elasticsearch: " + IOUtils.toString(entity.getContent(), StandardCharsets.UTF_8));
                } finally {
                    // ensure all content is taken out to free resources
                    EntityUtils.consume(entity);
                }
            }
        }
    }
}
