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

import com.zeppamobile.common.UniversalConstants;
import com.zeppamobile.common.utils.ModuleUtils;
import com.zeppamobile.common.utils.Utils;

/**
 * 
 * @author Brendan
 * 
 *         Creat Account Servlet 
 *
 */
public class CreateAccountServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6074841711114263838L;


    

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html");
		request.setAttribute("attribute1", "This is attribute 1");
		
		request.getRequestDispatcher("WEB-INF/pages/create-account.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		
		/*
		 * Get the vendor and employee info from parameters
		 */
	    String firstName = request.getParameter("firstName");
	    String lastName = request.getParameter("lastName");
	    String emailAddress = request.getParameter("emailAddress");
	    String companyName = request.getParameter("companyName");
	    String addressLine1 = request.getParameter("addressLine1");
	   	String addressLine2 = request.getParameter("addressLine2");
	   	String city = request.getParameter("city");
	   	String state = request.getParameter("state");
	   	String zipcode = request.getParameter("zipcode");
	   	String password = request.getParameter("password");
		
		if (Utils.isWebSafe(firstName)) {

			/*
			 * Parameters accepted, making call to api servlet
			 */
			Map<String, String> params = new HashMap<String, String>();
			params.put("firstName", firstName);
			params.put("lastName", lastName);
			params.put("emailAddress", emailAddress);
			params.put("companyName", companyName);
			params.put("addressLine1", addressLine1);
			params.put("addressLine2", addressLine2);
			params.put("city", city);
			params.put("state", state);
			params.put("zipcode", zipcode);
			params.put("password", password);
			
			/*
			 * Read from the request
			 */
			try {

				URL url = ModuleUtils.getZeppaModuleUrl("zeppa-api",
						"/endpoint/vendor-servlet/", params);
				
		        String message = URLEncoder.encode("my message", "UTF-8");

	            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	            connection.setDoOutput(false);
	            connection.setRequestMethod("POST");

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(connection.getInputStream()));
				String line;
				
//	            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
//	            writer.write("message=" + message);
//	            writer.close();
	    
	            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
	                // OK
	            	response.getWriter().println("Connection Response OK: " + connection.getResponseMessage());

					// Read from the buffer line by line and write to the response
					// item					
					while ((line = reader.readLine()) != null) {
						response.getWriter().println(line);
					}

					
	            } else {
	                // Server returned HTTP error code.
	            	response.getWriter().println("Connection Response Error: " + connection.getResponseMessage());
	            	
					// Read from the buffer line by line and write to the response
					// item					
					while ((line = reader.readLine()) != null) {
						response.getWriter().println(line);
					}
	            }
	            
	            reader.close();
	        } catch (MalformedURLException e) {
				e.printStackTrace(response.getWriter());
	        } catch (IOException e) {
				e.printStackTrace(response.getWriter());
			} catch (Exception e) {
				e.printStackTrace(response.getWriter());
				
			}

		} else {
			/*
			 * If bad parameters were passed
			 */
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}
	

}
