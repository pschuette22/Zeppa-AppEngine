package com.zeppamobile.frontend.webpages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.zeppamobile.common.UniversalConstants;
import com.zeppamobile.common.utils.ModuleUtils;
import com.zeppamobile.common.utils.Utils;

/**
 * @author PSchuette
 * 
 *         Display the way tags are indexed in Zeppa. Tags are not inserted into
 *         the datastore
 *
 */
public class IndexTagExampleServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		System.out.println("Received get request");
		UserService userService = UserServiceFactory.getUserService();

		// Verify user is logged in.
		if (userService.getCurrentUser() != null) {
			
			System.out.println("Logged in user made request");
			// TODO: verify user is authorized

		} else {
			// If not logged in, redirect them to login page
			loginRedirect(userService, req, resp);
			return;
		}

		// Return the jsp file as is for simple get request
		resp.sendRedirect("/index-tag-example.jsp");
//		resp.setStatus(HttpServletResponse.SC_OK);
	}

	/**
	 * Post requests are made from redirects or when web page is attempting to
	 * index a tag
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		UserService userService = UserServiceFactory.getUserService();
		// String callingUrl = req.getRequestURL().toString();
		// System.out.println("Calling url: " + callingUrl);

		// Make sure user is logged into Google Account
		if (userService.getCurrentUser() != null) {
			System.out.println("Logged in user made request");
			// TODO: verify user is authorized
		} else {
			// If not logged in, redirect them to login page
			loginRedirect(userService, req, resp);
			return;
		}

		// Start building the response url
		StringBuilder builder = new StringBuilder();
		builder.append("/index-tag-example.jsp");

		String tagTextParam = req.getParameter(UniversalConstants.kREQ_TAG_TEXT);
		if (Utils.isWebSafe(tagTextParam)) {
			// Append request params			 
			try {
				// Attempt to index the tag
				doIndexTag(tagTextParam, req, resp);
			} catch (ParseException | JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				resp.getWriter().println(e.getLocalizedMessage());
				resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}

			
		}

		// Return response
		resp.sendRedirect(builder.toString());
	}

	/**
	 * Convenience method for redirecting user to login page if they try to
	 * access this without being logged in
	 * 
	 * @param userService
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	private void loginRedirect(UserService userService, HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		System.out.println("Redirecting to login");
		String redirectUrl = userService.createLoginURL(req.getRequestURI());
		// Redirect the request to login page
		req.getRequestDispatcher(redirectUrl).forward(req, resp);
	}

	/**
	 * Make a request to smartfollow to index a given tag to
	 * 
	 * @param tagText
	 *            - formatted text of tag to be indexed
	 * @param req
	 *            - http request object
	 * @param resp
	 *            - http response object
	 * @throws IOException
	 * @throws ParseException
	 * @throws JSONException 
	 */
	@SuppressWarnings("unchecked")
	private void doIndexTag(String tagText, HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ParseException, JSONException {

		// Get the indexed words from smartfollow
		Map<String, String> params = new HashMap<String, String>();
		params.put(UniversalConstants.kREQ_TAG_TEXT, tagText);

		URL url = ModuleUtils.getZeppaModuleUrl("zeppa-smartfollow", "word-tagger", params);

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(false);
		connection.setRequestMethod("GET");
		// request has 10 seconds to execute
		connection.setReadTimeout(10 * 1000);

		// If success, read and report
		if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
			// Read and close the response
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			String responseString = "";
			while ((line = reader.readLine()) != null) {
				responseString += line;
			}
			reader.close();

			// Parse the response json
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(responseString);

			List<String> indexWords = (List<String>) json.get(UniversalConstants.kJSON_INDEX_WORD_LIST);
			Map<String, List<String>> synsMap = (Map<String, List<String>>) json
					.get(UniversalConstants.kJSON_INDEX_WORD_SYNS_MAP);
			Map<String,Double> weightMap = (Map<String, Double>) json
					.get(UniversalConstants.kJSON_INDEX_WORD_WEIGHT_MAP);
			double totalTagWeight = (Double) json.get(UniversalConstants.kJSON_TOTAL_WEIGHT);
			
			// Neatly package the response for simple rendering
			JSONObject obj = new JSONObject();
			obj.put(UniversalConstants.kJSON_TOTAL_WEIGHT, totalTagWeight);
			
			// Build a nice little json array of words that make up this tag
			// TODO: add a "Human Readable" option?
			JSONArray words = new JSONArray();
			for(String indexWord: indexWords) {
				JSONObject wordObj = new JSONObject();
				wordObj.put(UniversalConstants.kJSON_INDEX_WORD, indexWord);
				wordObj.put(UniversalConstants.kJSON_INDEX_WORD_SYNS_ARRAY, synsMap.get(indexWord));
				wordObj.put(UniversalConstants.kJSON_TAG_WORD_WEIGHT, weightMap.get(indexWord));
				words.put(wordObj);
			}
			obj.put(UniversalConstants.kJSON_TAG_WORD_INFO_ARRAY, words);
			obj.put(UniversalConstants.kJSON_TAG_WORD_COUNT, words.length());
			
			// Whoop Whoop, send it back to the response
			resp.addHeader(UniversalConstants.HEADER_TAG_INDEXING_INFO, obj.toJSONString());
			resp.setStatus(HttpServletResponse.SC_OK);
			
		} else {
			resp.setStatus(connection.getResponseCode());
			// TODO: throw exception or notify the user of error
		}
	}


}
