package com.zeppamobile.api.datamodel;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class ZeppaNotification {

	public enum NotificationType {
		MINGLE_REQUEST, // 0
		MINGLE_ACCEPTED, // 1
		EVENT_RECOMMENDATION, // 2
		DIRECT_INVITE, // 3
		COMMENT_ON_POST, // 4
		EVENT_CANCELED, // 5
		EVENT_UPDATED, // 6
		USER_JOINED, // 7
		USER_LEAVING, // 8
	}

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	@Persistent
	private Long created;

	@Persistent
	private Long updated;

	@Persistent
	private Long senderId;

	@Persistent
	private Long recipientId;

	@Persistent
	private Long eventId;

	@Persistent
	private Long expires;

	@Persistent
	private NotificationType type;

	@Persistent
	private String extraMessage;

	@Persistent
	private Boolean hasSeen;

	/**
	 * Construct a new Notification that can be persisted and viewed by the user
	 * 
	 * @param senderId
	 * @param recipientId
	 * @param eventId
	 * @param expires
	 * @param type
	 * @param extraMessage
	 * @param hasSeen
	 */
	public ZeppaNotification(Long senderId, Long recipientId, Long eventId,
			Long expires, NotificationType type, String extraMessage,
			Boolean hasSeen) {

		this.created = System.currentTimeMillis();
		this.updated = System.currentTimeMillis();
		this.recipientId = recipientId;
		this.senderId = senderId;
		this.eventId = eventId;
		this.expires = expires;
		this.type = type;
		this.extraMessage = extraMessage;
		this.hasSeen = hasSeen;
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

	public Long getRecipientId() {
		return recipientId;
	}

	public void setRecipientId(Long recipientId) {
		this.recipientId = recipientId;
	}

	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}

	public Long getExpires() {
		return expires;
	}

	public void setExpires(Long expires) {
		this.expires = expires;
	}

	public NotificationType getType() {
		return type;
	}

	public void setType(NotificationType type) {
		this.type = type;
	}

	public String getExtraMessage() {
		return extraMessage;
	}

	public void setExtraMessage(String extraMessage) {
		this.extraMessage = extraMessage;
	}

	public Boolean getHasSeen() {
		return hasSeen;
	}

	public void setHasSeen(Boolean hasSeen) {
		this.hasSeen = hasSeen;
	}

	public Long getSenderId() {
		return senderId;
	}

	public void setSenderId(Long userId) {
		this.senderId = userId;
	}

}
