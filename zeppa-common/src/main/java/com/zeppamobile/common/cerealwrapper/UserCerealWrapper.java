package com.zeppamobile.common.cerealwrapper;

import java.util.ArrayList;
import java.util.List;

public class UserCerealWrapper extends CerealWrapper {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private Long identifier = Long.valueOf(-1);
	
	private Boolean isNewUser = Boolean.FALSE;
	
	private List<Long> minglerIds = new ArrayList<Long>();
	
	private List<EventTagCerealWrapper> tags = new ArrayList<EventTagCerealWrapper>();
	
	private List<EventCerealWrapper> events = new ArrayList<EventCerealWrapper>();

	private List<EventRelationshipCerealWrapper> eventRelationships = new ArrayList<EventRelationshipCerealWrapper>();
	
	
	public UserCerealWrapper(Long identifier, Boolean isNewUser,
			List<Long> minglerIds, List<EventTagCerealWrapper> tags,
			List<EventCerealWrapper> events,
			List<EventRelationshipCerealWrapper> eventRelationships) {
		super();
		this.identifier = identifier;
		this.isNewUser = isNewUser;
		this.minglerIds = minglerIds;
		this.tags = tags;
		this.events = events;
		this.eventRelationships = eventRelationships;
	}

	public Long getIdentifier() {
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
