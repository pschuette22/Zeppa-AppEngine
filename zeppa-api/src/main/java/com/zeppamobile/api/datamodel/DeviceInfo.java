package com.zeppamobile.api.datamodel;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class DeviceInfo  {
	
	public enum DeviceType {
		ANDROID, iOS
	};
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;
	
	@Persistent
	private Long created;
	
	@Persistent
	private Long updated;
	
	@Persistent
	private Long ownerId;

	@Persistent
	private DeviceType phoneType;

	@Persistent
	private String registrationId;

	@Persistent
	private Boolean loggedIn;

	@Persistent
	private Long lastLogin;

	/*
	 * These will only be change when a new version of Zeppa is installed
	 * Versions will be defined ##.##.## or <version>.<update>.<bugfix>
	 */
	@Persistent
	// This will be increased when new features are added
	private Integer version;

	@Persistent
	// This will be increased when existing features are enhanced or minor
	// features are added
	private Integer update;

	@Persistent
	// This will be increased when bugs are fixed
	private Integer bugfix;

//	/**
//	 * Contructor. Not used by endpoints.
//	 * 
//	 * @param owner
//	 * @param phoneType
//	 * @param registrationId
//	 * @param loggedIn
//	 * @param version
//	 * @param update
//	 * @param bugfix
//	 */
//	public DeviceInfo(DeviceType phoneType,
//			String registrationId, Boolean loggedIn, Integer version,
//			Integer update, Integer bugfix) {
//
//		this.created = System.currentTimeMillis();
//		this.updated = System.currentTimeMillis();
//		this.phoneType = phoneType;
//		this.registrationId = registrationId;
//		this.loggedIn = loggedIn;
//		this.version = version;
//		this.update = update;
//		this.bugfix = bugfix;
//	}
	
	public DeviceInfo(){}
	
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


	public DeviceType getPhoneType() {
		return phoneType;
	}

	public void setPhoneType(DeviceType phoneType) {
		this.phoneType = phoneType;
	}

	public String getRegistrationId() {
		return registrationId;
	}

	public void setRegistrationId(String registrationId) {
		this.registrationId = registrationId;
	}

	public Boolean getLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(Boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Integer getUpdate() {
		return update;
	}

	public void setUpdate(Integer update) {
		this.update = update;
	}

	public Integer getBugfix() {
		return bugfix;
	}

	public void setBugfix(Integer bugfix) {
		this.bugfix = bugfix;
	}

	public Long getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Long lastLogin) {
		this.lastLogin = lastLogin;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}
	

}
