package com.zeppamobile.frontend.webpages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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

	private static String startDateFilter = "";
	private static String endDateFilter = "";
	private static String minAgeFilter = "";
	private static String maxAgeFilter = "";
	private static String genderFilter = "";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html");
		startDateFilter = req.getParameter("startDate");
		endDateFilter = req.getParameter("endDate");
		minAgeFilter = req.getParameter("minAge");
		maxAgeFilter = req.getParameter("maxAge");
		genderFilter = req.getParameter("gender");
//		System.out.println("----start: "+startDateFilter);
//		System.out.println("----end: "+endDateFilter);
		
		HttpSession session = req.getSession(true);
		Object obj = session.getAttribute("UserInfo");
		if (obj != null) {
			UserInfoCerealWrapper sessionInfo = (UserInfoCerealWrapper) obj;
			
			// Strings to hold the info for chart.js
			String[] allEventDemo = getDemographicCountAllEvents(sessionInfo);
			req.setAttribute("genderData", allEventDemo[0]);
			req.setAttribute("ageData", allEventDemo[1]);

			String allEventTags = getTagsAllEvents(sessionInfo, true);
			req.setAttribute("tagData", allEventTags);
			
			String allEventTagsWatched = getTagsAllEvents(sessionInfo, false);
			req.setAttribute("watchedTagData", allEventTagsWatched);
			
			String popularEvents = getPopularEventsAllEvents(sessionInfo);
			req.setAttribute("popEvents", popularEvents);
			
			String popularDays = getPopularDaysAllEvents(sessionInfo);
			req.setAttribute("popDays", popularDays);
			
			req.getRequestDispatcher("WEB-INF/pages/analytics.jsp").forward(req, resp);
		} else {
			req.getRequestDispatcher("WEB-INF/pages/login.jsp").forward(req, resp);
		}
	}

	/** 
	 * Call the api analytics servlet to get all
	 * gender information for the current vendor's
	 * events
	 * @param resultsArray
	 * @return
	 */
	public static String[] getDemographicCountAllEvents(UserInfoCerealWrapper sessionInfo) {
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
			params = createFilterParams(params);
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
		String genderData = "\"none\"";
		if(maleCount > 0 || femaleCount > 0 || unidentified > 0) {
			genderData = "[" + "{" + "    value: " + String.valueOf(maleCount) + "," + "    color:\"#F7464A\","
				+ "    highlight: \"#FF5A5E\"," + "    label: \"Male\"" + "}," + "{" + "    value: "
				+ String.valueOf(femaleCount) + "," + "    color: \"#46BFBD\"," + "    highlight: \"#5AD3D1\","
				+ "    label: \"Female\"" + "}," + "{" + "    value: " + String.valueOf(unidentified) + ","
				+ "    color: \"#FDB45C\"," + "    highlight: \"#FFC870\"," + "   label: \"Unidentified\"" + "}" + "]";
		}
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
	 * @param joined - true if you want to get only joined relationships, false if you want watched relationships  
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String getTagsAllEvents(UserInfoCerealWrapper sessionInfo, boolean joined) {
		List<AnalyticsDataWrapper> tagCounts = new ArrayList<AnalyticsDataWrapper>();
		try {
			// Set up the call to the analytics api servlet
			Map<String, String> params = new HashMap<String, String>();
			params.put(UniversalConstants.PARAM_VENDOR_ID,
					URLEncoder.encode(String.valueOf(sessionInfo.getVendorID()), "UTF-8"));
			if(joined) {
				params.put(UniversalConstants.ANALYTICS_TYPE, UniversalConstants.OVERALL_EVENT_TAGS);
			} else {
				params.put(UniversalConstants.ANALYTICS_TYPE, UniversalConstants.OVERALL_EVENT_TAGS_WATCHED);
			}
			params = createFilterParams(params);
			
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
				    tagCounts.add(new AnalyticsDataWrapper(key, ((int)(long)tags.get(key))));
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		
		String label = "labels: [";
		String data = "data: [";
		for(int i=0; i < tagCounts.size(); i++) {
			AnalyticsDataWrapper adw = tagCounts.get(i);
			// If it's not the last element then add comma at the end otherwise don't
			if (i < (tagCounts.size() - 1)) {
				label = label.concat("\"" + adw.getKey() + "\",");
				data = data.concat(String.valueOf(adw.getValue()) + ",");
			} else {
				label = label.concat("\"" + adw.getKey() + "\"");
				data = data.concat(String.valueOf(adw.getValue()));
			}
		}
		String ret = "{" + label+"],"
				+ "datasets: [ {"
				+ "label: \"PopEvents dataset\","
				+ "fillColor: \"rgba(220,220,220,0.5)\","
				+ "strokeColor: \"rgba(220,220,220,0.8)\","
				+ "highlightFill: \"rgba(220,220,220,0.75)\","
				+ "highlightStroke: \"rgba(220,220,220,1)\","
				+ data
				+ "]}]}";
		return ret;
	}
	
	/**
	 * Call the api analytics servlet to get
	 * the 5 most popular events for the given 
	 * @param sessionInfo
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String getPopularEventsAllEvents(UserInfoCerealWrapper sessionInfo) {
		List<AnalyticsDataWrapper> eventCounts = new ArrayList<AnalyticsDataWrapper>();
		
		try {
			// Set up the call to the analytics api servlet
			Map<String, String> params = new HashMap<String, String>();
			params.put(UniversalConstants.PARAM_VENDOR_ID,
					URLEncoder.encode(String.valueOf(sessionInfo.getVendorID()), "UTF-8"));
			params.put(UniversalConstants.ANALYTICS_TYPE, UniversalConstants.OVERALL_EVENT_POPULAR_EVENTS);
			params = createFilterParams(params);
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
				    eventCounts.add(new AnalyticsDataWrapper(key, ((int)(long)events.get(key))));
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		
		String label = "labels: [";
		String data = "data: [";
		for(int i=0; i < eventCounts.size(); i++) {
			AnalyticsDataWrapper adw = eventCounts.get(i);
			// If it's not the last element then add comma at the end otherwise don't
			if (i < (eventCounts.size() - 1)) {
				label = label.concat("\"" + adw.getKey() + "\",");
				data = data.concat(String.valueOf(adw.getValue()) + ",");
			} else {
				label = label.concat("\"" + adw.getKey()+"\"");
				data = data.concat(String.valueOf(adw.getValue()));
			}
		}
		
		String ret = "{" + label+"],"
				+ "datasets: [ {"
				+ "label: \"PopEvents dataset\","
				+ "fillColor: \"rgba(220,220,220,0.5)\","
				+ "strokeColor: \"rgba(220,220,220,0.8)\","
				+ "highlightFill: \"rgba(220,220,220,0.75)\","
				+ "highlightStroke: \"rgba(220,220,220,1)\","
				+ data
				+ "]}]}";
		return ret;
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
			params = createFilterParams(params);
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
		
		return ret;
	}
	
	private static Map<String, String> createFilterParams(Map<String, String> map) throws UnsupportedEncodingException {
		Map<String, String> params = map;
		if(startDateFilter != null)
			params.put(UniversalConstants.START_DATE_FILTER, URLEncoder.encode(startDateFilter, "UTF-8"));
		if(endDateFilter != null)
			params.put(UniversalConstants.END_DATE_FILTER, URLEncoder.encode(endDateFilter, "UTF-8"));
		if(minAgeFilter != null)
			params.put(UniversalConstants.MIN_AGE_FILTER, URLEncoder.encode(minAgeFilter, "UTF-8"));
		if(maxAgeFilter != null)
			params.put(UniversalConstants.MAX_AGE_FILTER, URLEncoder.encode(maxAgeFilter, "UTF-8"));
		if(genderFilter != null)
			params.put(UniversalConstants.GENDER_FILTER, URLEncoder.encode(genderFilter, "UTF-8"));
		
		return params;
	}
}
