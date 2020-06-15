package org.dstadler.commoncrawl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.BaseIntegrationTest;
import org.apache.poi.stress.FileHandler;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Runnable for testing one file.
 *
 * It also provides methods to forcefully kill a thread which is
 * not responding any more.
 *
 * When finished, it writes results to the output JSON file
 */
public final class FileHandlingRunnable implements Runnable {
    private final static Gson gson = new GsonBuilder().create();
    private final static FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("MMM dd HH:mm:ss");

    // for stopping after a test takes too long
    protected static ConcurrentMap<String, Pair<Long,Thread>> startTimes = new ConcurrentSkipListMap<>();

    protected static long filesCount;
    protected static AtomicInteger
            count = new AtomicInteger(0),
            ignored = new AtomicInteger(0),
            failed = new AtomicInteger(0);

    private final long start;
    private final String file;
    private final FileHandler fileHandler;
    private final Writer resultWriter;
    private final File rootDir;

    public FileHandlingRunnable(long start, String file, FileHandler fileHandler, Writer resultWriter, File rootDir) {
        this.start = start;
        this.file = file;
        this.fileHandler = checkNotNull(fileHandler, "Did not get a handler for %s", file);
        this.resultWriter = resultWriter;
        this.rootDir = rootDir;
    }

    @Override
    public void run() {
        long fileStart = System.currentTimeMillis();

        Thread.currentThread().setName(file);

        System.out.println("Running file " + file + " using " + fileHandler.getClass().getSimpleName());

        BaseIntegrationTest main = new BaseIntegrationTest(rootDir, file, fileHandler);

        int localCount = count.incrementAndGet();
        try {
            startTimes.put(file, Pair.of(System.currentTimeMillis(), Thread.currentThread()));
            try {
                main.test();
            } finally {
                startTimes.remove(file);
            }

            writeResult(resultWriter, file, null, false, System.currentTimeMillis() - fileStart);

            printInfo(localCount, "");
        } catch (Throwable e) {
            failed.incrementAndGet();
            writeResult(resultWriter, file, e, false, -1);
            printInfo(localCount, ", failure: " + e.toString());
        }
    }

    public void printInfo(int localCount, String info) {
        long diff = (System.currentTimeMillis() - start)/1000;
        long allCount = localCount+ignored.get();
        double countPerMinute = ((double)allCount)/diff*60;
        double remaining = filesCount-allCount;

        System.out.println(String.format("%,d", localCount) + " done, " + String.format("%,d", ignored.get()) +
                " ignored, thus " + String.format("%,d", allCount) + " of " + String.format("%,d", filesCount) + " done, " +
                String.format("%,d", failed.get()) + " failed, " +
                "took " + diff + " seconds (" + String.format("%.2f", ((double)diff)/60) + " minutes, " +
                String.format("%.2f", ((double)diff)/60/60) + " hours), " + String.format("%.2f", countPerMinute) + " per minute, " +
                String.format("%.2f", ((double)allCount)/filesCount*100) + "%, " +
                "estimated remaining time: " + String.format("%.0f", remaining/countPerMinute) + " minutes, " +
                "estimated finish at: " + DATE_FORMAT.format(DateUtils.addSeconds(new Date(), (int) (remaining*60/countPerMinute))) +
                ", " + file + " using " + (fileHandler == null ? "<null>" : fileHandler.getClass().getSimpleName()) + info);
    }

    public static void writeResult(Writer resultWriter, String file, Throwable e, boolean timeout, long duration) {
        Result result = new Result(file, e, timeout, duration);

        // need to sync globally here to only ever write to the file from one thread
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (resultWriter) {
            gson.toJson(result, resultWriter);
            try {
                resultWriter.append("\n");
            } catch (IOException e1) {
                throw new IllegalStateException(e1);
            }
        }
    }

    private static class Result {
        String fileName;
        String exceptionText;
        String exceptionStacktrace;
        boolean timeout;
        long duration;

        public Result(String fileName, Throwable exception, boolean timeout, long duration) {
            super();
            this.fileName = fileName;
            this.timeout = timeout;
            if(exception != null) {
                this.exceptionText = exception.toString();
                this.exceptionStacktrace = ExceptionUtils.getStackTrace(exception);
            }
            this.duration = duration;
        }
    }

    protected static void stopThread(Writer resultWriter, Iterator<Map.Entry<String, Pair<Long, Thread>>> iterator, String file, Thread thread) throws InterruptedException {
        thread.interrupt();

        // allow some time to stop
        Thread.sleep(2000);

        killThread(resultWriter, iterator, file, thread);
    }

    protected static void killThread(Writer resultWriter, Iterator<Map.Entry<String, Pair<Long, Thread>>> iterator, String file, Thread thread) {
        // forcefully kill it
        //noinspection deprecation
        thread.stop();

        // then remove it from the list
        iterator.remove();

        writeResult(resultWriter, file, null, true, -1);
    }
}
