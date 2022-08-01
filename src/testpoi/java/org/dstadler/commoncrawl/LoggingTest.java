package org.dstadler.commoncrawl;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class LoggingTest {
	@BeforeAll
	public static void setUpClass() throws IOException {
		LoggingUtils.configureLog4j2();
	}

	@Test
	public void testLogging() {
		final Logger LOG = LogManager.getLogger(LoggingTest.class);

		// only the Fatal logging should be visible when this test is run
		LOG.atTrace().log("Test if trace is logged");
		LOG.atDebug().log("Test if debug is logged");
		LOG.atInfo().log("Test if info is logged");
		LOG.atWarn().log("Test if warn is logged");
		LOG.atError().log("Test if error is logged");
		LOG.atFatal().log("Test if fatal is logged");
	}
}
