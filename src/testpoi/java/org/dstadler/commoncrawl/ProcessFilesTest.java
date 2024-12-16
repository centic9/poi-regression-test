package org.dstadler.commoncrawl;

import org.apache.poi.openxml4j.exceptions.InvalidOperationException;
import org.apache.poi.stress.FileHandler;
import org.apache.poi.stress.HSLFFileHandler;
import org.apache.poi.stress.XWPFFileHandler;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

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

		assertTrue(stringWriter.toString().contains("\"notexistingfile\""),
				"Had: " + stringWriter.toString().replace("\n", "").substring(0, 200));
		assertTrue(stringWriter.toString().contains("\"exceptionText\":\"java.io.FileNotFoundException:"),
				"Had: " + stringWriter.toString().replace("\n", "").substring(0, 200));
	}

	@Disabled("Just a local test")
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

		assertTrue(stringWriter.toString().contains("\"scstatehouse.gov_getfile.php"),
				"Had: " + stringWriter.toString().replace("\n", ""));
		assertFalse(stringWriter.toString().contains("\"exceptionText\":\"java.io.FileNotFoundException:"),
				"Had: " + stringWriter.toString().replace("\n", ""));
	}

	@Disabled("Just a local test")
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

	@Disabled("Only used for local testing")
	@Test
	public void testOneFile() throws Exception {
		String file = "alfresco.vgregion.se_alfresco_service_vgr_storage_node_content_workspace_spacesstore_a1dc0dc0-b6f6-4890-8dc6-e4dd029764f1_tom_20fl_c3_b6desschema_20nytt.ppt_a=false&guest=true&native=true.ppt";
		String ROOT_DIR = "../download2";
		FileHandler handler = new HSLFFileHandler();

		System.out.println("Handling file: " + file);
		File fileIO = new File(ROOT_DIR, file);
		try (InputStream stream = new BufferedInputStream(new FileInputStream(fileIO), 1024 * 100)) {
			try {
				handler.handleFile(stream, file);
			} catch (IOException e) {
				if (!e.getMessage().equals("Truncated ZIP file")) {
					throw e;
				}
			}
			//handler.handleAdditional(fileIO);

			try {
				handler.handleExtracting(fileIO);
			} catch (InvalidOperationException e) {
				if (e.getCause() != null && !e.getCause().getMessage().equals("Truncated ZIP file")) {
					throw e;
				}
			}
		}
	}
}
