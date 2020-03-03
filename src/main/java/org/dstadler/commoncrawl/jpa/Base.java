package org.dstadler.commoncrawl.jpa;

import java.util.Date;

import javax.persistence.Basic;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Base class for all JPA objects with some
 * base functionality common to all database objects
 */
@SuppressWarnings("unused")
public class Base {
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}


    @Basic
    private Date created = new Date();


	/**
	 * @return the created date of this database item
	 */
	public Date getCreated() {
		return created;
	}


	/**
	 * @param created the created date of this database item to set
	 */
	public void setCreated(Date created) {
		this.created = created;
	}
}
