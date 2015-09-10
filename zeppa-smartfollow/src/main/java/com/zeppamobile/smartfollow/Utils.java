package com.zeppamobile.smartfollow;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import opennlp.tools.postag.POSModel;

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
	 * @param slang
	 * @return translation or null
	 */
	public static List<String> slangConverter(String slang) {

		List<String> result = new ArrayList<String>();
		// TODO: convert Slang

		// try to convert slang
		// else (cant convert)
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
			
			if(builder.length() > 0){
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
	 * Get part of speech model used to determine the parts of speech of a tag
	 */
	public static POSModel getPOSModel() {
		InputStream modelIn = null;
		try {
			modelIn = Utils.class.getClassLoader().getResourceAsStream("en-pos-maxent.bin");
			POSModel model = new POSModel(modelIn);
			return model;
		} catch (IOException e) {
			// Model loading failed, handle the error
			e.printStackTrace();
			return null;
		} finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				} catch (IOException e) {
				}
			}
		}

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
		System.out.println(w1 + " Character Similarity to" + w2 + ": " + sim);
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

}
