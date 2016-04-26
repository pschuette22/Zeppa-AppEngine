package com.zeppamobile.api.endpoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.zeppamobile.api.Constants;
import com.zeppamobile.api.PMF;
import com.zeppamobile.api.datamodel.EventTag;
import com.zeppamobile.api.datamodel.InviteGroup;
import com.zeppamobile.api.datamodel.ZeppaUser;
import com.zeppamobile.api.datamodel.ZeppaUserInfo;
import com.zeppamobile.api.datamodel.ZeppaUserToUserRelationship;
import com.zeppamobile.api.datamodel.ZeppaUserToUserRelationship.UserRelationshipType;
import com.zeppamobile.api.endpoint.utils.ClientEndpointUtility;
import com.zeppamobile.api.endpoint.utils.RelationshipUtility;
import com.zeppamobile.api.endpoint.utils.TaskUtility;
import com.zeppamobile.api.googlecalendar.GoogleCalendarService;

@Api(name = Constants.API_NAME, version = "v1", scopes = { Constants.EMAIL_SCOPE }, audiences = { Constants.WEB_CLIENT_ID })
public class ZeppaUserEndpoint {

	private static final Logger LOG = Logger.getLogger(ZeppaUserEndpoint.class
			.getName());

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
	public ZeppaUser insertZeppaUser(ZeppaUser zeppaUser,
			@Named("idToken") String tokenString) throws IOException,
			UnauthorizedException {

		LOG.log(Level.WARNING, "Inserting Zeppa User");
		// Get the Payload
		GoogleIdToken.Payload payload = ClientEndpointUtility
				.checkToken(tokenString);

		// Try to get a user for this payload
		ZeppaUser user = ClientEndpointUtility
				.getAuthorizedUserForPayload(payload);

		// If matching user is found, return found user object
		if (user != null) {
			System.out.println("User already exists");
			return user;
		}
		LOG.log(Level.WARNING, "User doesn't exist yet");

		// // Get a list of invite groups
		List<InviteGroup> groups = getInviteGroupsForUser(payload.getEmail());
		// Throw Exception if user wasnt invited to use Zeppa
		if (groups == null || groups.isEmpty()) {
			throw new UnauthorizedException("Not invited yet");
		}

		// Set entity information

		// Set Google Calendar

		/*
		 * Create user info data object
		 */
		LOG.log(Level.WARNING, "Instantiating Object");
		ZeppaUserInfo userInfo = zeppaUser.getUserInfo();

		ZeppaUserInfo insertInfo = new ZeppaUserInfo();
		insertInfo.setCreated(System.currentTimeMillis());
		insertInfo.setUpdated(System.currentTimeMillis());
		insertInfo.setGivenName(userInfo.getGivenName());
		insertInfo.setFamilyName(userInfo.getFamilyName());
		insertInfo.setImageUrl(userInfo.getImageUrl());
		if(userInfo.getGender() != null) {
			insertInfo.setGender(userInfo.getGender());
		}
		insertInfo.setDateOfBirth(userInfo.getDateOfBirth());

		/*
		 * Create User Data Object
		 */
		ZeppaUser insert = new ZeppaUser();
		insert.setCreated(System.currentTimeMillis());
		insert.setUpdated(System.currentTimeMillis());
		insert.setUserInfo(insertInfo);
		// Set the auth email to make sure user can't be made for someone else
		insert.setAuthEmail(payload.getEmail());
		insert = GoogleCalendarService.insertZeppaCalendar(insert);

		/*
		 * Persist this user
		 */
		LOG.log(Level.WARNING, "Inserting Object");
		PersistenceManager mgr = getPersistenceManager();
		// Get these now because they are not persisted
		List<String> initialTags = zeppaUser.getInitialTags();
		try {
			// Persist the User object
			zeppaUser = mgr.makePersistent(insert);
			LOG.log(Level.WARNING, "Made Persistent");

		} finally {
			mgr.close();
		}

		/*
		 * 
		 * Create this users initial tags
		 */
		List<EventTag> tags = new ArrayList<EventTag>();
		// Make the initial tags
		for (String tagText : initialTags) {
			EventTag tag = new EventTag(zeppaUser, tagText);
			tags.add(tag);
		}
		// Persist
		PersistenceManager tmgr = getPersistenceManager();
		try {
			tmgr.makePersistentAll(tags);
		} finally {
			tmgr.close();
		}

		/*
		 * Find initial connections based on invite groups
		 */
		// Find initial connections based on other members invite groups
		List<Key> initialConnectionKeys = new ArrayList<Key>();
		for (InviteGroup group : groups) {
			// Get all the keys of members
			List<Key> members = group.getGroupMemberKeys();
			// Quickly remove already existing members before they are added
			// back again
			initialConnectionKeys.removeAll(members);
			initialConnectionKeys.addAll(members);
			// Add this user to the group
			group.addGroupMember(zeppaUser);
		}

		// If there are initial connections to be made, make them
		if (!initialConnectionKeys.isEmpty()) {
			// Schedule making initial mingling connections
			PersistenceManager umgr = getPersistenceManager();
			PersistenceManager rmgr = getPersistenceManager();
			try {
				for (Key k : initialConnectionKeys) {
					try {
						// Iterate through
						ZeppaUser mingler = umgr.getObjectById(ZeppaUser.class,
								k);

						ZeppaUserToUserRelationship relationship = new ZeppaUserToUserRelationship(
								zeppaUser, mingler,
								UserRelationshipType.MINGLING);

						relationship = rmgr.makePersistent(relationship);

						TaskUtility.scheduleCreateEventRelationshipsForUsers(
								zeppaUser.getId(), mingler.getId());
						// TODO: schedule initial follows operation
					} catch (JDOObjectNotFoundException e) {
						// TODO: handle this
					}
				}
			} finally {
				umgr.close();
				rmgr.close();
			}
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
	public ZeppaUser updateZeppaUser(ZeppaUser zeppauser,
			@Named("idToken") String tokenString) throws UnauthorizedException {

		// Fetch Authorized Zeppa User
		ZeppaUser user = ClientEndpointUtility
				.getAuthorizedZeppaUser(tokenString);
		if (user == null) {
			throw new UnauthorizedException(
					"No matching user found for this token");
		}
		// Verify this user is editing the proper account
		if (!zeppauser.getAuthEmail().equals(user.getAuthEmail())) {
			throw new UnauthorizedException("Cannot edit other user accounts");
		}

		PersistenceManager mgr = getPersistenceManager();
		try {
			user = mgr.getObjectById(ZeppaUser.class, user.getKey());
			ZeppaUserInfo currentInfo = user.getUserInfo();
			ZeppaUserInfo updatedInfo = zeppauser.getUserInfo();

			// Update user info properties
			currentInfo.setGivenName(updatedInfo.getGivenName());
			currentInfo.setFamilyName(updatedInfo.getFamilyName());
			currentInfo.setImageUrl(updatedInfo.getImageUrl());
			currentInfo.setUpdated(System.currentTimeMillis());

			// Update user properties
			user.setUserInfo(currentInfo);
			user.setLatitude(zeppauser.getLatitude());
			user.setLongitude(zeppauser.getLongitude());
			user.setPhoneNumber(zeppauser.getPhoneNumber());

			zeppauser = user;
			
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
	@ApiMethod(name = "removeCurrentZeppaUser")
	public void removeCurrentZeppaUser(@Named("idToken") String tokenString)
			throws UnauthorizedException {

		// Fetch Authorized Zeppa User
		ZeppaUser user = ClientEndpointUtility
				.getAuthorizedZeppaUser(tokenString);
		if (user == null) {
			throw new UnauthorizedException(
					"No matching user found for this token");
		}

		PersistenceManager mgr = getPersistenceManager();

		try {

			RelationshipUtility.removeZeppaAccountEntities(user.getId()
					.longValue());
			// Delete Zeppa Calendar
			GoogleCalendarService.deleteCalendar(user);

			// TODO: remove all invite group references

			// Delete ZeppaUser
			mgr.deletePersistent(user);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			mgr.close();
		}
	}

	/**
	 * fetch the user with this authorizer object
	 * 
	 * */
	@ApiMethod(name = "fetchCurrentZeppaUser")
	public ZeppaUser fetchCurrentZeppaUser(@Named("idToken") String tokenString)
			throws UnauthorizedException {

		LOG.log(Level.WARNING, "Fetching current user");

		// Fetch Authorized Zeppa User
		ZeppaUser user = ClientEndpointUtility
				.getAuthorizedZeppaUser(tokenString);

		if (user != null) {
			LOG.log(Level.WARNING,
					"Retreived user for email: " + user.getAuthEmail());

			// "Touch" properties to be added to response objects
			user.getKey();
			user.getAuthEmail();
			user.getZeppaCalendarId();
			user.getPhoneNumber();
			user.getLatitude();
			user.getLongitude();
			
			user.getUserInfo();
//			user.getUserInfo().getGivenName();
//			user.getUserInfo().getFamilyName();
//			user.getUserInfo().getImageUrl();
		}

		return user;
	}

	/**
	 * Fetch invite groups for user
	 */
	@SuppressWarnings("unchecked")
	private List<InviteGroup> getInviteGroupsForUser(String email) {
		List<InviteGroup> groups = null;
		PersistenceManager mgr = getPersistenceManager();
		try {
			Query q = mgr.newQuery(InviteGroup.class);
			q.setFilter("emails.contains(email)");
			q.declareParameters("String email");
			groups = (List<InviteGroup>) q.execute(email);

		} finally {
			mgr.close();
		}

		return groups;
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
