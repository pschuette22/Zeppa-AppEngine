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
import com.zeppamobile.common.UniversalConstants;

/**
 * 
 * @author Pete Schuette
 * 
 * Servlet for fetching vendor event relationship
 *
 */
public class VendorEventRelationshipServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String vendorId = req.getParameter(UniversalConstants.PARAM_VENDOR_ID);
		String eventId = req.getParameter(UniversalConstants.PARAM_EVENT_ID); 
		JSONArray results = new JSONArray();
		
		if (eventId != null && !eventId.isEmpty()) {
			eventId = URLDecoder.decode(eventId,"UTF-8");
			results = getAllRelationshipsForEventJSON(Long.valueOf(eventId));
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
	
	@SuppressWarnings("unchecked")
	public JSONArray getAllRelationshipsForEventJSON(Long eventId){
		JSONArray results = new JSONArray();
		List<VendorEventRelationship> relationships = new ArrayList<VendorEventRelationship>();
		PersistenceManager mgr = getPersistenceManager();
		try {
			Query q = mgr.newQuery(VendorEventRelationship.class,
					"eventId == " + eventId);

			Collection<VendorEventRelationship> response = (Collection<VendorEventRelationship>) q.execute();
			if(response.size()>0) {
				// This was a success
				// TODO: implement any extra initialization if desired
				// Sometimes, jdo objects want to be touched when fetch strategy is not defined
				relationships.addAll(response);
			}
			
		} finally {
			mgr.close();
		}
		for(VendorEventRelationship rel : relationships) {
			JSONObject json = rel.toJson();
			results.add(json);
		}
		return results;
	}
	
	@SuppressWarnings("unchecked")
	public JSONArray getAllRelationshipsForVendorJSON(Long vendorId) {
		JSONArray results = new JSONArray();
		PersistenceManager mgr = getPersistenceManager();
		List<VendorEvent> events = new ArrayList<VendorEvent>();
		// Get all events for the given vendor ID
		try {
			Query q = mgr.newQuery(VendorEvent.class,
					"hostId == " + vendorId);

			Collection<VendorEvent> response = (Collection<VendorEvent>) q.execute();
			if(response.size()>0) {
				// This was a success
				// TODO: implement any extra initialization if desired
				// Sometimes, jdo objects want to be touched when fetch strategy is not defined
				events.addAll(response);
			}
			
		} finally {
			mgr.close();
		}
		
		// Go through each event for the vendor and find all relationships
		for(VendorEvent event : events) {
			JSONArray temp = getAllRelationshipsForEventJSON(event.getId());
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
	 * 
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
