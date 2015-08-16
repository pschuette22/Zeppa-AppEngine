package com.zeppamobile.api.datamodel;

import javax.jdo.annotations.IdGeneratorStrategy;
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
	private String googleProfileId; // Calling user object

	@Persistent
	private String zeppaCalendarId;
	
	@Persistent
	private String authEmail;
	

	// For Guice
	public ZeppaUser(){}
	
//	public ZeppaUser(ZeppaUserInfo userInfo, String googleProfileId,
//			String zeppaCalendarId) {
//
//		this.created = System.currentTimeMillis();
//		this.updated = System.currentTimeMillis();
//		this.userInfo = userInfo;
//		this.googleProfileId = googleProfileId;
//		this.zeppaCalendarId = zeppaCalendarId;
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
	
	public void setAuthEmail(String authEmail){
		this.authEmail = authEmail;
	}

	public ZeppaUserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(ZeppaUserInfo userInfo) {
		this.userInfo = userInfo;
	}

	public String getGoogleProfileId() {
		return googleProfileId;
	}

	public void setGoogleProfileId(String googleProfileId) {
		this.googleProfileId = googleProfileId;
	}

	public String getZeppaCalendarId() {
		return zeppaCalendarId;
	}

	public void setZeppaCalendarId(String zeppaCalendarId) {
		this.zeppaCalendarId = zeppaCalendarId;
	}

	/*
	 * These methods are only used by App Engine Database
	 */


}