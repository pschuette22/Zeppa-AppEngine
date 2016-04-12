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
import com.zeppamobile.common.utils.Utils;

/**
 * 
 * @author Kevin Moratelli
 * 
 *         Servlet for the dashboard page
 *
 */
public class DashboardServlet extends HttpServlet {

	private static final long serialVersionUID = -6074841711114263838L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/html");
		
    	String error = req.getParameter("error");
		
		HttpSession session = req.getSession(true);
		Object obj = session.getAttribute("UserInfo");
		if(obj != null && error == null) {
			UserInfoCerealWrapper userInfo = (UserInfoCerealWrapper)obj;
			// Make sure the user is logged in
			if(userInfo.getEmployeeID() > 0) {
				// Get the chart js string for demographic info
				String ageData = AnalyticsServlet.getDemographicCountAllEvents(userInfo)[1];
				// Get the chart js string for the tags graph
				String tagsData = AnalyticsServlet.getTagsAllEvents(userInfo, true);
				
				JSONArray upcomingEvents = getUpcomingEvents(userInfo.getVendorID());
				JSONArray pastEvents = getPastEvents(userInfo.getVendorID());
				
				req.setAttribute("ageData", ageData);
				req.setAttribute("tagData", tagsData);
				req.setAttribute("upcomingEvents", upcomingEvents.toJSONString());
				req.setAttribute("pastEvents", pastEvents.toJSONString());
				req.getRequestDispatcher("WEB-INF/pages/home.jsp").forward(req, resp);
			}
			else {
				req.getRequestDispatcher("WEB-INF/pages/login.jsp").forward(req, resp);
			}
		}
		else {
			req.setAttribute("errorDivText", "There was a problem when enabling PrivaKey, please try again. Error:" + error);
			
			req.getRequestDispatcher("WEB-INF/pages/login.jsp").forward(req, resp);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
	    String id_token = req.getParameter("id_token");		
	    resp.getWriter().println("Account Settings id_token: " + id_token);
	    
		HttpSession session = req.getSession(false);
		String nonce = (String) session.getAttribute("PrivaKeyNonce");
		Long employeeID = null;
		Object obj = session.getAttribute("UserInfo");
		if(obj != null) {
			UserInfoCerealWrapper userInfo = (UserInfoCerealWrapper)obj;
			employeeID = userInfo.getEmployeeID();
		}
		resp.getWriter().append("Account Settings Nonce: " + nonce);
		
		if (Utils.isWebSafe(id_token) && employeeID > 0) {
	
			/*
			 * Parameters accepted, making call to api servlet
			 */
			Map<String, String> params = new HashMap<String, String>();
			params.put("id_token", id_token);
			params.put("nonce", nonce);
			params.put("employeeID", employeeID.toString());
			
			/*
			 * Read from the request
			 */
			try {
				URL url = ModuleUtils.getZeppaModuleUrl("zeppa-api",
						"privakey/", params);
	
				resp.getWriter().println("Account Settings URL: " + url);
	            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	            connection.setDoOutput(false);
	            connection.setRequestMethod("GET");
	            connection.setReadTimeout(10000); //10 Sec
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(connection.getInputStream()));
				String line;
				
				resp.getWriter().println("Connection Response Message: " + connection.getResponseMessage());
	            
				String s = ""; 
				while ((line = reader.readLine()) != null) {
					s += line;
				}
				
				resp.getWriter().println("Response: " + s);
				
				if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
	            	resp.getWriter().println("Connection Response Created: " + connection.getResponseMessage());
	            	resp.sendRedirect("/dashboard");
										
	            }
				else if(connection.getResponseCode() == HttpURLConnection.HTTP_BAD_REQUEST)
				{
					resp.sendRedirect("/login?privakeySuccess=false&error=BadRequest");
				}
				else if(connection.getResponseCode() == HttpURLConnection.HTTP_FORBIDDEN)
				{
					resp.sendRedirect("/login?privakeySuccess=false&error=Forbidden");
				}
				else if(connection.getResponseCode() == HttpURLConnection.HTTP_CONFLICT)
				{
					resp.sendRedirect("/login?privakeySuccess=false&error=Conflict");
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
		
		resp.sendRedirect("/login?privakeySuccess=false");
	}
	
	/**
	 * Call the VendorEventServlet in the api module to get the next 5 upcoming
	 * events and return them in a JSON array to be parsed in the jsp
	 * 
	 * @param vendorId - the id of the current vendor
	 * @return - JSON array containing info on next 5 events
	 */
	private JSONArray getUpcomingEvents(Long vendorId) {
		Map<String, String> params = new HashMap<String, String>();
		//List<VendorEventWrapper> upcomingEvents = new ArrayList<VendorEventWrapper>();
		JSONArray results = new JSONArray();
		try {
			params.put(UniversalConstants.PARAM_VENDOR_ID, URLEncoder.encode(vendorId.toString(), "UTF-8"));
			params.put(UniversalConstants.PARAM_UPCOMING_EVENTS, URLEncoder.encode(UniversalConstants.PARAM_UPCOMING_EVENTS, "UTF-8"));
			// Create the connection to the api module VendorEventServlet
			URL url = ModuleUtils.getZeppaModuleUrl("zeppa-api", "/endpoint/dashboard-servlet/", params);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(false);
			connection.setRequestMethod("GET");

			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;

			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				// Read from the buffer line by line and write to the response item
				String responseString = "";
				while ((line = reader.readLine()) != null) {
					responseString += line;
				}
				// Abstract the upcoming event info from the json response
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject)parser.parse(responseString);
				results = (JSONArray)obj.get("events");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return results;
	}
	
	/**
	 * Call the VendorEventServlet in the api module to get the 5 most recent past
	 * events and return them in a JSON array to be parsed in the jsp
	 * @param vendorId - the id of the current vendor
	 * @return - JSON array containing info on next 5 events
	 */
	private JSONArray getPastEvents(Long vendorId) {
		Map<String, String> params = new HashMap<String, String>();
		JSONArray results = new JSONArray();
		try {
			params.put(UniversalConstants.PARAM_VENDOR_ID, URLEncoder.encode(vendorId.toString(), "UTF-8"));
			params.put(UniversalConstants.PARAM_PAST_EVENTS, URLEncoder.encode(UniversalConstants.PARAM_PAST_EVENTS, "UTF-8"));
			// Create the connection to the api module VendorEventServlet
			URL url = ModuleUtils.getZeppaModuleUrl("zeppa-api", "/endpoint/dashboard-servlet/", params);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(false);
			connection.setRequestMethod("GET");

			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				// Read from the buffer line by line and write to the response item
				String responseString = "";
				while ((line = reader.readLine()) != null) {
					responseString += line;
				}
				// Abstract the past event info from the json response
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject)parser.parse(responseString);
				results = (JSONArray)obj.get("events");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return results;
	}
}
