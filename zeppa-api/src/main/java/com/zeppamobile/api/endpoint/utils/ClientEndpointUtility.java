package com.zeppamobile.api.endpoint.utils;

import java.security.GeneralSecurityException;

import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.server.spi.response.UnauthorizedException;
import com.zeppamobile.api.AppConfig;
import com.zeppamobile.api.Constants;
import com.zeppamobile.api.PMF;
import com.zeppamobile.api.datamodel.ZeppaEvent;
import com.zeppamobile.api.datamodel.ZeppaUser;
import com.zeppamobile.api.datamodel.ZeppaUserToUserRelationship;
import com.zeppamobile.common.UniversalConstants;
import com.zeppamobile.common.utils.Utils;

/**
 * This is the base class for endpoints called from the Zeppa Client Apps
 * (General User)
 * 
 * @author Pete Schuette
 *
 */
public class ClientEndpointUtility {

	private static final Logger LOG = Logger.getLogger(ClientEndpointUtility.class.getName());

	/**
	 * <p>
	 * Fetch the user for this id token
	 * </p>
	 * This method calls checkToken and getAuthorizedZeppaUser sequentially
	 * 
	 * @param idToken
	 *            passed by client
	 * @return ZeppaUser for this passed token or null
	 * @throws UnauthorizedException
	 *             if token is not valid
	 */
	public static ZeppaUser getAuthorizedZeppaUser(String tokenString) throws UnauthorizedException {
		// Get payload for token
		GoogleIdToken.Payload payload = checkToken(tokenString);
		// Return user based on payload
		return getAuthorizedUserForPayload(payload);
	}

	/**
	 * Get ZeppaUser based on GoogleIdToken payload
	 * 
	 * @param payload
	 * @return
	 */
	public static ZeppaUser getAuthorizedUserForPayload(GoogleIdToken.Payload payload) throws UnauthorizedException {
		// Verify it is valid
		if (payload == null || !Utils.isWebSafe(payload.getEmail())) {
			throw new UnauthorizedException("Invalid id-token");
		}

		// This will be the result of the query or null
		ZeppaUser result = null;

		// Execute the query and clean up where necessary
		PersistenceManager mgr = getPersistenceManager();

		try {

			Query q = mgr.newQuery(ZeppaUser.class, "authEmail == '" + payload.getEmail() + "'");
			q.setUnique(true);

			result = (ZeppaUser) q.execute();

		} finally {
			mgr.close();
		}
		return result;
	}

	/**
	 * Validate a token used to access the backend
	 * 
	 * @param tokenString
	 *            id token sent from client
	 * @return Payload for this token or null if invalid
	 */
	public static GoogleIdToken.Payload checkToken(String tokenString) throws UnauthorizedException {

		// TODO: validate auth token, client id, etc.
		AuthChecker checker = new AuthChecker(UniversalConstants.APP_CLIENT_IDS, Constants.WEB_CLIENT_ID);

		try {
			GoogleIdToken.Payload payload = checker.check(tokenString);

			if (checker.isValid() || AppConfig.isTest()) {

				return payload;
			} else {

				throw new UnauthorizedException("Invalid Auth With Problem: " + checker.problem());
			}
		} catch (GeneralSecurityException e) {
			// TODO: flag the security error
			throw new UnauthorizedException("Security Error: " + e.getLocalizedMessage());
		}

	}

	/**
	 * When entity relationships are changed, update the user object
	 * 
	 * @param user
	 * @return true if object was persisted successfully
	 */
	public static boolean updateUserEntityRelationships(ZeppaUser user) {
		// PersistenceManager mgr = getPersistenceManager();
		// try {
		// mgr.makePersistent(user);
		// } catch (Exception e) {
		// return false;
		// } finally {
		// mgr.close();
		// }

		return true;
	}

	/**
	 * Update an event when relationships are changed
	 * 
	 * @param event
	 */
	public static boolean updateEventRelationships(ZeppaEvent event) {
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
	 * Get the relationship between two
	 * 
	 * @param userId1
	 * @param userId2
	 * @return
	 */
	public static ZeppaUserToUserRelationship getUserRelationship(Long userId1, Long userId2) {
		if (userId1.longValue() == userId2.longValue()) {
			throw new NullPointerException("Missing a user id");
		}
		String filter = "(creatorId == " + userId1 + " || creatorId == " + userId2.longValue() + ") && (subjectId == "
				+ userId1.longValue() + "|| subjectId == " + userId2.longValue() + ")";

		PersistenceManager mgr = getPersistenceManager();
		ZeppaUserToUserRelationship result = null;
		try {
			Query q = mgr.newQuery(ZeppaUserToUserRelationship.class);

			q.setFilter(filter);
			q.setUnique(true);

			result = (ZeppaUserToUserRelationship) q.execute();

		} finally {
			mgr.close();
		}

		return result;
	}

	/**
	 * Get the persistence manager
	 * 
	 * @return
	 */
	private static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}

}
