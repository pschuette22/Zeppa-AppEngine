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
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.zeppamobile.common.UniversalConstants;
import com.zeppamobile.common.cerealwrapper.UserInfoCerealWrapper;
import com.zeppamobile.common.utils.ModuleUtils;

/**
 * 
 * @author Kieran Lynn
 * 
 *         Event Servlet 
 *
 */
public class EventsServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6074841711114263838L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		HttpSession session = request.getSession(true);
		Object obj = session.getAttribute("UserInfo");
		if(obj != null)
		{
			UserInfoCerealWrapper userInfo = (UserInfoCerealWrapper)obj;
			
			//response.getWriter().println("User Info Name: " + userInfo.getGivenName() + " " + userInfo.getFamilyName());
			//response.getWriter().println("User Info Employee ID: " + userInfo.getEmployeeID());
			//response.getWriter().println("User Info Vendor ID: " + userInfo.getVendorID());
			
			Long vendorId = userInfo.getVendorID();
			Map<String, String> params = new HashMap<String, String>();
			params.put(UniversalConstants.PARAM_VENDOR_ID, URLEncoder.encode(vendorId.toString(), "UTF-8"));
			
			try {
	
				URL url = ModuleUtils.getZeppaModuleUrl("zeppa-api",
						"/endpoint/vendor-event-servlet/", params);
				
	            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	            connection.setDoOutput(false);
	            connection.setRequestMethod("GET");
	
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(connection.getInputStream()));
				String line;
				
	            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
					// Read from the buffer line by line and write to the response
					// item					
	            	
	            	String responseString = "";
					while ((line = reader.readLine()) != null) {
						responseString+=line;
					}
	//				JSONParser parser = new JSONParser();
	//				JSONArray resultsArray = (JSONArray)parser.parse(responseString);
	//				String html = "";
	//				for(int i=0;i<resultsArray.size();i++){
	//					JSONObject event = (JSONObject) resultsArray.get(i);
	//					String title = (String) event.get("title");
	//				}
					request.setAttribute("allEvents", responseString);
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
			
			request.getRequestDispatcher("WEB-INF/pages/events.jsp").forward(request, response);
		}	
		else
		{
			request.getRequestDispatcher("WEB-INF/pages/login.jsp").forward(request, response);
		}
	

	}
}
