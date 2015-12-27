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
public class EventTag {

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
	private ZeppaUser owner;

	@Persistent
	private List<Key> follows = new ArrayList<Key>();

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
		this.owner = owner;
		this.ownerId = owner.getId();
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

	public ZeppaUser getOwner() {
		return owner;
	}

	public void setOwner(ZeppaUser owner) {
		this.owner = owner;
	}
	
	public boolean addEventTagFollow(EventTagFollow follow){
		return this.follows.add(follow.getKey());
	}
	
	public boolean removeFollow(EventTagFollow follow){
		return this.follows.remove(follow.getKey());
	}
	
	/**
	 * get the database keys of follow objects
	 * 
	 * @return follows
	 */
	public List<Key> getFollowKeys() {
		return this.follows;
	}

}
