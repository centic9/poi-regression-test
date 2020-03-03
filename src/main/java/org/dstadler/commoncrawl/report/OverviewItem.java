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

	public OverviewItem(int count, FileStatus statusBefore, FileStatus statusNow, String fileName) {
		super();
		this.count = count;
		this.statusBefore = statusBefore;
		this.statusNow = statusNow;
		this.fileName = fileName;
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
}
