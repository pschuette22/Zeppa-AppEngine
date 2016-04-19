package com.zeppamobile.api.endpoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.datanucleus.query.JDOCursorHelper;
import com.zeppamobile.api.Constants;
import com.zeppamobile.api.PMF;
import com.zeppamobile.api.datamodel.ZeppaUser;
import com.zeppamobile.api.datamodel.ZeppaUserToUserRelationship;
import com.zeppamobile.api.datamodel.ZeppaUserToUserRelationship.UserRelationshipType;
import com.zeppamobile.api.endpoint.utils.ClientEndpointUtility;
import com.zeppamobile.api.endpoint.utils.TaskUtility;
import com.zeppamobile.api.notifications.NotificationUtility;
import com.zeppamobile.api.notifications.PayloadBuilder;
import com.zeppamobile.common.utils.Utils;

@Api(name = Constants.API_NAME, version = "v1", scopes = { Constants.EMAIL_SCOPE }, audiences = { Constants.WEB_CLIENT_ID })
public class ZeppaUserToUserRelationshipEndpoint {

	// private static final Logger log = Logger
	// .getLogger(ZeppaUserToUserRelationshipEndpoint.class.getName());

	/**
	 * This method lists all the entities inserted in datastore. It uses HTTP
	 * GET method and paging support.
	 * 
	 * @return A CollectionResponse class containing the list of all entities
	 *         persisted and a cursor to the next page.
	 * @throws OAuthRequestException
	 */

