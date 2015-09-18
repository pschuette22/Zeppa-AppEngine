package com.zeppamobile.common.datamodel;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.json.simple.JSONObject;

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

	@Persistent(defaultFetchGroup="false")
	private ZeppaUser owner;
	
	/**
	 * Create a new EventTag Instance
	 * @param userId
	 * @param tagText
	 */
	public EventTag(ZeppaUser owner, String tagText) {

		this.created = System.currentTimeMillis();
		this.updated = System.currentTimeMillis();
		this.tagText = tagText;
		this.owner = owner;
		this.userId = owner.getId();
	}
	
	/**
	 * TEMPORARY
	 */
	public EventTag(Long ownerId, String tagText) {

		this.created = System.currentTimeMillis();
		this.updated = System.currentTimeMillis();
		this.tagText = tagText;
		this.userId = ownerId;
	}
	
	/**
	 * Rebuild an EventTag from json
	 * @param json
	 */
	public EventTag(JSONObject json){
		
		this.key = (Key) json.get("key");
		this.created = (Long) json.get("created");
		this.updated = (Long) json.get("updated");
		this.userId = (Long) json.get("userId");
		this.tagText = (String) json.get("tagText");
		
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
