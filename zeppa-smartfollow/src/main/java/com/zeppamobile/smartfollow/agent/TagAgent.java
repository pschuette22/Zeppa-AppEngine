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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.IndexWordSet;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.PointerType;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.relationship.Relationship;
import net.sf.extjwnl.data.relationship.RelationshipFinder;
import net.sf.extjwnl.data.relationship.RelationshipList;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

import com.zeppamobile.common.datamodel.EventTag;
import com.zeppamobile.common.datamodel.EventTagFollow;
import com.zeppamobile.common.utils.JSONUtils;
import com.zeppamobile.common.utils.ModuleUtils;
import com.zeppamobile.smartfollow.Configuration;
import com.zeppamobile.smartfollow.Constants;
import com.zeppamobile.smartfollow.Utils;

public class TagAgent {

	private EventTag tag;
	private List<EventTagFollow> tagFollows = new ArrayList<EventTagFollow>();
	private List<String> convertedTagWords = new ArrayList<String>();
	private String[] posTags;

	// Parse the tag
	private List<TagPart> parsedTagParts = new ArrayList<TagPart>();

	public TagAgent(UserAgent userAgent, EventTag tag) {
		this.tag = tag;
		// Fetch all the instances of people following this tag
		try {
			fetchTagFollows();
		} catch (NullPointerException e) {
			if (!Configuration.isTesting()) {
				System.out
						.print("Caught null pointer fetching tags, ignored in test");
			} else {
				throw e;
			}
		}
		/*
		 * Try to turn the tag into a machine-readable sentence This also
		 * initializes convertedTagWords and posTags If an exception is throw...
		 * that really confuses things Thrown because there was an issue getting
		 * NLP Dictionary
		 */
		try {
			dissectText();
		} catch (JWNLException e) {
			// Done derped
			e.printStackTrace();
			System.out.print("Unable to dissect text");
		}
	}

	public EventTag getTag() {
		return tag;
	}

