package com.zeppamobile.frontend.webpages;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * @author PSchuette
 * 
 *         Display the way tags are indexed in Zeppa. Tags are not inserted into
 *         the datastore
 *
 */
public class IndexTagExampleServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		System.out.println("Received get request");
		UserService userService = UserServiceFactory.getUserService();
		
		// Verify user is logged in.
		if(userService.getCurrentUser()!=null){
		
			System.out.println("Logged in user made request");
			
		} else {
			// If not logged in, redirect them to login page
			loginRedirect(userService, req, resp);
			return;
		}
		
		
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		UserService userService = UserServiceFactory.getUserService();
		// String callingUrl = req.getRequestURL().toString();
		// System.out.println("Calling url: " + callingUrl);

		// Make sure user is logged into Google Account
		if (userService.getCurrentUser() != null) {
			System.out.println("Logged in user made request");
			// If the user is logged in, show them the page
			// resp.sendRedirect("/index-tag-example.jsp");
			req.getRequestDispatcher("/index-tag-example").forward(req, resp);
		} else {
			// If not logged in, redirect them to login page
			loginRedirect(userService, req, resp);
			return;
		}
	}

	/**
	 * Convenience method for redirecting user to login page if they try to
	 * access this without being logged in
	 * 
	 * @param userService
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	private void loginRedirect(UserService userService, HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		System.out.println("Redirecting to login");
		String redirectUrl = userService.createLoginURL(req.getRequestURI());
		// Redirect the request to login page
		req.getRequestDispatcher(redirectUrl).forward(req, resp);
	}

}
