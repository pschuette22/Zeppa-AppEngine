package com.zeppamobile.smartfollow;

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
	public static String slangConverter(String slang) {
		String result = null;


		return result;
	}
	
	
	public static String characterStringConverter(String characterString){
		
		// Contains some number of dollar signs
		if(characterString.contains("$")){
			return "money";
		} else if(characterString.contains(":)") || characterString.contains("=)")){
			return "makes me happy";
		} else if(characterString.contains(":(") || characterString.contains("=(")){
			return "makes me sad";
		} else if(characterString.contains("->") || characterString.contains("<-")){
			return "therefore";
		} else if(characterString.contains("<")) {
			return "is worse than";
		} else if(characterString.contains(">")) {
			return "is better than";
		}
		
		// Couldn't convert it
		// TODO: Log and review for future reference
		return characterString;
	}

	/**
	 * Determine if this word is negative - could reverse meaning of tag
	 * 
	 * @param word
	 * @return true if negative
	 */
	public static boolean isNegative(String word) {
		boolean isNegative = false;

		// Most basic case. "nt" is included for those who don't use apostrophes  
		if (word.equalsIgnoreCase("no") || word.equalsIgnoreCase("not")
				|| word.endsWith("nt") || word.endsWith("n't")) {
			isNegative = true;
		}

		return isNegative;
	}

}
