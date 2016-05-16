package com.zeppamobile.smartfollow.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import com.zeppamobile.common.UniversalConstants;
import com.zeppamobile.smartfollow.Constants;
import com.zeppamobile.smartfollow.Utils;
import com.zeppamobile.smartfollow.comparewords.SenseComparator;

import edu.stanford.nlp.ling.WordLemmaTag;
import it.uniroma1.lcl.adw.utils.SentenceProcessor;
import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.Word;

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
			List<WordLemmaTag> lemmaTags = SentenceProcessor.getInstance().processSentence(tagWordSentence, false);
			Map<IndexWord, Synset> synsetMap = SenseComparator.formatSenses(lemmaTags);

			// Find the senses that are closest to each other
			List<String> indexedWords = new ArrayList<String>();
			Map<String, List<String>> indexWordSynsMap = new HashMap<String, List<String>>();
			Map<String, Double> weightMap = new HashMap<String, Double>();
			double totalTagWeight = 0;

			// Iterate through the senses formatting and mapping to syn sets
			for (Entry<IndexWord, Synset> entry : synsetMap.entrySet()) {
				String wordId = formatWordSense(entry.getKey().getLemma(), entry.getValue());

				indexedWords.add(wordId);
				List<String> indexWordSyns = new ArrayList<String>();
				// Add all the synonym ids
				for (Word synWord : entry.getValue().getWords()) {
					if (!wordId.startsWith(synWord.getLemma())) {
						indexWordSyns.add(formatWordSense(synWord.getLemma(), entry.getValue()));
					}
				}
				indexWordSynsMap.put(wordId, indexWordSyns);

				totalTagWeight += getIndexWordWeight(wordId);
			}

			if (totalTagWeight > 0) {
				// After doing all that jazz, figure relative weights
				for (String wordId : indexedWords) {
					weightMap.put(wordId, getIndexWordWeight(wordId)/totalTagWeight);
				}
			}

			// Put together a nice little json object and badaboom badabing
			JSONObject json = new JSONObject();
			json.put(UniversalConstants.kJSON_INDEX_WORD_LIST, indexedWords);
			json.put(UniversalConstants.kJSON_INDEX_WORD_SYNS_MAP, indexWordSynsMap);
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
	 * Format a given iWordID into proper, unique format
	 * 
	 * @param iWordId
	 * @return formatted string
	 */
	private String formatWordSense(String wordLemma, Synset sense) {
		return wordLemma + "-" + sense.getPOS().getKey() + "-" + sense.getOffset();
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
		if (indexWord.contains("-n-")) {
			// noun
			return UniversalConstants.WEIGHT_NOUN;
		} else if (indexWord.contains("-v-")) {
			// verb
			return UniversalConstants.WEIGHT_VERB;
		} else if (indexWord.contains("-r-")) {
			// adverb
			return UniversalConstants.WEIGHT_ADVERB;
		} else if (indexWord.contains("-a-")) {
			// adjective
			return UniversalConstants.WEIGHT_ADJECTIVE;
		} else {
			return 0;
		}
	}

}
