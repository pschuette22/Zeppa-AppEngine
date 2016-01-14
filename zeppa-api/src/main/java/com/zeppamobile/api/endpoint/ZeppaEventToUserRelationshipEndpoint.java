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
import com.zeppamobile.api.datamodel.ZeppaEvent;
import com.zeppamobile.api.datamodel.ZeppaEventToUserRelationship;
import com.zeppamobile.api.datamodel.ZeppaUser;
import com.zeppamobile.api.endpoint.utils.ClientEndpointUtility;
import com.zeppamobile.api.googlecalendar.GoogleCalendarService;
import com.zeppamobile.api.notifications.NotificationUtility;
import com.zeppamobile.common.utils.Utils;

@Api(name = Constants.API_NAME, version = "v1", scopes = { Constants.EMAIL_SCOPE }, audiences = { Constants.WEB_CLIENT_ID })
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
		List<ZeppaEventToUserRelationship> execute = null;

		try {
			mgr = getPersistenceManager();
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

//			/*
//			 * Initialize object and remove bad eggs Badeggs are relationships
//			 * to events user
//			 */
//			List<ZeppaEventToUserRelationship> badEggs = new ArrayList<ZeppaEventToUserRelationship>();
//			for (ZeppaEventToUserRelationship relationship : execute) {
//				ZeppaEvent event = relationship.getEvent();
//				if (!event.isAuthorized(user.getId().longValue())) {
//					badEggs.add(relationship);
//				}
//			}
//			execute.removeAll(badEggs);

		} finally {
			mgr.close();
		}

		return CollectionResponse.<ZeppaEventToUserRelationship> builder()
				.setItems(execute).setNextPageToken(cursorString).build();
	}

//	/**
//	 * This method gets the entity having primary key id. It uses HTTP GET
//	 * method.
//	 * 
//	 * @param id
//	 *            the primary key of the java bean.
//	 * @return The entity with primary key id.
//	 * @throws OAuthRequestException
//	 */
//	@ApiMethod(name = "getZeppaEventToUserRelationship")
//	public ZeppaEventToUserRelationship getZeppaEventToUserRelationship(
//			Key key, @Named("idToken") String tokenString)
//			throws UnauthorizedException {
//
//		// Fetch Authorized Zeppa User
//		ZeppaUser user = ClientEndpointUtility
//				.getAuthorizedZeppaUser(tokenString);
//		if (user == null) {
//			throw new UnauthorizedException(
//					"No matching user found for this token");
//		}
//
//		PersistenceManager mgr = getPersistenceManager();
//		ZeppaEventToUserRelationship zeppaeventtouserrelationship = null;
//		try {
//			zeppaeventtouserrelationship = mgr.getObjectById(
//					ZeppaEventToUserRelationship.class, key);
//
//		} catch (javax.jdo.JDOObjectNotFoundException ex) {
//			ex.printStackTrace();
//			throw ex;
//		} finally {
//			mgr.close();
//		}
//		return zeppaeventtouserrelationship;
//	}

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
			@Named("idToken") String tokenString) throws UnauthorizedException {

		// Fetch Authorized Zeppa User
		ZeppaUser user = ClientEndpointUtility
				.getAuthorizedZeppaUser(tokenString);
		if (user == null) {
			throw new UnauthorizedException(
					"No matching user found for this token");
		}

		if (relationship.getEventId() == null) {
			throw new NullPointerException("Null Event Id");
		}
		

		PersistenceManager mgr = getPersistenceManager();
		Transaction txn = mgr.currentTransaction();
		try {
			
			
			txn.begin();
			ZeppaEvent event = mgr.getObjectById(ZeppaEvent.class,
					relationship.getEventId());
			

			// Update relationship values
			relationship.setEventHostId(event.getHostId());
			relationship.setExpires(event.getEnd());
			relationship.setIsRecommended(Boolean.FALSE);
			relationship.setIsWatching(Boolean.FALSE);
			relationship.setIsAttending(Boolean.FALSE);
			relationship.setWasInvited(relationship.getWasInvited());
			if(relationship.getWasInvited()){
				relationship.setInvitedByUserId(relationship.getInvitedByUserId());
			} else {
				relationship.setInvitedByUserId(Long.valueOf(-1));
			}
			relationship.setCreated(System.currentTimeMillis());
			relationship.setUpdated(System.currentTimeMillis());

			relationship = mgr.makePersistent(relationship);


			// If relationship is inserted via http, should be an invite
			if (relationship.getWasInvited()) {

				NotificationUtility.scheduleNotificationBuild(
						ZeppaEventToUserRelationship.class.getName(),
						relationship.getId(), "invited");
			}
			txn.commit();

		} finally {
			if(txn.isActive()){
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
	@ApiMethod(name = "updateZeppaEventToUserRelationship")
	public ZeppaEventToUserRelationship updateZeppaEventToUserRelationship(
			ZeppaEventToUserRelationship relationship,
			@Named("idToken") String tokenString) throws IOException,
			UnauthorizedException {

		// Fetch Authorized Zeppa User
		ZeppaUser user = ClientEndpointUtility
				.getAuthorizedZeppaUser(tokenString);
		if (user == null) {
			throw new UnauthorizedException(
					"No matching user found for this token");
		}

		PersistenceManager mgr = getPersistenceManager();
		ZeppaEvent event = null;
		try {
			event = mgr.getObjectById(ZeppaEvent.class,
					relationship.getEventId());

		} catch (JDOObjectNotFoundException e) {
			e.printStackTrace();
			throw (e);
		} finally {
			mgr.close();
		}

		mgr = getPersistenceManager();
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

		mgr = getPersistenceManager();
		Transaction txn = mgr.currentTransaction();
		try {
			txn.begin();

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

			relationship = mgr.makePersistent(current);
			txn.commit();

		} catch (javax.jdo.JDOObjectNotFoundException e) {
			e.printStackTrace();
			throw (e);
		} finally {
			if (txn.isActive()) {
				txn.rollback();
				action = Resources.UpdateEventRelationshipNotificationAction.NONE;
			}
			mgr.close();
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

	private static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}

}
