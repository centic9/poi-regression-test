package org.dstadler.commoncrawl.datalayer;

import static org.junit.Assert.*;

import org.dstadler.commons.testing.PrivateConstructorCoverage;
import org.junit.Test;

public class DataAccessFactoryTest extends DatabaseBase {

	@Test
	public void testGetInstance() {
		try (DataAccess access = DataAccessFactory.getInstance(DataAccessFactory.DB_TEST)) {
			assertNotNull(access);
		}
	}

	// helper method to get coverage of the unused constructor
	@Test
	public void testPrivateConstructor() throws Exception {
		PrivateConstructorCoverage.executePrivateConstructor(DataAccessFactory.class);
	}
}
