package com.zeppamobile.api.endpoint.utils;


import org.json.JSONException;
import org.json.JSONObject;

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
	public static String zeppaNotificationPayload(ZeppaNotification notification) {

		JSONObject json = new JSONObject();

		try {
			json.put("purpose", "zeppaNotification");
			json.put("notificationId", notification.getId().longValue());
			json.put("senderId", notification.getSenderId().longValue());
			String eventIdString = notification.getEventId() == null ? String.valueOf(-1)
					: String.valueOf(notification.getEventId().longValue());
			json.put("eventId", eventIdString);

			json.put("expires", notification.getExpires().longValue());
		} catch (JSONException e) {
			e.printStackTrace();
			return null;

		}

		return json.toString();
	}

	/**
	 * Builds the payload to be delivered so relationship is deleted locally.
	 * 
	 * @param senderId
	 * @param recipientId
	 * @return payload as string
	 */
	public static String silentUserRelationshipDeletedPayload(Long senderId, Long recipientId) {
		JSONObject json = new JSONObject();

		try {
			json.put("purpose", "userRelationshipDeleted");

			json.put("senderId", String.valueOf(senderId.longValue()));
			json.put("recipientId", String.valueOf(recipientId.longValue()));

		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return json.toString();
	}

	/**
	 * Builds the payload to be delivered to user when event is canceled without
	 * notifying
	 * 
	 * @param eventId
	 * @return payload as string
	 */
	public static String silentEventDeletedPayload(Long eventId) {
		JSONObject json = new JSONObject();

		try {
			json.put("purpose", "eventDeleted");
			json.put("eventId", String.valueOf(eventId.longValue()));

		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}

		return json.toString();
	}

}
