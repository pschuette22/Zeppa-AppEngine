package com.zeppamobile.api.datamodel;

import java.util.ArrayList;
import java.util.List;

public class VendorEvent {

	private Long vendorId;
	private Long eventId;
	private String title;
	private String description;
	private Long start;
	private Long end;
	private Long created;
	List<EventTag> tags = new ArrayList<EventTag>();
	
	
	public Long getVendorId() {
		return vendorId;
	}
	public void setVendorId(Long vendorId) {
		// To get dependency in UML
		Vendor v;
		this.vendorId = vendorId;
	}
	public Long getEventId() {
		return eventId;
	}
	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Long getStart() {
		return start;
	}
	public void setStart(Long start) {
		this.start = start;
	}
	public Long getEnd() {
		return end;
	}
	public void setEnd(Long end) {
		this.end = end;
	}
	public Long getCreated() {
		return created;
	}
	public void setCreated(Long created) {
		this.created = created;
	}
	public List<EventTag> getTags() {
		return tags;
	}
	public void addTag(EventTag tag) {
		this.tags.add(tag);
	}

	
}
