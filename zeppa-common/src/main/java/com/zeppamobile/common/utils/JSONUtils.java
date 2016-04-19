package com.zeppamobile.common.utils;

import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class JSONUtils {

	/**
	 * @Constructor private constructor: utility class
	 */
	private JSONUtils() {
	}

	/**
	 * Decode a json List of strings
	 * 
	 * @param jsonString
	 * @return List<String> of deco
	 */
	@SuppressWarnings("unchecked")
	public static List<String> decodeListString(String jsonString) {
		try {
			return (List<String>) JSONValue.parse(jsonString);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Convenience Method to parse string into JSON object
	 * @param jsonString
	 * @return
	 */
	public static JSONObject parseJson(String jsonString) {
		JSONObject json = (JSONObject) JSONValue.parse(jsonString);
		return json;
	}

	// /**
	// * Decode list of ZeppaEventToUserRelationship objects from http GET
	// * response
	// *
	// * @param responseString
	// * http GET request response string
	// * @return List<ZeppaEventToUserRelationship> relationships encoded into
	// * response string
	// * @throws JSONException
	// * if there is an error decoding items
	// */
	// public static List<ZeppaEventToUserRelationship>
	// convertEventRelationshipListString(
	// String responseString) {
	// List<ZeppaEventToUserRelationship> result = new
	// ArrayList<ZeppaEventToUserRelationship>();
	//
	// JSONArray array = (JSONArray) JSONValue.parse(responseString);
	//
	// // Iterate through list of
	// for (int i = 0; i < array.size(); i++) {
	//
	// JSONObject json = (JSONObject) array.get(i);
	// ZeppaEventToUserRelationship relationship = new
	// ZeppaEventToUserRelationship(
	// json);
	// result.add(relationship);
	//
	// }
	//
	// return result;
	// }
	//
	// /**
	// * Decode list of ZeppaEvent objects from HTTP Get Request response
	// *
	// * @param responseString
	// * from get request
	// * @return List<ZeppaEvent>
	// * @throws JSONException
	// * if improperly encoded
	// */
	// public static List<ZeppaEvent> convertEventListString(String
	// responseString) {
	// List<ZeppaEvent> result = new ArrayList<ZeppaEvent>();
	//
	// JSONArray array = (JSONArray) JSONValue.parse(responseString);
	//
	// // Iterate through list of
	// for (int i = 0; i < array.size(); i++) {
	//
	// JSONObject json = (JSONObject) array.get(i);
	// ZeppaEvent event = new ZeppaEvent(json);
	// result.add(event);
	//
	// }
	//
	// return result;
	// }
	//
	// /**
	// * Convert an HTTP get request response string of Event Tag Follows
	// *
	// * @param responseString
	// * @return
	// * @throws JSONException
	// */
	// public static List<EventTag> convertEventTagListString(String
	// responseString) {
	// List<EventTag> result = new ArrayList<EventTag>();
	//
	// JSONArray array = (JSONArray) JSONValue.parse(responseString);
	//
	// // Iterate through list of
	// for (int i = 0; i < array.size(); i++) {
	//
	// JSONObject json = (JSONObject) array.get(i);
	// EventTag tag = new EventTag(json);
	// result.add(tag);
	//
	// }
	//
	// return result;
	// }
	//
	// /**
	// * Convert an HTTP get request response string of Event Tag Follows
	// *
	// * @param responseString
	// * @return
	// * @throws JSONException
	// */
	// public static List<EventTagFollow> convertTagFollowListString(
	// String responseString) {
	// List<EventTagFollow> result = new ArrayList<EventTagFollow>();
	//
	// JSONArray array = (JSONArray) JSONValue.parse(responseString);
	//
	// // Iterate through list of
	// for (int i = 0; i < array.size(); i++) {
	//
	// JSONObject json = (JSONObject) array.get(i);
	// EventTagFollow follow = new EventTagFollow(json);
	// result.add(follow);
	//
	// }
	//
	// return result;
	// }
	//
	// /**
	// * Convert a list of EventTagFollow objects to json array
	// *
	// * @param follows
	// * @return
	// * @throws JSONException
	// */
	// @SuppressWarnings("unchecked")
	// public static JSONArray convertTagFollowListToJson(
	// List<EventTagFollow> follows) {
	// JSONArray result = new JSONArray();
	//
	// for (EventTagFollow follow : follows) {
	// result.add(follow.toJson());
	// }
	//
	// return result;
	// }
	//
	// /**
	// * Convert an HTTP get request response string of
	// * ZeppaUserToUserRelationship objects
	// *
	// * @param responseString
	// * @return List<ZeppaUserToUserRelationship>
	// * @throws JSONException
	// */
	// public static List<ZeppaUserToUserRelationship>
	// convertUserRelationshipListString(
	// String responseString) {
	// List<ZeppaUserToUserRelationship> result = new
	// ArrayList<ZeppaUserToUserRelationship>();
	//
	// JSONArray array = (JSONArray) JSONValue.parse(responseString);
	//
	// // Iterate through list of
	// for (int i = 0; i < array.size(); i++) {
	//
	// JSONObject json = (JSONObject) array.get(i);
	// ZeppaUserToUserRelationship follow = new ZeppaUserToUserRelationship(
	// json);
	// result.add(follow);
	//
	// }
	//
	// return result;
	// }

}
