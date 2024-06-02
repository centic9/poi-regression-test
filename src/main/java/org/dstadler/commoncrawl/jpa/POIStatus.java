package org.dstadler.commoncrawl.jpa;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.StringUtils;
import org.dstadler.commoncrawl.ResultItem;

import com.google.common.base.Preconditions;

/**
 * POJO for the JPA table POISTATUS which holds one row for each file that
 * was processed at some time.
 *
 * We currently add a set of columns per version that is tested so that we
 * can easily query for changes across versions.
 *
 * A sub-table with version and state would probably cause very costly queries
 * making building the report a very long-running operation.
 *
 * Manually created indices.
 *
 *
 * CREATE INDEX POI315VM_INDEX ON POISTATUS ( POI315VM );
 */
@SuppressWarnings({"FieldCanBeLocal", "unused"})
@JsonPropertyOrder({ "filename","exceptionStacktrace313","exceptionStacktrace314beta1","exceptionStacktrace314beta2",
		"exceptionText313","exceptionText314beta1","exceptionText314beta2","poi313","poi314beta1","poi314beta2",
		"exceptionStacktrace315beta1","exceptionText315beta1","poi315beta1","exceptionStacktrace315beta2",
		"exceptionText315beta2","poi315beta2","exceptionStacktrace315beta3","exceptionText315beta3","poi315beta3",
		"exceptionStacktrace316beta1","exceptionText316beta1","poi316beta1","exceptionStacktrace315RC2",
		"exceptionText315RC2","poi315RC2","exceptionStacktrace315VM","exceptionText315VM","poi315VM",
		"exceptionStacktrace316beta2","exceptionText316beta2","poi316beta2","exceptionStacktrace316beta3",
		"exceptionText316beta3","poi316beta3","exceptionStacktrace317beta1","exceptionText317beta1","poi317beta1",
		"exceptionStacktrace317","exceptionText317","poi317",
		"exceptionStacktrace400SNAPSHOT","exceptionText400SNAPSHOT","poi400SNAPSHOT",
		"exceptionStacktrace400RC1","exceptionText400RC1","poi400RC1",
		"exceptionStacktrace401RC1","exceptionText401RC1","poi401RC1",
		"exceptionStacktrace401RC2","exceptionText401RC2","poi401RC2",
		"exceptionStacktrace402SNAPSHOT","exceptionText402SNAPSHOT","poi402SNAPSHOT",
		"exceptionStacktrace410SNAPSHOT","exceptionText410SNAPSHOT","poi410SNAPSHOT",
		"exceptionStacktrace410RC1","exceptionText410RC1","poi410RC1",
		"exceptionStacktrace410RC2","exceptionText410RC2","poi410RC2",
		"exceptionStacktrace411RC1","exceptionText411RC1","poi411RC1",
		"exceptionStacktrace411RC2","exceptionText411RC2","poi411RC2",
		"exceptionStacktrace412RC1","exceptionText412RC1","poi412RC1",
		"exceptionStacktrace412RC2","exceptionText412RC2","poi412RC2",
		"exceptionStacktrace412RC3","exceptionText412RC3","poi412RC3",
		"exceptionStacktrace413RC0","exceptionText413RC0","poi413RC0",
		"exceptionStacktrace500RC1","exceptionText500RC1","poi500RC1",
		"exceptionStacktrace500RC2","exceptionText500RC2","poi500RC2",
		"exceptionStacktrace510RC1","exceptionText510RC1","poi510RC1",
		"exceptionStacktrace510RC2","exceptionText510RC2","poi510RC2",
		"exceptionStacktrace520RC1","exceptionText520RC1","poi520RC1",
		"exceptionStacktrace523RC1","exceptionText523RC1","poi523RC1",
		"exceptionStacktrace524RC1","exceptionText524RC1","poi524RC1",
		"exceptionStacktrace525RC1","exceptionText525RC1","poi525RC1",
		"exceptionStacktrace530RC1","exceptionText530RC1","poi530RC1" })
@Entity
public class POIStatus extends Base {
	@JsonProperty
	@Id
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String filename;

	@JsonProperty
	@Basic
	private FileStatus poi313;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionText313;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionStacktrace313;

	@JsonProperty
	@Basic
	private FileStatus poi314beta1;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionText314beta1;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionStacktrace314beta1;

	@JsonProperty
	@Basic
	private FileStatus poi314beta2;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionText314beta2;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionStacktrace314beta2;

