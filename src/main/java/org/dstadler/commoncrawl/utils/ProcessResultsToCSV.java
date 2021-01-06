package org.dstadler.commoncrawl.utils;

import com.fasterxml.jackson.core.JsonParseException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.text.translate.CharSequenceTranslator;
import org.apache.commons.text.translate.CsvTranslators;
import org.dstadler.commoncrawl.ResultItem;
import org.dstadler.commons.logging.jdk.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProcessResultsToCSV {
    private static final Logger log = LoggerFactory.make();

    private static final CharSequenceTranslator translator = new CsvTranslators.CsvEscaper();

    public static void main(String[] args) throws Exception {
        LoggerFactory.initLogging();

        for(String resultFile : args) {
            String fileName = resultFile + ".csv";
            try (Writer writer = new BufferedWriter(new FileWriter(fileName))) {
                log.info("Writing to " + fileName);

                handleFile(writer, new File(resultFile));
            } catch (Exception e) {
                StringWriter fullException = new StringWriter();
                try (PrintWriter writer = new PrintWriter(fullException)) {
                    ExceptionUtils.printRootCauseStackTrace(e, writer);
                }
                log.log(Level.SEVERE, "Exception\n" + fullException);
            }
        }
    }

    public static void handleFile(Writer writer, File resultFile) throws IOException {
        if(!resultFile.exists()) {
            throw new IllegalStateException("Cannot write results to database without the result file at " + resultFile);
        }

        log.info("Reading file " + resultFile + ", " + resultFile.length() + " bytes");
        try (BufferedReader reader = new BufferedReader(new FileReader(resultFile), 1024*1024)) {
            int count = 0;

            while(true) {
                String line = reader.readLine();
                if(line == null) {
                    break;
                }

                if (!handleLine(writer, count, line)) {
                    continue;
                }

                count++;
                if(count % 50000 == 0) {
                    log.info("Having " + count + " lines, current file: " + resultFile + ", current line: " + line);
                }
            }

            log.info("Processed " + count + " from " + resultFile);
        }
    }

    static boolean handleLine(Writer writer, int count, String line) throws IOException {
        final ResultItem item;
        try {
            item = ResultItem.parse(line);
        } catch (JsonParseException e) {
            log.log(Level.WARNING, "Failed to parse line " + count + ": " + line, e);
            return false;
        }

        writeToCSV(writer, item);

        return true;
    }

    private static void writeToCSV(Writer writer, ResultItem item) throws IOException {
        translator.translate(item.getFileName(), writer);
        writer.append(',');
        translator.translate(item.getExceptionText() == null ?
                "null" :
                item.getExceptionText().replace("\n", " "), writer);
        writer.append(',');
        //translator.translate(item.getExceptionStacktrace(), writer);
        //writer.append(',');
        writer.append(Long.toString(item.getDuration()));
        writer.append(',');
        writer.append(Boolean.toString(item.isTimeout()));
        writer.append('\n');
    }
}
