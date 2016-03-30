package com.zeppamobile.api.endpoint;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.zeppamobile.api.Constants;
import com.zeppamobile.api.PMF;
import com.zeppamobile.api.datamodel.InviteGroup;
import com.zeppamobile.api.datamodel.ZeppaUser;
import com.zeppamobile.api.datamodel.ZeppaUserInfo;
import com.zeppamobile.api.endpoint.utils.ClientEndpointUtility;
import com.zeppamobile.api.endpoint.utils.RelationshipUtility;
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
			return user;
		}
		LOG.log(Level.WARNING, "User doesn't exist yet");

		// // Get a list of invite groups
		// List<InviteGroup> groups =
		// getInviteGroupsForUser(payload.getEmail());
		// // Throw Exception if user wasnt invited to use Zeppa
		// if (groups == null || groups.isEmpty()) {
		// throw new UnauthorizedException("Not invited yet");
		// }

		// Set entity information

		// Set Google Calendar

		// ZeppaUser insert = new ZeppaUser(zeppaUser.getUserInfo(),
		// zeppaUser.getZeppaCalendarId(), zeppaUser.getInitialTags());

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
		insertInfo.setPrimaryUnformattedNumber(userInfo
				.getPrimaryUnformattedNumber());
		insertInfo.setGoogleAccountEmail(payload.getEmail());

		/*
		 * Create User Data Object
		 */
		ZeppaUser insert = new ZeppaUser();
		insert.setCreated(System.currentTimeMillis());
		insert.setUpdated(System.currentTimeMillis());
		insert.setUserInfo(insertInfo);
		insert.setAuthEmail(payload.getEmail());
		insert = GoogleCalendarService.insertZeppaCalendar(insert);

		/*
		 * Persist this user
		 */
		LOG.log(Level.WARNING, "Inserting Object");

		PersistenceManager mgr = getPersistenceManager();
		try {
			// Persist the User object
			zeppaUser = mgr.makePersistent(insert);
			LOG.log(Level.WARNING, "Made Persistent");

		} finally {
			mgr.close();
		}

		// /*
		// *
		// * Create this users initial tags
		// */
		// List<EventTag> tags = new ArrayList<EventTag>();
		// // Make the initial tags
		// for (String tagText : zeppaUser.getInitialTags()) {
		// EventTag tag = new EventTag(zeppaUser, tagText);
		// tags.add(tag);
		// zeppaUser.addTag(tag);
		// }
		// // Persist
		// PersistenceManager tmgr = getPersistenceManager();
		// try {
		// tmgr.makePersistentAll(tags);
		// } finally {
		// tmgr.close();
		// }

		/*
		 * Find initial connections based on invite groups
		 */
		// Find initial connections based on other members invite groups
		// List<ZeppaUser> initialConnections = new ArrayList<ZeppaUser>();
		// for (InviteGroup group : groups) {
		// group.addGroupMember(zeppaUser);
		// List<ZeppaUser> members = group.getGroupMembers();
		// // Quickly remove already existing members before they are added
		// // back again
		// initialConnections.removeAll(members);
		// initialConnections.addAll(members);
		// }
		// // Schedule making initial mingling connections
		// for (ZeppaUser mingler : initialConnections) {
		// ZeppaUserToUserRelationship relationship = new
		// ZeppaUserToUserRelationship(
		// zeppaUser.getId(), mingler.getId(),
		// UserRelationshipType.MINGLING);
		// zeppaUser.addCreatedRealtionship(relationship);
		// mingler.addSubjectRelationship(relationship);
		// ClientEndpointUtility.updateUserEntityRelationships(mingler);
		// TaskUtility.scheduleCreateEventRelationshipsForUsers(
		// zeppaUser.getId(), mingler.getId());
		// }
		//
		// // Update user relationships
		// ClientEndpointUtility.updateUserEntityRelationships(zeppaUser);
		//
		// /*
		// * Update all invite group(s) with this user
		// */
		// PersistenceManager gmgr =
		// getPersistenceManager();
		// try {
		// gmgr.makePersistentAll(groups);
		// } finally {
		// gmgr.close();
		// }

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

		if (!zeppauser.getAuthEmail().equals(user.getAuthEmail())) {
			throw new UnauthorizedException("Cannot edit other user accounts");
		}

		PersistenceManager mgr = getPersistenceManager();
		try {
			ZeppaUserInfo currentInfo = user.getUserInfo();
			ZeppaUserInfo updatedInfo = zeppauser.getUserInfo();

			currentInfo.setGivenName(updatedInfo.getGivenName());
			currentInfo.setFamilyName(updatedInfo.getFamilyName());
			currentInfo.setImageUrl(updatedInfo.getImageUrl());
			currentInfo.setPrimaryUnformattedNumber(updatedInfo
					.getPrimaryUnformattedNumber());
			currentInfo.setUpdated(System.currentTimeMillis());

			user.setUserInfo(currentInfo);
			user.setZeppaCalendarId(zeppauser.getZeppaCalendarId());
			user.setUpdated(System.currentTimeMillis());

			zeppauser = mgr.makePersistent(user);
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
			// Delete Zeppa Cale'ndar
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
			LOG.log(Level.WARNING, "Retreived user for email: " + user.getAuthEmail());

			// "Touch" properties to be added to response objects
			user.getKey();
			user.getAuthEmail();
			user.getZeppaCalendarId();

			user.getUserInfo();
			user.getUserInfo().getGivenName();
			user.getUserInfo().getFamilyName();
			user.getUserInfo().getGoogleAccountEmail();
			user.getUserInfo().getPrimaryUnformattedNumber();
			user.getUserInfo().getImageUrl();
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
			q.setFilter("emails.contains(:email)");
			q.declareImports("java.util.List");
			q.declareParameters("List emails");
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
