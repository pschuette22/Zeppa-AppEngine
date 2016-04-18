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
				System.out.println("--GENFILT: "+user.getUserInfo().getGivenName());
				System.out.println("--GENFILT: "+user.getUserInfo().getGender().toString()+" - "+gender.toString());
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
	public static List<VendorEventRelationship> filterRelationshipsOnAge(List<VendorEventRelationship> rels, int minAge, int maxAge) {
		// If max age is above 60 then make it chosen large value
		if(maxAge == -1) {
			maxAge = 150;
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
//			System.out.println("--AGE: "+age.getYears()+" MIN: "+minAge+" MAX: "+maxAge);
			// if the user is in the age range add them to the list
			if(minAge <= age.getYears() && maxAge >= age.getYears()) {
				System.out.println("--NAME: "+user.getUserInfo().getGivenName());
				System.out.println("--AGEFILT: "+age+" - "+user.getUserInfo().getDateOfBirth());
				returnList.add(rel);
			}
		}
		
		return returnList;
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
				System.out.println("DATE USER: "+ZeppaUserServlet.getUser(rel.getUserId()).getUserInfo().getGivenName());
				returnList.add(rel);
			}
		}
		
		return returnList;
	}

}
