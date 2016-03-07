package com.zeppamobile.frontend.accounts;

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
 * Servlet implementation class CreateAccountServlet
 */
public class CreateAccountServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		System.out.println("doPost to create account servlet");
		/*
		 * Get the vendor and employee info from parameters
		 */
		String name = request.getParameter("eventName");
		// Fetch the current ID token
		String access_token = (String)request.getSession().getAttribute(UniversalConstants.PARAM_ID_TOKEN);
		
		if (Utils.isWebSafe(name)) {

			/*
			 * Parameters accepted, making call to api servlet
			 */
			Map<String, String> params = new HashMap<String, String>();
			params.put(UniversalConstants.PARAM_EVENT_NAME, name);
			params.put(UniversalConstants.PARAM_ID_TOKEN, access_token);
			/*
			 * Read from the request
			 */
			try {

				URL url = ModuleUtils.getZeppaModuleUrl("zeppa-api",
						"/admin/event-servlet/", params);
				
		        String encodedUrl = URLEncoder.encode(url.toString(), "UTF-8");
		        System.out.println("Encoded URL: " + encodedUrl);
	            HttpURLConnection connection = (HttpURLConnection) (new URL(encodedUrl)).openConnection();
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
	            	response.getWriter().println("Connection Response: " + connection.getResponseMessage());

					// Read from the buffer line by line and write to the response
					// item					
					while ((line = reader.readLine()) != null) {
						response.getWriter().println(line);
					}

					
	            } else {
	                // Server returned HTTP error code.
	            	response.getWriter().println("Connection Response: " + connection.getResponseMessage());
	            	
					// Read from the buffer line by line and write to the response
					// item					
					while ((line = reader.readLine()) != null) {
						response.getWriter().println(line);
					}
	            }
	            
	            reader.close();
	        } catch (MalformedURLException e) {
	            // ...
				response.getWriter().println("Event Name: " + name);
				response.getWriter().println("ID Token: " + access_token);
				e.printStackTrace(response.getWriter());
	        } catch (IOException e) {
	            // ...
				response.getWriter().println("Event Name: " + name);
				response.getWriter().println("ID Token: " + access_token);
				e.printStackTrace(response.getWriter());
			} catch (Exception e) {
				response.getWriter().println("Event Name: " + name);
				response.getWriter().println("ID Token: " + access_token);
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
