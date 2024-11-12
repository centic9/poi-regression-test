package org.dstadler.commoncrawl;

import org.apache.log4j.LogManager;
import org.dstadler.commoncrawl.datalayer.DataAccess;
import org.dstadler.commoncrawl.datalayer.DataAccessFactory;
import org.dstadler.commoncrawl.datalayer.DatabaseStarter;
import org.dstadler.commoncrawl.report.BaseReport;
import org.dstadler.commons.logging.jdk.LoggerFactory;
import org.dstadler.commons.util.DocumentStarter;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Tool to produce the HTML reports from the database after
 * the regression test run was executed and results were
 * uploaded via {@link ProcessResults}.
 *
 * It performs a number of queries on the database and
 * then generates static HTML files with the different reports.
 */
public class Report extends BaseReport {
    private static final Logger log = LoggerFactory.make();

    public static final int ALL_DATA_LIMIT = 10000;

    public static void main(String[] args) throws IOException {
		LoggerFactory.initLogging();

		long start = System.currentTimeMillis();

        Runnable shutdown = DatabaseStarter.ensureDatabase(11527);

        try (DataAccess access = DataAccessFactory.getInstance(DataAccessFactory.DB_PROD)) {
            createReport(access);
        } finally {
            // ensure that the database is shut down
            shutdown.run();
        }

        log.info("Done after " + (System.currentTimeMillis() - start)/1000 + " seconds");

        LogManager.shutdown();
	}

    public static void createReport(DataAccess access) throws IOException {
        if(!REPORT_DIR.exists() && !REPORT_DIR.mkdirs()) {
            throw new IllegalStateException("Could not create dir " + REPORT_DIR);
        }

        long statusCount = access.countStatus("m.poi315VM is null");
        log.info("Having " + statusCount + " Status-Items before starting to write");

        String versionBefore = "5.3.0RC1";
        String versionNow =    "5.4.0RC1";
        report(access, statusCount, "index.html", "index.html",
                versionBefore, versionNow, "POI315VM is null", true);

        String fileName = "index" + versionBefore.replace(".", "") + "to" +
                versionNow.replace(".", "") + ".html";
        report(access, statusCount, fileName, fileName,
                versionBefore, versionNow, "POI315VM is null", true);
    }

    @SuppressWarnings("SameParameterValue")
    static void report(DataAccess access, long statusCount, String file, String fileAll, String VERSION_BEFORE, String VERSION_NOW,
                       String where, boolean copySampleFiles) throws IOException {
        final String COL_NOW = VERSION_NOW.replace(".", "").toUpperCase();
        Map<String, Object> context = getDefaultContext(statusCount, VERSION_BEFORE, VERSION_NOW);
        context.put("copySampleFiles", copySampleFiles);

        if(file != null) {
            final File REPORT_FILE = new File(REPORT_DIR, file);
            final String COL_BEFORE = VERSION_BEFORE.replace(".", "").toUpperCase();

            // first the report with only the regressions since the previous version
            overviewData(access, context, COL_BEFORE, VERSION_BEFORE, COL_NOW, VERSION_NOW, where, copySampleFiles);
            regressionData(access, context, COL_BEFORE, COL_NOW, where, copySampleFiles);

            writeReport(context, REPORT_FILE);

            new DocumentStarter().openFile(REPORT_FILE);
        }

        // second report with all exceptions, not only regressions
        if(fileAll != null) {
            final File REPORT_FILE_ALL = new File(REPORT_DIR_ALL, fileAll);

            context.put("baseVersion", null);
            context.put("allDataLimit", ALL_DATA_LIMIT);

            // only include the top 10000 failures, but include sample files
            overviewDataAll(access, context, COL_NOW, VERSION_NOW, where, copySampleFiles);
            allErrorData(access, context, ALL_DATA_LIMIT, COL_NOW, where, copySampleFiles);
            writeReport(context, REPORT_FILE_ALL);

            new DocumentStarter().openFile(REPORT_FILE_ALL);
        }
    }
}
