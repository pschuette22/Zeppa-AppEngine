package com.zeppamobile.common.datainfo;

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
	
	private List<EventTagInfo> tags = new ArrayList<EventTagInfo>();
	
	private List<EventInfo> events = new ArrayList<EventInfo>();

	private List<EventRelationshipInfo> eventRelationships = new ArrayList<EventRelationshipInfo>();
	
	
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

	public List<EventTagInfo> getTags() {
		return tags;
	}

	public void setTags(List<EventTagInfo> tags) {
		this.tags = tags;
	}

	public List<EventInfo> getEvents() {
		return events;
	}

	public void setEvents(List<EventInfo> events) {
		this.events = events;
	}

	public List<EventRelationshipInfo> getEventRelationships() {
		return eventRelationships;
	}

	public void setEventRelationships(List<EventRelationshipInfo> eventRelationships) {
		this.eventRelationships = eventRelationships;
	}
	

}
