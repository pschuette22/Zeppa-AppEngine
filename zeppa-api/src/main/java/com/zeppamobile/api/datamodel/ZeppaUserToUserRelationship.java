package com.zeppamobile.api.datamodel;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdGeneratorStrategy;
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
	 * For maintaining relationships
	 */
	@Persistent
	private ZeppaUser creator;
	
	@Persistent
	private ZeppaUser subject;
	
	@Persistent(mappedBy="relationship", defaultFetchGroup="false")
	@Element(dependent="true")
	private List<EventTagFollow> tagFollows = new ArrayList<EventTagFollow>();
	

	/**
	 * Instantiate a User To User Relationship
	 * 
	 * @param creatorId
	 * @param subjectId
	 * @param relationshipType
	 */
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

	public ZeppaUser getCreator() {
		return creator;
	}

	public void setCreator(ZeppaUser creator) {
		this.creator = creator;
		this.creatorId = creator.getId();
	}

	public Long getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(Long subjectId) {
		this.subjectId = subjectId;
	}

	public ZeppaUser getSubject() {
		return subject;
	}

	public void setSubject(ZeppaUser subject) {
		this.subject = subject;
		this.subjectId = subject.getId();
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
	
	
	public boolean addTagFollow(EventTagFollow follow){
		return this.tagFollows.add(follow);
	}
	
	public boolean removeTagFollow(EventTagFollow follow) {
		return this.tagFollows.remove(follow);
	}

	public List<EventTagFollow> getTagFollows() {
		return tagFollows;
	}
	

}
