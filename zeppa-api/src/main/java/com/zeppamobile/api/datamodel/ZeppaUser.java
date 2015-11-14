package com.zeppamobile.api.datamodel;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class ZeppaUser {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	@Persistent
	private Long created;

	@Persistent
	private Long updated;

	@Persistent(embeddedElement = "true", dependent = "true", defaultFetchGroup = "true")
	private ZeppaUserInfo userInfo;

	@Persistent
	private String zeppaCalendarId;

	@Persistent
	private String authEmail;

	/*
	 * These are tags to be persisted as soon as the
	 */
	@NotPersistent
	private List<String> initialTags;

//	public ZeppaUser(ZeppaUserInfo userInfo, String zeppaCalendarId,
//			List<String> initialTags) {
//
//		this.created = System.currentTimeMillis();
//		this.updated = System.currentTimeMillis();
//		this.userInfo = userInfo;
//		this.zeppaCalendarId = zeppaCalendarId;
//		this.initialTags = initialTags;
//	}

	/*
	 * -------------- Setters ----------------
	 */

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

	public String getAuthEmail() {
		return authEmail;
	}

	public void setAuthEmail(String authEmail) {
		this.authEmail = authEmail;
	}

	public ZeppaUserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(ZeppaUserInfo userInfo) {
		this.userInfo = userInfo;
	}

	public String getZeppaCalendarId() {
		return zeppaCalendarId;
	}

	public void setZeppaCalendarId(String zeppaCalendarId) {
		this.zeppaCalendarId = zeppaCalendarId;
	}

	public List<String> getInitialTags() {
		return initialTags;
	}

	public void setInitialTags(List<String> initialTags) {
		this.initialTags = initialTags;
	}

	/*
	 * 
	 * These are all owned entities Relationships are stored here for cascading
	 * deleted and easy access
	 */

	/*
	 * 
	 * 
	 * Devices represent devices used to login to this account These are how we
	 * notify the user of updates to the database
	 */
	@Persistent(mappedBy = "owner", defaultFetchGroup = "false")
	@Element(dependent = "true")
	private List<DeviceInfo> devices = new ArrayList<DeviceInfo>();

	/**
	 * Add a device to the list of user devices
	 * 
	 * @param device
	 * @return true if device is successfully added
	 */
	public boolean addDevice(DeviceInfo device) {
		return this.devices.add(device);
	}

	/**
	 * Remove a device from the list of devices
	 * 
	 * @param device
	 * @return true if device was removed from list
	 */
	public boolean removeDevice(DeviceInfo device) {
		return this.devices.remove(device);
	}

	/*
	 * Event tags are a list of tags that the user has added to their profile
	 */
	@Persistent(mappedBy = "owner")
	@Element(dependent = "true")
	private List<EventTag> tags = new ArrayList<EventTag>();

	/**
	 * Add a tag to the list of tags associated with this profile
	 * 
	 * @param tag
	 * @return true if tag was successfully added
	 */
	public boolean addTag(EventTag tag) {
		return this.tags.add(tag);
	}

	/**
	 * Remove a tag from the list of tags associated with this profile
	 * 
	 * @param tag
	 * @return
	 */
	public boolean removeTag(EventTag tag) {
		return this.tags.remove(tag);
	}
	
	@Persistent
	private List<EventTagFollow> follows = new ArrayList<EventTagFollow>();

	/**
	 * Add a tag to the list of tags associated with this profile
	 * 
	 * @param tag
	 * @return true if tag was successfully added
	 */
	public boolean addTagFollow(EventTagFollow follow) {
		return this.follows.add(follow);
	}

	/**
	 * Remove a tag from the list of tags associated with this profile
	 * 
	 * @param tag
	 * @return
	 */
	public boolean removeTagFollow(EventTagFollow follow) {
		return this.follows.remove(follow);
	}
	

	/*
	 * 
	 * List of events this user has started
	 */
	@Persistent(mappedBy = "host", defaultFetchGroup = "false")
	@Element(dependent = "true")
	private List<ZeppaEvent> events = new ArrayList<ZeppaEvent>();

	/**
	 * Add an Event to this user's list of events
	 * 
	 * @param event
	 * @return true if event was added successfully
	 */
	public boolean addEvent(ZeppaEvent event) {
		return this.events.add(event);
	}

	/**
	 * Remove an Event from this user's list of events
	 * 
	 * @param event
	 * @return
	 */
	public boolean removeEvent(ZeppaEvent event) {
		return this.events.remove(event);
	}

	/*
	 * 
	 * List of relationships to events this user may join
	 */
	@Persistent(defaultFetchGroup = "false")
	private List<ZeppaEventToUserRelationship> eventRelationships = new ArrayList<ZeppaEventToUserRelationship>();

	/**
	 * Add an Event Relationship to list of relationships for this user
	 * 
	 * @param relationship
	 * @return true if relationship was added successfully
	 */
	public boolean addEventRelationship(
			ZeppaEventToUserRelationship relationship) {
		return this.eventRelationships.add(relationship);
	}

	/**
	 * Remove an Event Relationship from list of relationships for this user
	 * 
	 * @param relationship
	 * @return true if Event Relationship was successfully removed
	 */
	public boolean removeEventRelationship(
			ZeppaEventToUserRelationship relationship) {
		return this.eventRelationships.remove(relationship);
	}

	/*
	 * These are user relationships created by this user.
	 */
	@Persistent(defaultFetchGroup = "false")
	private List<ZeppaUserToUserRelationship> createdRelationships = new ArrayList<ZeppaUserToUserRelationship>();

	/**
	 * Add a User relationship to list of relationships created by this user
	 * 
	 * @param relationship
	 * @return true if relationship was added successfully
	 */
	public boolean addCreatedRealtionship(
			ZeppaUserToUserRelationship relationship) {
		return createdRelationships.add(relationship);
	}

	/**
	 * Remove a User relationship that was created by this user
	 * 
	 * @param relationship
	 * @return true if Event Relationship was successfully removed
	 */
	public boolean removeCreatedRelationship(
			ZeppaUserToUserRelationship relationship) {
		return createdRelationships.remove(relationship);
	}

	/*
	 * These are user relationships where this user is the subject
	 */
	@Persistent(defaultFetchGroup = "false")
	private List<ZeppaUserToUserRelationship> subjectRelationships = new ArrayList<ZeppaUserToUserRelationship>();

	/**
	 * Add a User Relationship this user is the subject of
	 * 
	 * @param relationship
	 * @return true if relationship was removed
	 */
	public boolean addSubjectRelationship(
			ZeppaUserToUserRelationship relationship) {
		return subjectRelationships.add(relationship);
	}

	/**
	 * Remove a User relationship that this user is the subject of
	 * 
	 * @param relationship
	 * @return
	 */
	public boolean removeSubjectRelationship(
			ZeppaUserToUserRelationship relationship) {
		return subjectRelationships.remove(relationship);
	}

	/**
	 * Remove a User relationship associated with this user
	 * 
	 * @param relationship
	 * @return true if relationship was removed
	 */
	public boolean removeUserRelationship(
			ZeppaUserToUserRelationship relationship) {
		return (removeCreatedRelationship(relationship) || removeSubjectRelationship(relationship));
	}

	/*
	 * Hold a list of notifications sent to this
	 */
	@Persistent(defaultFetchGroup = "false")
	private List<ZeppaNotification> notifications = new ArrayList<ZeppaNotification>();

	/**
	 * Add a notification that was delivered to this user
	 * 
	 * @param notification
	 * @return true if the notification was added successfully
	 */
	public boolean addNotification(ZeppaNotification notification) {
		return notifications.add(notification);
	}

	/**
	 * Remove a notification sent to this user
	 * 
	 * @param notification
	 * @return true if notification was removed successfully
	 */
	public boolean removeNotification(ZeppaNotification notification) {
		return notifications.remove(notification);
	}

	/*
	 * Hold a list of notifications this user sent
	 */
	@Persistent(defaultFetchGroup = "false")
	private List<ZeppaNotification> sentNotifications = new ArrayList<ZeppaNotification>();

	/**
	 * Add a notification this user sent
	 * 
	 * @param notification
	 * @return true notification was added successfully
	 */
	public boolean addSentNotification(ZeppaNotification notification) {
		return sentNotifications.add(notification);
	}

	/**
	 * Remove a notification this user sent
	 * 
	 * @param notification
	 * @return true if notification was removed successfully
	 */
	public boolean removeSentNotification(ZeppaNotification notification) {
		return sentNotifications.remove(notification);
	}

	/*
	 * Hold a list of comments this user has made
	 */
	@Persistent(defaultFetchGroup = "false")
	private List<EventComment> comments = new ArrayList<EventComment>();

	/**
	 * Add a reference to a comment this user made on an event
	 * 
	 * @param comment
	 * @return true if comment was added successfully
	 */
	public boolean addComment(EventComment comment) {
		return comments.add(comment);
	}

	/**
	 * Remove a reference to a comment this user made on an event
	 * 
	 * @param comment
	 * @return
	 */
	public boolean removeComment(EventComment comment) {
		return comments.remove(comment);
	}
	
	
	
}