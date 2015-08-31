package com.zeppamobile.common.datamodel;

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

	@Persistent
	private String authEmail;

	@NotPersistent
	private List<String> initialTags;

	public ZeppaUser(ZeppaUserInfo userInfo, String zeppaCalendarId,
			List<String> initialTags) {

		this.created = System.currentTimeMillis();
		this.updated = System.currentTimeMillis();
		this.userInfo = userInfo;
		this.zeppaCalendarId = zeppaCalendarId;
		this.initialTags = initialTags;
	}

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

	/*
	 * These methods are only used by App Engine Database
	 */

}