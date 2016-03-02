package com.zeppamobile.api.datamodel;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class VendorEventRelationship {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;
	
	@Persistent
	private Long userId;
	
	@Persistent
	private Long eventId;
	
	@Persistent
	private boolean joined;
	
	@Persistent
	private boolean seen;
	
	@Persistent
	private boolean watched;
	
	@Persistent
	private boolean shared;
	
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public Long getEventId() {
		return eventId;
	}
	public void setEventId(Long eventId) {
		// To get dependency in UML
		VendorEvent ve;
		ZeppaUser u;
		this.eventId = eventId;
	}
	public boolean isJoined() {
		return joined;
	}
	public void setJoined(boolean joined) {
		this.joined = joined;
	}
	public boolean isSeen() {
		return seen;
	}
	public void setSeen(boolean seen) {
		this.seen = seen;
	}
	public boolean isWatched() {
		return watched;
	}
	public void setWatched(boolean watched) {
		this.watched = watched;
	}
	public boolean isShared() {
		return shared;
	}
	public void setShared(boolean shared) {
		this.shared = shared;
	}
	
	
}
