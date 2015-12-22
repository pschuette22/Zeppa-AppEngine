package com.zeppamobile.api.endpoint;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;
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
import com.zeppamobile.api.datamodel.EventComment;
import com.zeppamobile.api.datamodel.ZeppaEvent;
import com.zeppamobile.api.datamodel.ZeppaEventToUserRelationship;
import com.zeppamobile.api.datamodel.ZeppaUser;
import com.zeppamobile.api.endpoint.utils.ClientEndpointUtility;
import com.zeppamobile.api.endpoint.utils.TaskUtility;
import com.zeppamobile.api.googlecalendar.GoogleCalendarService;
import com.zeppamobile.api.notifications.NotificationUtility;
import com.zeppamobile.common.utils.Utils;

@Api(name = Constants.API_NAME, version = "v1", scopes = { Constants.EMAIL_SCOPE }, audiences = { Constants.WEB_CLIENT_ID })
public class ZeppaEventEndpoint {

	// private static final String cursorString = null;

	/**
	 * This method lists all the entities inserted in datastore. It uses HTTP
	 * GET method and paging support.
	 * 
	 * @return A CollectionResponse class containing the list of all entities
	 *         persisted and a cursor to the next page.
	 * @throws UnauthorizedException
	 *             if idToken does not represent a valid user or user makes an
	 *             unauthorized request
	 *             
	 */
	@SuppressWarnings({ "unchecked" })
	@ApiMethod(name = "listZeppaEvent")
	public CollectionResponse<ZeppaEvent> listZeppaEvent(
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
		List<ZeppaEvent> execute = null;

		try {
			mgr = getPersistenceManager();
			Query query = mgr.newQuery(ZeppaEvent.class);
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

			execute = (List<ZeppaEvent>) query.execute();

			cursor = JDOCursorHelper.getCursor(execute);
			if (cursor == null) {
				cursorString = null;
			} else {
				cursorString = cursor.toWebSafeString();
			}

			/*
			 * Initialize object and remove bad eggs. 
			 * Only event owners may query for lists of events
			 */
			List<ZeppaEvent> badEggs = new ArrayList<ZeppaEvent>();
			for (ZeppaEvent event : execute) {
				if (event.getHostId().longValue() != user.getId().longValue()) {
					badEggs.add(event);
				}
			}
			execute.removeAll(badEggs);
			
			/*
			 * If user only queried for events they cannot see
			 */
			if(execute.isEmpty() && !badEggs.isEmpty()){
				// TODO: This user was being a dickhead. let them know
			}

		} finally {
			mgr.close();
		}

		return CollectionResponse.<ZeppaEvent> builder().setItems(execute)
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

	@ApiMethod(name = "getZeppaEvent")
	public ZeppaEvent getZeppaEvent(@Named("id") Long id,
			@Named("idToken") String tokenString) throws UnauthorizedException {

		// Fetch Authorized Zeppa User
		ZeppaUser user = ClientEndpointUtility
				.getAuthorizedZeppaUser(tokenString);
		if (user == null) {
			throw new UnauthorizedException(
					"No matching user found for this token");
		}

		PersistenceManager mgr = getPersistenceManager();
		ZeppaEvent zeppaevent = null;
		try {
			zeppaevent = mgr.getObjectById(ZeppaEvent.class, id);
			if (!zeppaevent.isAuthorized(user.getId().longValue())) {
				throw new UnauthorizedException("Not allowed to see this event");
			}

		} finally {
			mgr.close();
		}
		return zeppaevent;
	}

	/**
	 * This inserts a new entity into App Engine datastore. If the entity
	 * already exists in the datastore, an exception is thrown. It uses HTTP
	 * POST method.
	 * 
	 * @param zeppaevent
	 *            the entity to be inserted.
	 * @return The inserted entity.
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	@ApiMethod(name = "insertZeppaEvent")
	public ZeppaEvent insertZeppaEvent(ZeppaEvent zeppaevent,
			@Named("idToken") String tokenString) throws UnauthorizedException,
			IOException {

		// Fetch Authorized Zeppa User
		ZeppaUser user = ClientEndpointUtility
				.getAuthorizedZeppaUser(tokenString);
		if (user == null) {
			throw new UnauthorizedException(
					"No matching user found for this token");
		}

		if (user.getId().longValue() != zeppaevent.getHostId().longValue()) {
			throw new UnauthorizedException(
					"Cannot insert event for other users");
		}

		zeppaevent.setHost(user);
		zeppaevent.setCreated(System.currentTimeMillis());
		zeppaevent.setUpdated(System.currentTimeMillis());

		// Manager to insert zeppa event
		PersistenceManager emgr = getPersistenceManager();

		try {
			/*
			 * Add this event to google calendar
			 */
			zeppaevent = GoogleCalendarService
					.insertGCalEvent(user, zeppaevent);

			// Persist Event
			zeppaevent = emgr.makePersistent(zeppaevent);

			/*
			 * Establish mapped relationship to the host
			 */
			if(user.addEvent(zeppaevent)) {
				// Mapped to the host
			}
			
		} finally {

			emgr.close();

		}

