package com.zeppamobile.frontend.webpages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
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
import com.zeppamobile.common.cerealwrapper.UserInfoCerealWrapper;
import com.zeppamobile.common.cerealwrapper.VendorEventWrapper;
import com.zeppamobile.common.utils.ModuleUtils;

/**
 * 
 * @author Pete Schuette
 * 
 *         Blank servlet for testing
 *
 */
public class DashboardServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6074841711114263838L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/html");
		
		HttpSession session = req.getSession(true);
		Object obj = session.getAttribute("UserInfo");
		if(obj != null) {
			UserInfoCerealWrapper userInfo = (UserInfoCerealWrapper)obj;
			// Make sure the user is logged in
			if(userInfo.getEmployeeID() > 0) {
				// Get the chart js string for demographic info
				String ageData = AnalyticsServlet.getDemographicCountAllEvents(userInfo)[1];
				// Get the chart js string for the tags graph
				String tagsData = AnalyticsServlet.getTagsAllEvents(userInfo, true);
				
				JSONArray upcomingEvents = getUpcomingtEvents(userInfo.getVendorID());
				
				req.setAttribute("ageData", ageData);
				req.setAttribute("tagData", tagsData);
				req.setAttribute("upcomingEvents", upcomingEvents.toJSONString());
				req.getRequestDispatcher("WEB-INF/pages/home.jsp").forward(req, resp);
			}
			else {
				req.getRequestDispatcher("WEB-INF/pages/login.jsp").forward(req, resp);
			}
		}
		else {
			req.getRequestDispatcher("WEB-INF/pages/login.jsp").forward(req, resp);
		}
	}

	private JSONArray getUpcomingtEvents(Long vendorId) {
		Map<String, String> params = new HashMap<String, String>();
		//List<VendorEventWrapper> upcomingEvents = new ArrayList<VendorEventWrapper>();
		JSONArray results = new JSONArray();
		try {
			params.put(UniversalConstants.PARAM_VENDOR_ID, URLEncoder.encode(vendorId.toString(), "UTF-8"));
			params.put(UniversalConstants.PARAM_UPCOMING_EVENTS, URLEncoder.encode(UniversalConstants.PARAM_UPCOMING_EVENTS, "UTF-8"));
			// Create the connection to the api module VendorEventServlet
			URL url = ModuleUtils.getZeppaModuleUrl("zeppa-api", "/endpoint/vendor-event-servlet/", params);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(false);
			connection.setRequestMethod("GET");

			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;

			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				// Read from the buffer line by line and write to the response
				// item
				String responseString = "";
				while ((line = reader.readLine()) != null) {
					responseString += line;
				}
				System.out.println("------RESP: "+ responseString);
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject)parser.parse(responseString);
				results = (JSONArray)obj.get("events");
//				System.out.println("------OBJ CURRENT: "+ obj.toJSONString());
//				System.out.println("------CURRENT: "+ results.toJSONString());
//				for(int i=0; i < results.size(); i++) {
//					JSONObject temp = (JSONObject) results.get(i);
//					VendorEventWrapper eventWrapper = new VendorEventWrapper((Long) temp.get("id"), null, null,
//							(Long) temp.get("hostId"), (String) temp.get("title"), (String) temp.get("description"),
//							(Long) temp.get("start"), (Long) temp.get("end"), null, (String)temp.get("displayLocation"), null);
//					upcomingEvents.add(eventWrapper);
//				}
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return results;
	}

}
