package com.zeppamobile.api.notifications;


import org.json.simple.JSONObject;

import com.zeppamobile.api.datamodel.ZeppaNotification;

/**
 * 
 * @author DrunkWithFunk21
 * 
 */
public class PayloadBuilder {

	/**
	 * @constructor hidden constructor
	 */
	private PayloadBuilder() {
	}

	/**
	 * Builds the payload to be delivered to the device so it is processed
	 * properly.
	 * 
	 * @param notification
	 * @return payload as deliverable string
	 */
	@SuppressWarnings("unchecked")
	public static String zeppaNotificationPayload(ZeppaNotification notification) {

		JSONObject json = new JSONObject();

		json.put("purpose", "zeppaNotification");
		json.put("id", notification.getId().longValue());
		json.put("title", notification.getTitle());
		json.put("message", notification.getMessage());
		json.put("type", notification.getType().name());
		json.put("expires", notification.getExpires().longValue());
		json.put("senderId", notification.getSenderId());
		json.put("eventId", notification.getEventId()>0?notification.getEventId():Long.valueOf(-1));

		return json.toString();
	}

	/**
	 * Builds the payload to be delivered so relationship is deleted locally.
	 * 
	 * @param senderId
	 * @param recipientId
	 * @return payload as string
	 */
	@SuppressWarnings("unchecked")
	public static String silentUserRelationshipDeletedPayload(Long senderId, Long recipientId) {
		JSONObject json = new JSONObject();

		json.put("purpose", "userRelationshipDeleted");

		json.put("senderId", String.valueOf(senderId.longValue()));
		json.put("recipientId", String.valueOf(recipientId.longValue()));
		return json.toString();
	}

	/**
	 * Builds the payload to be delivered to user when event is canceled without
	 * notifying
	 * 
	 * @param eventId
	 * @return payload as string
	 */
	@SuppressWarnings("unchecked")
	public static String silentEventDeletedPayload(Long eventId) {
		JSONObject json = new JSONObject();

		json.put("purpose", "eventDeleted");
		json.put("eventId", String.valueOf(eventId.longValue()));

		return json.toString();
	}

}
