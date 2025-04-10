package org.dstadler.commoncrawl.report;

import org.apache.commons.io.FileUtils;
import org.dstadler.commoncrawl.datalayer.DataAccess;
import org.dstadler.commoncrawl.datalayer.DataAccessFactory;
import org.dstadler.commoncrawl.datalayer.DatabaseBase;
import org.dstadler.commoncrawl.jpa.FileStatus;
import org.dstadler.commoncrawl.jpa.POIStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.TypedQuery;
import java.io.File;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class BaseReportTest extends DatabaseBase {
    // clean out existing entries first before and after the test
    @BeforeEach
    @AfterEach
    public void setUp() {
        try (DataAccess access = DataAccessFactory.getInstance(DataAccessFactory.DB_TEST)) {
            access.startTransaction();

            //noinspection JpaQlInspection
            TypedQuery<POIStatus> query = access.getEm().createQuery("select m from POIStatus m", POIStatus.class);
            for (POIStatus poiStatus : query.getResultList()) {
                access.getEm().remove(poiStatus);
            }
            access.commitTransaction();
        }
    }

    @Test
    public void testRegressionData() throws Exception {
        assumeTrue(BaseReport.ROOT_DIR.exists(),
                "Need the corpus available at " + BaseReport.ROOT_DIR);
        assumeTrue(BaseReport.ROOT_DIR.isDirectory(),
                "Need a corpus-directory available at " + BaseReport.ROOT_DIR);

        try (DataAccess access = DataAccessFactory.getInstance(DataAccessFactory.DB_TEST)) {

            runAndCheckCount(access, FileStatus.OK, 0, "testfile" + 0);

            runAndCheckCount(access, FileStatus.ERROR, 1, "testfile" + 1);
            // errors are only logged now: fail("Should catch exception because file does not exist");

            access.startTransaction();
            access.getEm().remove(new POIStatus("testfile" + 1));
            access.commitTransaction();

            File file = File.createTempFile("BaseReportTest", ".tst");
            try {
                FileUtils.writeStringToFile(file, "Test", "UTF-8");
                runAndCheckCount(access, FileStatus.ERROR, 1, file.getName());
            } finally {
                assertTrue(file.exists());
                assertTrue(file.delete());
            }

            File destFile = new File(BaseReport.REPORT_DIR, file.getName());
            assertFalse(destFile.exists(),
                    "Source is not available during report-generation, so should not find: " + destFile.getAbsolutePath());
        }
    }

    private void runAndCheckCount(DataAccess access, FileStatus fileStatus, int expectedCount, String filename) {
        access.startTransaction();
        POIStatus status = new POIStatus(filename);
        status.setPoi317(FileStatus.OK);
        status.setPoi400SNAPSHOT(fileStatus);
        access.writePOIStatus(status);
        access.commitTransaction();

        HashMap<String, Object> context = new HashMap<>();
        BaseReport.regressionData(access, context, "317", "400SNAPSHOT",
                null, true);

        assertEquals(expectedCount, ((List<?>)context.get("items")).size());
    }
}