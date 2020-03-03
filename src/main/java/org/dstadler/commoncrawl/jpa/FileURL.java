package org.dstadler.commoncrawl.jpa;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.google.common.base.Preconditions;

@SuppressWarnings("unused")
@Entity
public class FileURL extends Base {
	public final static int URL_MAX_LENGTH = 8096;

	@Id
	@Column(length = URL_MAX_LENGTH)
	private String url;

	@Basic
	private String mime;

	@Basic
	private int status;

	@Basic
	private String digest;

	@Basic
	private long length;

	@Basic
	private long offset;

	@Basic
	@Column(length = URL_MAX_LENGTH)
	private String filename;

	public FileURL() {
		super();
	}

	public FileURL(String url, String mime, int status, String digest, long length, long offset, String filename) {
		super();

		Preconditions.checkArgument(url.length() <= URL_MAX_LENGTH, "Had length: " + url.length());
		Preconditions.checkArgument(filename.length() <= URL_MAX_LENGTH, "Had length: " + filename.length());

		this.url = url;
		this.mime = mime;
		this.status = status;
		this.digest = digest;
		this.length = length;
		this.offset = offset;
		this.filename = filename;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		Preconditions.checkArgument(url.length() <= URL_MAX_LENGTH, "Had length: " + url.length());
		this.url = url;
	}

	public String getMime() {
		return mime;
	}

	public void setMime(String mime) {
		this.mime = mime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getDigest() {
		return digest;
	}

	public void setDigest(String digest) {
		this.digest = digest;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	public long getOffset() {
		return offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		Preconditions.checkArgument(filename.length() <= URL_MAX_LENGTH, "Had length: " + filename.length());
		this.filename = filename;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((url == null) ? 0 : url.hashCode());
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
		FileURL other = (FileURL) obj;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		return true;
	}
}
