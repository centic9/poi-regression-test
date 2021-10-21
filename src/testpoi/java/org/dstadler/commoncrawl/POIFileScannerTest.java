package org.dstadler.commoncrawl;

import org.apache.poi.stress.FileHandler;
import org.apache.poi.stress.XSSFFileHandler;
import org.apache.poi.util.SuppressForbidden;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class POIFileScannerTest {
    private final static File ROOT_DIR = new File("src/testpoi/resources");

    @Ignore("Only works when commoncrawl-corpus is available")
    @Test
    @SuppressForbidden("Just an ignored test")
    public void testInvalidFile() throws IOException, InterruptedException {
        FileHandler fileHandler = POIFileScanner.getFileHandler(new File("../download"),
                "www.bgs.ac.uk_downloads_directdownload.cfm_id=2362&noexcl=true&t=west_20sussex_20-_20building_20stone_20quarries");

        assertEquals(XSSFFileHandler.class, fileHandler.getClass());

        // to show the output from ZipFile() from commons-compress
        // although I did not find out yet why the ZipFile is not closed here
        System.gc();
        Thread.sleep(1000);
        System.gc();
        Thread.sleep(1000);
    }

    @Test
    public void test() throws IOException {
        Collection<Map.Entry<String, FileHandler>> scan = POIFileScanner.scan(ROOT_DIR);
        assertEquals("There are 3 files in the src/testpoi/resources directory",
                3, scan.size());
    }
}
