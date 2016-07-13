package com.zeppamobile.common.datamodel;

import java.util.Date;

import com.google.appengine.api.search.GeoPoint;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;

@Entity
public class ZActivity extends ZData {

	private String title;

	@Index
	private Date start;

	@Index
	private Date end;

	@Index
	private GeoPoint location;

	private Boolean isInvitesAllowed;

	private String googleCalendarEventId;

	// TODO: add term list

	protected ZActivity(String title, Date start, Date end, GeoPoint location,
			Boolean isInvitesAllowed, String googleCalendarEventId) {
		super();
		this.title = title;
		this.start = start;
		this.end = end;
		this.location = location;
		this.isInvitesAllowed = isInvitesAllowed;
		this.googleCalendarEventId = googleCalendarEventId;
	}

	public String getTitle() {
		return title;
	}

	public Date getStart() {
		return start;
	}

	public Date getEnd() {
		return end;
	}

	public GeoPoint getLocation() {
		return location;
	}

	public Boolean getIsInvitesAllowed() {
		return isInvitesAllowed;
	}

	public String getGoogleCalendarEventId() {
		return googleCalendarEventId;
	}

}
