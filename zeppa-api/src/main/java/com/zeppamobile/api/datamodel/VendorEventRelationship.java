package com.zeppamobile.api.datamodel;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.json.simple.JSONObject;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class VendorEventRelationship {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	@Persistent
	private Long userId;

	@Persistent
	private Long eventId;

	@Persistent
	private boolean joined;

	@Persistent
	private boolean seen;

	@Persistent
	private boolean watched;

	@Persistent
	private boolean shared;

	@Persistent
	private List<Long> shareIds;

	public VendorEventRelationship(Long userId, Long eventId, boolean joined, boolean seen, boolean watched,
			boolean shared, List<Long> shareIds) {
		super();
		this.userId = userId;
		this.eventId = eventId;
		this.joined = joined;
		this.seen = seen;
		this.watched = watched;
		this.shared = shared;
		this.shareIds = shareIds;
	}

	public Long getId() {
		return key.getId();
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}

	public boolean isJoined() {
		return joined;
	}

	public void setJoined(boolean joined) {
		this.joined = joined;
	}

	public boolean isSeen() {
		return seen;
	}

	public void setSeen(boolean seen) {
		this.seen = seen;
	}

	public boolean isWatched() {
		return watched;
	}

	public void setWatched(boolean watched) {
		this.watched = watched;
	}

	public boolean isShared() {
		return shared;
	}

	public void setShared(boolean shared) {
		this.shared = shared;
	}

	public List<Long> getShareIds() {
		return shareIds;
	}

	public void setShareIds(List<Long> shareIds) {
		this.shareIds = shareIds;
	}
	
	

	/**
	 * Convert this object to a json object
	 * 
	 * @return jsonObject
	 */
	@SuppressWarnings("unchecked")
	public JSONObject toJson() {
		JSONObject obj = new JSONObject();

		obj.put("id", key.getId());
		obj.put("userId", userId == null ? Long.valueOf(-1) : userId);
		obj.put("eventId", eventId == null ? Long.valueOf(-1) : eventId);

		obj.put("joined", joined);
		obj.put("seen", seen);
		obj.put("watched", watched);
		obj.put("shared", shared);
		obj.put("shareIds", shareIds == null ? (new ArrayList<Long>()) : shareIds);

		return obj;
	}

}
