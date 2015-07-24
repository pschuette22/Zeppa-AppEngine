package com.minook.zeppa;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class ZeppaEventToUserRelationship {

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
	private Long eventHostId;

	@Persistent
	private Long userId;

	@Persistent
	private Long expires; // when the event ends

	@Persistent
	private Boolean isWatching;

	@Persistent
	private Boolean isAttending;

	@Persistent
	private Boolean wasInvited;

	@Persistent
	private Long invitedByUserId;
	
	@Persistent // Event holds a tag user follows
	private Boolean isRecommended;


	// For Guice
	public ZeppaEventToUserRelationship() {

	}
	
	public void init(ZeppaEvent event, Long userId, Boolean wasInvited, Boolean isRecommended, Long invitedByUserId){
		this.created = System.currentTimeMillis();
		this.updated = System.currentTimeMillis();
		this.eventId = event.getId();
		this.eventHostId = event.getHostId();
		this.userId = userId;
		this.expires = event.getEnd();
		this.isWatching = Boolean.FALSE;
		this.isAttending = Boolean.FALSE;
		this.wasInvited = wasInvited;
		this.isRecommended = isRecommended;
		this.invitedByUserId = invitedByUserId;
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

	public Long getExpires() {
		return expires;
	}

	public void setExpires(Long expires) {
		this.expires = expires;
	}

	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}

	public Boolean getIsWatching() {
		return isWatching;
	}

	public void setIsWatching(Boolean isWatching) {
		this.isWatching = isWatching;
	}

	public Boolean getIsAttending() {
		return isAttending;
	}

	public void setIsAttending(Boolean isAttending) {
		this.isAttending = isAttending;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Boolean getWasInvited() {
		return wasInvited;
	}

	public void setWasInvited(Boolean wasInvited) {
		this.wasInvited = wasInvited;
	}

	public Long getInvitedByUserId() {
		return invitedByUserId;
	}

	public void setInvitedByUserId(Long invitedByUserId) {
		this.invitedByUserId = invitedByUserId;
	}

	public Boolean getIsRecommended() {
		return isRecommended;
	}

	public void setIsRecommended(Boolean isRecommended) {
		this.isRecommended = isRecommended;
	}

	public Long getEventHostId() {
		return eventHostId;
	}

	public void setEventHostId(Long eventHostId) {
		this.eventHostId = eventHostId;
	}
	
	
	
	

}
