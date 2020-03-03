package org.dstadler.commoncrawl;

import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.POIFileScanner;
import org.apache.poi.Version;
import org.apache.poi.stress.FileHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;

/**
 * Process all files found in the download-directory and handle them
 * in multiple threads. This works better than TestCorpusFiles if you
 * have a large number of files, e.g. by using binaries from the CommonCrawl
 * as JUnit cannot handle hundreds of thousands of files.
 */
@SuppressWarnings("Duplicates")
public class ProcessFiles {
    private static final int NUMBER_OF_THREADS = 12;

    // these are currently duplicated from BaseReport here as Gradle or IntelliJ does not
    // set the dependency correctly
    protected static final File ROOT_DIR = new File("../download");
    protected static final File BACKUP_DIR = new File("../backup");

    private static final File RESULT_FILE = new File("result-" + Version.getVersion() + "-" +
            FastDateFormat.getInstance("yyyy-MM-dd-HH-mm").format(new Date()) + ".json");

    private static final Set<String> EXCLUDES = new HashSet<>();

    static {
        // causes native OOM in some Sun-drawing methods or take a long time due to nearly OOM with 1G Xmx...
        EXCLUDES.add("focus-schools.dpi.wi.gov_sites_default_files_imce_focus-schools_ppt_title_i_funding_flexibility_options.ppsx.pptx");
        EXCLUDES.add("download.oldindex/aero.aixm.www_gallery_content_public_2009_12_aixm_seminar_2-AIXM_205.1_20-_20UML_to_XSD_20-_20AIXM_5.1_Schemas.ppt");
        EXCLUDES.add("download.oldindex/ar.org.ateentrerios.www_formacion_formacion5.doc");
        EXCLUDES.add("download.oldindex/ca.smpdb.pathman_system_powerpoints_468_original_Spermidine_Spermine_Biosynthesis.ppt");
        EXCLUDES.add("download.oldindex/ca.smpdb.pathman_system_powerpoints_470_original_CYCLOPHOSPHAMIDE.ppt");
        EXCLUDES.add("scstatehouse.gov_getfile.php_type=codeoflaws&title=2&chapter=1.docx");
        EXCLUDES.add("zumrohhasanah.files.wordpress.com_2010_12_update-2-07-2011-sosialisasi-pendidikan-bidan-unair-by-infokom-hima-bidan1.ppsx.pptx");
        EXCLUDES.add("download.oldindex/co.edu.colombiaaprende.www_html_directivos_1598_articles-177759_archivo16.ppt");
    }

    public static void main(String[] args) throws Throwable {
        Collection<Entry<String, FileHandler>> files =
                POIFileScanner.scan(ROOT_DIR);

        File resultFile = RESULT_FILE;
        if(args.length > 0) {
            resultFile = new File(args[0]);
        }

        try (Writer resultWriter = new FileWriter(resultFile, true)) {
            FileHandlingRunnable.filesCount = files.size();

            ExecutorService executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
            ScheduledExecutorService memoryChecker = Executors.newScheduledThreadPool(1);

            long start = System.currentTimeMillis();

            try {
                // schedule memory checker
                memoryChecker.scheduleWithFixedDelay(new MemoryChecker(resultWriter), 5, 5, TimeUnit.SECONDS);

                List<Future<?>> futures = new ArrayList<>();
                for(final Entry<String, FileHandler> pair : files) {
                    if(EXCLUDES.contains(pair.getKey())) {
                        FileHandlingRunnable.ignored.incrementAndGet();

                        continue;
                    }
                    if(pair.getKey().compareTo("") <= 0) {
                        FileHandlingRunnable.ignored.incrementAndGet();

                    	continue;
                    }
                    //new FileHandlingRunnable(start, pair.getKey(), pair.getRight(), resultWriter).run();
                    futures.add(executor.submit(new FileHandlingRunnable(start, pair.getKey(), pair.getValue(), resultWriter, ROOT_DIR)));
                }

                // free list of files as it is not needed any more now
                //noinspection UnusedAssignment
                files = null;

                // no new tasks expected now, trigger ordered shutdown
                executor.shutdown();

                System.out.println("Collecting results from " + futures.size() + " futures");
                Iterator<Future<?>> it = futures.iterator();
                while(it.hasNext()) {
                    Future<?> future = it.next();

                    // wait for execution and receive any results
                    handleFuture(resultWriter, future);

                    // remove futures that are done to free up memory, they hold full stacktraces and other
                    // bits of information after execution
                    it.remove();
                }

                memoryChecker.shutdownNow();
            } catch (Throwable e) {
                e.printStackTrace();
                // stop as soon as possible when any throwable is caught
                executor.shutdownNow();
                memoryChecker.shutdownNow();
                throw e;
            }

            System.out.println("Awaiting termination");
            executor.awaitTermination(60, TimeUnit.MINUTES);
            memoryChecker.awaitTermination(60, TimeUnit.MINUTES);

            System.out.println("Done");
        } catch (Throwable e) {
            e.printStackTrace();
            throw e;
        }
    }

