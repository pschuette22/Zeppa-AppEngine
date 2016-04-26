package com.zeppamobile.api.endpoint;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

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
import com.zeppamobile.api.datamodel.VendorEvent;
import com.zeppamobile.api.datamodel.VendorEventRelationship;
import com.zeppamobile.api.datamodel.ZeppaUser;
import com.zeppamobile.api.endpoint.utils.ClientEndpointUtility;
import com.zeppamobile.common.utils.Utils;

@Api(name = Constants.API_NAME, version = "v1", scopes = { Constants.EMAIL_SCOPE }, audiences = {
		Constants.WEB_CLIENT_ID })
public class VendorEventEndpoint {

	/**
	 * This method lists all the entities inserted in datastore. It uses HTTP
	 * GET method and paging support.
	 * 
	 * @return A CollectionResponse class containing the list of all entities
	 *         persisted and a cursor to the next page.
	 * @throws OAuthRequestException
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listVendorEvent", path = "listVendorEvent")
	public CollectionResponse<VendorEvent> listVendorEvent(@Nullable @Named("filter") String filterString,
			@Nullable @Named("cursor") String cursorString, @Nullable @Named("ordering") String orderingString,
			@Nullable @Named("limit") Integer limit, @Named("idToken") String tokenString)
			throws UnauthorizedException {

		// Fetch Authorized Zeppa User
		ZeppaUser user = ClientEndpointUtility.getAuthorizedZeppaUser(tokenString);
		if (user == null) {
			throw new UnauthorizedException("No matching user found for this token");
		}

		PersistenceManager mgr = null;
		Cursor cursor = null;
		List<VendorEvent> execute = null;

		try {
			// Initialize the query process
			mgr = getPersistenceManager();
			Query query = mgr.newQuery(VendorEvent.class);
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
			execute = (List<VendorEvent>) query.execute();

			cursor = JDOCursorHelper.getCursor(execute);
			if (cursor == null) {
				cursorString = null;
			} else {
				cursorString = cursor.toWebSafeString();
			}

			// Tight loop for fetching all entities from datastore and
			// accomodate
			// for lazy fetch.
			for (VendorEvent obj : execute) {
				Query rQuery = mgr.newQuery(VendorEventRelationship.class,
						"userId==" + user.getId() + " && eventId==" + obj.getId());
				rQuery.setUnique(true);
				try {
					VendorEventRelationship r = (VendorEventRelationship) rQuery.execute();
					if (r != null) {
						// Touch all the needed fields
						r.getKey();
						r.getId();
						r.getEventId();
						r.getUserId();
						r.isJoined();
						r.isSeen();
						r.isWatched();
						r.isShared();

						obj.setRelationship(r);
					}
				} catch (JDOObjectNotFoundException e) {

				}
			}
		} finally {
			mgr.close();
		}

		// Return That Collection!
		return CollectionResponse.<VendorEvent> builder().setItems(execute).setNextPageToken(cursorString).build();
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

	@ApiMethod(name = "getVendorEvent", path = "getVendorEvent")
	public VendorEvent getVendorEvent(@Named("id") Long id, @Named("idToken") String tokenString)
			throws UnauthorizedException {

		// Fetch Authorized Zeppa User
		ZeppaUser user = ClientEndpointUtility.getAuthorizedZeppaUser(tokenString);
		if (user == null) {
			throw new UnauthorizedException("No matching user found for this token");
		}

		PersistenceManager mgr = getPersistenceManager();
		VendorEvent VendorEvent = null;
		try {
			VendorEvent = mgr.getObjectById(VendorEvent.class, id);
		} finally {
			mgr.close();
		}
		return VendorEvent;
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
