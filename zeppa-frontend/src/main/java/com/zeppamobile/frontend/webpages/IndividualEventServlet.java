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

}
