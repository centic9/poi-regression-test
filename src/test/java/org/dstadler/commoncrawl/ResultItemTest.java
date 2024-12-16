package org.dstadler.commoncrawl;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class ResultItemTest {

    public static final String SAMPLE_JSON = "{" +
            "\"fileName\":\"0-static--content-springer-com.brum.beds.ac.uk_esm_art_3a10.1186_2fs12870-017-1048-9_mediaobjects_12870_2017_1048_moesm1_esm.pptm\"," +
            "\"exceptionText\":\"org.apache.poi.EmptyFileException: The supplied file was empty (zero bytes long)\"," +
            "\"exceptionStacktrace\":\"org.apache.poi.EmptyFileException: The supplied file was empty (zero bytes long)\\n\\tat org.apache.poi.util.IOUtils.peekFirstNBytes(IOUtils.java:111)\\n\\tat org.apache.poi.poifs.filesystem.FileMagic.valueOf(FileMagic.java:209)\\n\\tat org.apache.poi.openxml4j.opc.internal.ZipHelper.verifyZipHeader(ZipHelper.java:143)\\n\\tat org.apache.poi.openxml4j.opc.internal.ZipHelper.openZipStream(ZipHelper.java:175)\\n\\tat org.apache.poi.openxml4j.opc.ZipPackage.\\u003cinit\\u003e(ZipPackage.java:104)\\n\\tat org.apache.poi.openxml4j.opc.OPCPackage.open(OPCPackage.java:312)\\n\\tat org.apache.poi.ooxml.util.PackageHelper.open(PackageHelper.java:47)\\n\\tat org.apache.poi.xslf.usermodel.XMLSlideShow.\\u003cinit\\u003e(XMLSlideShow.java:108)\\n\\tat org.apache.poi.stress.XSLFFileHandler.handleFile(XSLFFileHandler.java:35)\\n\\tat org.apache.poi.stress.BaseIntegrationTest.handleFile(BaseIntegrationTest.java:166)\\n\\tat org.apache.poi.stress.BaseIntegrationTest.testOneFile(BaseIntegrationTest.java:58)\\n\\tat org.apache.poi.stress.BaseIntegrationTest.test(BaseIntegrationTest.java:53)\\n\\tat org.dstadler.commoncrawl.FileHandlingRunnable.run(FileHandlingRunnable.java:73)\\n\\tat java.base/java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:515)\\n\\tat java.base/java.util.concurrent.FutureTask.run(FutureTask.java:264)\\n\\tat java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128)\\n\\tat java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628)\\n\\tat java.base/java.lang.Thread.run(Thread.java:834)\\n\"," +
            "\"timeout\":false," +
            "\"duration\":-1" +
            "}";

    @Test
    public void testParseLine() throws IOException {
        ResultItem item = ResultItem.parse(SAMPLE_JSON);
        assertNotNull(item);
        assertTrue(item.getFileName().contains("static--content"));
        assertTrue(item.getExceptionText().contains("EmptyFileException"));
        assertTrue(item.getExceptionStacktrace().contains("org.apache.poi"));
        assertEquals(-1, item.getDuration());
        assertFalse(item.isTimeout());
    }

    @Test
    public void testParseEmptyLine() throws IOException {
        ResultItem item = ResultItem.parse("{}");
        assertNotNull(item);
        assertNull(item.getFileName());
        assertNull(item.getExceptionText());
        assertNull(item.getExceptionStacktrace());
        assertEquals(0, item.getDuration());
        assertFalse(item.isTimeout());
    }

    @Test
    public void testParseInvalidFields() {
        assertThrows(IllegalStateException.class, () -> ResultItem.parse("{\"field\":\"value\"}"));
        assertThrows(IllegalStateException.class, () -> ResultItem.parse("{\"field\":1}"));
        assertThrows(IllegalStateException.class, () -> ResultItem.parse("{\"field\":1.2}"));
        assertThrows(IllegalStateException.class, () -> ResultItem.parse("{\"field\":true"));
    }

    @Test
    public void testParseInvalidToken() {
        assertThrows(IllegalStateException.class, () -> ResultItem.parse("[]"));
    }
}