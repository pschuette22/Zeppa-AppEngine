package com.zeppamobile.api.endpoint;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.datanucleus.query.JDOCursorHelper;
import com.zeppamobile.api.Constants;
import com.zeppamobile.api.PMF;
import com.zeppamobile.api.Resources;
import com.zeppamobile.api.datamodel.VendorEvent;
import com.zeppamobile.api.datamodel.VendorEventRelationship;
import com.zeppamobile.api.datamodel.ZeppaEvent;
import com.zeppamobile.api.datamodel.ZeppaEventToUserRelationship;
import com.zeppamobile.api.datamodel.ZeppaUser;
import com.zeppamobile.api.endpoint.utils.ClientEndpointUtility;
import com.zeppamobile.api.googlecalendar.GoogleCalendarService;
import com.zeppamobile.api.notifications.NotificationUtility;
import com.zeppamobile.common.utils.Utils;

@Api(name = Constants.API_NAME, version = "v1", scopes = { Constants.EMAIL_SCOPE }, audiences = {
		Constants.WEB_CLIENT_ID })
public class VendorEventRelationshipEndpoint {

	/**
	 * This method lists all the entities inserted in datastore. It uses HTTP
	 * GET method and paging support.
	 * 
	 * @return A CollectionResponse class containing the list of all entities
	 *         persisted and a cursor to the next page.
	 * @throws OAuthRequestException
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listVendorEventRelationship", path = "listVendorEventRelationship")
	public CollectionResponse<VendorEventRelationship> listVendorEventRelationship(
			@Nullable @Named("filter") String filterString, @Nullable @Named("cursor") String cursorString,
			@Nullable @Named("ordering") String orderingString, @Nullable @Named("limit") Integer limit,
			@Named("idToken") String tokenString) throws UnauthorizedException {

		// Fetch Authorized Zeppa User
		ZeppaUser user = ClientEndpointUtility.getAuthorizedZeppaUser(tokenString);
		if (user == null) {
			throw new UnauthorizedException("No matching user found for this token");
		}

		PersistenceManager mgr = null;
		Cursor cursor = null;
		List<VendorEventRelationship> execute = null;

		try {
			// Initialize the query process
			mgr = getPersistenceManager();
			Query query = mgr.newQuery(VendorEventRelationship.class);
			// Determine if there is a valid cursor, and start from there if so
			if (Utils.isWebSafe(cursorString)) {
				cursor = Cursor.fromWebSafeString(cursorString);
				HashMap<String, Object> extensionMap = new HashMap<String, Object>();
				extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
				query.setExtensions(extensionMap);
			}

			// Set the query filter.
			if (Utils.isWebSafe(filterString)) {
				query.setFilter(filterString);
			} // TODO: reject if the query filter is not valid

			// Set the order of the collection returned
			if (Utils.isWebSafe(orderingString)) {
				query.setOrdering(orderingString);
			}

			// If there is a query limit, respond to it
			if (limit != null) {
				query.setRange(0, limit);
			}

			// Run that query!
			execute = (List<VendorEventRelationship>) query.execute();

			cursor = JDOCursorHelper.getCursor(execute);
			if (cursor == null) {
				cursorString = null;
			} else {
				cursorString = cursor.toWebSafeString();
			}
			
		} finally {
			mgr.close();
		}

		// Return That Collection!
		return CollectionResponse.<VendorEventRelationship> builder().setItems(execute).setNextPageToken(cursorString)
				.build();
	}

	/**
	 * This method gets the entity having primary key id. It uses HTTP GET
	 * method.
	 * 
	 * @param id
	 *            the primary key of the java bean.
	 * @return The entity with primary key id.
	 * @throws OAuthRequestException
	 */

