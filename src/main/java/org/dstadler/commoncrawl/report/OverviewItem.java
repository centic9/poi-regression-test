package org.dstadler.commoncrawl.report;

import org.dstadler.commoncrawl.jpa.FileStatus;

/**
 * Object to hold information for generating the HTML reports.
 */
public class OverviewItem {
	private final int count;
	private final FileStatus statusBefore;
	private final FileStatus statusNow;
	private final String fileName;
	private final String percentage;

	public OverviewItem(int count, FileStatus statusBefore, FileStatus statusNow, String fileName, String percentage) {
		super();
		this.count = count;
		this.statusBefore = statusBefore;
		this.statusNow = statusNow;
		this.fileName = fileName;
		this.percentage = percentage;
	}

	public int getCount() {
		return count;
	}

	@SuppressWarnings("unused")	// called by Velocity
	public FileStatus getStatusBefore() {
		return statusBefore;
	}

	@SuppressWarnings("unused")	// called by Velocity
	public FileStatus getStatusNow() {
		return statusNow;
	}

	public String getFileName() {
		return fileName;
	}

	@SuppressWarnings("unused")	// called by Velocity
	public String getBackgroundColor() {
		if(statusBefore == FileStatus.OK &&
				(statusNow == FileStatus.ERROR || statusNow == FileStatus.INVALID)) {
			return "yellow";
		}

		return "white";
	}

	@SuppressWarnings("unused")	// called by Velocity
	public String getPercentage() {
		return percentage;
	}
}
