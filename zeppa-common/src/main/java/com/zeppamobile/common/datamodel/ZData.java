package com.zeppamobile.common.datamodel;

import java.util.Date;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * 
 * @author PSchuette22
 * 
 * Base data model for Zeppa Datastore objects
 *
 */
@Entity
public class ZData {

	/**
	 * Datastore identifier
	 */
	@Id
	public Long id;
	
	/**
	 * Date object was created
	 */
	@Index
	private Date created;
	
	/**
	 * Date object was updated
	 */
	private Date updated;
	
	/**
	 * Required constructor
	 */
	protected ZData(){
		this.created = new Date();
		this.updated = new Date();
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public Long getId() {
		return id;
	}

	public Date getCreated() {
		return created;
	}
	
	
	
	
}
