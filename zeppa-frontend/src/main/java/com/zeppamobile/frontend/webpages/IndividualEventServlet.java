package com.zeppamobile.frontend.webpages;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author Kieran Lynn
 * 
 *         Event Servlet 
 *
 */
public class IndividualEventServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6074841711114263838L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		resp.setContentType("text/html");
		req.setAttribute("attribute1", "This is attribute 1");
		
		req.getRequestDispatcher("WEB-INF/pages/events.jsp").forward(req, resp);
	}
	

}
