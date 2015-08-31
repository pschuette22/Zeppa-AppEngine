package com.zeppamobile.api.endpoint;

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
import com.zeppamobile.common.datamodel.DeviceInfo;
import com.zeppamobile.common.utils.Utils;

@Api(name = "deviceinfoendpoint", version = "v1", scopes = { Constants.EMAIL_SCOPE }, clientIds = {
		Constants.TYPE_OTHER_CLIENT_ID,
		Constants.WEB_CLIENT_ID,
		Constants.ANDROID_DEBUG_CLIENT_ID, Constants.ANDROID_RELEASE_CLIENT_ID,
		Constants.IOS_DEBUG_CLIENT_ID, Constants.IOS_CLIENT_ID_OLD }, audiences = { Constants.WEB_CLIENT_ID })
public class DeviceInfoEndpoint {

	/**
	 * This method lists all the entities inserted in datastore. It uses HTTP
	 * GET method and paging support.
	 * 
	 * @return A CollectionResponse class containing the list of all entities
	 *         persisted and a cursor to the next page.
	 * @throws OAuthRequestException
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listDeviceInfo")
	public CollectionResponse<DeviceInfo> listDeviceInfo(
			@Nullable @Named("filter") String filterString,
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("ordering") String orderingString,
			@Nullable @Named("limit") Integer limit) {

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

			// Tight loop for fetching all entities from datastore and
			// accomodate
			// for lazy fetch.
			for (DeviceInfo obj : execute)
				;
		} finally {
			mgr.close();
		}

		return CollectionResponse.<DeviceInfo> builder().setItems(execute)
				.setNextPageToken(cursorString).build();
	}

	// /**
	// * This method lists devices based on a list of items.
	// * Assumes an ArrayList<String> was passed as a JSON Object
	// *
	// * @param jsonList - json list of items
	// * @param itemType - email or phone
	// * @param user auth object
	// * @return collection of DeviceInfo with matching
	// */
	// @ApiMethod(name = "listDeviceInfo")
	// public CollectionResponse<DeviceInfo> listDeviceInfo(
	// @Nullable @Named("jsonList") String jsonList,
	// @Nullable @Named("itemType") String itemType, User user) throws
	// JSONException, OAuthRequestException {
	//
	// JSONObject json = new JSONObject(jsonList);
	// List<String> items = (List<String>) json.get("list");
	//
	// if (Constants.PRODUCTION && user == null) {
	// throw new OAuthRequestException("Unauthorized call");
	// }
	//
	// PersistenceManager mgr = null;
	// Cursor cursor = null;
	// List<DeviceInfo> execute = null;
	//
	//
	// return null;
	// }

	/**
	 * This method gets the entity having primary key id. It uses HTTP GET
	 * method.
	 * 
	 * @param id
	 *            the primary key of the java bean.
	 * @return The entity with primary key id.
	 * @throws OAuthRequestException
	 */
	@ApiMethod(name = "getDeviceInfo")
	public DeviceInfo getDeviceInfo(@Named("id") Long id) {

		PersistenceManager mgr = getPersistenceManager();
		DeviceInfo deviceinfo = null;
		try {
			deviceinfo = mgr.getObjectById(DeviceInfo.class, id);
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
	 * @throws OAuthRequestException
	 */
	@ApiMethod(name = "insertDeviceInfo")
	public DeviceInfo insertOrUpdateDeviceInfo(DeviceInfo deviceinfo) {

		if (deviceinfo.getOwnerId() == null
				|| deviceinfo.getRegistrationId() == null) {
			throw new NullPointerException();
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

			// Make it persistent
			result = mgr.makePersistent(deviceinfo);

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
	public DeviceInfo updateDeviceInfo(DeviceInfo deviceinfo) {

		deviceinfo.setUpdated(System.currentTimeMillis());

		PersistenceManager mgr = getPersistenceManager();
		try {
			DeviceInfo current = mgr.getObjectById(DeviceInfo.class,
					deviceinfo.getId());
			current.setBugfix(deviceinfo.getBugfix());
			current.setUpdate(deviceinfo.getUpdate());
			current.setVersion(deviceinfo.getVersion());

			current.setLastLogin(deviceinfo.getLastLogin());
			current.setLoggedIn(deviceinfo.getLoggedIn());
			current.setUpdated(System.currentTimeMillis());
			mgr.makePersistent(current);

			deviceinfo = current;
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
	public void removeDeviceInfo(DeviceInfo deviceinfo) {

		PersistenceManager mgr = getPersistenceManager();
		try {
			mgr.deletePersistent(deviceinfo);
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
//	private boolean containsDeviceInfo(DeviceInfo deviceinfo) {
//		PersistenceManager mgr = getPersistenceManager();
//		boolean contains = true;
//		try {
//			mgr.getObjectById(DeviceInfo.class, deviceinfo.getKey());
//		} catch (javax.jdo.JDOObjectNotFoundException ex) {
//			contains = false;
//		} finally {
//			mgr.close();
//		}
//		return contains;
//	}

	private static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}

}
