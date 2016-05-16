package com.zeppamobile.smartfollow.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import com.zeppamobile.common.UniversalConstants;
import com.zeppamobile.smartfollow.comparewords.SenseComparator;

/**
 * 
 * @author PSchuette Servlet used to tag words in an event tag
 */
public class WordTaggerServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// Get the text of this tag
		String tagText = req.getParameter(UniversalConstants.kREQ_TAG_TEXT);
		if (com.zeppamobile.common.utils.Utils.isWebSafe(tagText)) {
			// Break the text up into separate words where a capital indicates a
			// new word
			StringBuilder builder = new StringBuilder();

			for (int i = 0; i < tagText.length(); i++) {
				char c = tagText.charAt(i);
				if (Character.isUpperCase(c)) {
					builder.append(" ");
				}
				builder.append(c);
			}

			// Split the words into a stack by removing white space
			String tagWordSentence = builder.toString().trim();
			Map<String, List<String>> indexedSynsMap = SenseComparator.doWordMapping(tagWordSentence);

			// Find the senses that are closest to each other
			Map<String, Double> weightMap = new HashMap<String, Double>();
			double totalTagWeight = 0;

			// Iterate through the senses formatting and mapping to syn sets
			for (String indexedWord: indexedSynsMap.keySet()) {
				totalTagWeight += getIndexWordWeight(indexedWord);
			}

			// Map weighting
			if (totalTagWeight > 0) {
				// After doing all that jazz, figure relative weights
				for (String wordId : indexedSynsMap.keySet()) {
					weightMap.put(wordId, getIndexWordWeight(wordId)/totalTagWeight);
				}
			}

			// Put together a nice little json object and badaboom badabing
			JSONObject json = new JSONObject();
			json.put(UniversalConstants.kJSON_INDEX_WORD_SYNS_MAP, indexedSynsMap);
			json.put(UniversalConstants.kJSON_INDEX_WORD_WEIGHT_MAP, weightMap);
			json.put(UniversalConstants.kJSON_TOTAL_WEIGHT, totalTagWeight);

			// Write the response without extra spacing
			resp.getWriter().write(json.toJSONString());
			resp.setStatus(HttpServletResponse.SC_OK);

		} else {
			// bad request, param not set
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}

	}


	/**
	 * Get the tag word weight based on the part of speech
	 * 
	 * @param indexWord
	 * @return associated weight or 0;
	 */
	public static double getIndexWordWeight(String indexWord) {
		/*
		 * Pschuette NOTE: I copied this entire method and the majority of the
		 * above from zeppa-api:com.zeppamobile.api.tasks.TagIndexingServlet ...
		 * This kinda smells to me, perhaps smartfollow should be passing around
		 * tag weights
		 */
		if (indexWord.endsWith("-n")) {
			// noun
			return UniversalConstants.WEIGHT_NOUN;
		} else if (indexWord.endsWith("-v")) {
			// verb
			return UniversalConstants.WEIGHT_VERB;
		} else if (indexWord.endsWith("-r")) {
			// adverb
			return UniversalConstants.WEIGHT_ADVERB;
		} else if (indexWord.endsWith("-a")) {
			// adjective
			return UniversalConstants.WEIGHT_ADJECTIVE;
		} else {
			return 0;
		}
	}

}
