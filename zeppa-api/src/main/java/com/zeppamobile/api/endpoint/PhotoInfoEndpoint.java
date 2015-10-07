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
import com.zeppamobile.api.endpoint.utils.ClientEndpointUtility;
import com.zeppamobile.common.auth.Authorizer;
import com.zeppamobile.common.datamodel.PhotoInfo;
import com.zeppamobile.common.datamodel.ZeppaUser;

@ApiReference(AppInfoEndpoint.class)
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
			@Nullable @Named("limit") Integer limit,
			@Named("auth") Authorizer auth) throws UnauthorizedException {

		ZeppaUser user = ClientEndpointUtility.getAuthorizedZeppaUser(auth);

		PersistenceManager mgr = null;
		Cursor cursor = null;
		List<PhotoInfo> execute = null;

		try {
			mgr = ClientEndpointUtility.getPersistenceManager();
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

			/*
			 * Loop through the list of photoinfo object initialize and remove
			 * the bad eggs
			 */

			List<PhotoInfo> badEggs = new ArrayList<PhotoInfo>();
			for (PhotoInfo photo : execute) {
				if (photo.getOwnerEmail().equals(auth.getEmail())) {
					// Good stuff
				} else {
					badEggs.add(photo);
				}
			}
			execute.removeAll(badEggs);

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
	public PhotoInfo getPhotoInfo(@Named("id") Long id,
			@Named("auth") Authorizer auth) throws UnauthorizedException {

		ZeppaUser user = ClientEndpointUtility.getAuthorizedZeppaUser(auth);

		PersistenceManager mgr = ClientEndpointUtility.getPersistenceManager();
		PhotoInfo photoinfo = null;
		try {
			photoinfo = mgr.getObjectById(PhotoInfo.class, id);
			if (!user.getAuthEmail().equals(photoinfo.getOwnerEmail())) {
				throw new UnauthorizedException(
						"Can't see photos you don't own");
			}
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
	public PhotoInfo insertPhotoInfo(PhotoInfo photoinfo,
			@Named("auth") Authorizer auth) throws UnauthorizedException {

		ZeppaUser user = ClientEndpointUtility.getAuthorizedZeppaUser(auth);

		if(!user.getAuthEmail().equals(photoinfo.getOwnerEmail())){
			throw new UnauthorizedException("Can't insert photos for someone else");
		}
		
		PersistenceManager mgr = ClientEndpointUtility.getPersistenceManager();
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
	public void removePhotoInfo(@Named("id") Long id,
			@Named("auth") Authorizer auth) throws UnauthorizedException {

		ZeppaUser user = ClientEndpointUtility.getAuthorizedZeppaUser(auth);

		PersistenceManager mgr = ClientEndpointUtility.getPersistenceManager();
		try {
			PhotoInfo photoinfo = mgr.getObjectById(PhotoInfo.class, id);
			// Verify this user owns this photo
			if(!user.getAuthEmail().equals(photoinfo.getOwnerEmail())){
				throw new UnauthorizedException("Can't insert photos for someone else");
			}
			
			mgr.deletePersistent(photoinfo);
		} finally {
			mgr.close();
		}
	}

}
