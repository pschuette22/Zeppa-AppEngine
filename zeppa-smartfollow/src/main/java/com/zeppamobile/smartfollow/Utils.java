package com.zeppamobile.smartfollow;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.StorageScopes;
import com.google.appengine.tools.cloudstorage.GcsService;

import net.sf.extjwnl.data.POS;
import net.sf.extjwnl.data.PointerType;

public class Utils {

	/**
	 * @Constructor private constructor as this is a Utility file
	 */
	private Utils() {
	}

	/**
	 * This utility method attempts to convert common slang to text recognizable
	 * by the machine
	 * 
	 * This is not implemented yet. Perhaps add to library instead
	 * 
	 * @param slang
	 * @return translation or null
	 */
	public static List<String> slangConverter(String slang) {

		List<String> result = new ArrayList<String>();

		// TODO: convert slang if needed

		result.add(slang);

		return result;
	}

	/**
	 * Try to convert common multi-character strings to processable words
	 * 
	 * @param characterString
	 * @return
	 */
	public static List<String> characterStringConverter(String characterString) {
		List<String> result = new ArrayList<String>();
		if (characterString.contains("$")) {
			result.add("money");
		} else if (characterString.contains(":)")
				|| characterString.contains("=)")) {
			result.add("makes");
			result.add("me");
			result.add("happy");
		} else if (characterString.contains(":(")
				|| characterString.contains("=(")) {
			result.add("makes");
			result.add("me");
			result.add("sad");
		} else if (characterString.contains("->")
				|| characterString.contains("<-")) {
			result.add("therefore");
		} else if (characterString.contains("<3")) {
			result.add("I");
			result.add("love");
		} else if (characterString.contains("</3")) {
			result.add("breaks");
			result.add("my");
			result.add("heart");
		} else if (characterString.contains("<")) {
			// Things like "<<<<<"
			result.add("is");
			result.add("worse");
			result.add("than");
		} else if (characterString.contains(">")) {
			// Things like ">>>>"
			result.add("is");
			result.add("better");
			result.add("than");
		} else { // didnt recognize it
			// TODO: flag this to be deciphered later
			result.add(characterString);
		}
		return result;
	}

	/**
	 * Convert tag text to list of seperated words
	 * 
	 * @return
	 */
	public static List<String> convertTextToStringList(String tagText) {

		List<String> result = new ArrayList<String>();
		StringBuilder builder = new StringBuilder();

		// If characters are all upper case, we wont be able to decipher it
		if (allUpperCase(tagText) || allLowerCase(tagText)) {
			result.add(tagText);
		} else {

			for (int i = tagText.length() - 1; i >= 0; --i) {
				char ch = tagText.charAt(i);

				if (Character.isLowerCase(ch)) {
					// Most common case
					builder.insert(0, ch);
				} else if (Character.isUpperCase(ch)) {
					// Words are separated by Upper Case Character by default
					// If an upper case character is encountered, separate it
					builder.insert(0, ch);
					result.add(0, builder.toString());
					builder = new StringBuilder();
				} else if (Character.isDigit(ch)) {
					// Keep numbers together
					if (builder.length() > 0
							&& !Character.isDigit(builder.charAt(0))) {
						// Character before isn't a digit. Break it up
						// Keep digits together in case they are years or
						// something
						result.add(builder.toString());
						builder = new StringBuilder();
					}

					builder.insert(0, ch);
				} else if (ch == '.' || ch == ',' || ch == '!' || ch == '?') {
					// Word break;
					result.add(0, builder.toString());
					// add string
					result.add(String.valueOf(ch));
					// reset builder
					builder = new StringBuilder();

				} else if (!isSpacer(ch)) {
					if (ch == '\'') {
						// This is an apostrophe, should be left where it is
						result.add(0, builder.toString());
					} else if (builder.length() > 0
							&& Character.isLetterOrDigit(builder.charAt(0))) {
						// Add to characters strung together
						// Could be a Smiley face, heart, or something of the
						// like
						result.add(0, builder.toString());
						builder = new StringBuilder();
					}
					// Build string of characters
					builder.append(ch);
				} else if (builder.length() > 0) {
					/*
					 * Encountered spacer and builder had text in queue Add
					 * queued text to result list
					 */
					result.add(0, builder.toString());
					builder = new StringBuilder();
				} // else, no need to do anything

			}

			if (builder.length() > 0) {
				result.add(0, builder.toString());
			}
		}

		return result;
	}

