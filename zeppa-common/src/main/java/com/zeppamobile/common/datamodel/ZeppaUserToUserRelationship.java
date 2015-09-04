package com.zeppamobile.common.datamodel;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.json.JSONObject;

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

	// For guice

	public ZeppaUserToUserRelationship(Long creatorId, Long subjectId,
			UserRelationshipType relationshipType) {

		this.created = System.currentTimeMillis();
		this.updated = System.currentTimeMillis();
		this.creatorId = creatorId;
		this.subjectId = subjectId;
		this.relationshipType = relationshipType;
	}
	
	/**
	 * Reconstruct user relationship object from json
	 * @param json
	 */
	public ZeppaUserToUserRelationship(JSONObject json){
		this.key = (Key) json.get("key");
		this.created = json.getLong("created");
		this.updated = json.getLong("updated");
		this.creatorId = json.getLong("creatorId");
		this.subjectId = json.getLong("subjectId");
		
		String type = json.getString("relationshipType");
		relationshipType = UserRelationshipType.valueOf(type);
		
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

}
