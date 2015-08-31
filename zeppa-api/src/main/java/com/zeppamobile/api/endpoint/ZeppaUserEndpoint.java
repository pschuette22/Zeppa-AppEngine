package com.zeppamobile.api.endpoint;

import java.io.IOException;
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
import com.google.appengine.api.users.User;
import com.google.appengine.datanucleus.query.JDOCursorHelper;
import com.zeppamobile.api.Constants;
import com.zeppamobile.api.PMF;
import com.zeppamobile.api.endpoint.utils.RelationshipUtility;
import com.zeppamobile.common.datamodel.ZeppaUser;
import com.zeppamobile.common.datamodel.ZeppaUserInfo;
import com.zeppamobile.common.googlecalendar.GoogleCalendarService;
import com.zeppamobile.common.utils.Utils;

@ApiReference(AppEndpointBase.class)
public class ZeppaUserEndpoint {


	/**
	 * This method lists all the entities inserted in datastore. It uses HTTP
	 * GET method and paging support.
	 * 
	 * @return A CollectionResponse class containing the list of all entities
	 *         persisted and a cursor to the next page.
	 * @throws OAuthRequestException
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listZeppaUser")
	public CollectionResponse<ZeppaUser> listZeppaUser(
			@Nullable @Named("filter") String filterString,
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("ordering") String orderingString,
			@Nullable @Named("limit") Integer limit) {
		
		PersistenceManager mgr = null;
		Cursor cursor = null;
		List<ZeppaUser> execute = null;

		try {
			mgr = getPersistenceManager();
			Query query = mgr.newQuery(ZeppaUser.class);
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

			execute = (List<ZeppaUser>) query.execute();

			cursor = JDOCursorHelper.getCursor(execute);
			if (cursor == null) {
				cursorString = null;
			} else {
				cursorString = cursor.toWebSafeString();
			}
			// Tight loop for fetching all entities from datastore and
			// accomodate
			// for lazy fetch.
			for (ZeppaUser obj : execute)
				;
		} finally {
			mgr.close();
		}

		return CollectionResponse.<ZeppaUser> builder().setItems(execute)
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
	@ApiMethod(name = "getZeppaUser")
	public ZeppaUser getZeppaUser(@Named("userId") Long userId) {


		PersistenceManager mgr = getPersistenceManager();
		ZeppaUser zeppauser = null;
		try {
			zeppauser = mgr.getObjectById(ZeppaUser.class, userId);

			// if(!zeppauser.getAuthEmail().equalsIgnoreCase(user.getEmail())){
			// zeppauser = null;
			// throw new
			// OAuthRequestException("Not Authorized to Access this object");
			// }
			ZeppaUserInfo info = zeppauser.getUserInfo();

		} catch (javax.jdo.JDOObjectNotFoundException e) {
			e.printStackTrace();

		} finally {
			mgr.close();
		}
		return zeppauser;
	}

	/**
	 * This inserts a new entity into App Engine datastore. It uses HTTP POST
	 * method.
	 * 
	 * @param zeppauser
	 *            the entity to be inserted.
	 * @return The inserted entity.
	 * @throws IOException
	 *             , GeneralSecurityException
	 * @throws UnauthorizedException
	 * @throws OAuthRequestException
	 */

	@ApiMethod(name = "insertZeppaUser")
	public ZeppaUser insertZeppaUser(ZeppaUser zeppaUser) throws IOException {


		try {
			ZeppaUser fetched = fetchUserByEmail(zeppaUser.getAuthEmail());
			if (fetched != null) {
				return fetched;
			}

		} catch (javax.jdo.JDOObjectNotFoundException ex) {
			// Doesnt exist, safe to insert
		}

		zeppaUser.setCreated(System.currentTimeMillis());
		zeppaUser.setUpdated(System.currentTimeMillis());

		zeppaUser.getUserInfo().setCreated(System.currentTimeMillis());
		zeppaUser.getUserInfo().setUpdated(System.currentTimeMillis());

		zeppaUser = GoogleCalendarService.insertZeppaCalendar(zeppaUser);

		PersistenceManager mgr = getPersistenceManager();

		try {
			zeppaUser = mgr.makePersistent(zeppaUser);

		} finally {

			mgr.close();
		}

		return zeppaUser;
	}

