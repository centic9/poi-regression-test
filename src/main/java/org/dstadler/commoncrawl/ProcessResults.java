package org.dstadler.commoncrawl;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.dstadler.commoncrawl.datalayer.DataAccess;
import org.dstadler.commoncrawl.datalayer.DataAccessFactory;
import org.dstadler.commoncrawl.datalayer.DatabaseStarter;
import org.dstadler.commoncrawl.jpa.POIStatus;
import org.dstadler.commons.logging.jdk.LoggerFactory;

/**
 * Tool to read the results after the regression test run
 * from the JSON file and populate the columns for the current version
 * in the database with the results for each file.
 *
 * The version is determine from the name of the result-file which
 * is passed in as commandline parameter.
 */
public class ProcessResults {
    private static final Logger log = LoggerFactory.make();

    public static void main(String[] args) throws Exception {
        LoggerFactory.initLogging();

        Runnable shutdown = DatabaseStarter.ensureDatabase(11527);

        try (DataAccess access = DataAccessFactory.getInstance(DataAccessFactory.DB_PROD)) {
            log.info("Having " + access.countURLs() + " URLs and " + access.countStatus(null) + " Status-Items before starting to write");

            try {
                for(String resultFile : args) {
                    handleFile(access, new File(resultFile));
                }
            } finally {
                if(access.getEm().getTransaction().isActive()) {
                    access.getEm().getTransaction().rollback();
                }
            }
        } catch (Exception e) {
            StringWriter fullException = new StringWriter();
            try (PrintWriter writer = new PrintWriter(fullException)) {
                ExceptionUtils.printRootCauseStackTrace(e, writer);
            }
            log.log(Level.SEVERE, "Exception\n" + fullException);
        } finally {
            // ensure that the database is shut down
            shutdown.run();
        }
    }

    public static void handleFile(DataAccess access, File resultFile) throws IOException {
        if(!resultFile.exists()) {
            throw new IllegalStateException("Cannot write results to database without the result file at " + resultFile);
        }

        log.info("Reading file " + resultFile + ", " + resultFile.length() + " bytes");
        try (BufferedReader reader = new BufferedReader(new FileReader(resultFile), 1024*1024)) {
            int count = 0;

            access.startTransaction();
            while(true) {
                String line = reader.readLine();
                if(line == null) {
                    break;
                }

                if (!handleLine(access, resultFile, count, line)) {
                    continue;
                }

                count++;
                if(count % 1000 == 0) {
                    log.info("Committing after " + count + " lines, current file: " + resultFile + ", current line: " + line);
                    access.commitTransaction();
                    access.startTransaction();
                }
            }

            access.commitTransaction();

            log.info("Having " + access.countStatus(null) + " Status-Items after writing " + count + " lines");
        }
    }

    static boolean handleLine(DataAccess access, File resultFile, int count, String line) throws IOException {
        final ResultItem item;
        try {
            item = ResultItem.parse(line);
        } catch (JsonParseException e) {
            log.log(Level.WARNING, "Failed to parse line " + count + ": " + line, e);
            return false;
        }

        /*final String url;
        try {
            url = getURL(filenameToURL, item);
        } catch (IllegalStateException e){
            log.warning("Did not find filename: " + e);
            continue;
        }
        Preconditions.checkNotNull(url, "For filename %s", item.getFileName());*/

        writeToDatabase(access, resultFile.getName(), item);
        return true;
    }

    private static void writeToDatabase(DataAccess access, String fileName, ResultItem item) {
        POIStatus status = access.getStatus(item.getFileName());
        if(status == null) {
            status = new POIStatus();
            status.setFilename(item.getFileName());
        }

        status.setByVersion(fileName, item);

        access.writePOIStatus(status);
    }
}
