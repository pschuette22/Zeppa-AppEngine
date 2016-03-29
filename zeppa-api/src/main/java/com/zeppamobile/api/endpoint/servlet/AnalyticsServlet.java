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

import com.google.appengine.repackaged.org.joda.time.DateTime;
import com.google.appengine.repackaged.org.joda.time.LocalDate;
import com.google.appengine.repackaged.org.joda.time.Years;
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
		String vendorId = req.getParameter(UniversalConstants.PARAM_VENDOR_ID);
		String type = req.getParameter(UniversalConstants.ANALYTICS_TYPE);
		
		if (type != null && type.equals(UniversalConstants.OVERALL_EVENT_DEMOGRAPHICS)) {
			// Get map with the gender counts
			Map<String, Integer> genderCounts = getAllEventInfoDemographic(null, Long.valueOf(vendorId));
			JSONObject json = new JSONObject();
			// put the counts in the JSON object so they can be returned to the frontend in the response
			json.put("maleCount", genderCounts.get("MALE"));
			json.put("femaleCount", genderCounts.get("FEMALE"));
			json.put("unidentified", genderCounts.get("UNIDENTIFIED"));
			
			Map<String, Integer> ageCounts = getAgeCountAllEvents(null, Long.valueOf(vendorId));
			JSONObject ageJson = new JSONObject();
			ageJson.put("under18", ageCounts.get("under18"));
			ageJson.put("18to20", ageCounts.get("18to20"));
			ageJson.put("21to24", ageCounts.get("21to24"));
			ageJson.put("25to29", ageCounts.get("25to29"));
			ageJson.put("30to39", ageCounts.get("30to39"));
			ageJson.put("40to49", ageCounts.get("40to49"));
			ageJson.put("50to59", ageCounts.get("50to59"));
			ageJson.put("over60", ageCounts.get("over60"));
			
			resp.setStatus(HttpServletResponse.SC_OK);
			JSONArray respArray = new JSONArray();
			respArray.add(json);
			respArray.add(ageJson);
			resp.getWriter().write(respArray.toJSONString());
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
		} else if (type != null && type.equals(UniversalConstants.OVERALL_EVENT_POPULAR_EVENTS)) {
			// Get the most popular events and return their title and count in JSON
			Map<VendorEvent, Integer> eventCounts = getAllEventPopularEvents(null, Long.valueOf(vendorId));
			JSONObject jsonPopEvents = new JSONObject();
			for(Entry<VendorEvent, Integer> entry : eventCounts.entrySet()) {
				jsonPopEvents.put(entry.getKey().getTitle(), entry.getValue());
			}
			resp.getWriter().write(jsonPopEvents.toJSONString());
		} else if (type != null && type.equals(UniversalConstants.OVERALL_EVENT_POPULAR_DAYS)) {
			Map<Integer, Integer> dayCounts = getAllEventPopularDay(null, Long.valueOf(vendorId));
			JSONObject days = new JSONObject();
			days.put("Monday", dayCounts.get(1));
			days.put("Tuesday", dayCounts.get(2));
			days.put("Wednesday", dayCounts.get(3));
			days.put("Thursday", dayCounts.get(4));
			days.put("Friday", dayCounts.get(5));
			days.put("Saturday", dayCounts.get(6));
			days.put("Sunday", dayCounts.get(7));
			resp.getWriter().write(days.toJSONString());
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
	
	private Map<Integer, Integer> getAllEventPopularDay(FilterCerealWrapper filter, long vendorId) {
		Map<Integer, Integer> dayCounts = new HashMap<Integer, Integer>();
		// Initialize each day of week to have a 0 count
		for(int i=1; i < 8; i++) {
			dayCounts.put(i, 0);
		}
		
		List<VendorEvent> events = VendorEventServlet.getAllEvents(vendorId);
		// Populate the map with the counts for each day
		for(VendorEvent event : events) {
			DateTime dt = new DateTime(event.getStart());
			List<VendorEventRelationship> relationships = VendorEventRelationshipServlet.getAllJoinedRelationshipsForEvent(event.getId());
			dayCounts.put(dt.getDayOfWeek(), (dayCounts.get(dt.getDayOfWeek()) + relationships.size()));
			System.out.println("EVENT: "+event.getTitle()+", DAY: "+dt.getDayOfWeek()+", j: "+relationships.size()+", n: "+VendorEventRelationshipServlet.getAllRelationshipsForEvent(event.getId()).size());
		}
		
		return dayCounts;
	}
	
	private Map<VendorEvent, Integer> getAllEventPopularEvents(FilterCerealWrapper filter, long vendorId) {
		List<VendorEvent> events = VendorEventServlet.getAllEvents(vendorId);
		Map<VendorEvent, Integer> eventCounts = new HashMap<VendorEvent, Integer>();
		// Populate the map with the counts for each event
		for(VendorEvent event : events) {
			List<VendorEventRelationship> relationships = VendorEventRelationshipServlet.getAllJoinedRelationshipsForEvent(event.getId());
			eventCounts.put(event, relationships.size());
		}
		
		// If there are no more than 5 events then return all of them
		if(eventCounts.size() <= 5) {
			return eventCounts;
		}
		
		Map<VendorEvent, Integer> maxCounts = new HashMap<VendorEvent, Integer>();
		for(int i=0; i < 5; i++) {
			Entry<VendorEvent, Integer> max = null;
			// find the maximum count
			for(Entry<VendorEvent, Integer> entry : eventCounts.entrySet()) {
				if(max == null || entry.getValue() > max.getValue()) {
					max = entry;
				}
			}
			// add the max to the return map and remove it from the eventCounts map so the next max can be found
			maxCounts.put(max.getKey(), max.getValue());
			eventCounts.remove(max.getKey());
		}
		
		return maxCounts;
		
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
	

	private Map<String, Integer> getAgeCountAllEvents(FilterCerealWrapper filter, Long vendorId) {
		Map<String, Integer> ret = new HashMap<String, Integer>();
		int under18 = 0;
		int age18to20 = 0;
		int age21to24 = 0;
		int age25to29 = 0;
		int age30to39 = 0;
		int age40to49 = 0;
		int age50to59 = 0;
		int over60 = 0;
		
		// Get all events for the vendor
		List<VendorEvent> events = VendorEventServlet.getAllEvents(vendorId);
		List<VendorEventRelationship> relationships = new ArrayList<VendorEventRelationship>();

		// Go through each event for the vendor and find all relationships
		for (VendorEvent event : events) {
			relationships.addAll(VendorEventRelationshipServlet.getAllRelationshipsForEvent(event.getId()));
		}
		
		// Loop through all relationships
		for (VendorEventRelationship rel : relationships) {
			// Find the user
			Long id = rel.getUserId();
			ZeppaUser user = ZeppaUserServlet.getUser(id);
			// Get the user's date of birth
			LocalDate dob = new LocalDate(user.getUserInfo().getDateOfBirth());
			VendorEvent event = VendorEventServlet.getIndividualEvent(String.valueOf(rel.getEventId()));
			LocalDate eventTime = new LocalDate(event.getStart());
			Years age = Years.yearsBetween(dob, eventTime);
			// Find out the age range the user falls into and increment the 
			if(age.getYears() < 18) {
				under18++;
			} else if(age.getYears() >= 18 && age.getYears() <= 24) {
				age21to24++;
			} else if(age.getYears() >= 25 && age.getYears() <= 29) {
				age25to29++;
			} else if(age.getYears() >= 30 && age.getYears() <= 39) {
				age30to39++;
			} else if(age.getYears() >= 40 && age.getYears() <= 49) {
				age40to49++;
			} else if(age.getYears() >= 50 && age.getYears() <= 59) {
				age50to59++;
			} else {
				over60++;
			}
		}
		
		ret.put("under18", under18);
		ret.put("18to20", age18to20);
		ret.put("21to24", age21to24);
		ret.put("25to29", age25to29);
		ret.put("30to39", age30to39);
		ret.put("40to49", age40to49);
		ret.put("50to59", age50to59);
		ret.put("over60", over60);
		return ret;
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
			// Get the user and their initial tags
			ZeppaUser user = ZeppaUserServlet.getUser(rel.getUserId());
			List<String> userTags = new ArrayList<String>();
			if(user.getInitialTags() != null && user.getInitialTags().size() > 0)
				userTags.addAll(user.getInitialTags());
			
			// Get all tags followed by the user and add them to the list
			List<EventTag> tags = EventTagServlet.getAllUserTags(user.getId());
			for(EventTag tag : tags) {
				userTags.add(tag.getTagText());
			}
			if(userTags != null && !userTags.isEmpty()) {
				for(String tagText : userTags) {
					// Check if the user's tag was used in an event for this vendor
					if(eventTags.contains(tagText)) {
						int curVal;
						// If the the tag was followed and is already in the hashmap then increment the count for that tag
					    if (tagHash.containsKey(tagText)) {
					        curVal = tagHash.get(tagText);
					        tagHash.put(tagText, curVal + 1);
					    } else {
					    	// If the tag was followed but isn't already in the hashmap then set count to 1
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
