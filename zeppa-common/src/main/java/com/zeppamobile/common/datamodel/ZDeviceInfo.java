package com.zeppamobile.common.datamodel;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;

@Entity
public class ZDeviceInfo extends ZData {

	public enum OperatingSystem {
		ANDROID, iOS
	};

	@Parent
	Key<ZUser> user;

	@Index
	private OperatingSystem operatingSystem;

	/**
	 * Device token or registration id - value used to
	 */
	private String token;

	@Index
	private Boolean loggedIn;

	/*
	 * Zeppa App Version code running on device
	 */
	private Integer version;
	private Integer update;
	private Integer bugfix;
	
	/**
	 * Constructor
	 * 
	 * @param user
	 * @param operatingSystem
	 * @param token
	 * @param loggedIn
	 * @param version
	 * @param update
	 * @param bugfix
	 */
	public ZDeviceInfo(Key<ZUser> user, OperatingSystem operatingSystem, String token, Boolean loggedIn, Integer version,
			Integer update, Integer bugfix) {
		super();
		this.user = user;
		this.operatingSystem = operatingSystem;
		this.token = token;
		this.loggedIn = loggedIn;
		this.version = version;
		this.update = update;
		this.bugfix = bugfix;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
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

	public Key<ZUser> getUser() {
		return user;
	}

	public OperatingSystem getOperatingSystem() {
		return operatingSystem;
	}	
	
}