	/**
	 * Determine if all characters in a string are upper case letters
	 * 
	 * @param tagText
	 * @return true if all
	 */
	private static boolean allUpperCase(String tagText) {

		for (int i = 0; i < tagText.length(); i++) {
			char c = tagText.charAt(i);
			if (!Character.isUpperCase(c)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Determine if all characters in a string are lower case letters
	 * 
	 * @param tagText
	 * @return
	 */
	private static boolean allLowerCase(String tagText) {
		for (int i = 0; i < tagText.length(); i++) {
			char c = tagText.charAt(i);
			if (!Character.isLowerCase(c)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Calculate the relativity of two words based on degrees of separation
	 * 
	 * @return percentage as decimal between 0 and 1
	 */
	public static double stringSimilarityCalculation(String w1, String w2) {
		int degrees = 0;
		if (w1.length() != w2.length()) {
			degrees += Math.abs(w1.length() - w2.length());
		}

		try {
			for (int i = 0; i < w1.length(); i++) {
				if (w1.charAt(i) != w2.charAt(i)) {
					degrees += 1;
				}
			}
		} catch (IndexOutOfBoundsException e) {
			// keep going till out of bounds
		}
		// calculated similarity
		double sim = (1 - ((degrees * 2) / (w1.length() + w2.length())));
		System.out.println(w1 + " Character Similarity to " + w2 + ": " + sim);
		return sim;
	}

	/**
	 * @param ch
	 *            - a character
	 * @return true if ch is a known replacer of a space
	 */
	public static boolean isSpacer(char ch) {
		return (ch == '_' || Character.isWhitespace(ch) || ch == '-');
	}

	/**
	 * 
	 * @param list
	 *            of ids
	 * @param id
	 *            that is being looked for
	 * @return true if id is held in list
	 */
	public static boolean listContainsId(List<Long> list, Long id) {
		for (Long l : list) {
			if (l.longValue() == id.longValue()) {
				// Id is held, return true
				return true;
			}
		}
		return false;
	}

	public static String toLowercase(String s) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			ch = Character.toLowerCase(ch);
			builder.append(ch);
		}
		return builder.toString();
	}

	/**
	 * Get the pointer count index of a given pointer type
	 * 
	 * @param t
	 *            - pointer type
	 * @return index in pointer count array or -1
	 */
	public static int getPointerTypeIndex(PointerType t) {
		switch (t) {
		case ANTONYM:
			return 0;
		case HYPERNYM:
			return 1;
		case HYPONYM:
			return 2;
		case ENTAILMENT:
			return 3;
		case SIMILAR_TO:
			return 4;
		case MEMBER_HOLONYM:
			return 5;
		case SUBSTANCE_HOLONYM:
			return 6;
		case PART_HOLONYM:
			return 7;
		case MEMBER_MERONYM:
			return 8;
		case SUBSTANCE_MERONYM:
			return 9;
		case PART_MERONYM:
			return 10;
		case CAUSE:
			return 11;
		case PARTICIPLE_OF:
			return 12;
		case SEE_ALSO:
			return 13;
		case PERTAINYM:
			return 14;
		case ATTRIBUTE:
			return 15;
		case VERB_GROUP:
			return 16;
		case DERIVATION:
			return 17;
		case DOMAIN_ALL:
			return 18;
		case MEMBER_ALL:
			return 19;
		case CATEGORY:
			return 20;
		case USAGE:
			return 21;
		case REGION:
			return 22;
		case CATEGORY_MEMBER:
			return 23;
		case USAGE_MEMBER:
			return 24;
		case REGION_MEMBER:
			return 25;
		case INSTANCE_HYPERNYM:
			return 26;
		case INSTANCES_HYPONYM:
			return 27;
		default:
			// Just to make compiler happy.. shouldn't happen
			return -1;
		}
	}

	/**
	 * Get the pointer count index of a given part of speech
	 * 
	 * @param pos
	 *            - part of speech in question
	 * @return index (0-3) or -1 if invalid
	 */
	public static int getPOSIndex(POS pos) {
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
			return -1;
		}
	}

	/**
	 * Calculates the weight of this pointer type from Constants.POINTER_COUNTS
	 * 
	 * @param type
	 *            pointer type
	 * @return weight
	 */
	public static double getWeight(PointerType type) {
		return (Constants.POINTER_COUNTS[getPointerTypeIndex(type)] / Constants.TOTAL_POINTER_COUNT);
	}

	/**
	 * This method sorts an array of PointerTypes using insertion sort and
	 * arranges them in descending order by the weights assigned to them in
	 * Constants.POINTER_COUNTS.
	 * 
	 * @param pointerTypes
	 *            list of pointertypes
	 */
	public static void sortDescending(PointerType[] pointerTypes) {
		// Insertion sort
		for (int i = 1; i < pointerTypes.length; i++) {
			int j = i;
			while (j > 0
					&& Utils.getWeight(pointerTypes[j - 1]) < Utils
							.getWeight(pointerTypes[j])) {
				// Swap
				PointerType temp = pointerTypes[j - 1];
				pointerTypes[j - 1] = pointerTypes[j];
				pointerTypes[j] = temp;

				j--;
			}
		}

		return;
	}
}
