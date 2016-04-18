package com.zeppamobile.api.endpoint.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.appengine.repackaged.org.joda.time.LocalDate;
import com.google.appengine.repackaged.org.joda.time.Years;
import com.zeppamobile.api.datamodel.EventTag;
import com.zeppamobile.api.datamodel.VendorEvent;
import com.zeppamobile.api.datamodel.VendorEventRelationship;
import com.zeppamobile.api.datamodel.ZeppaUser;
import com.zeppamobile.common.UniversalConstants;
import com.zeppamobile.common.cerealwrapper.AnalyticsDataWrapper;
import com.zeppamobile.common.cerealwrapper.FilterCerealWrapper;
import com.zeppamobile.common.cerealwrapper.FilterCerealWrapper.Gender;

/**
 * Servlet implementation class IndividualEventAnalyticsServlet
 */
public class IndividualEventAnalyticsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String eventId = request.getParameter(UniversalConstants.PARAM_EVENT_ID);
		String type = request.getParameter(UniversalConstants.ANALYTICS_TYPE);
		FilterCerealWrapper filter = getFilterInfo(request);
		if (type != null && type.equals(UniversalConstants.INDIV_EVENT_DEMOGRAPHICS)) {
			// Get map with the gender counts
			System.out.println("Getting gender counts");
			Map<String, Integer> genderCounts = getEventGenderInfo(filter, Long.valueOf(eventId));
			JSONObject json = new JSONObject();
			// put the counts in the JSON object so they can be returned to the
			// frontend in the response
			json.put("maleCount", genderCounts.get("MALE"));
			json.put("femaleCount", genderCounts.get("FEMALE"));
			json.put("unidentified", genderCounts.get("UNIDENTIFIED"));
			
			System.out.println("Getting age counts");
			Map<String, Integer> ageCounts = getEventAgeCount(filter, Long.valueOf(eventId));
			JSONObject ageJson = new JSONObject();
			ageJson.put("under18", ageCounts.get("under18"));
			ageJson.put("18to20", ageCounts.get("18to20"));
			ageJson.put("21to24", ageCounts.get("21to24"));
			ageJson.put("25to29", ageCounts.get("25to29"));
			ageJson.put("30to39", ageCounts.get("30to39"));
			ageJson.put("40to49", ageCounts.get("40to49"));
			ageJson.put("50to59", ageCounts.get("50to59"));
			ageJson.put("over60", ageCounts.get("over60"));
			
			response.setStatus(HttpServletResponse.SC_OK);
			JSONArray respArray = new JSONArray();
			respArray.add(json);
			respArray.add(ageJson);
			response.getWriter().write(respArray.toJSONString());
		} else if (type != null && type.equals(UniversalConstants.INDIV_EVENT_TAGS)) {
			// Get all of the tags that brought users to events
			System.out.println("Getting tag counts");
			List<AnalyticsDataWrapper> tags = getAllEventTags(filter, Long.valueOf(eventId), true);
			JSONObject json = new JSONObject();
			for (AnalyticsDataWrapper adw : tags) {
				json.put(adw.getKey(), adw.getValue());
			}
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(json.toJSONString());
		} else if (type != null && type.equals(UniversalConstants.INDIV_EVENT_TAGS_WATCHED)) {
			// Get all of the tags that brought users to events
			System.out.println("Getting watched counts");
			List<AnalyticsDataWrapper> tags = getAllEventTags(filter, Long.valueOf(eventId), false);
			JSONObject json = new JSONObject();
			for (AnalyticsDataWrapper adw : tags) {
				json.put(adw.getKey(), adw.getValue());
			}
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(json.toJSONString());
		}
	}
	
	/**
	 * Gets the demographic gender info for the given event
	 * @param filter - filter information given by the user
	 * @param eventId - the id of the vendor
	 * @return - a map with gender and corresponding counts as the entries
	 */
	private Map<String, Integer> getEventGenderInfo(FilterCerealWrapper filter, long eventId) {
		Map<String, Integer> allEventGender = new HashMap<String, Integer>();
		// Get all events for the vendor
		VendorEvent event = VendorEventServlet.getIndividualEvent(String.valueOf(eventId));
		List<VendorEventRelationship> rels = VendorEventRelationshipServlet.getAllJoinedRelationshipsForEvent(event.getId(), filter);
		System.out.println("COUNT: "+rels.size());
		
		// Get the gender count across all events for the vendor
		allEventGender = AnalyticsServlet.getGenderCountAllEvents(rels);
		return allEventGender;
	}
	
	/**
	 * Get the age counts for the attendees of the given event
	 * @param filter - filter info given by the user 
	 * @param eventId - the id of the event
	 * @return - map with age ranges and corresponding counts
	 */
	private Map<String, Integer> getEventAgeCount(FilterCerealWrapper filter, Long eventId) {
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
		VendorEvent event = VendorEventServlet.getIndividualEvent(String.valueOf(eventId));
		List<VendorEventRelationship> relationships = VendorEventRelationshipServlet.getAllJoinedRelationshipsForEvent(event.getId(), filter);
		
		// Loop through all relationships
		for (VendorEventRelationship rel : relationships) {
			// Find the user
			Long id = rel.getUserId();
			ZeppaUser user = ZeppaUserServlet.getUser(id);
			// Get the user's date of birth
			LocalDate dob = new LocalDate(user.getUserInfo().getDateOfBirth());
			VendorEvent eventTemp = VendorEventServlet.getIndividualEvent(String.valueOf(rel.getEventId()));
			LocalDate eventTime = new LocalDate(eventTemp.getStart());
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
	 * Generate the filter object from the parameters given in the request
	 * @param params - params that contain the filter info
	 * @return - a filter object with the correct params
	 */
	private FilterCerealWrapper getFilterInfo(HttpServletRequest req) {
		// get the gender filter as an enum
		String gender = req.getParameter(UniversalConstants.GENDER_FILTER);
		Gender g = Gender.ALL;
		if (gender != null) {
			if (gender.equalsIgnoreCase("male")) {
				g = Gender.MALE;
			} else if (gender.equalsIgnoreCase("female")) {
				g = Gender.FEMALE;
			} else if (gender.equalsIgnoreCase("undefined")) {
				g = Gender.UNDEFINED;
			}
		}
		// Set age filters to default values if they are null
		int min = -1;
		int max = -1;
		if (req.getParameter(UniversalConstants.MIN_AGE_FILTER) != null
				&& !req.getParameter(UniversalConstants.MIN_AGE_FILTER).isEmpty()
				&& !req.getParameter(UniversalConstants.MIN_AGE_FILTER).equalsIgnoreCase("None")
				&& !req.getParameter(UniversalConstants.MIN_AGE_FILTER).equalsIgnoreCase("under18")) {
			min = Integer.valueOf(req.getParameter(UniversalConstants.MIN_AGE_FILTER));
		}
		if (req.getParameter(UniversalConstants.MAX_AGE_FILTER) != null
				&& !req.getParameter(UniversalConstants.MAX_AGE_FILTER).isEmpty()
				&& !req.getParameter(UniversalConstants.MAX_AGE_FILTER).equalsIgnoreCase("None")
				&& !req.getParameter(UniversalConstants.MAX_AGE_FILTER).equalsIgnoreCase("over60")) {
			max = Integer.valueOf(req.getParameter(UniversalConstants.MAX_AGE_FILTER));
		}
		FilterCerealWrapper filter = new FilterCerealWrapper(max, min, g, -1L, -1L);
		return filter;
	}
	
	/**
	 * Gets all of the info needed for the all events tag demographics
	 * @param filter - the filter information entered by the user
	 * @param vendorId - the id of the current vendor
	 * @return 
	 */
	private List<AnalyticsDataWrapper> getAllEventTags(FilterCerealWrapper filter, long eventId, boolean joined) {
		Map<String, Integer> tagsHash = new HashMap<String, Integer>();
		// First get all events for the vendor
		VendorEvent event = VendorEventServlet.getIndividualEvent(String.valueOf(eventId));
		// Create a list that will store all of the tags that were used for the event
		List<String> tagList = new ArrayList<>();
		for (Long id : event.getTagIds()) {
			// Get the tag text from each tag
			EventTag tag = EventTagServlet.getTag(id);
			// If the tag hasn't been seen yet add it to the list
			if (!tagList.contains(tag.getTagText())) {
				tagList.add(tag.getTagText());
			}
		}
		// Loop through each event for the vendor
		List<VendorEventRelationship> relationships = new ArrayList<VendorEventRelationship>();
		if (joined) {
			// get all users with a joined relationship to the event
			relationships = VendorEventRelationshipServlet.getAllJoinedRelationshipsForEvent(event.getId(), filter);
		} else {
			relationships = VendorEventRelationshipServlet.getAllWatchedRelationshipsForEvent(event.getId(), filter);
		}

		// This hash map contains all tag texts that are common
		// between one of the vendor's events and a user with a relationship to it
		tagsHash.putAll(getRelatedUserTagInfo(tagList, relationships));
		
		List<AnalyticsDataWrapper> maxTags = new ArrayList<AnalyticsDataWrapper>();
		// If there are 5 or fewer tags then just return all of them
		if(tagsHash.size() <= 5) {
			for(Entry<String, Integer> ent : tagsHash.entrySet()) {
				maxTags.add(new AnalyticsDataWrapper(ent.getKey(), ent.getValue()));
			}
			return maxTags;
		}
		
		// If there are more than 5 tags found then find the 5 max
		for(int i=0; i < 5; i++) {
			AnalyticsDataWrapper max = null;
			// find the maximum count
			for (Entry<String, Integer> entry : tagsHash.entrySet()) {
				if (max == null || entry.getValue() > max.getValue()) {
					max = new AnalyticsDataWrapper(entry.getKey(), entry.getValue());
				}
			}
			// add the max to the return map and remove it from the tagsHash map so the next max can be found
			maxTags.add(max);
			tagsHash.remove(max.getKey());
		}
		
		return maxTags;
	}
	
	/**
	 * Get the number of attendees of the given event
	 * who follow each tag associated with the event
	 * @param eventTags - the text of the tags associated with the event
	 * @param eventId - the id of the event
	 * @return - hash map with the key being tag text and the value 
	 * being the number of attendees who follow the tag
	 */
	private static HashMap<String, Integer> getRelatedUserTagInfo(List<String> eventTags, List<VendorEventRelationship> relationships) {
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

}
