package com.zeppamobile.common.datainfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EventInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long eventId = -1;

	private long hostId = -1;

	private boolean isNewEvent = false;

	private List<EventRelationshipInfo> relationships = new ArrayList<EventRelationshipInfo>();

	private List<EventTagInfo> tags = new ArrayList<EventTagInfo>();

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

	public List<EventRelationshipInfo> getRelationships() {
		return relationships;
	}

	public void setRelationships(List<EventRelationshipInfo> relationships) {
		this.relationships = relationships;
	}

	public List<EventTagInfo> getTags() {
		return tags;
	}

	public void setTags(List<EventTagInfo> tags) {
		this.tags = tags;
	}

}
