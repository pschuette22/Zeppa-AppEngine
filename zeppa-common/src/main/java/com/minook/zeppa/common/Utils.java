package com.minook.zeppa.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class Utils {

	private Utils() {

	}

//	/*
//	 * ---------------------------------------------------- The Following was
//	 * Copied from google Sample
//	 * https://code.google.com/p/google-api-java-client/source/browse/calendar-
//	 * appengine-sample/src/main/java/com/google/api/services/samples/calendar/
//	 * appengine/server/Utils.java?repo=samples
//	 * ----------------------------------------------------
//	 */
//
//	/**
//	 * Global instance of the {@link DataStoreFactory}. The best practice is to
//	 * make it a single globally shared instance across your application.
//	 */
//	private static final AppEngineDataStoreFactory DATA_STORE_FACTORY = AppEngineDataStoreFactory.getDefaultInstance();
//
//	/** Global instance of the HTTP transport. */
//	static final HttpTransport HTTP_TRANSPORT = new UrlFetchTransport();
//
//	/** Global instance of the JSON factory. */
//	static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
//
//	private static GoogleClientSecrets clientSecrets = null;
//
//	static GoogleClientSecrets getClientCredential() throws IOException {
//		if (clientSecrets == null) {
//			clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
//					new InputStreamReader(Utils.class.getResourceAsStream("/client_secrets.json")));
//			Preconditions.checkArgument(
//					!clientSecrets.getDetails().getClientId().startsWith("Enter ")
//							&& !clientSecrets.getDetails().getClientSecret().startsWith("Enter "),
//					"Download client_secrets.json file from https://code.google.com/apis/console/"
//							+ "?api=calendar into calendar-appengine-sample/src/main/resources/client_secrets.json");
//		}
//		return clientSecrets;
//	}
//
//	static String getRedirectUri(HttpServletRequest req) {
//		GenericUrl url = new GenericUrl(req.getRequestURL().toString());
//		url.setRawPath("/oauth2callback");
//		return url.build();
//	}
//
//	static GoogleAuthorizationCodeFlow newFlow() throws IOException {
//		return new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, getClientCredential(),
//				Collections.singleton(CalendarScopes.CALENDAR)).setDataStoreFactory(DATA_STORE_FACTORY)
//						.setAccessType("offline").build();
//	}
//
//	static Calendar loadCalendarClient(User user) throws IOException {
//		String userId = user.getUserId();
//		Credential credential = newFlow().loadCredential(userId);
//		return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).build();
//	}

	/*
	 * End of Copy
	 * --------------------------------------------------------------
	 */

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
