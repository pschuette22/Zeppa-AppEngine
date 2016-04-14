package com.zeppamobile.api.endpoint;

import javax.jdo.PersistenceManager;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.zeppamobile.api.Constants;
import com.zeppamobile.api.PMF;
import com.zeppamobile.api.datamodel.ZeppaFeedback;
import com.zeppamobile.api.datamodel.ZeppaUser;
import com.zeppamobile.api.endpoint.utils.ClientEndpointUtility;

@Api(name = Constants.API_NAME, version = "v1", scopes = { Constants.EMAIL_SCOPE }, audiences = { Constants.WEB_CLIENT_ID })
public class ZeppaFeedbackEndpoint {

	/**
	 * This inserts a new entity into App Engine datastore. If the entity
	 * already exists in the datastore, an exception is thrown. It uses HTTP
	 * POST method.
	 * 
	 * @return The inserted entity.
	 * @throws OAuthRequestException
	 */

	@ApiMethod(name = "insertZeppaFeedback")
	public ZeppaFeedback insertZeppaFeedback(ZeppaFeedback zeppafeedback,
			@Named("idToken") String tokenString) throws UnauthorizedException {

		// Fetch Authorized Zeppa User
		ZeppaUser user = ClientEndpointUtility
				.getAuthorizedZeppaUser(tokenString);
		if (user == null) {
			throw new UnauthorizedException(
					"No matching user found for this token");
		}

		zeppafeedback.setCreated(System.currentTimeMillis());
		zeppafeedback.setUpdated(System.currentTimeMillis());
		zeppafeedback.setUserId(user.getId().longValue());

		PersistenceManager mgr = getPersistenceManager();
		try {

			// Store feedback
			zeppafeedback = mgr.makePersistent(zeppafeedback);

		} finally {

			mgr.close();
		}
		return zeppafeedback;
	}

	
	private static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}
}
