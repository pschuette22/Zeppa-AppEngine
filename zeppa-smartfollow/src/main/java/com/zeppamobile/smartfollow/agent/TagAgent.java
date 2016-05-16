/*
 * WordNet License
 * 
 * WordNet 3.0 Copyright 2006 by Princeton University. All rights reserved. 
 * THIS SOFTWARE AND DATABASE IS PROVIDED "AS IS" AND PRINCETON UNIVERSITY 
 * MAKES NO REPRESENTATIONS OR WARRANTIES, EXPRESS OR IMPLIED. BY WAY OF EXAMPLE, 
 * BUT NOT LIMITATION, PRINCETON UNIVERSITY MAKES NO REPRESENTATIONS OR 
 * WARRANTIES OF MERCHANT- ABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE OR THAT 
 * THE USE OF THE LICENSED SOFTWARE, DATABASE OR DOCUMENTATION WILL NOT INFRINGE 
 * ANY THIRD PARTY PATENTS, COPYRIGHTS, TRADEMARKS OR OTHER RIGHTS.
 */
package com.zeppamobile.smartfollow.agent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.POS;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

import com.zeppamobile.common.cerealwrapper.EventTagCerealWrapper;
import com.zeppamobile.common.report.SmartfollowReport;
import com.zeppamobile.smartfollow.Utils;
import com.zeppamobile.smartfollow.comparewords.WordInfo;
import com.zeppamobile.smartfollow.nlp.POSFactory;
import com.zeppamobile.smartfollow.task.CompareTagsTask;
import com.zeppamobile.smartfollow.task.CompareWordsTask;

/**
 * 
 * @author Pete Schuette
 * 
 *         This agent is used to calculate the similarity between Tags It
 *         employs Java's Word Net Library (Princeton), OpenNLP (Source
 *         Forge), and the ADW Library (Pilehvar)
 * 
 */
public class TagAgent extends BaseAgent {

	private EventTagCerealWrapper tag;
	private List<String> convertedTagWords = new ArrayList<String>();
	private String[] posTags;
	private ServletContext context;
	private POSModel model;

	// Parse the tag
	private List<WordInfo> parsedTagParts = new ArrayList<WordInfo>();

	public TagAgent(ServletContext context, EventTagCerealWrapper tag,
			SmartfollowReport report) {
		this.context = context;

		this.tag = tag;
		this.report = report;

		// Log progress
		log("Initializing Agent for " + tag.getTagText());

		/*
		 * Try to turn the tag into a machine-readable sentence This also
		 * initializes convertedTagWords and posTags If an exception is throw...
		 * that really confuses things Thrown because there was an issue getting
		 * NLP Dictionary
		 */
		try {
			log("Identifying elements of text");
			dissectText(context, tag.getTagText());
		} catch (JWNLException e) {
			// Done derped
			e.printStackTrace();
			log("Wordnet Error...");
			System.out.print("Unable to dissect text");
		}
	}

	public EventTagCerealWrapper getTagInfo() {
		return tag;
	}

	/**
	 * Percent of owners minglers who follow this tag
	 * 
	 * @param numberOfMinglers
	 *            - total number of minglers for the tag owner
	 * @return decimal percent (-1 on error)
	 */
	public double getCalculatedPopularity(int numberOfMinglers,
			List<Long> mutualMinglerIds) {

		// If this is a new tag, return -1 to indicate popularity cannot be
		// calculated
		if (tag.isNewTag()) {
			return -1;
		}

		if (numberOfMinglers > 0
				&& tag.getFollowerIds().size() == numberOfMinglers) {
			// Every mingler follows this. return 1.
			return 1;
		}
		try {
			double calculatedPopularity = 0;
			int mmFollows = 0;
			if (!mutualMinglerIds.isEmpty()) {
				for (Long followerId : tag.getFollowerIds()) {
					if (Utils.listContainsId(mutualMinglerIds, followerId)) {
						mmFollows++;
					}
				}
				// If there were tag follows, make this the majority
				// Calculated popularity set to the number of mutual minglers
				// following as a percent
				calculatedPopularity += (mmFollows / tag.getFollowerIds()
						.size());
			}

			// Mutual Minglers is figured in twice. If the two users have a lot
			// of mutual mingers and a majority of them follow this tag, it
			// sways relative popularity greatly. If not, it has no effect
			// ie. if half the tag followers are mutual mingers and all mutual
			// follow, relative popularity would be %75
			calculatedPopularity += ((1 - calculatedPopularity) * (tag
					.getFollowerIds().size() / numberOfMinglers));

			return calculatedPopularity;
		} catch (Exception e) {
			// Tag follows threw error, number of minglers is 0, could be
			// anything
			return -1;
		}
	}

	/**
	 * Percent of minglers in a provided list who follow tag
	 * 
	 * @return decimal percent (-1 default or unable to calculate)
	 */
	public double getCalculatedPopularityForMutualMinglers(
			List<Long> mutualMinglerIds) {
		int mutualWhoFollow = 0;
		for (Long followerId : tag.getFollowerIds()) {
			for (Long l : mutualMinglerIds) {
				if (followerId.longValue() == l.longValue()) {
					mutualWhoFollow++;
				}
			}
		}
		return (mutualWhoFollow / mutualMinglerIds.size());
	}

