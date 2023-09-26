package org.dstadler.commoncrawl.datalayer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.commons.lang3.StringUtils;
import org.dstadler.commoncrawl.jpa.FileStatus;
import org.dstadler.commoncrawl.jpa.FileURL;
import org.dstadler.commoncrawl.jpa.POIStatus;
import org.dstadler.commons.testing.TestHelpers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.TypedQuery;
import java.util.List;

public class DataAccessTest extends DatabaseBase {
	private DataAccess access;

	private final String LONG_STR = StringUtils.repeat("a", 8096);
	private final String OVERLONG_STR = StringUtils.repeat("a", 8096+1);

	@Before
	public void create() {
		access = DataAccessFactory.getInstance(DataAccessFactory.DB_TEST);

		access.startTransaction();

		// clean up
		for(int i = 0;i < 100;i++) {
			String urlStr = "url" + i;
			removeIfExists(urlStr);
		}
		for(int i = 0;i < 100;i++) {
			String urlStr = "file" + i;
			removeIfExists(urlStr);
		}
		removeIfExists(LONG_STR);
		removeIfExists("testfile0");
		removeIfExists("testfile1");

		access.commitTransaction();

		access.startTransaction();

		// check
		for(int i = 0;i < 100;i++) {
			String url = "url" + i;
			assertNull("Should not have urls any more, but had " + url, access.getURL(url));
		}
		assertNull("Should not have LONG_STR any more", access.getURL(LONG_STR));
	}

	private void removeIfExists(String urlStr) {
		FileURL url = access.getURL(urlStr);
		if(url != null) {
			access.getEm().remove(url);
		}
		POIStatus status = access.getStatus(urlStr);
		if(status != null) {
			access.getEm().remove(status);
		}
	}

	@After
	public void close() {
		if (access != null) {
			access.commitTransaction();
			access.close();
		}
	}

	@Test
	public void testGetEm() {
		assertNotNull(access.getEm());
	}

	@Test
	public void testRollbackTransaction() {
		access.rollbackTransaction();

		// start transaction so we can close it in @After
		access.startTransaction();
	}

	@Test
	public void testFileURL() {
		assertEquals(0, access.countURLs());

		// write the first url
		assertTrue(access.writeFileURL(new FileURL("url1", "mime", 200, "abcd", 123, 432, "testfile")));

		FileURL url = access.getURL("url1");
		assertEquals("url1", url.getUrl());
		assertEquals("mime", url.getMime());
		assertEquals(200, url.getStatus());
		assertEquals("abcd", url.getDigest());
		assertEquals(123, url.getLength());
		assertEquals(432, url.getOffset());
		assertEquals("testfile", url.getFilename());

		assertEquals(1, access.countURLs());

		// write a 2nd url
		assertTrue(access.writeFileURL(new FileURL("url2", "mime", 201, "abcde", 124, 434, "testfile2")));

		url = access.getURL("url2");
		assertEquals("url2", url.getUrl());
		assertEquals("mime", url.getMime());
		assertEquals(201, url.getStatus());
		assertEquals("abcde", url.getDigest());
		assertEquals(124, url.getLength());
		assertEquals(434, url.getOffset());
		assertEquals("testfile2", url.getFilename());

		assertEquals(2, access.countURLs());

		// write the same with "older" filename
		assertFalse(access.writeFileURL(new FileURL("url2", "mime", 201, "abcde", 124, 434, "testfile0")));

		url = access.getURL("url2");
		assertEquals("url2", url.getUrl());
		assertEquals("mime", url.getMime());
		assertEquals(201, url.getStatus());
		assertEquals("abcde", url.getDigest());
		assertEquals(124, url.getLength());
		assertEquals(434, url.getOffset());
		assertEquals("testfile2", url.getFilename());

		assertEquals(2, access.countURLs());

		// write the same with "newer" filename
		assertTrue(access.writeFileURL(new FileURL("url2", "mime", 201, "abcde", 124, 434, "testfile3")));

		url = access.getURL("url2");
		assertEquals("url2", url.getUrl());
		assertEquals("mime", url.getMime());
		assertEquals(201, url.getStatus());
		assertEquals("abcde", url.getDigest());
		assertEquals(124, url.getLength());
		assertEquals(434, url.getOffset());
		assertEquals("testfile3", url.getFilename());

		assertEquals(2, access.countURLs());
	}

