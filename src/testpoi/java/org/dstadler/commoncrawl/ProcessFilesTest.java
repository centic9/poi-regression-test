package org.dstadler.commoncrawl;

import org.apache.poi.stress.FileHandler;
import org.apache.poi.stress.POIFileScanner;
import org.apache.poi.stress.XWPFFileHandler;
import org.junit.Ignore;
import org.junit.Test;

import java.io.*;
import java.util.Collection;
import java.util.Map;

import static org.junit.Assert.*;

public class ProcessFilesTest {
	private Writer stringWriter;

	@Test
	public void testNotExistingFile() throws IOException {
		stringWriter = new StringWriter();
		try {
			FileHandlingRunnable runnable = new FileHandlingRunnable(System.currentTimeMillis(), "notexistingfile",
					new NullFileHandler(), stringWriter, ProcessFiles.ROOT_DIR);
			runnable.run();
		} finally {
			stringWriter.close();
		}

		assertTrue("Had: " + stringWriter.toString().replace("\n", "").substring(0, 200),
				stringWriter.toString().contains("\"notexistingfile\""));
		assertTrue("Had: " + stringWriter.toString().replace("\n", "").substring(0, 200),
				stringWriter.toString().contains("\"exceptionText\":\"java.io.FileNotFoundException:"));
	}

	@Test
	public void testFileNullHandler() throws IOException {
		stringWriter = new StringWriter();
		try {
			FileHandlingRunnable runnable = new FileHandlingRunnable(System.currentTimeMillis(), "scstatehouse.gov_getfile.php_type=codeoflaws&title=2&chapter=1.docx",
					new NullFileHandler(), stringWriter, ProcessFiles.ROOT_DIR);
			runnable.run();
		} finally {
			stringWriter.close();
		}

		assertTrue("Had: " + stringWriter.toString().replace("\n", ""),
				stringWriter.toString().contains("\"scstatehouse.gov_getfile.php"));
		assertFalse("Had: " + stringWriter.toString().replace("\n", ""),
				stringWriter.toString().contains("\"exceptionText\":\"java.io.FileNotFoundException:"));
	}

	@Ignore("Just a local test")
	@Test
	public void testFileHandlingRunnable() throws IOException {
		stringWriter = new StringWriter();
		try {
			FileHandlingRunnable runnable = new FileHandlingRunnable(System.currentTimeMillis(), "scstatehouse.gov_getfile.php_type=codeoflaws&title=2&chapter=1.docx",
					new XWPFFileHandler(), stringWriter, ProcessFiles.ROOT_DIR);
			runnable.run();
		} finally {
			stringWriter.close();
		}

		assertEquals("", stringWriter.toString());
	}

	private static class NullFileHandler implements FileHandler {
        @Override
        public void handleFile(InputStream stream, String path) {
        }

        @Override
        public void handleExtracting(File file) {
        }

		@Override
		public void handleAdditional(File file) {
		}
	}

	@Test
	public void testMemoryChecker() {
		// just invoke it here so
		Writer writer = new OutputStreamWriter(System.out);
		new ProcessFiles.MemoryChecker(writer).run();
	}

	@Test
	public void testPOIFileScanner() throws IOException {
		// just make sure that POIFileScanner works and all dependencies
		// are available
		Collection<Map.Entry<String, FileHandler>> files = POIFileScanner.scan(new File("src"));
		assertNotNull(files);
	}
}
