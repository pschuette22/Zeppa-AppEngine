package com.zeppamobile.api.datamodel;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.json.simple.JSONObject;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class EventTagFollow {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	@Persistent
	private Long created;

	@Persistent
	private Long updated;

	@Persistent
	private Long tagId;

	@Persistent
	private Long tagOwnerId;

	@Persistent
	private Long followerId;
	
	@Persistent
	private double calculatedInterest;
	

	/**
	 * Instantiate follow object
	 * 
	 * @param tag
	 * @param followerId
	 */
	public EventTagFollow(EventTag tag, ZeppaUser follower, ZeppaUserToUserRelationship relationship) {

		this.created = System.currentTimeMillis();
		this.updated = System.currentTimeMillis();
		this.tagId = tag.getId();
		this.tagOwnerId = tag.getOwnerId();
		this.followerId = follower.getId();
		
	}
	
	/**
	 * Instantiate follow object
	 * 
	 * @param tag
	 * @param followerId
	 */
	public EventTagFollow(EventTag tag, Long followerId) {

		this.created = System.currentTimeMillis();
		this.updated = System.currentTimeMillis();
		this.tagId = tag.getId();
		this.tagOwnerId = tag.getOwnerId();
		this.followerId = followerId;
		
		
	}


	/**
	 * Rebuild an EventTagFollow from json
	 * 
	 * @param json
	 *            representation
	 */
	public EventTagFollow(JSONObject json) {
		try {
			this.key = (Key) json.get("key");
		} catch (Exception e) {
			// When rebuilding for insert, object will not have key
		}
		this.created = (Long)json.get("created");
		this.updated = (Long)json.get("updated");
		this.tagId = (Long)json.get("tagId");
		this.tagOwnerId = (Long) json.get("tagOwnerId");
		this.followerId = (Long) json.get("followerId");
	}
	
	/**
	 * Convert this object to @JSONObject
	 * @return @JSONObject mapped to this object
	 */
	@SuppressWarnings("unchecked")
	public JSONObject toJson(){
		JSONObject obj = new JSONObject();
		obj.put("key", key);
		obj.put("created", created);
		obj.put("updated",updated);
		obj.put("tagId",tagId);
		obj.put("tagOwnerId",tagOwnerId);
		obj.put("followerId", followerId);
		
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

	public Long getFollowerId() {
		return followerId;
	}

	public void setFollowerId(Long followerId) {
		this.followerId = followerId;
	}

	public Long getTagId() {
		return tagId;
	}

	public void setTagId(Long tagId) {
		this.tagId = tagId;
	}

	public Long getTagOwnerId() {
		return tagOwnerId;
	}

	public void setTagOwnerId(Long tagOwnerId) {
		this.tagOwnerId = tagOwnerId;
	}

	public void setTag(EventTag tag) {
		this.tagId = tag.getId();
	}

	public void setFollower(ZeppaUser follower) {
		this.followerId = follower.getId();
	}


}
