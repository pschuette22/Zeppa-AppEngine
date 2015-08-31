package com.zeppamobile.api.endpoint;

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
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.datanucleus.query.JDOCursorHelper;
import com.zeppamobile.api.PMF;
import com.zeppamobile.api.endpoint.utils.TaskUtility;
import com.zeppamobile.api.notifications.NotificationUtility;
import com.zeppamobile.api.notifications.PayloadBuilder;
import com.zeppamobile.common.datamodel.ZeppaUser;
import com.zeppamobile.common.datamodel.ZeppaUserToUserRelationship;
import com.zeppamobile.common.datamodel.ZeppaUserToUserRelationship.UserRelationshipType;
import com.zeppamobile.common.utils.Utils;

@ApiReference(AppEndpointBase.class)
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

	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listZeppaUserToUserRelationship")
	public CollectionResponse<ZeppaUserToUserRelationship> listZeppaUserToUserRelationship(
			@Nullable @Named("filter") String filterString,
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("ordering") String orderingString,
			@Nullable @Named("limit") Integer limit) {

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
			// Tight loop for fetching all entities from datastore and
			// accomodate
			// for lazy fetch.
			for (ZeppaUserToUserRelationship obj : execute)
				;
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
	@ApiMethod(name = "getZeppaUserToUserRelationship")
	public ZeppaUserToUserRelationship getZeppaUserToUserRelationship(
			@Named("relationshipId") Long relationshipId) {

		PersistenceManager mgr = getPersistenceManager();
		ZeppaUserToUserRelationship zeppausertouserrelationship;
		try {
			zeppausertouserrelationship = mgr.getObjectById(
					ZeppaUserToUserRelationship.class, relationshipId);
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
			ZeppaUserToUserRelationship relationship) {

		if (relationship.getCreatorId() == null) {
			throw new NullPointerException("CreatorId not specified");
		}

		if (relationship.getSubjectId() == null) {
			throw new NullPointerException("SubjectId not specified");
		}

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
				}

				return r;
			}

		} catch (javax.jdo.JDOObjectNotFoundException e) {
			// Expect this not to exist
		}

		// Verified the relationship doesn't exist so create a new one
		try {

			// Persist relationship
			relationship.setCreated(System.currentTimeMillis());
			relationship.setUpdated(System.currentTimeMillis());

			relationship = mgr.makePersistent(relationship);

		} finally {
			mgr.close();
		}

		if (relationship.getRelationshipType().equals(
				UserRelationshipType.PENDING_REQUEST)) {

			NotificationUtility.scheduleNotificationBuild(
					ZeppaUserToUserRelationship.class.getName(),
					relationship.getId(), "mingle-request");

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
			ZeppaUserToUserRelationship relationship) {

		PersistenceManager mgr = getPersistenceManager();
		boolean didAcceptRequest = false;
		try {
			mgr.currentTransaction().begin();

			ZeppaUserToUserRelationship current = mgr.getObjectById(
					ZeppaUserToUserRelationship.class, relationship.getId());

			didAcceptRequest = (relationship.getRelationshipType().equals(
					UserRelationshipType.MINGLING) && current
					.getRelationshipType().equals(
							UserRelationshipType.PENDING_REQUEST));

			current.setUpdated(System.currentTimeMillis());
			current.setRelationshipType(relationship.getRelationshipType());

			relationship = mgr.makePersistent(current);
			mgr.currentTransaction().commit();

		} finally {

			if (mgr.currentTransaction().isActive()) {
				mgr.currentTransaction().rollback();
			}

		}

		if (didAcceptRequest) {
			NotificationUtility.scheduleNotificationBuild(
					ZeppaUserToUserRelationship.class.getName(),
					relationship.getId(), "mingling");

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
	public void removeZeppaUserToUserRelationship(
			@Named("relationshipId") Long relationshipId,
			@Named("userId") Long userId) {

		PersistenceManager mgr = getPersistenceManager();
		PersistenceManager umgr = getPersistenceManager();
		try {

			ZeppaUserToUserRelationship relationship = mgr.getObjectById(
					ZeppaUserToUserRelationship.class, relationshipId);
			if (relationship.getRelationshipType() == UserRelationshipType.MINGLING) {

				try {
					ZeppaUser user1 = umgr.getObjectById(ZeppaUser.class,
							relationship.getCreatorId());
					ZeppaUser user2 = umgr.getObjectById(ZeppaUser.class,
							relationship.getSubjectId());
					TaskUtility.scheduleDeleteRelationshipsBetweenUsers(user1
							.getId().longValue(), user2.getId().longValue());

					if (user1.getId().longValue() == userId.longValue()) {
						String payload = PayloadBuilder
								.silentUserRelationshipDeletedPayload(
										user1.getId(), user2.getId());
						NotificationUtility.preprocessNotificationDelivery(
								payload, user2.getId().longValue());
					} else {
						String payload = PayloadBuilder
								.silentUserRelationshipDeletedPayload(
										user2.getId(), user1.getId());
						NotificationUtility.preprocessNotificationDelivery(
								payload, user1.getId().longValue());
					}

				} catch (JDOObjectNotFoundException e) {
					// Couldn't find one of the users
				} finally {
					umgr.close();
				}
			}

			mgr.deletePersistent(relationship);

		} finally {
			mgr.close();
		}
	}

	private static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}

}