    private static final MemoryMXBean MEMORY_MX_BEAN = ManagementFactory.getMemoryMXBean();
    private static final long MEMORY_AVAILABLE = MEMORY_MX_BEAN.getHeapMemoryUsage().getMax();
    private static final int MEMORY_KEEP_AMOUNT = 500 * 1024 * 1024;

    /**
     * Small runnable which is invoked repeatedly and checks if leftover free
     * memory is below 500MB and starts stopping threads in this case to avoid
     * running into full-blown OutOfMemory errors
     */
    protected static class MemoryChecker implements Runnable {
        private final Writer resultWriter;

        public MemoryChecker(Writer resultWriter) {
            this.resultWriter = resultWriter;
        }

        @Override
        public void run() {
            long used = MEMORY_MX_BEAN.getHeapMemoryUsage().getUsed();
            if(MEMORY_AVAILABLE > 0 && used > (MEMORY_AVAILABLE - MEMORY_KEEP_AMOUNT)) {
                try {
                    handleOOM(resultWriter, new OutOfMemoryError("Found " + used + " bytes of memory in use with " + MEMORY_AVAILABLE +
                            " bytes available overall, thus leaving only " + (MEMORY_AVAILABLE - used) + " bytes free, " +
                            "but limit is " + MEMORY_KEEP_AMOUNT + " thus stopping threads to avoid OOM"));
                } catch (InterruptedException e) {
                    throw new IllegalStateException(e);
                }
            }
        }
    }

    private static void handleFuture(Writer resultWriter, Future<?> future) throws InterruptedException {
        while(true) {
            try {
                future.get(1, TimeUnit.MINUTES);

                // done for this future
                break;
            } catch (TimeoutException e) {
                handleTimeout(resultWriter, e);
            } catch (OutOfMemoryError e) {
                handleOOM(resultWriter, e);
            } catch (ExecutionException e) {
                if (e.getCause() instanceof OutOfMemoryError) {
                    handleOOM(resultWriter, (OutOfMemoryError) e.getCause());
                } else {
                    //writeResult(resultWriter, future.get, null, true);
                    System.out.println("Fatal exception for future: " + future);
                    e.printStackTrace();
                }
            }
        }
    }

    static void handleTimeout(Writer resultWriter, TimeoutException e) throws InterruptedException {
        // timeout, let's check if some files take a long time...
        Iterator<Entry<String, Pair<Long, Thread>>> iterator = FileHandlingRunnable.startTimes.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Pair<Long, Thread>> entry = iterator.next();
            if (entry.getValue().getKey() < (System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(5))) {
                System.out.println("Stopping processing for file " + entry.getKey() + " due to timeout: " + e);
                FileHandlingRunnable.stopThread(resultWriter, iterator, entry.getKey(), entry.getValue().getValue());
            }
        }
    }

    static void handleOOM(Writer resultWriter, OutOfMemoryError e) throws InterruptedException {
        /*// try to handle OOM by
        Thread.sleep(120*1000);

        writeResult(resultWriter, , null, true);*/

        // kill the longest running futures to try to gracefully handle OutOfMemory
        Iterator<Entry<String, Pair<Long, Thread>>> iterator = FileHandlingRunnable.startTimes.entrySet().iterator();
        if(iterator.hasNext()) {
            Entry<String, Pair<Long, Thread>> entry = iterator.next();
            System.out.println("Stopping processing for file " + entry.getKey() + " due to memory shortage: " + e);
            FileHandlingRunnable.stopThread(resultWriter, iterator, entry.getKey(), entry.getValue().getValue());
        }
    }
}
