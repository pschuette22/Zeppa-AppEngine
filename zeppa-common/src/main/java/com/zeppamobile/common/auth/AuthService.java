package com.zeppamobile.common.auth;

/**
 * AuthService will hold an auth wrapper and determine what the user can and
 * cannot access
 * 
 * @author Pete Schuette
 *
 */
public class AuthService {

	private AuthInfo wrapper;

	public AuthService(AuthInfo wrapper) {
		this.wrapper = wrapper;
	}

}
