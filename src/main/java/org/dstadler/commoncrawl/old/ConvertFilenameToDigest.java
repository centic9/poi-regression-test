package org.dstadler.commoncrawl.old;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.io.FilenameUtils;
import org.dstadler.commoncrawl.datalayer.DataAccess;
import org.dstadler.commoncrawl.datalayer.DataAccessFactory;
import org.dstadler.commoncrawl.datalayer.DatabaseStarter;
import org.dstadler.commoncrawl.jpa.FileURL;
import org.dstadler.commons.logging.jdk.LoggerFactory;

import static org.dstadler.commoncrawl.report.BaseReport.ROOT_DIR;

/**
 * Obsolete utility to work with URLs in the database
 */
public class ConvertFilenameToDigest {
	private static final Logger log = LoggerFactory.make();

	public static void main(String[] args) throws Exception {
		LoggerFactory.initLogging();

		if(!ROOT_DIR.exists()) {
			throw new IllegalStateException("Cannot rename files without directory " + ROOT_DIR);
		}

        DatabaseStarter.ensureDatabase(11527);

        Map<String, String> filenameToDigest = new HashMap<>();
        Set<String> digests = new HashSet<>();
        log.info("Reading filename to digest mapping from database");
        try (DataAccess access = DataAccessFactory.getInstance(DataAccessFactory.DB_PROD)) {
        	List<FileURL> urls = access.getAllURLs();
        	for(FileURL url : urls) {
        		filenameToDigest.put(url.getFilename(), url.getDigest());
        		digests.add(url.getDigest());
        	}
        }

    	int count = 0;

    	File[] files = ROOT_DIR.listFiles();
		if(files == null) {
			throw new IllegalStateException("Did not find directory " + ROOT_DIR.getAbsolutePath());
		}
    	log.info("Reading " + files.length + " files from " + ROOT_DIR);
    	for(File file : files) {
    		String digest = filenameToDigest.get(file.getName());
    		if(digest == null && !digests.contains(FilenameUtils.removeExtension(file.getName()))) {
    			throw new IllegalArgumentException("Could not convert file " + file.getName());
    		}

    		log.info("Found digest " + digest + " for filename " + file.getName());
    	}
    	log.info("Renamed " + count + " files");
	}
}
