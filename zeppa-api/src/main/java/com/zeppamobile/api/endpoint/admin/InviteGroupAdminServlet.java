package com.zeppamobile.api.endpoint.admin;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.server.spi.response.UnauthorizedException;
import com.zeppamobile.api.endpoint.utils.ClientEndpointUtility;
import com.zeppamobile.common.UniversalConstants;
import com.zeppamobile.common.utils.Utils;

/**
 * 
 * @author Pete Schuette
 * 
 * Servlet that is mapped for adding invite groups
 *
 */
public class InviteGroupAdminServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * List of emails authorized to insert an invite group
	 */
	private static final String[] AUTHORIZED_EMAILS = {"pschuette22@gmail.com", "pschuette@zeppamobile.com"};
	
	
	/**
	 * Insert an Invite Group
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		/*
		 * Get the necessary parameters 
		 */
		String idToken = req.getParameter(UniversalConstants.PARAM_ID_TOKEN);
		String emailList = req.getParameter(UniversalConstants.PARAM_EMAIL_LIST);
		String tagList = req.getParameter(UniversalConstants.PARAM_TAG_LIST);
		
		/*
		 * Make sure the parameters are good
		 */
		if(!Utils.isWebSafe(idToken) || !Utils.isWebSafe(emailList) || !Utils.isWebSafe(tagList)){
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}
		
		try {
			/*
			 * Verify user is authorized to insert invite group
			 */
			GoogleIdToken.Payload payload = ClientEndpointUtility.checkToken(idToken);
			if(!isAuthorized(payload.getEmail())) {
				throw new UnauthorizedException("Unauthorized to insert invite group");
			}
			
			
			
			
			
		} catch (UnauthorizedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
		
	}

	
	/**
	 * Check to see if this user is allowed to insert an invite group
	 * 
	 * @param email - email of authorized user
	 * @return true if authorized
	 */
	private boolean isAuthorized(String email) {
		for(int i = 0;i<AUTHORIZED_EMAILS.length;i++){
			if(email.equalsIgnoreCase(AUTHORIZED_EMAILS[i])){
				return true;
			}
		}
		return false;
	}
	
	
}
