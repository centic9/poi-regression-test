package org.dstadler.commoncrawl.jpa;

import static org.junit.Assert.*;

import org.apache.commons.lang3.StringUtils;
import org.dstadler.commoncrawl.ResultItem;
import org.dstadler.commons.testing.TestHelpers;
import org.junit.Test;

import java.io.IOException;

public class POIStatusTest {

	@Test
	public void testSetByVersion() throws Exception {
		ResultItem item = ResultItem.parse("{\"timeout\":false}");
		assertFalse(item.isTimeout());
		checkStatus(item, FileStatus.OK);
	}

	@Test
	public void testSetByVersionFailed() throws Exception {
		ResultItem item = ResultItem.parse("{\"exceptionText\":\"testexception\"}");
		assertFalse(item.isTimeout());
		checkStatus(item, FileStatus.ERROR);
	}

	@Test
	public void testSetByVersionTimeout() throws Exception {
		ResultItem item = ResultItem.parse("{\"timeout\":true}");
		assertTrue(item.isTimeout());
		checkStatus(item, FileStatus.TIMEOUT);
	}

	@Test
	public void testSetByVersionInvalid() throws Exception {
		ResultItem item = ResultItem.parse("{\"exceptionText\":\"NotOLE2FileException: Invalid header signature; read \"}");
		assertFalse(item.isTimeout());
		checkStatus(item, FileStatus.INVALID);
	}

	@Test
	public void testSetByVersionOldFormat() throws Exception {
		ResultItem item = ResultItem.parse("{\"exceptionText\":\"org.opentest4j.TestAbortedException: File * excluded because it is unsupported old Excel format\"}");
		assertFalse(item.isTimeout());
		checkStatus(item, FileStatus.OLDFORMAT);

		item = ResultItem.parse("{\"exceptionText\":\"org.opentest4j.TestAbortedException: File * excluded because it is an unsupported old format\"}");
		assertFalse(item.isTimeout());
		checkStatus(item, FileStatus.OLDFORMAT);
	}

	@Test
	public void testSetByVersionTimeoutByStacktrace() throws Exception {
		// detect timeout for some special stacktraces as well
		ResultItem item = ResultItem.parse("{\"timeout\":false,\"exceptionText\":\"java.lang.ThreadDeath: blabla\",\"exceptionStacktrace\":\"java.lang.ThreadDeath: blabla\"}");
		assertFalse(item.isTimeout());
		checkStatus(item, FileStatus.TIMEOUT);
	}


	void checkStatus(ResultItem item, FileStatus expectedStatus) {
		POIStatus status = checkStatus(item, "poi-3.13");
		assertEquals(expectedStatus, status.getPoi313());

		status = checkStatus(item, "poi-3.14-beta1");
		assertEquals(expectedStatus, status.getPoi314beta1());

		status = checkStatus(item, "poi-3.14-beta2");
		assertEquals(expectedStatus, status.getPoi314beta2());

		status = checkStatus(item, "poi-3.15-beta1");
		assertEquals(expectedStatus, status.getPoi315beta1());

		status = checkStatus(item, "poi-3.15-beta2");
		assertEquals(expectedStatus, status.getPoi315beta2());

		status = checkStatus(item, "poi-3.15-beta3");
		assertEquals(expectedStatus, status.getPoi315beta3());

		try {
			checkStatus(item, "someother");
		} catch (IllegalStateException e) {
			// expected on invalid version
		}
	}

	private POIStatus checkStatus(ResultItem item, String version) {
		POIStatus status = new POIStatus();
		status.setByVersion(version, item);
		return status;
	}

	@Test
	public void testSetByVersionOOMByStacktrace() throws Exception {
		// detect timeout for some special stacktraces as well
		ResultItem item = ResultItem.parse("{\"exceptionText\":\"java.lang.OutOfMemoryError: blabla\",\"exceptionStacktrace\":\"java.lang.OutOfMemoryError: blabla\"}");
		assertFalse(item.isTimeout());
		checkStatus(item, FileStatus.OOM);
	}

	@Test
	public void testSetFilename() {
		POIStatus status = new POIStatus();
		status.setFilename("abcd");
		assertEquals("abcd", status.getFilename());
	}

	@Test
	public void testConstructor() {
		POIStatus status = new POIStatus("abcd");
		assertEquals("abcd", status.getFilename());

		status = new POIStatus(StringUtils.repeat("a", FileURL.URL_MAX_LENGTH));
		assertEquals(StringUtils.repeat("a", FileURL.URL_MAX_LENGTH), status.getFilename());

		try {
			new POIStatus(StringUtils.repeat("a", FileURL.URL_MAX_LENGTH +1));
			fail("Should catch exception");
		} catch (IllegalArgumentException e) {
			// expected here
		}
	}

	@Test
	public void testHashCode() throws IOException {
		POIStatus status = new POIStatus();
		POIStatus equ = new POIStatus();
		TestHelpers.HashCodeTest(status, equ);

		ResultItem item = ResultItem.parse("{\"exceptionText\":\"java.lang.OutOfMemoryError: blabla\",\"exceptionStacktrace\":\"java.lang.OutOfMemoryError: blabla\"}");
		status = new POIStatus("file1");
		status.setByVersion("poi-3.13", item);
		equ = new POIStatus("file1");
		equ.setByVersion("poi-3.13", item);
		TestHelpers.HashCodeTest(status, equ);
	}


	@Test
	public void testEquals() {
		POIStatus status = new POIStatus();
		POIStatus equ = new POIStatus();
		POIStatus notequ = new POIStatus("file");
		TestHelpers.EqualsTest(status, equ, notequ);

		status = new POIStatus("file1");
		equ = new POIStatus("file1");
		TestHelpers.EqualsTest(status, equ, notequ);

		notequ = new POIStatus();
		TestHelpers.EqualsTest(status, equ, notequ);
	}
}
