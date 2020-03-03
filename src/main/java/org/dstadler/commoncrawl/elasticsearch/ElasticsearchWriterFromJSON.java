package org.dstadler.commoncrawl.elasticsearch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.LogManager;
import org.dstadler.commoncrawl.jpa.POIStatus;
import org.dstadler.commoncrawl.utils.ExportPOIStatus;
import org.dstadler.commons.logging.jdk.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

/**
 * Send POIStatus-objects to Elasticsearch from a JSON-file.
 *
 * The data is expected to be available at /tmp/export.json.gz
 *
 * Specify the connection to Elasticsearch via commandline parameters
 *
 * Create the JSON via {@link ExportPOIStatus}
 */
public class ElasticsearchWriterFromJSON extends ElasticsearchWriter {
    private static final Logger log = LoggerFactory.make();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        LoggerFactory.initLogging();

        long start = System.currentTimeMillis();

        if(args.length <= 2) {
            log.severe("Usage: ElasticsearchWriterFromCSV <eshost> <esuser> <espassword>");
            System.exit(1);
        }

        setupTemplate(args[0], args[1], args[2]);

        try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                        new GZIPInputStream(new FileInputStream("/tmp/export.json.gz")),
                        StandardCharsets.UTF_8),
                1024*1024)) {
            Iterator<POIStatus> it = new POIStatusIterator(reader);
            writeDocuments(it, args[0], args[1], args[2]);
        }

        log.info("Done after " + (System.currentTimeMillis() - start)/1000 + " seconds");

        LogManager.shutdown();
    }

    /**
     * An {@link Iterator} which provides items from the JSON file
     * by reading lines and converting them to {@link POIStatus} objects.
      */
    private static class POIStatusIterator implements Iterator<POIStatus> {
        private final BufferedReader reader;
        String nextLine;

        public POIStatusIterator(BufferedReader reader) throws IOException {
            this.reader = reader;
            nextLine = reader.readLine();
        }

        @Override
        public boolean hasNext() {
            return nextLine != null;
        }

        @Override
        public POIStatus next() {
            String ret = nextLine;
            try {
                nextLine = reader.readLine();
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }

            try {
                return objectMapper.readValue(ret, POIStatus.class);
            } catch (JsonProcessingException e) {
                throw new IllegalStateException("While reading line: " + ret, e);
            }
        }
    }
}
