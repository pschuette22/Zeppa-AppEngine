package com.zeppamobile.smartfollow.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zeppamobile.common.UniversalConstants;
import com.zeppamobile.common.utils.Utils;

import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.POS;
import edu.stanford.nlp.ling.TaggedWord;
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

			builder = new StringBuilder();

			// If there are multiple words, find the ones closest to each other
			// word
			if (lemmaTags.size() > 1) {
				SemSigComparator comparator = new SemSigComparator();
				List<IWord> indexedWords = new ArrayList<IWord>();
				for (int i = 0; i < lemmaTags.size() - 1; i++) {
					WordLemmaTag src = lemmaTags.get(i);
					POS srcPOS = GeneralUtils.getTagfromTag(src.tag());
					System.out.println("src: " + src.lemma() + " " + srcPOS.getTag());
					WordLemmaTag trg = lemmaTags.get(i + 1);
					POS trgPOS = GeneralUtils.getTagfromTag(trg.tag());
					System.out.println("trg: " + trg.lemma() + " " + trgPOS.getTag());
					IWord[] closestSet = comparator.getClosestSenses(src.lemma(), srcPOS, trg.lemma(), trgPOS,
							LKB.WordNetGloss, new WeightedOverlap(), 0);
					if (indexedWords.isEmpty()) {
						indexedWords.add(closestSet[0]);
					}
					indexedWords.add(closestSet[1]);
				}

				for (IWord iword : indexedWords) {
					if (iword == null) {
						builder.append("null word ");
					} else {
						builder.append(iword.getLemma() + "#" + iword.getPOS() + "#" + iword.getSenseKey() + " ");
					}
				}

			} else if (!lemmaTags.isEmpty()) {
				WordLemmaTag taggedWord = lemmaTags.get(0);

				builder.append(taggedWord.lemma() + "#" + GeneralUtils.getTagfromTag(taggedWord.tag()).getTag() + "#1");
			}

			// Write the response without extra spacing
			resp.getWriter().write(builder.toString().trim());
			resp.setStatus(HttpServletResponse.SC_OK);

		} else {
			// bad request, param not set
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}

	}

}
