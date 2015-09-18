package com.zeppamobile.common.auth;

/**
 * AuthService will hold an auth wrapper and determine what the user can and
 * cannot access
 * 
 * @author Pete Schuette
 *
 */
public class AuthService {

	private Authorizer auth;

	public AuthService(Authorizer auth) {
		this.auth = auth;
	}
	
	
	public boolean isValid(){
		boolean isValid = false;
		
		
		
		return isValid;
	}

}
