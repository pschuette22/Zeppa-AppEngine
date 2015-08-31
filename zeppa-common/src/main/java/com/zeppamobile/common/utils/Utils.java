package com.zeppamobile.common.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * 
 * @author Pete Schuette
 * 
 * Utilities used by the Zeppa API's
 *
 */
public class Utils {

	/**
	 * @constructor private constructor as this is a utils class
	 */
	private Utils() {

	}



	// Quick method to determine if this list contains this long(ID) value
	public static boolean listContainsLong(List<Long> list, Long l) {
		if (list.isEmpty())
			return false;

		Iterator<Long> iterator = list.iterator();
		while (iterator.hasNext()) {
			if (iterator.next().longValue() == l.longValue())
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
	 * @param s
	 * @return true if the string is web safe (not null or empty)
	 */
	public static boolean isWebSafe(String s) {
		if (s == null || s.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}

	public static int safeCastLongToInt(Long l) throws IllegalArgumentException {
		int result = -1;
		if (l > Integer.MAX_VALUE || l < Integer.MIN_VALUE) {
			// Should I throw an exception..?
			l = Long.valueOf(7 * 24 * 60 * 60);
		}

		result = (int) l.longValue();

		return result;
	}

}
