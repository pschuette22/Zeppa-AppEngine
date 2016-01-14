package com.zeppamobile.api.endpoint;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;
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
import com.zeppamobile.api.datamodel.EventComment;
import com.zeppamobile.api.datamodel.ZeppaEvent;
import com.zeppamobile.api.datamodel.ZeppaEventToUserRelationship;
import com.zeppamobile.api.datamodel.ZeppaUser;
import com.zeppamobile.api.endpoint.utils.ClientEndpointUtility;
import com.zeppamobile.api.notifications.NotificationUtility;
import com.zeppamobile.common.utils.Utils;

@Api(name = Constants.API_NAME, version = "v1", scopes = { Constants.EMAIL_SCOPE }, audiences = { Constants.WEB_CLIENT_ID })
public class EventCommentEndpoint {

	/**
	 * This method lists all the entities inserted in datastore. It uses HTTP
	 * GET method and paging support.
	 * 
	 * @return A CollectionResponse class containing the list of all entities
	 *         persisted and a cursor to the next page.
	 * @throws OAuthRequestException
	 */
	@SuppressWarnings("unchecked")
	@ApiMethod(name = "listEventComment", path = "listEventComment")
	public CollectionResponse<EventComment> listEventComment(
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

			// // Tight loop for fetching all entities from datastore and
			// // accomodate
			// // for lazy fetch.
			// List<Long> authedEventIds = new ArrayList<Long>();
			// List<Long> unauthorizedEventIds = new ArrayList<Long>();
			for (EventComment comment : execute) {
				// Touch the fields we want
				comment.getKey();
				comment.getCreated();
				comment.getUpdated();
				comment.getCommenterId();
				comment.getEventId();
				comment.getText();

				// if (!Utils.listContainsLong(authedEventIds,
				// comment.getEventId())
				// && !Utils.listContainsLong(unauthorizedEventIds,
				// comment.getEventId())) {
				// ZeppaEvent event = comment.getEvent();
				// if (event.isAuthorized(user.getId().longValue())) {
				// authedEventIds.add(event.getId());
				// } else {
				// unauthorizedEventIds.add(event.getId());
				// }
				// }

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
	@ApiMethod(name = "getEventComment", path = "getEventComment")
	public EventComment getEventComment(@Named("id") Long id,
			@Named("idToken") String tokenString) throws UnauthorizedException {

		// Fetch Authorized Zeppa User
		ZeppaUser user = ClientEndpointUtility
				.getAuthorizedZeppaUser(tokenString);
		if (user == null) {
			throw new UnauthorizedException(
					"No matching user found for this token");
		}

		PersistenceManager mgr = getPersistenceManager();
		EventComment eventcomment = null;
		try {
			eventcomment = mgr.getObjectById(EventComment.class, id);

			// TODO: verify this user can get the requested comment
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
			@Named("idToken") String tokenString) throws UnauthorizedException {

		if (eventcomment.getEventId() == null) {
			throw new NullPointerException("Event Id Not Set");
		}

		// Authorized ZeppaUser
		// Fetch Authorized Zeppa User
		ZeppaUser user = ClientEndpointUtility
				.getAuthorizedZeppaUser(tokenString);
		if (user == null) {
			throw new UnauthorizedException(
					"No matching user found for this token");
		}

		PersistenceManager mgr = getPersistenceManager();
		Transaction txn = mgr.currentTransaction();
		try {

			txn.begin();

			// Fetch the event to make sure it exists
			ZeppaEvent e = mgr.getObjectById(ZeppaEvent.class,
					eventcomment.getEventId());
			
			// If the commenter is not the host of the event, make sure they
			// have a relationship to it
			if (e.getHostId().longValue() != user.getId().longValue()) {
				@SuppressWarnings("unchecked")
				Collection<ZeppaEventToUserRelationship> c = (Collection<ZeppaEventToUserRelationship>) mgr
						.newQuery(
								ZeppaEventToUserRelationship.class,
								"eventId==" + e.getId() + " && userId=="
										+ user.getId()).execute();
				if (c == null || c.isEmpty()) {
					throw new UnauthorizedException(
							"Cannot comment on events you don't hold a relationship to");
				}
			}

			eventcomment.setCreated(System.currentTimeMillis());
			eventcomment.setUpdated(System.currentTimeMillis());

			// Persist the comment
			eventcomment = mgr.makePersistent(eventcomment);

			txn.commit();

		} finally {
			if (txn.isActive()) {
				txn.rollback();
				eventcomment = null;
			}
			mgr.close();
		}

		/*
		 * Schedule appropriate notifications to be built and delivered 
		 */
		if (eventcomment.getId() != null) {
			NotificationUtility.scheduleNotificationBuild(
					EventComment.class.getName(), eventcomment.getId(),
					"comment-posted");
		}

		return eventcomment;
	}

	// /**
	// * Fetch the event this user is commenting on
	// *
	// * @param comment
	// * @param auth
	// * @return
	// * @throws UnauthorizedException
	// */
	// private ZeppaEvent getEventForComment(EventComment comment, ZeppaUser
	// user) throws UnauthorizedException {
	// PersistenceManager mgr = getPersistenceManager();
	// ZeppaEvent event = null;
	// try {
	// event = mgr.getObjectById(ZeppaEvent.class, comment.getEventId());
	// // Make sure user can comment on this
	//
	// if (!event.isAuthorized(user.getId().longValue())) {
	// throw new UnauthorizedException(
	// "User is not authorized to see this event");
	// }
	//
	// } finally {
	// mgr.close();
	// }
	// return event;
	// }

	private static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}

}
