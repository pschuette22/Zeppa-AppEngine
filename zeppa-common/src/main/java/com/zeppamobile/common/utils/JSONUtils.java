package com.zeppamobile.common.utils;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zeppamobile.common.datamodel.EventTag;
import com.zeppamobile.common.datamodel.EventTagFollow;
import com.zeppamobile.common.datamodel.ZeppaEventToUserRelationship;

public class JSONUtils {

	/**
	 * @Constructor private constructor: utility class
	 */
	private JSONUtils() {
	}

	/**
	 * Decode list of ZeppaEventToUserRelationship objects from http GET response
	 * @param responseString http GET request response string
	 * @return List<ZeppaEventToUserRelationship> relationships encoded into response string
	 * @throws JSONException if there is an error decoding items
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
	 * 
	 * @param responseString
	 * @return
	 * @throws JSONException
	 */
	public static List<EventTag> convertEventTagListString(String responseString) throws JSONException {
		List<EventTag> result = new ArrayList<EventTag>();
		
		
		
		return result;
	}
	
	/**
	 * Convert an HTTP get request response string of Event Tag Follows
	 * @param responseString
	 * @return
	 * @throws JSONException
	 */
	public static List<EventTagFollow> convertTagFollowListString(String responseString) throws JSONException {
		List<EventTagFollow> result = new ArrayList<EventTagFollow>();
		
		return result;
	}
	
}
