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
import com.zeppamobile.common.auth.Authorizer;
import com.zeppamobile.common.datamodel.ZeppaNotification;
import com.zeppamobile.common.datamodel.ZeppaUser;
import com.zeppamobile.common.utils.Utils;

@ApiReference(BaseEndpoint.class)
public class ZeppaNotificationEndpoint extends BaseEndpoint {

	/**
	 * This method lists all the entities inserted in datastore. It uses HTTP
	 * GET method and paging support.
	 * 
	 * @return A CollectionResponse class containing the list of all entities
	 *         persisted and a cursor to the next page.
	 * @throws OAuthRequestException
	 */
	@SuppressWarnings("unchecked")
	@ApiMethod(name = "listZeppaNotification")
	public CollectionResponse<ZeppaNotification> listZeppaNotification(
			@Nullable @Named("filter") String filterString,
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("ordering") String orderingString,
			@Nullable @Named("limit") Integer limit,
			@Named("auth") Authorizer auth) throws UnauthorizedException {

		ZeppaUser user = getAuthorizedZeppaUser(auth);

		PersistenceManager mgr = null;
		Cursor cursor = null;
		List<ZeppaNotification> execute = null;

		try {
			mgr = getPersistenceManager();
			Query query = mgr.newQuery(ZeppaNotification.class);
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

			execute = (List<ZeppaNotification>) query.execute();

			cursor = JDOCursorHelper.getCursor(execute);

			if (cursor == null) {
				cursorString = null;
			} else {
				cursorString = cursor.toWebSafeString();
			}
			// Tight loop for fetching all entities from datastore and
			// accomodate
			// for lazy fetch.
			List<ZeppaNotification> badEggs = new ArrayList<ZeppaNotification>();
			for (ZeppaNotification notif : execute) {
				if (notif.getRecipientId().longValue() != user.getId()
						.longValue()) {
					badEggs.add(notif);
				}
			}
			execute.removeAll(badEggs);

		} finally {
			mgr.close();
		}

		return CollectionResponse.<ZeppaNotification> builder()
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
	@ApiMethod(name = "getZeppaNotification")
	public ZeppaNotification getZeppaNotification(@Named("id") Long id,
			@Named("auth") Authorizer auth) throws UnauthorizedException {

		ZeppaUser user = getAuthorizedZeppaUser(auth);

		PersistenceManager mgr = getPersistenceManager();
		ZeppaNotification zeppanotification = null;
		try {
			zeppanotification = mgr.getObjectById(ZeppaNotification.class, id);
			if (zeppanotification.getRecipientId().longValue() != user.getId()
					.longValue()) {
				throw new UnauthorizedException(
						"Can't get notifications that were not sent to you");
			}
		} catch (javax.jdo.JDOObjectNotFoundException ex) {
			zeppanotification = null;

		} finally {
			mgr.close();
		}
		return zeppanotification;
	}

	// /**
	// * This inserts a new entity into App Engine datastore. If the entity
	// * already exists in the datastore, an exception is thrown. It uses HTTP
	// * POST method.
	// *
	// * @param zeppanotification
	// * the entity to be inserted.
	// * @return The inserted entity.
	// * @throws OAuthRequestException
	// */
	// @ApiMethod(name = "insertZeppaNotification")
	// public ZeppaNotification insertZeppaNotification(
	// ZeppaNotification zeppanotification) {
	//
	// if (zeppanotification.getSenderId() == null) {
	// throw new NullPointerException("Must Set User Id");
	// }
	//
	// zeppanotification.setCreated(System.currentTimeMillis());
	// zeppanotification.setUpdated(System.currentTimeMillis());
	//
	// PersistenceManager mgr = getPersistenceManager();
	// try {
	//
	// mgr.makePersistent(zeppanotification);
	//
	// } finally {
	//
	// mgr.close();
	// }
	//
	// return zeppanotification;
	// }

	/**
	 * This method is used for updating an existing entity. If the entity does
	 * not exist in the datastore, an exception is thrown. It uses HTTP PUT
	 * method.
	 * 
	 * @param zeppanotification
	 *            the entity to be updated.
	 * @return The updated entity.
	 * @throws OAuthRequestException
	 */
	@ApiMethod(name = "updateZeppaNotification")
	public ZeppaNotification updateZeppaNotification(
			ZeppaNotification zeppanotification, @Named("auth") Authorizer auth)
			throws UnauthorizedException {

		ZeppaUser user = getAuthorizedZeppaUser(auth);

		zeppanotification.setUpdated(System.currentTimeMillis());
		PersistenceManager mgr = getPersistenceManager();
		try {
			ZeppaNotification current = mgr.getObjectById(
					ZeppaNotification.class, zeppanotification.getId());

			if (current.getRecipientId().longValue() != user.getId()
					.longValue()) {
				throw new UnauthorizedException("");
			}

			current.setEventId(zeppanotification.getEventId());
			current.setExpires(zeppanotification.getExpires());
			current.setExtraMessage(zeppanotification.getExtraMessage());
			current.setHasSeen(zeppanotification.getHasSeen());
			current.setUpdated(System.currentTimeMillis());

			zeppanotification = mgr.makePersistent(current);

		} finally {
			mgr.close();
		}
		return zeppanotification;
	}

	/**
	 * This method removes the entity with primary key id. It uses HTTP DELETE
	 * method.
	 * 
	 * @param id
	 *            the primary key of the entity to be deleted.
	 * @throws OAuthRequestException
	 */
	@ApiMethod(name = "removeZeppaNotification")
	public void removeZeppaNotification(@Named("id") Long id,
			@Named("auth") Authorizer auth) throws UnauthorizedException {

		ZeppaUser user = getAuthorizedZeppaUser(auth);

		PersistenceManager mgr = getPersistenceManager();
		try {
			ZeppaNotification zeppanotification = mgr.getObjectById(
					ZeppaNotification.class, id);
			if (zeppanotification.getRecipientId().longValue() == user.getId()
					.longValue()
					|| zeppanotification.getSenderId().longValue() == user
							.getId().longValue()) {
				mgr.deletePersistent(zeppanotification);
			} else {
				throw new UnauthorizedException(
						"Not able to remove notifications you didnt send or receive");
			}

		} catch (javax.jdo.JDOObjectNotFoundException ex) {

		} finally {
			mgr.close();
		}
	}

}