	/**
	 * Average popularity of events holding this tag
	 * 
	 * @param eventAgents
	 *            - events started by this user
	 * @return decimal percent (-1 default/ ignore)
	 * 
	 */
	public double getCalculatedHeldEventPopularity(List<EventAgent> eventAgents) {
		// Default value of negative 1
		double calculatedHeldEventPopularity = -1;

		// Iterate through all eventAgents passed in
		// NOTE: consider filtering out events created before tag
		for (EventAgent agent : eventAgents) {
			// If the event agent holds tag and popularity calculated, average
			// in
			if (agent.containsTag(tag.getTagId())
					&& agent.getCalculatedPopularity() > 0) {
				if (calculatedHeldEventPopularity < 0) {
					// calculated held popularity needs to be intialized
					calculatedHeldEventPopularity = agent
							.getCalculatedPopularity();
				} else {
					// Quickly average this popularity in
					calculatedHeldEventPopularity += agent
							.getCalculatedPopularity();
					calculatedHeldEventPopularity /= 2;
				}
			}

		}

		return calculatedHeldEventPopularity;
	}

	/**
	 * Call this method to pull apart tag text and assign parts of speech.
	 * 
	 * @throws JWNLException
	 */
	private void dissectText(ServletContext context, String text)
			throws JWNLException {
		System.out.println("Parsing text");
		// First, compound text to list of words, numbers and character strings
		List<String> stringList = Utils.convertTextToStringList(text);

		// Convert to dictionary words and add to list of words
		for (String s : stringList) {

			if (Character.isLetter(s.charAt(0))) {

				// This is a slang word. Try to replace it
//				convertedTagWords.addAll(Utils.slangConverter(s));
				// TODO: pick the word apart and add individual

			} else if (!Character.isDigit(s.charAt(0)) && s.length() > 1) {
				// String is not made of letters or digits and is multiple
				// characters. Must be characters try to convert them
//				convertedTagWords.addAll(Utils.characterStringConverter(s));

			} else { // is a number
				convertedTagWords.add(s);
			}

		}

		/*
		 * Quickly log progress
		 */
		StringBuilder builder = new StringBuilder();
		builder.append("Converted tag to readable words: ");
		Iterator<String> iterator = convertedTagWords.iterator();
		while (iterator.hasNext()) {
			builder.append(iterator.next());
			if (iterator.hasNext()) {
				builder.append(", ");
			}
		}
		log(builder.toString());

		/*
		 * Try to get the model
		 */
		POSFactory factory = new POSFactory(context);
		if (model == null) {
			System.out.println("Building model");
			model = factory.buildPOSModel();
			System.out.println("completed");
		}
		
		if (model == null) {
			System.out.println("Model is null");
		}

		POSTaggerME tagger = new POSTaggerME(model);
		// gets tag parts of speech
		String[] tagWordArray = new String[convertedTagWords.size()];
		for (int i = 0; i < convertedTagWords.size(); i++) {
			tagWordArray[i] = convertedTagWords.get(i);
		}

		posTags = tagger.tag(tagWordArray);

		try {
			for (int i = 0; i < convertedTagWords.size(); i++) {
				String word = convertedTagWords.get(i);
				String posT = posTags[i];
				log("Word: " + word + " - Part Of Speech: " + posT);
				POS pos = null;

				// Assign part of speech or keep it null
				if (posT.startsWith("JJ")) {
					pos = POS.ADJECTIVE;

				} else if (posT.startsWith("NN") || posT.contains("PR")) {
					pos = POS.NOUN;
				} else if (posT.startsWith("RB")) {
					pos = POS.ADVERB;
				} else if (posT.startsWith("VB")) {
					pos = POS.VERB;
				}
				parsedTagParts.add(new WordInfo(word, pos));

			}

		} catch (IndexOutOfBoundsException | NullPointerException e) {
			// Catch an thrown exception
			System.out.print("Error Converting word to tag part");
			e.printStackTrace();
		}

	}

	/**
	 * Calculate the similarity of this tag to another (between 0 and 1)
	 * 
	 * @param tag
	 *            - tag to compare this tag to
	 * @return calculatedSimilarity as a decimal percent
	 */
	public double calculateSimilarity(TagAgent tag) {
		log("Calculating similarity");
		if (this.getTagText().equalsIgnoreCase(tag.getTagText())) {
			// Tags are the same, ignoring case. Very likely talking about the
			// same thing therefore completely similar
			System.out.println(getTagText() + " is the same as "
					+ tag.getTagText());
			return 1;
		} else {
			// Fire up a new task
			CompareTagsTask task = new CompareTagsTask(this.context, this.parsedTagParts, tag.parsedTagParts);
			task.execute();
			return task.getSimilarity();
		}
	}

	/**
	 * Get the text for this tag as users see it
	 * 
	 * @return
	 */
	public String getTagText() {
		return tag.getTagText();
	}



}