	@ApiMethod(name = "getVendorEventRelationship", path = "getVendorEventRelationship")
	public VendorEventRelationship getVendorEventRelationship(@Named("id") Long id,
			@Named("idToken") String tokenString) throws UnauthorizedException {

		// Fetch Authorized Zeppa User
		ZeppaUser user = ClientEndpointUtility.getAuthorizedZeppaUser(tokenString);
		if (user == null) {
			throw new UnauthorizedException("No matching user found for this token");
		}

		PersistenceManager mgr = getPersistenceManager();
		VendorEventRelationship vendorEventRelationship = null;
		try {
			vendorEventRelationship = mgr.getObjectById(VendorEventRelationship.class, id);

		} finally {
			mgr.close();
		}
		return vendorEventRelationship;
	}

	/**
	 * This inserts a new entity into App Engine datastore. If the entity
	 * already exists in the datastore, an exception is thrown. It uses HTTP
	 * POST method.
	 * 
	 * @param zeppaeventtouserrelationship
	 *            the entity to be inserted.
	 * @return The inserted entity.
	 * @throws OAuthRequestException
	 */
	@ApiMethod(name = "insertVendorEventRelationship")
	public VendorEventRelationship insertZeppaEventToUserRelationship(VendorEventRelationship relationship,
			@Named("idToken") String tokenString) throws UnauthorizedException {

		// Fetch Authorized Zeppa User
		ZeppaUser user = ClientEndpointUtility.getAuthorizedZeppaUser(tokenString);
		if (user == null) {
			throw new UnauthorizedException("No matching user found for this token");
		}

		if (relationship.getEventId() == null) {
			throw new NullPointerException("Null Event Id");
		}

		PersistenceManager mgr = getPersistenceManager();
		Transaction txn = mgr.currentTransaction();
		try {

			txn.begin();
			VendorEvent event = mgr.getObjectById(VendorEvent.class, relationship.getEventId());

			// TODO: make sure that a vendor event relationship does not already
			// exist between this user and event

			
			relationship.setUserId(user.getId());
			relationship = mgr.makePersistent(relationship);

			
			txn.commit();

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
	 * This method is used for updating an existing entity. If the entity does
	 * not exist in the datastore, an exception is thrown. It uses HTTP PUT
	 * method.
	 * 
	 * @param zeppaeventtouserrelationship
	 *            the entity to be updated.
	 * @return The updated entity.
	 * @throws IOException
	 * @throws GeneralSecurityException
	 * @throws OAuthRequestException
	 */
	@ApiMethod(name = "updateVendorEventRelationship")
	public VendorEventRelationship updateZeppaEventToUserRelationship(VendorEventRelationship relationship,
			@Named("idToken") String tokenString) throws IOException, UnauthorizedException {

		// Fetch Authorized Zeppa User
		ZeppaUser user = ClientEndpointUtility.getAuthorizedZeppaUser(tokenString);
		if (user == null) {
			throw new UnauthorizedException("No matching user found for this token");
		}

		PersistenceManager mgr = getPersistenceManager();
		VendorEvent event = null;
		try {
			event = mgr.getObjectById(VendorEvent.class, relationship.getEventId());

		} catch (JDOObjectNotFoundException e) {
			e.printStackTrace();
			throw (e);
		} finally {
			mgr.close();
		}

		mgr = getPersistenceManager();
		Transaction txn = mgr.currentTransaction();
		try {
			txn.begin();

			VendorEventRelationship current = mgr.getObjectById(VendorEventRelationship.class,
					relationship.getId());

			current.setJoined(relationship.isJoined());
			current.setWatched(relationship.isWatched());
			current.setSeen(relationship.isSeen());
			current.setShared(false);

			relationship = mgr.makePersistent(current);
			
			
			txn.commit();

		} catch (javax.jdo.JDOObjectNotFoundException e) {
			e.printStackTrace();
			throw (e);
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
			mgr.close();
		}

		
		return relationship;
	}

	/**
	 * Get the persistence manager to make changes to the datastore
	 * 
	 * @return persistence manager instance
	 */
	private static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}

}