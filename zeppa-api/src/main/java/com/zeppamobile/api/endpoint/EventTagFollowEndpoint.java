package com.zeppamobile.api.endpoint;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.zeppamobile.api.datamodel.EventTag;
import com.zeppamobile.api.datamodel.EventTagFollow;
import com.zeppamobile.api.datamodel.ZeppaUser;
import com.zeppamobile.api.datamodel.ZeppaUserToUserRelationship;
import com.zeppamobile.api.datamodel.ZeppaUserToUserRelationship.UserRelationshipType;
import com.zeppamobile.api.endpoint.utils.ClientEndpointUtility;
import com.zeppamobile.common.utils.Utils;

@Api(name = Constants.API_NAME, version = "v1", scopes = { Constants.EMAIL_SCOPE }, audiences = { Constants.WEB_CLIENT_ID })
public class EventTagFollowEndpoint {

	/**
	 * This method lists all the entities inserted in datastore. It uses HTTP
	 * GET method and paging support.
	 * 
	 * @return A CollectionResponse class containing the list of all entities
	 *         persisted and a cursor to the next page.
	 * @throws OAuthRequestException
	 */
	@SuppressWarnings({ "unchecked" })
	@ApiMethod(name = "listEventTagFollow")
	public CollectionResponse<EventTagFollow> listEventTagFollow(
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
		List<EventTagFollow> execute = null;

		try {
			mgr = getPersistenceManager();
			Query query = mgr.newQuery(EventTagFollow.class);
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

			// Get the list of followers
			execute = (List<EventTagFollow>) query.execute();
			cursor = JDOCursorHelper.getCursor(execute);

			if (cursor == null) {
				cursorString = null;
			} else {
				cursorString = cursor.toWebSafeString();
			}
			cursorString = cursor.toWebSafeString();

			/*
			 * Pick out the bad eggs: follows this user isnt allowed to see
			 */
			List<EventTagFollow> badEggs = new ArrayList<EventTagFollow>();
			for (EventTagFollow f : execute) {
				if (f.getFollowerId().longValue() == user.getId().longValue()) {
					// TODO: let tag owners see it?
					// yay
				} else {
					badEggs.add(f);
				}
			}
			execute.removeAll(badEggs);

		} finally {
			mgr.close();
		}

		return CollectionResponse.<EventTagFollow> builder().setItems(execute)
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
	@ApiMethod(name = "getEventTagFollow")
	public EventTagFollow getEventTagFollow(@Named("id") Long id,
			@Named("idToken") String tokenString) throws UnauthorizedException {

		// Fetch Authorized Zeppa User
		ZeppaUser user = ClientEndpointUtility
				.getAuthorizedZeppaUser(tokenString);
		if (user == null) {
			throw new UnauthorizedException(
					"No matching user found for this token");
		}

		PersistenceManager mgr = getPersistenceManager();
		EventTagFollow eventtagfollow = null;
		try {
			eventtagfollow = mgr.getObjectById(EventTagFollow.class, id);

			if (eventtagfollow.getFollowerId().longValue() == user.getId()
					.longValue()) {
				// Sick
			} else {
				throw new UnauthorizedException("Not allowed to get this");
			}

		} finally {
			mgr.close();
		}
		return eventtagfollow;
	}

	/**
	 * This inserts a new entity into App Engine datastore. If the entity
	 * already exists in the datastore, an exception is thrown. It uses HTTP
	 * POST method.
	 * 
	 * @param eventtagfollow
	 *            the entity to be inserted.
	 * @return The inserted entity.
	 * @throws OAuthRequestException
	 */
	@ApiMethod(name = "insertEventTagFollow")
	public EventTagFollow insertEventTagFollow(EventTagFollow eventtagfollow,
			@Named("idToken") String tokenString) throws UnauthorizedException {

		if (eventtagfollow.getTagId() == null) {
			throw new NullPointerException();
		}

		// Fetch Authorized Zeppa User
		ZeppaUser user = ClientEndpointUtility
				.getAuthorizedZeppaUser(tokenString);
		if (user == null) {
			throw new UnauthorizedException(
					"No matching user found for this token");
		}

		ZeppaUserToUserRelationship relationship = ClientEndpointUtility
				.getUserRelationship(user.getId().longValue(), eventtagfollow
						.getTagOwnerId().longValue());

		if (relationship == null
				|| !relationship.getRelationshipType().equals(
						UserRelationshipType.MINGLING)) {
			throw new UnauthorizedException("Can't follow tags by this user");
		}

		if (eventtagfollow.getFollowerId().longValue() != user.getId()
				.longValue()) {
			throw new UnauthorizedException(
					"Can't create follow for someone else");
		}

		eventtagfollow.setCreated(System.currentTimeMillis());
		eventtagfollow.setUpdated(System.currentTimeMillis());

		PersistenceManager mgr = getPersistenceManager();

		try {

			/*
			 * Get tag and update relationships
			 */
			EventTag tag = getTagById(eventtagfollow.getTagId());
			/*
			 * Set the relationships 
			 */
			eventtagfollow.setTag(tag);
			eventtagfollow.setRelationship(relationship);
			eventtagfollow.setFollower(user);
			
			// Store and update
			eventtagfollow = mgr.makePersistent(eventtagfollow);

			if (tag.addEventTagFollow(eventtagfollow)) {
				// Successfully added event tag follow
			}

			// Update relationship holding follows
			if (relationship.addTagFollow(eventtagfollow)) {
				// successfully added tag follow to relationship
			}

		} finally {

			mgr.close();
		}
		return eventtagfollow;
	}

	// @ApiMethod(name = "insertEventTagFollowArray")
	// public CollectionResponse<EventTagFollow> insertEventTagFollowArray(
	// @Named("jsonArray") String arrayAsJson,
	// @Named("idToken") String tokenString) {
	// List<EventTagFollow> result = new ArrayList<EventTagFollow>();
	//
	// JSONArray array = (JSONArray) JSONValue.parse(arrayAsJson);
	//
	// for (int i = 0; i < array.size(); i++) {
	// JSONObject obj = (JSONObject) array.get(i);
	//
	// EventTagFollow follow = new EventTagFollow(obj);
	// result.add(follow);
	// }
	//
	// if (!result.isEmpty()) {
	// PersistenceManager mgr = getPersistenceManager();
	//
	// try {
	//
	// // Store and update
	// result = (List<EventTagFollow>) mgr.makePersistentAll(result);
	//
	// } finally {
	//
	// mgr.close();
	// }
	// }
	//
	// return CollectionResponse.<EventTagFollow> builder().setItems(result)
	// .build();
	// }

	/**
	 * This method is used for updating an existing entity. If the entity does
	 * not exist in the datastore, an exception is thrown. It uses HTTP PUT
	 * method.
	 * 
	 * @param eventtagfollow
	 *            the entity to be updated.
	 * @return The updated entity.
	 * @throws OAuthRequestException
	 */
	@ApiMethod(name = "updateEventTagFollow")
	public EventTagFollow updateEventTagFollow(EventTagFollow eventtagfollow,
			@Named("idToken") String tokenString) throws UnauthorizedException {

		// Fetch Authorized Zeppa User
		ZeppaUser user = ClientEndpointUtility
				.getAuthorizedZeppaUser(tokenString);
		if (user == null) {
			throw new UnauthorizedException(
					"No matching user found for this token");
		}

		if (eventtagfollow.getFollowerId().longValue() != user.getId()
				.longValue()) {
			throw new UnauthorizedException(
					"Can't update follows you don't own");
		}

		eventtagfollow.setUpdated(System.currentTimeMillis());
		PersistenceManager mgr = getPersistenceManager();
		try {

			mgr.makePersistent(eventtagfollow);
		} finally {
			mgr.close();
		}
		return eventtagfollow;
	}

	/**
	 * This method removes the entity with primary key id. It uses HTTP DELETE
	 * method.
	 * 
	 * @param id
	 *            the primary key of the entity to be deleted.
	 * @throws OAuthRequestException
	 */
	@ApiMethod(name = "removeEventTagFollow")
	public void removeEventTagFollow(@Named("id") Long id,
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
			// Retrieve tag by Id
			EventTagFollow eventtagfollow = mgr.getObjectById(
					EventTagFollow.class, id);

			if (eventtagfollow.getFollowerId().longValue() != user.getId()
					.longValue()) {
				throw new UnauthorizedException(
						"Can't update follows you don't own");
			}

			/*
			 * Get tag and update relationships
			 */
			EventTag tag = getTagById(eventtagfollow.getTagId());
			// remove mapping to tag
			if (tag.removeFollow(eventtagfollow)) {
				// Removed follow from tag
			}
			
			if(eventtagfollow.getRelationship().removeTagFollow(eventtagfollow)){
				// Removed mapping to user relationship
			}
			
			if(eventtagfollow.getFollower().removeTagFollow(eventtagfollow)){
				// Removed mapping to follower
			}

			// remove the tag
			mgr.deletePersistent(eventtagfollow);

		} finally {
			mgr.close();
		}
	}

	/**
	 * Get an event tag by it's database identifier
	 * 
	 * @param tagId
	 * @param auth
	 * @return Event tag for this ID
	 */
	private EventTag getTagById(Long tagId) {
		EventTag tag = null;
		PersistenceManager mgr = getPersistenceManager();
		/*
		 * Get the tag assume user is authenticaed at this TODO: veridy user is
		 * allowed to see this
		 */
		try {
			tag = mgr.getObjectById(EventTag.class, tagId);
		} finally {
			mgr.close();
		}

		return tag;
	}

	private static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}

}