	@SuppressWarnings("unchecked")
	@ApiMethod(name = "listZeppaUserToUserRelationship", path = "listZeppaUserToUserRelationship")
	public CollectionResponse<ZeppaUserToUserRelationship> listZeppaUserToUserRelationship(
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
		List<ZeppaUserToUserRelationship> execute = null;

		try {
			mgr = getPersistenceManager();
			Query query = mgr.newQuery(ZeppaUserToUserRelationship.class);
			if (cursorString != null && cursorString != "") {
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

			execute = (List<ZeppaUserToUserRelationship>) query
					.executeWithArray();

			cursor = JDOCursorHelper.getCursor(execute);
			if (cursor == null) {
				cursorString = null;
			} else {
				cursorString = cursor.toWebSafeString();
			}

			// /*
			// * Remove entities the calling user isnt allowed to see
			// */
			// List<ZeppaUserToUserRelationship> badEggs = new
			// ArrayList<ZeppaUserToUserRelationship>();
			// for (ZeppaUserToUserRelationship relationship : execute) {
			// if (relationship.getCreatorId().longValue() == user.getId()
			// .longValue()
			// || relationship.getSubjectId().longValue() == user
			// .getId().longValue()
			// || relationship.getRelationshipType().equals(
			// UserRelationshipType.MINGLING)) {
			// // Users can see relationships they are involved in
			// // They can also see who is mingling with who
			//
			// } else {
			// badEggs.add(relationship);
			// }
			// }
			// execute.removeAll(badEggs);

			List<ZeppaUserToUserRelationship> unfoundUsers = new ArrayList<ZeppaUserToUserRelationship>();
			for (ZeppaUserToUserRelationship r : execute) {
				try {
					Long otherUserId = r.getOtherUserId(user.getId());
					ZeppaUser otherUser = mgr.getObjectById(ZeppaUser.class,
							otherUserId);
					r.setUserInfo(otherUser.getUserInfo());
				} catch (JDOObjectNotFoundException e) {
					// couldnt find the user, remove the relationship from the
					// equation.. Queue for removal?
					unfoundUsers.add(r);
				}

			}
			execute.removeAll(unfoundUsers);

		} finally {
			mgr.close();
		}

		return CollectionResponse.<ZeppaUserToUserRelationship> builder()
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
	@ApiMethod(name = "getZeppaUserToUserRelationship", path = "getZeppaUserToUserRelationship")
	public ZeppaUserToUserRelationship getZeppaUserToUserRelationship(
			@Named("relationshipId") Long relationshipId,
			@Named("idToken") String tokenString) throws UnauthorizedException {

		// Fetch Authorized Zeppa User
		ZeppaUser user = ClientEndpointUtility
				.getAuthorizedZeppaUser(tokenString);
		if (user == null) {
			throw new UnauthorizedException(
					"No matching user found for this token");
		}

		PersistenceManager mgr = getPersistenceManager();
		ZeppaUserToUserRelationship zeppausertouserrelationship = null;
		try {
			zeppausertouserrelationship = mgr.getObjectById(
					ZeppaUserToUserRelationship.class, relationshipId);

			// Verify authorized user is involved with this relationship
			if (zeppausertouserrelationship.getCreatorId().longValue() != user
					.getId().longValue()
					&& zeppausertouserrelationship.getSubjectId().longValue() != user
							.getId().longValue()) {
				throw new UnauthorizedException(
						"Not authorized to create relationships for other people");
			} else {
				Long otherUserId = zeppausertouserrelationship.getOtherUserId(user.getId());
				ZeppaUser otherUser = mgr.getObjectById(ZeppaUser.class, otherUserId);
				zeppausertouserrelationship.setUserInfo(otherUser.getUserInfo());
			}

		} finally {
			mgr.close();
		}

		return zeppausertouserrelationship;
	}

	/**
	 * This inserts a new entity into App Engine datastore. If the entity
	 * already exists in the datastore, an exception is thrown. It uses HTTP
	 * POST method.
	 * 
	 * @param zeppausertouserrelationship
	 *            the entity to be inserted.
	 * @return The inserted entity.
	 * @throws OAuthRequestException
	 */
	@ApiMethod(name = "insertZeppaUserToUserRelationship")
	public ZeppaUserToUserRelationship insertZeppaUserToUserRelationship(
			ZeppaUserToUserRelationship relationship,
			@Named("idToken") String tokenString) throws UnauthorizedException,
			BadRequestException {

		try {
			// Make sure params are good
			if (relationship.getCreatorId().longValue() == relationship
					.getSubjectId()) {
				throw new BadRequestException(
						"Subject and Creator ID's are equal");
			}
		} catch (NullPointerException e) {
			throw new BadRequestException("Missing user ID(s)");
		}

		// Fetch Authorized Zeppa User
		ZeppaUser user = ClientEndpointUtility
				.getAuthorizedZeppaUser(tokenString);
		if (user == null) {
			throw new UnauthorizedException(
					"No matching user found for this token");
		}

		// Verify authorized user is involved with this relationship
		if (relationship.getCreatorId().longValue() != user.getId().longValue()) {
			throw new UnauthorizedException(
					"Not authorized to create relationships for other people");
		}

		// Get the other user
		Long otherUserId = relationship
				.getOtherUserId(user.getId().longValue());
		ZeppaUser otherUser = getUserById(otherUserId);

		PersistenceManager mgr = getPersistenceManager();

		// Check to make sure the relationship does not already exist
		try {
			Query q = mgr.newQuery(ZeppaUserToUserRelationship.class);
			q.declareParameters("Long callingUserIdParam, Long otherUserIdParam");
			q.setFilter("(creatorId == otherUserIdParam || creatorId == callingUserIdParam) && (subjectId == callingUserIdParam || subjectId == otherUserIdParam)");
			q.setUnique(true);
			ZeppaUserToUserRelationship r = (ZeppaUserToUserRelationship) q
					.execute(relationship.getCreatorId(),
							relationship.getSubjectId());

			if (r != null) {
				mgr.close();

				// If both are pending type, set relationship type to accepted
				if (r.getRelationshipType().equals(
						UserRelationshipType.PENDING_REQUEST)
						&& relationship.getRelationshipType().equals(
								UserRelationshipType.PENDING_REQUEST)
						&& r.getCreatorId().longValue() == relationship
								.getSubjectId().longValue()) {
					r.setRelationshipType(UserRelationshipType.MINGLING);
					r = mgr.makePersistent(r);
					// Notify both users they are now mingling?
				}
				r.setUserInfo(otherUser.getUserInfo());
				return r;
			}

		} catch (javax.jdo.JDOObjectNotFoundException e) {
			// Expect this not to exist
		}

		Transaction txn = mgr.currentTransaction();
		// Verified the relationship doesn't exist so create a new one
		try {

			txn.begin();
			ZeppaUserToUserRelationship insert = new ZeppaUserToUserRelationship(
					user, otherUser, UserRelationshipType.PENDING_REQUEST);
			// // set entity values
			// relationship.setCreated(System.currentTimeMillis());
			// relationship.setUpdated(System.currentTimeMillis());
			// relationship.setCreator(user);
			// relationship.setSubject(otherUser);

			// persist the relationship
			relationship = mgr.makePersistent(insert);

			// Schedule notification to inform the subject user, someone wants
			// to mingle
			NotificationUtility.scheduleNotificationBuild(
					ZeppaUserToUserRelationship.class.getName(),
					relationship.getId(), "mingle-request");

			txn.commit();

			relationship.setUserInfo(otherUser.getUserInfo());

		} finally {
			if (txn.isActive()) {
				txn.rollback();
				relationship = null;
			}
			mgr.close();
		}

		return relationship;
	}

	/**
	 * This method is used for updating an existing entity. If the entity does
	 * not exist in the datastore, an exception is thrown. It uses HTTP PUT
	 * method.
	 * 
	 * @param zeppausertouserrelationship
	 *            the entity to be updated.
	 * @return The updated entity.
	 * @throws OAuthRequestException
	 */
	@ApiMethod(name = "updateZeppaUserToUserRelationship")
	public ZeppaUserToUserRelationship updateZeppaUserToUserRelationship(
			ZeppaUserToUserRelationship relationship,
			@Named("idToken") String tokenString) throws UnauthorizedException {

		// Fetch Authorized Zeppa User
		ZeppaUser user = ClientEndpointUtility
				.getAuthorizedZeppaUser(tokenString);
		if (user == null) {
			throw new UnauthorizedException(
					"No matching user found for this token");
		}

		PersistenceManager mgr = getPersistenceManager();
		Transaction txn = mgr.currentTransaction();
		try {
			txn.begin();
			// Fetch the current state of the user relationship
			ZeppaUserToUserRelationship current = mgr.getObjectById(
					ZeppaUserToUserRelationship.class, relationship.getKey());

			// Verify user is able to make changes to this relationship
			if (current.getCreatorId().longValue() != user.getId().longValue()
					&& current.getSubjectId().longValue() != user.getId()
							.longValue()) {
				throw new UnauthorizedException(
						"Not authorized to update relationships you're not part of");
			}

			// Determine if the user accepted a request to mingle
			if (relationship.getRelationshipType().equals(
					UserRelationshipType.MINGLING)
					&& current.getRelationshipType().equals(
							UserRelationshipType.PENDING_REQUEST)) {
				NotificationUtility.scheduleNotificationBuild(
						ZeppaUserToUserRelationship.class.getName(),
						relationship.getId(), "mingling");

			}

			// Update entities of the relationship
			current.setUpdated(System.currentTimeMillis());
			current.setRelationshipType(relationship.getRelationshipType());

			// make changes to the datastore
			relationship = mgr.makePersistent(current);
			txn.commit();

		} finally {
			if (txn.isActive()) {
				txn.rollback();
				relationship = null;
			}
			mgr.close();

		}

		return relationship;
	}

	/**
	 * This method removes the entity with primary key id. It uses HTTP DELETE
	 * method.
	 * 
	 * @param id
	 *            the primary key of the entity to be deleted.
	 * 
	 */
	@ApiMethod(name = "removeZeppaUserToUserRelationship")
	public void removeZeppaUserToUserRelationship(@Named("id") Long id,
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

			// Fetch users involved in this relationship
			ZeppaUserToUserRelationship relationship = mgr.getObjectById(
					ZeppaUserToUserRelationship.class, id);

			// Verify authorized user is involved with this relationship
			if (relationship.getCreatorId().longValue() != user.getId()
					.longValue()
					&& relationship.getSubjectId().longValue() != user.getId()
							.longValue()) {
				throw new UnauthorizedException(
						"Not authorized to remove relationships you're not part of");
			}

			// Get the other user
			Long otherUserId = relationship.getOtherUserId(user.getId()
					.longValue());
			ZeppaUser otherUser = getUserById(otherUserId);

			// If users are mingling make appropriate adjustments
			if (relationship.getRelationshipType() == UserRelationshipType.MINGLING) {

				// Create task to delete relationships between users
				TaskUtility.scheduleDeleteRelationshipsBetweenUsers(user
						.getId().longValue(), otherUser.getId().longValue());

				String payload = PayloadBuilder
						.silentUserRelationshipDeletedPayload(user.getId(),
								otherUser.getId());
				NotificationUtility.preprocessNotificationDelivery(payload,
						otherUser.getId().longValue());
			}

			// remove the relationships from data store
			mgr.deletePersistent(relationship);

		} finally {
			mgr.close();
		}
	}

	/**
	 * 
	 * @param userId
	 * @return
	 */
	private ZeppaUser getUserById(Long userId) {
		ZeppaUser result = null;

		PersistenceManager mgr = getPersistenceManager();
		try {
			result = mgr.getObjectById(ZeppaUser.class, userId);
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
	public static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}

}
