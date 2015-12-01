package com.zeppamobile.frontend.getevents;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.zeppamobile.common.UniversalConstants;
import com.zeppamobile.common.utils.ModuleUtils;

/**
 * Servlet implementation class GetEventsServlet
 */
public class GetEventsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		// Fetch the current ID token
		// String idToken = req.getParameter(UniversalConstants.PARAM_ID_TOKEN);
		String token = (String) req.getSession().getAttribute(
				UniversalConstants.PARAM_ID_TOKEN);
		// resp.getWriter().println(token);

		Map<String, String> params = new HashMap<String, String>();
		// Add the id token as a parameter
		params.put(UniversalConstants.PARAM_ID_TOKEN, token);

		try {
			// Create the URL to map to the EventAdminServlet
			URL url = ModuleUtils.getZeppaModuleUrl("zeppa-api",
					"/admin/event-servlet/", params);

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					url.openStream()));

			// Read from the buffer line by line and write to the response
			// item
			StringBuilder builder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
			reader.close();

			

			/*
			 * Try parsing all this fun stuff out
			 */
			try {
				
				String format = "%-20s %-20s %-20s %-20s";
				resp.getWriter().println(
						String.format(format, "Event Title", "Location",
								"Start Time (ms)", "End Time (ms)") + "\n");
				// Get the objects array from the JSON response
				JSONParser parser = new JSONParser();
				
				JSONObject obj = (JSONObject) parser.parse(builder.toString());
				resp.getWriter().println(builder.toString());
				
				String arrayString = (String) obj.get(UniversalConstants.KEY_OBJECTS);
				
				JSONArray objArr = (JSONArray) parser.parse(arrayString);

				// Get and print all of the event titles
				for (int i = 0; i < objArr.size(); i++) {
					JSONObject element = (JSONObject) objArr.get(i);
					String title = (String) element.get("title");
					String location = (String) element.get("displayLocation");
					Long start = (Long) element.get("start");
					Long end = (Long) element.get("end");
					resp.getWriter()
							.println(
									String.format(format, title, location,
											String.valueOf(start),
											String.valueOf(end)));
				}
				resp.setStatus(HttpServletResponse.SC_OK);


			} catch (ParseException | NullPointerException e) {
				resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				e.printStackTrace(resp.getWriter());
			}

			// Set there response status
		} catch (MalformedURLException e) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			e.printStackTrace(resp.getWriter());

		} catch (IOException e) {
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			e.printStackTrace(resp.getWriter());
		}

	}
}
