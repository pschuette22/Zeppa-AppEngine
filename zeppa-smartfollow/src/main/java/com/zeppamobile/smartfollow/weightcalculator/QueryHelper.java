/**
 * @author Eric Most
 */
package com.zeppamobile.smartfollow.weightcalculator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.PointerTarget;
import net.sf.extjwnl.data.PointerType;
import net.sf.extjwnl.data.PointerUtils;
import net.sf.extjwnl.data.Synset;
import net.sf.extjwnl.data.list.PointerTargetNodeList;
import net.sf.extjwnl.data.list.PointerTargetTreeNodeList;
import net.sf.extjwnl.data.relationship.RelationshipFinder;
import net.sf.extjwnl.data.relationship.RelationshipList;
import net.sf.extjwnl.dictionary.Dictionary;

import com.zeppamobile.smartfollow.Constants;

public class QueryHelper extends PointerUtils {
	private static final String PATH_TO_DUMP_FILE = "src/main/java/com/zeppamobile/smartfollow/weightcalculator/pointer-types.txt";

	private static Dictionary dictionary;

	// Part of speech frequencies
	private static final double NOUN = 0.759;
	private static final double VERB = 0.074;
	private static final double ADJECTIVE = 0.138;
	private static final double ADVERB = 0.029;

	// Timeout and error threshold settings
	private static final long TIMEOUT = 1000;
	private static final int ERROR_THRESHOLD = 10;

	// Trials
	private static final long NUM_TRIALS = 100000;
	
	// Search depth for findRelationships()
	private static int searchDepth = 5;

	public static void main(String[] args) {
		FileWriter fw = null;
		BufferedWriter bw = null;
		PrintWriter pw = null;
		long resultCount, queryCount = 0;
		int errorCount = 0;

		// Initialize dictionary
		try {
			dictionary = Constants.getDictionary();
		} catch (JWNLException e) {
			System.err.println("Error retrieving JWNL Dictionary");
			e.printStackTrace();
		}

		try {
			IndexWord word1 = dictionary.getIndexWord(POS.NOUN, "run");
			IndexWord word2 = dictionary.getIndexWord(POS.VERB, "play");
			
			System.out.println(word1.getSenses().toString() + "\n");
			//System.out.println(word2.getSenses().toString());
			
			
			//for (PointerType pt : PointerType.HYPERNYM) {
				//System.out.println("Trying Pointer Type: " + pt.getLabel());
				System.out.println("It's " + (PointerType.HYPERNYM.isSymmetric() ? "symmetric" : "asymmetric"));
				for (Synset s1 : word1.getSenses()) {
					for (Synset s2 : word2.getSenses()) {
						RelationshipList rl = RelationshipFinder.findRelationships(s1, s2, PointerType.HYPERNYM);
	
						if (!rl.isEmpty()) {
							System.out.println("Relationship found");
						} 		
					}
				}
			//}
		} catch (Exception e) {
			e.printStackTrace();
		}
			
	

		/*
		 * // Create/load file try { File file = new File(PATH_TO_DUMP_FILE); if
		 * (!file.exists()) { file.createNewFile(); }
		 * 
		 * System.out.println("Path to file:");
		 * System.out.println(file.getAbsolutePath());
		 * 
		 * fw = new FileWriter(file.getAbsoluteFile(), true); // Append to file
		 * bw = new BufferedWriter(fw); pw = new PrintWriter(bw); } catch
		 * (IOException e) { System.err.println("Error creating file writer");
		 * e.printStackTrace(); }
		 * 
		 * try { System.out.println("Beginning search."); // Cycle through our
		 * 3D array of POS x POS x PointerType for (int i = 0; i < NUM_TRIALS;
		 * i++) { for (POS posSource : POS.getAllPOS()) { for (POS posTarget :
		 * POS.getAllPOS()) { for (PointerType pt : PointerType
		 * .getAllPointerTypesForPOS(posSource)) {
		 * System.out.print(posSource.getLabel() + " -> " + pt.getLabel() +
		 * " -> " + posTarget.getLabel() + "\r"); Synset tuple[] =
		 * getSynsets(pt, posSource, posTarget);
		 * 
		 * if (tuple != null) { // Execute google search String source =
		 * tuple[0].getWords().get(0) .getLemma(); String target =
		 * tuple[1].getWords().get(0) .getLemma();
		 * 
		 * // If the words are the same get the next word // from the target
		 * synset if (source.equalsIgnoreCase(target) &&
		 * tuple[1].getWords().size() > 1) { target = tuple[1].getWords().get(1)
		 * .getLemma(); }
		 * 
		 * resultCount = getResultsCount(source, target); queryCount++;
		 * 
		 * // Check for error if (resultCount == -1) { if (++errorCount >
		 * ERROR_THRESHOLD) {
		 * System.err.println("Maximum error threshold reached.");
		 * 
		 * return; } continue; } } else { resultCount = 0; }
		 * 
		 * // Write to file with the following tab-delimited // format: //
		 * PointerType posSource posTarget count pw.println(pt.getLabel() + "\t"
		 * + posSource.getLabel() + "\t" + posTarget.getLabel() + "\t" +
		 * resultCount); } } }
		 * 
		 * // Write results for each cycle to file in case the program //
		 * crashes or gets killed pw.flush();
		 * 
		 * if (i % 100 == 0) { System.out.println(queryCount +
		 * " queries executed."); } }
		 * 
		 * } finally {
		 * 
		 * pw.close(); }
		 */
		
		System.out.println("Exiting");
	}

