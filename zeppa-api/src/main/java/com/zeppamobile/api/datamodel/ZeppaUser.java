package com.zeppamobile.api.datamodel;

import java.util.List;

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
	 * These are tags to be persisted as soon as the user object is created in database
	 */
	@NotPersistent
	private List<String> initialTags;
	
	/**
	 * Blank Constructor
	 */
	public ZeppaUser() {
		// NOTE: Required for appengine
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


}