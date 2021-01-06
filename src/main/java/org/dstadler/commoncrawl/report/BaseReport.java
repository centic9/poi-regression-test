package org.dstadler.commoncrawl.report;

import org.apache.commons.lang3.StringUtils;
import org.archive.util.FileUtils;
import org.dstadler.commoncrawl.datalayer.DataAccess;
import org.dstadler.commoncrawl.jpa.FileStatus;
import org.dstadler.commoncrawl.utils.VelocityUtils;
import org.dstadler.commons.logging.jdk.LoggerFactory;

import javax.persistence.Query;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Base class for creating reports from the table in the
 * Derby database.
 */
@SuppressWarnings({"SqlDialectInspection", "SqlNoDataSourceInspection"})
public class BaseReport {
    private static final Logger log = LoggerFactory.make();

    // location of the test-corpus
    public static final File ROOT_DIR = new File("../download");

    // location of files that were moved out of the corpus, e.g. duplicates, but
    // which are still kept available to avoid re-downloading them
    public static final File BACKUP_DIR = new File("../backup");

    protected static final File REPORT_DIR = new File("build/reports");
    protected static final File REPORT_DIR_ALL = new File("build/reportsAll");

    public static Map<String, Object> getDefaultContext(long statusCount, String VERSION_BEFORE,
                                                 String VERSION_NOW) {
        Map<String,Object> context = new HashMap<>();

        // some global values and helpers
        context.put("statusCount", statusCount);
        context.put("version", "POI " + VERSION_NOW);
        context.put("baseVersion", "POI " + VERSION_BEFORE);
        context.put("stringutils", StringUtils.class);
        context.put("date", new Date().toString());
        return context;
    }

    public static void writeReport(Map<String, Object> context, File resultFile) throws IOException {
        log.info("Writing " + ((List<?>)context.get("items")).size() + " items to " + resultFile);

        // ensure the main directory for the report exists
        if(!resultFile.getParentFile().exists() && !resultFile.getParentFile().mkdirs()) {
            throw new IllegalStateException("Could not create dir " + resultFile.getParentFile() + " for file " + resultFile);
        }

        VelocityUtils.render(context, "Report.vm", resultFile);
    }

    protected static void regressionData(DataAccess access, Map<String, Object> context,
                                         String COL_BEFORE, String COL_NOW,
                                         String where, boolean copySampleFiles) {
        String sqlString = "select COUNT(*), POI" + COL_NOW + ", EXCEPTIONTEXT" + COL_NOW + ", EXCEPTIONSTACKTRACE" + COL_NOW + ", MIN(FILENAME) "
                + "from poistatus m "
                + "where (POI" + COL_BEFORE + " = 0 AND POI" + COL_NOW + " != 0) "
                //+ "where (POI" + COL_BEFORE + " = 0 AND POI" + COL_NOW + " != 0 AND EXCEPTIONTEXT" + COL_NOW + " NOT LIKE 'java.lang.ClassCastException%') "
                + (where != null ? " and " + where + " " : "")
                + "group by POI" + COL_NOW + ", EXCEPTIONTEXT" + COL_NOW + ", EXCEPTIONSTACKTRACE" + COL_NOW + " ";
                // no need for order, we re-sort it afterwards anyway
                //+ "order by 1 DESC ";
        log.info("Loading regression data via query " + sqlString);
        Query query = access.getEm().createNativeQuery(sqlString);

        processData(context, copySampleFiles, query, REPORT_DIR);
    }

    private static void processData(Map<String, Object> context, boolean copySampleFiles, Query query, File reportDir) {
        List<ReportItem> items = new ArrayList<>();
        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        for(Object[] result : results) {
            int count = Integer.parseInt(result[0].toString());
            FileStatus status = getFileStatus(result[1]);
            String exception = result[2] != null ? result[2].toString() : "<null>";
            String stacktrace = result[3] != null ? result[3].toString() : "<null>";
            String fileName = result[4] != null ? result[4].toString() : "<null>";

            items.add(new ReportItem(count, status, exception, stacktrace, fileName));
        }

        items = combineItems(items);
        if(copySampleFiles) {
            for (ReportItem item : items) {
                try {
                    copySampleFile(reportDir, item.getFileName());
                } catch (IOException e) {
                    log.log(Level.SEVERE, "Could not copy file " + item.getFileName(), e);
                }
            }
        }

        context.put("items", items);
    }

    protected static void overviewData(DataAccess access, Map<String, Object> context,
                                     String COL_BEFORE, String VERSION_BEFORE,
                                     String COL_NOW, String VERSION_NOW,
                                       String where, boolean copySampleFiles) throws IOException {
        List<Object[]> results = executeQuery(access,
                "select count(*), m.POI" + COL_BEFORE + ", m.POI" + COL_NOW + ", MIN(FILENAME) "
                + "from poistatus m "
                + (where != null ? " where " + where + " " : "")
                + "group by m.POI" + COL_BEFORE + ", m.POI" + COL_NOW + " "
                + "order by 1 desc");

        List<OverviewItem> items = new ArrayList<>();
        for(Object[] result : results) {
            int count = Integer.parseInt(result[0].toString());
            FileStatus poiBefore = getFileStatus(result[1]);
            FileStatus poiNow = getFileStatus(result[2]);
            String fileName = result[3].toString();

            log.info("Having " + result[0] + " times " + poiBefore + " for " + VERSION_BEFORE + " and " + poiNow + " for " + VERSION_NOW +
                    ", sample-file: " + fileName);

            items.add(new OverviewItem(count, poiBefore, poiNow, fileName,
                    String.format("%.2f", ((double)100)*count/(Long)context.get("statusCount"))));

            if(copySampleFiles) {
                copySampleFile(REPORT_DIR, fileName);
            }
        }
        context.put("overview", items);
    }

