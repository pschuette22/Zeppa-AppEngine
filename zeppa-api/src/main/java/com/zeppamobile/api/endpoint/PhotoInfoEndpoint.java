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
import com.zeppamobile.api.datamodel.PhotoInfo;

@Api(name = "photoinfoendpoint", version = "v1", scopes = { Constants.EMAIL_SCOPE }, clientIds = {
		Constants.ANDROID_DEBUG_CLIENT_ID, Constants.ANDROID_RELEASE_CLIENT_ID,
		Constants.IOS_DEBUG_CLIENT_ID, Constants.IOS_CLIENT_ID_OLD }, audiences = { Constants.WEB_CLIENT_ID })
public class PhotoInfoEndpoint {

	/**
	 * This method lists all the entities inserted in datastore. It uses HTTP
	 * GET method and paging support.
	 * 
	 * @return A CollectionResponse class containing the list of all entities
	 *         persisted and a cursor to the next page.
	 * @throws OAuthRequestException
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listPhotoInfo")
	public CollectionResponse<PhotoInfo> listPhotoInfo(
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("limit") Integer limit) {

		PersistenceManager mgr = null;
		Cursor cursor = null;
		List<PhotoInfo> execute = null;

		try {
			mgr = getPersistenceManager();
			Query query = mgr.newQuery(PhotoInfo.class);
			if (cursorString != null && cursorString != "") {
				cursor = Cursor.fromWebSafeString(cursorString);
				HashMap<String, Object> extensionMap = new HashMap<String, Object>();
				extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
				query.setExtensions(extensionMap);
			}

			if (limit != null) {
				query.setRange(0, limit);
			}

			execute = (List<PhotoInfo>) query.execute();
			cursor = JDOCursorHelper.getCursor(execute);
			if (cursor != null)
				cursorString = cursor.toWebSafeString();

			// Tight loop for fetching all entities from datastore and
			// accomodate
			// for lazy fetch.
			for (PhotoInfo obj : execute)
				;
		} finally {
			mgr.close();
		}

		return CollectionResponse.<PhotoInfo> builder().setItems(execute)
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
	@ApiMethod(name = "getPhotoInfo")
	public PhotoInfo getPhotoInfo(@Named("id") Long id) {

		PersistenceManager mgr = getPersistenceManager();
		PhotoInfo photoinfo = null;
		try {
			photoinfo = mgr.getObjectById(PhotoInfo.class, id);
		} finally {
			mgr.close();
		}
		return photoinfo;
	}

	/**
	 * This inserts a new entity into App Engine datastore. If the entity
	 * already exists in the datastore, an exception is thrown. It uses HTTP
	 * POST method.
	 * 
	 * @param photoinfo
	 *            the entity to be inserted.
	 * @return The inserted entity.
	 * @throws OAuthRequestException
	 */
	@ApiMethod(name = "insertPhotoInfo")
	public PhotoInfo insertPhotoInfo(PhotoInfo photoinfo) {

		PersistenceManager mgr = getPersistenceManager();
		try {

			mgr.makePersistent(photoinfo);
		} finally {
			mgr.close();
		}
		return photoinfo;
	}

	/**
	 * This method removes the entity with primary key id. It uses HTTP DELETE
	 * method.
	 * 
	 * @param id
	 *            the primary key of the entity to be deleted.
	 * @throws OAuthRequestException
	 */
	@ApiMethod(name = "removePhotoInfo")
	public void removePhotoInfo(@Named("id") Long id) {

		PersistenceManager mgr = getPersistenceManager();
		try {
			PhotoInfo photoinfo = mgr.getObjectById(PhotoInfo.class, id);
			mgr.deletePersistent(photoinfo);
		} finally {
			mgr.close();
		}
	}

	private boolean containsPhotoInfo(PhotoInfo photoinfo) {
		PersistenceManager mgr = getPersistenceManager();
		boolean contains = true;
		try {
			mgr.getObjectById(PhotoInfo.class, photoinfo.getKey());
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
