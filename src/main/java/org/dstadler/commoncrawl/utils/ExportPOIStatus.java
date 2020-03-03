package org.dstadler.commoncrawl.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.LogManager;
import org.dstadler.commoncrawl.datalayer.DataAccess;
import org.dstadler.commoncrawl.datalayer.DataAccessFactory;
import org.dstadler.commoncrawl.datalayer.DatabaseStarter;
import org.dstadler.commoncrawl.jpa.POIStatus;
import org.dstadler.commons.logging.jdk.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;

/**
 * Utility to export contents of the main database-table to a line-based JSON file
 */
@SuppressWarnings("JpaQlInspection")
public class ExportPOIStatus {
    private static final Logger log = LoggerFactory.make();
    private static final int BULK_SIZE = 20000;

    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        LoggerFactory.initLogging();

        long start = System.currentTimeMillis();

        Runnable shutdown = DatabaseStarter.ensureDatabase(11527);

        try (DataAccess access = DataAccessFactory.getInstance(DataAccessFactory.DB_PROD)) {
            createExport(access, new File("/tmp/export.json.gz"));
        } finally {
            // ensure that the database is shut down
            shutdown.run();
        }

        log.info("Done after " + (System.currentTimeMillis() - start)/1000 + " seconds");

        LogManager.shutdown();
    }

    private static void createExport(DataAccess access, File file) throws IOException {
        try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                new GZIPOutputStream(new FileOutputStream(file)), StandardCharsets.UTF_8), 1024*1024)) {
            EntityManager entityManager = access.getEm();

            Query queryTotal = entityManager.createQuery
                    ("SELECT COUNT(f.filename) FROM POIStatus f");
            long countResult = (long)queryTotal.getSingleResult();

            log.info("Reading " + countResult + " rows in " + (countResult/BULK_SIZE)+1 + " bulks");

            int first = 0;
            while(first < countResult) {
                TypedQuery<POIStatus> query = entityManager.createQuery("SELECT f FROM POIStatus f", POIStatus.class);
                query.setFirstResult(first);
                query.setMaxResults(BULK_SIZE);
                List<POIStatus> statusList = query.getResultList();

                log.info("Had results at " + first + ": " + statusList.size());

                for (POIStatus poiStatus : statusList) {
                    out.write(mapper.writeValueAsString(poiStatus));
                    out.write("\n");
                }

                first += BULK_SIZE;
            }
        }
    }
}
