package com.zeppamobile.api.endpoint;

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
import com.zeppamobile.common.datamodel.ZeppaUser;
import com.zeppamobile.common.datamodel.ZeppaUserInfo;
import com.zeppamobile.common.utils.Utils;

@ApiReference(BaseEndpoint.class)
public class ZeppaUserInfoEndpoint extends BaseEndpoint {

	/**
	 * This method lists all the entities inserted in datastore. It uses HTTP
	 * GET method and paging support.
	 * 
	 * @return A CollectionResponse class containing the list of all entities
	 *         persisted and a cursor to the next page.
	 * @throws OAuthRequestException
	 */
	@SuppressWarnings({ "unchecked" })
	@ApiMethod(name = "listZeppaUserInfo")
	public CollectionResponse<ZeppaUserInfo> listZeppaUserInfo(
			@Nullable @Named("filter") String filterString,
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("ordering") String orderingString,
			@Nullable @Named("limit") Integer limit,
			@Named("auth") Authorizer auth) throws UnauthorizedException {
		
		ZeppaUser user = getAuthorizedZeppaUser(auth);

		PersistenceManager mgr = null;
		Cursor cursor = null;
		List<ZeppaUserInfo> execute = null;

		try {
			mgr = getPersistenceManager();
			Query query = mgr.newQuery(ZeppaUserInfo.class);
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

			execute = (List<ZeppaUserInfo>) query.execute();

			cursor = JDOCursorHelper.getCursor(execute);
			if (cursor == null) {
				cursorString = null;
			} else {
				cursorString = cursor.toWebSafeString();
			}
			// Tight loop for fetching all entities from datastore and
			// accomodate
			// for lazy fetch.
			for (ZeppaUserInfo obj : execute) {
				obj.getKey();
				obj.getKey().getParent();
				obj.getKey().getParent().getId();
				obj.getId();
				obj.getGivenName();
				obj.getGivenName();
				obj.getGoogleAccountEmail();
				obj.getImageUrl();
				obj.getPrimaryUnformattedNumber();

			}

		} finally {
			mgr.close();
		}

		return CollectionResponse.<ZeppaUserInfo> builder().setItems(execute)
				.setNextPageToken(cursorString).build();
	}

	/**
	 * This methods returns a ZeppaUserInfo instance for the requestedUserId.
	 * ZeppaUser objects will only be held by their user. Info of connections
	 * will be held
	 * 
	 * @param requestedUserId
	 * @param user
	 * @return
	 * @throws OAuthRequestException
	 */

	@ApiMethod(name = "fetchZeppaUserInfoByParentId")
	public ZeppaUserInfo fetchZeppaUserInfoByParentId(
			@Named("requestedParentId") Long requestedUserId,
			@Named("auth") Authorizer auth) throws UnauthorizedException {
		
		ZeppaUser user = getAuthorizedZeppaUser(auth);

		PersistenceManager mgr = getPersistenceManager();
		ZeppaUserInfo result = null;

		try {
			ZeppaUser userResult = mgr.getObjectById(ZeppaUser.class,
					requestedUserId);
			result = userResult.getUserInfo();
			// Touch all the fields so they are properly returned
			result.getCreated();
			result.getFamilyName();
			result.getGivenName();
			result.getGoogleAccountEmail();
			result.getPrimaryUnformattedNumber();
			result.getUpdated();
			result.getImageUrl();

		} catch (javax.jdo.JDOObjectNotFoundException ex) {
			result = null;
		} finally {
			mgr.close();
		}

		return result;
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
	@ApiMethod(name = "getZeppaUserInfo")
	public ZeppaUserInfo getZeppaUserInfo(@Named("id") Long id,
			@Named("auth") Authorizer auth) throws UnauthorizedException {
		
		ZeppaUser user = getAuthorizedZeppaUser(auth);

		PersistenceManager mgr = getPersistenceManager();
		ZeppaUserInfo zeppauserinfo = null;
		try {
			zeppauserinfo = mgr.getObjectById(ZeppaUserInfo.class, id);
		} finally {
			mgr.close();
		}
		return zeppauserinfo;
	}

	// /**
	// * This inserts a new entity into App Engine datastore. If the entity
	// * already exists in the datastore, an exception is thrown. It uses HTTP
	// * POST method.
	// *
	// * @param zeppauserinfo
	// * the entity to be inserted.
	// * @return The inserted entity.
	// * @throws OAuthRequestException
	// */
	// @ApiMethod(name = "insertZeppaUserInfo")
	// public ZeppaUserInfo insertZeppaUserInfo(ZeppaUserInfo zeppauserinfo,
	// @Named("auth") Authorizer auth) {
	//
	// PersistenceManager mgr = getPersistenceManager();
	// try {
	//
	// mgr.makePersistent(zeppauserinfo);
	// } finally {
	// mgr.close();
	// }
	// return zeppauserinfo;
	// }

	// /**
	// * This method is used for updating an existing entity. If the entity does
	// * not exist in the datastore, an exception is thrown. It uses HTTP PUT
	// * method.
	// *
	// * @param zeppauserinfo
	// * the entity to be updated.
	// * @return The updated entity.
	// * @throws OAuthRequestException
	// */
	// @ApiMethod(name = "updateZeppaUserInfo")
	// public ZeppaUserInfo updateZeppaUserInfo(ZeppaUserInfo zeppauserinfo,
	// @Named("auth") Authorizer auth) throws UnauthorizedException {
	// ZeppaUser user = getAuthorizedZeppaUser(auth);
	//
	// PersistenceManager mgr = getPersistenceManager();
	// try {
	// ZeppaUserInfo current = mgr.getObjectById(ZeppaUserInfo.class,
	// zeppauserinfo.getId());
	// current.setGivenName(zeppauserinfo.getGivenName());
	// current.setFamilyName(zeppauserinfo.getFamilyName());
	// current.setImageUrl(zeppauserinfo.getImageUrl());
	// current.setPrimaryUnformattedNumber(zeppauserinfo
	// .getPrimaryUnformattedNumber());
	// current.setUpdated(System.currentTimeMillis());
	//
	// mgr.makePersistent(zeppauserinfo);
	// zeppauserinfo = current;
	//
	// } finally {
	// mgr.close();
	// }
	// return zeppauserinfo;
	// }

	// /**
	// * This method removes the entity with primary key id. It uses HTTP DELETE
	// * method.
	// *
	// * @param id
	// * the primary key of the entity to be deleted.
	// */
	// @ApiMethod(name = "removeZeppaUserInfo")
	// public void removeZeppaUserInfo(@Named("id") Long id) {
	// PersistenceManager mgr = getPersistenceManager();
	// try {
	// ZeppaUserInfo zeppauserinfo = mgr.getObjectById(
	// ZeppaUserInfo.class, id);
	// mgr.deletePersistent(zeppauserinfo);
	// } finally {
	// mgr.close();
	// }
	// }

	// private boolean containsZeppaUserInfo(ZeppaUserInfo zeppauserinfo) {
	// PersistenceManager mgr = getPersistenceManager();
	// boolean contains = true;
	// try {
	// mgr.getObjectById(ZeppaUserInfo.class, zeppauserinfo.getKey());
	// } catch (javax.jdo.JDOObjectNotFoundException ex) {
	// contains = false;
	// } finally {
	// mgr.close();
	// }
	// return contains;
	// }

}
