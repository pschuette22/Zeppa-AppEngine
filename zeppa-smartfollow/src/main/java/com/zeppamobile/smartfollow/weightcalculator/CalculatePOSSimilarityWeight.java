package com.zeppamobile.smartfollow.weightcalculator;

import java.io.File;
import java.io.PrintStream;
import java.util.Iterator;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.data.IndexWord;
import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.dictionary.Dictionary;

import com.zeppamobile.smartfollow.Constants;

import de.tudarmstadt.ukp.jwktl.JWKTL;
import de.tudarmstadt.ukp.jwktl.api.IWiktionaryEdition;
import de.tudarmstadt.ukp.jwktl.api.IWiktionaryEntry;
import de.tudarmstadt.ukp.jwktl.api.IWiktionaryPage;
import de.tudarmstadt.ukp.jwktl.api.PartOfSpeech;
import de.tudarmstadt.ukp.jwktl.api.util.Language;

public class CalculatePOSSimilarityWeight {

	/*
	 * These indicate the paths to files of wiktionary pages
	 */
	private final String PATH_TO_DUMP_FILE = "set-path-to-dump-file";
	private final String TARGET_DIRECTORY = "WikiDictionaryDirectory";

	/*
	 * These point to relevant wiktionary files
	 */
	private File dumpFile;
	private File outputDirectory;

	/*
	 * Keep track of the total number of index words evaluated by pos
	 */
	private float[] indexWordCounts;

	/*
	 * POS mapping counts
	 */
	private float[][] definitionWordCounts;

