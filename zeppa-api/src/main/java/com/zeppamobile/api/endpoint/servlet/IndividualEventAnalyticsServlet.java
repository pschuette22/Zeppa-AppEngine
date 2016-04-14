package com.zeppamobile.api.endpoint.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.appengine.repackaged.org.joda.time.LocalDate;
import com.google.appengine.repackaged.org.joda.time.Years;
import com.zeppamobile.api.datamodel.VendorEvent;
import com.zeppamobile.api.datamodel.VendorEventRelationship;
import com.zeppamobile.api.datamodel.ZeppaUser;
import com.zeppamobile.common.UniversalConstants;
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
			Map<String, Integer> genderCounts = getEventGenderInfo(filter, Long.valueOf(eventId));
			JSONObject json = new JSONObject();
			// put the counts in the JSON object so they can be returned to the
			// frontend in the response
			json.put("maleCount", genderCounts.get("MALE"));
			json.put("femaleCount", genderCounts.get("FEMALE"));
			json.put("unidentified", genderCounts.get("UNIDENTIFIED"));
			
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
				&& !req.getParameter(UniversalConstants.MIN_AGE_FILTER).equalsIgnoreCase("None")) {
			min = Integer.valueOf(req.getParameter(UniversalConstants.MIN_AGE_FILTER));
		}
		if (req.getParameter(UniversalConstants.MAX_AGE_FILTER) != null
				&& !req.getParameter(UniversalConstants.MAX_AGE_FILTER).isEmpty()
				&& !req.getParameter(UniversalConstants.MAX_AGE_FILTER).equalsIgnoreCase("None")) {
			max = Integer.valueOf(req.getParameter(UniversalConstants.MAX_AGE_FILTER));
		}
		FilterCerealWrapper filter = new FilterCerealWrapper(max, min, g, -1L, -1L);
		return filter;
	}

}
