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
import com.google.appengine.api.users.User;
import com.google.appengine.datanucleus.query.JDOCursorHelper;
import com.zeppamobile.api.Constants;
import com.zeppamobile.api.PMF;
import com.zeppamobile.api.Utils;
import com.zeppamobile.api.datamodel.EventTag;
import com.zeppamobile.api.endpoint.Utils.TaskUtility;

@Api(name = "eventtagendpoint", version = "v1", scopes = { Constants.EMAIL_SCOPE }, clientIds = {
		Constants.WEB_CLIENT_ID, Constants.ANDROID_DEBUG_CLIENT_ID,
		Constants.ANDROID_RELEASE_CLIENT_ID, Constants.IOS_DEBUG_CLIENT_ID,
		Constants.IOS_CLIENT_ID_OLD }, audiences = { Constants.WEB_CLIENT_ID })
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
			@Nullable @Named("limit") Integer limit, User user)
			throws OAuthRequestException {

		if (Constants.PRODUCTION && user == null) {
			throw new OAuthRequestException("Unauthorized call");
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
	public EventTag getEventTag(@Named("id") Long id, User user)
			throws OAuthRequestException {

		if (Constants.PRODUCTION && user == null) {
			throw new OAuthRequestException("Unauthorized call");
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
	public EventTag insertEventTag(EventTag eventtag, User user)
			throws OAuthRequestException {

		if (Constants.PRODUCTION && user == null) {
			throw new OAuthRequestException("Unauthorized call");
		}

		if (eventtag.getOwnerId() == null) {
			throw new NullPointerException("Event Tag Must Specify OwnerId");
		}

		eventtag.setCreated(System.currentTimeMillis());
		eventtag.setUpdated(System.currentTimeMillis());

		PersistenceManager mgr = getPersistenceManager();

		try {

			// Update and store objects
			mgr.makePersistent(eventtag);

		} catch (javax.jdo.JDOObjectNotFoundException e) {
			e.printStackTrace();
			eventtag = null;
		} finally {

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
	public void removeEventTag(@Named("tagId") Long tagId, User user)
			throws OAuthRequestException {

		if (Constants.PRODUCTION && user == null) {
			throw new OAuthRequestException("Unauthorized call");
		}

		PersistenceManager mgr = getPersistenceManager();
		try {
			EventTag eventtag = mgr.getObjectById(EventTag.class, tagId);
			TaskUtility.scheduleDeleteTagFollows(tagId);

			mgr.deletePersistent(eventtag);

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