	@Test
	public void testLargeStrings() {
		assertEquals(0, access.countURLs());

		// first write with the max possible length
		assertTrue(access.writeFileURL(new FileURL(LONG_STR, "mime", 200, "abcd", 123, 432, LONG_STR)));

		FileURL url = access.getURL(LONG_STR);
		assertEquals(LONG_STR, url.getUrl());
		assertEquals("mime", url.getMime());
		assertEquals(200, url.getStatus());
		assertEquals("abcd", url.getDigest());
		assertEquals(123, url.getLength());
		assertEquals(432, url.getOffset());
		assertEquals(LONG_STR, url.getFilename());

		assertEquals(1, access.countURLs());

		// then ensure that overlong string cannot be written
		try {
			access.writeFileURL(new FileURL(OVERLONG_STR, "mime", 200, "abcd", 123, 432, "filename99"));
			fail("Should catch exception here");
		} catch (IllegalArgumentException e) {
			TestHelpers.assertContains(e, "8097");
		}
		try {
			access.writeFileURL(new FileURL("url99", "mime", 200, "abcd", 123, 432, OVERLONG_STR));
			fail("Should catch exception here");
		} catch (IllegalArgumentException e) {
			TestHelpers.assertContains(e, "8097");
		}

		assertEquals(1, access.countURLs());
	}

	@Test
	public void testPOIStatus() {
		//noinspection JpaQlInspection
		String queryString = "select m.filename from POIStatus m";
		TypedQuery<String> q = access.getEm().createQuery(queryString, String.class);

		// See https://issues.apache.org/jira/browse/OPENJPA-1903
		//q.setHint(QueryHints.HINT_IGNORE_PREPARED_QUERY, queryString);

		List<String> filenames = q.getResultList();

		assertEquals("Found files: " + filenames,
				0, access.countStatus(null));

		POIStatus status = new POIStatus("file1");
		access.writePOIStatus(status);

		assertEquals(1, access.countStatus(null));
		assertEquals(0, access.countStatus("1 = 0"));

		status = access.getStatus("file1");
		assertNotNull(status);
		assertEquals("file1", status.getFilename());
		assertNull(status.getPoi313());
		assertNull(status.getPoi314beta1());
		assertNull(status.getPoi314beta2());
		assertNull(status.getPoi315beta1());
		assertNull(status.getPoi315beta2());
		assertNull(status.getPoi315beta3());

		status.setPoi313(FileStatus.OK);
		status.setPoi314beta1(FileStatus.INVALID);
		status.setPoi314beta2(FileStatus.TIMEOUT);
		status.setPoi315beta1(FileStatus.ERROR);
		status.setPoi315beta2(FileStatus.MISSING);
		status.setPoi315beta3(FileStatus.OOM);
		status.setPoi400SNAPSHOT(FileStatus.ZEROBYTES);

		access.writePOIStatus(status);
		status = access.getStatus("file1");
		assertNotNull(status);
		assertEquals("file1", status.getFilename());
		assertEquals(FileStatus.OK, status.getPoi313());
		assertEquals(FileStatus.INVALID, status.getPoi314beta1());
		assertEquals(FileStatus.TIMEOUT, status.getPoi314beta2());
		assertEquals(FileStatus.ERROR, status.getPoi315beta1());
		assertEquals(FileStatus.MISSING, status.getPoi315beta2());
		assertEquals(FileStatus.OOM, status.getPoi315beta3());
		assertEquals(FileStatus.ZEROBYTES, status.getPoi400SNAPSHOT());

		assertEquals(1, access.countStatus(null));
	}

	@Test
	public void testGetAllURLs() {
		assertEquals(0, access.getAllURLs().size());

		// write urls
		assertTrue(access.writeFileURL(new FileURL("url1", "mime", 200, "abcd", 123, 432, "testfile")));
		assertTrue(access.writeFileURL(new FileURL("url2", "mime", 200, "abcd", 123, 432, "testfile")));
		assertTrue(access.writeFileURL(new FileURL("url3", "mime", 200, "abcd", 123, 432, "testfile")));
		assertTrue(access.writeFileURL(new FileURL("url4", "mime", 200, "abcd", 123, 432, "testfile")));

		assertEquals(4, access.getAllURLs().size());
	}

	@Test
	public void testUpdateEntry() {
		POIStatus status = new POIStatus("file1");
		status.setPoi313(FileStatus.ERROR);
		access.writePOIStatus(status);

		status = access.getStatus("file1");
		assertEquals(FileStatus.ERROR, status.getPoi313());

		access.commitTransaction();
		access.startTransaction();

		// try writing a different status in a different transaction
		// here it works, but in ProcessResults it does not !?!
		try (DataAccess lAccess = DataAccessFactory.getInstance(DataAccessFactory.DB_TEST)) {
			lAccess.startTransaction();

			status = lAccess.getStatus("file1");
			status.setPoi313(FileStatus.TIMEOUT);
			lAccess.writePOIStatus(status);

			lAccess.commitTransaction();
		}

		try (DataAccess lAccess = DataAccessFactory.getInstance(DataAccessFactory.DB_TEST)) {
			status = lAccess.getStatus("file1");
			assertEquals(FileStatus.TIMEOUT, status.getPoi313());
		}
	}
}
