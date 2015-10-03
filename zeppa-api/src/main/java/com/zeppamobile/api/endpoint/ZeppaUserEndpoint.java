package com.zeppamobile.api.endpoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiReference;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.zeppamobile.api.endpoint.utils.RelationshipUtility;
import com.zeppamobile.api.endpoint.utils.TaskUtility;
import com.zeppamobile.common.auth.Authorizer;
import com.zeppamobile.common.datamodel.EventTag;
import com.zeppamobile.common.datamodel.InviteGroup;
import com.zeppamobile.common.datamodel.ZeppaUser;
import com.zeppamobile.common.datamodel.ZeppaUserInfo;
import com.zeppamobile.common.datamodel.ZeppaUserToUserRelationship;
import com.zeppamobile.common.datamodel.ZeppaUserToUserRelationship.UserRelationshipType;
import com.zeppamobile.common.googlecalendar.GoogleCalendarService;

@ApiReference(BaseEndpoint.class)
public class ZeppaUserEndpoint extends BaseEndpoint {


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
			@Named("auth") Authorizer auth) throws IOException,
			UnauthorizedException {

		// Authorize this auth object. Return User object if one is found
		ZeppaUser user = getAuthorizedZeppaUser(auth);
		if (user != null) {
			return user;
		}

		// Check to see if user was invited
		List<InviteGroup> groups = getInviteGroupsForUser(auth);
		if (groups == null || groups.isEmpty()) {
			throw new UnauthorizedException("Not invited yet");
		}

		// Set entity information
		zeppaUser.setCreated(System.currentTimeMillis());
		zeppaUser.setUpdated(System.currentTimeMillis());
		zeppaUser.getUserInfo().setCreated(System.currentTimeMillis());
		zeppaUser.getUserInfo().setUpdated(System.currentTimeMillis());
		zeppaUser.setAuthEmail(auth.getEmail());

		// Set Google Calendar
		zeppaUser = GoogleCalendarService.insertZeppaCalendar(zeppaUser);

		/*
		 * Persist this user
		 */
		PersistenceManager mgr = getPersistenceManager();
		try {
			// Persist the User object
			zeppaUser = mgr.makePersistent(zeppaUser);
		} finally {
			mgr.close();
		}

		/*
		 * 
		 * Create this users initial tags
		 * 
		 */
		List<EventTag> tags = new ArrayList<EventTag>();
		// Make the initial tags
		for (String tagText : zeppaUser.getInitialTags()) {
			EventTag tag = new EventTag(zeppaUser, tagText);
			tags.add(tag);
			zeppaUser.addTag(tag);
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
		List<ZeppaUser> initialConnections = new ArrayList<ZeppaUser>();
		for (InviteGroup group : groups) {
			group.addGroupMember(zeppaUser);
			List<ZeppaUser> members = group.getGroupMembers();
			// Quickly remove already existing members before they are added
			// back again
			initialConnections.removeAll(members);
			initialConnections.addAll(members);
		}
		// Schedule making initial mingling connections
		for (ZeppaUser mingler : initialConnections) {
			ZeppaUserToUserRelationship relationship = new ZeppaUserToUserRelationship(zeppaUser.getId(), mingler.getId(), UserRelationshipType.MINGLING);
			zeppaUser.addCreatedRealtionship(relationship);
			mingler.addSubjectRelationship(relationship);
			updateUserRelationships(mingler);
			TaskUtility.scheduleCreateEventRelationshipsForUsers(zeppaUser.getId(), mingler.getId());
		}
		
		// Update user relationships
		updateUserRelationships(zeppaUser);
		
		/*
		 * Update all invite group(s) with this user
		 * 
		 */
		PersistenceManager gmgr = getPersistenceManager();
		try {
			gmgr.makePersistentAll(groups);
		} finally {
			gmgr.close();
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
			@Named("auth") Authorizer auth) throws UnauthorizedException {

		ZeppaUser user = getAuthorizedZeppaUser(auth);

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
	@ApiMethod(name = "removeZeppaUser")
	public void removeZeppaUser(@Named("id") Long userId,
			@Named("auth") Authorizer auth) throws UnauthorizedException {

		ZeppaUser user = getAuthorizedZeppaUser(auth);

		if (user.getId().longValue() != userId.longValue()) {
			throw new UnauthorizedException("Can't remove other users");
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
	public ZeppaUser fetchCurrentZeppaUser(@Named("auth") Authorizer auth)
			throws UnauthorizedException {
		return getAuthorizedZeppaUser(auth);
	}

	/**
	 * Fetch invite groups for user
	 */
	@SuppressWarnings("unchecked")
	private List<InviteGroup> getInviteGroupsForUser(Authorizer auth) {
		List<InviteGroup> groups = null;
		PersistenceManager mgr = getPersistenceManager();
		try {
			Query q = mgr.newQuery(InviteGroup.class);
			q.setFilter("emails.contains(:email)");
			q.declareImports("java.util.List");
			groups = (List<InviteGroup>) q.execute(auth.getEmail());

		} finally {
			mgr.close();
		}

		return groups;
	}

}
