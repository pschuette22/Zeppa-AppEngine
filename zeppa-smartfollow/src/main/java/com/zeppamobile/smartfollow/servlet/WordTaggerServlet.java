package com.zeppamobile.smartfollow.servlet;

import java.io.IOException;
import java.util.ArrayList;
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

import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.stanford.nlp.ling.WordLemmaTag;
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
			Map<String, List<String>> synsetMap = new HashMap<String, List<String>>();

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
							indexedWords.add(indexedWordId);
							List<IWordID> relatedWords = word.getRelatedWords();
							List<String> relatedWordIds = new ArrayList<String>();
							for (IWordID relatedWord : relatedWords) {
								// String relatedWordId =
								// relatedWord.getLemma()+"-"+relatedWord.getPOS().getTag()+"-"+relatedWord.getWordNumber();
								String relatedWordId = formatIWordID(relatedWord);
								System.out.println("Related WordId: " + relatedWordId);
								relatedWordIds.add(relatedWordId);
							}

							// List<ISynsetID> relatedSynsets =
							// synset.getRelatedSynsets();
							// for(ISynsetID relatedSynset: relatedSynsets) {
							// String relatedWordId = relatedSynset.;
							// System.out.println("Related WordId: " +
							// relatedWordId);
							// relatedWordIds.add(relatedWordId);
							// }

							synsetMap.put(indexedWordId, relatedWordIds);
						}
					}
					IWord word = closestSet[1];
					if (word != null) {
						// String indexedWordId =
						// word.getLemma()+"-"+word.getPOS().getTag()+"-"+word.getLexicalID();
						String indexedWordId = formatIWordID(word.getID());
						System.out.println("Indexed Word Id: " + indexedWordId);
						indexedWords.add(indexedWordId);
						List<IWordID> relatedWords = word.getRelatedWords();
						List<String> relatedWordIds = new ArrayList<String>();
						for (IWordID relatedWord : relatedWords) {
							// String relatedWordId =
							// relatedWord.getLemma()+"-"+relatedWord.getPOS().getTag()+"-"+relatedWord.getWordNumber();
							String relatedWordId = formatIWordID(relatedWord);
							System.out.println("Related WordId: " + relatedWordId);

							relatedWordIds.add(relatedWordId);
						}
						synsetMap.put(indexedWordId, relatedWordIds);
					}
				}

				// TODO: return these objects serialized

				// for (IWord iword : indexedWords) {
				// if (iword == null) {
				// builder.append("null word ");
				// } else {
				// builder.append(iword.getLemma() + "#" + iword.getPOS() + "#"
				// + iword.getSenseKey() + " ");
				// }
				// }

			} else if (!lemmaTags.isEmpty()) {
				WordLemmaTag taggedWord = lemmaTags.get(0);

				builder.append(taggedWord.lemma() + "-" + GeneralUtils.getTagfromTag(taggedWord.tag()).getTag() + "-1");
			}

			JSONObject json = new JSONObject();
			json.put(UniversalConstants.kJSON_INDEX_WORD_LIST, indexedWords);
			json.put(UniversalConstants.kJSON_INDEX_WORD_SYNS_MAP, synsetMap);

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

}
