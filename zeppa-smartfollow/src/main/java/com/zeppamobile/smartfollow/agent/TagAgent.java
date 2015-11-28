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

import org.apache.tools.ant.types.selectors.DepthSelector;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.IndexWordSet;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.PointerType;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.list.PointerTargetNode;
import net.sf.extjwnl.data.list.PointerTargetNodeList;
import net.sf.extjwnl.data.relationship.Relationship;
import net.sf.extjwnl.data.relationship.RelationshipFinder;
import net.sf.extjwnl.data.relationship.RelationshipList;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

import com.zeppamobile.common.datainfo.EventTagInfo;
import com.zeppamobile.common.report.SmartfollowReport;
import com.zeppamobile.smartfollow.Constants;
import com.zeppamobile.smartfollow.Utils;
import com.zeppamobile.smartfollow.nlp.POSFactory;

/**
 * 
 * @author Pete Schuette
 * 
 *         This agent is used to calculate the similarity between Tags It
 *         employs Java's Word Net Library (Princeton) and OpenNLP (Source
 *         Forge)
 *
 */
public class TagAgent extends BaseAgent {

	private EventTagInfo tag;
	private List<String> convertedTagWords = new ArrayList<String>();
	private String[] posTags;

	// Parse the tag
	private List<TagPart> parsedTagParts = new ArrayList<TagPart>();

