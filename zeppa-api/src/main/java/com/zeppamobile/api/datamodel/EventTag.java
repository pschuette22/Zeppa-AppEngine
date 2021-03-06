package com.zeppamobile.api.datamodel;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.json.simple.JSONObject;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class EventTag {
	
	// This enum tells whether the tag is owned by a user or a vendor
	public enum TagType {USER, VENDOR};

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	@Persistent
	private Long created;

	@Persistent
	private Long updated;

	@Persistent
	private Long ownerId;

	@Persistent
	private String tagText;
	
	@Persistent
	private List<String> indexedWords;
	
	@Persistent
	private TagType type;

	@NotPersistent
	private EventTagFollow follow;
	
	/**
	 * Blank Constructor
	 */
	public EventTag() {

	}

	/**
	 * Create a new EventTag Instance
	 * 
	 * @param userId
	 * @param tagText
	 */
	public EventTag(ZeppaUser owner, String tagText) {

		this.created = System.currentTimeMillis();
		this.updated = System.currentTimeMillis();
		this.tagText = tagText;
		this.ownerId = owner.getId();
		this.indexedWords = new ArrayList<String>();
	}

	/**
	 * Rebuild an EventTag from json
	 * 
	 * @param json
	 */
	public EventTag(JSONObject json) {

		this.key = (Key) json.get("key");
		this.created = (Long) json.get("created");
		this.updated = (Long) json.get("updated");
		this.ownerId = (Long) json.get("ownerId");
		this.tagText = (String) json.get("tagText");
//		this.indexedWords = (List<String>) json.get("indexedWords");
	}

	/**
	 * Convert this object to a json object
	 * 
	 * @return jsonObject
	 */
	@SuppressWarnings("unchecked")
	public JSONObject toJson() {
		JSONObject obj = new JSONObject();

		obj.put("id", key.getId());
		obj.put("created", created == null ? Long.valueOf(-1) : created);
		obj.put("updated", updated == null ? Long.valueOf(-1) : updated);

		obj.put("tagText", tagText);
		obj.put("ownerId", ownerId);

		return obj;
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
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}
	
	public TagType getType() {
		return type;
	}

	public void setType(TagType type) {
		this.type = type;
	}

	public List<String> getIndexedWords() {
		return indexedWords;
	}

	public void setIndexedWords(List<String> indexedWords) {
		this.indexedWords = indexedWords;
	}

	public EventTagFollow getFollow() {
		return follow;
	}

	public void setFollow(EventTagFollow follow) {
		this.follow = follow;
	}
	
	

}
