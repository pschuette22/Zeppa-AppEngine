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
import com.zeppamobile.api.datamodel.ZeppaUser;
import com.zeppamobile.api.datamodel.ZeppaUserInfo;
import com.zeppamobile.api.endpoint.utils.ClientEndpointUtility;
import com.zeppamobile.common.utils.JSONUtils;
import com.zeppamobile.common.utils.Utils;

@Api(name = Constants.API_NAME, version = "v1", scopes = { Constants.EMAIL_SCOPE }, audiences = { Constants.WEB_CLIENT_ID })
public class ZeppaUserInfoEndpoint {

	/**
	 * This method lists all the entities inserted in datastore. It uses HTTP
	 * GET method and paging support.
	 * 
	 * @return A CollectionResponse class containing the list of all entities
	 *         persisted and a cursor to the next page.
	 * @throws OAuthRequestException
	 */
	@SuppressWarnings({ "unchecked" })
	@ApiMethod(name = "listZeppaUserInfo")
	public CollectionResponse<ZeppaUserInfo> listZeppaUserInfo(
			@Nullable @Named("filter") String filterString,
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("ordering") String orderingString,
			@Nullable @Named("limit") Integer limit,
			@Nullable @Named("stringListArg") String listArg,
			@Nullable @Named("stringListArg2") String listArg2,
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
		List<ZeppaUserInfo> execute = null;
		
		List<Object> args = new ArrayList<Object>();
		
		try {
			mgr = getPersistenceManager();
			Query query = mgr.newQuery(ZeppaUserInfo.class);
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
			
			/*
			 * If there is a list argument passed as a JSON string, decode and pass 
			 */
			if(Utils.isWebSafe(listArg)){
				List<String> arg = JSONUtils.decodeListString(listArg);
				if(arg != null){
					args.add(arg);
				}
			}

			if(Utils.isWebSafe(listArg2)){
				List<String> arg = JSONUtils.decodeListString(listArg2);
				if(arg != null){
					args.add(arg);
				}
			}
			
			execute = (List<ZeppaUserInfo>) query.executeWithArray(args);

			cursor = JDOCursorHelper.getCursor(execute);
			if (cursor == null) {
				cursorString = null;
			} else {
				cursorString = cursor.toWebSafeString();
			}
			// Tight loop for fetching all entities from datastore and
			// accomodate
			// for lazy fetch.
			for (ZeppaUserInfo obj : execute) {
				obj.getKey();
				obj.getKey().getParent();
				obj.getKey().getParent().getId();
				obj.getId();
				obj.getGivenName();
				obj.getFamilyName();
				obj.getGoogleAccountEmail();
				obj.getImageUrl();
				obj.getPrimaryUnformattedNumber();

			}

		} finally {
			mgr.close();
		}

		return CollectionResponse.<ZeppaUserInfo> builder().setItems(execute)
				.setNextPageToken(cursorString).build();
	}
	

	/**
	 * This methods returns a ZeppaUserInfo instance for the requestedUserId.
	 * ZeppaUser objects will only be held by their user. Info of connections
	 * will be held
	 * 
	 * @param requestedUserId
	 * @param user
	 * @return
	 * @throws OAuthRequestException
	 */

	@ApiMethod(name = "fetchZeppaUserInfoByParentId")
	public ZeppaUserInfo fetchZeppaUserInfoByParentId(
			@Named("requestedParentId") Long parentId,
			@Named("idToken") String tokenString) throws UnauthorizedException {

		// Fetch Authorized Zeppa User
		ZeppaUser user = ClientEndpointUtility
				.getAuthorizedZeppaUser(tokenString);
		if (user == null) {
			throw new UnauthorizedException(
					"No matching user found for this token");
		}

		PersistenceManager mgr = getPersistenceManager();
		ZeppaUserInfo result = null;

		try {
			ZeppaUser userResult = mgr.getObjectById(ZeppaUser.class,
					parentId);
			result = userResult.getUserInfo();
			// Touch all the fields so they are properly returned
			result.getCreated();
			result.getFamilyName();
			result.getGivenName();
			result.getGoogleAccountEmail();
			result.getPrimaryUnformattedNumber();
			result.getUpdated();
			result.getImageUrl();

		} catch (javax.jdo.JDOObjectNotFoundException ex) {
			result = null;
		} finally {
			mgr.close();
		}

		return result;
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
	@ApiMethod(name = "getZeppaUserInfo")
	public ZeppaUserInfo getZeppaUserInfo(@Named("id") Long id,
			@Named("idToken") String tokenString) throws UnauthorizedException {

		// Fetch Authorized Zeppa User
		ZeppaUser user = ClientEndpointUtility
				.getAuthorizedZeppaUser(tokenString);
		if (user == null) {
			throw new UnauthorizedException(
					"No matching user found for this token");
		}

		PersistenceManager mgr = getPersistenceManager();
		ZeppaUserInfo zeppauserinfo = null;
		try {
			zeppauserinfo = mgr.getObjectById(ZeppaUserInfo.class, id);
		} finally {
			mgr.close();
		}
		return zeppauserinfo;
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
