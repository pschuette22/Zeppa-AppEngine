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
import com.google.appengine.api.oauth.OAuthRequestException;
import com.zeppamobile.api.PMF;
import com.zeppamobile.api.datamodel.EventTag;
import com.zeppamobile.api.datamodel.EventTagFollow;
import com.zeppamobile.api.endpoint.utils.TaskUtility;
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
			String ownerId = req.getParameter(UniversalConstants.PARAM_VENDOR_ID);
			String tagId = req.getParameter(UniversalConstants.PARAM_TAG_ID);
			String responseString = "";
			
			if(ownerId!=null && !ownerId.isEmpty()){
				ownerId = URLDecoder.decode(ownerId,"UTF-8");
				responseString = getTags(Long.parseLong(ownerId));
			}else if(tagId!=null && !tagId.isEmpty()){
				tagId = URLDecoder.decode(tagId,"UTF-8");
				responseString = getTagJson(Long.parseLong(tagId));
			}
			

			resp.setStatus(HttpServletResponse.SC_OK);
			resp.setContentType("application/json");
			// Convert the object to json and return in the writer
			
			resp.getWriter().print(responseString);
			
			
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
			tag.setType(EventTag.TagType.VENDOR);
			tag = insertTag(tag);

			// Convert the object to json and return in the writer
			JSONObject json = tag.toJson();
			resp.getWriter().write(json.toJSONString());
			
		} catch (Exception e) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			e.printStackTrace(resp.getWriter());
		}
	}
	
	/**
	 * This method gets the tags having the owner id given.
	 * 
	 * @param id
	 *            the id of the owner of the tag
	 * @return The json of all of the tags
	 * @throws OAuthRequestException
	 */
	@SuppressWarnings("unchecked")
	public String getTags(Long ownerId) throws UnauthorizedException {

		List<EventTag> results = new ArrayList<EventTag>();
		PersistenceManager mgr = getPersistenceManager();
		String responseString ="";
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
			JSONArray arr = new JSONArray();
			for(EventTag tag : results) {
				JSONObject json = tag.toJson();
				arr.add(json);
			}
			responseString = arr.toJSONString();
			
		} finally {
			mgr.close();
		}
		return responseString;
	}

	/**
	 * This method gets the tag based on id.
	 * 
	 * @param id
	 *            the id of the tag
	 * @return The JSON of the tags
	 * @throws OAuthRequestException
	 */
	public static String getTagJson(Long tagId) {
		return getTag(tagId).toJson().toJSONString();
	}
	
	/**
	 * Gets the EventTag with the given ID
	 * @param tagId - the id of the tag to get
	 * @return - the tag with the given ID
	 */
	public static EventTag getTag(Long tagId) {
		PersistenceManager mgr = getPersistenceManager();
		EventTag tag = new EventTag();
		try {
			tag = mgr.getObjectById(EventTag.class, tagId);
		} finally {
			mgr.close();
		}
		
		return tag;
	}
	
	/**
	 * This inserts a new Employee entity into App Engine datastore. If the entity
	 * already exists in the datastore, an exception is thrown. 
	 * 
	 * @param tag - the entity to be inserted.
	 * @return The inserted entity.
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	public static EventTag insertTag(EventTag tag) throws UnauthorizedException,
			IOException {
		// Manager to insert the tag
		PersistenceManager mgr = getPersistenceManager();
//		Transaction txn = mgr.currentTransaction();

		try {
			// Start the transaction
//			txn.begin();

			// Set tag time characteristics
			tag.setCreated(System.currentTimeMillis());
			tag.setUpdated(System.currentTimeMillis());

			// Persist the tag
			tag = mgr.makePersistent(tag);

			// commit the changes
//			txn.commit();

			TaskUtility.scheduleIndexEventTag(tag, false);
			
		} catch (Exception e) {
			// catch any errors that might occur
			e.printStackTrace();
		} finally {
			
//			if (txn.isActive()) {
//				txn.rollback();
//				tag = null;
//			}

			mgr.close();

		}

		return tag;
	}

	/**
	 * Gets all tags that the given user follows
	 * @param userId - the user who's tags will be returned
	 * @return - all tags followed by the user
	 */
	@SuppressWarnings("unchecked")
	public static List<EventTag> getAllUserTags(Long userId) {
		List<EventTagFollow> follows = new ArrayList<EventTagFollow>();
		PersistenceManager mgr = getPersistenceManager();
		try {
			// Get all of the follow relationships for the given user
			Query q = mgr.newQuery(EventTagFollow.class,
					"followerId == " + userId);
			Collection<EventTagFollow> response = (Collection<EventTagFollow>) q.execute();
			if(response.size()>0) {
				follows.addAll(response);
			}
			
		} finally {
			mgr.close();
		}
		List<EventTag> tags = new ArrayList<EventTag>();
		for(EventTagFollow follow : follows) {
			tags.add(getTag(follow.getTagId()));
		}
		
		return tags;
	}
	
	/**
	 * Get the persistence manager for interacting with datastore
	 */
	private static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}
	
}