	@JsonProperty
	@Basic
	private FileStatus poi315beta1;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionText315beta1;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionStacktrace315beta1;

	@JsonProperty
	@Basic
	private FileStatus poi315beta2;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionText315beta2;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionStacktrace315beta2;

	@JsonProperty
	@Basic
	private FileStatus poi315beta3;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionText315beta3;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionStacktrace315beta3;

	@JsonProperty
	@Basic
	private FileStatus poi315RC2;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionText315RC2;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionStacktrace315RC2;

	@JsonProperty
	@Basic
	private FileStatus poi315VM;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionText315VM;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionStacktrace315VM;

	@JsonProperty
	@Basic
	private FileStatus poi316beta1;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionText316beta1;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionStacktrace316beta1;

	@JsonProperty
	@Basic
	private FileStatus poi316beta2;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionText316beta2;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionStacktrace316beta2;

	@JsonProperty
	@Basic
	private FileStatus poi316beta3;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionText316beta3;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionStacktrace316beta3;

	@JsonProperty
	@Basic
	private FileStatus poi317beta1;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionText317beta1;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionStacktrace317beta1;

	@JsonProperty
	@Basic
	private FileStatus poi317;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionText317;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionStacktrace317;

	@JsonProperty
	@Basic
	private FileStatus poi400SNAPSHOT;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionText400SNAPSHOT;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionStacktrace400SNAPSHOT;

	@JsonProperty
	@Basic
	private FileStatus poi400RC1;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionText400RC1;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionStacktrace400RC1;

	@JsonProperty
	@Basic
	private FileStatus poi401RC1;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionText401RC1;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionStacktrace401RC1;

	@JsonProperty
	@Basic
	private FileStatus poi401RC2;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionText401RC2;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionStacktrace401RC2;

	@JsonProperty
	@Basic
	private FileStatus poi402SNAPSHOT;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionText402SNAPSHOT;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionStacktrace402SNAPSHOT;

	@JsonProperty
	@Basic
	private FileStatus poi410SNAPSHOT;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionText410SNAPSHOT;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionStacktrace410SNAPSHOT;

	@JsonProperty
	@Basic
	private long duration410SNAPSHOT;

	@JsonProperty
	@Basic
	private FileStatus poi410RC1;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionText410RC1;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionStacktrace410RC1;

	@JsonProperty
	@Basic
	private long duration410RC1;

	@JsonProperty
	@Basic
	private FileStatus poi410RC2;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionText410RC2;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionStacktrace410RC2;

	@JsonProperty
	@Basic
	private long duration410RC2;

	@JsonProperty
	@Basic
	private FileStatus poi411RC1;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionText411RC1;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionStacktrace411RC1;

	@JsonProperty
	@Basic
	private long duration411RC1;

	@JsonProperty
	@Basic
	private FileStatus poi411RC2;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionText411RC2;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionStacktrace411RC2;

	@JsonProperty
	@Basic
	private long duration411RC2;

	@JsonProperty
	@Basic
	private FileStatus poi412RC1;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionText412RC1;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionStacktrace412RC1;

	@JsonProperty
	@Basic
	private long duration412RC1;

	@JsonProperty
	@Basic
	private FileStatus poi412RC2;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionText412RC2;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionStacktrace412RC2;

	@JsonProperty
	@Basic
	private long duration412RC2;

	@JsonProperty
	@Basic
	private FileStatus poi412RC3;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionText412RC3;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionStacktrace412RC3;

	@JsonProperty
	@Basic
	private long duration412RC3;

	@JsonProperty
	@Basic
	private FileStatus poi413RC0;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionText413RC0;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionStacktrace413RC0;

	@JsonProperty
	@Basic
	private long duration413RC0;

	@JsonProperty
	@Basic
	private FileStatus poi500RC1;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionText500RC1;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionStacktrace500RC1;

	@JsonProperty
	@Basic
	private long duration500RC1;

	@JsonProperty
	@Basic
	private FileStatus poi500RC2;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionText500RC2;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionStacktrace500RC2;

	@JsonProperty
	@Basic
	private long duration500RC2;

	@JsonProperty
	@Basic
	private FileStatus poi510RC1;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionText510RC1;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionStacktrace510RC1;

