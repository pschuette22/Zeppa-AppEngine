package com.zeppamobile.api.endpoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

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
	@SuppressWarnings("unchecked")
	@ApiMethod(name = "listZeppaUserInfo")
	public CollectionResponse<ZeppaUserInfo> listZeppaUserInfo(
			@Nullable @Named("filter") String filterString,
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("ordering") String orderingString,
			@Nullable @Named("limit") Integer limit,
			@Nullable @Named("jsonArgs") String jsonString,
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
		List<ZeppaUser> execute = null;
		List<ZeppaUserInfo> result = new ArrayList<ZeppaUserInfo>();
		
		List<String> args = new ArrayList<String>();
		
		try {
			mgr = getPersistenceManager();
			Query query = mgr.newQuery(ZeppaUser.class);
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
			if(Utils.isWebSafe(jsonString)){
				JSONArray jsonArray = (JSONArray) JSONValue.parse(jsonString);
				for(int i = 0; i < jsonArray.size(); i++){
					args.add((String)jsonArray.get(i));
				}
				// Declare list import and list param
				query.declareImports("import java.util.List;");
				query.declareParameters("List listParam");
			}
			
			
			execute = (List<ZeppaUser>) query.execute(args);

			cursor = JDOCursorHelper.getCursor(execute);
			if (cursor == null) {
				cursorString = null;
			} else {
				cursorString = cursor.toWebSafeString();
			}
			// Tight loop for fetching all entities from datastore and
			// accomodate
			// for lazy fetch.
			for (ZeppaUser u : execute) {
				ZeppaUserInfo obj = u.getUserInfo();
				obj.getKey();
				obj.getKey().getParent();
				obj.getKey().getParent().getId();
				obj.getId();
				obj.getGivenName();
				obj.getFamilyName();
				obj.getImageUrl();
				// Add touched object to result
				result.add(obj);
			}

		} finally {
			mgr.close();
		}

		return CollectionResponse.<ZeppaUserInfo> builder().setItems(result)
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

	@ApiMethod(name = "fetchZeppaUserInfoByParentId",path="fetchZeppaUserInfoByParentId")
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
			result.getKey();
			result.getId();
			result.getCreated();
			result.getFamilyName();
			result.getGivenName();
			result.getUpdated();
			result.getImageUrl();

		} catch (javax.jdo.JDOObjectNotFoundException ex) {
			result = null;
		} finally {
			mgr.close();
		}

		return result;
	}

//	/**
//	 * This method gets the entity having primary key id. It uses HTTP GET
//	 * method.
//	 * 
//	 * @param id
//	 *            the primary key of the java bean.
//	 * @return The entity with primary key id.
//	 * @throws OAuthRequestException
//	 */
//	@ApiMethod(name = "getZeppaUserInfo",path="getZeppaUserInfo")
//	public ZeppaUserInfo getZeppaUserInfo(Key key,
//			@Named("idToken") String tokenString) throws UnauthorizedException {
//
//		// Fetch Authorized Zeppa User
//		ZeppaUser user = ClientEndpointUtility
//				.getAuthorizedZeppaUser(tokenString);
//		if (user == null) {
//			throw new UnauthorizedException(
//					"No matching user found for this token");
//		}
//
//		PersistenceManager mgr = getPersistenceManager();
//		ZeppaUserInfo zeppauserinfo = null;
//		try {
//			zeppauserinfo = mgr.getObjectById(ZeppaUserInfo.class, key);
//		} finally {
//			mgr.close();
//		}
//		return zeppauserinfo;
//	}
	
	/**
	 * Get the persistence manager
	 * 
	 * @return
	 */
	public static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}

	
	
}
