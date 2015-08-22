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
import com.zeppamobile.api.datamodel.ZeppaFeedback;

@Api(name = "zeppafeedbackendpoint", version = "v1", scopes = { Constants.EMAIL_SCOPE }, clientIds = {
		Constants.ANDROID_DEBUG_CLIENT_ID, Constants.ANDROID_RELEASE_CLIENT_ID,
		Constants.IOS_DEBUG_CLIENT_ID, Constants.IOS_CLIENT_ID_OLD }, audiences = { Constants.WEB_CLIENT_ID })
public class ZeppaFeedbackEndpoint {

	/**
	 * This method lists all the entities inserted in datastore. It uses HTTP
	 * GET method and paging support.
	 * 
	 * @return A CollectionResponse class containing the list of all entities
	 *         persisted and a cursor to the next page.
	 * @throws OAuthRequestException
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listZeppaFeedback")
	public CollectionResponse<ZeppaFeedback> listZeppaFeedback(
			@Nullable @Named("filter") String filterString,
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("parameterDeclaration") String paramDeclaration,
			@Nullable @Named("ordering") String orderingString,
			@Nullable @Named("limit") Integer limit) {

		PersistenceManager mgr = null;
		Cursor cursor = null;
		List<ZeppaFeedback> execute = null;

		try {
			mgr = getPersistenceManager();
			Query query = mgr.newQuery(ZeppaFeedback.class);
			if (Utils.isWebSafe(cursorString)) {
				cursor = Cursor.fromWebSafeString(cursorString);
				HashMap<String, Object> extensionMap = new HashMap<String, Object>();
				extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
				query.setExtensions(extensionMap);
			}

			if (Utils.isWebSafe(filterString)) {
				query.setFilter(filterString);
			}

			if (Utils.isWebSafe(paramDeclaration)) {
				query.declareParameters(paramDeclaration);
			}

			if (Utils.isWebSafe(orderingString)) {
				query.setOrdering(orderingString);
			}

			if (limit != null) {
				query.setRange(0, limit);
			}

			execute = (List<ZeppaFeedback>) query.execute();

			cursor = JDOCursorHelper.getCursor(execute);

			if (cursor == null) {
				cursorString = null;
			} else {
				cursorString = cursor.toWebSafeString();
			}
			// Tight loop for fetching all entities from datastore and
			// accomodate
			// for lazy fetch.
			for (ZeppaFeedback obj : execute)
				;
		} finally {
			mgr.close();
		}

		return CollectionResponse.<ZeppaFeedback> builder().setItems(execute)
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
	@ApiMethod(name = "getZeppaFeedback")
	public ZeppaFeedback getZeppaFeedback(@Named("id") Long id) {

		PersistenceManager mgr = getPersistenceManager();
		ZeppaFeedback zeppafeedback = null;
		try {
			zeppafeedback = mgr.getObjectById(ZeppaFeedback.class, id);
		} catch (javax.jdo.JDOObjectNotFoundException ex) {
			ex.printStackTrace();
			throw ex;
		} finally {
			mgr.close();
		}
		return zeppafeedback;
	}

	/**
	 * This inserts a new entity into App Engine datastore. If the entity
	 * already exists in the datastore, an exception is thrown. It uses HTTP
	 * POST method.
	 * 
	 * @return The inserted entity.
	 * @throws OAuthRequestException
	 */

	@ApiMethod(name = "insertZeppaFeedback")
	public ZeppaFeedback insertZeppaFeedback(ZeppaFeedback zeppafeedback) {

		if (zeppafeedback.getUserId() == null) {
			throw new NullPointerException("Null User Id");
		}

		zeppafeedback.setCreated(System.currentTimeMillis());
		zeppafeedback.setUpdated(System.currentTimeMillis());

		PersistenceManager mgr = getPersistenceManager();
		try {

			// Store feedback
			mgr.makePersistent(zeppafeedback);

		} finally {

			mgr.close();
		}
		return zeppafeedback;
	}

	/**
	 * This method is used for updating an existing entity. If the entity does
	 * not exist in the datastore, an exception is thrown. It uses HTTP PUT
	 * method.
	 * 
	 * @param zeppafeedback
	 *            the entity to be updated.
	 * @return The updated entity.
	 * @throws OAuthRequestException
	 */
	@ApiMethod(name = "updateZeppaFeedback")
	public ZeppaFeedback updateZeppaFeedback(ZeppaFeedback zeppafeedback) {

		zeppafeedback.setUpdated(System.currentTimeMillis());
		PersistenceManager mgr = getPersistenceManager();
		try {

			zeppafeedback.setUpdated(System.currentTimeMillis());
			mgr.makePersistent(zeppafeedback);
		} finally {
			mgr.close();
		}
		return zeppafeedback;
	}

	/**
	 * This method removes the entity with primary key id. It uses HTTP DELETE
	 * method.
	 * 
	 * @param id
	 *            the primary key of the entity to be deleted.
	 * @throws OAuthRequestException
	 */

	@ApiMethod(name = "removeZeppaFeedback")
	public void removeZeppaFeedback(@Named("id") Long id) {

		PersistenceManager mgr = getPersistenceManager();
		try {
			ZeppaFeedback zeppafeedback = mgr.getObjectById(
					ZeppaFeedback.class, id);
			mgr.deletePersistent(zeppafeedback);
		} finally {
			mgr.close();
		}
	}

	@SuppressWarnings("unchecked")
	public static void removeFeedbackForUser(Long userId) {

		PersistenceManager mgr = getPersistenceManager();
		try {
			Query query = mgr.newQuery(ZeppaFeedback.class);
			query.setFilter("userId == " + userId);
			List<ZeppaFeedback> feedback = (List<ZeppaFeedback>) query
					.execute();
			mgr.deletePersistentAll(feedback);

		} finally {
			mgr.close();
		}
	}

	private boolean containsZeppaFeedback(ZeppaFeedback zeppafeedback) {
		PersistenceManager mgr = getPersistenceManager();
		boolean contains = true;
		try {
			mgr.getObjectById(ZeppaFeedback.class, zeppafeedback.getKey());
		} catch (javax.jdo.JDOObjectNotFoundException ex) {
			contains = false;
		} finally {
			mgr.close();
		}
		return contains;
	}

	private static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}

}
