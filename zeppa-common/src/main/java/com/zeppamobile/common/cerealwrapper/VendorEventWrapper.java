package com.zeppamobile.common.cerealwrapper;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

public class VendorEventWrapper implements Comparable<VendorEventWrapper>{

	private Long eventId;
	private Long created;
	private Long updated;
	private String googleCalendarId;
	private String googleCalendarEventId;
	private Long hostId;
	private String title;
	private String description;
	private Long start;
	private Long end;
	private List<Long> tagIds = new ArrayList<Long>();
	private int joinedCount;

	/*
	 * ===== Information relevant to location =====
	 */
	private String displayLocation;

	private String mapsLocation;

	private Float latitude;

	private Float longitude;
	
	public VendorEventWrapper(Long eventId, Long created, Long updated, Long hostId, String title, String description,
			Long start, Long end, List<Long> tagIds, String displayLocation, String mapsLocation) {
		super();
		this.eventId = eventId;
		this.created = created;
		this.updated = updated;
		this.hostId = hostId;
		this.title = title;
		this.description = description;
		this.start = start;
		this.end = end;
		this.tagIds = tagIds;
		this.displayLocation = displayLocation;
		this.mapsLocation = mapsLocation;
	}
	
	/**
	 * Convert this object to a json object
	 * 
	 * @return jsonObject
	 */
	@SuppressWarnings("unchecked")
	public JSONObject toJson() {
		JSONObject obj = new JSONObject();

		obj.put("id", eventId);
		obj.put("created", created == null ? Long.valueOf(-1) : created);
		obj.put("updated", updated == null ? Long.valueOf(-1) : updated);
		obj.put("googleCalendarId",
				googleCalendarId == null ? "googleCalendarId"
						: googleCalendarId);
		obj.put("googleCalendarEventId",
				googleCalendarEventId == null ? "googleCalendarEventId"
						: googleCalendarEventId);
		obj.put("hostId", hostId == null ? Long.valueOf(-1) : hostId);
		obj.put("title", title == null ? "title" : title);
		obj.put("description", description == null ? "description"
				: description);
		obj.put("start", start == null ? Long.valueOf(-1) : start);
		obj.put("end", end == null ? Long.valueOf(-1) : end);
		obj.put("displayLocation", displayLocation == null ? "displayLocation"
				: displayLocation);
		obj.put("mapsLocation", mapsLocation == null ? "mapsLocation"
				: mapsLocation);
		obj.put("tagIds",
				tagIds == null ? (new ArrayList<Long>())
						: tagIds);
		obj.put("joinedCount", end == null ? Long.valueOf(-1) : joinedCount);
		

		return obj;
	}

	// Getters and Setters
	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
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

	public Long getHostId() {
		return hostId;
	}

	public void setHostId(Long hostId) {
		this.hostId = hostId;
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

	public List<Long> getTagIds() {
		return tagIds;
	}

	public void setTagIds(List<Long> tagIds) {
		this.tagIds = tagIds;
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

	public int getJoinedCount() {
		return joinedCount;
	}

	public void setJoinedCount(int joinedCount) {
		this.joinedCount = joinedCount;
	}
	
	@Override
    public int compareTo(VendorEventWrapper another) {
        return this.getStart().compareTo(another.getStart());
    }
}
