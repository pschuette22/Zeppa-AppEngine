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

import org.json.simple.JSONObject;

import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.zeppamobile.api.PMF;
import com.zeppamobile.api.datamodel.EventTag;
import com.zeppamobile.common.UniversalConstants;

/**
 * @author Kevin Moratelli
 * 
 */
public class EventTagServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		try {
			// Fetch the owner ID
			Long ownerId = Long.parseLong(req.getParameter(UniversalConstants.PARAM_VENDOR_ID));

			List<EventTag> tags = getTags(ownerId);

			resp.setStatus(HttpServletResponse.SC_OK);
			resp.setContentType("application/json");
			// Convert the object to json and return in the writer
			for(EventTag tag : tags) {
				JSONObject json = tag.toJson();
				resp.getOutputStream().print(json.toJSONString());
			}
			resp.getOutputStream().flush();
			resp.getOutputStream().close();
			
		} catch (UnauthorizedException e) {
			// user is not authorized to make this call
			resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			e.printStackTrace(resp.getWriter());
		} 
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		try {
			EventTag tag = new EventTag();
			tag.setOwnerId(Long.valueOf(URLDecoder.decode(req.getParameter(UniversalConstants.PARAM_VENDOR_ID), "UTF-8")));
			tag.setTagText(URLDecoder.decode(req.getParameter("tagText"), "UTF-8"));
			
			tag = insertVendor(tag);

			// Convert the object to json and return in the writer
			JSONObject json = tag.toJson();
			resp.getWriter().write(json.toJSONString());
			
		} catch (Exception e) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			resp.getWriter().print(e.getStackTrace());
		}
	}
	
	/**
	 * This method gets the tags having the owner id given.
	 * 
	 * @param id
	 *            the id of the owner of the tag
	 * @return The entity with primary key id.
	 * @throws OAuthRequestException
	 */
	@SuppressWarnings("unchecked")
	public List<EventTag> getTags(Long ownerId) throws UnauthorizedException {

		List<EventTag> results = new ArrayList<EventTag>();
		PersistenceManager mgr = getPersistenceManager();
		try {
			Query q = mgr.newQuery(EventTag.class,
					"ownerId == " + ownerId);

			Collection<EventTag> response = (Collection<EventTag>) q.execute();
			if(response.size()>0) {
				// This was a success
				// TODO: implement any extra initialization if desired
				// Sometimes, jdo objects want to be touched when fetch strategy is not defined
				results.addAll(response);
			}
			
		} finally {
			mgr.close();
		}
		return results;
	}

	/**
	 * This inserts a new EventTag entity into App Engine datastore. If the entity
	 * already exists in the datastore, an exception is thrown. It uses HTTP
	 * POST method.
	 * 
	 * @param tag - the entity to be inserted.
	 * @return The inserted entity.
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	public EventTag insertVendor(EventTag tag) throws UnauthorizedException,
			IOException {
		// Manager to insert the tag
		PersistenceManager mgr = getPersistenceManager();
		Transaction txn = mgr.currentTransaction();

		try {
			// Start the transaction
			txn.begin();

			// Set tag time characteristics
			tag.setCreated(System.currentTimeMillis());
			tag.setUpdated(System.currentTimeMillis());

			// Persist the tag
			tag = mgr.makePersistent(tag);

			// commit the changes
			txn.commit();

		} catch (Exception e) {
			// catch any errors that might occur
			e.printStackTrace();
		} finally {

			if (txn.isActive()) {
				txn.rollback();
				tag = null;
			}

			mgr.close();

		}

		return tag;
	}

	/**
	 * Get the persistence manager for interacting with datastore
	 */
	private static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}
	
}
