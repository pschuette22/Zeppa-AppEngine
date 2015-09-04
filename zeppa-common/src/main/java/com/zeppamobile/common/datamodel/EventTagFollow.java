package com.zeppamobile.common.datamodel;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.json.JSONObject;

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
		this.created = json.getLong("created");
		this.updated = json.getLong("updated");
		this.tagId = json.getLong("tagId");
		this.tagOwnerId = json.getLong("tagOwnerId");
		this.followerId = json.getLong("followerId");
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

}
