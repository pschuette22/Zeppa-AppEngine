package com.zeppamobile.api.datamodel;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class EventTag {

	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;
	
	@Persistent
	private Long created;
	
	@Persistent
	private Long updated;
	
	@Persistent
	private Long userId;

	@Persistent
	private String tagText;

	// For Guice
	public EventTag(){}
	
//	public EventTag(Long userId, String tagText) {
//
//		this.created = System.currentTimeMillis();
//		this.updated = System.currentTimeMillis();
//		this.tagText = tagText;
//		this.userId = userId;
//	}

	public Long getCreated() {
		return created;
	}

	public void setCreated(Long created) {
		this.created = created;
	}

	public Long getUpdated() {
		return updated;
	}

	public void setUpdated(Long updated) {
		this.updated = updated;
	}
	
	public Key getKey() {
		return key;
	}

	public Long getId() {
		return key.getId();
	}
	
	public String getTagText() {
		return tagText;
	}

	public void setTagText(String tagText) {
		this.tagText = tagText;
	}

	public Long getOwnerId() {
		return userId;
	}

	public void setOwnerId(Long ownerId) {
		this.userId = ownerId;
	}

}