	/**
	 * This is a task object. It calculates similarity weight of different parts
	 * of speech
	 * 
	 */
	public CalculatePOSSimilarityWeight(int initialze) {

		// 5 indexes to smoothly handle unidentified POS
		definitionWordCounts = new float[4][4];
		indexWordCounts = new float[4];

		// zero out arrays
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				definitionWordCounts[i][j] = initialze;
			}
			indexWordCounts[i] = initialze;
		}

	}

	// Setup the wiki pages
	public void setUp() {
		dumpFile = new File(PATH_TO_DUMP_FILE);
		outputDirectory = new File(TARGET_DIRECTORY);

		// Just in case error occurs and we want to pick up where we left off
		boolean overwriteExisting = false;

		JWKTL.parseWiktionaryDump(dumpFile, outputDirectory, overwriteExisting);
	}

	/**
	 * Execute the task of counting dictionary references
	 * 
	 * @throws JWNLException
	 */
	public void execute() throws JWNLException {

		IWiktionaryEdition wkt = JWKTL.openEdition(dumpFile);
		Dictionary d = Constants.getDictionary();

		// Do Noun Calculations
		doCalculationsForPOS(wkt, d, POS.NOUN);
		// Do Verb Calculations
		doCalculationsForPOS(wkt, d, POS.VERB);
		// Do Adjective Calculations
		doCalculationsForPOS(wkt, d, POS.ADJECTIVE);
		// Do Adverb Calculations
		doCalculationsForPOS(wkt, d, POS.ADVERB);

		// Free up some resources
		wkt.close();

	}

	/**
	 * Print the results of the test calculation
	 * 
	 * @param printer
	 *            - print stream to print results to
	 */
	public void printFormattedResults(PrintStream printer) {

		float indexWordCount = getTotalIndexWordCount();
		float definitionWordCount = getTotalDefinitionWordCount();

		printer.println("Printing result of definition index counting:\n");
		printer.println("total index words evaluated: " + indexWordCount);
		printer.println("total definition words evaluated: "
				+ definitionWordCount);
		printer.println("Average definition words per index word: "
				+ definitionWordCount / indexWordCount);

		printer.print("\n **** Part of Speech Definition Word Count: **** \n");

		// Print out top headers
		printer.print(String.format("%-16s", "Part of Speech"));
		for (int i = 0; i < 5; i++) {
			printer.format("%20s", " " + getPOSFromIndex(i));
		}
		printer.println();

		// Print a line between each row and keep a minimum of 2 spaces between
		// rows
		for (int i = 0; i < 4; i++) {
			printer.format("%-16s", getPOSFromIndex(i));

			for (int j = 0; j < 4; j++) {
				printer.format(" %18e ", definitionWordCounts[i][j]);
			}

			try {
				printer.format(" %18e ", getTotalDefinitionWordCountForPOS(i));
			} catch (IrrelevantException e) {
				// Won't happen, there to make compiler happy
			}
			printer.print("\n\n");
		}

		// print totals
		printer.format("%-16s", "Total");
		for (int i = 0; i < 4; i++) {
			try {
				printer.format(" %18e ", getTotalPOSCountForDefinitionWords(i));
			} catch (IrrelevantException e) {
				// Won't happen, here to make compiler happy
			}
		}

		printer.print("\n\n\n **** Part of Speech Index Word Count: **** \n");
		// First section is to be 16 characters float with
		// Each following section is 20 characters with 2 leading spaces and 2
		// trailing
		// Each section is separated by a '|'

		// Print out top headers
		printer.format("%-16s", "Part of Speech");
		for (int i = 0; i < 5; i++) {
			printer.print(String.format("%20s", " " + getPOSFromIndex(i)));
		}
		printer.println();
		printer.format("%-16s", "Count");
		for (int i = 0; i < 4; i++) {
			printer.format(" %18e ", indexWordCounts[i]);
		}
		printer.format(" %18e ", indexWordCount);

		printer.print("\n\n\n **** POS Definition Word POS Counts / Total POS Definition Words: **** \n");
		// First section is to be 16 characters float with
		// Each following section is 20 characters with 2 leading spaces and 2
		// trailing
		// Each section is separated by a '|'

		// Print out top headers
		printer.format("%-16s", "Part of Speech");
		for (int i = 0; i < 4; i++) {
			printer.format("%20s", " " + getPOSFromIndex(i));
		}
		printer.println();

		// print 5 lines for each
		// first is blank
		// second has numerator
		// third has denominator
		// forth has fraction
		// fifth is blank
		for (int i = 0; i < 4; i++) {

			try {
				float defWordsForPOS = getTotalDefinitionWordCountForPOS(i);

				printer.format("%-16s", getPOSFromIndex(i));
				for (int j = 0; j < 4; j++) {
					printer.format(" %18e ", definitionWordCounts[i][j]
							/ defWordsForPOS);
				}

			} catch (IrrelevantException e1) {
				// TODO Auto-generated catch block
				printer.print("Error fetching definition word count");
			}
			printer.print("\n\n");
		}

		printer.print("\n\n\n **** POS Definition Word POS Counts / Total Definition Words: ****\n");
		// First section is to be 16 characters float with
		// Each following section is 20 characters with 2 leading spaces and 2
		// trailing
		// Each section is separated by a '|'

		// Print out top headers
		printer.format("%-16s", "Part of Speech");
		for (int i = 0; i < 5; i++) {
			printer.format("%20s", " " + getPOSFromIndex(i));
		}
		printer.println();

		// print 5 lines for each
		// first is blank
		// second has numerator
		// third has denominator
		// forth has fraction
		// fifth is blank
		for (int i = 0; i < 4; i++) {

			try {

				printer.format("%-16s", getPOSFromIndex(i));
				for (int j = 0; j < 4; j++) {
					printer.format(" %18e ", definitionWordCounts[i][j]
							/ definitionWordCount);
				}

				printer.format(" %18e ", getTotalDefinitionWordCountForPOS(i)
						/ definitionWordCount);

			} catch (IrrelevantException e1) {
				// TODO Auto-generated catch block
				printer.print("Error fetching definition word count");
			}
			printer.print("\n\n");
		}

		printer.format("%-16s", "Part of Speech");
		for (int i = 0; i < 4; i++) {
			try {
				printer.format(" %18e ", getTotalPOSCountForDefinitionWords(i)
						/ definitionWordCount);
				
			} catch (IrrelevantException e) {
				// Won't happen, just here to make compiler happy;
			}
		}
		printer.print("\n\n\n");

	}

	/**
	 * Do counting for one of the relevant parts of speech
	 * 
	 * @param d
	 *            - dictionary used for counting
	 * @param pos
	 *            - part of speech to evaluate
	 */
	private void doCalculationsForPOS(IWiktionaryEdition wkt, Dictionary d,
			POS pos) throws JWNLException {

		// Get every index word in the working dictionary
		Iterator<IndexWord> i = d.getIndexWordIterator(pos);

		while (i.hasNext()) {

			IndexWord w = i.next();
			// increment total word counter

			try {
				// increment index word count for proper index
				indexWordCounts[getPOSIndex(w.getPOS())]++;

				try {
					// try to grab the wikipage for this word
					IWiktionaryPage page = wkt.getPageForWord(w.getLemma());

					// iterate through the entries to see entry words by part of
					// speechS
					Iterator<IWiktionaryEntry> iterator = page.getEntries()
							.iterator();
					while (iterator.hasNext()) {
						IWiktionaryEntry entry = iterator.next();
						// Filter out non-English entries
						if (entry.getWordLanguage() == Language.ENGLISH
								&& addCalc(pos, entry.getPartOfSpeech())) {
							// Calculation was successfully added,
						}

					}

				} catch (Exception e) {
					// note the error
				}

			} catch (IrrelevantException e1) {
				// If this POS is irrelevant, skip everything.
				// Shouldn't ever happen
				// TODO: flag and evaluate this?
			}

		}

	}

	/**
	 * Quickly increment the counter array
	 * 
	 * @param wordPOS
	 *            - word being defined's part of speech
	 * @param defPOS
	 *            - word in definition's part of speech
	 * @return true if calculation was incremeneted
	 */
	private boolean addCalc(POS wordPOS, PartOfSpeech defPOS) {
		try {
			definitionWordCounts[getPOSIndex(wordPOS)][getPartOfSpeechIndex(defPOS)]++;
			return true;
		} catch (IrrelevantException e) {
			// If we don't care about this part of speech, don't bother with it
			return false;
		}
	}

	/**
	 * Get the calculation array's POS index. This method is the same as
	 * getPartOfSpeechIndex but should be used for wordnet library
	 * 
	 * @param pos
	 * @return row or column index for part of speeches calculation index
	 * @throws IrrelevantException
	 */
	private int getPOSIndex(POS pos) throws IrrelevantException {
		switch (pos) {
		case NOUN:
			return 0;
		case VERB:
			return 1;
		case ADJECTIVE:
			return 2;
		case ADVERB:
			return 3;
		default:
			throw new IrrelevantException();
		}
	}

	/**
	 * Get the calculation array's PartOfSpeech index. This method is the same
	 * as getPOSIndex but should be used for wiki library
	 * 
	 * @param partOfSpeech
	 * @return row or column index for part of speeches calculation index
	 * @throws IrrelevantException
	 */
	private int getPartOfSpeechIndex(PartOfSpeech partOfSpeech)
			throws IrrelevantException {
		switch (partOfSpeech) {
		case NOUN:
			return 0;
		case VERB:
			return 1;
		case ADJECTIVE:
			return 2;
		case ADVERB:
			return 3;
		default:
			throw new IrrelevantException();

		}
	}

	/**
	 * Get a human readable string of the part of speech associated with a given
	 * index
	 * 
	 * @param index
	 *            - indicating part of speech
	 * @return part of speech - as human readable string
	 * @throws IrrelevantException
	 *             - when index is out of (0,3) range
	 */
	private String getPOSFromIndex(int index) {
		switch (index) {
		case 0:
			return "Noun";
		case 1:
			return "Verb";
		case 2:
			return "Adjective";
		case 3:
			return "Adverb";
		case 4:
			return "Total";
		default:
			// shouldn't happen
			return "!ERROR!";
		}

	}

	/**
	 * count the total number of index words evaluated
	 * 
	 * @return count
	 */
	private float getTotalIndexWordCount() {
		float result = 0;
		for (int i = 0; i < 4; i++) {
			result += indexWordCounts[i];
		}
		return result;
	}

	/**
	 * get a count for the total number of definition words used to describe a
	 * certain part of speech (sum of part of speech ROW counts)
	 * 
	 * @param posIndex
	 * @return count
	 * @throws IrrelevantException
	 */
	private float getTotalDefinitionWordCountForPOS(int posIndex)
			throws IrrelevantException {
		float result = 0;
		for (int i = 0; i < 4; i++) {
			try {
				result += definitionWordCounts[i][posIndex];
			} catch (ArrayIndexOutOfBoundsException e) {
				throw new IrrelevantException();
			}
		}
		return result;
	}

	/**
	 * get a count for the total number of definition words with a given part of
	 * speech (sum of part of speech COLUMN counts)
	 * 
	 * @param posIndex
	 * @return count
	 * @throws IrrelevantException
	 */
	private float getTotalPOSCountForDefinitionWords(int posIndex)
			throws IrrelevantException {
		float result = 0;
		for (int i = 0; i < 4; i++) {
			try {
				result += definitionWordCounts[posIndex][i];
			} catch (ArrayIndexOutOfBoundsException e) {
				throw new IrrelevantException();
			}
		}
		return result;
	}

	/**
	 * get a count for the total number of definition words used
	 * 
	 * @return count
	 */
	private float getTotalDefinitionWordCount() {
		float result = 0;
		for (int i = 0; i < 4; i++) {
			try {
				result += getTotalDefinitionWordCountForPOS(i);
			} catch (IrrelevantException e) {
				// Just to make compiler happy, this won't happen
			}
		}
		return result;
	}

	/**
	 * 
	 * @author Pete Schuette
	 * 
	 *         Simple exception to throw if the part of speech is irrelevant
	 *
	 */
	private class IrrelevantException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	}

}
