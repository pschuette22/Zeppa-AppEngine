package com.zeppamobile.common.utils;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zeppamobile.common.datamodel.EventTag;
import com.zeppamobile.common.datamodel.EventTagFollow;
import com.zeppamobile.common.datamodel.ZeppaEvent;
import com.zeppamobile.common.datamodel.ZeppaEventToUserRelationship;
import com.zeppamobile.common.datamodel.ZeppaUserToUserRelationship;

public class JSONUtils {

	/**
	 * @Constructor private constructor: utility class
	 */
	private JSONUtils() {
	}

	/**
	 * Decode list of ZeppaEventToUserRelationship objects from http GET
	 * response
	 * 
	 * @param responseString
	 *            http GET request response string
	 * @return List<ZeppaEventToUserRelationship> relationships encoded into
	 *         response string
	 * @throws JSONException
	 *             if there is an error decoding items
	 */
	public static List<ZeppaEventToUserRelationship> convertEventRelationshipListString(
			String responseString) throws JSONException {
		List<ZeppaEventToUserRelationship> result = new ArrayList<ZeppaEventToUserRelationship>();

		JSONArray array = new JSONArray(responseString);

		// Iterate through list of
		for (int i = 0; i < array.length(); i++) {
			if (array.isNull(i)) {
				break;
			}

			JSONObject json = array.getJSONObject(i);
			ZeppaEventToUserRelationship relationship = new ZeppaEventToUserRelationship(
					json);
			result.add(relationship);

		}

		return result;
	}

	/**
	 * Decode list of ZeppaEvent objects from HTTP Get Request response
	 * 
	 * @param responseString
	 *            from get request
	 * @return List<ZeppaEvent>
	 * @throws JSONException
	 *             if improperly encoded
	 */
	public static List<ZeppaEvent> convertEventListString(String responseString)
			throws JSONException {
		List<ZeppaEvent> result = new ArrayList<ZeppaEvent>();

		JSONArray array = new JSONArray(responseString);

		// Iterate through list of
		for (int i = 0; i < array.length(); i++) {
			if (array.isNull(i)) {
				break;
			}

			JSONObject json = array.getJSONObject(i);
			ZeppaEvent event = new ZeppaEvent(json);
			result.add(event);

		}

		return result;
	}

	/**
	 * 
	 * @param responseString
	 * @return
	 * @throws JSONException
	 */
	public static List<EventTag> convertEventTagListString(String responseString)
			throws JSONException {
		List<EventTag> result = new ArrayList<EventTag>();

		JSONArray array = new JSONArray(responseString);

		// Iterate through list of
		for (int i = 0; i < array.length(); i++) {
			if (array.isNull(i)) {
				break;
			}

			JSONObject json = array.getJSONObject(i);
			EventTag tag = new EventTag(json);
			result.add(tag);

		}

		return result;
	}

	/**
	 * Convert an HTTP get request response string of Event Tag Follows
	 * 
	 * @param responseString
	 * @return
	 * @throws JSONException
	 */
	public static List<EventTagFollow> convertTagFollowListString(
			String responseString) throws JSONException {
		List<EventTagFollow> result = new ArrayList<EventTagFollow>();

		JSONArray array = new JSONArray(responseString);

		// Iterate through list of
		for (int i = 0; i < array.length(); i++) {
			if (array.isNull(i)) {
				break;
			}

			JSONObject json = array.getJSONObject(i);
			EventTagFollow follow = new EventTagFollow(json);
			result.add(follow);

		}

		return result;
	}

	/**
	 * Convert a list of EventTagFollow objects to json array
	 * @param follows
	 * @return
	 * @throws JSONException
	 */
	public static JSONArray convertTagFollowListToJson(
			List<EventTagFollow> follows) throws JSONException {
		JSONArray result = new JSONArray();

		for (EventTagFollow follow : follows) {
			JSONObject json = new JSONObject(follow);
			result.put(json);
		}

		return result;
	}

	/**
	 * Convert an HTTP get request response string of
	 * ZeppaUserToUserRelationship objects
	 * 
	 * @param responseString
	 * @return List<ZeppaUserToUserRelationship>
	 * @throws JSONException
	 */
	public static List<ZeppaUserToUserRelationship> convertUserRelationshipListString(
			String responseString) throws JSONException {
		List<ZeppaUserToUserRelationship> result = new ArrayList<ZeppaUserToUserRelationship>();

		JSONArray array = new JSONArray(responseString);

		// Iterate through list of
		for (int i = 0; i < array.length(); i++) {
			if (array.isNull(i)) {
				break;
			}

			JSONObject json = array.getJSONObject(i);
			ZeppaUserToUserRelationship follow = new ZeppaUserToUserRelationship(
					json);
			result.add(follow);

		}

		return result;
	}

}
