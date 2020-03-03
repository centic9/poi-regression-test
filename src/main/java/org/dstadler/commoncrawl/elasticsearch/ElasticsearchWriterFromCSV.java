package org.dstadler.commoncrawl.elasticsearch;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import org.apache.log4j.LogManager;
import org.dstadler.commoncrawl.jpa.POIStatus;
import org.dstadler.commons.logging.jdk.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

/**
 * Sends POIStatus items to Elasticsearch from a CSV export file
 * produced from the Derby-Database.
 *
 * Note that this is rather "flaky" as the CSV-Export does not
 * contain headers and does change when new columns are added
 * so we switched to {@link ElasticsearchWriterFromJSON} mostly now.
 *
 * https://db.apache.org/derby/docs/10.14/adminguide/cadminimport16245.html
 *
 * call SYSCS_UTIL.SYSCS_EXPORT_TABLE
 *  ('APP', 'POISTATUS','/tmp/export.sql',null, null, null)
 */
public class ElasticsearchWriterFromCSV extends ElasticsearchWriter {
    private static final Logger log = LoggerFactory.make();

    public static void main(String[] args) throws Exception {
        LoggerFactory.initLogging();

        long start = System.currentTimeMillis();

        if(args.length <= 2) {
            log.severe("Usage: ElasticsearchWriterFromCSV <eshost> <esuser> <espassword>");
            System.exit(1);
        }

        setupTemplate(args[0], args[1], args[2]);

        try (InputStream stream = new GZIPInputStream(new FileInputStream("/tmp/export.sql.gz"), 16*1024)) {
            MappingIterator<POIStatus> statusIter = new CsvMapper().readerWithTypedSchemaFor(POIStatus.class).readValues(stream);
            writeDocuments(statusIter, args[0], args[1], args[2]);
        }

        log.info("Done after " + (System.currentTimeMillis() - start)/1000 + " seconds");

        LogManager.shutdown();
    }
}
