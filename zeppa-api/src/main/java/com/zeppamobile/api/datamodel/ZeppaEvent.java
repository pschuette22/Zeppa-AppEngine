package com.zeppamobile.api.datamodel;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.json.simple.JSONObject;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
@Discriminator(strategy=DiscriminatorStrategy.CLASS_NAME)
public class ZeppaEvent {

	
	public enum EventPrivacyType {
		CASUAL, // Friends
		PRIVATE, // Invite Only
		PUBLIC // Anyone can see and join
	}

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	protected Key key;

	@Persistent
	protected Long created;

	@Persistent
	protected Long updated;

	@Persistent
	protected String googleCalendarId;

	@Persistent
	protected String googleCalendarEventId;

	@Persistent
	protected String iCalUID; // verify what this is

	@Persistent
	protected EventPrivacyType privacy;

	@Persistent
	protected Long hostId;

	@Persistent
	protected String title;

	@Persistent
	protected String description;

	@Persistent
	protected Boolean guestsMayInvite;

	@Persistent
	protected Long start;

	@Persistent
	protected Long end;

	/*
	 * ===== Information relevant to location =====
	 */
	@Persistent
	protected String displayLocation;

	@Persistent
	protected String mapsLocation;

	@Persistent
	protected Float latitude;

	@Persistent
	protected Float longitude;
	/*
	 * ============================================
	 * 
	 */

	@Persistent(defaultFetchGroup = "true")
	protected List<Long> tagIds = new ArrayList<Long>();

	// Initially invited users
	@Persistent
	protected List<Long> invitedUserIds;


	public ZeppaEvent(String googleCalendarId,
			String googleCalendarEventId, String iCalUID,
			EventPrivacyType privacy, Long hostId, String title,
			String description, Boolean guestsMayInvite, Long start, Long end,
			String displayLocation, String mapsLocation, List<Long> tagIds,
			List<Long> invitedUserIds) {

		this.created = System.currentTimeMillis();
		this.updated = System.currentTimeMillis();
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
	@SuppressWarnings("unchecked")
	public ZeppaEvent(JSONObject json) {
		this.key = (Key) json.get("key");

		this.created = (Long) json.get("created");
		this.updated = (Long) json.get("updated");
		this.googleCalendarId = (String) json.get("googleCalendarId");
		this.googleCalendarEventId = (String) json.get("googleCalendarEventId");

		this.iCalUID = (String) json.get("iCalUID");

		this.privacy = EventPrivacyType.valueOf((String) json.get("privacy"));
		this.hostId = (Long) json.get("hostId");
		this.title = (String) json.get("title");
		this.description = (String) json.get("description");
		this.guestsMayInvite = (Boolean) json.get("guestsMayInvite");
		this.start = (Long) json.get("start");
		this.end = (Long) json.get("end");
		this.displayLocation = (String) json.get("displayLocation");

		this.mapsLocation = (String) json.get("mapsLocation");
		this.tagIds = (ArrayList<Long>) json.get("tagIds");

		/*
		 * Rebuild the array of users that were invitied initially
		 */
		this.invitedUserIds = (ArrayList<Long>) json.get("invitedUserIds");

	}

	/**
	 * Convert this object to a json object
	 * 
	 * @return jsonObject
	 */
	@SuppressWarnings("unchecked")
	public JSONObject toJson() {
		JSONObject obj = new JSONObject();

		obj.put("key", key);
		obj.put("id", key.getId());
		obj.put("created", created == null ? Long.valueOf(-1) : created);
		obj.put("updated", updated == null ? Long.valueOf(-1) : updated);
		obj.put("googleCalendarId",
				googleCalendarId == null ? "googleCalendarId"
						: googleCalendarId);
		obj.put("googleCalendarEventId",
				googleCalendarEventId == null ? "googleCalendarEventId"
						: googleCalendarEventId);
		obj.put("iCalUID", iCalUID == null ? "iCalUID" : iCalUID);
		obj.put("privacy", privacy == null ? "privacy" : privacy.toString());
		obj.put("hostId", hostId == null ? Long.valueOf(-1) : hostId);
		obj.put("title", title == null ? "title" : title);
		obj.put("description", description == null ? "description"
				: description);
		obj.put("guestsMayInvite", guestsMayInvite == null ? false
				: guestsMayInvite);
		obj.put("start", start == null ? Long.valueOf(-1) : start);
		obj.put("end", end == null ? Long.valueOf(-1) : end);
		obj.put("displayLocation", displayLocation == null ? "displayLocation"
				: displayLocation);
		obj.put("mapsLocation", mapsLocation == null ? "mapsLocation"
				: mapsLocation);
		obj.put("tagIds",
				tagIds == null ? (new ArrayList<Long>())
						: tagIds);
		obj.put("invitedUserIds",
				invitedUserIds == null ? (new ArrayList<Long>())
						: invitedUserIds);

		return obj;
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

	public void setHost(ZeppaUser host) {
		this.hostId = host.getId();
	}

	public Float getLatitude() {
		return latitude;
	}

	public void setLatitude(Float latitude) {
		this.latitude = latitude;
	}

	public Float getLongitude() {
		return longitude;
	}

	public void setLongitude(Float longitude) {
		this.longitude = longitude;
	}
	
	
}
