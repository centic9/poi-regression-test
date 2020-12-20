package org.dstadler.commoncrawl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.poi.Version;
import org.apache.poi.stress.FileHandler;
import org.apache.poi.stress.TestAllFiles;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

/**
 * Load a list of files and their mime-types from a JSON file and process the
 * files according to a mapping of the mime-type to extension.
 *
 * This is just a helper tool, the main tool for running the regression-tests is {@link ProcessFiles}
 */
public class ProcessJSONListOfFiles {
    private static final File BASE_DIR = new File("/");

    private static final File RESULT_FILE = new File("result-vm-" + Version.getVersion() + "-" +
            FastDateFormat.getInstance("yyyy-MM-dd-HH-mm").format(new Date()) + ".json");

    private static final Set<String> EXCLUDES = new HashSet<>();
    static {
        EXCLUDES.add("/data2/docs/commoncrawl2/5W/5WUVHHK4YQ5HJQVPA5WG4F3TB2UGZJGF");
    }

    public static void main(String[] args) throws Throwable {
        final File jsonFile;
        if(args.length == 0) {
            jsonFile = new File("/opt/file-type-detection/filetypes.txt");
        } else {
            jsonFile = new File(args[0]);
        }

        processFile(jsonFile);
    }

    static void processFile(File jsonFile) throws InterruptedException, IOException {
        try (Writer resultWriter = new FileWriter(RESULT_FILE, true)) {
            ExecutorService executor = Executors.newSingleThreadExecutor();

            long start = System.currentTimeMillis();

            try {
                // read once for line-count to have progress-reporting in the output
                try (Stream<String> lines = Files.lines(jsonFile.toPath(), Charset.defaultCharset())) {
                    FileHandlingRunnable.filesCount = lines.count();
                }

                // and a second time for the actual processing
                try (Stream<String> lines = Files.lines(jsonFile.toPath(), Charset.defaultCharset())) {
                    lines.forEachOrdered(line -> process(line, executor, start, resultWriter));
                }

                executor.shutdown();

                System.out.println("Having results from " + FileHandlingRunnable.filesCount + " files with " + FileHandlingRunnable.ignored.get() + " ignored files overall");
            } catch (Throwable e) {
                e.printStackTrace();
                // stop as soon as possible when any throwable is caught
                executor.shutdownNow();
                throw e;
            }

            System.out.println("Awaiting termination");
            executor.awaitTermination(60, TimeUnit.MINUTES);

            System.out.println("Done");
        } catch (Throwable e) {
            e.printStackTrace();
            throw e;
        }
    }

    protected static void process(String line, ExecutorService executor, long start, Writer resultWriter) {
        try {
            JsonElement jelement = JsonParser.parseString(line);
            JsonObject jobject = jelement.getAsJsonObject();
            String fileName = jobject.get("fileName").getAsString();
            try {
                String mimeType = jobject.get("mediaType").getAsString();

                // only process certain mime-types
                if (!MimeTypes.matches(mimeType)) {
                    FileHandlingRunnable.ignored.incrementAndGet();
                    return;
                }
                if (EXCLUDES.contains(fileName)) {
                    FileHandlingRunnable.ignored.incrementAndGet();
                    return;
                }
                // use to re-start at a certain file if it is specified here
                if (fileName.compareTo("") <= 0) {
                    FileHandlingRunnable.ignored.incrementAndGet();
                    return;
                }

                FileHandler fileHandler = TestAllFiles.HANDLERS.get(MimeTypes.toExtension(mimeType));

                //new FileHandlingRunnable(start, fileName, fileHandler, resultWriter).run();
                FileHandlingRunnable runnable = new FileHandlingRunnable(start, fileName, fileHandler, resultWriter, BASE_DIR);
                Future<?> future = executor.submit(runnable);

                // wait for execution and receive any results
                while (true) {
                    try {
                        // immediately wait for the future with a Timeout to stop any that take too long
                        future.get(1, TimeUnit.MINUTES);

                        // done for this future
                        break;
                    } catch (TimeoutException e) {
                        ProcessFiles.handleTimeout(resultWriter, e);
                    } catch (OutOfMemoryError e) {
                        ProcessFiles.handleOOM(resultWriter, e);
                    } catch (ExecutionException e) {
                        //writeResult(resultWriter, future.get, null, true);
                        System.out.println("Fatal exception for future: " + future);
                        e.printStackTrace();
                    }
                }
            } catch (OutOfMemoryError e) {
                System.out.println("OutOfMemory exception: " + e);
                e.printStackTrace();

                FileHandlingRunnable.writeResult(resultWriter, fileName, null, true, -1);
            }
        } catch (JsonParseException e) {
            System.out.println("Could not parse JSON for line: " + line);
        } catch (RuntimeException | InterruptedException e) {
            throw new RuntimeException("While handling line: " + line, e);
        }
    }
}
