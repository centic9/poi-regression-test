package org.dstadler.commoncrawl.datalayer;

import java.io.Closeable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import org.dstadler.commoncrawl.jpa.FileURL;
import org.dstadler.commoncrawl.jpa.POIStatus;
import org.dstadler.commons.logging.jdk.LoggerFactory;

/**
 * Interface to the Database with methods to
 * start/finish transactions and perform read
 * and write operations.
 */
public class DataAccess implements Closeable {
    private static final Logger log = LoggerFactory.make();

	private EntityManager em;

	private EntityManagerFactory factory;

	public DataAccess(EntityManager em, EntityManagerFactory factory) {
		super();
		this.em = em;
		this.factory = factory;
	}

	public EntityManager getEm() {
		return em;
	}

	public void startTransaction() {
		if(log.isLoggable(Level.FINE)) {
			log.fine("Starting transaction");
		}
		em.getTransaction().begin();
	}

	public void commitTransaction() {
		if(log.isLoggable(Level.FINE)) {
			log.fine("Committing transaction");
		}
		em.getTransaction().commit();
	}

	public void rollbackTransaction() {
		if(em.getTransaction().isActive()) {
			log.info("Rolling back transaction");
			em.getTransaction().rollback();
		} else {
			log.info("Not rolling back, transaction is not active");
		}
	}

	@Override
	public void close() {
		em.close();
		em = null;
		factory.close();
		factory = null;
	}

	/**
	 *
	 * @param url The file-information to persist.
	 * @return true if data was written, false if the url already
	 * 		exists with the same or a newer filename
	 */
	public boolean writeFileURL(FileURL url) {
		FileURL existing = getURL(url.getUrl());
		if(existing == null) {
			em.persist(url);
			return true;
		}

		// if this one is newer, remove the current one and write the new one
		// compare on filename as this contains the crawl that had the file
		if(existing.getFilename().compareTo(url.getFilename()) < 0) {
			em.remove(existing);
			em.persist(url);
			return true;
		}

		return false;
	}

	public FileURL getURL(String url) {
		return em.find(FileURL.class, url);
	}

	public void writePOIStatus(POIStatus status) {
		em.merge(status);
	}

	public POIStatus getStatus(String filename) {
		return em.find(POIStatus.class, filename);
	}

	public long countURLs() {
		//noinspection JpaQlInspection
		String queryString = "select count(m) from FileURL m";
		TypedQuery<Long> q = em.createQuery(queryString, Long.class);

		// See https://issues.apache.org/jira/browse/OPENJPA-1903
		//q.setHint(QueryHints.HINT_IGNORE_PREPARED_QUERY, queryString);

		return q.getSingleResult();
	}

	public long countStatus(String where) {
		//noinspection JpaQlInspection
		String queryString = "select count(m) from POIStatus m" + (where != null ? " where " + where : "");
		TypedQuery<Long> q = em.createQuery(queryString, Long.class);

		// See https://issues.apache.org/jira/browse/OPENJPA-1903
		//q.setHint(QueryHints.HINT_IGNORE_PREPARED_QUERY, queryString);

		return q.getSingleResult();
	}

	public List<FileURL> getAllURLs() {
		//noinspection JpaQlInspection
		TypedQuery<FileURL> q = em.createQuery("select m from FileURL m", FileURL.class);
		return q.getResultList();
	}
}
