package com.zeppamobile.api.endpoint;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.json.simple.JSONValue;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.datanucleus.query.JDOCursorHelper;
import com.zeppamobile.api.Constants;
import com.zeppamobile.api.PMF;
import com.zeppamobile.api.datamodel.InviteGroup;
import com.zeppamobile.api.datamodel.ZeppaUser;
import com.zeppamobile.api.endpoint.utils.ClientEndpointUtility;
import com.zeppamobile.common.utils.Utils;

@Api(name = Constants.API_NAME, version = "v1", scopes = { Constants.EMAIL_SCOPE }, audiences = { Constants.WEB_CLIENT_ID })
public class InviteGroupEndpoint {

	/**
	 * Endpoint to query invite groups for this user
	 * 
	 * @param filterString
	 * @param cursorString
	 * @param orderingString
	 * @param limit
	 * @param auth
	 * @return
	 * @throws UnauthorizedException
	 */
	@SuppressWarnings({ "unchecked" })
	@ApiMethod(name = "listInviteGroup")
	public CollectionResponse<InviteGroup> listInviteGroup(
			@Nullable @Named("filter") String filterString,
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("ordering") String orderingString,
			@Nullable @Named("limit") Integer limit,
			@Named("idToken") String tokenString) throws UnauthorizedException {

		GoogleIdToken.Payload tokenPayload = ClientEndpointUtility
				.checkToken(tokenString);
		if (tokenPayload == null || !Utils.isWebSafe(tokenPayload.getEmail())) {
			throw new UnauthorizedException("Unrecognized Email");
		}

		PersistenceManager mgr = null;
		Cursor cursor = null;
		List<InviteGroup> execute = null;

		try {
			mgr = getPersistenceManager();
			Query query = mgr.newQuery(InviteGroup.class);
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
			execute = (List<InviteGroup>) query.execute();
			cursor = JDOCursorHelper.getCursor(execute);

			if (cursor == null) {
				cursorString = null;
			} else {
				cursorString = cursor.toWebSafeString();
			}
			cursorString = cursor.toWebSafeString();

			// TODO: remove bad eggs (groups that don't contain this user)

		} finally {
			mgr.close();
		}

		return CollectionResponse.<InviteGroup> builder().setItems(execute)
				.setNextPageToken(cursorString).build();

	}

	/**
	 * Insert an invite group into the data store. Automatically connect people
	 * who are already on Zeppa
	 * 
	 * @param emailList
	 *            - list of emails for people on Zeppa
	 * @param tagList
	 *            - List of recommended tags for users when they create their
	 *            account
	 * @return inviteGroup - result of
	 */
	public InviteGroup insertInviteGroup(@Named("emailListJson") String emailListString,
			@Named("tagListJson") String tagListString, @Named("idToken") String idToken) {

		/*
		 * Invite group that was entered into the datastore
		 */
		InviteGroup result = null;

		PersistenceManager mgr = getPersistenceManager();
		PersistenceManager umgr = getPersistenceManager();

		try {
			/*
			 * Parse out the params
			 */
			@SuppressWarnings("unchecked")
			List<String> emailList = (List<String>) JSONValue
					.parse(emailListString);

			@SuppressWarnings("unchecked")
			List<String> tagList = (List<String>) JSONValue
					.parse(tagListString);

			InviteGroup group = new InviteGroup();
			group.setEmails(emailList);
			group.setSuggestedTags(tagList);

			/*
			 * Get group members that already exist with user persistence
			 * manager
			 */
			Query userQuery = umgr.newQuery(ZeppaUser.class);
			userQuery.declareImports("java.util.List");
			userQuery.declareParameters("List emails");
			userQuery.setFilter(":emails.contains(authEmail)");
			@SuppressWarnings("unchecked")
			List<ZeppaUser> userList = (List<ZeppaUser>) userQuery
					.execute(emailList);

			/*
			 * Add all the existing user objects to this invite group Determine
			 * if any connections should be made at this time
			 */
			if (userList != null && !userList.isEmpty()) {
				for (ZeppaUser u : userList) {
					group.addGroupMember(u);
				}

				/*
				 * If there was more than one group member, make sure they are
				 * connected
				 */
				// Should this be done? If they have not already connected is there a reason for that?
//				if (userList.size() > 1) {
//					for (int i = 0; i < userList.size() - 1; i++) {
//						for (int j = i; j < userList.size(); j++) {
//							ZeppaUser u1 = userList.get(i);
//							ZeppaUser u2 = userList.get(j);
//
//							/*
//							 * Instantiate the relationship object type
//							 */
//							ZeppaUserToUserRelationship r = new ZeppaUserToUserRelationship(
//									u1,
//									u2,
//									ZeppaUserToUserRelationship.UserRelationshipType.MINGLING);
//							// Insert relationship. Insert method will make sure there isn't already a relationship
//							ZeppaUserToUserRelationshipEndpoint e = new ZeppaUserToUserRelationshipEndpoint();
//							e.insertZeppaUserToUserRelationship(r,idToken);
//						}
//					}
//				}
				
				
			}

			// Insert the invite group
			result = mgr.makePersistent(group);
			
			//TODO: email the people who were just invited to use Zeppa
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			mgr.close();
			umgr.close();
		}

		return result;
	}

	private static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}

}
