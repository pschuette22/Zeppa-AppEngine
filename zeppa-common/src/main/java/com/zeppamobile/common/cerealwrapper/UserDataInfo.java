package com.zeppamobile.common.cerealwrapper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserDataInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private long identifier = -1;
	
	private boolean isNewUser = false;
	
	private List<Long> minglerIds = new ArrayList<Long>();
	
	private List<EventTagCerealWrapper> tags = new ArrayList<EventTagCerealWrapper>();
	
	private List<EventCerealWrapper> events = new ArrayList<EventCerealWrapper>();

	private List<EventRelationshipCerealWrapper> eventRelationships = new ArrayList<EventRelationshipCerealWrapper>();
	
	
	public long getIdentifier() {
		return identifier;
	}

	public void setIdentifier(long identifier) {
		this.identifier = identifier;
	}

	public boolean isNewUser() {
		return isNewUser;
	}

	public void setNewUser(boolean isNewUser) {
		this.isNewUser = isNewUser;
	}

	public List<Long> getMinglerIds() {
		return minglerIds;
	}

	public void setMinglerIds(List<Long> minglerIds) {
		this.minglerIds = minglerIds;
	}

	public List<EventTagCerealWrapper> getTags() {
		return tags;
	}

	public void setTags(List<EventTagCerealWrapper> tags) {
		this.tags = tags;
	}

	public List<EventCerealWrapper> getEvents() {
		return events;
	}

	public void setEvents(List<EventCerealWrapper> events) {
		this.events = events;
	}

	public List<EventRelationshipCerealWrapper> getEventRelationships() {
		return eventRelationships;
	}

	public void setEventRelationships(List<EventRelationshipCerealWrapper> eventRelationships) {
		this.eventRelationships = eventRelationships;
	}
	

}
