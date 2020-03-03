package org.dstadler.commoncrawl;

import org.apache.commons.lang3.StringUtils;
import org.dstadler.commoncrawl.datalayer.DataAccess;
import org.dstadler.commoncrawl.datalayer.DataAccessFactory;
import org.dstadler.commoncrawl.datalayer.DatabaseStarter;
import org.dstadler.commoncrawl.jpa.FileStatus;
import org.dstadler.commoncrawl.jpa.POIStatus;
import org.dstadler.commoncrawl.report.BaseReport;
import org.dstadler.commoncrawl.report.OverviewItem;
import org.dstadler.commoncrawl.report.ReportItem;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ReportTest {
    public static final File RESULT_FILE = new File("build/testReport.html");
    public static final File RESULT_FILE_ALL = new File("build/testReportAll.html");

    @Test
    public void testFileHandleLeaks() throws IOException {
        Runnable shutdown = DatabaseStarter.ensureDatabase(11527);

        try (DataAccess access = DataAccessFactory.getInstance(DataAccessFactory.DB_TEST)) {
            assertNotNull(access);

            access.startTransaction();
            POIStatus status = new POIStatus("file1");
            access.writePOIStatus(status);
            access.commitTransaction();

            long statusCount = access.countStatus("m.poi315VM is null");
            assertTrue("Should have some POIStatus entries now",
                    statusCount > 0);

            //noinspection unchecked,SqlDialectInspection,SqlNoDataSourceInspection
            List<Object[]> results = access.getEm().createNativeQuery(
                    "select count(*), m.POI410SNAPSHOT, MIN(FILENAME) "
                            + "from poistatus m "
                            + "group by m.POI410SNAPSHOT "
                            + "order by 1 desc").getResultList();

            assertNotNull(results);
        } finally {
            // ensure that the database is shut down
            shutdown.run();
        }
    }

    @Test
    public void testReportLayout() throws IOException {
        Map<String, Object> context = BaseReport.getDefaultContext(22, "314beta2", "315beta1");

        addData(context);

        Report.writeReport(context, RESULT_FILE);

        //new DocumentStarter().openFile(RESULT_FILE);
    }

    @Test
    public void testReportLayoutAll() throws IOException {
        Map<String, Object> context = Report.getDefaultContext(2, "314beta2", "315beta1");
        context.put("baseVersion", null);

        addData(context);

        Report.writeReport(context, RESULT_FILE_ALL);

        //new DocumentStarter().openFile(RESULT_FILE_ALL);
    }

    private void addData(Map<String, Object> context) {
        List<OverviewItem> overview = new ArrayList<>();
        overview.add(new OverviewItem(23, FileStatus.OK, FileStatus.ERROR, "some file name"));
        context.put("overview", overview);

        List<ReportItem> items = getItems(10);
        //items.add(new ReportItem(44, FileStatus.ERROR, StringUtils.repeat("somelongexceptiontextwithoutspace", 20), "some stacktrace text", "this is a filename"));
        context.put("items", items);
    }

    private List<ReportItem> getItems(int n) {
        List<ReportItem> items = new ArrayList<>();
        for(int i = 0;i < n;i++) {
            items.add(new ReportItem(46, FileStatus.ERROR, StringUtils.repeat("some long exception text", 20), "some stacktrace text", "this is a filename"));
            items.add(new ReportItem(45, FileStatus.ERROR, "some exception text", StringUtils.repeat("some long stacktrace text", 10) + "\n" + StringUtils.repeat("some long stacktrace text", 10), "this is a filename"));
            items.add(new ReportItem(44, FileStatus.ERROR, "some exception text", "some stacktrace text", "this is a filename"));
            items.add(new ReportItem(1, FileStatus.ERROR, "only one", "some stacktrace text", "this is a filename"));
        }
        return items;
    }

    @Test
    public void testCombine() {
        List<ReportItem> items = getItems(1);
        List<ReportItem> newItems = Report.combineItems(items);

        assertEquals(89, newItems.get(0).getCount());
        assertEquals(46, newItems.get(1).getCount());
        assertEquals(1, newItems.get(2).getCount());

        assertEquals("some exception text", newItems.get(0).getException());
        assertEquals(StringUtils.repeat("some long exception text", 20), newItems.get(1).getException());
        assertEquals("only one", newItems.get(2).getException());
    }
}