package com.zeppamobile.api.endpoint;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.datanucleus.query.JDOCursorHelper;
import com.zeppamobile.api.Constants;
import com.zeppamobile.api.PMF;
import com.zeppamobile.api.Utils;
import com.zeppamobile.api.endpoint.utils.NotificationUtility;
import com.zeppamobile.common.datamodel.EventComment;

@Api(name = "eventcommentendpoint", version = "v1", scopes = { Constants.EMAIL_SCOPE }, clientIds = {
		Constants.WEB_CLIENT_ID, Constants.TYPE_OTHER_CLIENT_ID,
		Constants.ANDROID_DEBUG_CLIENT_ID, Constants.ANDROID_RELEASE_CLIENT_ID,
		Constants.IOS_DEBUG_CLIENT_ID, Constants.IOS_CLIENT_ID_OLD }, audiences = { Constants.WEB_CLIENT_ID })
public class EventCommentEndpoint {

	/**
	 * This method lists all the entities inserted in datastore. It uses HTTP
	 * GET method and paging support.
	 * 
	 * @return A CollectionResponse class containing the list of all entities
	 *         persisted and a cursor to the next page.
	 * @throws OAuthRequestException
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listEventComment")
	public CollectionResponse<EventComment> listEventComment(
			@Nullable @Named("filter") String filterString,
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("ordering") String orderingString,
			@Nullable @Named("limit") Integer limit) {

		PersistenceManager mgr = null;
		Cursor cursor = null;
		List<EventComment> execute = null;

		try {
			mgr = getPersistenceManager();
			Query query = mgr.newQuery(EventComment.class);
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

			execute = (List<EventComment>) query.execute();

			cursor = JDOCursorHelper.getCursor(execute);

			if (cursor == null) {
				cursorString = null;
			} else {
				cursorString = cursor.toWebSafeString();
			}

			// Tight loop for fetching all entities from datastore and
			// accomodate
			// for lazy fetch.
			for (EventComment obj : execute)
				;
		} finally {
			mgr.close();
		}

		return CollectionResponse.<EventComment> builder().setItems(execute)
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
	@ApiMethod(name = "getEventComment")
	public EventComment getEventComment(@Named("id") Long id) {

		PersistenceManager mgr = getPersistenceManager();
		EventComment eventcomment = null;
		try {
			eventcomment = mgr.getObjectById(EventComment.class, id);
		} finally {
			mgr.close();
		}
		return eventcomment;
	}

	/**
	 * This inserts a new entity into App Engine datastore. If the entity
	 * already exists in the datastore, an exception is thrown. It uses HTTP
	 * POST method.
	 * 
	 * @param eventcomment
	 *            the entity to be inserted.
	 * @return The inserted entity.
	 * @throws OAuthRequestException
	 */
	@ApiMethod(name = "insertEventComment")
	public EventComment insertEventComment(EventComment eventcomment) {

		if (eventcomment.getEventId() == null) {
			throw new IllegalArgumentException("Event Id Not Set");
		}

		eventcomment.setCreated(System.currentTimeMillis());
		eventcomment.setUpdated(System.currentTimeMillis());

		PersistenceManager mgr = getPersistenceManager();

		try {
			// If event isnt found, just throw exception and return null

			eventcomment = mgr.makePersistent(eventcomment);

		} catch (javax.jdo.JDOObjectNotFoundException e) {
			e.printStackTrace();
			eventcomment = null;
		} finally {
			mgr.close();
		}

		// If success, notify
		if (eventcomment != null) {
			NotificationUtility.scheduleNotificationBuild(
					EventComment.class.getName(), eventcomment.getId(),
					"comment-posted");
		}

		return eventcomment;
	}

	// /**
	// * This method is used for updating an existing entity. If the entity does
	// * not exist in the datastore, an exception is thrown. It uses HTTP PUT
	// * method.
	// *
	// * @param eventcomment
	// * the entity to be updated.
	// * @return The updated entity.
	// */
	// @ApiMethod(name = "updateEventComment")
	// public EventComment updateEventComment(EventComment eventcomment, User
	// user) {
	//
	//
	// eventcomment.setUpdated(System.currentTimeInMillis());
	// PersistenceManager mgr = getPersistenceManager();
	// try {
	//
	// mgr.makePersistent(eventcomment);
	// } finally {
	// mgr.close();
	// }
	// return eventcomment;
	// }

	/**
	 * This method removes the entity with primary key id. It uses HTTP DELETE
	 * method.
	 * 
	 * @param id
	 *            the primary key of the entity to be deleted.
	 * @throws OAuthRequestException
	 */
	@ApiMethod(name = "removeEventComment")
	public void removeEventComment(@Named("id") Long id) {

		PersistenceManager mgr = getPersistenceManager();
		try {
			EventComment eventcomment = mgr.getObjectById(EventComment.class,
					id);
			mgr.deletePersistent(eventcomment);
		} catch (javax.jdo.JDOObjectNotFoundException ex) {
			// comment was already deleted
		} finally {
			mgr.close();
		}
	}

	//
	// private boolean containsEventComment(EventComment eventcomment) {
	// PersistenceManager mgr = getPersistenceManager();
	// boolean contains = true;
	// try {
	// mgr.getObjectById(EventComment.class, eventcomment.getKey());
	// } catch (javax.jdo.JDOObjectNotFoundException ex) {
	// contains = false;
	// } finally {
	// mgr.close();
	// }
	// return contains;
	// }

	private static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}

}
