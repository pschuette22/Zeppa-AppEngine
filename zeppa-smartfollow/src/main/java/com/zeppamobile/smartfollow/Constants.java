package com.zeppamobile.smartfollow;

import net.sf.extjwnl.JWNLException;
import net.sf.extjwnl.dictionary.Dictionary;

/**
 * Constants for smartfollow
 * 
 * @author Pete Schuette
 *
 */
public final class Constants {
	
	/**
	 * @Constructor private constructor as this is a constants file
	 */
	private Constants() {
	}
	
	/**
	 * Compiler flag to check if unit testing
	 */
	public static boolean TESTING = false;
	public static void setTesting(boolean testing){
		TESTING = testing;
	}
	
	
	/*
	 * Must determine this user is %65 interested in this tag to follow
	 */
	public static final double MIN_INTEREST_TO_FOLLOW = .65;
	
	/*
	 * 
	 * Weight values for final interest calculations
	 */
	// Calculated interest in this tag based on matching
	public static final double TAG_INTEREST_WEIGHT = .5;
	// Similarity of the two users tags, calculated popularity and event popularity
	public static final double USER_SIMILARITY_WEIGHT = .3;
	// Calculated popularity of the given tag
	public static final double TAG_POPULARITY_WEIGHT = .2;
	
	

	/*
	 * 
	 * English dictionary used for processing words
	 */
	private static Dictionary dictionary;

	public synchronized static Dictionary getDictionary() throws JWNLException {
		if (dictionary == null) {
			dictionary = Dictionary.getDefaultResourceInstance();
		}
		return dictionary;
	}

	

}
