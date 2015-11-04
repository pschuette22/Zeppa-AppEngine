package com.zeppamobile.api.endpoint;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

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
import com.zeppamobile.api.endpoint.utils.ClientEndpointUtility;
import com.zeppamobile.common.datamodel.DeviceInfo;
import com.zeppamobile.common.datamodel.ZeppaUser;
import com.zeppamobile.common.utils.Utils;

@Api(name = Constants.API_NAME, version = "v1", scopes = { Constants.EMAIL_SCOPE }, audiences = { Constants.WEB_CLIENT_ID })
public class DeviceInfoEndpoint {

	/**
	 * This method lists all the entities inserted in datastore. It uses HTTP
	 * GET method and paging support.
	 * 
	 * @return A CollectionResponse class containing the list of all entities
	 *         persisted and a cursor to the next page.
	 * @throws UnauthorizedException
	 * @throws OAuthRequestException
	 */
	@SuppressWarnings({ "unchecked" })
	@ApiMethod(name = "listDeviceInfo")
	public CollectionResponse<DeviceInfo> listDeviceInfo(
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
		List<DeviceInfo> execute = null;

		try {
			mgr = getPersistenceManager();
			Query query = mgr.newQuery(DeviceInfo.class);
			if (Utils.isWebSafe(cursorString)) {
				cursor = Cursor.fromWebSafeString(cursorString);
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

			execute = (List<DeviceInfo>) query.execute();

			cursor = JDOCursorHelper.getCursor(execute);
			if (cursor == null) {
				cursorString = null;
			} else {
				cursorString = cursor.toWebSafeString();
			}

			/*
			 * Devices may only be fetched by the owner Remove the bad eggs.
			 * TODO: Flag it?
			 */
			List<DeviceInfo> badEggs = new ArrayList<DeviceInfo>();
			for (DeviceInfo obj : execute) {
				// Make sure authed user owns this device
				if (obj.getOwnerId().longValue() != user.getId().longValue()) {
					badEggs.add(obj);
				}
			}
			execute.remove(badEggs);

		} finally {
			mgr.close();
		}

		return CollectionResponse.<DeviceInfo> builder().setItems(execute)
				.setNextPageToken(cursorString).build();
	}

	/**
	 * This method gets the entity having primary key id. It uses HTTP GET
	 * method.
	 * 
	 * @param id
	 *            the primary key of the java bean.
	 * @return The entity with primary key id.
	 * @throws UnauthorizedException
	 */
	@ApiMethod(name = "getDeviceInfo")
	public DeviceInfo getDeviceInfo(@Named("id") Long id,
			@Named("idToken") String tokenString) throws UnauthorizedException {

		// Fetch Authorized Zeppa User
		ZeppaUser user = ClientEndpointUtility
				.getAuthorizedZeppaUser(tokenString);
		if (user == null) {
			throw new UnauthorizedException(
					"No matching user found for this token");
		}

		PersistenceManager mgr = getPersistenceManager();
		DeviceInfo deviceinfo = null;
		try {
			deviceinfo = mgr.getObjectById(DeviceInfo.class, id);
			if (deviceinfo.getOwnerId().longValue() != user.getId().longValue()) {
				throw new UnauthorizedException("You don't own this device");
			}
		} finally {
			mgr.close();
		}
		return deviceinfo;
	}

	/**
	 * This inserts a new entity into App Engine datastore. If the entity
	 * already exists in the datastore, an exception is thrown. It uses HTTP
	 * POST method.
	 * 
	 * @param deviceinfo
	 *            the entity to be inserted.
	 * @return The inserted entity or null.
	 * @throws UnauthorizedException
	 */
	@ApiMethod(name = "insertDeviceInfo")
	public DeviceInfo insertOrUpdateDeviceInfo(DeviceInfo deviceinfo,
			@Named("idToken") String tokenString) throws UnauthorizedException {

		// Make sure necessary items are there before inserting
		if (deviceinfo.getOwnerId() == null
				|| deviceinfo.getRegistrationId() == null) {
			throw new NullPointerException();
		}

		// Fetch Authorized Zeppa User
		ZeppaUser user = ClientEndpointUtility
				.getAuthorizedZeppaUser(tokenString);
		if (user == null) {
			throw new UnauthorizedException(
					"No matching user found for this token");
		}

		DeviceInfo result = null;
		PersistenceManager mgr = getPersistenceManager();

		// Try to fetch an instance of device info with a matching token and
		// user id
		try {
			String filter = "ownerId == " + deviceinfo.getOwnerId()
					+ " && registrationId == '"
					+ deviceinfo.getRegistrationId() + "'";

			Query q = mgr.newQuery(DeviceInfo.class, filter);

			// Does not check that this is a safe cast... it is.
			@SuppressWarnings("unchecked")
			List<DeviceInfo> response = (List<DeviceInfo>) q.execute();

			// If there are already matching devices, update them
			if (response == null || response.isEmpty()) {
				deviceinfo.setCreated(System.currentTimeMillis());
				deviceinfo.setUpdated(System.currentTimeMillis());
			} else {

				// get and remove the first object
				DeviceInfo current = response.get(0);
				response.remove(0);

				// Set version information
				current.setBugfix(deviceinfo.getBugfix());
				current.setUpdate(deviceinfo.getUpdate());
				current.setVersion(deviceinfo.getVersion());

				// set login info/ set updated
				current.setLastLogin(deviceinfo.getLastLogin());
				current.setLoggedIn(deviceinfo.getLoggedIn());
				current.setUpdated(System.currentTimeMillis());

				// passed device info is set to held info with updated values
				deviceinfo = current;

				// If there are more than one instances of this object, delete
				// the extras
				if (!response.isEmpty()) {
					mgr.deletePersistentAll(response);
				}
			}

			deviceinfo.setOwner(user);
			user.addDevice(deviceinfo);

			// Persist changes into the datastore
			result = mgr.makePersistent(deviceinfo);
			ClientEndpointUtility.updateUserEntityRelationships(user);

		} catch (javax.jdo.JDOObjectNotFoundException | NullPointerException e) {
			e.printStackTrace();
		} finally {
			mgr.close();
		}

		// Return the inserted item or null if an error occured.
		return result;
	}

	/**
	 * This method is used for updating an existing entity. If the entity does
	 * not exist in the datastore, an exception is thrown. It uses HTTP PUT
	 * method.
	 * 
	 * @param deviceinfo
	 *            the entity to be updated.
	 * @return The updated entity.
	 * @throws OAuthRequestException
	 */
	@ApiMethod(name = "updateDeviceInfo")
	public DeviceInfo updateDeviceInfo(DeviceInfo deviceinfo,
			@Named("idToken") String tokenString) throws UnauthorizedException {

		// Fetch Authorized Zeppa User
		ZeppaUser user = ClientEndpointUtility
				.getAuthorizedZeppaUser(tokenString);
		if (user == null) {
			throw new UnauthorizedException(
					"No matching user found for this token");
		}

		deviceinfo.setUpdated(System.currentTimeMillis());

		PersistenceManager mgr = getPersistenceManager();
		try {
			DeviceInfo current = mgr.getObjectById(DeviceInfo.class,
					deviceinfo.getId());

			if (user.getId().longValue() == current.getOwnerId().longValue()) {

				current.setBugfix(deviceinfo.getBugfix());
				current.setUpdate(deviceinfo.getUpdate());
				current.setVersion(deviceinfo.getVersion());

				current.setLastLogin(deviceinfo.getLastLogin());
				current.setLoggedIn(deviceinfo.getLoggedIn());
				current.setUpdated(System.currentTimeMillis());
				mgr.makePersistent(current);

				deviceinfo = current;
			} else {
				throw new UnauthorizedException("You can't update this device");
			}
		} finally {
			mgr.close();
		}
		return deviceinfo;
	}

	/**
	 * This method removes the entity with primary key id. It uses HTTP DELETE
	 * method.
	 * 
	 * @param id
	 *            the primary key of the entity to be deleted.
	 * @throws OAuthRequestException
	 */
	@ApiMethod(name = "removeDeviceInfo")
	public void removeDeviceInfo(@Named("id") Long id,
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
			DeviceInfo info = mgr.getObjectById(DeviceInfo.class, id);

			if (info.getOwnerId().longValue() == user.getId().longValue()) {
				if (user.removeDevice(info)) {
					ClientEndpointUtility.updateUserEntityRelationships(user);
				}
				mgr.deletePersistent(info);
			} else {
				throw new UnauthorizedException("You can't delete this device");
			}

		} finally {
			mgr.close();
		}
	}

	/**
	 * This method checks to see if device info object already exists
	 * 
	 * @param deviceinfo
	 * @return true if deviceinfo exists
	 */
	// private boolean containsDeviceInfo(DeviceInfo deviceinfo) {
	// PersistenceManager mgr = getPersistenceManager();
	// boolean contains = true;
	// try {
	// mgr.getObjectById(DeviceInfo.class, deviceinfo.getKey());
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

}
