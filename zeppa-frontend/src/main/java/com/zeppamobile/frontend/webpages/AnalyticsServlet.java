package com.zeppamobile.frontend.webpages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
import com.zeppamobile.common.cerealwrapper.AnalyticsDataWrapper;
import com.zeppamobile.common.cerealwrapper.UserInfoCerealWrapper;
import com.zeppamobile.common.utils.ModuleUtils;

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
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html");
		HttpSession session = req.getSession(true);
		Object obj = session.getAttribute("UserInfo");
		if (obj != null) {
			UserInfoCerealWrapper sessionInfo = (UserInfoCerealWrapper) obj;
			
			System.out.println("Demographics");
			// Strings to hold the info for chart.js
			String[] allEventDemo = getGenderCountAllEvents(sessionInfo);
			req.setAttribute("genderData", allEventDemo[0]);
			req.setAttribute("ageData", allEventDemo[1]);

			System.out.println("Tags");
			String allEventTags = getTagsAllEvents(sessionInfo);
			req.setAttribute("tagData", allEventTags);
			
			System.out.println("Popular Events");
			String popularEvents = getPopularEventsAllEvents(sessionInfo);
			req.setAttribute("popEvents", popularEvents);
			
			System.out.println("Popular Days");
			String popularDays = getPopularDaysAllEvents(sessionInfo);
			req.setAttribute("popDays", popularDays);
			
			req.getRequestDispatcher("WEB-INF/pages/analytics.jsp").forward(req, resp);
		} else {
			req.getRequestDispatcher("WEB-INF/pages/login.jsp").forward(req, resp);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub

	}

	/** 
	 * Call the api analytics servlet to get all
	 * gender information for the current vendor's
	 * events
	 * @param resultsArray
	 * @return
	 */
	public static String[] getGenderCountAllEvents(UserInfoCerealWrapper sessionInfo) {
		Long maleCount = 0L;
		Long femaleCount = 0L;
		Long unidentified = 0L;
		Long under18 = 0L;
		Long age18to20 = 0L;
		Long age21to24 = 0L;
		Long age25to29 = 0L;
		Long age30to39 = 0L;
		Long age40to49 = 0L;
		Long age50to59 = 0L;
		Long over60 = 0L;
		try {
			// Set up the call to the analytics api servlet
			Map<String, String> params = new HashMap<String, String>();
			params.put(UniversalConstants.PARAM_VENDOR_ID,
					URLEncoder.encode(String.valueOf(sessionInfo.getVendorID()), "UTF-8"));
			params.put(UniversalConstants.ANALYTICS_TYPE, UniversalConstants.OVERALL_EVENT_DEMOGRAPHICS);
			URL url = ModuleUtils.getZeppaModuleUrl("zeppa-api", "/endpoint/analytics-servlet/", params);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(false);
			connection.setRequestMethod("GET");

			// Read the response from the call to the api servlet
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				// Read from the buffer line by line and write to the response string
				String responseGender = "";
				while ((line = reader.readLine()) != null) {
					responseGender += line;
				}
				JSONParser parser = new JSONParser();
				// Parse the JSON in the response, get the count of each gender
				System.out.println("------RESPONSE: " + responseGender + "------");
				JSONArray demoInfo = (JSONArray) parser.parse(responseGender);
				if(demoInfo.size() == 2) {
					// Get all of the demographic info from the json response
					JSONObject genderInfo = (JSONObject) demoInfo.get(0);
					maleCount = (Long) genderInfo.get("maleCount");
					femaleCount = (Long) genderInfo.get("femaleCount");
					unidentified = (Long) genderInfo.get("unidentified");
					JSONObject ageInfo = (JSONObject) demoInfo.get(1);
					under18 = (Long) ageInfo.get("under18");
					age18to20 = (Long) ageInfo.get("18to20");
					age21to24 = (Long) ageInfo.get("21to24");
					age25to29 = (Long) ageInfo.get("25to29");
					age30to39 = (Long) ageInfo.get("30to39");
					age40to49 = (Long) ageInfo.get("40to49");
					age50to59 = (Long) ageInfo.get("50to59");
					over60 = (Long) ageInfo.get("over60");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new String[] {"", ""};
		}
		
		// Create the string for chart.js for the gender pie chart
		String genderData = "[" + "{" + "    value: " + String.valueOf(maleCount) + "," + "    color:\"#F7464A\","
				+ "    highlight: \"#FF5A5E\"," + "    label: \"Male\"" + "}," + "{" + "    value: "
				+ String.valueOf(femaleCount) + "," + "    color: \"#46BFBD\"," + "    highlight: \"#5AD3D1\","
				+ "    label: \"Female\"" + "}," + "{" + "    value: " + String.valueOf(unidentified) + ","
				+ "    color: \"#FDB45C\"," + "    highlight: \"#FFC870\"," + "   label: \"Unidentified\"" + "}" + "]";
		
		String ageData = "{labels: [\"under18\", \"18to20\", \"21to24\", \"25to29\", \"30to39\", \"40to49\", \"50to59\", \"over60\"],"
				+ "    datasets: [ {"
				+ "label: \"Age dataset\","
				+ "fillColor: \"rgba(220,220,220,0.5)\","
				+ "strokeColor: \"rgba(220,220,220,0.8)\","
				+ "highlightFill: \"rgba(220,220,220,0.75)\","
				+ "highlightStroke: \"rgba(220,220,220,1)\","
				+ "data: ["+under18+", "+age18to20+", "+age21to24+", "+age25to29+", "+age30to39+", "+age40to49+", "+age50to59+", "+over60+"]}]}";
		
		return new String[] {genderData, ageData};
	}

	/**
	 * Call the api analytics servlet to get
	 * all of the tag information
	 * @param sessionInfo
	 * @return
	 */
	public static String getTagsAllEvents(UserInfoCerealWrapper sessionInfo) {
		try {
			// Set up the call to the analytics api servlet
			Map<String, String> params = new HashMap<String, String>();
			params.put(UniversalConstants.PARAM_VENDOR_ID,
					URLEncoder.encode(String.valueOf(sessionInfo.getVendorID()), "UTF-8"));
			params.put(UniversalConstants.ANALYTICS_TYPE, UniversalConstants.OVERALL_EVENT_TAGS);
			URL url = ModuleUtils.getZeppaModuleUrl("zeppa-api", "/endpoint/analytics-servlet/", params);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(false);
			connection.setRequestMethod("GET");

			// Read the response from the call to the api servlet
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				// Read from the buffer line by line and write to the response string
				String responseTags = "";
				while ((line = reader.readLine()) != null) {
					responseTags += line;
				}
				JSONParser parser = new JSONParser();
				JSONObject tags = (JSONObject) parser.parse(responseTags);
				for(Iterator iterator = tags.keySet().iterator(); iterator.hasNext();) {
				    String key = (String) iterator.next();
				    System.out.println("-------" + key + ": "+ tags.get(key));
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		return "";
	}
	
	/**
	 * Call the api analytics servlet to get
	 * the 5 most popular events for the given 
	 * @param sessionInfo
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String getPopularEventsAllEvents(UserInfoCerealWrapper sessionInfo) {
		try {
			// Set up the call to the analytics api servlet
			Map<String, String> params = new HashMap<String, String>();
			params.put(UniversalConstants.PARAM_VENDOR_ID,
					URLEncoder.encode(String.valueOf(sessionInfo.getVendorID()), "UTF-8"));
			params.put(UniversalConstants.ANALYTICS_TYPE, UniversalConstants.OVERALL_EVENT_POPULAR_EVENTS);
			URL url = ModuleUtils.getZeppaModuleUrl("zeppa-api", "/endpoint/analytics-servlet/", params);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(false);
			connection.setRequestMethod("GET");

			// Read the response from the call to the api servlet
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				// Read from the buffer line by line and write to the response string
				String response = "";
				while ((line = reader.readLine()) != null) {
					response += line;
				}
				JSONParser parser = new JSONParser();
				JSONObject events = (JSONObject) parser.parse(response);
				for(Iterator iterator = events.keySet().iterator(); iterator.hasNext();) {
				    String key = (String) iterator.next();
				    System.out.println("-------" + key + ": "+ events.get(key));
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		return "";
	}

	/**
	 * Call the api analytics servlet to get
	 * the 5 most popular events for the given 
	 * @param sessionInfo
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String getPopularDaysAllEvents(UserInfoCerealWrapper sessionInfo) {
		Map<String, Integer> dayData = new HashMap<String, Integer>();
		
		try {
			// Set up the call to the analytics api servlet
			Map<String, String> params = new HashMap<String, String>();
			params.put(UniversalConstants.PARAM_VENDOR_ID,
					URLEncoder.encode(String.valueOf(sessionInfo.getVendorID()), "UTF-8"));
			params.put(UniversalConstants.ANALYTICS_TYPE, UniversalConstants.OVERALL_EVENT_POPULAR_DAYS);
			URL url = ModuleUtils.getZeppaModuleUrl("zeppa-api", "/endpoint/analytics-servlet/", params);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(false);
			connection.setRequestMethod("GET");

			// Read the response from the call to the api servlet
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				// Read from the buffer line by line and write to the response string
				String response = "";
				while ((line = reader.readLine()) != null) {
					response += line;
				}
				JSONParser parser = new JSONParser();
				JSONObject events = (JSONObject) parser.parse(response);
				for(Iterator iterator = events.keySet().iterator(); iterator.hasNext();) {
				    String key = (String) iterator.next();
				    dayData.put(key, ((int) (long) events.get(key)));
				    System.out.println("-------DAYS" + key + ": "+ events.get(key));
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		
		String ret = "{labels: [\"Monday\", \"Tuesday\", \"Wednesday\", \"Thursday\", \"Friday\", \"Saturday\", \"Sunday\" ], "
			+ "datasets: [ {"
			+ "label: \"Day-of-Week dataset\","
			+ "fillColor: \"rgba(220,220,220,0.5)\","
			+ "strokeColor: \"rgba(220,220,220,0.8)\","
			+ "highlightFill: \"rgba(220,220,220,0.75)\","
			+ "highlightStroke: \"rgba(220,220,220,1)\","
			+ "data: [" + dayData.get("Monday")+", "+dayData.get("Tuesday")+", "+dayData.get("Wednesday")+", "+dayData.get("Thursday")+", "
			+ dayData.get("Friday")+", "+dayData.get("Saturday")+", "+dayData.get("Sunday")
			+ "]}]}";
		System.out.println("----DAYS: " + ret);
		return ret;
	}
}
