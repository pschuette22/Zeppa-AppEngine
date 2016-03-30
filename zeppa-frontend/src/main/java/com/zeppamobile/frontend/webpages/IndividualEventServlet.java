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
