package com.zeppamobile.api.endpoint;

import java.io.IOException;
import java.security.GeneralSecurityException;
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
import com.zeppamobile.api.datamodel.ZeppaEvent;
import com.zeppamobile.api.datamodel.ZeppaUser;
import com.zeppamobile.api.endpoint.Utils.GoogleCalendarService;
import com.zeppamobile.api.endpoint.Utils.NotificationUtility;
import com.zeppamobile.api.endpoint.Utils.TaskUtility;

@Api(name = "zeppaeventendpoint", version = "v1", scopes = { Constants.EMAIL_SCOPE }, clientIds = {
		Constants.ANDROID_DEBUG_CLIENT_ID, Constants.ANDROID_RELEASE_CLIENT_ID,
		Constants.IOS_DEBUG_CLIENT_ID, Constants.IOS_CLIENT_ID_OLD }, audiences = { Constants.WEB_CLIENT_ID })
public class ZeppaEventEndpoint {

	// private static final String cursorString = null;

	/**
	 * This method lists all the entities inserted in datastore. It uses HTTP
	 * GET method and paging support.
	 * 
	 * @return A CollectionResponse class containing the list of all entities
	 *         persisted and a cursor to the next page.
	 * @throws OAuthRequestException
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listZeppaEvent")
	public CollectionResponse<ZeppaEvent> listZeppaEvent(
			@Nullable @Named("filter") String filterString,
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("ordering") String orderingString,
			@Nullable @Named("limit") Integer limit) {

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
			// Tight loop for fetching all entities from datastore and
			// accomodate
			// for lazy fetch.
			for (ZeppaEvent obj : execute)
				;
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
	public ZeppaEvent getZeppaEvent(@Named("id") Long id) {

		PersistenceManager mgr = getPersistenceManager();
		ZeppaEvent zeppaevent = null;
		try {
			zeppaevent = mgr.getObjectById(ZeppaEvent.class, id);
		} catch (javax.jdo.JDOObjectNotFoundException ex) {

			throw ex;
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
	public ZeppaEvent insertZeppaEvent(ZeppaEvent zeppaevent)
			throws IOException {

		if (zeppaevent.getHostId() == null) {
			throw new NullPointerException("Null Host User Id");
		}

		zeppaevent.setCreated(System.currentTimeMillis());
		zeppaevent.setUpdated(System.currentTimeMillis());

		// Manager for Zeppa User
		PersistenceManager umgr = getPersistenceManager();

		// Manager to insert zeppa event
		PersistenceManager emgr = getPersistenceManager();

		try {

			// Fetch Host User Object
			ZeppaUser zeppaUser = umgr.getObjectById(ZeppaUser.class,
					zeppaevent.getHostId());

			zeppaevent = GoogleCalendarService.insertGCalEvent(zeppaUser,
					zeppaevent);

			// Persist Event
			zeppaevent = emgr.makePersistent(zeppaevent);

			// Make Relationships to Event and Persist Them

		} finally {

			// If
			emgr.close();

		}

		TaskUtility.scheduleCreateEventRelationships(zeppaevent.getId());

		return zeppaevent;
	}

	/**
	 * This method is used for updating an existing entity. If the entity does
	 * not exist in the datastore, an exception is thrown. It uses HTTP PUT
	 * method.
	 * 
	 * @param zeppaevent
	 *            the entity to be updated.
	 * @return The updated entity.
	 * @throws OAuthRequestException
	 */
	@ApiMethod(name = "updateZeppaEvent")
	public ZeppaEvent updateZeppaEvent(ZeppaEvent zeppaevent)
			throws OAuthRequestException {

		PersistenceManager mgr = getPersistenceManager();
		try {
			ZeppaEvent current = mgr.getObjectById(ZeppaEvent.class,
					zeppaevent.getId());

			current.setTitle(zeppaevent.getTitle());
			current.setDescription(zeppaevent.getDescription());
			current.setStart(zeppaevent.getStart());
			current.setEnd(zeppaevent.getEnd());
			current.setMapsLocation(zeppaevent.getMapsLocation());
			current.setDisplayLocation(zeppaevent.getDisplayLocation());

			current.setUpdated(System.currentTimeMillis());
			mgr.makePersistent(current);
			zeppaevent = current;
		} finally {
			mgr.close();
		}
		return zeppaevent;
	}

	/**
	 * This method removes the entity with primary key id. It uses HTTP DELETE
	 * method.
	 * 
	 * @param id
	 *            the primary key of the entity to be deleted.
	 * @throws OAuthRequestException
	 */
	@ApiMethod(name = "removeZeppaEvent")
	public void removeZeppaEvent(@Named("id") Long id) {

		PersistenceManager mgr = getPersistenceManager();
		try {
			ZeppaEvent zeppaevent = mgr.getObjectById(ZeppaEvent.class, id);
			NotificationUtility.scheduleNotificationBuild(
					ZeppaEvent.class.getName(), zeppaevent.getId(),
					"deletedEvent");

			GoogleCalendarService.deleteCalendarEvent(zeppaevent);
			mgr.deletePersistent(zeppaevent);

		} catch (javax.jdo.JDOObjectNotFoundException | IOException ex) {
			ex.printStackTrace();
		} finally {
			mgr.close();
		}

	}

	// private boolean containsZeppaEvent(ZeppaEvent zeppaevent) {
	// PersistenceManager mgr = getPersistenceManager();
	// boolean contains = true;
	// try {
	// mgr.getObjectById(ZeppaEvent.class, zeppaevent.getKey());
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

	/*
	 * ------------------------- My Update Methods ------------------------
	 */

}