	/**
	 * A PointerType connects two synsets S1->PointerType->S2 Randomly generate
	 * S1 based on POS frequencies and find S2
	 * 
	 * @param pt
	 *            - some PointerType
	 * @return a synset tuple (S1, S2) or null on timeout
	 */
	private static Synset[] getSynsets(PointerType pt) {
		Synset source, target;
		List<PointerTarget> targets;
		List<POS> validPOS = new ArrayList<POS>();
		IndexWord base;
		POS key;

		try {
			long startTime = System.currentTimeMillis();

			// Get valid parts of speech for this pointer type and choose one of
			// them at random
			// Use this to retrieve a random word
			validPOS = getApplicablePOS(pt);

			do {
				key = getRandomPOS(validPOS);
				base = dictionary.getRandomIndexWord(key);
				source = base.getSenses().get(0);
				targets = source.getTargets(pt);
			} while (targets.size() == 0
					&& System.currentTimeMillis() - startTime < TIMEOUT);

			if (targets.size() != 0) {
				target = targets.get(0).getSynset();
			} else {
				// timeout
				return null;
			}
		} catch (JWNLException e) {
			System.err.println("Error retrieving senses for PointerType: "
					+ pt.getLabel());
			e.printStackTrace();
			return null;
		}

		return new Synset[] { source, target };
	}

	/**
	 * A PointerType connects two synsets S1->PointerType->S2 Randomly generate
	 * S1 of POS source and find S2 of POS target, if it exists
	 * 
	 * @param pt
	 *            - some PointerType
	 * @param source
	 *            - S1.PartOfSpeech
	 * @param target
	 *            - S2.PartOfSpeech
	 * @return a synset tuple (S1, S2) or null on timeout (1000ms)
	 */
	private static Synset[] getSynsets(PointerType pt, POS posSource,
			POS posTarget) {
		Synset source, target;
		List<PointerTarget> targets;
		IndexWord base;
		boolean foundTargetPOS = false;

		try {
			long startTime = System.currentTimeMillis();

			do {
				base = dictionary.getRandomIndexWord(posSource);
				source = base.getSenses().get(0);
				targets = source.getTargets(pt);

				for (PointerTarget ptrTarget : targets) {
					if (ptrTarget.getPOS() == posTarget) {
						foundTargetPOS = true;
						break;
					}
				}
				// Reenter loop if we have an empty list or we have not yet
				// found a match for target POS
			} while ((targets.size() == 0 || !foundTargetPOS)
					&& System.currentTimeMillis() - startTime < TIMEOUT);

			if (targets.size() != 0) {
				target = targets.get(0).getSynset();
			} else {
				// Timeout
				return null;
			}
		} catch (JWNLException e) {
			System.err.println("Error retrieving senses for PointerType: "
					+ pt.getLabel());
			e.printStackTrace();
			return null;
		}

		return new Synset[] { source, target };
	}