	public TagAgent(ServletContext context, EventTagInfo tag,
			SmartfollowReport report) {
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

	public EventTagInfo getTagInfo() {
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
		// First, compound text to list of words, numbers and character strings
		List<String> stringList = Utils.convertTextToStringList(text);

		// Convert to dictionary words and add to list of words
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
		POSModel model = factory.buildPOSModel();

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
		log("Calculating similarity");
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

						log("Comparing " + p.word + " to " + p2.word);

						double sim = p.calculatedMatch(p2);
						// If we can calculate their similarity with some degree
						// of certainty,
						// add and average
						log("Calculated similarity: " + sim);
						if (sim >= 0) {
							// Tags have made a valid comparison of content

							// Quickly average adding this calculation
							double matchWeight = p.getCalculatedMatchWeight(p2);
							similarity += sim * matchWeight;
							similarityWeight += matchWeight;

							log("Total similarity " + similarity);
							log("Total similarity weight " + similarityWeight);

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
				return (getPOSWeight(pos) * getPOSWeight(part.pos) * .5);
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
				return .6;
			} else if (pos.equals(POS.ADVERB)) {
				return .3;
			} else if (pos.equals(POS.ADJECTIVE)) {
				return .5;
			} else
				return .2;
		}

		/**
		 * Calculate the weight of comparing one part of speech to another
		 * 
		 * @param otherPOS
		 * @return
		 */
		private double getPOSComparisonWeight(POS otherPOS) {

			// Make sure the parts of speech are comparable
			if (pos != null && otherPOS != null) {
				switch (pos) {
				case NOUN:
					switch (otherPOS) {
					case NOUN:

					case VERB:

					case ADJECTIVE:

					case ADVERB:
					}
					break;

				case VERB:
					switch (otherPOS) {
					case NOUN:

					case VERB:

					case ADJECTIVE:

					case ADVERB:
					}
					break;

				case ADJECTIVE:
					switch (otherPOS) {
					case NOUN:

					case VERB:

					case ADJECTIVE:

					case ADVERB:
					}
					break;

				case ADVERB:
					switch (otherPOS) {
					case NOUN:

					case VERB:

					case ADJECTIVE:

					case ADVERB:
					}
					break;

				}
			}

			return .1;

		}

		/**
		 * Determine the weight of pointer relationships to enhance the accuracy
		 * of the tag comparison calculation
		 * 
		 * @param type
		 *            - Pointer Type for a given relationship
		 * @return calculation weight of this pointer
		 */
		private double getPointerTypeWeight(PointerType type) {
			switch (type) {

			case ANTONYM:
				/*
				 * Antonyms are opposites. With negative weight, opposite of
				 * opposite ex: Happy to Sad
				 */
				return -.75;

			case HYPERNYM:
			case HYPONYM:
			case INSTANCE_HYPERNYM:
			case INSTANCES_HYPONYM:
				/*
				 * Hypernym/ Hyponym mean the same thing, just opposite
				 * directions This means relationship is a group member to group
				 * type ex: Cutlery to Spoon
				 */
				return .8;

			case ENTAILMENT:
				/*
				 * An entailment is a deduction or implication, that is,
				 * something that follows logically from or is implied by
				 * something else. In logic, an entailment is the relationship
				 * between sentences whereby one sentence will be true if all
				 * the others are also true.
				 */
				return .55;

			case SIMILAR_TO:
				/*
				 * This is only used to compare adjectives. One adjective is
				 * similar to another. High weight. Ex: Happy to Glad
				 */
				return .95;

			case MEMBER_HOLONYM:
			case SUBSTANCE_HOLONYM:
			case PART_HOLONYM:
			case MEMBER_MERONYM:
			case SUBSTANCE_MERONYM:
			case PART_MERONYM:
				/*
				 * Holonym/Meronym are the same, just on different sides of the
				 * equation Holonymy (in Greek ὅλον holon, "whole" and ὄνομα
				 * onoma, "name") is a semantic relation. Holonymy defines the
				 * relationship between a term denoting the whole and a term
				 * denoting a part of, or a member of, the whole. That is, 'X'
				 * is a holonym of 'Y' if Ys are parts of Xs, or.
				 */
				return .9;

			case CAUSE:
				/*
				 * Cause only applies to verbs; one leads to the other. ex.
				 * joking causes laughing
				 */
				return .6;

			case PARTICIPLE_OF:
				/*
				 * a word formed from a verb (e.g., going, gone, being, been )
				 * and used as an adjective (e.g., working woman, burned toast )
				 * or a noun (e.g., good breeding ). In English, participles are
				 * also used to make compound verb forms (e.g., is going, has
				 * been ).
				 */
				return .85;

			case SEE_ALSO:
				/*
				 * In a dictionary, when a word is part of the definition. ex.
				 * Plane is used in definition of Air Port
				 */
				return .78;

			case PERTAINYM:
				/*
				 * (plural pertainyms) (computational linguistics) a word,
				 * usually an adjective, which can be defined as
				 * "of or pertaining to" another word.
				 */
				return .7;

			case ATTRIBUTE:
				/*
				 * 1 : an inherent characteristic; also : an accidental quality
				 * 2 : an object closely associated with or belonging to a
				 * specific person, thing, or office <a scepter is the attribute
				 * of power>; especially : such an object used for
				 * identification in painting or sculpture 3 : a word ascribing
				 * a quality; especially : adjective
				 */
				return .95;

			case VERB_GROUP:
				/*
				 * countable noun. A verb group or verbal group consists of a
				 * verb, or of a main verb following a modal or one or more
				 * auxiliaries. Examples are 'walked', 'can see', and 'had been
				 * waiting'.
				 */
				return .65;

			case DERIVATION:
				/*
				 * 1. the obtaining or developing of something from a source or
				 * origin. "the derivation of scientific laws from observation"
				 * synonyms: deriving, induction, deduction, inference; More 2.
				 * LINGUISTICS in generative grammar, the set of stages that
				 * link the abstract underlying structure of an expression to
				 * its surface form.
				 */
				return .95;

			case DOMAIN_ALL:
			case MEMBER_ALL:
				/*
				 * Domain/ Member are the same in opposite directions Check to
				 * see if Noun is a member of a domain
				 */
				return .6;

			case CATEGORY:
			case CATEGORY_MEMBER:

				/*
				 * 
				 * 1. a class or division of people or things regarded as having
				 * particular shared characteristics.
				 * "five categories of intelligence" synonyms: class,
				 * classification, group, grouping, bracket, heading, set; More
				 * 2. PHILOSOPHY one of a possibly exhaustive set of classes
				 * among which all things might be distributed.
				 */
				return .75;

			case USAGE:
			case USAGE_MEMBER:
				/*
				 * the way that words are used by people when they speak and
				 * write their language
				 */
				return .85;

			case REGION:
			case REGION_MEMBER:
				/*
				 * (Unsure if this is proper definition) Definition of
				 * LINGUISTIC GEOGRAPHY. : local or regional variations of a
				 * language or dialect studied as a field of knowledge —called
				 * also dialect geography. — linguistic geographer noun.
				 */
				return .75;

			default:
				// Just to keep compiler happy
				return 0;
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
							log(indexWord.getLemma() + " and "
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

						// } else if (pos == POS.NOUN) {
						// // Couple of Nouns without index check for similarity
						//
						// /*
						// *
						// * Only Calculate similarity by degrees of separation
						// * for longer nouns. If it is 3 letters or less, it's
						// * likely an acronym or something like "me" and leaves
						// * too much margin for error.
						// */
						// if (word.length() > 3 && part.word.length() > 3) {
						// calc = 0;
						// if (word.contains(part.word)
						// || part.word.contains(word)) {
						// // One word contains the other. They're probably
						// // relatively similar
						// calc += .4;
						// }
						//
						//
						// double stringSimilarity =
						// Utils.stringSimilarityCalculation(word,
						// part.word);
						// if(stringSimilarity > .2){
						// calc += (1 - calc)
						// * stringSimilarity;
						// }
						//
						// if(calc < .2){
						// calc = -1;
						// }
						//
						// }

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
						// If there is no similarity here, try comparing the
						// words with other relevant parts of speech
						/*
						 * If the index word sets for each word lemma are null,
						 * look them up
						 */
						if (indexWordSet == null) {
							indexWordSet = Constants.getDictionary()
									.lookupAllIndexWords(indexWord.getLemma());
						}

						if (part.indexWordSet == null) {
							part.indexWordSet = Constants.getDictionary()
									.lookupAllIndexWords(
											part.indexWord.getLemma());
						}

						// Return the comparison
						return compareIndexWordSets(indexWordSet,
								part.indexWordSet);

					}

				}

			} catch (Exception e) {

				// A multitude of issues could have happened.
				e.printStackTrace();
				log("Exception");
			}

			return calc;
		}

