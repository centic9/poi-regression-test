package org.dstadler.commoncrawl.datalayer;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;

public class DatabaseBase {
	public static DatabaseStarter starter = new DatabaseStarter();

	@BeforeClass
	public static void startDB() throws IOException {
		starter.start();
	}

	@AfterClass
	public static void stopDB() {
		assertTrue(starter.stop());

		// With Gradle many tests are run in one VM, so we should not
		// remove them here
//		Enumeration<Driver> drivers = DriverManager.getDrivers();
//		while(drivers.hasMoreElements()) {
//			Driver driver = drivers.nextElement();
//			DriverManager.deregisterDriver(driver);
//		}
	}
}
