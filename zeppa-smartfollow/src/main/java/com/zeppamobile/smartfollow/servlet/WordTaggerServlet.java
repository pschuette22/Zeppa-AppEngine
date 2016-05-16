package com.zeppamobile.smartfollow.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import com.zeppamobile.common.UniversalConstants;
import com.zeppamobile.common.utils.Utils;
import com.zeppamobile.smartfollow.comparewords.WordInfo;
import com.zeppamobile.smartfollow.task.CompareTagsTask;

import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.stanford.nlp.ling.WordLemmaTag;
import it.uniroma1.lcl.adw.ADWConfiguration;
import it.uniroma1.lcl.adw.comparison.WeightedOverlap;
import it.uniroma1.lcl.adw.semsig.LKB;
import it.uniroma1.lcl.adw.semsig.SemSigComparator;
import it.uniroma1.lcl.adw.utils.GeneralUtils;
import it.uniroma1.lcl.adw.utils.SentenceProcessor;

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
		if (Utils.isWebSafe(tagText)) {
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

			// Split the words into an array by removing white space
			String tagWordSentence = builder.toString().trim();

			List<WordLemmaTag> lemmaTags = SentenceProcessor.getInstance().processSentence(tagWordSentence, false);

			// Tag the sentence

			// These will be the objects that are returned
			List<String> indexedWords = new ArrayList<String>();
			Map<String, List<Integer>> synsetMap = new HashMap<String, List<Integer>>();
			double totalTagWeight = 0;
			
			// If there are multiple words, find the ones closest to each other
			// word
			if (lemmaTags.size() > 1) {
				SemSigComparator comparator = new SemSigComparator();
				for (int i = 0; i < lemmaTags.size() - 1; i++) {
					WordLemmaTag src = lemmaTags.get(i);
					POS srcPOS = GeneralUtils.getTagfromTag(src.tag());
					// System.out.println("src: " + src.lemma() + " " +
					// srcPOS.getTag());
					WordLemmaTag trg = lemmaTags.get(i + 1);
					POS trgPOS = GeneralUtils.getTagfromTag(trg.tag());
					// System.out.println("trg: " + trg.lemma() + " " +
					// trgPOS.getTag());
					IWord[] closestSet = comparator.getClosestSenses(src.lemma(), srcPOS, trg.lemma(), trgPOS,
							LKB.WordNetGloss, new WeightedOverlap(), 0);
					if (indexedWords.isEmpty()) {
						IWord word = closestSet[0];
						// ISynset synset = word.getSynset();
						if (word != null) {
							// String indexedWordId =
							// word.getLemma()+"-"+word.getPOS().getTag()+"-"+word.getLexicalID();
							String indexedWordId = formatIWordID(word.getID());
							System.out.println("Indexed Word Id: " + indexedWordId);
							System.out.println("Lemma: " + word.getLemma());
							indexedWords.add(indexedWordId);
							List<ISynsetID> relatedSynsets = word.getSynset().getRelatedSynsets();
							List<Integer> relatedSynsetIds = new ArrayList<Integer>();
							for (ISynsetID relatedSynset : relatedSynsets) {
								// String relatedWordId =
								// relatedWord.getLemma()+"-"+relatedWord.getPOS().getTag()+"-"+relatedWord.getWordNumber();
								int offset = relatedSynset.getOffset();
								//System.out.println("Related WordId: " + relatedWordId);
								//System.out.println("Related Word: " + relatedWord.getLemma());
								System.out.println("Related synset offset: " + offset);
								relatedSynsetIds.add(offset);
							}

							// List<ISynsetID> relatedSynsets =
							// synset.getRelatedSynsets();
							// for(ISynsetID relatedSynset: relatedSynsets) {
							// String relatedWordId = relatedSynset.;
							// System.out.println("Related WordId: " +
							// relatedWordId);
							// relatedWordIds.add(relatedWordId);
							// }

							synsetMap.put(indexedWordId, relatedSynsetIds);
							totalTagWeight+=getIndexWordWeight(indexedWordId);
						}
					}
					IWord word = closestSet[1];
					if (word != null) {
						// String indexedWordId =
						// word.getLemma()+"-"+word.getPOS().getTag()+"-"+word.getLexicalID();
						String indexedWordId = formatIWordID(word.getID());
						System.out.println("Indexed Word Id: " + indexedWordId);
						System.out.println("Lemma: " + word.getLemma());
						indexedWords.add(indexedWordId);
						List<ISynsetID> relatedSynsets = word.getSynset().getRelatedSynsets(); //getRelatedWords();
						List<Integer> relatedSynsetIds = new ArrayList<Integer>();
						for (ISynsetID relatedSynset : relatedSynsets) {
							// String relatedWordId =
							// relatedWord.getLemma()+"-"+relatedWord.getPOS().getTag()+"-"+relatedWord.getWordNumber();
							int offset = relatedSynset.getOffset();
							System.out.println("Related synset offset: " + offset);

							relatedSynsetIds.add(offset);
						}
						synsetMap.put(indexedWordId, relatedSynsetIds);
						totalTagWeight+=getIndexWordWeight(indexedWordId);
					}
				}

			} else if (!lemmaTags.isEmpty()) {
				WordLemmaTag taggedWord = lemmaTags.get(0);

				builder.append(taggedWord.lemma() + "-" + GeneralUtils.getTagfromTag(taggedWord.tag()).getTag() + "-1");
			}
			
			// Map the indexed words weight in the tag
			Map<String,Double> weightMap = new HashMap<String,Double>();
			if(totalTagWeight>0){
				for(String indexedWord: indexedWords){
					// Iterate through the list of indexed words mapping their relative weight
					weightMap.put(indexedWord, getIndexWordWeight(indexedWord)/totalTagWeight);
				}
			}
			

			// Put together a nice little json object and badaboom badabing
			JSONObject json = new JSONObject();
			json.put(UniversalConstants.kJSON_INDEX_WORD_LIST, indexedWords);
			json.put(UniversalConstants.kJSON_INDEX_WORD_SYNS_MAP, synsetMap);
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
	private String formatIWordID(IWordID iWordId) {
		String s = iWordId.toString();

		//
		int startIndex = s.indexOf("-") + 1;
		int endIndex = s.lastIndexOf("-");

		s = s.substring(startIndex, endIndex);

		return s;
	}
	
	/**
	 * Get the tag word weight based on the part of speech
	 * 
	 * @param indexWord
	 * @return associated weight or 0;
	 */
	public static double getIndexWordWeight(String indexWord) {
		/*
		 * Pschuette NOTE: I copied this entire method and the majority of the above from
		 * zeppa-api:com.zeppamobile.api.tasks.TagIndexingServlet ... This
		 * kinda smells to me, perhaps smartfollow should be passing around tag
		 * weights
		 */
		if (indexWord.contains("-N-")) {
			// noun
			return UniversalConstants.WEIGHT_NOUN;
		} else if (indexWord.contains("-V-")) {
			// verb
			return UniversalConstants.WEIGHT_VERB;
		} else if (indexWord.contains("-R-")) {
			// adverb
			return UniversalConstants.WEIGHT_ADVERB;
		} else if (indexWord.contains("-A-")) {
			// adjective
			return UniversalConstants.WEIGHT_ADJECTIVE;
		} else {
			return 0;
		}
	}

}
