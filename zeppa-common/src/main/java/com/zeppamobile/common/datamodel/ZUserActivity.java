package com.zeppamobile.common.datamodel;

import java.util.Date;

import com.google.appengine.api.search.GeoPoint;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Parent;

@Entity
public class ZUserActivity extends ZActivity {

	public enum Privacy {
		PUBLIC, FRIENDS, PRIVATE
	};

	@Parent
	Key<ZUser> host;

	private Privacy privacy;

	public ZUserActivity(String title, Date start, Date end, GeoPoint location,
			Boolean isInvitesAllowed, String googleCalendarEventId,Key<ZUser> host,  Privacy privacy) {
		super(title, start, end, location, isInvitesAllowed, googleCalendarEventId);
		this.host = host;
		this.privacy = privacy;
	}

	public Key<ZUser> getHost() {
		return host;
	}

	public Privacy getPrivacy() {
		return privacy;
	}
	
	
	
}