	@JsonProperty
	@Basic
	private long duration510RC1;

	@JsonProperty
	@Basic
	private FileStatus poi510RC2;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionText510RC2;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionStacktrace510RC2;

	@JsonProperty
	@Basic
	private long duration510RC2;

	@JsonProperty
	@Basic
	private FileStatus poi520RC1;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionText520RC1;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionStacktrace520RC1;

	@JsonProperty
	@Basic
	private long duration520RC1;

	@JsonProperty
	@Basic
	private FileStatus poi523RC1;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionText523RC1;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionStacktrace523RC1;

	@JsonProperty
	@Basic
	private long duration523RC1;

	@JsonProperty
	@Basic
	private FileStatus poi524RC1;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionText524RC1;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionStacktrace524RC1;

	@JsonProperty
	@Basic
	private long duration524RC1;

	@JsonProperty
	@Basic
	private FileStatus poi525RC1;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionText525RC1;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionStacktrace525RC1;

	@JsonProperty
	@Basic
	private long duration525RC1;

	@JsonProperty
	@Basic
	private FileStatus poi530RC1;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionText530RC1;

	@JsonProperty
	@Basic
	@Column(length = FileURL.URL_MAX_LENGTH)
	private String exceptionStacktrace530RC1;

	@JsonProperty
	@Basic
	private long duration530RC1;

	public POIStatus() {
		super();
	}

	public POIStatus(String filename) {
		super();
		Preconditions.checkArgument(filename.length() <= FileURL.URL_MAX_LENGTH, "Had length: " + filename.length());
		this.filename = filename;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		Preconditions.checkArgument(filename.length() <= FileURL.URL_MAX_LENGTH, "Had length: " + filename.length());
		this.filename = filename;
	}

	public FileStatus getPoi313() {
		return poi313;
	}

	public void setPoi313(FileStatus poi313) {
		this.poi313 = poi313;
	}

	public FileStatus getPoi314beta1() {
		return poi314beta1;
	}

	public void setPoi314beta1(FileStatus poi314beta1) {
		this.poi314beta1 = poi314beta1;
	}

	public FileStatus getPoi314beta2() {
		return poi314beta2;
	}

	public void setPoi314beta2(FileStatus poi314beta2) {
		this.poi314beta2 = poi314beta2;
	}

	public FileStatus getPoi315beta1() {
		return poi315beta1;
	}

	public void setPoi315beta1(FileStatus poi315beta1) {
		this.poi315beta1 = poi315beta1;
	}

	public FileStatus getPoi315beta2() {
		return poi315beta2;
	}

	public void setPoi315beta2(FileStatus poi315beta2) {
		this.poi315beta2 = poi315beta2;
	}

	public FileStatus getPoi315beta3() {
		return poi315beta3;
	}

	public void setPoi315beta3(FileStatus poi315beta3) {
		this.poi315beta3 = poi315beta3;
	}

	public FileStatus getPoi315RC2() {
		return poi315RC2;
	}

	public void setPoi315RC2(FileStatus poi315RC2) {
		this.poi315RC2 = poi315RC2;
	}

	public FileStatus getPoi315VM() {
		return poi315VM;
	}

	public void setPoi315VM(FileStatus poi315VM) {
		this.poi315VM = poi315VM;
	}

	public FileStatus getPoi316beta1() {
		return poi316beta1;
	}

	public void setPoi316beta1(FileStatus poi316beta1) {
		this.poi316beta1 = poi316beta1;
	}

	public FileStatus getPoi316beta2() {
		return poi316beta2;
	}

	public void setPoi316beta2(FileStatus poi316beta2) {
		this.poi316beta2 = poi316beta2;
	}

	public FileStatus getPoi316beta3() {
		return poi316beta3;
	}

	public void setPoi316beta3(FileStatus poi316beta3) {
		this.poi316beta3 = poi316beta3;
	}

	public FileStatus getPoi317beta1() {
		return poi317beta1;
	}

	public void setPoi317beta1(FileStatus poi317beta1) {
		this.poi317beta1 = poi317beta1;
	}

	public FileStatus getPoi317() {
		return poi317;
	}

	public void setPoi317(FileStatus poi317) {
		this.poi317 = poi317;
	}

	public FileStatus getPoi400SNAPSHOT() {
		return poi400SNAPSHOT;
	}

