package com.zeppamobile.smartfollow.task;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.IndexWordSet;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.PointerType;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.relationship.RelationshipFinder;
import net.sf.extjwnl.data.relationship.RelationshipList;

import com.zeppamobile.smartfollow.Constants;
import com.zeppamobile.smartfollow.Utils;
import com.zeppamobile.smartfollow.comparewords.WordInfo;

/**
 * 
 * @author PSchuette
 * 
 *         This object is used to compare two words and analyze relevant info
 * 
 */
public class CompareWordsTask extends SmartFollowTask {

	/**
	 * First word used in comparison
	 */
	private WordInfo sourceWord;

	/**
	 * Second word used in comparison
	 */
	private WordInfo targetWord;

	/**
	 * Similarity calculated from this task
	 */
	private double similarity = 0; // [0,1]

	/**
	 * Weight of this similarity calculation
	 */
	private double weight = 0; // [0,1] but mostly small values, we scale in
								// TagAgent.

	private double defaultWeight = 0.01;

	/**
	 * Create a task to be used to compare words
	 * 
	 * @param sourceWords
	 * @param targetWords
	 */
	public CompareWordsTask(ServletContext context, WordInfo sourceWord, WordInfo targetWord) {
		super(context, "CompareWordsTask");
		this.sourceWord = sourceWord;
		this.targetWord = targetWord;
	}

	/**
	 * Get the task's calculated similarity where the tasks weight represents
	 * 100%
	 * 
	 * @return
	 */
	public double getSimilarity() {
		return similarity;
	}

	/**
	 * Get the task's PointerType weight for comparing this words similarity
	 * relative to others
	 * 
	 * @return
	 */
	public double getWeight() {
		return weight;
	}

	/**
	 * Execute task comparing two words
	 */
	public void execute() {

		// first check to see if they're the same part of speech
		try {
			if (sourceWord.getPos() == null && targetWord.getPos() == null) {
				// Both null
				// If they're similar enough, we'll compare
				// Get the first char to make comparison easier
				char ch1 = sourceWord.getWord().charAt(0);
				char ch2 = targetWord.getWord().charAt(0);
				if (!Character.isLetterOrDigit(ch1)
						&& !Character.isLetterOrDigit(ch2)) {
					// This might be absolutely worthless

					// identify symbols
					// Only compare if they're multiple characters
					if (sourceWord.getWord().length() > 1
							|| targetWord.getWord().length() > 1) {
						// similarity = Utils.stringSimilarityCalculation(word,
						// part.word);
						// Set the character similarities
					}

				} else if (Character.isDigit(ch1) && Character.isDigit(ch2)) {
					// // Two numbers. Try to cast them
					// int i1 = Integer.valueOf(word);
					// int i2 = Integer.valueOf(part.word);
					// // Average them. If it was a year, could be relatively
					// // similar
					// // The smaller the difference, the higher the similarity
					// // 2015 and 2016 have 4030/4031 similarity
					// // 1 and 100 have 2/101
					// // 2 and 3 have 4/5
					// calc = 1 - (Math.abs(i1 - i2) / (i1 + i2));
				} else if (Character.isLetter(ch1) && Character.isLetter(ch2)) {
					// // Two undefined words
					// if (word.length() > 4 && part.word.length() > 4) {
					// // Don't compare words that aren't at least 5
					// // letters
					// // this eliminates words like 'the' and 'that'
					// calc = Utils.stringSimilarityCalculation(word,
					// part.word);
					// }

				} // else, they're not similar at all

			} else if (sourceWord.getIndexWord() == null
					|| targetWord.getIndexWord() == null) {
				// One has defined part of speech and the other does not
				// Ignore this case
			} else {

				// Fetch the relevant pointer types for these index words
				List<PointerType> relevantTypes = new ArrayList<PointerType>();

				int sourceIndex = Utils.getPOSIndex(sourceWord.getPos());
				int targetIndex = Utils.getPOSIndex(targetWord.getPos());
				for (PointerType type : PointerType.getAllPointerTypes()) {
					int typeIndex = Utils.getPointerTypeIndex(type);
					// Check to see if there are counts this pointer type
					// between these parts of speech
					if (type.appliesTo(sourceWord.getPos())
							&& type.appliesTo(targetWord.getPos())
							&& Constants.POINTER_COUNTS_BY_POS[typeIndex][sourceIndex][targetIndex] > 0) {
						relevantTypes.add(type);
					}
				}

				// Iterate through IndexWord for relevant pointer types
				compareIndexWordsForPointerTypes(sourceWord.getIndexWord(),
						targetWord.getIndexWord(), relevantTypes);

				// If there was nothing found, try again with index word sets
				if (weight == 0) {
					IndexWordSet sourceSet = sourceWord.getIndexWordSet();
					IndexWordSet targetSet = targetWord.getIndexWordSet();
					compareIndexWordSets(sourceSet, targetSet);

					System.out
							.println("Found relationship using IndexWordSets of"
									+ " similarity "
									+ similarity
									+ " and weight " + weight);
				}

			}

		} catch (Exception e) {

			// A multitude of issues could have happened.
			e.printStackTrace();
			// log("Exception");
		}

	}

