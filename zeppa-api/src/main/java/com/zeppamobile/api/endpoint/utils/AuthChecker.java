package com.zeppamobile.api.endpoint.utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

/**
 * Taken from StackOverflow and slightly modified
 * http://stackoverflow.com/questions
 * /16590854/how-to-verify-android-id-token-on-app-engine-backend
 * 
 * @author Pete Schuette
 *
 */
public class AuthChecker {

	private static final Logger LOG = Logger.getLogger(AuthChecker.class
			.getName());

	private final List<String> mClientIDs;
	private final String mAudience;
	private final GoogleIdTokenVerifier mVerifier;
	private final JsonFactory mJFactory;
	private String mProblem = "Verification failed. (Time-out?)";
	private boolean isValid = false;

	public AuthChecker(String[] clientIDs, String audience) {
		this.mClientIDs = Arrays.asList(clientIDs);
		this.mAudience = audience;
		HttpTransport transport = new NetHttpTransport();
		this.mJFactory = new JacksonFactory();
		this.mVerifier = new GoogleIdTokenVerifier.Builder(transport, mJFactory)
				.build();
	}

	/**
	 * Validate this id token and return payload with user information
	 * 
	 * @param tokenString
	 * @return
	 */
	public GoogleIdToken.Payload check(String tokenString) {
		GoogleIdToken.Payload payload = null;
		try {
			LOG.log(Level.WARNING, "Verifying Token");
			GoogleIdToken token = GoogleIdToken.parse(mJFactory, tokenString);
			// If token could not be parsed
			if (token == null || token.getPayload() == null) {
				LOG.log(Level.WARNING, "GIDToken null for for idToken: "
						+ tokenString);
				return null;
			}

			// Take a quick peek
			LOG.log(Level.WARNING, "Parsed Token: " + token.toString());

			if (token.getPayload().getEmailVerified()) {
				LOG.log(Level.WARNING, "Verified Token Successfully");
				GoogleIdToken.Payload tempPayload = token.getPayload();

				LOG.log(Level.WARNING, "Returning valid payload");
				isValid = true;
				payload = tempPayload;

			} else {
				// Verification Failed
				LOG.log(Level.WARNING, "GIDToken not verified for idToken: "
						+ tokenString);
			}
		} catch (IOException e) {
			mProblem = "Network problem: " + e.getLocalizedMessage();
		}
		return payload;
	}

	public String problem() {
		return mProblem;
	}

	public boolean isValid() {
		return isValid;
	}

	/**
	 * Manually verify that this token is legit
	 * 
	 * @param token
	 * @return true if this is a good token
	 */
	private boolean verifyIdToken(GoogleIdToken token) {
		boolean isLegit = false;

		if (token != null && token.getPayload() != null) {
			GoogleIdToken.Payload payload = token.getPayload();

		}

		return isLegit;
	}

}
