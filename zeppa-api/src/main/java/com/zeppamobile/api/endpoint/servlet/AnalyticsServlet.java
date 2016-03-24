package com.zeppamobile.api.endpoint.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.zeppamobile.api.PMF;
import com.zeppamobile.api.datamodel.EventTag;
import com.zeppamobile.api.datamodel.VendorEvent;
import com.zeppamobile.api.datamodel.VendorEventRelationship;
import com.zeppamobile.api.datamodel.ZeppaUser;
import com.zeppamobile.api.datamodel.ZeppaUserInfo;
import com.zeppamobile.common.UniversalConstants;
import com.zeppamobile.common.cerealwrapper.FilterCerealWrapper;

/**
 * 
 * @author Pete Schuette
 * 
 * Servlet class for interacting with analytics
 *
 */
public class AnalyticsServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO get all gender counts and return JSON in the resp
		String vendorId = req.getParameter(UniversalConstants.PARAM_VENDOR_ID);
		String type = req.getParameter(UniversalConstants.ANALYTICS_TYPE);
		
		if (type != null && type.equals(UniversalConstants.OVERALL_EVENT_DEMOGRAPHICS)) {
			// Get map with the gender counts
			Map<String, Integer> counts = getAllEventInfoDemographic(null, Long.valueOf(vendorId));
			JSONObject json = new JSONObject();
			// put the counts in the JSON object so they can be returned to the frontend in the response
			json.put("maleCount", counts.get("MALE"));
			json.put("femaleCount", counts.get("FEMALE"));
			json.put("unidentified", counts.get("UNIDENTIFIED"));
			System.out.println("--------"+json.toJSONString()+"--------");
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.getWriter().write(json.toJSONString());
		} else if (type != null && type.equals(UniversalConstants.OVERALL_EVENT_TAGS)) {
			// Get all of the tags that brought users to events
			Map<String, Integer> tags = getAllEventTags(null, Long.valueOf(vendorId));
			// Sort the map by the number of followers
			tags = sortMapByValue(tags);
			JSONObject json = new JSONObject();
			int count = 0;
			for(Entry<String, Integer> entry : tags.entrySet()) {
				if(count < 5) {
					json.put(entry.getKey(), entry.getValue());
					count++;
				} else {
					break;
				}
			}
			resp.setStatus(HttpServletResponse.SC_OK);
			System.out.println("-------"+json.toJSONString()+"-------");
			resp.getWriter().write(json.toJSONString());
		}
		
	}
	
	/**
	 * Gets the demographic gender info for all events for a vendor
	 * @param filter - filter information given by the user
	 * @param vendorId - the id of the vendor
	 * @return - a map with gender and corresponding counts as the entries
	 */
	private Map<String, Integer> getAllEventInfoDemographic(FilterCerealWrapper filter, long vendorId) {
		Map<String, Integer> allEventGender = new HashMap<String, Integer>();
		// Get all events for the vendor
		List<VendorEvent> events = VendorEventServlet.getAllEvents(vendorId);
		List<VendorEventRelationship> rels = new ArrayList<VendorEventRelationship>();
		
		// Go through each event for the vendor and find all relationships
		for(VendorEvent event : events) {
			rels.addAll(VendorEventRelationshipServlet.getAllRelationshipsForEvent(event.getId()));
		}
		
		// Get the gender count across all events for the vendor
		allEventGender = getGenderCountAllEvents(rels);
		return allEventGender;
	}
	
	/**
	 * Gets all of the info needed for the all events tag demographics
	 * @param filter - the filter information entered by the user
	 * @param vendorId - the id of the current vendor
	 * @return 
	 */
	private Map<String, Integer> getAllEventTags(FilterCerealWrapper filter, long vendorId) {
		HashMap<String, Integer> tagsHash = new HashMap<String, Integer>();
		// First get all events for the vendor
		List<VendorEvent> events = VendorEventServlet.getAllEvents(vendorId);
		// Loop through each event for the vendor
		for(VendorEvent event : events) {
			// Create a list that will store all of the tags that were used for the event
			List<String> tagList = new ArrayList<>();
			for(Long id : event.getTagIds()) {
				// Get the tag text from each tag
				EventTag tag = null;
				PersistenceManager mgr = getPersistenceManager();
				try {
					tag = mgr.getObjectById(EventTag.class, id);
					// If the tag hasn't been seen yet add it to the list
					if(!tagList.contains(tag.getTagText())) {
						tagList.add(tag.getTagText());
					}
				} finally {
					mgr.close();
				}
			}
			// This hash map contains all tag texts that are common 
			// between one of the vendor's events and a user with a relationship to it
			tagsHash.putAll(getRelatedUserTagInfo(tagList, event.getId()));
		}
		
		return tagsHash;
	}
	

	private String getAllEventPopularDay(FilterCerealWrapper filter, long vendorId) {
		return "";
	}
	
	private String getAllEventPopularEvents(FilterCerealWrapper filter, long vendorId) {
		return "";
	}
	
	private String getIndividualEventInfoTags(FilterCerealWrapper filter, long vendorId) {
		return "";
	}
	
	private String getIndividualEventInfoDemographics(FilterCerealWrapper filter, long vendorId) {
		return "";
	}
	
	/**
	 * Sorts a map by value
	 * @param map - the map to be sorted
	 * @return - the sorted map
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Map<String, Integer> sortMapByValue(Map<String, Integer> map) {
	     List<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>(map.entrySet());
	     Collections.sort(list, new Comparator() {
	          public int compare(Object o1, Object o2) {
	               return ((Comparable) ((Map.Entry) (o1)).getValue())
	              .compareTo(((Map.Entry) (o2)).getValue());
	          }
	     });

	    Map<String, Integer> result = new HashMap<String, Integer>();
	    for (Iterator it = list.iterator(); it.hasNext();) {
	        Map.Entry entry = (Map.Entry)it.next();
	        result.put((String)entry.getKey(), (Integer)entry.getValue());
	    }
	    return result;
	} 
	
	
	
	/** 
	 * Take the results from getting all related Users and count the gender
	 * demographic info for the users
	 * @param relationships - all event relationships for the vendor's events
	 * @return - a map with gender and corresponding counts as the entries
	 */
	private Map<String, Integer> getGenderCountAllEvents(List<VendorEventRelationship> relationships) {
		Map<String, Integer> ret = new HashMap<String, Integer>();
		int maleCount = 0;
		int femaleCount = 0;
		int unidentifiedCount = 0;

		// Loop through all relationships
		for (VendorEventRelationship rel : relationships) {
			// Find the user
			Long id = rel.getUserId();
			ZeppaUser user = ZeppaUserServlet.getUser(id);
			// Check their gender and increment the appropriate counter
			if (user.getUserInfo().getGender() != null
					&& user.getUserInfo().getGender().equals(ZeppaUserInfo.Gender.MALE)) {
				maleCount++;
			} else if ((user.getUserInfo().getGender() != null
					&& user.getUserInfo().getGender().equals(ZeppaUserInfo.Gender.FEMALE))) {
				femaleCount++;
			} else {
				unidentifiedCount++;
			}
		}
		ret.put("MALE", maleCount);
		ret.put("FEMALE", femaleCount);
		ret.put("UNIDENTIFIED", unidentifiedCount);
		
		return ret;
	}
	
	
	
	/**
	 * Get the number of attendees of the given event
	 * who follow each tag associated with the event
	 * @param eventTags - the text of the tags associated with the event
	 * @param eventId - the id of the event
	 * @return - hash map with the key being tag text and the value 
	 * being the number of attendees who follow the tag
	 */
	private static HashMap<String, Integer> getRelatedUserTagInfo(List<String> eventTags, Long eventId) {
		// get all users with a relationship to an event
		List<VendorEventRelationship> relationships = VendorEventRelationshipServlet.getAllRelationshipsForEvent(eventId);
		HashMap<String, Integer> tagHash = new HashMap<String, Integer>();
		for(VendorEventRelationship rel : relationships) {
			// Get the user and their tags
			ZeppaUser user = ZeppaUserServlet.getUser(rel.getUserId());
			List<String> userTags = user.getInitialTags();
			if(userTags != null && !userTags.isEmpty()) {
				for(String tagText : userTags) {
					// Check if the user's tag was used in an event for this vendor
					if(eventTags.contains(tagText)) {
						int curVal;
						// If the the tag was used and is already in the hashmap then increment the count for that tag
					    if (tagHash.containsKey(tagText)) {
					        curVal = tagHash.get(tagText);
					        tagHash.put(tagText, curVal + 1);
					    } else {
					    	// If the tag was used but isn't already in the hashmap then set count to 1
					    	tagHash.put(tagText, 1);
					    }
					}
				}
			}
		}
		
		return tagHash;
	}
	
	/**
	 * Get the persistence manager for interacting with datastore
	 */
	private static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}

}
