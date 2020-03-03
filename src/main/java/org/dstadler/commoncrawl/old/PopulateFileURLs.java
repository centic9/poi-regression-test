package org.dstadler.commoncrawl.old;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.dstadler.commoncrawl.datalayer.DataAccess;
import org.dstadler.commoncrawl.datalayer.DataAccessFactory;
import org.dstadler.commoncrawl.datalayer.DatabaseStarter;
import org.dstadler.commoncrawl.index.CDXItem;
import org.dstadler.commoncrawl.jpa.FileURL;
import org.dstadler.commons.logging.jdk.LoggerFactory;

public class PopulateFileURLs {
	private static final Logger log = LoggerFactory.make();

	private static final File COMBINED_FILE = new File("../CommonCrawlDocumentDownload/commoncrawl.txt");

	public static void main(String[] args) throws Exception {
		LoggerFactory.initLogging();

		if(!COMBINED_FILE.exists()) {
			throw new IllegalStateException("Cannot write URLs to database without the combined file of urls at " + COMBINED_FILE);
		}

        DatabaseStarter.ensureDatabase(11527);

        log.info("Reading file " + COMBINED_FILE + ", " + COMBINED_FILE.length() + " bytes");
        try (DataAccess access = DataAccessFactory.getInstance(DataAccessFactory.DB_PROD)) {
        	log.info("Having " + access.countURLs() + " URLs before starting to write");

    		try (BufferedReader reader = new BufferedReader(new FileReader(COMBINED_FILE), 1024*1024)) {
    			int count = 0, written = 0;

    			access.startTransaction();
    			while(true) {
    				String line = reader.readLine();
    				if(line == null) {
    					break;
    				}

    				CDXItem item = CDXItem.parse(line);
    				FileURL url = convertToFileURL(item);
    				written += access.writeFileURL(url) ? 1 : 0;

    				count++;
    				if(count % 1000 == 0) {
    					log.info("Committing after " + count + " lines, written: " + written + ", current line: " + line);
    					access.commitTransaction();
    					access.startTransaction();
    				}
    			}

    			access.commitTransaction();

    			log.info("Having " + access.countURLs() + " URLs after writing, of the " + count + " lines " + written + " were newly written");
    		}
        } catch (Exception e) {
        	StringWriter fullException = new StringWriter();
			try (PrintWriter writer = new PrintWriter(fullException)) {
        		ExceptionUtils.printRootCauseStackTrace(e, writer);
        	}
        	log.log(Level.SEVERE, "Exception\n" + fullException);
        }
	}

	private static FileURL convertToFileURL(CDXItem item) {
		return new FileURL(item.url, item.mime, Integer.parseInt(item.status), item.digest, item.length, item.offset, item.filename);
	}
}