	public void setPoi400SNAPSHOT(FileStatus poi400SNAPSHOT) {
		this.poi400SNAPSHOT = poi400SNAPSHOT;
	}

	public FileStatus getPoi400RC1() {
		return poi400RC1;
	}

	public void setPoi400RC1(FileStatus poi400RC1) {
		this.poi400RC1 = poi400RC1;
	}

	public FileStatus getPoi401RC1() {
		return poi401RC1;
	}

	public void setPoi401RC1(FileStatus poi401RC1) {
		this.poi401RC1 = poi401RC1;
	}

	public FileStatus getPoi401RC2() {
		return poi401RC2;
	}

	public void setPoi401RC2(FileStatus poi401RC2) {
		this.poi401RC2 = poi401RC2;
	}

	public FileStatus getPoi402SNAPSHOT() {
		return poi402SNAPSHOT;
	}

	public void setPoi402SNAPSHOT(FileStatus poi402SNAPSHOT) {
		this.poi402SNAPSHOT = poi402SNAPSHOT;
	}

	public FileStatus getPoi410SNAPSHOT() {
		return poi410SNAPSHOT;
	}

	public void setPoi410SNAPSHOT(FileStatus poi410SNAPSHOT) {
		this.poi410SNAPSHOT = poi410SNAPSHOT;
	}

	public FileStatus getPoi410RC1() {
		return poi410RC1;
	}

	public void setPoi410RC1(FileStatus poi410RC1) {
		this.poi410RC1 = poi410RC1;
	}

	public FileStatus getPoi410RC2() {
		return poi410RC2;
	}

	public void setPoi410RC2(FileStatus poi410RC2) {
		this.poi410RC2 = poi410RC2;
	}

	public FileStatus getPoi411RC1() {
		return poi411RC1;
	}

	public void setPoi411RC1(FileStatus poi411RC1) {
		this.poi411RC1 = poi411RC1;
	}

	public FileStatus getPoi411RC2() {
		return poi411RC2;
	}

	public void setPoi411RC2(FileStatus poi411RC2) {
		this.poi411RC2 = poi411RC2;
	}

	public FileStatus getPoi412RC1() {
		return poi412RC1;
	}

	public void setPoi412RC1(FileStatus poi412RC1) {
		this.poi412RC1 = poi412RC1;
	}

	public FileStatus getPoi412RC2() {
		return poi412RC2;
	}

	public void setPoi412RC2(FileStatus poi412RC2) {
		this.poi412RC2 = poi412RC2;
	}

	public FileStatus getPoi412RC3() {
		return poi412RC3;
	}

	public void setPoi412RC3(FileStatus poi412RC3) {
		this.poi412RC3 = poi412RC3;
	}

	public FileStatus getPoi413RC0() {
		return poi413RC0;
	}

	public void setPoi413RC0(FileStatus poi413RC0) {
		this.poi413RC0 = poi413RC0;
	}

	public FileStatus getPoi500RC1() {
		return poi500RC1;
	}

	public void setPoi500RC1(FileStatus poi500RC1) {
		this.poi500RC1 = poi500RC1;
	}

	public FileStatus getPoi500RC2() {
		return poi500RC2;
	}

	public void setPoi500RC2(FileStatus poi500RC2) {
		this.poi500RC2 = poi500RC2;
	}

	public FileStatus getPoi510RC1() {
		return poi510RC1;
	}

	public void setPoi510RC1(FileStatus poi510RC1) {
		this.poi510RC1 = poi510RC1;
	}

	public FileStatus getPoi510RC2() {
		return poi510RC2;
	}

	public void setPoi510RC2(FileStatus poi510RC2) {
		this.poi510RC2 = poi510RC2;
	}

	public FileStatus getPoi520RC1() {
		return poi520RC1;
	}

	public void setPoi520RC1(FileStatus poi520RC1) {
		this.poi520RC1 = poi520RC1;
	}

	public FileStatus getPoi523RC1() {
		return poi523RC1;
	}

	public void setPoi523RC1(FileStatus poi523RC1) {
		this.poi523RC1 = poi523RC1;
	}

	public FileStatus getPoi524RC1() {
		return poi524RC1;
	}

	public void setPoi524RC1(FileStatus poi524RC1) {
		this.poi524RC1 = poi524RC1;
	}

	public FileStatus getPoi525RC1() {
		return poi525RC1;
	}

	public void setPoi525RC1(FileStatus poi525RC1) {
		this.poi525RC1 = poi525RC1;
	}

