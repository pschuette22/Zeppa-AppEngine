package com.zeppamobile.frontend.account;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.zeppamobile.common.cerealwrapper.CerealWrapperFactory;
import com.zeppamobile.common.cerealwrapper.UserInfoCerealWrapper;
import com.zeppamobile.common.utils.ModuleUtils;
import com.zeppamobile.common.utils.Utils;

/**
 * 
 * @author Kieran Lynn
 * 
 *         Blank servlet for testing
 *
 */
public class LoginServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6074841711114263838L;


	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html");
		
		request.getRequestDispatcher("WEB-INF/pages/login.jsp").forward(request, response);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

	    String token = req.getParameter("token");
		
		if (Utils.isWebSafe(token)) {

			/*
			 * Parameters accepted, making call to api servlet
			 */
			Map<String, String> params = new HashMap<String, String>();
			params.put("token", token);
			
			/*
			 * Read from the request
			 */
			try {

				URL url = ModuleUtils.getZeppaModuleUrl("zeppa-api",
						"/endpoint/authentication-servlet/", params);

	            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	            connection.setDoOutput(false);
	            connection.setRequestMethod("GET");

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(connection.getInputStream()));
				String line;
				
	    
	            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
	                // OK
	            	//resp.getWriter().println("Connection Response OK: " + connection.getResponseMessage());

					// Read from the buffer line by line and write to the response
					// item	
	            	String s = ""; 
					while ((line = reader.readLine()) != null) {
						s += line;
					}
					
					//resp.getWriter().println(s);
					
					CerealWrapperFactory fact = new CerealWrapperFactory();
					UserInfoCerealWrapper userInfo = (UserInfoCerealWrapper)fact.getFromCereal(s);
					
					
					HttpSession session = req.getSession(true);
					session.setAttribute("UserInfo", userInfo);
					
					//resp.getWriter().println("User Info Object: " + userInfo.toString());
					//resp.getWriter().println("User Info Name: " + userInfo.getGivenName() + " " + userInfo.getFamilyName());
					resp.setStatus(HttpURLConnection.HTTP_OK);
					
					//resp.addHeader("isAuthorized", "true");
					//resp.sendRedirect("/dashboard");
					
	            }
	            else if(connection.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED)
	            {
	            	resp.getWriter().println("Employee Not Found: " + connection.getResponseMessage());
	            	
	            	resp.setStatus(HttpURLConnection.HTTP_UNAUTHORIZED);
	            	
	            	//resp.addHeader("isAuthorized", "false");
	            }
	            else {
	                // Server returned HTTP error code.
	            	resp.getWriter().println("Connection Response Error: " + connection.getResponseMessage());
	            	
	            	resp.addHeader("isAuthorized", "false");
	            }
	            
	            reader.close();
	        } catch (MalformedURLException e) {
				e.printStackTrace(resp.getWriter());
	        } catch (IOException e) {
				e.printStackTrace(resp.getWriter());
			} catch (Exception e) {
				e.printStackTrace(resp.getWriter());
				
			}

		} else {
			/*
			 * If bad parameters were passed
			 */
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	
	

}
