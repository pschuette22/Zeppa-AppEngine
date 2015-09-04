package com.zeppamobile.common.datamodel;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.json.JSONObject;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class EventTag {

	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;
	
	@Persistent
	private Long created;
	
	@Persistent
	private Long updated;
	
	@Persistent
	private Long userId;

	@Persistent
	private String tagText;

	/**
	 * Create a new EventTag Instance
	 * @param userId
	 * @param tagText
	 */
	public EventTag(Long userId, String tagText) {

		this.created = System.currentTimeMillis();
		this.updated = System.currentTimeMillis();
		this.tagText = tagText;
		this.userId = userId;
	}
	
	/**
	 * Rebuild an EventTag from json
	 * @param json
	 */
	public EventTag(JSONObject json){
		
		this.key = (Key) json.get("key");
		this.created = json.getLong("created");
		this.updated = json.getLong("updated");
		this.userId = json.getLong("userId");
		this.tagText = json.getString("tagText");
		
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
	
	public String getTagText() {
		return tagText;
	}

	public void setTagText(String tagText) {
		this.tagText = tagText;
	}

	public Long getOwnerId() {
		return userId;
	}

	public void setOwnerId(Long ownerId) {
		this.userId = ownerId;
	}

}
