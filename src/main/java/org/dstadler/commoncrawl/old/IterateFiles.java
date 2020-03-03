package org.dstadler.commoncrawl.old;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.dstadler.commoncrawl.MimeTypes;
import org.dstadler.commoncrawl.Utils;
import org.dstadler.commoncrawl.datalayer.DataAccess;
import org.dstadler.commoncrawl.datalayer.DataAccessFactory;
import org.dstadler.commoncrawl.datalayer.DatabaseStarter;
import org.dstadler.commoncrawl.jpa.FileURL;
import org.dstadler.commons.logging.jdk.LoggerFactory;

import static org.dstadler.commoncrawl.report.BaseReport.ROOT_DIR;

/**
 * Obosolete tool to list all URLs in the database
 */
public class IterateFiles {
	private static final Logger log = LoggerFactory.make();

    private Map<String, String> filenameToDigest = new HashMap<>();
    private Set<String> digests = new HashSet<>();

	public static void main(String[] args) throws Exception {
		LoggerFactory.initLogging();

		new IterateFiles().run();
	}

	private void run() {
		if(!ROOT_DIR.exists()) {
			throw new IllegalStateException("Cannot rename files without directory " + ROOT_DIR);
		}

        readFromDatabase();

    	checkAllFiles();
	}

	private void checkAllFiles() {
		int count = 0;
    	List<String> missingInDB = new ArrayList<>();
    	File[] files = ROOT_DIR.listFiles();
		if(files == null) {
			throw new IllegalStateException("Did not find directory " + ROOT_DIR.getAbsolutePath());
		}
    	log.info("Reading " + files.length + " files from " + ROOT_DIR);
    	for(File file : files) {
    		String digest = filenameToDigest.get(file.getName());
    		if(digest == null) {
    			//throw new IllegalArgumentException("Could not find file " + file.getName());
    			missingInDB.add(file.getName());
    			continue;
    		}

    		log.info("Found digest " + digest + " for filename " + file.getName());
    	}
    	log.info("Found " + count + " files, " + missingInDB.size() + " were not found in the database");
	}

	private void readFromDatabase() {
		DatabaseStarter.ensureDatabase(11527);

        try (DataAccess access = DataAccessFactory.getInstance(DataAccessFactory.DB_PROD)) {
        	log.info("Reading " + access.countURLs() + " filename to digest mappings from database");
        	List<FileURL> urls = access.getAllURLs();
        	for(FileURL url : urls) {
        		filenameToDigest.put(Utils.computeDownloadFileName(url.getUrl(), MimeTypes.toExtension(url.getMime())).getName(), url.getDigest());
        		digests.add(url.getDigest());
        	}
        	log.info("Found " + filenameToDigest.size() + " filenames and " + digests.size() + " digests");
        }
	}
}
