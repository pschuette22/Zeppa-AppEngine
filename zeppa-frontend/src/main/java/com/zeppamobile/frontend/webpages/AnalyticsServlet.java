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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
		// TODO Auto-generated method stub

		HttpSession session = req.getSession(true);
		Object obj = session.getAttribute("UserInfo");
		if(obj != null)
		{
			UserInfoCerealWrapper sessionInfo = (UserInfoCerealWrapper)obj;
			
		// Variables to hold the gender demographic counts
		int maleCount = 0;
		int femaleCount = 0;
		resp.setContentType("text/html");

		Map<String, String> params = new HashMap<String, String>();
		// TODO: REPLACE HARD CODED VENDOR ID
		params.put(UniversalConstants.PARAM_VENDOR_ID, URLEncoder.encode(String.valueOf(sessionInfo.getVendorID()), "UTF-8"));

		URL url = ModuleUtils.getZeppaModuleUrl("zeppa-api", "/endpoint/event-relationship-servlet/", params);

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(false);
		connection.setRequestMethod("GET");

		BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String line;
		if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
			// Read from the buffer line by line and write to the response
			String responseString = "";
			while ((line = reader.readLine()) != null) {
				responseString += line;
			}
			JSONParser parser = new JSONParser();
			JSONArray resultsArray;
			try {
				resultsArray = (JSONArray) parser.parse(responseString);
				// For each user found, get their gender info
				for (int i = 0; i < resultsArray.size(); i++) {
					JSONObject user = (JSONObject) resultsArray.get(i);
					String id = (String) String.valueOf(user.get("userId"));
					params.put(UniversalConstants.PARAM_USER_ID, id);
					// Call the zeppa user servlet with the userId param
					URL urlUser = ModuleUtils.getZeppaModuleUrl("zeppa-api", "/endpoint/user-servlet/", params);

					HttpURLConnection connectionUser = (HttpURLConnection) urlUser.openConnection();
					connectionUser.setDoOutput(false);
					connectionUser.setRequestMethod("GET");

					reader = new BufferedReader(new InputStreamReader(connectionUser.getInputStream()));
					if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
						// Read from the buffer line by line and write to the
						// response
						String responseUser = "";
						while ((line = reader.readLine()) != null) {
							responseUser += line;
						}
						// Parse the JSON, get the gender and increment the count
						JSONObject userInfo = (JSONObject) parser.parse(responseUser);
						String gender = (String) userInfo.get("gender");
						if (gender != null && gender.equalsIgnoreCase(("MALE")))
							maleCount++;
						else if (gender != null && gender.equalsIgnoreCase("FEMALE"))
							femaleCount++;
					}

				}

				System.out.println("-------MALE:" + maleCount);
				System.out.println("-------FEMALE:" + femaleCount);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			req.setAttribute("allEventsData", responseString);
		} else {
			// Server returned HTTP error code.
			resp.getWriter().println("Connection Response Error: " + connection.getResponseMessage());

			// Read from the buffer line by line and write to the response
			// item
			while ((line = reader.readLine()) != null) {
				resp.getWriter().println(line);
			}
		}

		String data = "[" + "{" + "    value: 300," + "    color:\"#F7464A\"," + "    highlight: \"#FF5A5E\","
				+ "    label: \"Over 21\"" + "}," + "{" + "    value: 50," + "    color: \"#46BFBD\","
				+ "    highlight: \"#5AD3D1\"," + "    label: \"Under 18\"" + "}," + "{" + "    value: 100,"
				+ "    color: \"#FDB45C\"," + "    highlight: \"#FFC870\"," + "   label: \"18-21\"" + "}" + "]";

		req.setAttribute("data", data);

		req.getRequestDispatcher("WEB-INF/pages/analytics.jsp").forward(req, resp);
		
		} else {
			req.getRequestDispatcher("WEB-INF/pages/login.jsp").forward(req, resp);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub

	}

}
