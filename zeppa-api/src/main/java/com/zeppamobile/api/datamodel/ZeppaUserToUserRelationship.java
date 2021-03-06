package com.zeppamobile.api.datamodel;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.json.simple.JSONObject;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class ZeppaUserToUserRelationship {

	public enum UserRelationshipType {
		PENDING_REQUEST, MINGLING
	}

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	@Persistent
	private Long created;

	@Persistent
	private Long updated;

	@Persistent
	private Long creatorId;

	@Persistent
	private Long subjectId;

	@Persistent
	private UserRelationshipType relationshipType;

	/*
	 * This is our calculation of how interested one of this users in this
	 * relationship is in doing activities with another user
	 */
	@Persistent(defaultFetchGroup = "false")
	private Double creatorInterest;

	@Persistent(defaultFetchGroup = "false")
	private Double subjectInterest;

	/**
	 * Not persistent entity to be set before being passed back to client so
	 * additional get requests are not required
	 */
	@NotPersistent
	private ZeppaUserInfo userInfo;

	/**
	 * Blank Constructor to make appengine happy
	 */
	public ZeppaUserToUserRelationship() {
		// NOTE: needed by appengine do not delete
	}

	/**
	 * Instantiate a User To User Relationship
	 * 
	 * @param creatorId
	 * @param subjectId
	 * @param relationshipType
	 */
	public ZeppaUserToUserRelationship(ZeppaUser creator, ZeppaUser subject,
			UserRelationshipType relationshipType) {

		this.created = System.currentTimeMillis();
		this.updated = System.currentTimeMillis();
		this.creatorId = creator.getId();
		this.subjectId = subject.getId();
		this.relationshipType = relationshipType;
	}

	/**
	 * Reconstruct user relationship object from json
	 * 
	 * @param json
	 */
	public ZeppaUserToUserRelationship(JSONObject json) {

		this.key = (Key) json.get("key");
		this.created = (Long) json.get("created");
		this.updated = (Long) json.get("updated");
		this.creatorId = (Long) json.get("creatorId");
		this.subjectId = (Long) json.get("subjectId");
		this.relationshipType = UserRelationshipType.valueOf((String) json
				.get("relationshipType"));
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

	public Long getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}

	public Long getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(Long subjectId) {
		this.subjectId = subjectId;
	}

	public UserRelationshipType getRelationshipType() {
		return relationshipType;
	}

	public void setRelationshipType(UserRelationshipType relationshipType) {
		this.relationshipType = relationshipType;
	}

	/**
	 * Convenience method to get other userId of user involved in this
	 * relationship
	 * 
	 * @param userId
	 *            , asking user's id
	 * @return the other user id or null if provided user is not involved in
	 *         this relationship
	 */
	public Long getOtherUserId(Long userId) {
		if (userId.longValue() == creatorId.longValue()) {
			return subjectId;
		} else if (userId.longValue() == subjectId.longValue()) {
			return creatorId;
		} else {
			// TODO: flag this
			return null;
		}
	}

	public ZeppaUserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(ZeppaUserInfo userInfo) {
		this.userInfo = userInfo;
	}

}
