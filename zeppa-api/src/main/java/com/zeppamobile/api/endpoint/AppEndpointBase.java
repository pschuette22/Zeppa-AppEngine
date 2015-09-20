package com.zeppamobile.api.endpoint;

import java.io.Serializable;

import javax.jdo.PersistenceManager;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.zeppamobile.api.Constants;
import com.zeppamobile.api.PMF;
import com.zeppamobile.api.exception.DickheadException;
import com.zeppamobile.common.auth.Authorizer;
import com.zeppamobile.common.datamodel.ZeppaEvent;
import com.zeppamobile.common.datamodel.ZeppaUser;

@Api(name = Constants.API_NAME, version = "v1", scopes = { Constants.EMAIL_SCOPE }, audiences = { Constants.WEB_CLIENT_ID })
public class AppEndpointBase {

	/**
	 * Class with information on the lastest verison of the Android client
	 * 
	 * @author Pete Schuette
	 *
	 */
	class AndroidClientInfo implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		// Values corresponding to the most recently released version of the
		// android client
		final int currentVersion = Constants.androidClientVersion;
		final int currentUpdate = Constants.androidClientUpdate;
		final int currentBugfix = Constants.androidClientBugfix;

		// Values for the minium client value allowed to use zeppa before
		// requiring update
		final int minVersion = 1;
		final int minUpdate = 0;
		final int minBugfix = 0;

		// Message to display to users when asking them to update
		String message = null;

	}

	/**
	 * Class with information on the latest version of the iOS client
	 * 
	 * @author Pete Schuette
	 *
	 */
	class IOSClientInfo implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		// Values of the latest release of the ios app
		final int currentVersion = Constants.iOSClientVersion;
		final int currentUpdate = Constants.iOSClientUpdate;
		final int currentBugfix = Constants.iOSClientBugfix;

		// Minimum values installed app can have without requiring update
		final int minVersion = 1;
		final int minUpdate = 0;
		final int minBugfix = 0;

		// Message to display to users when asking them to update
		String message = null;

	}

	/**
	 * GET Request to fetch latest Android client info
	 * 
	 * @return
	 */
	@ApiMethod(name = "getAndroidClientInfo")
	public AndroidClientInfo getAndroidClientInfo() {
		return new AndroidClientInfo();
	}

	/**
	 * GET Request to fetch latest iOS client info
	 * 
	 * @return IOSClientInfo instance - all instances have the same data
	 */
	@ApiMethod(name = "getIOSClientInfo")
	public IOSClientInfo getIOSClientInfo() {
		return new IOSClientInfo();
	}

	/**
	 * Fetch the user for this authorizer
	 * 
	 * @param auth
	 * @return
	 * @throws DickheadException
	 */
	protected ZeppaUser getAuthorizedZeppaUser(Authorizer auth)
			throws DickheadException {
		// TODO: check tokens

		ZeppaUser result = null;
		PersistenceManager mgr = getPersistenceManager();
		try {

			result = mgr.getObjectById(ZeppaUser.class, auth.getUserId());

		} catch (NullPointerException e) {
			// Auth item wasn't made by us
			throw new DickheadException("Bad Authorizer", "ZeppaUser",
					Long.valueOf(-1), auth);
		} finally {
			mgr.close();
		}

		return result;
	}

	/**
	 * When entity relationships are changed, update the user object
	 * 
	 * @param user
	 * @return true if object was persisted successfully
	 */
	protected boolean updateUserRelationships(ZeppaUser user) {
		PersistenceManager mgr = getPersistenceManager();
		try {
			mgr.makePersistent(user);
		} catch (Exception e) {
			return false;
		} finally {
			mgr.close();
		}

		return true;
	}

	/**
	 * Update an event when relationships are changed
	 * 
	 * @param event
	 */
	protected boolean updateEventRelationships(ZeppaEvent event) {
		PersistenceManager mgr = getPersistenceManager();
		try {
			mgr.makePersistent(event);
		} catch (Exception e) {
			return false;
		} finally {
			mgr.close();
		}
		return true;
	}

	/**
	 * Get the persistence manager
	 * 
	 * @return
	 */
	protected static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}
}
