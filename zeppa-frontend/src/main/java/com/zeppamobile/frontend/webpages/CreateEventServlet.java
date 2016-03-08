package com.zeppamobile.frontend.webpages;

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

import com.zeppamobile.common.utils.ModuleUtils;
import com.zeppamobile.common.utils.Utils;

/**
 * 
 * @author Kieran Lynn
 * 
 *         Event Servlet 
 *
 */
public class CreateEventServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6074841711114263838L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Get users tags and send them back.
		
		request.getRequestDispatcher("WEB-INF/pages/create-event.jsp").forward(request, response);
		

	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		
		/*
		 * Get the vendor and employee info from parameters
		 */
	    String title = request.getParameter("title");
	    String description = request.getParameter("description");
	    String start = request.getParameter("start");
	    String end = request.getParameter("end");
	    String address = request.getParameter("address");
	    String vendorId = request.getParameter("vendorID");//???
	    vendorId = "-1";
	   	
	    //TO DO tags still
		
		if (Utils.isWebSafe(title)) {

			/*
			 * Parameters accepted, making call to api servlet
			 */
			Map<String, String> params = new HashMap<String, String>();
			params.put("title", URLEncoder.encode(title, "UTF-8"));
			params.put("description", URLEncoder.encode(description, "UTF-8"));
			params.put("start", URLEncoder.encode(start, "UTF-8"));
			params.put("end", URLEncoder.encode(end, "UTF-8"));
			params.put("address", URLEncoder.encode(address, "UTF-8"));
			params.put("vendorId", URLEncoder.encode(vendorId, "UTF-8"));
			//Do tags
			
			
			/*
			 * Read from the request
			 */
			try {

				URL url = ModuleUtils.getZeppaModuleUrl("zeppa-api",
						"/endpoint/vendor-event-servlet/", params);
				
	            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	            connection.setDoOutput(false);
	            connection.setRequestMethod("POST");

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(connection.getInputStream()));
				String line;
	    
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
