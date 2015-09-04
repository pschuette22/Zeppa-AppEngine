package com.zeppamobile.common.datamodel;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class ZeppaEvent {

	public enum EventPrivacyType {
		CASUAL, // Friends
		PRIVATE // Invite Only
	}

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	@Persistent
	private Long created;

	@Persistent
	private Long updated;

	@Persistent
	private String googleCalendarId;

	@Persistent
	private String googleCalendarEventId;

	@Persistent
	private String iCalUID; // verify what this is

	@Persistent
	private EventPrivacyType privacy;

	@Persistent
	private Long hostId;

	@Persistent
	private String title;

	@Persistent
	private String description;

	@Persistent
	private Boolean guestsMayInvite;

	@Persistent
	private Long start;

	@Persistent
	private Long end;

	@Persistent
	private String displayLocation;

	@Persistent
	private String mapsLocation;

	@Persistent(defaultFetchGroup = "true")
	private List<Long> tagIds = new ArrayList<Long>();

	// Initially invited users
	@Persistent
	private List<Long> invitedUserIds;

	public ZeppaEvent(Long created, Long updated, String googleCalendarId,
			String googleCalendarEventId, String iCalUID,
			EventPrivacyType privacy, Long hostId, String title,
			String description, Boolean guestsMayInvite, Long start, Long end,
			String displayLocation, String mapsLocation, List<Long> tagIds,
			List<Long> invitedUserIds) {

		this.created = created;
		this.updated = updated;
		this.googleCalendarId = googleCalendarId;
		this.googleCalendarEventId = googleCalendarEventId;
		this.iCalUID = iCalUID;
		this.privacy = privacy;
		this.hostId = hostId;
		this.title = title;
		this.description = description;
		this.guestsMayInvite = guestsMayInvite;
		this.start = start;
		this.end = end;
		this.displayLocation = displayLocation;
		this.mapsLocation = mapsLocation;
		this.tagIds = tagIds;
		this.invitedUserIds = invitedUserIds;
	}

	/**
	 * Rebuild
	 * 
	 * @param json
	 */
	public ZeppaEvent(JSONObject json) {
		this.key = (Key) json.get("key");
		
		this.created = json.getLong("created");
		this.updated = json.getLong("updated");
		this.googleCalendarId = json.getString("googleCalendarId");
		this.googleCalendarEventId = json.getString("googleCalendarEventId");
		
		try {
			this.iCalUID = json.getString("iCalUID");
		} catch (JSONException e) {

		}
		
		this.privacy = EventPrivacyType.valueOf(json.getString("privacy"));
		this.hostId = json.getLong("hostId");
		this.title = json.getString("title");
		this.description = json.getString("description");
		this.guestsMayInvite = json.getBoolean("guestsMayInvite");
		this.start = json.getLong("start");
		this.end = json.getLong("end");
		this.displayLocation = json.getString("displayLocation");

		try {
			this.mapsLocation = json.getString("mapsLocation");
		} catch (JSONException e) {

		}
		/*
		 * Fetch tag ids from json array
		 */
		this.tagIds = new ArrayList<Long>();
		try {
			JSONArray tagArray = json.getJSONArray("tagIds");
			for (int i = 0; i < tagArray.length(); i++) {
				if (tagArray.isNull(i)) {
					break;
				}
				tagIds.add(tagArray.getLong(i));
			}
		} catch (JSONException e) {

		}

		/*
		 * Rebuild the array of users that were invitied initially
		 */
		this.invitedUserIds = new ArrayList<Long>();
		try {
			JSONArray invitedArray = json.getJSONArray("invitedUserIds");
			for (int i = 0; i < invitedArray.length(); i++) {
				if (invitedArray.isNull(i)) {
					break;
				}
				invitedUserIds.add(invitedArray.getLong(i));
			}

		} catch (JSONException e) {

		}

	}

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

	public String getGoogleCalendarId() {
		return googleCalendarId;
	}

	public void setGoogleCalendarId(String googleCalendarId) {
		this.googleCalendarId = googleCalendarId;
	}

	public String getGoogleCalendarEventId() {
		return googleCalendarEventId;
	}

	public void setGoogleCalendarEventId(String googleCalendarEventId) {
		this.googleCalendarEventId = googleCalendarEventId;
	}

	public String getiCalUID() {
		return iCalUID;
	}

	public void setiCalUID(String iCalUID) {
		this.iCalUID = iCalUID;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getStart() {
		return start;
	}

	public void setStart(Long start) {
		this.start = start;
	}

	public Long getEnd() {
		return end;
	}

	public void setEnd(Long end) {
		this.end = end;
	}

	public String getDisplayLocation() {
		return displayLocation;
	}

	public void setDisplayLocation(String displayLocation) {
		this.displayLocation = displayLocation;
	}

	public String getMapsLocation() {
		return mapsLocation;
	}

	public void setMapsLocation(String mapsLocation) {
		this.mapsLocation = mapsLocation;
	}

	public List<Long> getTagIds() {
		return tagIds;
	}

	public void setTagIds(List<Long> tagIds) {
		this.tagIds = tagIds;
	}

	public EventPrivacyType getPrivacy() {
		return privacy;
	}

	public void setPrivacy(EventPrivacyType privacy) {
		this.privacy = privacy;
	}

	public Long getHostId() {
		return hostId;
	}

	public void setHostId(Long hostId) {
		this.hostId = hostId;
	}

	public Boolean getGuestsMayInvite() {
		return guestsMayInvite;
	}

	public void setGuestsMayInvite(Boolean guestsMayInvite) {
		this.guestsMayInvite = guestsMayInvite;
	}

	public List<Long> getInvitedUserIds() {
		return invitedUserIds;
	}

	public void setInvitedUserIds(List<Long> invitedUserIds) {
		this.invitedUserIds = invitedUserIds;
	}

}
