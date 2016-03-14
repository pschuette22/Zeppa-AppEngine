package com.zeppamobile.frontend.webpages;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author Pete Schuette
 * 
 *         Blank servlet for testing
 *
 */
public class AnalyticsServlet extends HttpServlet {

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
		
		String data = "["+
		            "{"+
		            "    value: 300,"+
		            "    color:\"#F7464A\","+
		            "    highlight: \"#FF5A5E\","+
		            "    label: \"Red\""+
		            "},"+
		            "{"+
		            "    value: 50,"+
		            "    color: \"#46BFBD\","+
		            "    highlight: \"#5AD3D1\","+
		            "    label: \"Green\""+
		            "},"+
		            "{"+
		            "    value: 100,"+
		            "    color: \"#FDB45C\","+
		            "    highlight: \"#FFC870\","+
		            "   label: \"Yellow\""+
		            "}"+
		        "]";
		
		req.setAttribute("data", data);
		
		req.getRequestDispatcher("WEB-INF/pages/analytics.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub

	}
	

}
