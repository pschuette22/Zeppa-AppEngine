package com.zeppamobile.frontend.webpages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.zeppamobile.common.UniversalConstants;
import com.zeppamobile.common.utils.ModuleUtils;

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

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String eventId = URLDecoder.decode(req.getParameter(UniversalConstants.PARAM_EVENT_ID), "UTF-8");
		Map<String, String> params = new HashMap<String, String>();
		params.put(UniversalConstants.PARAM_EVENT_ID, URLEncoder.encode(eventId, "UTF-8"));
		try {
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
				req.setAttribute("eventInfo", responseString);
				
				//Once you have the event info parse out the tag ids and get the tag information for those tags
				//This is super ugly code but deal with it cause the presentation is tomorrow. See the hackiest way to build a json array string ever below
				
				JSONParser parser = new JSONParser();
				JSONArray array = (JSONArray) parser.parse(responseString);
				JSONObject obj = (JSONObject) array.get(0);
				JSONArray tagIds = (JSONArray) obj.get("tagIds");
				String[] tagArr =new String[tagIds.size()];
				String tagJSON = "[";
				for (int i=0;i<tagArr.length; i++){
					String tag = ((Long)tagIds.get(i)).toString();
					tagJSON += getTag(tag);
					tagJSON += ",";
				}
				tagJSON = tagJSON.substring(0, tagJSON.length()-1); //Remove the last comma
				tagJSON +="]";
				
				req.setAttribute("tags", tagJSON);
				
				String genderData = getUsersJSON(eventId);
				req.setAttribute("genderData", genderData);
				
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

		req.getRequestDispatcher("WEB-INF/pages/individual-event.jsp").forward(req, resp);
	}
	
	private String getUsersJSON(String eventId){
		try {
			int maleCount = 0;
			int femaleCount = 0;
			int unidentified = 0;
			String genderData = "";
			Map<String, String> params = new HashMap<String, String>();
			params.put(UniversalConstants.PARAM_EVENT_ID, URLEncoder.encode(eventId, "UTF-8"));
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
				resultsArray = (JSONArray) parser.parse(responseString);
				// For each user found, get their gender info
				for (int i = 0; i < resultsArray.size(); i++) {
					JSONObject user = (JSONObject) resultsArray.get(i);
					String id = (String) String.valueOf(user.get("userId"));
					params.put(UniversalConstants.PARAM_USER_ID, id);
					System.out.println("Userr id:"+id);
					// Call the zeppa user servlet" with the userId param
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
						System.out.println("--------USER RESPONSE " + responseUser);
						// Parse the JSON, get the gender and increment the count
						JSONObject userInfo = (JSONObject) parser.parse(responseUser);
						String gender = (String) userInfo.get("gender");
						if (gender != null && gender.equalsIgnoreCase(("MALE"))) {
							maleCount++;
						} else if (gender != null && gender.equalsIgnoreCase("FEMALE")) {
							femaleCount++;
						}
					}
				}
				genderData = "[" + "{" + "    value: " +String.valueOf(maleCount)+ "," + "    color:\"#F7464A\"," + "    highlight: \"#FF5A5E\","
							+ "    label: \"Male\"" + "}," + "{" + "    value: " +String.valueOf(femaleCount)+ "," + "    color: \"#46BFBD\","
							+ "    highlight: \"#5AD3D1\"," + "    label: \"Female\"" + "}," + "{" + "    value: " +String.valueOf(unidentified)+ ","
							+ "    color: \"#FDB45C\"," + "    highlight: \"#FFC870\"," + "   label: \"Unidentified\"" + "}" + "]";

				
			}
		return genderData;
		} catch (MalformedURLException e) {
			return e.getMessage();
		} catch (IOException e) {
			return e.getMessage();
		} catch (Exception e) {
			return e.getMessage();
		}
	}
	
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

}
