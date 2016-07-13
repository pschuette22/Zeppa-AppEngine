package com.zeppamobile.common.datamodel;

import com.google.appengine.api.search.GeoPoint;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

/**
 * 
 * @author PSchuette
 * 
 * User object for Zeppa datastore
 *
 */
@Entity
public class ZUser extends ZData {

	@Index
	private String googleAccountId;
	
	private String zeppaCalendarId;
	
	private String phoneNumber;
	
	private String email;
	
	@Index
	private GeoPoint location;

	public ZUser(String googleAccountId, String zeppaCalendarId, String phoneNumber, GeoPoint location) {
		super();
		this.googleAccountId = googleAccountId;
		this.zeppaCalendarId = zeppaCalendarId;
		this.phoneNumber = phoneNumber;
		this.location = location;
	}

	public String getGoogleAccountId() {
		return googleAccountId;
	}

	public void setGoogleAccountId(String googleAccountId) {
		this.googleAccountId = googleAccountId;
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

	public GeoPoint getLocation() {
		return location;
	}

	public void setLocation(GeoPoint location) {
		this.location = location;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	
	
}
