package com.zeppamobile.api.endpoint.utils;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.api.server.spi.response.UnauthorizedException;
import com.zeppamobile.api.PMF;
import com.zeppamobile.api.exception.DickheadException;
import com.zeppamobile.common.auth.Authorizer;
import com.zeppamobile.common.datamodel.ZeppaEvent;
import com.zeppamobile.common.datamodel.ZeppaUser;
import com.zeppamobile.common.datamodel.ZeppaUserToUserRelationship;

/**
 * This is the base class for endpoints called from the Zeppa Client Apps
 * (General User)
 * 
 * @author Pete Schuette
 *
 */
public class ClientEndpointUtility {

	/**
	 * Fetch the user for this authorizer
	 * 
	 * @param auth
	 * @return
	 * @throws UnauthorizedException
	 */
	public static ZeppaUser getAuthorizedZeppaUser(Authorizer auth)
			throws UnauthorizedException {

		ZeppaUser result = null;
		// Verify token is authentic
		if (isValidAuth(auth) && auth.getUserId().longValue() > 0) {
			PersistenceManager mgr = getPersistenceManager();
			try {

				// Fetch user by supplied user id
				result = mgr.getObjectById(ZeppaUser.class, auth.getUserId());

				if (result.getAuthEmail().equalsIgnoreCase(auth.getEmail())) {
					// groovy
				} else {
					throw new UnauthorizedException(
							"Auth email does not match user email");
				}

			} catch (NullPointerException e) {
				// Auth item wasn't made by us
				throw new DickheadException("Bad Authorizer", ZeppaUser.class,
						Long.valueOf(-1), auth);
			} finally {
				mgr.close();
			}
		} // else, fetch user by auth email

		return result;
	}

	/**
	 * Quick method to verify authentication wrapper tokens
	 * 
	 * @param auth
	 * @return
	 * @throws DickheadException
	 */
	public static boolean isValidAuth(Authorizer auth) throws DickheadException {
		boolean isValid = true;
		// TODO: validate auth token, client id, etc.
		
		return isValid;
	}

	/**
	 * When entity relationships are changed, update the user object
	 * 
	 * @param user
	 * @return true if object was persisted successfully
	 */
	public static boolean updateUserRelationships(ZeppaUser user) {
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
	public static ZeppaUserToUserRelationship getUserRelationship(Long userId1,
			Long userId2) {
		if (userId1.longValue() == userId2.longValue()) {
			throw new NullPointerException("Missing a user id");
		}
		String filter = "(creatorId == " + userId1 + " || creatorId == "
				+ userId2.longValue() + ") && (subjectId == "
				+ userId1.longValue() + "|| subjectId == "
				+ userId2.longValue() + ")";

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
	 * Update a user relationship
	 * 
	 * @param relationship
	 */
	public static void updateUserRelationship(
			ZeppaUserToUserRelationship relationship) {
		PersistenceManager mgr = getPersistenceManager();
		try {
			mgr.makePersistent(relationship);
		} finally {
			mgr.close();
		}
	}

	/**
	 * Get the persistence manager
	 * 
	 * @return
	 */
	public static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}

}
