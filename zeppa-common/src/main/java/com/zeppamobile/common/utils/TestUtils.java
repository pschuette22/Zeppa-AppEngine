package com.zeppamobile.common.utils;

/**
 * 
 * @author Pete Schuette
 * 
 *         Utility class used to specifically when testing
 *
 */
public class TestUtils {

	/**
	 * Prefix in test token strings
	 */
	private static String testTokenPrefix = "testToken:";
	
	/**
	 * Convenience method for building an acceptable test auth token for a given
	 * email when testing
	 * 
	 * @param testEmail - email embedded in auth token
	 * @return tokenString
	 */
	public static String buildTestAuthToken(String testEmail) {
		return testTokenPrefix + testEmail;
	}

	public static int getTestTokenPrefixLength(){
		return testTokenPrefix.length();
	}
	
	public static String getTestTokenPrefix(){
		return testTokenPrefix;
	}
	
}
