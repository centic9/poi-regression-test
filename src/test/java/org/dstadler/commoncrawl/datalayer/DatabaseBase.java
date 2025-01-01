package org.dstadler.commoncrawl.datalayer;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DatabaseBase {
	public static DatabaseStarter starter = new DatabaseStarter();

	@BeforeAll
	public static void startDB() throws IOException {
		starter.start();
	}

	@AfterAll
	public static void stopDB() {
		assertTrue(starter.stop(),
				"Stopping database failed, look for log with message 'Unable to stop database management system'");

		// With Gradle many tests are run in one VM, so we should not
		// remove them here
//		Enumeration<Driver> drivers = DriverManager.getDrivers();
//		while(drivers.hasMoreElements()) {
//			Driver driver = drivers.nextElement();
//			DriverManager.deregisterDriver(driver);
//		}
	}
}