	public Long getTagId() {
		return tag.getId();
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
		if (tagFollows.isEmpty()) {
			return -1; // Ignore in case this is a new tag
		} else if (numberOfMinglers > 0
				&& tagFollows.size() == numberOfMinglers) {
			// Every mingler follows this. return 1.
			return 1;
		}
		try {
			double calculatedPopularity = 0;
			int mmFollows = 0;
			if (!mutualMinglerIds.isEmpty()) {
				for (EventTagFollow follow : tagFollows) {
					if (Utils.listContainsId(mutualMinglerIds,
							follow.getFollowerId())) {
						mmFollows++;
					}
				}
				// If there were tag follows, make this the majority
				// Calculated popularity set to the number of mutual minglers
				// following as a percent
				calculatedPopularity += (mmFollows / tagFollows.size());
			}

			// Mutual Minglers is figured in twice. If the two users have a lot
			// of mutual mingers and a majority of them follow this tag, it
			// sways relative popularity greatly. If not, it has no effect
			// ie. if half the tag followers are mutual mingers and all mutual
			// follow, relative popularity would be %75
			calculatedPopularity += ((1 - calculatedPopularity) * (tagFollows
					.size() / numberOfMinglers));

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
		for (EventTagFollow f : tagFollows) {
			for (Long l : mutualMinglerIds) {
				if (f.getFollowerId().longValue() == l.longValue()) {
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
			if (agent.containsTag(tag.getId())
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
	 * Quickly fetch all the follows for this tag
	 */
	private void fetchTagFollows() {

		if (Configuration.isTesting()) {
			return;
		}

		try {

			Dictionary<String, String> params = new Hashtable<String, String>();
			params.put("filter", "tagId==" + tag.getId());

			URL eventRelationshipsURL = ModuleUtils.getZeppaAPIUrl(
					"listEventTagFollow", params);

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					eventRelationshipsURL.openStream()));

			StringBuilder builder = new StringBuilder();

			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}

			List<EventTagFollow> result = JSONUtils
					.convertTagFollowListString(builder.toString());
			tagFollows.addAll(result);

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Call this method to pull apart tag text and assign parts of speech.
	 * 
	 * @throws JWNLException
	 */
	private void dissectText() throws JWNLException {
		// First, compound text to list of words, numbers and character strings
		List<String> stringList = Utils.convertTextToStringList(getTagText());

		// Check to see if the words are recognized
		for (String s : stringList) {

			if (Character.isLetter(s.charAt(0))) {

				// This is a slang word. Try to replace it
				convertedTagWords.addAll(Utils.slangConverter(s));
				// TODO: pick the word apart and add individual

			} else if (!Character.isDigit(s.charAt(0)) && s.length() > 1) {
				// String is not made of letters or digits and is multiple
				// characters. Must be characters try to convert them
				convertedTagWords.addAll(Utils.characterStringConverter(s));

			} else { // is a number
				convertedTagWords.add(s);
			}

		}

		/*
		 * Try to get the model
		 */
		POSModel model = Utils.getPOSModel();
		if (model == null) {
			// maybe flag this
			return;
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
				System.out.println("Word: " + word + " - Part Of Speech: "
						+ posT);
				POS pos = null;
				// Try to assign a part of speech. NBD if we can't
				if (posT.startsWith("JJ")) {
					pos = POS.ADJECTIVE;

				} else if (posT.startsWith("NN") || posT.contains("PR")) {
					pos = POS.NOUN;
				} else if (posT.startsWith("RB")) {
					pos = POS.ADVERB;
				} else if (posT.startsWith("VB")) {
					pos = POS.VERB;
				}
				parsedTagParts.add(new TagPart(word, pos));

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
		double similarity = 0;

		if (getTagText().equalsIgnoreCase(tag.getTagText())) {
			// Tags are the same, ignoring case. Very likely talking about the
			// same thing therefore completely similar
			System.out.print(getTagText() + " is the same as "
					+ tag.getTagText());
			similarity = 1;
		} else {
			// Take the average of each part of the dissected tags match count
			try {
				// iterate through each part of a tag

				double similarityWeight = 0;

				for (TagPart p : parsedTagParts) {
					// compare it to each part of another tag
					for (TagPart p2 : tag.parsedTagParts) {

						System.out.println("Comparing " + p.word + " to "
								+ p2.word);

						double sim = p.calculatedMatch(p2);
						// If we can calculate their similarity with some degree
						// of certainty,
						// add and average
						System.out.println("Calculated similarity: " + sim);
						if (sim >= 0) {
							// Tags have made a valid comparison of content

							// Quickly average adding this calculation
							double matchWeight = p.getCalculatedMatchWeight(p2);
							similarity += sim * matchWeight;
							similarityWeight += matchWeight;

							System.out
									.println("Total similarity " + similarity);
							System.out.println("Total similarity weight "
									+ similarityWeight);

						}

					}
				}

				if (similarityWeight > 0) {
					similarity /= similarityWeight;
				}
			} catch (Exception e) {
				/*
				 * Something went wrong. return calculations make so far
				 */
				if (similarity == 0) {
					similarity = -1;
				}
			}

		}
		return similarity;
	}

	/**
	 * Get the text for this tag as users see it
	 * 
	 * @return
	 */
	public String getTagText() {
		return tag.getTagText();
	}

	/**
	 * 
	 * @author Pete Schuette
	 * 
	 *         This class represents a word in a tag
	 *
	 */
	private class TagPart {

		private String word;
		private POS pos;
		// Relatively high probability this value is null
		private IndexWord indexWord = null;
		private IndexWordSet indexWordSet = null;

		public TagPart(String word, POS pos) {

			// Make non-noun words lowercase for library readability
			if (Character.isLetter(word.charAt(0))
					&& (pos == null || pos != POS.NOUN)) {
				word = Utils.toLowercase(word);
			}

			this.word = word;
			this.pos = pos;
			try {
				// If there is a defined part of speech, try to get the synset
				if (pos != null) {
					// Synset synset = null;
					// if (pos == POS.ADJECTIVE) {
					// synset = new AdjectiveSynset(Constants.getDictionary());
					// } else if (pos == POS.VERB) {
					// synset = new VerbSynset(Constants.getDictionary());
					// } else {
					//
					// synset = new Synset(Constants.getDictionary(), pos);
					// }

					indexWord = Constants.getDictionary().lookupIndexWord(pos,
							word);

				}

			} catch (JWNLException e) {
				e.printStackTrace();
				// all good, synset is null;
				// TODO: Log word and reason

			}

		}

		/**
		 * Get the weight of calculated matches
		 * 
		 * @param part
		 *            - @TagPart used to calculate the match
		 * @return double between 0 and 1;
		 */
		public double getCalculatedMatchWeight(TagPart part) {
			if (pos == null || part.pos == null || indexWord == null
					|| part.indexWord == null) {
				return .2;
			} else if (pos.equals(part.pos)) {
				// Same part of speech
				return getPOSWeight(pos);
			} else {
				return (getPOSWeight(pos) * getPOSWeight(part.pos));
			}

		}

		/**
		 * My determined weights of different parts of speech when determining
		 * this
		 * 
		 * @param pos
		 * @return
		 */
		private double getPOSWeight(POS pos) {
			if (pos.equals(POS.NOUN)) {
				return .9;
			} else if (pos.equals(POS.VERB)) {
				return .7;
			} else if (pos.equals(POS.ADVERB)) {
				return .5;
			} else if (pos.equals(POS.ADJECTIVE)) {
				return .4;
			} else
				return .2;
		}

		/**
		 * Calculates this part of a tag with another
		 * 
		 * @param part
		 *            - TagPart to compare to this one
		 * @return percent similarity as decimal (-1 for incomparable)
		 */
		public double calculatedMatch(TagPart part) {
			// negative number indicates it couldn't be calculated

			// This is default and error value
			double calc = -1;

			// first check to see if they're the same part of speech
			try {
				if (pos == null && part.pos == null) {
					// Both null
					// If they're similar enough, we'll compare
					// Get the first char to make comparison easier
					char ch1 = word.charAt(0);
					char ch2 = part.word.charAt(0);
					if (!Character.isLetterOrDigit(ch1)
							&& !Character.isLetterOrDigit(ch2)) {
						// This might be absolutely worthless

						// identify symbols
						// Only compare if they're multiple characters
						if (word.length() > 1 || part.word.length() > 1) {
							calc = Utils.stringSimilarityCalculation(word,
									part.word);
						}

					} else if (Character.isDigit(ch1) && Character.isDigit(ch2)) {
						// Two numbers. Try to cast them
						int i1 = Integer.valueOf(word);
						int i2 = Integer.valueOf(part.word);
						// Average them. If it was a year, could be relatively
						// similar
						// The smaller the difference, the higher the similarity
						// 2015 and 2016 have 4030/4031 similarity
						// 1 and 100 have 2/101
						// 2 and 3 have 4/5
						calc = 1 - (Math.abs(i1 - i2) / (i1 + i2));
					} else if (Character.isLetter(ch1)
							&& Character.isLetter(ch2)) {
						// Two undefined words
						if (word.length() > 4 && part.word.length() > 4) {
							// Don't compare words that aren't at least 5
							// letters
							// this eliminates words like 'the' and 'that'
							calc = Utils.stringSimilarityCalculation(word,
									part.word);
						}

					} // else, they're not similar at all

				} else if (pos == null || part.pos == null) {
					// One has defined part of speech and the other does not
					// Ignore this case
				} else if (pos.equals(part.pos)) {
					// Same part of speech
					if (word.equalsIgnoreCase(part.word)) {
						// Same part of speech and word. completely the same
						calc = 1;

					} else if (indexWord != null && part.indexWord != null) {

						// Check for immediate relationship
						if (RelationshipFinder.getImmediateRelationship(
								indexWord, part.indexWord) > 0
								|| RelationshipFinder.getImmediateRelationship(
										part.indexWord, indexWord) > 0) {
							System.out.println(indexWord.getLemma() + " and "
									+ part.indexWord.getLemma()
									+ " are synonyms");
							return 1;
						}

						/*
						 * Find usage relationships
						 */

						List<PointerType> allTypes = PointerType
								.getAllPointerTypesForPOS(pos);

						calc = compareIndexWordsForPointerTypes(indexWord,
								part.indexWord, allTypes);

					} else if (pos == POS.NOUN) {
						// Couple of Nouns without index check for similarity

						/*
						 * 
						 * Only Calculate similarity by degrees of separation
						 * for longer nouns. If it is 3 letters or less, it's
						 * likely an acronym or something like "me" and leaves
						 * too much margin for error.
						 */
						if (word.length() > 3 && part.word.length() > 3) {
							calc = 0;
							if (word.contains(part.word)
									|| part.word.contains(word)) {
								// One word contains the other. They're probably
								// relatively similar
								calc += .4;
							}

							calc += (1 - calc)
									* Utils.stringSimilarityCalculation(word,
											part.word);
						}

					}

				} else if (indexWord != null && part.indexWord != null) {

					/*
					 * Words are indexed but different parts of speech. Try to
					 * find relationships, throw out if none found
					 */

					List<PointerType> relevantTypes = new ArrayList<PointerType>();
					for (PointerType p1Type : PointerType
							.getAllPointerTypesForPOS(pos)) {
						if (p1Type.appliesTo(part.pos)) {
							relevantTypes.add(p1Type);
						}
					}

					/*
					 * Compare indexed words to calculate similarity
					 */
					calc = compareIndexWordsForPointerTypes(indexWord,
							part.indexWord, relevantTypes);
					
					if (calc <= 0) {
						// If there is no similarity here, try comparing the words with other relevant parts of speech
						/*
						 * If the index word sets for each word lemma are null, look them up
						 */
						if(indexWordSet == null){
							indexWordSet = Constants.getDictionary().lookupAllIndexWords(
									indexWord.getLemma());
						}
						
						if(part.indexWordSet == null) {
							part.indexWordSet = Constants.getDictionary().lookupAllIndexWords(
									part.indexWord.getLemma());
						}
						
						// Return the comparison
						return compareIndexWordSets(indexWordSet, part.indexWordSet);
						
					}

				}

			} catch (Exception e) {

				// A multitude of issues could have happened.
				e.printStackTrace();
				System.out.println("Exception");
			}

			return calc;
		}

		/**
		 * Compare two indexed words based on a relationships of given pointer
		 * types
		 * 
		 * @param word1
		 * @param word2
		 * @param pointerTypes
		 * @return calculated similarity as a decimal between 0 and 1
		 * @throws JWNLException
		 * @throws CloneNotSupportedException
		 */
		private double compareIndexWordsForPointerTypes(IndexWord word1,
				IndexWord word2, List<PointerType> pointerTypes)
				throws CloneNotSupportedException, JWNLException {
			double similarity = 0;

			List<Synset> set1 = word1.getSenses();
			List<Synset> set2 = word2.getSenses();

			if (set1.isEmpty() || set2.isEmpty() || pointerTypes.isEmpty()) {
				similarity = -1;
			} else {
				/*
				 * Otherwise, perform calculations
				 */

				// Iterate through both lists and figure out the highest
				// similarity
				for (int i = 0; i < set1.size(); i++) {
					Synset s1 = set1.get(i);
					for (int j = 0; j < set2.size(); j++) {
						Synset s2 = set2.get(j);

						/*
						 * Iterate through all pointer types and try to find
						 * relationships Calculate strength of these
						 * relationships
						 */
						for (PointerType type : pointerTypes) {
							RelationshipList relationships = RelationshipFinder
									.findRelationships(s1, s2, type);

							if (relationships.isEmpty()) {
								continue;
							}

							double relationshipWeight = getPOSWeight(s1
									.getPOS()) * getPOSWeight(s2.getPOS());
							double similarityCalc = calculatedRelationshipsListStrength(
									relationships, relationshipWeight);

							if (similarity < similarityCalc) {
								similarity = similarityCalc;
							}

						}

					}
				}
			}

			return similarity;
		}

		/**
		 * Given a list of relationships between synsets, determine their
		 * relational strength
		 * 
		 * @return percent strength between 0 and 1 where 0 is no strength and 1
		 *         is completely the same
		 */
		private double calculatedRelationshipsListStrength(
				RelationshipList relationships, double relationshipWeight) {
			double strength = 0;

			/*
			 * Create a map of all pointers references with their minimum depth
			 * of reference
			 */
			try {
				Iterator<Relationship> iterator = relationships.iterator();
				while (iterator.hasNext()) {

					Relationship r = iterator.next();

					double depth = r.getDepth();

					double adjustment = (Math.pow(relationshipWeight, depth))
							* (1.0 - strength);
					strength += adjustment;

				}

			} catch (NullPointerException e) {
				// Bad list was passed in, no strength
			}


			return strength;
		}

		/**
		 * Calculate other forms of this words similarity;
		 * 
		 * @param wordSet1
		 * @param wordSet2
		 * @return
		 */
		private double compareIndexWordSets(IndexWordSet wordSet1,
				IndexWordSet wordSet2) {
			double similarity = 0;

			if (wordSet1 != null && wordSet2 != null) {

				/*
				 * Double loop through both wordsets to find ones with a
				 * matching part of speech
				 */
				for (IndexWord indexWord1 : wordSet1.getIndexWordCollection()) {
					for (IndexWord indexWord2 : wordSet2
							.getIndexWordCollection()) {
						if (indexWord1.getPOS().equals(indexWord2.getPOS())) {
							/*
							 * When a matching part of speech is found,
							 * Calculate similarity
							 */
							try {
								double calculatedSimilarity = compareIndexWordsForPointerTypes(
										indexWord1,
										indexWord2,
										PointerType
												.getAllPointerTypesForPOS(indexWord1
														.getPOS()));
								if (similarity < calculatedSimilarity) {
									similarity = calculatedSimilarity;
								}

							} catch (CloneNotSupportedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (JWNLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}

					}
				}
			}

			return similarity;
		}

	}

}
