package org.dstadler.commoncrawl.jpa;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.apache.commons.lang3.StringUtils;

/**
 * Different states that a file can have in each version that is tested
 */
public enum FileStatus {
	// set the number to order by "severity", i.e. errors first

    // =====================================================================================================
    // NOTE: The value used in the database is not "order", but ordinal, which is counted starting from top!

	// processing was fine
	OK(6),

	// file was of an invalid format
	INVALID(1),

	// processing failed with an exception or other error
	ERROR(0),

	// file was not found on disk any more (e.g. may have been removed by duplicate check)
	MISSING(2),

	// processing the file took too long and thus it was cancelled
	// Note: this can also be caused by other files processed at the same time
	TIMEOUT(3),

	// processing the file ran into out-of-memory
	// Note: this can also be caused by other files processed at the same time
	OOM(4),

	// the file was empty
	ZEROBYTES(5),

	// file is reported as having an "old format"
	OLDFORMAT(6);

	private final int order;

	FileStatus(int order) {
		this.order = order;
	}

	public int getOrder() {
		return order;
	}

	@SuppressWarnings("unused")
	@JsonCreator
	public static FileStatus forValue(String value) {
		if(StringUtils.isEmpty(value)) {
			return null;
		}

		if(StringUtils.isNumeric(value)) {
		    return values()[Integer.parseInt(value)];
        } else {
            return valueOf(value);
        }
	}
}
