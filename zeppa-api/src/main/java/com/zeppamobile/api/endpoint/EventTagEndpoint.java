package com.zeppamobile.api.endpoint;

import java.util.ArrayList;
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
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.datanucleus.query.JDOCursorHelper;
import com.zeppamobile.api.Constants;
import com.zeppamobile.api.PMF;
import com.zeppamobile.api.datamodel.EventTag;
import com.zeppamobile.api.datamodel.EventTagFollow;
import com.zeppamobile.api.datamodel.ZeppaUser;
import com.zeppamobile.api.endpoint.utils.ClientEndpointUtility;
import com.zeppamobile.api.endpoint.utils.TaskUtility;
import com.zeppamobile.common.utils.Utils;

@Api(name = Constants.API_NAME, version = "v1", scopes = { Constants.EMAIL_SCOPE }, audiences = { Constants.WEB_CLIENT_ID })
public class EventTagEndpoint {

	/**
	 * This method lists all the entities inserted in datastore. It uses HTTP
	 * GET method and paging support.
	 * 
	 * @return A CollectionResponse class containing the list of all entities
	 *         persisted and a cursor to the next page.
	 * @throws OAuthRequestException
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listEventTag")
	public CollectionResponse<EventTag> listEventTag(
			@Nullable @Named("filter") String filterString,
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("ordering") String orderingString,
			@Nullable @Named("limit") Integer limit,
			@Named("idToken") String tokenString) throws UnauthorizedException {

		// Fetch Authorized Zeppa User
		ZeppaUser user = ClientEndpointUtility
				.getAuthorizedZeppaUser(tokenString);
		if (user == null) {
			throw new UnauthorizedException(
					"No matching user found for this token");
		}

		PersistenceManager mgr = null;
		Cursor cursor = null;
		List<EventTag> execute = null;

		try {
			mgr = getPersistenceManager();
			Query query = mgr.newQuery(EventTag.class);
			if (Utils.isWebSafe(cursorString)) {
				cursor = Cursor.fromWebSafeString(cursorString);
				HashMap<String, Object> extensionMap = new HashMap<String, Object>();
				extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
				query.setExtensions(extensionMap);
			}

			if (Utils.isWebSafe(filterString)) {
				query.setFilter(filterString);
			}

			if (Utils.isWebSafe(orderingString)) {
				query.setOrdering(orderingString);
			}

			if (limit != null) {
				query.setRange(0, limit);
			}

			execute = (List<EventTag>) query.execute();

			cursor = JDOCursorHelper.getCursor(execute);
			if (cursor == null) {
				cursorString = null;
			} else {
				cursorString = cursor.toWebSafeString();
			}

			// Tight loop for fetching all entities from datastore and
			// accomodate
			// for lazy fetch.
			for (EventTag obj : execute)
				;
		} finally {
			mgr.close();
		}

		return CollectionResponse.<EventTag> builder().setItems(execute)
				.setNextPageToken(cursorString).build();
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
	@ApiMethod(name = "getEventTag")
	public EventTag getEventTag(@Named("id") Long id,
			@Named("idToken") String tokenString) throws UnauthorizedException {

		// Fetch Authorized Zeppa User
		ZeppaUser user = ClientEndpointUtility
				.getAuthorizedZeppaUser(tokenString);
		if (user == null) {
			throw new UnauthorizedException(
					"No matching user found for this token");
		}

		PersistenceManager mgr = getPersistenceManager();
		EventTag eventtag = null;
		try {
			eventtag = mgr.getObjectById(EventTag.class, id);
		} finally {
			mgr.close();
		}
		return eventtag;
	}

	/**
	 * 
	 * @param eventtag
	 *            the entity to be inserted.
	 * @return The inserted entity.
	 * @throws OAuthRequestException
	 */
	@ApiMethod(name = "insertEventTag")
	public EventTag insertEventTag(EventTag eventtag,
			@Named("idToken") String tokenString) throws UnauthorizedException {

		if (eventtag.getOwnerId() == null) {
			throw new NullPointerException("Event Tag Must Specify OwnerId");
		}

		// Fetch Authorized Zeppa User
		ZeppaUser user = ClientEndpointUtility
				.getAuthorizedZeppaUser(tokenString);
		if (user == null) {
			throw new UnauthorizedException(
					"No matching user found for this token");
		}

		if (eventtag.getOwnerId().longValue() != user.getId().longValue()) {
			throw new UnauthorizedException("Cannot make tags for other people");
		}

		eventtag.setCreated(System.currentTimeMillis());
		eventtag.setUpdated(System.currentTimeMillis());

		PersistenceManager mgr = getPersistenceManager();
		// Persistence manager for making changes to the user
		// PersistenceManager umgr = getPersistenceManager();
		Transaction txn = mgr.currentTransaction();
		try {
			txn.begin();
			// Reopen the user with a persistence manager to make changes
			user = mgr.getObjectById(ZeppaUser.class, user.getKey());
			// Set the owner user
			eventtag.setOwner(user);
			// Update the user relationships
			if (user.addTag(eventtag)) {
				System.out.println("Mapped tag to user");
				ClientEndpointUtility.updateUserEntityRelationships(user);
			} else {
				System.out.println("Did not map tag to user");
			}

			// Update and store objects
			eventtag = mgr.makePersistent(eventtag);
			txn.commit();
		} finally {
			// Close the persistence managers so changes are made to data
			if (txn.isActive()) {
				System.out.println("Transaction rolled back");
				txn.rollback();
				eventtag = null;
			}
			mgr.close();
		}

		return eventtag;
	}

	/**
	 * This method removes the entity with primary key id. It uses HTTP DELETE
	 * method.
	 * 
	 * @param id
	 *            the primary key of the entity to be deleted.
	 * @throws OAuthRequestException
	 */
	@ApiMethod(name = "removeEventTag")
	public void removeEventTag(@Named("tagId") Long tagId,
			@Named("idToken") String tokenString) throws UnauthorizedException {

		// Fetch Authorized Zeppa User
		ZeppaUser user = ClientEndpointUtility
				.getAuthorizedZeppaUser(tokenString);
		if (user == null) {
			throw new UnauthorizedException(
					"No matching user found for this token");
		}

		PersistenceManager mgr = getPersistenceManager();
		PersistenceManager rmgr = getPersistenceManager();
		try {
			EventTag eventtag = mgr.getObjectById(EventTag.class, tagId);

			// Make sure user is allowed to access this item
			if (user.getId().longValue() == eventtag.getOwnerId().longValue()) {
				TaskUtility.scheduleDeleteTagFollows(tagId);
				Transaction txn = rmgr.currentTransaction();

				txn.begin();

				List<Key> followKeys = eventtag.getFollowKeys();

				if (!followKeys.isEmpty()) {
					/*
					 * Iterate through the keys and kill relationships
					 */
					List<EventTagFollow> follows = new ArrayList<EventTagFollow>();
					for (Key k : followKeys) {
						try {
							EventTagFollow f = rmgr.getObjectById(
									EventTagFollow.class, k);
							follows.add(f);
						} catch (JDOObjectNotFoundException e) {
							// Follow not found... oh well
						}
					}
					// Delete all the follows
					rmgr.deletePersistentAll(follows);
				}

				mgr.deletePersistent(eventtag);
			} else {
				throw new UnauthorizedException(
						"Cannot delete a tag you don't own");
			}

		} catch (javax.jdo.JDOObjectNotFoundException e) {
			e.printStackTrace();
		} finally {
			mgr.close();
		}
	}

	private static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}

}