    protected static void overviewDataAll(DataAccess access, Map<String, Object> context,
                                        String COL_NOW, String VERSION_NOW,
                                          String where, boolean copySampleFiles) throws IOException {
        List<Object[]> results = executeQuery(access,
                "select count(*), m.POI" + COL_NOW + ", MIN(FILENAME) "
                + "from poistatus m "
                + (where != null ? " where " + where + " " : "")
                + "group by m.POI" + COL_NOW + " "
                + "order by 1 desc");

        List<OverviewItem> items = new ArrayList<>();
        for(Object[] result : results) {
            int count = Integer.parseInt(result[0].toString());
            FileStatus poiNow = getFileStatus(result[1]);
            String fileName = result[2].toString();

            log.info("Having " + result[0] + " times " + poiNow + " for " + VERSION_NOW +
                    ", sample-file: " + fileName);

            items.add(new OverviewItem(count, null, poiNow, fileName,
                    String.format("%.2f", ((double)100)*count/(Long)context.get("statusCount"))));

            if(copySampleFiles) {
                copySampleFile(REPORT_DIR_ALL, fileName);
            }
        }
        context.put("overview", items);
    }

    private static List<Object[]> executeQuery(DataAccess access, String sqlString) {
        log.info("Loading overview data via query " + sqlString);
        Query query = access.getEm().createNativeQuery(sqlString);

        //noinspection unchecked
        return (List<Object[]>) query.getResultList();
    }

    @SuppressWarnings("SameParameterValue")
    protected static void allErrorData(DataAccess access, Map<String, Object> context, int maxResults,
                                       String COL_NOW, String where, boolean copySampleFiles) {
        String sqlString = "select COUNT(*), POI" + COL_NOW + ", EXCEPTIONTEXT" + COL_NOW + ", EXCEPTIONSTACKTRACE" + COL_NOW + ", MIN(FILENAME) "
                + "from poistatus m "
                + "where POI" + COL_NOW + " != 0 AND POI" + COL_NOW + " != 1 "
                + (where != null ? " and " + where + " " : "")
                + "group by POI" + COL_NOW + ", EXCEPTIONTEXT" + COL_NOW + ", EXCEPTIONSTACKTRACE" + COL_NOW + " ";
                // no need for order, we re-sort it afterwards anyway
                //+ "order by 1 DESC ";
        log.info("Loading error data for up to " + maxResults +
                ", copy files: " + copySampleFiles + " via query " + sqlString);
        Query query = access.getEm().createNativeQuery(sqlString);

        query.setMaxResults(maxResults);

        processData(context, copySampleFiles, query, REPORT_DIR_ALL);
    }

    public static List<ReportItem> combineItems(List<ReportItem> items) {
        // first sum up the count for equal exception texts
        Map<String, ReportItem> combined = new HashMap<>();
        for(ReportItem item : items) {
            ReportItem found = combined.get(item.getException());
            if(found == null) {
                combined.put(item.getException(), item);
            } else {
                found.addCount(item.getCount());
            }
        }

        // then sort the resulting List
        List<ReportItem> newItems = new ArrayList<>(combined.values());
        newItems.sort((o1, o2) -> {
            // sort on Status first, lowest order first
            int ret = Integer.compare(o1.getStatus().getOrder(), o2.getStatus().getOrder());
            if(ret != 0) {
                return ret;
            }

            // reverse comparison so that highest count is on top
            return Integer.compare(o2.getCount(), o1.getCount());
        });

        return newItems;
    }

    private static void copySampleFile(File reportDir, String fileName) throws IOException {
        File destFile = new File(reportDir, fileName);

        // source-filename can contain sub-dirs that we need to create
        if(!destFile.getParentFile().exists() && !destFile.getParentFile().mkdirs()) {
            throw new IllegalStateException("Could not create dir " + destFile.getParentFile() + " for file " + destFile);
        }

        File srcFile = new File(BaseReport.ROOT_DIR, fileName);
        if(!destFile.exists() || srcFile.length() != destFile.length()) {
            log.info("Copying failing file " + srcFile + " to " + destFile);
            if(!srcFile.exists()) {
                // try backup-dir with duplicates if file is not found in main directory
                FileUtils.copyFile(new File(BaseReport.BACKUP_DIR, fileName), destFile);
            } else {
                FileUtils.copyFile(srcFile, destFile);
            }
        }
    }

    private static FileStatus getFileStatus(Object result) {
        final FileStatus status;
        if(result == null) {
            status = FileStatus.MISSING;
        } else {
            status = FileStatus.values()[Integer.parseInt(result.toString())];
        }
        return status;
    }
}
