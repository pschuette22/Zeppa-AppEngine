package com.zeppamobile.api.endpoint.servlet;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.server.spi.response.UnauthorizedException;
import com.zeppamobile.api.AppConfig;
import com.zeppamobile.api.Constants;
import com.zeppamobile.api.PMF;
import com.zeppamobile.api.datamodel.Employee;
import com.zeppamobile.api.endpoint.utils.ApiCerealWrapperFactory;
import com.zeppamobile.api.endpoint.utils.AuthChecker;
import com.zeppamobile.common.UniversalConstants;
import com.zeppamobile.common.cerealwrapper.UserInfoCerealWrapper;
import com.zeppamobile.common.utils.Utils;

/**
 * Servlet implementation class AuthenticationServlet
 */
public class AuthenticationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AuthenticationServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	try {

		// Fetch the current ID token
		String token = request.getParameter("token");
		
		Employee employee = getAuthorizedEmployee(token);
		if(employee != null)
		{
			ApiCerealWrapperFactory fact = new ApiCerealWrapperFactory();
			UserInfoCerealWrapper wrapper = (UserInfoCerealWrapper)fact.makeCereal(employee.getUserInfo());
			String userInfoString = fact.toCereal(wrapper);
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(userInfoString);
		}
		else
		{
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}


		
		
	} catch (UnauthorizedException e) {
		// user is not authorized to make this call
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		e.printStackTrace(response.getWriter());
	} 
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	/**
	 * <p>
	 * Fetch the user for this id token
	 * </p>
	 * This method calls checkToken and getAuthorizedZeppaUser sequentially
	 * 
	 * @param idToken
	 *            passed by client
	 * @return ZeppaUser for this passed token or null
	 * @throws UnauthorizedException
	 *             if token is not valid
	 */
	public static Employee getAuthorizedEmployee(String tokenString)
			throws UnauthorizedException {
		// Get payload for token
		GoogleIdToken.Payload payload = checkToken(tokenString);
		// Return user based on payload
		return getAuthorizedEmployeeForPayload(payload);
	}

	/**
	 * Get ZeppaUser based on GoogleIdToken payload
	 * 
	 * @param payload
	 * @return
	 */
	public static Employee getAuthorizedEmployeeForPayload(
			GoogleIdToken.Payload payload) throws UnauthorizedException {
		// Verify it is valid
		if (payload == null || !Utils.isWebSafe(payload.getEmail())) {
			throw new UnauthorizedException("Invalid id-token");
		}

		// This will be the result of the query or null
		Employee result = null;

		// Execute the query and clean up where necessary
		PersistenceManager mgr = getPersistenceManager();

		try {

			Query q = mgr.newQuery(Employee.class,
					"emailAddress == '" + payload.getEmail() + "'");
			q.setUnique(true);

			result = (Employee) q.execute();

		} finally {
			mgr.close();
		}
		return result;
	}

	/**
	 * Validate a token used to access the backend
	 * 
	 * @param tokenString
	 *            id token sent from client
	 * @return Payload for this token or null if invalid
	 */
	public static GoogleIdToken.Payload checkToken(String tokenString)
			throws UnauthorizedException {

		// TODO: validate auth token, client id, etc.
		AuthChecker checker = new AuthChecker(
				UniversalConstants.APP_CLIENT_IDS, Constants.WEB_CLIENT_ID);

		try {
			GoogleIdToken.Payload payload = checker.check(tokenString);

			if (checker.isValid() || AppConfig.isTest()) {

				return payload;
			} else {

				throw new UnauthorizedException("Invalid Auth With Problem: "
						+ checker.problem());
			}
		} catch (GeneralSecurityException e) {
			// TODO: flag the security error
			throw new UnauthorizedException("Security Error: "
					+ e.getLocalizedMessage());
		}

	}
	
	/**
	 * Get the persistence manager
	 * 
	 * @return
	 */
	private static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}
}
