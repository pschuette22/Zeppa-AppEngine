package com.zeppamobile.common.datamodel;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.json.simple.JSONObject;

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

	/*
	 * Entity relationships
	 */
	@Persistent
	private ZeppaUser host;

	@Persistent(mappedBy = "event")
	@Element(dependent = "true")
	private List<ZeppaEventToUserRelationship> attendeeRelationships = new ArrayList<ZeppaEventToUserRelationship>();

	@Persistent(mappedBy = "event")
	@Element(dependent = "true")
	private List<EventComment> comments = new ArrayList<EventComment>();

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

	public ZeppaUser getHost() {
		return host;
	}

	public void setHost(ZeppaUser host) {
		this.host = host;
	}

	public List<ZeppaEventToUserRelationship> getAttendeeRelationships() {
		return attendeeRelationships;
	}

	public void setAttendeeRelationships(
			List<ZeppaEventToUserRelationship> attendeeRelationships) {
		this.attendeeRelationships = attendeeRelationships;
	}
	
	public boolean addAttendeeRelationship(ZeppaEventToUserRelationship relationship){
		return this.attendeeRelationships.add(relationship);
	}

	public List<EventComment> getComments() {
		return comments;
	}

	public void setComments(List<EventComment> comments) {
		this.comments = comments;
	}
	
	
	public boolean addComment(EventComment comment){
		return this.comments.add(comment);
	}
	
	
	

}
