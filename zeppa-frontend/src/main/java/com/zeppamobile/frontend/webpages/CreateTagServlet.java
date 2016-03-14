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

import com.zeppamobile.common.UniversalConstants;
import com.zeppamobile.common.utils.ModuleUtils;
import com.zeppamobile.common.utils.Utils;

/**
 * 
 * @author Kieran Lynn
 * 
 *         Event Servlet 
 *
 */
public class CreateTagServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6074841711114263838L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		/*
		 * Get the vendor and employee info from parameters
		 */
	    String tagText = request.getParameter(UniversalConstants.PARAM_TAG_TEXT);
	    String vendorId = request.getParameter(UniversalConstants.PARAM_VENDOR_ID);//???
	    vendorId = "-1";
	   	
	    //TO DO tags still
		
		if (Utils.isWebSafe(tagText)) {

			/*
			 * Parameters accepted, making call to api servlet
			 */
			Map<String, String> params = new HashMap<String, String>();
			params.put(UniversalConstants.PARAM_TAG_TEXT, URLEncoder.encode(tagText, "UTF-8"));
			params.put(UniversalConstants.PARAM_VENDOR_ID, URLEncoder.encode(vendorId, "UTF-8"));
			//Do tags
			
			
			/*
			 * Read from the request
			 */
			try {

				URL url = ModuleUtils.getZeppaModuleUrl("zeppa-api",
						"/endpoint/event-tag-servlet/", params);
				
	            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	            connection.setDoOutput(false);
	            connection.setRequestMethod("POST");

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(connection.getInputStream()));
				String line;
	    
	            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
	                //Print the tag info back to the page so it can be added.
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
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {}
	

}