	/**
	 * Iterate through other forms of the word and see if they return a
	 * relationship
	 * 
	 * @param sourceSet
	 * @param targetSet
	 * @throws CloneNotSupportedException
	 * @throws JWNLException
	 */
	private void compareIndexWordSets(IndexWordSet sourceSet,
			IndexWordSet targetSet) throws CloneNotSupportedException,
			JWNLException {
		for (IndexWord source : sourceSet.getIndexWordCollection()) {
			for (IndexWord target : targetSet.getIndexWordCollection()) {
				// Only compare like parts of speech
				if (source.getPOS().equals(target.getPOS())) {
					List<PointerType> relevantTypes = PointerType
							.getAllPointerTypesForPOS(source.getPOS());
					if (!relevantTypes.isEmpty()) {
						compareIndexWordsForPointerTypes(source, target,
								relevantTypes);
					}
				}
			}
		}

	}

	/**
	 * Compare two indexed words based on a relationships of given pointer types
	 * 
	 * @param word1
	 * @param word2
	 * @param pointerTypes
	 * @return calculated similarity as a decimal between 0 and 1
	 * @throws JWNLException
	 * @throws CloneNotSupportedException
	 */
	private void compareIndexWordsForPointerTypes(IndexWord word1,
			IndexWord word2, List<PointerType> pointerTypes)
			throws CloneNotSupportedException, JWNLException {
		System.out.println("Comparing " + word1.getLemma() + " ("
				+ word1.getPOS().getLabel() + ") to " + word2.getLemma() + " ("
				+ word2.getPOS().getLabel() + ")");

		// Check to see if they are the same
		if (word1.equals(word2)) {
			similarity = 1;
			weight = defaultWeight;
			return;
		}

		// Check to see if there is an immediate relationship (this word is a
		// synonym of the other in some sense)
		if (RelationshipFinder.getImmediateRelationship(word1, word2) >= 0) {
			System.out.println("Found immediate relationship");
			similarity = 0.9;
			weight = defaultWeight;

			return;
		}

		List<Synset> set1 = word1.getSenses();
		List<Synset> set2 = word2.getSenses();

		if (set1.isEmpty() || set2.isEmpty() || pointerTypes.isEmpty()) {
			System.out.println("A synset or pointer types are missing");
			// If there are no synonyms or relevant pointer types, cannot
			// compare
			return; // Exit without doing any calculations
		} else {

			/*
			 * Otherwise, perform calculations
			 */
			System.out.println("Iterating through synsets, size = "
					+ set1.size() + "X" + set2.size());

			// Set these thresholds to the existing values (originally 0)
			// so that in the case where we iterate over the IndexWordSet
			// we end up selecting the strongest relationship
			double maxWeightedSimilarity = similarity * weight;
			double maxSimilarity = similarity;
			double maxWeight = weight;

			/*
			 * Iterate through all pointer types and try to find relationships
			 * Calculate strength of these relationships
			 */
			for (PointerType type : pointerTypes) {
				double pointerWeight = getPointerTypeWeight(type,
						word1.getPOS(), word2.getPOS());

				// Iterate through both lists and figure out the max similarity
				for (Synset s1 : word1.getSenses()) {
					for (Synset s2 : word2.getSenses()) {

						// Find the relationship between synsets
						RelationshipList relationships = RelationshipFinder
								.findRelationships(s1, s2, type);

						// If there were relationships established, calculate
						// their similarity
						if (!relationships.isEmpty()) {
							// Weight of this calculation

							double shallowDepth = relationships.getShallowest()
									.getSize();

							System.out.println("Found relationship for type "
									+ type.getLabel()
									+ " with shallow depth of " + shallowDepth);

							double similarityCalc = Math.pow(.9, shallowDepth);

							System.out.println("Similarity calculated: "
									+ similarityCalc + ", with weight: "
									+ pointerWeight);

							// Discovered a relationship with higher
							// similarity, reassign values
							if ((similarityCalc * pointerWeight) > maxWeightedSimilarity) {
								maxWeightedSimilarity = similarityCalc
										* pointerWeight;
								maxSimilarity = similarityCalc;
								maxWeight = pointerWeight;
							}
						}
					}
				}
			}

			// If we determined there is a calculated similarity between these
			// words that is greater than the current Relationship, replace
			if (maxWeightedSimilarity > similarity * weight) {
				similarity = maxSimilarity;
				weight = maxWeight;
			} else {
				System.out.println("No relationship found");

				// If not, dock it
				// TODO: if no similarity is found, add to the
				// calculation weight without adding to the weighted
				// similarity
				// System.out.println("adding weight without similarity");
				// weight += .01;
			}

			System.out.println("Similarity between " + word1.getLemma()
					+ " and " + word2.getLemma() + " is " + similarity
					+ " with weight " + weight);

		}

	}

	/**
	 * Get the calculation weight of the pointer type between these parts of
	 * speech
	 * 
	 * @param type
	 *            - pointer type between these synonyms
	 * @param source
	 *            - source word part of speech
	 * @param target
	 *            - target word part of speech
	 * @return calculation weight for determining similarity
	 */
	private double getPointerTypeWeight(PointerType type, POS source, POS target) {
		int typeIndex = Utils.getPointerTypeIndex(type);
		int sourceIndex = Utils.getPOSIndex(source);
		int targetIndex = Utils.getPOSIndex(target);
		double sourceCounts = Constants.POINTER_COUNTS_BY_POS[typeIndex][sourceIndex][targetIndex];
		// double targetCounts =
		// Constants.POINTER_COUNTS[typeIndex][targetIndex][sourceIndex];
		double pointerCounts = Constants.TOTAL_POINTER_COUNT;
		// Get the counts pointing both ways because target and source are not
		// guaranteed to be ordered
		return sourceCounts / pointerCounts;
	}

	@Override
	public void finalize() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String abort(boolean doResume) {
		// TODO Auto-generated method stub
		return null;
	}

}
