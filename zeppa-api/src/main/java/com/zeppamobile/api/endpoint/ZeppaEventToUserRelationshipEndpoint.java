package com.zeppamobile.api.endpoint;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;
import javax.jdo.JDOObjectNotFoundException;
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
import com.zeppamobile.api.Resources;
import com.zeppamobile.api.endpoint.utils.ClientEndpointUtility;
import com.zeppamobile.api.notifications.NotificationUtility;
import com.zeppamobile.common.auth.Authorizer;
import com.zeppamobile.common.datamodel.ZeppaEvent;
import com.zeppamobile.common.datamodel.ZeppaEventToUserRelationship;
import com.zeppamobile.common.datamodel.ZeppaUser;
import com.zeppamobile.common.googlecalendar.GoogleCalendarService;
import com.zeppamobile.common.utils.Utils;

@ApiReference(AppInfoEndpoint.class)
public class ZeppaEventToUserRelationshipEndpoint {

	/**
	 * This method lists all the entities inserted in datastore. It uses HTTP
	 * GET method and paging support.
	 * 
	 * @return A CollectionResponse class containing the list of all entities
	 *         persisted and a cursor to the next page.
	 * @throws OAuthRequestException
	 */
	@SuppressWarnings("unchecked")
	@ApiMethod(name = "listZeppaEventToUserRelationship")
	public CollectionResponse<ZeppaEventToUserRelationship> listZeppaEventToUserRelationship(
			@Nullable @Named("filter") String filterString,
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("ordering") String orderingString,
			@Nullable @Named("limit") Integer limit,
			@Named("auth") Authorizer auth) throws UnauthorizedException {

		ZeppaUser user = ClientEndpointUtility.getAuthorizedZeppaUser(auth);

		PersistenceManager mgr = null;
		Cursor cursor = null;
		List<ZeppaEventToUserRelationship> execute = null;

		try {
			mgr = ClientEndpointUtility.getPersistenceManager();
			Query query = mgr.newQuery(ZeppaEventToUserRelationship.class);
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

			execute = (List<ZeppaEventToUserRelationship>) query.execute();

			cursor = JDOCursorHelper.getCursor(execute);
			if (cursor == null) {
				cursorString = null;
			} else {
				cursorString = cursor.toWebSafeString();
			}

			/*
			 * Initialize object and remove bad eggs Badeggs are relationships
			 * to events user
			 */
			List<ZeppaEventToUserRelationship> badEggs = new ArrayList<ZeppaEventToUserRelationship>();
			for (ZeppaEventToUserRelationship relationship : execute) {
				ZeppaEvent event = relationship.getEvent();
				if (!event.isAuthorized(user.getId().longValue())) {
					badEggs.add(relationship);
				}
			}
			execute.removeAll(badEggs);

		} finally {
			mgr.close();
		}

		return CollectionResponse.<ZeppaEventToUserRelationship> builder()
				.setItems(execute).setNextPageToken(cursorString).build();
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
	@ApiMethod(name = "getZeppaEventToUserRelationship")
	public ZeppaEventToUserRelationship getZeppaEventToUserRelationship(
			@Named("id") Long id, @Named("auth") Authorizer auth)
			throws UnauthorizedException {

		ZeppaUser user = ClientEndpointUtility.getAuthorizedZeppaUser(auth);

		PersistenceManager mgr = ClientEndpointUtility.getPersistenceManager();
		ZeppaEventToUserRelationship zeppaeventtouserrelationship = null;
		try {
			zeppaeventtouserrelationship = mgr.getObjectById(
					ZeppaEventToUserRelationship.class, id);

		} catch (javax.jdo.JDOObjectNotFoundException ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			mgr.close();
		}
		return zeppaeventtouserrelationship;
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
	@ApiMethod(name = "insertZeppaEventToUserRelationship")
	public ZeppaEventToUserRelationship insertZeppaEventToUserRelationship(
			ZeppaEventToUserRelationship relationship,
			@Named("auth") Authorizer auth) throws UnauthorizedException {

		ZeppaUser user = ClientEndpointUtility.getAuthorizedZeppaUser(auth);

		if (relationship.getEventId() == null) {
			throw new NullPointerException("Null Event Id");
		}

		relationship.setCreated(System.currentTimeMillis());
		relationship.setUpdated(System.currentTimeMillis());

		PersistenceManager mgr = ClientEndpointUtility.getPersistenceManager();
		PersistenceManager emgr = ClientEndpointUtility.getPersistenceManager();
		PersistenceManager umgr = ClientEndpointUtility.getPersistenceManager();
		try {
			ZeppaEvent event = emgr.getObjectById(ZeppaEvent.class,
					relationship.getEventId());
			// Make sure user doesn't already have a relationship
			if(!event.isAuthorized(user.getId().longValue())){
				throw new UnauthorizedException("Cannot send invites for this event");
			}
			
			// Check to see if user already has relationship to this event
			for(ZeppaEventToUserRelationship r: event.getAttendeeRelationships()){
				if(r.getUserId().longValue() == relationship.getUserId().longValue()){
					// Throw an exception?
					return r;
				}
			}
			
			ZeppaUser attendee = umgr.getObjectById(ZeppaUser.class, relationship.getUserId());
			
			// Update relationship values
			relationship.setEventHostId(event.getHostId());
			relationship.setExpires(event.getEnd());
			relationship.setIsWatching(Boolean.FALSE);
			relationship.setIsAttending(Boolean.FALSE);
			relationship.setAttendee(attendee);
			relationship.setEvent(event);
			
			relationship = mgr.makePersistent(relationship);
			
			event.addAttendeeRelationship(relationship);
			attendee.addEventRelationship(relationship);
			
			// Update entity relationships
			ClientEndpointUtility.updateEventRelationships(event);
			ClientEndpointUtility.updateUserRelationships(attendee);
			
			// If relationship is inserted via http, should be an invite
			if (relationship.getWasInvited()) {

				NotificationUtility.scheduleNotificationBuild(
						ZeppaEventToUserRelationship.class.getName(),
						relationship.getId(), "invited");

			}
			
		} finally {
			mgr.close();
			emgr.close();
			umgr.close();
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
	@ApiMethod(name = "updateZeppaEventToUserRelationship")
	public ZeppaEventToUserRelationship updateZeppaEventToUserRelationship(
			ZeppaEventToUserRelationship relationship,
			@Named("auth") Authorizer auth) throws IOException,
			UnauthorizedException {

		ZeppaUser user = ClientEndpointUtility.getAuthorizedZeppaUser(auth);

		PersistenceManager mgr = ClientEndpointUtility.getPersistenceManager();
		ZeppaEvent event = null;
		try {
			event = mgr.getObjectById(ZeppaEvent.class,
					relationship.getEventId());
			if (!event.isAuthorized(user.getId().longValue())) {
				throw new UnauthorizedException(
						"User isnt authorized to see this event");
			}

		} catch (JDOObjectNotFoundException e) {
			e.printStackTrace();
			throw (e);
		} finally {
			mgr.close();
		}

		mgr = ClientEndpointUtility.getPersistenceManager();
		ZeppaUser zeppaUser = null;

		try {
			zeppaUser = mgr.getObjectById(ZeppaUser.class,
					relationship.getUserId());
		} catch (JDOObjectNotFoundException e) {
			e.printStackTrace();
			throw (e);
		} finally {
			mgr.close();
		}

		Resources.UpdateEventRelationshipNotificationAction action = Resources.UpdateEventRelationshipNotificationAction.NONE;

		mgr = ClientEndpointUtility.getPersistenceManager();
		try {
			ZeppaEventToUserRelationship current = mgr.getObjectById(
					ZeppaEventToUserRelationship.class, relationship.getId());

			if (current.getIsAttending() && !relationship.getIsAttending()) {
				// User left event
				action = Resources.UpdateEventRelationshipNotificationAction.USER_LEFT;

				GoogleCalendarService.leaveEvent(event, zeppaUser);

			} else if (!current.getIsAttending()
					&& relationship.getIsAttending()) {

				// User joined event
				action = Resources.UpdateEventRelationshipNotificationAction.USER_JOINED;
				GoogleCalendarService.joinEvent(event, zeppaUser);
			} else if (!current.getWasInvited() && relationship.getWasInvited()) {
				action = Resources.UpdateEventRelationshipNotificationAction.USER_INVITED;
			}

			current.setInvitedByUserId(relationship.getInvitedByUserId());
			current.setIsAttending(relationship.getIsAttending());
			current.setIsWatching(relationship.getIsWatching());
			current.setIsRecommended(relationship.getIsRecommended());
			current.setWasInvited(relationship.getWasInvited());
			current.setUpdated(System.currentTimeMillis());

			mgr.currentTransaction().begin();
			relationship = mgr.makePersistent(current);
			mgr.currentTransaction().commit();

		} catch (javax.jdo.JDOObjectNotFoundException e) {
			e.printStackTrace();
			throw (e);
		} finally {
			if (mgr.currentTransaction().isActive()) {
				mgr.currentTransaction().rollback();
				action = Resources.UpdateEventRelationshipNotificationAction.NONE;
			}

		}

		// Send notification if appropriate
		if (action == Resources.UpdateEventRelationshipNotificationAction.USER_JOINED) {
			NotificationUtility.scheduleNotificationBuild(
					ZeppaEventToUserRelationship.class.getName(),
					relationship.getId(), "joined");

		} else if (action == Resources.UpdateEventRelationshipNotificationAction.USER_LEFT) {
			NotificationUtility.scheduleNotificationBuild(
					ZeppaEventToUserRelationship.class.getName(),
					relationship.getId(), "left");

		} else if (action == Resources.UpdateEventRelationshipNotificationAction.USER_INVITED) {
			NotificationUtility.scheduleNotificationBuild(
					ZeppaEventToUserRelationship.class.getName(),
					relationship.getId(), "invited");
		}

		return relationship;
	}

	/**
	 * This method removes the entity with primary key id. It uses HTTP DELETE
	 * method.
	 * 
	 * @param id
	 *            the primary key of the entity to be deleted.
	 * @throws OAuthRequestException
	 */
	@ApiMethod(name = "removeZeppaEventToUserRelationship")
	public void removeZeppaEventToUserRelationship(@Named("id") Long id,
			@Named("auth") Authorizer auth) throws UnauthorizedException {

		ZeppaUser user = ClientEndpointUtility.getAuthorizedZeppaUser(auth);

		PersistenceManager mgr = ClientEndpointUtility.getPersistenceManager();
		try {
			ZeppaEventToUserRelationship zeppaeventtouserrelationship = mgr
					.getObjectById(ZeppaEventToUserRelationship.class, id);

			if (user.getId().longValue() == zeppaeventtouserrelationship
					.getEventHostId().longValue()
					|| user.getId().longValue() == zeppaeventtouserrelationship
							.getUserId().longValue()) {

				mgr.deletePersistent(zeppaeventtouserrelationship);
			} else {
				throw new UnauthorizedException(
						"Not authorized to remove this Event Relationship");
			}
		} catch (javax.jdo.JDOObjectNotFoundException ex) {
			// Object already deleted or something. all good.
			ex.printStackTrace();
		} finally {
			mgr.close();
		}
	}

}
