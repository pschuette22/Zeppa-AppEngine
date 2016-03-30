package com.zeppamobile.common.cerealwrapper;

import java.util.ArrayList;
import java.util.List;

public class EventCerealWrapper extends CerealWrapper {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long eventId = -1;

	private long hostId = -1;

	private boolean isNewEvent = false;

	private List<EventRelationshipCerealWrapper> relationships = new ArrayList<EventRelationshipCerealWrapper>();

	private List<EventTagCerealWrapper> tags = new ArrayList<EventTagCerealWrapper>();

	
	
	/**
	 * Construct some event cereal
	 * this is called from 
	 * 
	 * @param eventId
	 * @param hostId
	 * @param isNewEvent
	 * @param relationships
	 * @param tags
	 */
	public EventCerealWrapper(long eventId, long hostId, boolean isNewEvent,
			List<EventRelationshipCerealWrapper> relationships,
			List<EventTagCerealWrapper> tags) {
		super();
		this.eventId = eventId;
		this.hostId = hostId;
		this.isNewEvent = isNewEvent;
		this.relationships = relationships;
		this.tags = tags;
	}

	public long getEventId() {
		return eventId;
	}

	public void setEventId(long eventId) {
		this.eventId = eventId;
	}

	public long getHostId() {
		return hostId;
	}

	public void setHostId(long hostId) {
		this.hostId = hostId;
	}

	public boolean isNewEvent() {
		return isNewEvent;
	}

	public void setNewEvent(boolean isNewEvent) {
		this.isNewEvent = isNewEvent;
	}

	public List<EventRelationshipCerealWrapper> getRelationships() {
		return relationships;
	}

	public void setRelationships(List<EventRelationshipCerealWrapper> relationships) {
		this.relationships = relationships;
	}

	public List<EventTagCerealWrapper> getTags() {
		return tags;
	}

	public void setTags(List<EventTagCerealWrapper> tags) {
		this.tags = tags;
	}

}
