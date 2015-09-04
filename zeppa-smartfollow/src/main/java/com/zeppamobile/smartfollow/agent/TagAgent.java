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
import java.util.List;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.AdjectiveSynset;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.IndexWordSet;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.PointerType;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.VerbSynset;
import net.sf.extjwnl.data.relationship.RelationshipFinder;
import net.sf.extjwnl.data.relationship.RelationshipList;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

import com.zeppamobile.common.datamodel.EventTag;
import com.zeppamobile.common.datamodel.EventTagFollow;
import com.zeppamobile.smartfollow.Constants;
import com.zeppamobile.smartfollow.Utils;

public class TagAgent {

	private EventTag tag;
	private List<EventTagFollow> tagFollows = new ArrayList<EventTagFollow>();
	private List<String> convertedTagWords;
	private String[] posTags;

	// Parse the tag
	private List<TagPart> parsedTagParts = new ArrayList<TagPart>();

	public TagAgent(UserAgent userAgent, EventTag tag) {
		this.tag = tag;
		// Fetch all the instances of people following this tag
		fetchTagFollows();

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
		} else if (tagFollows.size() == numberOfMinglers) {
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
		// EventTagFollowEndpoint endpoint = new EventTagFollowEndpoint();
		// CollectionResponse<EventTagFollow> response = endpoint
		// .listEventTagFollow("tagId==" + tag.getId(), null, null, null);
		// tagFollows.addAll(response.getItems());

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

			if (!Character.isLetter(s.charAt(0))) {
				try {
					// If word isnt found in dictionary, assume it is slang
					IndexWordSet wordSet = Constants.getDictionary()
							.lookupAllIndexWords(s);
					if (wordSet.getIndexWordArray().length == 0) {
						// This is a slang word. Try to replace it
						convertedTagWords.addAll(Utils.slangConverter(s));
						// TODO: pick the word apart and add individual
					} else {
						convertedTagWords.add(s);
					}

				} catch (JWNLException e) {
					// Fuck it
					convertedTagWords.add(s);
				}
			} else if (!Character.isDigit(s.charAt(0)) && s.length() > 1) {
				// String is not made of letters or digits and is multiple
				// characters. Must be characters try to convert them
				convertedTagWords.addAll(Utils.characterStringConverter(s));

			} else { // is a number
				convertedTagWords.add(s);
			}

		}

		/*
		 * Convert the
		 */
		POSModel model = Utils.getPOSModel();
		if (model == null) {
			// maybe flag this
			return;
		}
		POSTaggerME tagger = new POSTaggerME(model);
		// gets tag parts of speech
		posTags = tagger.tag((String[]) convertedTagWords.toArray());

		try {
			for (int i = 0; i < convertedTagWords.size(); i++) {
				String word = convertedTagWords.get(i);
				String posT = posTags[i];
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

		if (tag.getTagText().equalsIgnoreCase(tag.getTagText())) {
			// Tags are the same, ignoring case. Very likely talking about the
			// same thing therefore completely similar
			similarity = 1;
		} else {
			// Take the average of each part of the dissected tags match count
			try {
				// iterate through each part of a tag
				for (TagPart p : parsedTagParts) {
					// compare it to each part of another tag
					for (TagPart p2 : tag.parsedTagParts) {

						double sim = p.calculatedMatch(p2);
						// If we can calculate their similarity with some degree
						// of certainty,
						// add and average
						if (sim >= 0) {
							// Quickly average adding this calculation
							similarity += sim;
							similarity /= 2;
						}

					}
				}
			} catch (Exception e) {
				/*
				 * Something went wrong. return calculations make so far
				 */
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

	private class TagPart {

		private String word;
		private POS pos;
		// Relatively high probability this value is null
		private IndexWord indexWord;

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
					Synset synset = null;
					if (pos == POS.ADJECTIVE) {
						synset = new AdjectiveSynset(Constants.getDictionary());
					} else if (pos == POS.VERB) {
						synset = new VerbSynset(Constants.getDictionary());
					} else {
						synset = new Synset(Constants.getDictionary(), pos);
					}

					indexWord = new IndexWord(Constants.getDictionary(), word,
							pos, synset);
				}

			} catch (JWNLException e) {
				e.printStackTrace();
				// all good, synset is null;
				// TODO: Log word and reason
				indexWord = null;
			}

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
			// This default and error value
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

				} else if (pos.equals(part.pos)) {
					// Same part of speech
					if (part.word.equalsIgnoreCase(word)) {
						// Same part of speech and word. completely the same
						calc = 1;

					} else if (indexWord != null && part.indexWord != null) {

						// Check for immediate relationship
						int immediate = RelationshipFinder
								.getImmediateRelationship(indexWord,
										part.indexWord);
						if (immediate == 1) {
							return 1;
						}

						// These words are the same part of speech and are
						// indexed
						// Calculate their similarity
						List<Synset> synsets = indexWord.getSenses();
						List<Synset> psynsets = part.indexWord.getSenses();

						/*
						 * Determine how many lists of relationships there are
						 * between all sysnsets
						 */
						if (!synsets.isEmpty() && !psynsets.isEmpty()) {
							int matchCount = 0;
							int total = 0;
							for (Synset s : synsets) {
								for (Synset sp : psynsets) {
									/*
									 * Find usage relationships: Times synonyms
									 * are used in place of each other
									 */
									RelationshipList list = RelationshipFinder
											.findRelationships(s, sp,
													PointerType.USAGE, 1);
									if (!list.isEmpty()) {
										matchCount++;
									}
									total++;
								}
							}

							if (total > 0) {
								calc = (matchCount / total);
							}
						}

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

					} else if (pos == null && part.pos == null) {
						// Both parts of speech are null. Likely uninterpreted
						// slag words
						// Calculate similarity

					}
					// else if ((synset != null && part.synset != null)) {
					// // These words are the same part of speech and have
					// // synsets
					// List<Word> syns = synset.getWords();
					// if (syns.contains(part.word)) {
					// // If this is a synonym, consider a complete match
					// calc = 1;
					// } else {
					// List<Word> pSyns = part.synset.getWords();
					// int synCount = syns.size() + pSyns.size();
					// int matchCount = 0;
					// // Iterate through this tags synonyms, determine if
					// // matching synonym
					// for (Word w : syns) {
					// if (pSyns.contains(w)) {
					// matchCount++;
					// }
					// }
					// for (Word pW : pSyns) {
					// if (syns.contains(pW)) {
					// matchCount++;
					// }
					// }
					//
					// // Percent overlap = number of matches/ total number
					// // of
					// // synonyms
					// calc = (matchCount / synCount);
					// }
					//
					// } // else, shouldn't compare them

				}
				// else they're not the same part of speech or anything, they
				// should not be compared

			} catch (Exception e) {
				// A multitude of issues could have happened. Fuck it.
			}

			return calc;
		}

	}

}