	/**
	 * Get a list of applicable parts of speech for a PointerType
	 * 
	 * @param pt
	 *            - some PointerType
	 * @return a List of POS
	 */
	private static List<POS> getApplicablePOS(PointerType pt) {
		List<POS> validPOS = new ArrayList<POS>();
		for (POS pos : POS.getAllPOS()) {
			if (pt.appliesTo(pos)) {
				validPOS.add(pos);
			}
		}

		return validPOS;
	}

	/**
	 * Randomly select a pointer type from the set
	 * 
	 * @return a PointerType
	 */
	private static PointerType getRandomPointerType() {
		List<PointerType> ptrTypes = PointerType.getAllPointerTypes();
		int randIndex = (int) (Math.random() * ptrTypes.size());

		return ptrTypes.get(randIndex);
	}

	/**
	 * Selects a random part of speech from the set based on the frequency of
	 * which they appear in the Wordnet dictionary
	 * 
	 * @param set
	 *            - a set of POS to choose from
	 * @return the chosen part of speech from set
	 */
	private static POS getRandomPOS(List<POS> set) {
		POS result;
		double totalFreq = 0.0;
		double pNoun, pVerb, pAdjective, pAdverb;

		for (POS pos : set) {
			switch (pos) {
			case NOUN:
				totalFreq += NOUN;
				break;
			case VERB:
				totalFreq += VERB;
				break;
			case ADJECTIVE:
				totalFreq += ADJECTIVE;
				break;
			case ADVERB:
				totalFreq += ADVERB;
				break;
			default:
				break;
			}
		}

		pNoun = NOUN / totalFreq;
		pVerb = VERB / totalFreq;
		pAdjective = ADJECTIVE / totalFreq;
		pAdverb = ADVERB / totalFreq;

		// Randomly select a valid POS
		double rand = Math.random();
		if (rand < pNoun) {
			result = POS.NOUN;
		} else if (rand < pNoun + pVerb) {
			result = POS.VERB;
		} else if (rand < pNoun + pVerb + pAdjective) {
			result = POS.ADJECTIVE;
		} else if (rand <= pNoun + pVerb + pAdjective + pAdverb) {
			result = POS.ADVERB;
		} else { // undefined
			return null;
		}

		return result;
	}

	/**
	 * Execute a google search and scrape the results count
	 * 
	 * @param word1
	 *            - first word to search
	 * @param word2
	 *            - second word to search
	 * @return number of results or -1 on error
	 */
	private static long getResultsCount(final String word1, final String word2) {
		try {
			// Attempt to create the URL
			String query = word1 + " " + word2;
			final URL url = new URL("https://www.google.com/search?q="
					+ URLEncoder.encode(query, "UTF-8"));
			// System.out.println(url.toString());
			final URLConnection connection = url.openConnection();

			// Set connection properties
			connection.setConnectTimeout(60000);
			connection.setReadTimeout(60000);
			connection.addRequestProperty("User-Agent", "Mozilla/5.0");

			// Set up reader
			BufferedReader input = new BufferedReader(new InputStreamReader(
					connection.getInputStream(), "UTF-8"));

			String line;
			// Get a line of HTML
			while ((line = input.readLine()) != null) {
				// Check for stats
				if (!line.contains("id=\"resultStats\"")) {
					continue;
				}

				// Split line into tokens by spaces
				String[] tokens = line.split(" ");
				for (int i = 1; i < tokens.length - 1; i++) {
					if (tokens[i - 1].contains("About")
							&& tokens[i + 1].contains("results")) {
						// 99.9% we have the right token here
						// Delete commas
						String num = tokens[i].replaceAll(",", "");
						try {
							return Long.parseLong(num);
						} catch (NumberFormatException nfe) {
							System.err.println("Error parsing web data");
							System.err.println("Input: " + num);
							nfe.printStackTrace();
							return -1;
						}
					}
				}
			}
		} catch (IOException ie) {
			System.err.println("Error reading web page");
			ie.printStackTrace();
		} catch (Exception e) {
			System.err.println("Uncaught exception");
			e.printStackTrace();
		}

		return -1;
	}
	
}
