package com.zeppamobile.api.endpoint.utils;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.repackaged.org.joda.time.LocalDate;
import com.google.appengine.repackaged.org.joda.time.Years;
import com.zeppamobile.api.datamodel.VendorEvent;
import com.zeppamobile.api.datamodel.VendorEventRelationship;
import com.zeppamobile.api.datamodel.ZeppaUser;
import com.zeppamobile.api.endpoint.servlet.VendorEventServlet;
import com.zeppamobile.api.endpoint.servlet.ZeppaUserServlet;
import com.zeppamobile.common.UniversalConstants;
import com.zeppamobile.common.cerealwrapper.FilterCerealWrapper.Gender;

public class AnalyticsFilter {
	
	/**
	 * Filters out VendorEventRelationships with users who 
	 * are not of the specified gender
	 * @param rels - relationships to be filtered
	 * @param gender - gender to filter on
	 * @return - list of filtered relationships
	 */
	public static List<VendorEventRelationship> filterRelationshipsOnGender(List<VendorEventRelationship> rels, Gender gender) {
		List<VendorEventRelationship> returnList = new ArrayList<VendorEventRelationship>();
		if(gender.equals(Gender.ALL))
			return rels;
		
		for(VendorEventRelationship rel : rels) {
			// Find the user
			Long id = rel.getUserId();
			ZeppaUser user = ZeppaUserServlet.getUser(id);
			// If the user is of the correct gender then add them to the list
			if(user.getUserInfo().getGender().toString().equals(gender.toString())) {
//				System.out.println("--GENFILT: "+user.getUserInfo().getGivenName());
//				System.out.println("--GENFILT: "+user.getUserInfo().getGender().toString()+" - "+gender.toString());
				returnList.add(rel);
			}
		}
		
		return returnList;
	}
	
	/**
	 * Filters out VendorEventRelationships with users who 
	 * are not in the desired age range
	 * @param rels - relationships to be filtered
	 * @param minAge - the minimum desired age
	 * @param maxAge - maximum desired age
	 * @return - list of relationships that meet filter criteria
	 */
	public static List<VendorEventRelationship> filterRelationshipsOnAge(List<VendorEventRelationship> rels, String minAge, String maxAge) {
		// If max age is above 60 then make it chosen large value
		if(maxAge.equalsIgnoreCase(UniversalConstants.AGE_FILTER_NONE)) {
			maxAge = UniversalConstants.AGE_FILTER_OVER60;
		}
		if(minAge.equalsIgnoreCase(UniversalConstants.AGE_FILTER_NONE)) {
			minAge = UniversalConstants.AGE_FILTER_UNDER18;
		}
		List<VendorEventRelationship> returnList = new ArrayList<VendorEventRelationship>();
		for(VendorEventRelationship rel : rels) {
			// Find the user
			ZeppaUser user = ZeppaUserServlet.getUser(rel.getUserId());
			// Get the user's date of birth
			LocalDate dob = new LocalDate(user.getUserInfo().getDateOfBirth());
			VendorEvent event = VendorEventServlet.getIndividualEvent(String.valueOf(rel.getEventId()));
			LocalDate eventTime = new LocalDate(event.getStart());
			Years age = Years.yearsBetween(dob, eventTime);
			int minVal = getMinAgeValue(minAge);
			int maxVal = getMaxAgeValue(maxAge);
			
//			System.out.println("--NAME: "+user.getUserInfo().getGivenName());
//			System.out.println("--AGECOMP: "+age.getYears()+" - MINVAL: " + minVal+ " - MAXVAL: "+maxVal);
			// if the user is in the age range add them to the list
			if(age.getYears() >= minVal &&	age.getYears() <= maxVal) {
				returnList.add(rel);
			}
		}
		
		return returnList;
	}
	
	private static int getMinAgeValue(String value) {
		if(value.equals(UniversalConstants.AGE_FILTER_UNDER18))
			return 0;
		else if(value.equals(UniversalConstants.AGE_FILTER_18to20))
			return 18;
		else if(value.equals(UniversalConstants.AGE_FILTER_21to24))
			return 20;
		else if(value.equals(UniversalConstants.AGE_FILTER_25to29))
			return 25;
		else if(value.equals(UniversalConstants.AGE_FILTER_30to39))
			return 30;
		else if(value.equals(UniversalConstants.AGE_FILTER_40to49))
			return 40;
		else if(value.equals(UniversalConstants.AGE_FILTER_50to59))
			return 50;
		else if(value.equals(UniversalConstants.AGE_FILTER_OVER60))
			return 60;
		
		return -1;
	}
	
	private static int getMaxAgeValue(String value) {
		if(value.equals(UniversalConstants.AGE_FILTER_UNDER18))
			return 18;
		else if(value.equals(UniversalConstants.AGE_FILTER_18to20))
			return 20;
		else if(value.equals(UniversalConstants.AGE_FILTER_21to24))
			return 24;
		else if(value.equals(UniversalConstants.AGE_FILTER_25to29))
			return 29;
		else if(value.equals(UniversalConstants.AGE_FILTER_30to39))
			return 39;
		else if(value.equals(UniversalConstants.AGE_FILTER_40to49))
			return 49;
		else if(value.equals(UniversalConstants.AGE_FILTER_50to59))
			return 59;
		else if(value.equals(UniversalConstants.AGE_FILTER_OVER60))
			return 150;
		
		// Arbitrary large age
		return 150;
	}
	
	/**
	 * Filters out VendorEventRelationships with events that 
	 * do not fall in desired date range
	 * @param rels - relationships to be filtered
	 * @param startRange - the start date of the desired date range
	 * @param endRange - the end date of the desired date range
	 * @return - list of relationships that meet filter criteria
	 */
	public static List<VendorEventRelationship> filterRelationshipsOnDate(List<VendorEventRelationship> rels, long startRange, long endRange) {
		// if max value isn't defined then make it largest possible
		if(endRange == -1L) {
			endRange = Long.MAX_VALUE;
		}
		List<VendorEventRelationship> returnList = new ArrayList<VendorEventRelationship>();
		for(VendorEventRelationship rel : rels) {
			// Find the user
			Long id = rel.getEventId();
			VendorEvent event = VendorEventServlet.getIndividualEvent(String.valueOf(id));
//			System.out.println("--DATE: "+event.getStart()+" MIN: "+startRange+" MAX: "+endRange);
			// if the event is in the date range add it to the list
			if(startRange <= event.getStart() && endRange >= event.getStart()) {
//				System.out.println("DATE USER: "+ZeppaUserServlet.getUser(rel.getUserId()).getUserInfo().getGivenName());
				returnList.add(rel);
			}
		}
		
		return returnList;
	}

}
