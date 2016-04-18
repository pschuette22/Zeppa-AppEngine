package com.zeppamobile.api.endpoint.utils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.server.spi.response.UnauthorizedException;
import com.zeppamobile.api.AppConfig;
import com.zeppamobile.common.utils.TestUtils;
import com.zeppamobile.common.utils.Utils;

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
	 * @throws GeneralSecurityException 
	 */
	public GoogleIdToken.Payload check(String tokenString)
			throws UnauthorizedException, GeneralSecurityException {

		// If this is a unit test, return a test payload
		if (AppConfig.isTest()) {
			return checkTestAuthToken(tokenString);
		}

		/*
		 * This just parses a token and returns the payload. It is not covered
		 * because I don't do runtime coverage or know the proper way to test
		 * GoogleIdTokenPayloads
		 */
		GoogleIdToken.Payload payload = null;
		try {
			GoogleIdToken token = GoogleIdToken.parse(mJFactory, tokenString);
			// If token could not be parsed
			if (token == null || token.getPayload() == null) {
				return null;
			}

			// Verify this token contains the given audience
//			if (!token.getPayload().getAudienceAsList()
//					.contains(this.mAudience)) {
//				throw new GeneralSecurityException("Audience mismatch");
//			}

//			// Verify this token contains the given audience
//			if (!mClientIDs.contains(token.getPayload().getIssuer())) {
//				throw new GeneralSecurityException("Invalid issuer");
//			}

			// Check email is verified
			if (token.getPayload().getEmailVerified()) {
				GoogleIdToken.Payload tempPayload = token.getPayload();

				isValid = true;
				payload = tempPayload;

			} else {
				throw new UnauthorizedException("Email is not verified");
			}

		} catch (IOException e) {
			mProblem = "IOException: " + e.getLocalizedMessage();
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
	 * Return a Google Id Token payload for the purpose of testing
	 * 
	 * @param authToken
	 *            - "token" ;)
	 * @return payload for this test email
	 */
	private GoogleIdToken.Payload checkTestAuthToken(String tokenString) {
		GoogleIdToken.Payload payload = null;

		if (Utils.isWebSafe(tokenString)
				&& tokenString.startsWith(TestUtils.getTestTokenPrefix())) {
			// parse the auth email out of the "token"
			String authEmail = tokenString.substring(TestUtils
					.getTestTokenPrefixLength());

			payload = new GoogleIdToken.Payload();
			payload.setEmail(authEmail);
			payload.setEmailVerified(true);

			// Expire in an hour (like other token payloads)
			payload.setExpirationTimeSeconds(System.currentTimeMillis()
					+ (60 * 60 * 1000));
		}

		return payload;
	}

}