	public FileStatus getPoi530RC1() {
		return poi530RC1;
	}

	public void setPoi530RC1(FileStatus poi530RC1) {
		this.poi530RC1 = poi530RC1;
	}

	public void setByVersion(String version, ResultItem item) {
		final FileStatus status;
		if(item.isTimeout() ||
                (item.getExceptionStacktrace() != null &&
						(item.getExceptionStacktrace().contains("java.lang.ThreadDeath") ||
						item.getExceptionStacktrace().contains("java.nio.channels.ClosedByInterruptException")))) {
			status = FileStatus.TIMEOUT;
		} else if (item.getExceptionText() == null) {
			status = FileStatus.OK;
		} else if (matchesInvalidStrings(item.getExceptionText())) {
			// Many files are not actual POI-known files even if their mime-type or extension indicates it
			// Instead of listing all these as "ERROR", we use "INVALID" to not look at these as "ERRORS"
			status = FileStatus.INVALID;
		} else if (item.getExceptionStacktrace() != null && item.getExceptionStacktrace().contains("java.lang.OutOfMemoryError")) {
			status = FileStatus.OOM;
		} else if (item.getExceptionText().contains("The supplied file was empty (zero bytes long)")) {
			status = FileStatus.ZEROBYTES;
		} else if (matchesOldFormatStrings(item.getExceptionText())) {
			// some files are with an older format which Apache POI does not support
			status = FileStatus.OLDFORMAT;
		} else {
			status = FileStatus.ERROR;
		}

		if(version.contains("3.13")) {
			this.poi313 = status;
			exceptionText313 = StringUtils.abbreviate(item.getExceptionText(), FileURL.URL_MAX_LENGTH);
			exceptionStacktrace313 = StringUtils.abbreviate(item.getExceptionStacktrace(), FileURL.URL_MAX_LENGTH);
		} else if (version.contains("3.14-beta1")) {
			this.poi314beta1 = status;
			exceptionText314beta1 = StringUtils.abbreviate(item.getExceptionText(), FileURL.URL_MAX_LENGTH);
			exceptionStacktrace314beta1 = StringUtils.abbreviate(item.getExceptionStacktrace(), FileURL.URL_MAX_LENGTH);
		} else if (version.contains("3.14-beta2")) {
			this.poi314beta2 = status;
			exceptionText314beta2 = StringUtils.abbreviate(item.getExceptionText(), FileURL.URL_MAX_LENGTH);
			exceptionStacktrace314beta2 = StringUtils.abbreviate(item.getExceptionStacktrace(), FileURL.URL_MAX_LENGTH);
		} else if (version.contains("3.15-beta1")) {
			this.poi315beta1 = status;
			exceptionText315beta1 = StringUtils.abbreviate(item.getExceptionText(), FileURL.URL_MAX_LENGTH);
			exceptionStacktrace315beta1 = StringUtils.abbreviate(item.getExceptionStacktrace(), FileURL.URL_MAX_LENGTH);
		} else if (version.contains("3.15-beta2")) {
			this.poi315beta2 = status;
			exceptionText315beta2 = StringUtils.abbreviate(item.getExceptionText(), FileURL.URL_MAX_LENGTH);
			exceptionStacktrace315beta2 = StringUtils.abbreviate(item.getExceptionStacktrace(), FileURL.URL_MAX_LENGTH);
		} else if (version.contains("3.15-beta3")) {
			this.poi315beta3 = status;
			exceptionText315beta3 = StringUtils.abbreviate(item.getExceptionText(), FileURL.URL_MAX_LENGTH);
			exceptionStacktrace315beta3 = StringUtils.abbreviate(item.getExceptionStacktrace(), FileURL.URL_MAX_LENGTH);
		} else if (version.contains("3.15-RC2")) {
			this.poi315RC2 = status;
			exceptionText315RC2 = StringUtils.abbreviate(item.getExceptionText(), FileURL.URL_MAX_LENGTH);
			exceptionStacktrace315RC2 = StringUtils.abbreviate(item.getExceptionStacktrace(), FileURL.URL_MAX_LENGTH);
		} else if (version.contains("vm-3.15")) {
			this.poi315VM = status;
			exceptionText315VM = StringUtils.abbreviate(item.getExceptionText(), FileURL.URL_MAX_LENGTH);
			exceptionStacktrace315VM = StringUtils.abbreviate(item.getExceptionStacktrace(), FileURL.URL_MAX_LENGTH);
		} else if (version.contains("3.16-beta1")) {
			this.poi316beta1 = status;
			exceptionText316beta1 = StringUtils.abbreviate(item.getExceptionText(), FileURL.URL_MAX_LENGTH);
			exceptionStacktrace316beta1 = StringUtils.abbreviate(item.getExceptionStacktrace(), FileURL.URL_MAX_LENGTH);
		} else if (version.contains("3.16-beta2")) {
			this.poi316beta2 = status;
			exceptionText316beta2 = StringUtils.abbreviate(item.getExceptionText(), FileURL.URL_MAX_LENGTH);
			exceptionStacktrace316beta2 = StringUtils.abbreviate(item.getExceptionStacktrace(), FileURL.URL_MAX_LENGTH);
		} else if (version.contains("3.16-beta3")) {
			this.poi316beta3 = status;
			exceptionText316beta3 = StringUtils.abbreviate(item.getExceptionText(), FileURL.URL_MAX_LENGTH);
			exceptionStacktrace316beta3 = StringUtils.abbreviate(item.getExceptionStacktrace(), FileURL.URL_MAX_LENGTH);
		} else if (version.contains("3.17-beta1")) {
			this.poi317beta1 = status;
			exceptionText317beta1 = StringUtils.abbreviate(item.getExceptionText(), FileURL.URL_MAX_LENGTH);
			exceptionStacktrace317beta1 = StringUtils.abbreviate(item.getExceptionStacktrace(), FileURL.URL_MAX_LENGTH);
		} else if (version.contains("3.17")) {
			this.poi317 = status;
			exceptionText317 = StringUtils.abbreviate(item.getExceptionText(), FileURL.URL_MAX_LENGTH);
			exceptionStacktrace317 = StringUtils.abbreviate(item.getExceptionStacktrace(), FileURL.URL_MAX_LENGTH);
		} else if (version.contains("4.0.0-SNAPSHOT")) {
			this.poi400SNAPSHOT = status;
			exceptionText400SNAPSHOT = StringUtils.abbreviate(item.getExceptionText(), FileURL.URL_MAX_LENGTH);
			exceptionStacktrace400SNAPSHOT = StringUtils.abbreviate(item.getExceptionStacktrace(), FileURL.URL_MAX_LENGTH);
		} else if (version.contains("4.0.0-RC1")) {
			this.poi400RC1 = status;
			exceptionText400RC1 = StringUtils.abbreviate(item.getExceptionText(), FileURL.URL_MAX_LENGTH);
			exceptionStacktrace400RC1 = StringUtils.abbreviate(item.getExceptionStacktrace(), FileURL.URL_MAX_LENGTH);
		} else if (version.contains("4.0.1-RC1")) {
			this.poi401RC1 = status;
			exceptionText401RC1 = StringUtils.abbreviate(item.getExceptionText(), FileURL.URL_MAX_LENGTH);
			exceptionStacktrace401RC1 = StringUtils.abbreviate(item.getExceptionStacktrace(), FileURL.URL_MAX_LENGTH);
		} else if (version.contains("4.0.1-RC2")) {
			this.poi401RC2 = status;
			exceptionText401RC2 = StringUtils.abbreviate(item.getExceptionText(), FileURL.URL_MAX_LENGTH);
			exceptionStacktrace401RC2 = StringUtils.abbreviate(item.getExceptionStacktrace(), FileURL.URL_MAX_LENGTH);
		} else if (version.contains("4.0.2-SNAPSHOT")) {
			this.poi402SNAPSHOT = status;
			exceptionText402SNAPSHOT = StringUtils.abbreviate(item.getExceptionText(), FileURL.URL_MAX_LENGTH);
			exceptionStacktrace402SNAPSHOT = StringUtils.abbreviate(item.getExceptionStacktrace(), FileURL.URL_MAX_LENGTH);
		} else if (version.contains("4.1.0-SNAPSHOT")) {
			this.poi410SNAPSHOT = status;
			exceptionText410SNAPSHOT = StringUtils.abbreviate(item.getExceptionText(), FileURL.URL_MAX_LENGTH);
			exceptionStacktrace410SNAPSHOT = StringUtils.abbreviate(item.getExceptionStacktrace(), FileURL.URL_MAX_LENGTH);
			duration410SNAPSHOT = item.getDuration();
		} else if (version.contains("4.1.0-RC1")) {
			this.poi410RC1 = status;
			exceptionText410RC1 = StringUtils.abbreviate(item.getExceptionText(), FileURL.URL_MAX_LENGTH);
			exceptionStacktrace410RC1 = StringUtils.abbreviate(item.getExceptionStacktrace(), FileURL.URL_MAX_LENGTH);
			duration410RC1 = item.getDuration();
		} else if (version.contains("4.1.0-RC2")) {
			this.poi410RC2 = status;
			exceptionText410RC2 = StringUtils.abbreviate(item.getExceptionText(), FileURL.URL_MAX_LENGTH);
			exceptionStacktrace410RC2 = StringUtils.abbreviate(item.getExceptionStacktrace(), FileURL.URL_MAX_LENGTH);
			duration410RC2 = item.getDuration();
		} else if (version.contains("4.1.1-RC1")) {
			this.poi411RC1 = status;
			exceptionText411RC1 = StringUtils.abbreviate(item.getExceptionText(), FileURL.URL_MAX_LENGTH);
			exceptionStacktrace411RC1 = StringUtils.abbreviate(item.getExceptionStacktrace(), FileURL.URL_MAX_LENGTH);
			duration411RC1 = item.getDuration();
		} else if (version.contains("4.1.1-RC2")) {
			this.poi411RC2 = status;
			exceptionText411RC2 = StringUtils.abbreviate(item.getExceptionText(), FileURL.URL_MAX_LENGTH);
			exceptionStacktrace411RC2 = StringUtils.abbreviate(item.getExceptionStacktrace(), FileURL.URL_MAX_LENGTH);
			duration411RC2 = item.getDuration();
		} else if (version.contains("4.1.2-RC1")) {
			this.poi412RC1 = status;
			exceptionText412RC1 = StringUtils.abbreviate(item.getExceptionText(), FileURL.URL_MAX_LENGTH);
			exceptionStacktrace412RC1 = StringUtils.abbreviate(item.getExceptionStacktrace(), FileURL.URL_MAX_LENGTH);
			duration412RC1 = item.getDuration();
		} else if (version.contains("4.1.2-RC2")) {
			this.poi412RC2 = status;
			exceptionText412RC2 = StringUtils.abbreviate(item.getExceptionText(), FileURL.URL_MAX_LENGTH);
			exceptionStacktrace412RC2 = StringUtils.abbreviate(item.getExceptionStacktrace(), FileURL.URL_MAX_LENGTH);
			duration412RC2 = item.getDuration();
		} else if (version.contains("4.1.2-RC3")) {
			this.poi412RC3 = status;
			exceptionText412RC3 = StringUtils.abbreviate(item.getExceptionText(), FileURL.URL_MAX_LENGTH);
			exceptionStacktrace412RC3 = StringUtils.abbreviate(item.getExceptionStacktrace(), FileURL.URL_MAX_LENGTH);
			duration412RC3 = item.getDuration();
		} else if (version.contains("4.1.3-RC0")) {
			this.poi413RC0 = status;
			exceptionText413RC0 = StringUtils.abbreviate(item.getExceptionText(), FileURL.URL_MAX_LENGTH);
			exceptionStacktrace413RC0 = StringUtils.abbreviate(item.getExceptionStacktrace(), FileURL.URL_MAX_LENGTH);
			duration413RC0 = item.getDuration();
		} else if (version.contains("5.0.0-RC1")) {
			this.poi500RC1 = status;
			exceptionText500RC1 = StringUtils.abbreviate(item.getExceptionText(), FileURL.URL_MAX_LENGTH);
			exceptionStacktrace500RC1 = StringUtils.abbreviate(item.getExceptionStacktrace(), FileURL.URL_MAX_LENGTH);
			duration500RC1 = item.getDuration();
		} else if (version.contains("5.0.0-RC2")) {
			this.poi500RC2 = status;
			exceptionText500RC2 = StringUtils.abbreviate(item.getExceptionText(), FileURL.URL_MAX_LENGTH);
			exceptionStacktrace500RC2 = StringUtils.abbreviate(item.getExceptionStacktrace(), FileURL.URL_MAX_LENGTH);
			duration500RC2 = item.getDuration();
		} else if (version.contains("5.1.0-RC1")) {
			this.poi510RC1 = status;
			exceptionText510RC1 = StringUtils.abbreviate(item.getExceptionText(), FileURL.URL_MAX_LENGTH);
			exceptionStacktrace510RC1 = StringUtils.abbreviate(item.getExceptionStacktrace(), FileURL.URL_MAX_LENGTH);
			duration510RC1 = item.getDuration();
		} else if (version.contains("5.1.0-RC2")) {
			this.poi510RC2 = status;
			exceptionText510RC2 = StringUtils.abbreviate(item.getExceptionText(), FileURL.URL_MAX_LENGTH);
			exceptionStacktrace510RC2 = StringUtils.abbreviate(item.getExceptionStacktrace(), FileURL.URL_MAX_LENGTH);
			duration510RC2 = item.getDuration();
		} else if (version.contains("5.2.0-RC1")) {
			this.poi520RC1 = status;
			exceptionText520RC1 = StringUtils.abbreviate(item.getExceptionText(), FileURL.URL_MAX_LENGTH);
			exceptionStacktrace520RC1 = StringUtils.abbreviate(item.getExceptionStacktrace(), FileURL.URL_MAX_LENGTH);
			duration520RC1 = item.getDuration();
		} else if (version.contains("5.2.3-RC1")) {
			this.poi523RC1 = status;
			exceptionText523RC1 = StringUtils.abbreviate(item.getExceptionText(), FileURL.URL_MAX_LENGTH);
			exceptionStacktrace523RC1 = StringUtils.abbreviate(item.getExceptionStacktrace(), FileURL.URL_MAX_LENGTH);
			duration523RC1 = item.getDuration();
		} else if (version.contains("5.2.4-RC1")) {
			this.poi524RC1 = status;
			exceptionText524RC1 = StringUtils.abbreviate(item.getExceptionText(), FileURL.URL_MAX_LENGTH);
			exceptionStacktrace524RC1 = StringUtils.abbreviate(item.getExceptionStacktrace(), FileURL.URL_MAX_LENGTH);
			duration524RC1 = item.getDuration();
		} else if (version.contains("5.2.5-RC1")) {
			this.poi525RC1 = status;
			exceptionText525RC1 = StringUtils.abbreviate(item.getExceptionText(), FileURL.URL_MAX_LENGTH);
			exceptionStacktrace525RC1 = StringUtils.abbreviate(item.getExceptionStacktrace(), FileURL.URL_MAX_LENGTH);
			duration525RC1 = item.getDuration();
		} else if (version.contains("5.3.0-RC1")) {
			this.poi530RC1 = status;
			exceptionText530RC1 = StringUtils.abbreviate(item.getExceptionText(), FileURL.URL_MAX_LENGTH);
			exceptionStacktrace530RC1 = StringUtils.abbreviate(item.getExceptionStacktrace(), FileURL.URL_MAX_LENGTH);
			duration530RC1 = item.getDuration();
		} else {
			throw new IllegalStateException("Unknown version found: " + version);
		}
	}