	/**
	 * This method is used for updating an existing entity. If the entity does
	 * not exist in the datastore, an exception is thrown. It uses HTTP PUT
	 * method.
	 * 
	 * @param zeppauser
	 *            the entity to be updated.
	 * @return The updated entity.
	 * @throws OAuthRequestException
	 */
	@ApiMethod(name = "updateZeppaUser")
	public ZeppaUser updateZeppaUser(ZeppaUser zeppauser, User user)
			throws OAuthRequestException {

		if (Constants.PRODUCTION && user == null) {
			throw new OAuthRequestException("Unauthorized call");
		}

		PersistenceManager mgr = getPersistenceManager();
		try {
			ZeppaUser current = mgr.getObjectById(ZeppaUser.class,
					zeppauser.getId());
			ZeppaUserInfo currentInfo = current.getUserInfo();
			ZeppaUserInfo updatedInfo = zeppauser.getUserInfo();

			currentInfo.setGivenName(updatedInfo.getGivenName());
			currentInfo.setFamilyName(updatedInfo.getFamilyName());
			currentInfo.setImageUrl(updatedInfo.getImageUrl());
			currentInfo.setPrimaryUnformattedNumber(updatedInfo
					.getPrimaryUnformattedNumber());
			currentInfo.setUpdated(System.currentTimeMillis());

			current.setUserInfo(currentInfo);
			current.setZeppaCalendarId(zeppauser.getZeppaCalendarId());
			current.setUpdated(System.currentTimeMillis());

			zeppauser = mgr.makePersistent(current);
		} finally {
			mgr.close();
		}
		return zeppauser;
	}

	/**
	 * This method removes the entity with primary key id. It uses HTTP DELETE
	 * method.
	 * 
	 * @param id
	 *            the primary key of the entity to be deleted.
	 * @throws OAuthRequestException
	 */
	@ApiMethod(name = "removeZeppaUser")
	public void removeZeppaUser(@Named("id") Long userId)
			throws OAuthRequestException {


		PersistenceManager mgr = getPersistenceManager();

		try {
			ZeppaUser zeppauser = mgr.getObjectById(ZeppaUser.class, userId);

			RelationshipUtility.removeZeppaAccountEntities(userId);
			// Delete Zeppa Calendar
			GoogleCalendarService.deleteCalendar(zeppauser);

			// Delete ZeppaUser
			mgr.deletePersistent(zeppauser);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			mgr.close();
		}
	}

	/**
	 * this method finds a ZeppaUser based on the user passed in If it doesnt
	 * find one, returns null to notify application that it needs to create a
	 * new user
	 * 
	 * 
	 * @param googlePlusProfileId
	 *            , profile id for the individual's google plus account
	 * @param User
	 *            , the asking user
	 * 
	 * @return ZeppaUser which matches user object
	 * @throws UnauthorizedException
	 * @throws OAuthRequestException
	 * 
	 * */

	@ApiMethod(name = "fetchCurrentZeppaUser")
	public ZeppaUser fetchUserByEmail(@Named("email")String email) {

		PersistenceManager mgr = getPersistenceManager();
		ZeppaUser zeppaUser = null;
		try {
			Query query = mgr.newQuery(ZeppaUser.class,
					"authEmail == '" + email + "'");
			query.setUnique(true);

			zeppaUser = (ZeppaUser) query.execute();

			if (zeppaUser != null) {

				// Touch all fields so they are returned properly to the app
				ZeppaUserInfo userInfo = zeppaUser.getUserInfo();
				userInfo.getGivenName();
				userInfo.getFamilyName();
				userInfo.getGoogleAccountEmail();
				userInfo.getImageUrl();
				userInfo.getPrimaryUnformattedNumber();
				userInfo.getUpdated();
				userInfo.getCreated();
			}

		} catch (JDOObjectNotFoundException e) {
			// Not found, this should happen every time a new user is created
		} finally {
			mgr.close();
		}

		return zeppaUser;
	}


	private static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}

}
