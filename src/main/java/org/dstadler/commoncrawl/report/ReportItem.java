package org.dstadler.commoncrawl.report;

import org.dstadler.commoncrawl.jpa.FileStatus;
import org.dstadler.commoncrawl.utils.BugAnnotations;

/**
 * Object to hold information for generating the HTML reports.
 */
public class ReportItem {
	private int count;
	private final FileStatus status;
	private final String exception;
	private final String stacktrace;
	private final String fileName;

	public ReportItem(int count, FileStatus status, String exception, String stacktrace, String fileName) {
		super();
		this.count = count;
		this.status = status;
		// shorten some packages in the exception and stacktrace
		this.exception = BugAnnotations.getReplacement(exception);
		this.stacktrace = BugAnnotations.getReplacement(stacktrace)
				// remove some parts of the stacktrace that are always equal and unrelated anyway
				.replaceAll("\tat o\\.a\\.p\\.BaseIntegrationTest\\.handleFile\\(BaseIntegrationTest\\.java:\\d+\\)\n", "")
				.replaceAll("\tat o\\.a\\.p\\.BaseIntegrationTest\\.test\\(BaseIntegrationTest\\.java:\\d+\\)\n", "")
				.replaceAll("\tat org\\.dstadler\\.commoncrawl\\.ProcessFiles\\$FileHandlingRunnable\\.run\\(ProcessFiles\\.java:\\d+\\)\n", "")
				.replace("\tat java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511)\n", "")
				.replace("\tat java.util.concurrent.FutureTask.run(FutureTask.java:266)\n", "")
				.replace("\tat java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)\n", "")
				.replace("\tat java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)\n", "")
				.replace("\tat java.lang.Thread.run(Thread.java:745)", "");
		this.fileName = fileName;
	}

	public int getCount() {
		return count;
	}

	public FileStatus getStatus() {
		return status;
	}

	public String getAnnotation() {
		return BugAnnotations.getAnnotation(stacktrace);
	}

	public String getException() {
		return exception;
	}

	public String getStacktrace() {
		return stacktrace;
	}

	public String getFileName() {
		return fileName;
	}

	public void addCount(int count) {
		this.count += count;
	}
}