	private static final String[] INVALID_FILE_EXCEPTIONS = new String[] {
		"NotOLE2FileException: Invalid header signature; read ",
		"NotOfficeXmlFileException: No valid entries or contents found, this is not a valid OOXML (Office Open XML) file",
		"The supplied data appears to be a raw XML file. Formats such as Office 2003 XML are not supported",
		"excluded because it is actually a PDF/RTF file",
		"IllegalArgumentException: The document is really a ",
		" excluded because the Zip file is incomplete",
		"java.io.IOException: Truncated ZIP file",
	};

	private boolean matchesInvalidStrings(String exceptionText) {
		for(String str : INVALID_FILE_EXCEPTIONS) {
			if(exceptionText.contains(str)) {
				return true;
			}
		}

		return false;
	}

	private static final String[] OLD_FORMAT_EXCEPTIONS = new String[] {
		// old text for Assume
		"excluded because it is unsupported old Excel format",
		// newer text for Assume
		"excluded because it is an unsupported old format",
	};

	private boolean matchesOldFormatStrings(String exceptionText) {
		for(String str : OLD_FORMAT_EXCEPTIONS) {
			if(exceptionText.contains(str)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((filename == null) ? 0 : filename.hashCode());
		return result;
	}

	@SuppressWarnings("RedundantIfStatement")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		POIStatus other = (POIStatus) obj;
		if (filename == null) {
			if (other.filename != null)
				return false;
		} else if (!filename.equals(other.filename))
			return false;
		return true;
	}
}
