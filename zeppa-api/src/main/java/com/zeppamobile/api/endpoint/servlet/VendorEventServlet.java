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
import org.json.simple.parser.JSONParser;

import com.google.api.server.spi.response.UnauthorizedException;
import com.zeppamobile.api.PMF;
import com.zeppamobile.api.datamodel.VendorEvent;
import com.zeppamobile.common.UniversalConstants;
import com.zeppamobile.common.cerealwrapper.VendorEventWrapper;

/**
 * @author Pete Schuette
 * 
 * What are the 
 *
 */
public class VendorEventServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String jsonString = "";
		try {
			// Get the parameters from the request
			String vendorId = req.getParameter(UniversalConstants.PARAM_VENDOR_ID);
			String eventId = req.getParameter(UniversalConstants.PARAM_EVENT_ID); 
			String upcomingEvents = req.getParameter(UniversalConstants.PARAM_UPCOMING_EVENTS);
			String pastEvents = req.getParameter(UniversalConstants.PARAM_PAST_EVENTS);
			System.out.println("-------param: "+pastEvents);
			JSONArray results = new JSONArray();
			
			// Determine if calling for individual event or all user events.
			if (eventId != null && !eventId.isEmpty()){
				eventId = URLDecoder.decode(eventId,"UTF-8");
				VendorEvent result = getIndividualEvent(eventId);
				// If nothing is found with that EventID then return a not found code
				if(result == null) {
					resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
					return;
				}
				results.add(result.toJson());
				jsonString = results.toJSONString();
			} else if(vendorId != null && !vendorId.isEmpty() && upcomingEvents == null){
				vendorId = URLDecoder.decode(vendorId,"UTF-8");
				results = getAllEventsJSON(Long.parseLong(vendorId));
				jsonString = results.toJSONString();
			}
			
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.setContentType("application/json");
			//send Json back
			resp.getWriter().write(jsonString);
		
		} catch (Exception e) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			e.printStackTrace(resp.getWriter());
		}
		
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			//Set vendor info
			String title = URLDecoder.decode(req.getParameter("title"), "UTF-8");
			String description = URLDecoder.decode(req.getParameter("description"), "UTF-8");
			Long start = Long.parseLong(URLDecoder.decode(req.getParameter("start"), "UTF-8"));
			Long end = Long.parseLong(URLDecoder.decode(req.getParameter("end"), "UTF-8"));
			Long vendorId = Long.parseLong(URLDecoder.decode(req.getParameter("vendorId"), "UTF-8"));
			String address = URLDecoder.decode(req.getParameter("address"), "UTF-8");
			
			//Get all of the tags and then add them to the event
			List<Long> tagIds = new ArrayList<Long>();
			String tagsString = URLDecoder.decode(req.getParameter(UniversalConstants.PARAM_TAG_LIST), "UTF-8");
			

			resp.getWriter().write("Tags String" + tagsString+"\n\n");
			
			JSONParser parser = new JSONParser();
			JSONArray tags_json = (JSONArray) parser.parse(tagsString);
			for (int i = 0; i < tags_json.size(); i++) {
				JSONObject obj = (JSONObject) tags_json.get(i);
				Long id = (Long) obj.get("id");
				tagIds.add(id);
			}
			VendorEvent event = new VendorEvent(title,description,start,end,vendorId,tagIds,address);
			event = insertEvent(event);

			// Convert the object to json and return in the writer
			JSONObject json = event.toJson();
			resp.getWriter().write(json.toJSONString());

		} catch (Exception e) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			e.printStackTrace(resp.getWriter());
		}
	}

	/**
	 * Create a JSON array containing all of the events for 
	 * the given vendor
	 * @param vendorId - the if of the vendor to get events for
	 * @return - JSON array with the event info
	 */
	@SuppressWarnings("unchecked")
	public JSONArray getAllEventsJSON(Long vendorId){
		JSONArray results = new JSONArray();
		
		List<VendorEvent> events = getAllEvents(vendorId);
		
		for(VendorEvent event : events) {
			JSONObject json = event.toJson();
			results.add(json);
		}
		return results;
	}
	
	/**
	 * Gets all events for the vendor with the given id
	 * @param vendorId - the id of the vendor
	 * @return - list of all events for the given vendor
	 */
	@SuppressWarnings("unchecked")
	public static List<VendorEvent> getAllEvents(Long vendorId) {
		List<VendorEvent> events = new ArrayList<VendorEvent>();
		PersistenceManager mgr = getPersistenceManager();
		try {
			Query q = mgr.newQuery(VendorEvent.class,
					"hostId == " + vendorId);

			Collection<VendorEvent> response = (Collection<VendorEvent>) q.execute();
			if(response.size()>0) {
				// This was a success
				events.addAll(response);
			}
			
		} finally {
			mgr.close();
		}
		
		return events;
	}
	
	/**
	 * Find an event with the given ID and return its JSON info
	 * @param eventId - the id of the desired event
	 * @return - the JSON with the event info
	 */
	public static VendorEvent getIndividualEvent(String eventId) {
		
		PersistenceManager mgr = getPersistenceManager();
		VendorEvent event = null;
		try {
			// Query for the event with the given ID
			event = mgr.getObjectById(VendorEvent.class, Long.valueOf(eventId));
		} finally {
			mgr.close();
		}
		
		return event;
	}
	
	/**
	 * This inserts a new entity into App Engine datastore. If the entity
	 * already exists in the datastore, an exception is thrown. It uses HTTP
	 * POST method.
	 * 
	 * @param event
	 *            the entity to be inserted.
	 * @return The inserted entity.
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	public static VendorEvent insertEvent(VendorEvent event) throws UnauthorizedException,
			IOException {

		// Manager to insert vendor and master employee
		PersistenceManager mgr = getPersistenceManager();
		Transaction txn = mgr.currentTransaction();

		try {
			// Start the transaction
			txn.begin();
			// Persist event
			event = mgr.makePersistent(event);
			// commit the changes
			txn.commit();

		} catch (Exception e) {
			// catch any errors that might occur
			e.printStackTrace();
		} finally {

			if (txn.isActive()) {
				txn.rollback();
				event = null;
			}
			mgr.close();
		}

		return event;
	}

	/**
	 * Get the persistence manager for interacting with datastore
	 */
	private static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}

}
