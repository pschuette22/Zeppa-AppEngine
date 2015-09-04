package com.zeppamobile.common.auth;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Auth wrapper is meant to be a wrapper class for transporting authentication info
 * For now, no encryption is made but this will change very soon
 * 
 * @author Pete Schuette
 *
 */
public class AuthInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Client Identifier to see where request came from
	private String clientId;
	
	// Authentication Scopes
	private String[] scopes;
	
	// Token Secret to make sure authentication has or will take place
	private String clientSecret;
	
	// Email of calling user
	private String authEmail;
	
	// UserId of calling user
	private Long userId;
	
	// Token of the device calling with this 
	private String deviceToken;

	// Map of additional flags to be passed
	private Map<String, String> flags;
		
	
	/**
	 * Create a new instance of an auth wrapper for making calls to Zeppa Apis
	 * @param clientId
	 * @param scopes
	 * @param clientSecret
	 * @param authEmail
	 * @param userId
	 */
	public AuthInfo(String clientId, String[] scopes, String clientSecret,
			String authEmail, Long userId) {
		super();
		this.clientId = clientId;
		this.scopes = scopes;
		this.clientSecret = clientSecret;
		this.authEmail = authEmail;
		this.userId = userId;
		this.flags = new HashMap<String, String>();
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String[] getScopes() {
		return scopes;
	}

	public void setScopes(String[] scopes) {
		this.scopes = scopes;
	}

	public String getClientSecret() {
		return clientSecret;
	}

	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}

	public String getAuthEmail() {
		return authEmail;
	}

	public void setAuthEmail(String authEmail) {
		this.authEmail = authEmail;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	
	
	
	
	
}
