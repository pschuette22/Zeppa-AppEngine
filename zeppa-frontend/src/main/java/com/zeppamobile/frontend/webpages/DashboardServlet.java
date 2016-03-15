package com.zeppamobile.frontend.webpages;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.zeppamobile.common.cerealwrapper.UserInfoCerealWrapper;

/**
 * 
 * @author Pete Schuette
 * 
 *         Blank servlet for testing
 *
 */
public class DashboardServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6074841711114263838L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		resp.setContentType("text/html");
		
		HttpSession session = req.getSession(true);
		Object obj = session.getAttribute("UserInfo");
		if(obj != null)
		{
			UserInfoCerealWrapper userInfo = (UserInfoCerealWrapper)obj;
		
			//resp.getWriter().println("User Info Name: " + userInfo.getGivenName() + " " + userInfo.getFamilyName());
			
			if(userInfo.getCreated() > 0)
			{
				req.getRequestDispatcher("WEB-INF/pages/home.jsp").forward(req, resp);
			}
			else
			{
				req.getRequestDispatcher("WEB-INF/pages/login.jsp").forward(req, resp);
			}
		}
		else 
		{
			req.getRequestDispatcher("WEB-INF/pages/login.jsp").forward(req, resp);
		}
	}

	

}
