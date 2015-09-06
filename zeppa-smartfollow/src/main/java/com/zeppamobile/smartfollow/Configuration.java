package com.zeppamobile.smartfollow;


public class Configuration {

	/**
	 * @constructor private contructor as this is a utility class
	 */
	private Configuration() {
	}

	
	/*
	 * 
	 * Save the testing configuration of the app.
	 * Try to use as sparingly as possible with tests =/
	 * 
	 */
	private static boolean isTesting = false;
	
	/**
	 * Set testing configuration to @true 
	 */
	public static void startTesting(){
		isTesting = true;
	}
	
	/**
	 * Set testing configuration to @false
	 */
	public static void stopTesting(){
		isTesting = false;
	}
	
	/**
	 * <p>Get the application testing configuration</p>
	 * <p>Avoid relying on this. Tests should include as much production code as possible</p>
	 * @return @true if currently running JUnit test
	 */
	public static boolean isTesting(){
		return isTesting;
	}

}
