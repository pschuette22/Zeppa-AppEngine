package com.zeppamobile.common.datamodel;

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

	/*
	 * For maintaining relationships
	 */

	@Persistent
	private ZeppaEvent event;

	@Persistent
	private ZeppaUser commenter;
	
	
	public EventComment(ZeppaEvent event, ZeppaUser commenter, Long created,
			Long updated, String text) {
		super();
		this.event = event;
		this.commenter = commenter;
		this.eventId = event.getId();
		this.commenterId = commenter.getId();
		this.created = created;
		this.updated = updated;
		this.text = text;

	}

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

	public ZeppaEvent getEvent() {
		return event;
	}

	public void setEvent(ZeppaEvent event) {
		this.event = event;
	}

	public ZeppaUser getCommenter() {
		return commenter;
	}

	public void setCommenter(ZeppaUser commenter) {
		this.commenter = commenter;
	}

}
