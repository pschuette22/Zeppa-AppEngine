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

	/**
	 * Google account email or unique, authenticated email
	 */
	@Persistent
	private String authEmail;

	/**
	 * 10 digit phone number of user with all non-digit characters and spaces
	 * removed (19876543210)
	 */
	@Persistent
	private String phoneNumber;

	/**
	 * Last known longitude of the user
	 */
	@Persistent
	private Long longitude;

	/**
	 * Last known latitude of the user
	 */
	@Persistent
	private Long latitude;

	/*
	 * These are tags to be persisted as soon as the
	 */
	@NotPersistent
	private List<String> initialTags;
	
	/**
	 * Blank Constructor if you want to initialize everything
	 */
	public ZeppaUser() {
		// Super expensive op
	}
	
	/**
	 * Construct a Zeppa User object with populated fields
	 * 
	 * @param authEmail - email used for authorization
	 * @param givenName - first name of this user
	 * @param familyName - last name of this user
	 * @param phoneNumber - phone number as unformatted 10-digit number
	 * @param latitude - last known latitude of this user
	 * @param longitude - last known longitude of this user
	 */
	public ZeppaUser(String authEmail, String givenName, String familyName, String phoneNumber, Long latitude, Long longitude, List<String> initialTags) {
		ZeppaUserInfo info = new ZeppaUserInfo();
		info.setGivenName(givenName);
		info.setFamilyName(familyName);
		info.setImageUrl("default-image-url.jpg");
		
		this.userInfo = info;
		this.authEmail = authEmail;
		this.phoneNumber = phoneNumber;
		this.latitude = latitude;
		this.longitude = longitude;
		this.initialTags = initialTags;
	}
	

	// public ZeppaUser(ZeppaUserInfo userInfo, String zeppaCalendarId,
	// List<String> initialTags) {
	//
	// this.created = System.currentTimeMillis();
	// this.updated = System.currentTimeMillis();
	// this.userInfo = userInfo;
	// this.zeppaCalendarId = zeppaCalendarId;
	// this.initialTags = initialTags;
	// }

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
	
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public Long getLongitude() {
		return longitude;
	}

	public void setLongitude(Long longitude) {
		this.longitude = longitude;
	}

	public Long getLatitude() {
		return latitude;
	}

	public void setLatitude(Long latitude) {
		this.latitude = latitude;
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

	/**
	 * Get the event tags for this user
	 * 
	 * @return tags
	 */
	public List<EventTag> getTags() {
		return tags;
	}

	@Persistent
	private List<Key> follows = new ArrayList<Key>();

	/**
	 * Add a tag to the list of tags associated with this profile
	 * 
	 * @param tag
	 * @return true if tag was successfully added
	 */
	public boolean addTagFollow(EventTagFollow follow) {
		return this.follows.add(follow.getKey());
	}

	/**
	 * Remove a tag from the list of tags associated with this profile
	 * 
	 * @param tag
	 * @return
	 */
	public boolean removeTagFollow(EventTagFollow follow) {
		return this.follows.remove(follow.getKey());
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
	private List<Key> eventRelationships = new ArrayList<Key>();

	/**
	 * Add an Event Relationship to list of relationships for this user
	 * 
	 * @param relationship
	 * @return true if relationship was added successfully
	 */
	public boolean addEventRelationship(
			ZeppaEventToUserRelationship relationship) {
		return this.eventRelationships.add(relationship.getKey());
	}

	/**
	 * Remove an Event Relationship from list of relationships for this user
	 * 
	 * @param relationship
	 * @return true if Event Relationship was successfully removed
	 */
	public boolean removeEventRelationship(
			ZeppaEventToUserRelationship relationship) {
		return this.eventRelationships.remove(relationship.getKey());
	}

	/*
	 * These are user relationships created by this user.
	 */
	@Persistent(defaultFetchGroup = "false")
	private List<Key> userRelationships = new ArrayList<Key>();

	/**
	 * Add a User relationship
	 * 
	 * @param relationship
	 * @return true if relationship was added successfully
	 */
	public boolean addUserRealtionship(ZeppaUserToUserRelationship relationship) {
		return userRelationships.add(relationship.getKey());
	}

	/**
	 * Remove a User relationship
	 * 
	 * @param relationship
	 * @return true if Relationship was successfully removed
	 */
	public boolean removeUserRelationship(
			ZeppaUserToUserRelationship relationship) {
		return userRelationships.remove(relationship.getKey());
	}

	/**
	 * get a list of the user relationships
	 * 
	 * @return relationships list
	 */
	public List<Key> getUserRelationships() {
		return userRelationships;
	}

	/*
	 * Hold a list of notifications sent to this user
	 */
	@Persistent(defaultFetchGroup = "false")
	private List<Key> notifications = new ArrayList<Key>();

	/**
	 * Add a notification that was delivered to this user
	 * 
	 * @param notification
	 * @return true if the notification was added successfully
	 */
	public boolean addNotification(ZeppaNotification notification) {
		return notifications.add(notification.getKey());
	}

	/**
	 * Remove a notification sent to this user
	 * 
	 * @param notification
	 * @return true if notification was removed successfully
	 */
	public boolean removeNotification(ZeppaNotification notification) {
		return notifications.remove(notification.getKey());
	}

	/*
	 * Hold a list of notifications this user sent
	 */
	@Persistent(defaultFetchGroup = "false")
	private List<Key> sentNotifications = new ArrayList<Key>();

	/**
	 * Add a notification this user sent
	 * 
	 * @param notification
	 * @return true notification was added successfully
	 */
	public boolean addSentNotification(ZeppaNotification notification) {
		return sentNotifications.add(notification.getKey());
	}

	/**
	 * Remove a notification this user sent
	 * 
	 * @param notification
	 * @return true if notification was removed successfully
	 */
	public boolean removeSentNotification(ZeppaNotification notification) {
		return sentNotifications.remove(notification.getKey());
	}

	/*
	 * Hold a list of comments this user has made
	 */
	@Persistent(defaultFetchGroup = "false")
	private List<Key> comments = new ArrayList<Key>();

	/**
	 * Add a reference to a comment this user made on an event
	 * 
	 * @param comment
	 * @return true if comment was added successfully
	 */
	public boolean addComment(EventComment comment) {
		return comments.add(comment.getKey());
	}

	/**
	 * Remove a reference to a comment this user made on an event
	 * 
	 * @param comment
	 * @return
	 */
	public boolean removeComment(EventComment comment) {
		return comments.remove(comment.getKey());
	}

}