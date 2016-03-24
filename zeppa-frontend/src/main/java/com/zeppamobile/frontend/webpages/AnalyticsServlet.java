package com.zeppamobile.frontend.webpages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.zeppamobile.common.UniversalConstants;
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
			// Strings to hold the info for chart.js
			String allEventGender = getGenderCountAllEvents(sessionInfo);
			req.setAttribute("genderData", allEventGender);

			// TODO: Discuss tag follow design
			/*String allEventTags = getTagsAllEvents(sessionInfo);
			req.setAttribute("tagData", allEventTags);*/
			
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
	public static String getGenderCountAllEvents(UserInfoCerealWrapper sessionInfo) {
		Long maleCount = 0L;
		Long femaleCount = 0L;
		Long unidentified = 0L;
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
				JSONObject userInfo = (JSONObject) parser.parse(responseGender);
				maleCount = (Long) userInfo.get("maleCount");
				femaleCount = (Long) userInfo.get("femaleCount");
				unidentified = (Long) userInfo.get("unidentified");
				System.out.println("-------MALE: " + maleCount + "------");
				System.out.println("-------FEMALE: " + femaleCount + "------");
				System.out.println("-------UNID: " + unidentified + "------");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		
		// Create the string for chart.js and return it
		String data = "[" + "{" + "    value: " + String.valueOf(maleCount) + "," + "    color:\"#F7464A\","
				+ "    highlight: \"#FF5A5E\"," + "    label: \"Male\"" + "}," + "{" + "    value: "
				+ String.valueOf(femaleCount) + "," + "    color: \"#46BFBD\"," + "    highlight: \"#5AD3D1\","
				+ "    label: \"Female\"" + "}," + "{" + "    value: " + String.valueOf(unidentified) + ","
				+ "    color: \"#FDB45C\"," + "    highlight: \"#FFC870\"," + "   label: \"Unidentified\"" + "}" + "]";
		
		return data;
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
				System.out.println("-------TYPE: "+tags.entrySet().toArray().getClass()+"------");
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		return "";
	}

}
