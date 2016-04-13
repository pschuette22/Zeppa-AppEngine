package com.zeppamobile.api.endpoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

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
import com.zeppamobile.api.datamodel.EventTag;
import com.zeppamobile.api.datamodel.EventTagFollow;
import com.zeppamobile.api.datamodel.ZeppaUser;
import com.zeppamobile.api.datamodel.ZeppaUserToUserRelationship;
import com.zeppamobile.api.endpoint.utils.ClientEndpointUtility;
import com.zeppamobile.api.endpoint.utils.TaskUtility;
import com.zeppamobile.common.utils.Utils;

@Api(name = Constants.API_NAME, version = "v1", scopes = { Constants.EMAIL_SCOPE }, audiences = { Constants.WEB_CLIENT_ID })
public class EventTagEndpoint {

	/**
	 * This method lists all the entities inserted in datastore. It uses HTTP
	 * GET method and paging support.
	 * 
	 * @return A CollectionResponse class containing the list of all entities
	 *         persisted and a cursor to the next page.
	 * @throws OAuthRequestException
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listEventTag", path = "listEventTag")
	public CollectionResponse<EventTag> listEventTag(
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
		List<EventTag> execute = null;

		try {
			mgr = getPersistenceManager();
			Query query = mgr.newQuery(EventTag.class);
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

			execute = (List<EventTag>) query.execute();

			cursor = JDOCursorHelper.getCursor(execute);
			if (cursor == null) {
				cursorString = null;
			} else {
				cursorString = cursor.toWebSafeString();
			}

			// Tight loop for fetching all entities from datastore and
			// accomodate
			// for lazy fetch.
			for (EventTag obj : execute)
				;
		} finally {
			mgr.close();
		}

		return CollectionResponse.<EventTag> builder().setItems(execute)
				.setNextPageToken(cursorString).build();
	}

	// /**
	// * This method gets the entity having primary key id. It uses HTTP GET
	// * method.
	// *
	// * @param id
	// * the primary key of the java bean.
	// * @return The entity with primary key id.
	// * @throws OAuthRequestException
	// */
	// @ApiMethod(name = "getEventTag")
	// public EventTag getEventTag(Key key,
	// @Named("idToken") String tokenString) throws UnauthorizedException {
	//
	// // Fetch Authorized Zeppa User
	// ZeppaUser user = ClientEndpointUtility
	// .getAuthorizedZeppaUser(tokenString);
	// if (user == null) {
	// throw new UnauthorizedException(
	// "No matching user found for this token");
	// }
	//
	// PersistenceManager mgr = getPersistenceManager();
	// EventTag eventtag = null;
	// try {
	// eventtag = mgr.getObjectById(EventTag.class, key);
	// } finally {
	// mgr.close();
	// }
	// return eventtag;
	// }

	/**
	 * 
	 * @param eventtag
	 *            the entity to be inserted.
	 * @return The inserted entity.
	 * @throws OAuthRequestException
	 */
	@SuppressWarnings("unchecked")
	@ApiMethod(name = "insertEventTag")
	public EventTag insertEventTag(EventTag eventtag,
			@Named("idToken") String tokenString) throws UnauthorizedException {

		if (eventtag.getOwnerId() == null) {
			throw new NullPointerException("Event Tag Must Specify OwnerId");
		}

		// Fetch Authorized Zeppa User
		ZeppaUser user = ClientEndpointUtility
				.getAuthorizedZeppaUser(tokenString);
		if (user == null) {
			throw new UnauthorizedException(
					"No matching user found for this token");
		}

		// This could be unnecessary
		if (eventtag.getOwnerId().longValue() != user.getId().longValue()) {
			throw new UnauthorizedException("Cannot make tags for other people");
		}

		PersistenceManager mgr = getPersistenceManager();
		// Persistence manager for making changes to the user
		// PersistenceManager umgr = getPersistenceManager();
		Transaction txn = mgr.currentTransaction();
		try {
			txn.begin();

			// Initialize the tag
			EventTag insert = new EventTag(user, eventtag.getTagText());

			// Update and store objects
			eventtag = mgr.makePersistent(insert);

			// Commit the transaction
			txn.commit();

			// Create follow relationships to this tag (with -1 interest
			// indicating interest has not yet been calculated)

			// Query for existing user relationships for this user
			Query query = mgr.newQuery(ZeppaUserToUserRelationship.class);
			query.setFilter("((creatorId=="+user.getId()+")||(subjectId=="+user.getId()+")) && relationshipType=='MINGLING'");
			
			List<ZeppaUserToUserRelationship> relationships = (List<ZeppaUserToUserRelationship>) query.execute();
			List<EventTagFollow> tagFollowObjects = new ArrayList<EventTagFollow>();
			// Iterate through all the relevant relationships to this user and create follow relationship
			for(ZeppaUserToUserRelationship relationship: relationships) {
				// Generate this relationship
				EventTagFollow follow = new EventTagFollow(eventtag, relationship.getOtherUserId(user.getId()));
				tagFollowObjects.add(follow);
			}
			
			tagFollowObjects = (List<EventTagFollow>) mgr.makePersistentAll(tagFollowObjects);
			txn.commit();
			
			// Schedule this tag to be indexed 
			TaskUtility.scheduleIndexEventTag(eventtag, true);
			
		} finally {
			// If transaction was not committed, roll it back
			if (txn.isActive()) {
				System.out.println("Transaction rolled back");
				txn.rollback();
				eventtag = null;
			}
			// Close persistence managers to finalize
			mgr.close();
		}

		return eventtag;
	}

	/**
	 * This method removes the entity with primary key id. It uses HTTP DELETE
	 * method.
	 * 
	 * @param id
	 *            the primary key of the entity to be deleted.
	 * @throws OAuthRequestException
	 */
	@ApiMethod(name = "removeEventTag")
	public void removeEventTag(@Named("tagId") Long tagId,
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
			// Search through users tags for one to be removed
			EventTag eventtag = mgr.getObjectById(EventTag.class, tagId);

			if (eventtag != null) {
				txn.begin();
				// Remove the follow objects
				long deleted = mgr.newQuery(EventTagFollow.class,
						"tagId==" + eventtag.getId().longValue())
						.deletePersistentAll();
				// TODO: remove references to this tag in events
				// Remove the tag
				mgr.deletePersistent(eventtag);
				txn.commit();
			} else {
				throw new UnauthorizedException(
						"Cannot delete a tag you don't own");
			}

		} catch (javax.jdo.JDOObjectNotFoundException e) {
			e.printStackTrace();
			// object with this ID was not found in db
		} finally {
			mgr.close();
		}
	}

	private static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}

}
