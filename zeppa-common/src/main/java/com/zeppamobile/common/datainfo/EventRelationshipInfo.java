package com.zeppamobile.common.datainfo;

import java.io.Serializable;

public class EventRelationshipInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long relationshipId = -1;
	
	private long userId = -1;
	
	private long eventId = -1;
	
	private boolean isWatching = false;
	
	private boolean isAttending = false;
	
	private boolean isRecommended = false;
		
	private long inviterId = -1;

	public long getRelationshipId() {
		return relationshipId;
	}

	public void setRelationshipId(long relationshipId) {
		this.relationshipId = relationshipId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public long getEventId() {
		return eventId;
	}

	public void setEventId(long eventId) {
		this.eventId = eventId;
	}

	public boolean isWatching() {
		return isWatching;
	}

	public void setWatching(boolean isWatching) {
		this.isWatching = isWatching;
	}

	public boolean isAttending() {
		return isAttending;
	}

	public void setAttending(boolean isAttending) {
		this.isAttending = isAttending;
	}
	
	public boolean isRecommended() {
		return isRecommended;
	}

	public void setRecommended(boolean isRecommended) {
		this.isRecommended = isRecommended;
	}

	public long getInviterId() {
		return inviterId;
	}

	public void setInviterId(long inviterId) {
		this.inviterId = inviterId;
	}
	
	
}
