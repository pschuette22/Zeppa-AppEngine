package com.zeppamobile.api.exception;

import com.google.api.server.spi.response.UnauthorizedException;
import com.zeppamobile.common.auth.Authorizer;

/**
 * This is called when a someone is being a dickhead
 * Thrown if an endpoint call is intercepted and tampered with
 * 
 * @author Pete Schuette
 *
 */
public class DickheadException extends UnauthorizedException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public DickheadException(String message, String objectType, long objectId, Authorizer auth) {
		super(message);
		// TODO: Flag it
		// TODO: Auto-send notification to 
	}


	@Override
	public String getLocalizedMessage() {
		super.getLocalizedMessage();
		
		return "Don't be a dickhead. You've been flagged";
	}



	/**
	 * 
	 */
	@Override
	public int getStatusCode() {
		// TODO Auto-generated method stub
		super.getStatusCode();
		int status = 515;
		return status;
	}

}
