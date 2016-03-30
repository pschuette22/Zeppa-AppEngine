package com.zeppamobile.api.datamodel;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.json.simple.JSONObject;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.datanucleus.annotations.Unowned;

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
	
	@Persistent // Event holds a tag attendee follows
	private Boolean isRecommended;

	
	/*
	 * The following is for creating a 'join' relationship
	 * These values map back to the 
	 */
	
	@Persistent(defaultFetchGroup="true")
	@Unowned
	private ZeppaEvent event;
	
	@Persistent(defaultFetchGroup="false")
	@Unowned
	private ZeppaUser attendee;
	
	
	
	/**
	 * Rebuild this object from JSON object
	 * @param json - object as JSON
	 * @throws JSONException throws exception if there is an error converting
	 */
	public ZeppaEventToUserRelationship(JSONObject json) {
		this.key = (Key) json.get("key");
		this.created = (Long) json.get("created");
		this.updated = (Long) json.get("updated");
		this.eventId = (Long) json.get("eventId");
		this.eventHostId = (Long) json.get("eventHostId");
		this.userId = (Long) json.get("userId");
		this.expires = (Long) json.get("expires");
		this.isWatching = (Boolean) json.get("isWatching");
		this.isAttending = (Boolean) json.get("isAttending");
		this.wasInvited = (Boolean) json.get("wasInvited");
		try {
			this.invitedByUserId = (Long) json.get("invitedByUserId");
		} catch (Exception e){
			invitedByUserId = Long.valueOf(-1);
		}
		this.isRecommended = (Boolean) json.get("isRecommended");
	}
	
	/**
	 * Constructor for instantiating a relationship on the backend
	 * @param event
	 * @param userId
	 * @param wasInvited
	 * @param isRecommended
	 * @param invitedByUserId
	 */
	public ZeppaEventToUserRelationship(ZeppaEvent event, Long userId, Boolean wasInvited, Boolean isRecommended, Long invitedByUserId){
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

	public ZeppaEvent getEvent() {
		return event;
	}

	public void setEvent(ZeppaEvent event) {
		this.event = event;
	}

	public ZeppaUser getAttendee() {
		return attendee;
	}

	public void setAttendee(ZeppaUser attendee) {
		this.attendee = attendee;
	}
	
}
