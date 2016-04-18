package com.zeppamobile.common.utils;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * 
 * @author Pete Schuette
 * 
 * non type-specific utilities used by Zeppa
 *
 */
public class Utils {

	private static SecureRandom random = new SecureRandom();
	
	/**
	 * @constructor private constructor as this is a utils class
	 */
	private Utils() {

	}

		  

	/**
	 * Creates a random 32 character string that can be used as a session key
	 * @return 32 character string
	 */
	public static String nextSessionId() {
	    return new BigInteger(130, random).toString(32);
	}
  
	/**
	 * Convenience method to determine if List of Long objects holds long with value l
	 * @param list of Long Objects
	 * @param l - long value to search for
	 * @return true if l was found in list
	 */
	public static boolean listContainsLong(List<Long> list, long l) {
		if (list.isEmpty())
			return false;

		Iterator<Long> iterator = list.iterator();
		while (iterator.hasNext()) {
			if (iterator.next().longValue() == l)
				return true;
		}

		return false;
	}

	/*
	 * This is a total hack. I need to figure out how to replace these with a
	 * Container like I tried to do above.
	 * 
	 * encoding only happens in respective clients but this is the operation
	 */
	public static String encodeListString(ArrayList<String> stringList) {
		StringBuilder stringbuilder = new StringBuilder();
		stringbuilder.append(stringList.get(0));
		if (stringList.size() > 1) {
			for (int i = 0; i < stringList.size(); i++) {
				stringbuilder.append(",");
				stringbuilder.append(stringList.get(i));
			}
		}

		return stringbuilder.toString();
	}

	public static List<String> decodeListString(String encodedString) {

		char[] characters = encodedString.toCharArray();
		StringBuilder stringbuilder = new StringBuilder();
		int position = 0;
		ArrayList<String> stringList = new ArrayList<String>();
		while (true) {
			try {
				char character = characters[position];
				if (character == ',') {
					String resultString = stringbuilder.toString();
					stringList.add(resultString);
					stringbuilder = new StringBuilder(); // clear it
				} else {
					stringbuilder.append(character);
				}

				position++;

			} catch (ArrayIndexOutOfBoundsException aiex) {
				// List ended
				String resultString = stringbuilder.toString();
				if (!resultString.isEmpty())
					stringList.add(resultString);
				break;
			} catch (NullPointerException nex) {
				// Other null exception
				break;
			}

		}

		return stringList;

	}

	public static List<Long> decodeIdListString(String encodedString) {

		char[] characters = encodedString.toCharArray();
		StringBuilder stringbuilder = new StringBuilder();
		int position = 0;
		ArrayList<Long> longList = new ArrayList<Long>();
		while (true) {
			try {
				char character = characters[position];
				if (character == ',') {
					String resultString = stringbuilder.toString();
					Long resultLong = Long.decode(resultString);
					longList.add(resultLong);
					stringbuilder = new StringBuilder(); // clear it
				} else {
					stringbuilder.append(character);
				}

				position++;

			} catch (ArrayIndexOutOfBoundsException aiex) {
				// List ended
				String resultString = stringbuilder.toString();
				if (!resultString.isEmpty()) {
					Long resultLong = Long.decode(resultString);
					longList.add(resultLong);
				}
				break;
			} catch (NullPointerException nex) {
				// Other null exception
				break;
			}

		}

		return longList;

	}

	/**
	 * Convenience method to determine if string has content
	 * @param s
	 * @return true if s is not null or empty
	 */
	public static boolean isWebSafe(String s) {
		return !(s == null || s.isEmpty());
	}

	/**
	 * 
	 * @param l - long to be converted to int
	 * @return int value of l
	 * @throws IllegalArgumentException if l is greater/less than max/min int value
	 */
	public static int safeCastLongToInt(Long l) throws IllegalArgumentException {
		int result = -1;
		if (l > Integer.MAX_VALUE || l < Integer.MIN_VALUE) {
			// Should I throw an exception..?
			l = Long.valueOf(-1);
		}

		result = (int) l.longValue();

		return result;
	}

}