		TaskUtility.scheduleCreateEventRelationships(zeppaevent.getId());

		return zeppaevent;
	}

	// /**
	// * This method is used for updating an existing entity. If the entity does
	// * not exist in the datastore, an exception is thrown. It uses HTTP PUT
	// * method.
	// *
	// * @param zeppaevent
	// * the entity to be updated.
	// * @return The updated entity.
	// * @throws OAuthRequestException
	// */
	// @ApiMethod(name = "updateZeppaEvent")
	// public ZeppaEvent updateZeppaEvent(ZeppaEvent zeppaevent,
	// @Named("idToken") String tokenString) throws UnauthorizedException {
	//
	// ZeppaUser user = getAuthorizedZeppaUser(auth);
	//
	// PersistenceManager mgr = getPersistenceManager();
	// try {
	// ZeppaEvent current = mgr.getObjectById(ZeppaEvent.class,
	// zeppaevent.getId());
	//
	// current.setTitle(zeppaevent.getTitle());
	// current.setDescription(zeppaevent.getDescription());
	// current.setStart(zeppaevent.getStart());
	// current.setEnd(zeppaevent.getEnd());
	// current.setMapsLocation(zeppaevent.getMapsLocation());
	// current.setDisplayLocation(zeppaevent.getDisplayLocation());
	//
	// current.setUpdated(System.currentTimeMillis());
	// mgr.makePersistent(current);
	// zeppaevent = current;
	// } finally {
	// mgr.close();
	// }
	// return zeppaevent;
	// }

	/**
	 * This method removes the entity with primary key id. It uses HTTP DELETE
	 * method.
	 * 
	 * @param id
	 *            the primary key of the entity to be deleted.
	 * @throws OAuthRequestException
	 */
	@ApiMethod(name = "removeZeppaEvent")
	public void removeZeppaEvent(@Named("id") Long id,
			@Named("idToken") String tokenString) throws UnauthorizedException {

		// Fetch Authorized Zeppa User
		ZeppaUser user = ClientEndpointUtility
				.getAuthorizedZeppaUser(tokenString);
		if (user == null) {
			throw new UnauthorizedException(
					"No matching user found for this token");
		}

		PersistenceManager mgr = getPersistenceManager();
		try {
			ZeppaEvent zeppaevent = mgr.getObjectById(ZeppaEvent.class, id);

			if (zeppaevent.getHostId().longValue() != user.getId().longValue()) {
				throw new UnauthorizedException(
						"Can't delete event you don't host");
			}

			/*
			 * Update db relationship between user and hosted event
			 */
			user.removeEvent(zeppaevent);
			ClientEndpointUtility.updateUserEntityRelationships(user);

			// Schedule notification to users that event was deleted
			NotificationUtility.scheduleNotificationBuild(
					ZeppaEvent.class.getName(), zeppaevent.getId(),
					"deletedEvent");

			// Remove event from calendar
			GoogleCalendarService.deleteCalendarEvent(zeppaevent);
			
			// Remove attendee mapping to event relationship
			for(ZeppaEventToUserRelationship relationship: zeppaevent.getAttendeeRelationships()){
				ZeppaUser attendee = relationship.getAttendee();
				attendee.removeEventRelationship(relationship);
			}
			
			// Remove commenter mapping to event comments
			for(EventComment comment: zeppaevent.getComments()){
				ZeppaUser commenter = comment.getCommenter();
				commenter.removeComment(comment);
			}
			
			// Remove event from datastore
			mgr.deletePersistent(zeppaevent);

		} catch (javax.jdo.JDOObjectNotFoundException | IOException ex) {
			ex.printStackTrace();
		} finally {
			mgr.close();
		}

	}

	private static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}

}
