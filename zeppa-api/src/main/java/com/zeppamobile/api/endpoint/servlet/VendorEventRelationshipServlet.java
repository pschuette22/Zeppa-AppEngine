package com.zeppamobile.api.endpoint.servlet;

import java.io.IOException;
import java.net.URLDecoder;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.api.server.spi.response.UnauthorizedException;
import com.zeppamobile.api.PMF;
import com.zeppamobile.api.datamodel.VendorEvent;
import com.zeppamobile.api.datamodel.VendorEventRelationship;
import com.zeppamobile.api.endpoint.utils.AnalyticsFilter;
import com.zeppamobile.common.UniversalConstants;
import com.zeppamobile.common.cerealwrapper.FilterCerealWrapper;
import com.zeppamobile.common.cerealwrapper.FilterCerealWrapper.Gender;
import com.zeppamobile.common.cerealwrapper.VendorEventWrapper;

/**
 * 
 * @author Pete Schuette
 * 
 * Servlet for fetching vendor event relationship
 *
 */
public class VendorEventRelationshipServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		// Get the parameters from the request
		String vendorId = req.getParameter(UniversalConstants.PARAM_VENDOR_ID);
		String eventId = req.getParameter(UniversalConstants.PARAM_EVENT_ID); 
		JSONArray results = new JSONArray();
		
		// Determine if the call is for a specific event or all of a vendor's events
		if (eventId != null && !eventId.isEmpty()) {
			eventId = URLDecoder.decode(eventId,"UTF-8");
			results = getAllRelationshipsForEventJSON(Long.valueOf(eventId), null);
			resp.setStatus(HttpServletResponse.SC_OK);
		} else if(vendorId != null && !vendorId.isEmpty()){
			vendorId = URLDecoder.decode(vendorId,"UTF-8");
			results = getAllRelationshipsForVendorJSON(Long.valueOf(vendorId));
			resp.setStatus(HttpServletResponse.SC_OK);
		}
		
		resp.setContentType("application/json");
		
		//send Json back
		resp.getWriter().write(results.toJSONString());
		
	}
	
	/**
	 * Generate a JSONArray with all VendorEventRelationships for a specific event
	 * @param eventId - the id to get all relationships for
	 * @return - JSONArray containing info on all relationships to the given event
	 */
	@SuppressWarnings("unchecked")
	public JSONArray getAllRelationshipsForEventJSON(Long eventId, FilterCerealWrapper filter){
		JSONArray results = new JSONArray();
		List<VendorEventRelationship> relationships = getAllRelationshipsForEvent(eventId, filter);
		for(VendorEventRelationship rel : relationships) {
			JSONObject json = rel.toJson();
			results.add(json);
		}
		return results;
	}
	
	/**
	 * Get all of the VendorEventRelationships for a specific event
	 * @param eventId - the id of the event to get relationships for
	 * @return - the list of relationships
	 */
	@SuppressWarnings("unchecked")
	public static List<VendorEventRelationship> getAllRelationshipsForEvent(Long eventId, FilterCerealWrapper filter) {
		List<VendorEventRelationship> relationships = new ArrayList<VendorEventRelationship>();
		PersistenceManager mgr = getPersistenceManager();
		try {
			Query q = mgr.newQuery(VendorEventRelationship.class,
					"eventId == " + eventId);
			Collection<VendorEventRelationship> response = (Collection<VendorEventRelationship>) q.execute();
			if(response.size()>0) {
				relationships.addAll(response);
			}
			
		} finally {
			mgr.close();
		}
		
		if(filter == null) {
			return relationships;
		}

		// Fitler results on gender if filter info specified 
		if(!filter.getGender().equals(Gender.ALL)) {
			relationships = AnalyticsFilter.filterRelationshipsOnGender(relationships, filter.getGender());
		}
		// Filter results on age if filter info specified 
		if(!(filter.getMinAge() == -1) || !(filter.getMaxAge() == -1)) {
			relationships = AnalyticsFilter.filterRelationshipsOnAge(relationships, filter.getMinAge(), filter.getMaxAge());
		} 
		// Filter results on date if filter info specified 
		if (!(filter.getStartDate() == -1) && !(filter.getStartDate() == -1)) {
			relationships = AnalyticsFilter.filterRelationshipsOnDate(relationships, filter.getStartDate(), filter.getEndDate());
		}
		
		return relationships;
	}
	
	/**
	 * Get all of the joined VendorEventRelationships for a specific event
	 * @param eventId - the id of the event to get relationships for
	 * @return - the list of relationships
	 */
	@SuppressWarnings("unchecked")
	public static List<VendorEventRelationship> getAllJoinedRelationshipsForEvent(Long eventId, FilterCerealWrapper filter){
		List<VendorEventRelationship> relationships = new ArrayList<VendorEventRelationship>();
		 
		PersistenceManager mgr = getPersistenceManager();
		try {
			Query q = mgr.newQuery(VendorEventRelationship.class,
					"eventId == " + eventId);
			Collection<VendorEventRelationship> response = (Collection<VendorEventRelationship>) q.execute();
			if(response.size()>0) {
				relationships.addAll(response);
			}
			
		} finally {
			mgr.close();
		}

		// Create list with only joined relationships
		List<VendorEventRelationship> joinedList = new ArrayList<VendorEventRelationship>(); 
		for(VendorEventRelationship rel : relationships) {
			if(rel.isJoined()) 
				joinedList.add(rel);
		}
		
		if(filter == null) {
			return joinedList;
		}

		// Fitler results on gender if filter info specified 
		if(!filter.getGender().equals(Gender.ALL)) {
			joinedList = AnalyticsFilter.filterRelationshipsOnGender(joinedList, filter.getGender());
		} 
		// Filter results on age if filter info specified 
		if(!(filter.getMinAge() == -1) || !(filter.getMaxAge() == -1)) {
			joinedList = AnalyticsFilter.filterRelationshipsOnAge(joinedList, filter.getMinAge(), filter.getMaxAge());
		}
		// Filter results on date if filter info specified 
		if (!(filter.getStartDate() == -1) || !(filter.getStartDate() == -1)) {
			joinedList = AnalyticsFilter.filterRelationshipsOnDate(joinedList, filter.getStartDate(), filter.getEndDate());
		}
		
		return joinedList;
	}
	
	/**
	 * Get all of the watched VendorEventRelationships for a specific event
	 * @param eventId - the id of the event to get relationships for
	 * @return - the list of relationships
	 */
	@SuppressWarnings("unchecked")
	public static List<VendorEventRelationship> getAllWatchedRelationshipsForEvent(Long eventId, FilterCerealWrapper filter){
		List<VendorEventRelationship> relationships = new ArrayList<VendorEventRelationship>();
		PersistenceManager mgr = getPersistenceManager();
		try {
			Query q = mgr.newQuery(VendorEventRelationship.class,
					"eventId == " + eventId);
			Collection<VendorEventRelationship> response = (Collection<VendorEventRelationship>) q.execute();
			if(response.size()>0) {
				relationships.addAll(response);
			}
			
		} finally {
			mgr.close();
		}
		// Create list with only watched relationships
		List<VendorEventRelationship> watchedList = new ArrayList<VendorEventRelationship>(); 
		for(VendorEventRelationship rel : relationships) {
			if(rel.isWatched()) 
				watchedList.add(rel);
		}
		
		if(filter == null) {
			return watchedList;
		}

		// Fitler results on gender if filter info specified 
		if(!filter.getGender().equals(Gender.ALL)) {
			watchedList = AnalyticsFilter.filterRelationshipsOnGender(watchedList, filter.getGender());
		}
		// Filter results on age if filter info specified 
		if(!(filter.getMinAge() == -1) || !(filter.getMaxAge() == -1)) {
			watchedList = AnalyticsFilter.filterRelationshipsOnAge(watchedList, filter.getMinAge(), filter.getMaxAge());
		}
		// Filter results on date if filter info specified 
		if (!(filter.getStartDate() == -1) || !(filter.getStartDate() == -1)) {
			watchedList = AnalyticsFilter.filterRelationshipsOnDate(watchedList, filter.getStartDate(), filter.getEndDate());
		}
		
		return watchedList;
	}
	
	/**
	 * Generate JSONArray with all relationships to all events for the given vendor 
	 * @param vendorId - the id of the vendor to get all relationships for
	 * @return - JSON array with all relationships for all of the given vendor's events
	 */
	@SuppressWarnings("unchecked")
	public JSONArray getAllRelationshipsForVendorJSON(Long vendorId) {
		JSONArray results = new JSONArray();
		// Get all events for the vendor
		List<VendorEvent> events = VendorEventServlet.getAllEvents(vendorId);
		
		// Go through each event for the vendor and find all relationships
		for(VendorEvent event : events) {
			JSONArray temp = getAllRelationshipsForEventJSON(event.getId(), null);
			for(int i=0; i < temp.size(); i++) {
				results.add(temp.get(i));
			}
		}
		return results;
	}
	
	/**
	 * This inserts a new entity into App Engine datastore. If the entity
	 * already exists in the datastore, an exception is thrown. It uses HTTP
	 * POST method.
	 * @param event
	 *            the entity to be inserted.
	 * @return The inserted entity.
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	public static VendorEventRelationship insertEventRelationship(VendorEventRelationship relationship) throws UnauthorizedException,
			IOException {
		// Manager to insert relationship
		PersistenceManager mgr = getPersistenceManager();
		Transaction txn = mgr.currentTransaction();

		try {
			// Start the transaction
			txn.begin();
			// Persist event
			relationship = mgr.makePersistent(relationship);
			// commit the changes
			txn.commit();
		} catch (Exception e) {
			// catch any errors that might occur
			e.printStackTrace();
		} finally {

			if (txn.isActive()) {
				txn.rollback();
				relationship = null;
			}

			mgr.close();
		}

		return relationship;
	}

	/**
	 * Get the persistence manager for interacting with datastore
	 */
	private static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}
}
