package com.zeppamobile.api.datamodel;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.json.simple.JSONObject;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class VendorEvent {
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	@Persistent
	private Long vendorId;
	
	@Persistent
	private String title;
	
	@Persistent
	private String description;
	
	@Persistent
	private Long start;
	
	@Persistent
	private Long end;
	
	@Persistent
	private Long created;
	
	@Persistent
	List<EventTag> tags = new ArrayList<EventTag>();
	
	
	public Long getVendorId() {
		return vendorId;
	}
	
	/**
	 * Convert this object to a json object
	 * 
	 * @return jsonObject
	 */
	@SuppressWarnings("unchecked")
	public JSONObject toJson() {
		JSONObject obj = new JSONObject();

		obj.put("key", key);
		obj.put("created", created == null ? Long.valueOf(-1) : created);
		obj.put("start", start == null ? Long.valueOf(-1) : start);
		obj.put("end", start == null ? Long.valueOf(-1) : end);
		obj.put("title", title);
		obj.put("description", description);
		obj.put("vendorId", vendorId);

		return obj;
	}
	
	public void setVendorId(Long vendorId) {
		// To get dependency in UML
		Vendor v;
		this.vendorId = vendorId;
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
