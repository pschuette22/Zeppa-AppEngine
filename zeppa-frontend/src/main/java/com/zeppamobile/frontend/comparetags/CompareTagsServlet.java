package com.zeppamobile.frontend.comparetags;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zeppamobile.common.utils.ModuleUtils;
import com.zeppamobile.common.utils.Utils;

public class CompareTagsServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This is a simple servlet request to compare two tags
	 * 
	 * @param req
	 *            - request made to servlet
	 * @param req
	 *            - response from servlet
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String response = "SC_FOUND";
		// TODO Auto-generated method stub
		// Set status to found at first just in case uncaught error occurs
		resp.setStatus(HttpServletResponse.SC_FOUND);
		
		// Logging stuff
//		resp.getWriter().println("Posting to Compare Tags");

		
		/*
		 * Get the tag text from parameters
		 */
		String tag1 = req.getParameter("tag1");
		String tag2 = req.getParameter("tag2");

		if (Utils.isWebSafe(tag1) && Utils.isWebSafe(tag2)) {

			/*
			 * Parameters accepted, making call to smartfollow servlet
			 */
			Map<String, String> params = new HashMap<String, String>();
			params.put("tagText1", tag1);
			params.put("tagText2", tag2);

			/*
			 * Read from the request
			 */
			try {
//				resp.getWriter().println("Building Url: ");

				URL url = ModuleUtils.getZeppaModuleUrl("zeppa-smartfollow",
						"compare-tags", params);
				
//				resp.getWriter().print(url.toString() + "\n");

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(url.openStream()));
				String line;

				// Read from the buffer line by line and write to the response
				// item
				StringBuilder message = new StringBuilder();
				
				while ((line = reader.readLine()) != null) {
					resp.getWriter().write(line+"\n");
//					resp.getWriter().println(line);
					message.append(line);
					message.append("\n");
				}

				reader.close();

				req.setAttribute("message", message.toString());
				
				// Set there response status
				response = "SC_OK";
//				resp.getWriter().println("Message: \n{\n" + message.toString() + "\n}");
				resp.setStatus(HttpServletResponse.SC_OK);

			} catch (MalformedURLException e) {
				// ...
				response = "SC_INTERNAL_SERVER_ERROR";
				resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				e.printStackTrace(resp.getWriter());
				
			} catch (IOException e) {
				// ...
				resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response = "SC_INTERNAL_SERVER_ERROR";
				e.printStackTrace(resp.getWriter());

			}

		} else {
			/*
			 * If bad parameters were passed
			 */
			response = "SC_BAD_REQUEST";
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		
//		resp.getWriter().println("Response: " + response);

	}

}
