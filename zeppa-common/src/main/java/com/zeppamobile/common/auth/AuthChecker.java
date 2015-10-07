package com.zeppamobile.common.auth;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.util.Utils;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;

/**
 * Taken from StackOverflow and slightly modified
 * http://stackoverflow.com/questions/16590854/how-to-verify-android-id-token-on-app-engine-backend
 * 
 * @author Pete Schuette
 *
 */
public class AuthChecker {

	@SuppressWarnings("rawtypes")
	private final List mClientIDs;
	private final String mAudience;
	private final GoogleIdTokenVerifier mVerifier;
	private final JsonFactory mJFactory;
	private String mProblem = "Verification failed. (Time-out?)";

	public AuthChecker(String[] clientIDs, String audience) {
	    this.mClientIDs = Arrays.asList(clientIDs);
	    this.mAudience = audience;
	    HttpTransport transport = Utils.getDefaultTransport();
	    this.mJFactory = Utils.getDefaultJsonFactory();
	    this.mVerifier = new GoogleIdTokenVerifier(transport, mJFactory);
	}

	/**
	 * Validate this id token and return payload with user information
	 * @param tokenString
	 * @return
	 */
	public GoogleIdToken.Payload check(String tokenString) {
	    GoogleIdToken.Payload payload = null;
	    try {
	        GoogleIdToken token = GoogleIdToken.parse(mJFactory, tokenString);
	        if (mVerifier.verify(token)) {
	            GoogleIdToken.Payload tempPayload = token.getPayload();
	            if (!tempPayload.getAudience().equals(mAudience))
	                mProblem = "Audience mismatch";
	            else if (!mClientIDs.contains(tempPayload.getIssuer()))
	                mProblem = "Client ID mismatch";
	            else
	                payload = tempPayload;
	        }
	    } catch (GeneralSecurityException e) {
	        mProblem = "Security issue: " + e.getLocalizedMessage();
	    } catch (IOException e) {
	        mProblem = "Network problem: " + e.getLocalizedMessage();
	    }
	    return payload;
	}

	public String problem() {
	    return mProblem;
	}
	
}
