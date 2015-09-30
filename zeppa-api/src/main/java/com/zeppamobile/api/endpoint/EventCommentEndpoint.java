package com.zeppamobile.api.endpoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiReference;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.datanucleus.query.JDOCursorHelper;
import com.zeppamobile.api.notifications.NotificationUtility;
import com.zeppamobile.common.auth.Authorizer;
import com.zeppamobile.common.datamodel.EventComment;
import com.zeppamobile.common.datamodel.ZeppaEvent;
import com.zeppamobile.common.datamodel.ZeppaEventToUserRelationship;
import com.zeppamobile.common.datamodel.ZeppaUser;
import com.zeppamobile.common.utils.Utils;

@ApiReference(EndpointBase.class)
public class EventCommentEndpoint extends EndpointBase {

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
			@Nullable @Named("limit") Integer limit,
			@Named("auth") Authorizer auth) throws UnauthorizedException {

		// Authorized ZeppaUser
		ZeppaUser user = getAuthorizedZeppaUser(auth);

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
			List<Long> authedEventIds = new ArrayList<Long>(); 
			for (EventComment comment : execute) {
				if(!Utils.listContainsLong(authedEventIds, comment.getEventId())){
					ZeppaEvent event = getEventForComment(comment, auth);
					authedEventIds.add(event.getId());
				}
				
			}
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
	public EventComment getEventComment(@Named("id") Long id,
			@Named("auth") Authorizer auth) throws UnauthorizedException {

		// Authorized ZeppaUser
		ZeppaUser user = getAuthorizedZeppaUser(auth);
		

		PersistenceManager mgr = getPersistenceManager();
		EventComment eventcomment = null;
		try {
			eventcomment = mgr.getObjectById(EventComment.class, id);
			
			// Do this to verify user is allowed to get this comment
			getEventForComment(eventcomment, auth);
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
	 * @throws UnauthorizedException
	 * @throws OAuthRequestException
	 */
	@ApiMethod(name = "insertEventComment")
	public EventComment insertEventComment(EventComment eventcomment,
			@Named("auth") Authorizer auth) throws UnauthorizedException {

		if (eventcomment.getEventId() == null) {
			throw new NullPointerException("Event Id Not Set");
		}

		// Authorized ZeppaUser
		ZeppaUser user = getAuthorizedZeppaUser(auth);
		ZeppaEvent event = getEventForComment(eventcomment, auth);

		eventcomment.setCreated(System.currentTimeMillis());
		eventcomment.setUpdated(System.currentTimeMillis());

		PersistenceManager mgr = getPersistenceManager();

		try {

			// Persist the comment
			eventcomment = mgr.makePersistent(eventcomment);

			// Add relationship to user who commented
			if (user.addComment(eventcomment)) {
				updateUserRelationships(user);
			}

			// Add relationship to event commented on
			if (event.addComment(eventcomment)) {
				updateEventRelationships(event);
			}

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

	/**
	 * Fetch the event this user is commenting on
	 * 
	 * @param comment
	 * @param auth
	 * @return
	 * @throws UnauthorizedException
	 */
	private ZeppaEvent getEventForComment(EventComment comment, Authorizer auth)
			throws UnauthorizedException {
		PersistenceManager mgr = getPersistenceManager();
		ZeppaEvent event = null;
		try {
			event = mgr.getObjectById(ZeppaEvent.class, comment.getEventId());
			// Make sure user can comment on this

			boolean isAuthorized = false;
			if (event.getHostId().longValue() == auth.getUserId().longValue()) {
				// don't throw anything
				isAuthorized = true;
			} else {
				for (ZeppaEventToUserRelationship r : event
						.getAttendeeRelationships()) {
					if (r.getUserId().longValue() == auth.getUserId()
							.longValue()) {
						isAuthorized = true;
						break;
					}
				}
			}

			if (!isAuthorized) {
				throw new UnauthorizedException(
						"User is not authorized to see this event");
			}

		} finally {
			mgr.close();
		}
		return event;
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

	// /**
	// * This method removes the entity with primary key id. It uses HTTP DELETE
	// * method.
	// *
	// * @param id
	// * the primary key of the entity to be deleted.
	// * @throws OAuthRequestException
	// */
	// @ApiMethod(name = "removeEventComment")
	// public void removeEventComment(@Named("id") Long id,
	// @Named("auth") Authorizer auth) throws UnauthorizedException {
	//
	// // Authorized ZeppaUser
	// ZeppaUser user = getAuthorizedZeppaUser(auth);
	//
	// PersistenceManager mgr = getPersistenceManager();
	// try {
	// EventComment eventcomment = mgr.getObjectById(EventComment.class,
	// id);
	//
	// if (user.removeComment(eventcomment)) {
	// updateUserRelationships(user);
	// } // TODO: else, something went wrong?
	//
	// mgr.deletePersistent(eventcomment);
	//
	// } finally {
	// mgr.close();
	// }
	// }

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
	//
	// private static PersistenceManager getPersistenceManager() {
	// return PMF.get().getPersistenceManager();
	// }

}
