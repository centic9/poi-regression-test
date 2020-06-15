package org.dstadler.commoncrawl;

import org.apache.poi.stress.FileHandler;
import org.apache.poi.stress.OPCFileHandler;
import org.apache.poi.stress.XSSFFileHandler;
import org.junit.Ignore;
import org.junit.Test;

import java.io.StringWriter;
import java.util.concurrent.TimeUnit;

public class FileHandlingRunnableTest {

    @Test
    public void testPrintInfo() {
        FileHandler handler = new OPCFileHandler();
        FileHandlingRunnable runnable = new FileHandlingRunnable(100, "testfile", handler, null, null);
        runnable.printInfo(1, "some text");

        runnable = new FileHandlingRunnable(System.currentTimeMillis()- TimeUnit.MINUTES.toMillis(3),
                "testfile", handler, null, null);
        FileHandlingRunnable.count.set(283849392);
        FileHandlingRunnable.filesCount = 12382823L;
        FileHandlingRunnable.ignored.set(2234);
        FileHandlingRunnable.failed.set(8326);
        runnable.printInfo(2371623, "some text");
    }

    @Ignore("Test one failing file")
    @Test
    public void testFailingFile() {
        FileHandler handler = new XSSFFileHandler();
        FileHandlingRunnable runnable = new FileHandlingRunnable(0,
                "../download.oldindex/au.edu.adelaide.www_saces_gambling_database_SACES_LGA_Gambling_Database_v4.02.xlsx",
                handler, new StringWriter(), null);
        runnable.run();
    }
}
