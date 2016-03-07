package com.zeppamobile.api.endpoint.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import com.google.api.server.spi.response.UnauthorizedException;
import com.zeppamobile.api.datamodel.Vendor;

/**
 * 
 * @author Pete Schuette
 * 
 * Servlet for interacting with employee objects
 *
 */
public class EmployeeServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		//try {

			// Fetch the current ID token
			boolean isLogin = Boolean.parseBoolean(req.getParameter("isLogin"));
			
			if(isLogin)
			{
				String email = req.getParameter("email");
				String password = req.getParameter("password");
				
				if(!email.isEmpty() && !password.isEmpty())
					resp.setStatus(HttpServletResponse.SC_OK);
				else
					resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
			else
			{
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}


			
			
		//} catch (UnauthorizedException e) {
		//	// user is not authorized to make this call
		//	resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		//	e.printStackTrace(resp.getWriter());
		//} 
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
	
	

}
