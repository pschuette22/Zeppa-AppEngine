package com.zeppamobile.api.datamodel;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class EventComment {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;
	
	@Persistent
	private Long created;
	
	@Persistent
	private Long updated;

	@Persistent
	private Long eventId;

	@Persistent
	private Long commenterId;

	@Persistent
	private String text;

	
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

	public Long getCommenterId() {
		return commenterId;
	}

	public void setCommenterId(Long commenterId) {
		this.commenterId = commenterId;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}
	
	

}
