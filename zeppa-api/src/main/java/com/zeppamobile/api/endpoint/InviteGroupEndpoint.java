package com.zeppamobile.api.endpoint;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

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
			throw new UnauthorizedException("unauthorized call");
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

	private static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}
	
}
