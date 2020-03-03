package org.dstadler.commoncrawl;

import org.dstadler.commoncrawl.jpa.FileStatus;
import org.dstadler.commoncrawl.report.ReportItem;
import org.junit.Test;

import static org.junit.Assert.*;

public class ReportItemTest {
    @Test
    public void test() {
        ReportItem item = new ReportItem(43, FileStatus.ERROR, "exception", "stacktrace", "filename");
        assertEquals(43, item.getCount());
        assertEquals(FileStatus.ERROR, item.getStatus());
        assertEquals("exception", item.getException());
        assertEquals("stacktrace", item.getStacktrace());
        assertEquals("filename", item.getFileName());
        assertEquals("", item.getAnnotation());
    }
    @Test
    public void testAnnotation() {
        ReportItem item = new ReportItem(43, FileStatus.ERROR, "exc", "ReadOnlySharedStringsTable.getEntryAt(ReadOnlySharedStringsTable.java:182)", "filename");
        assertEquals(43, item.getCount());
        assertEquals(FileStatus.ERROR, item.getStatus());
        assertEquals("exc", item.getException());
        assertEquals("ReadOnlySharedStringsTable.getEntryAt(ReadOnlySharedStringsTable.java:182)", item.getStacktrace());
        assertEquals("filename", item.getFileName());
        assertEquals("fixed", item.getAnnotation());
    }

    @Test
    public void testReplacements() {
        ReportItem item = new ReportItem(43, FileStatus.ERROR, "   org.apache.poi ", "abcd\tat java.lang.Thread.run(Thread.java:745)", "filename");
        assertEquals(43, item.getCount());
        assertEquals(FileStatus.ERROR, item.getStatus());
        assertEquals("o.a.p", item.getException());
        assertEquals("abcd", item.getStacktrace());
        assertEquals("filename", item.getFileName());

        item = new ReportItem(43, FileStatus.ERROR, "   org.apache.poi ", "abcd\tat java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)\nefgh", "filename");
        assertEquals(43, item.getCount());
        assertEquals(FileStatus.ERROR, item.getStatus());
        assertEquals("o.a.p", item.getException());
        assertEquals("abcdefgh", item.getStacktrace());
        assertEquals("filename", item.getFileName());

        item = new ReportItem(43, FileStatus.ERROR, "   org.apache.poi ",
                "\tat org.apache.poi.stress.HWPFFileHandler.handleFile(HWPFFileHandler.java:32)\n" +
                "\tat org.dstadler.commoncrawl.ProcessFiles$FileHandlingRunnable.run(ProcessFiles.java:220)\nabcd", "filename");
        assertEquals(43, item.getCount());
        assertEquals(FileStatus.ERROR, item.getStatus());
        assertEquals("o.a.p", item.getException());
        assertEquals("at o.a.p.stress.HWPFFileHandler.handleFile(HWPFFileHandler.java:32)\nabcd", item.getStacktrace());
        assertEquals("filename", item.getFileName());

        item = new ReportItem(43, FileStatus.ERROR, "   org.apache.poi ",
                "\tat org.apache.poi.stress.HWPFFileHandler.handleFile(HWPFFileHandler.java:32)\n" +
                "\tat org.apache.poi.BaseIntegrationTest.handleFile(BaseIntegrationTest.java:93)\n" +
                "\tat org.apache.poi.BaseIntegrationTest.test(BaseIntegrationTest.java:42)\n" +
                "\tat org.dstadler.commoncrawl.ProcessFiles$FileHandlingRunnable.run(ProcessFiles.java:220)\nabcd", "filename");
        assertEquals(43, item.getCount());
        assertEquals(FileStatus.ERROR, item.getStatus());
        assertEquals("o.a.p", item.getException());
        assertEquals("at o.a.p.stress.HWPFFileHandler.handleFile(HWPFFileHandler.java:32)\nabcd", item.getStacktrace());
        assertEquals("filename", item.getFileName());
    }
}