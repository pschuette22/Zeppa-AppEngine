package com.zeppamobile.smartfollow;

import javax.servlet.ServletContext;

public class AppConfig {

	/**
	 * Instance
	 */
	private static AppConfig config;
	
	/**
	 * @constructor private contructor as this is a utility class
	 */
	private AppConfig() {
		
		
	}
	
	public static AppConfig getInstance(){
		if(config==null){
			config = new AppConfig();
		}
		return config;
	}
	
	
	/**
	 * Initialize the context paths pointing to the static resources
	 * This is super hacky and should be done away with immediately
	 * @param context
	 */
	public void initResourcePaths(ServletContext context){
		
		
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
