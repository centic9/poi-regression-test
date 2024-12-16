package org.dstadler.commoncrawl;

import org.apache.poi.stress.FileHandler;
import org.apache.poi.stress.OPCFileHandler;
import org.apache.poi.stress.XSLFFileHandler;
import org.apache.poi.stress.XSSFFileHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.concurrent.TimeUnit;

public class FileHandlingRunnableTest {
	@BeforeAll
	public static void setUpClass() throws IOException {
		LoggingUtils.configureLog4j2();
	}

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

    @Test
    public void testFailingFile() {
        FileHandler handler = new XSSFFileHandler();
        FileHandlingRunnable runnable = new FileHandlingRunnable(0,
                // simply run with any file so we check that no exception is
                // reported even if processing the file fails
                "src/test/resources/test.vm",
                handler, new StringWriter(), null);
        runnable.run();
    }

	@Disabled("Only works when test-corpus is available")
    @Test
    public void testFile() {
        FileHandler handler = new XSLFFileHandler();
        FileHandlingRunnable runnable = new FileHandlingRunnable(0,
                // simply run with any file so we check that no exception is
                // reported even if processing the file fails
                "/opt/CommonCrawl/download/binaries.templates.cdn.office.net_support_templates_fr-fr_tf56227732_win32.potx",
                handler, new StringWriter(), null);
        runnable.run();
    }
}
