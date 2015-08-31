package com.zeppamobile.common.utils;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

}
