package com.zeppamobile.frontend.webpages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
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
import com.zeppamobile.common.utils.Utils;

/**
 * 
 * @author Kieran Lynn
 * 
 *         Event Servlet
 *
 */
public class IndividualEventServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6074841711114263838L;
	
	private static String minAgeFilter = "";
	private static String maxAgeFilter = "";
	private static String genderFilter = "";

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * Get the vendor and employee info from parameters
		 */
	    String title = req.getParameter("title");
	    String description = req.getParameter("description");
	    String start = req.getParameter("start");
	    String end = req.getParameter("end");
	    String address = req.getParameter("address");
	    String vendorId = req.getParameter(UniversalConstants.PARAM_VENDOR_ID);//???
	    String tagsList = req.getParameter(UniversalConstants.PARAM_TAG_LIST);
	    String eventId = req.getParameter(UniversalConstants.PARAM_EVENT_ID);
	    
		HttpSession session = req.getSession(true);
		Object obj = session.getAttribute("UserInfo");
		if(obj != null)
		{
			UserInfoCerealWrapper userInfo = (UserInfoCerealWrapper)obj;
			vendorId = userInfo.getVendorID().toString();
		}
		
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
			params.put(UniversalConstants.PARAM_TAG_LIST, URLEncoder.encode(tagsList, "UTF-8"));
			params.put(UniversalConstants.PARAM_EVENT_ID, URLEncoder.encode(eventId, "UTF-8"));
			
			/*
			 * Read from the request
			 */
			try {

				URL url = ModuleUtils.getZeppaModuleUrl("zeppa-api",
						"/endpoint/vendor-event-servlet/", params);
				
	            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	            connection.setDoOutput(false);
	            connection.setRequestMethod("PUT");

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(connection.getInputStream()));
				String line;
	    
	            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
	                // OK
	            	resp.getWriter().println("Connection Response OK: " + connection.getResponseMessage());
					// Read from the buffer line by line and write to the response
					// item					
					while ((line = reader.readLine()) != null) {
						resp.getWriter().write(line);
					}
	            } else {
	                // Server returned HTTP error code.
	            	resp.getWriter().println("Connection Response Error: " + connection.getResponseMessage());
	            	
					// Read from the buffer line by line and write to the response
					// item					
					while ((line = reader.readLine()) != null) {
						resp.getWriter().println(line);
					}
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
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html");
		HttpSession session = req.getSession(true);
		Object obj = session.getAttribute("UserInfo");
		if(obj != null)
		{
			if(req.getParameter(UniversalConstants.PARAM_EVENT_ID) != null) {
				String eventId = URLDecoder.decode(req.getParameter(UniversalConstants.PARAM_EVENT_ID), "UTF-8");
				minAgeFilter = req.getParameter("minAge");
				maxAgeFilter = req.getParameter("maxAge");
				genderFilter = req.getParameter("gender");
				if (eventId != null) {
					String eventInformation = getEventInformation(eventId);
					req.setAttribute("eventInfo", eventInformation);
					req.setAttribute("eventId", eventId);
					String tagResults = getAllEventTags(eventInformation);
					req.setAttribute("tags", tagResults);
					String[] demoData = getDemographicCountForEvent(Long.valueOf(eventId));
					req.setAttribute("genderData", demoData[0]);
					req.setAttribute("ageData", demoData[1]);
				}
			}
			req.getRequestDispatcher("WEB-INF/pages/individual-event.jsp").forward(req, resp);
		}else{
			req.getRequestDispatcher("WEB-INF/pages/login.jsp").forward(req, resp);
		}
	}
	
	/**
	 * Calls the vendor event servlet to get the basic information about the event. 
	 * @param eventId
	 * @return responseString
	 */
	private String getEventInformation(String eventId) {
		Map<String, String> params = new HashMap<String, String>();
		try {
			params.put(UniversalConstants.PARAM_EVENT_ID, URLEncoder.encode(eventId, "UTF-8"));
			//Get the event info
			URL url = ModuleUtils.getZeppaModuleUrl("zeppa-api", "/endpoint/vendor-event-servlet/", params);

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
				return responseString;
			}	
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "";
	}

	/**
	 * This gets the tagID from the eventInfo JSON and then calls getTag to get the text.
	 * It then formulates a JSON string and returns it.
	 * 
	 * @param eventInformationJSON
	 * @return
	 */
	private String getAllEventTags(String eventInformationJSON) {
		String tagJSON = "";
		try {
			JSONParser parser = new JSONParser();
			JSONArray array;
			
				array = (JSONArray) parser.parse(eventInformationJSON);
			
			JSONObject obj = (JSONObject) array.get(0);
			JSONArray tagIds = (JSONArray) obj.get("tagIds");
			String[] tagArr =new String[tagIds.size()];
			tagJSON = "[";
			for (int i=0;i<tagArr.length; i++){
				String tag = ((Long)tagIds.get(i)).toString();
				tagJSON += getTag(tag);
				tagJSON += ",";
			}
			tagJSON = tagJSON.substring(0, tagJSON.length()-1); //Remove the last comma
			tagJSON +="]";
			
			return tagJSON;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return tagJSON;
	}

	
	
	/** 
	 * Call the api analytics servlet to get all
	 * gender information for the current vendor's
	 * events
	 * @param resultsArray
	 * @return
	 */
	public static String[] getDemographicCountForEvent(Long eventId) {
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
			params.put(UniversalConstants.PARAM_EVENT_ID,
					URLEncoder.encode(String.valueOf(eventId), "UTF-8"));
			params.put(UniversalConstants.ANALYTICS_TYPE, UniversalConstants.INDIV_EVENT_DEMOGRAPHICS);
			params = createFilterParams(params);
			URL url = ModuleUtils.getZeppaModuleUrl("zeppa-api", "/endpoint/individual-analytics-servlet/", params);
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
	 * This tags a tagID and gets the tag information. Then it returns the JSON string that is returned
	 * @param tagId
	 * @return
	 */
	private String getTag(String tagId){
		try {
			
			Map<String, String> tagParams = new HashMap<String, String>();
			tagParams.put(UniversalConstants.PARAM_TAG_ID, URLEncoder.encode(tagId, "UTF-8"));
			URL url = ModuleUtils.getZeppaModuleUrl("zeppa-api",
					"/endpoint/event-tag-servlet/", tagParams);
			
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(false);
            connection.setRequestMethod("GET");

			BufferedReader reader = new BufferedReader(
					new InputStreamReader(connection.getInputStream()));
			String line;
			String responseString = "";
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				// Read from the buffer line by line and write to the response
				// item					
            	
				while ((line = reader.readLine()) != null) {
					responseString+=line;
				}
            }
            
            return responseString;
		} catch (MalformedURLException e) {
			return e.getMessage();
		} catch (IOException e) {
			return e.getMessage();
		} catch (Exception e) {
			return e.getMessage();
		}
	}
	
	private static Map<String, String> createFilterParams(Map<String, String> map) throws UnsupportedEncodingException {
		Map<String, String> params = map;
		if(minAgeFilter != null)
			params.put(UniversalConstants.MIN_AGE_FILTER, URLEncoder.encode(minAgeFilter, "UTF-8"));
		if(maxAgeFilter != null)
			params.put(UniversalConstants.MAX_AGE_FILTER, URLEncoder.encode(maxAgeFilter, "UTF-8"));
		if(genderFilter != null)
			params.put(UniversalConstants.GENDER_FILTER, URLEncoder.encode(genderFilter, "UTF-8"));
		
		return params;
	}

}
