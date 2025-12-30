package org.dstadler.commoncrawl.datalayer;

import java.util.logging.Logger;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import org.dstadler.commons.logging.jdk.LoggerFactory;

/**
 * Factory to instantiate the {@link DataAccess}
 */
public class DataAccessFactory {
    private static final Logger log = LoggerFactory.make();

    public static final String DB_PROD = "commoncrawl";
    public static final String DB_TEST = "commoncrawltest";

	public static DataAccess getInstance(String db) {
		log.info("Creating DataAccessLayer");

		EntityManagerFactory factory = Persistence.createEntityManagerFactory(db, System.getProperties());

		if(factory == null) {
			throw new IllegalStateException("Could not initialize EntityManagerFactory.");
		}

		EntityManager em = factory.createEntityManager();

		return new DataAccess(em, factory);
	}
}
