package com.zeppamobile.api.endpoint;

import javax.jdo.PersistenceManager;

import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiReference;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.zeppamobile.api.endpoint.utils.ClientEndpointUtility;
import com.zeppamobile.common.auth.Authorizer;
import com.zeppamobile.common.datamodel.ZeppaFeedback;
import com.zeppamobile.common.datamodel.ZeppaUser;

@ApiReference(AppInfoEndpoint.class)
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
			@Named("auth") Authorizer auth) throws UnauthorizedException {

		if (zeppafeedback.getUserId() == null) {
			throw new NullPointerException("Null User Id");
		}

		ZeppaUser user = ClientEndpointUtility.getAuthorizedZeppaUser(auth);

		if (user.getId().longValue() != zeppafeedback.getUserId().longValue()) {
			throw new UnauthorizedException(
					"Cant insert feedback for someone else");
		}

		zeppafeedback.setCreated(System.currentTimeMillis());
		zeppafeedback.setUpdated(System.currentTimeMillis());

		PersistenceManager mgr = ClientEndpointUtility.getPersistenceManager();
		try {

			// Store feedback
			mgr.makePersistent(zeppafeedback);

		} finally {

			mgr.close();
		}
		return zeppafeedback;
	}

}
