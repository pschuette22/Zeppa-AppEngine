package com.zeppamobile.common.auth;

import java.io.Serializable;

/**
 * 
 * Auth wrapper is meant to be a wrapper class for transporting authentication
 * info For now, no encryption is made but this will change very soon
 * 
 * @author Pete Schuette
 *
 */
public class Authorizer implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Access token
	private String accessToken;

	// Google Account Email
	private String email;

	// Client Identifier to see where request came from
	private String clientId;

	// Zeppa Database Id of calling user
	private Long userId;

	// Token of the device calling with this
	private String deviceToken;
	
	
	public Authorizer(String accessToken, String email, String clientId,
			Long userId, String deviceToken) {
		super();
		this.accessToken = accessToken;
		this.email = email;
		this.clientId = clientId;
		this.userId = userId;
		this.deviceToken = deviceToken;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getDeviceToken() {
		return deviceToken;
	}

	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	

}