		/**
		 * Calculate other forms of this words similarity; This is only used to
		 * compare two words which do not have a matching part of speech. To
		 * counter this, we try to find synonyms with matching parts of speech
		 * 
		 * @param wordSet1
		 *            - synonym set of one tag words
		 * @param wordSet2
		 *            - synonym set of other tag words
		 * @return calculated similarity as a decimial percent or -1 if
		 *         calculation couldn't be made
		 */
		private double compareIndexWordSets(IndexWordSet wordSet1,
				IndexWordSet wordSet2) {
			double similarity = -1;

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
				
				double weightedSimilaritySum = 0;
				double weightSum = 0;

				// Iterate through both lists and figure out the highest
				// similarity
				for (int i = 0; i < set1.size() && i < 5; i++) {
					Synset s1 = set1.get(i);
					for (int j = 0; j < set2.size() && j < 5; j++) {
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

							// double relationshipWeight = getPOSWeight(s1
							// .getPOS()) * getPOSWeight(s2.getPOS());
							double pointerTypeWeight = getPointerTypeWeight(type);
							double similarityCalc = calculatedRelationshipsListStrength(
									relationships, pointerTypeWeight);

							weightedSimilaritySum += (similarityCalc*pointerTypeWeight);
							weightSum+=pointerTypeWeight;

						}
						double synsetSimilarity = weightedSimilaritySum/weightSum;
						// Compounding percent of similarities
						similarity+=((1-similarity)*synsetSimilarity);

						// Or only return highest synset similarity calculation
//						// Only take the highest similarity calculation
//						if (similarity < synsetSimilarity) {
//							similarity = synsetSimilarity;
//						}

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

			// log("Calculating relationship list strength");
			try {

				/*
				 * Quick loop to find the lowest depth of a relationship
				 */
				// Total number of relationships
				double relationshipCount = relationships.size();
				// Most shallow relationship count
				double minDepth = relationships.getShallowest().getDepth();
				// Deepest relationship count
				double maxDepth = relationships.getDeepest().getDepth();

				// Calculates the range
				double range = minDepth - maxDepth;

				double adjustedWeight = 0;
				if (range == 0) {
					/*
					 * In the event that the minimum and maximum depth is the
					 * same, there is no point in further computation
					 */
					// Max and min depth are the same
					adjustedWeight += Math.pow(relationshipWeight, minDepth);
				} else {

					// Sum of all relationship depths
					double sumDepth = 0;
					/*
					 * Quickly iterate through all relationships adding depth to
					 * sum of all depths
					 */
					Iterator<Relationship> iterator = relationships.iterator();
					while (iterator.hasNext()) {
						sumDepth += iterator.next().getDepth();
					}
					// Average depth
					double averageDepth = sumDepth / relationshipCount;

					/*
					 * Hold the range difference between average depth and
					 * max/min to identify most common ranges
					 */
					double minDiff = averageDepth - minDepth;
					double maxDiff = maxDepth - averageDepth;

					/*
					 * calculate weight based on dispercement
					 */
					double minShare = minDiff / range;
					double maxShare = maxDiff / range;

					// Adjust the weight and displace based on depth and
					// displacement of depth
					adjustedWeight += Math.pow(relationshipWeight, minDepth)
							* minShare;
					adjustedWeight += Math.pow(relationshipWeight, maxDepth)
							* maxShare;
				}

				/*
				 * Adjust total strength to account for the number of
				 * relationships there are. This is useful if there are a bunch
				 * of distant relationships
				 */
				for (int i = 0; i < relationshipCount; i++) {
					strength += adjustedWeight * (1 - strength);
				}

			} catch (NullPointerException e) {
				// Bad list was passed in, no strength
			}

			return strength;
		}

		/**
		 * 
		 * @param relationship
		 */
		private void printPointerRelationshipInfo(Relationship relationship) {
			log("=======================");
			log(relationship.getType().toString()
					+ " relationship with depth: " + relationship.getDepth());
			PointerTargetNodeList nodes = relationship.getNodeList();
			Iterator<PointerTargetNode> iterator = nodes.iterator();
			log("Pointer Target Nodes: ");
			while (iterator.hasNext()) {
				PointerTargetNode n = iterator.next();
				log("	" + n.getPointerTarget().toString());

			}

		}

	}

}
