package com.zeppamobile.api.datamodel;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import org.json.simple.JSONObject;

@PersistenceCapable
public class VendorEvent extends ZeppaEvent {

	public VendorEvent(JSONObject json) {
		super(json);
		// TODO Auto-generated constructor stub
	}
	
	@Persistent
	private String placeId;
	
//	@PrimaryKey
//	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
//	private Key key;
//
//	@Persistent
//	private Long vendorId;
//	
//	@Persistent
//	private Long eventId;
//	
//	@Persistent
//	private String title;
//	
//	@Persistent
//	private String description;
//	
//	@Persistent
//	private Long start;
//	
//	@Persistent
//	private Long end;
//	
//	@Persistent
//	private Long created;
//	
//	@Persistent
//	List<EventTag> tags = new ArrayList<EventTag>();
//	
//	
//	public Long getVendorId() {
//		return vendorId;
//	}
//	public void setVendorId(Long vendorId) {
//		this.vendorId = vendorId;
//	}
//	public Long getEventId() {
//		return eventId;
//	}
//	public void setEventId(Long eventId) {
//		this.eventId = eventId;
//	}
//	public String getTitle() {
//		return title;
//	}
//	public void setTitle(String title) {
//		this.title = title;
//	}
//	public String getDescription() {
//		return description;
//	}
//	public void setDescription(String description) {
//		this.description = description;
//	}
//	public Long getStart() {
//		return start;
//	}
//	public void setStart(Long start) {
//		this.start = start;
//	}
//	public Long getEnd() {
//		return end;
//	}
//	public void setEnd(Long end) {
//		this.end = end;
//	}
//	public Long getCreated() {
//		return created;
//	}
//	public void setCreated(Long created) {
//		this.created = created;
//	}
//	public List<EventTag> getTags() {
//		return tags;
//	}
//	public void addTag(EventTag tag) {
//		this.tags.add(tag);
//	}

	
}
