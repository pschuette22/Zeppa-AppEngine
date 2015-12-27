package com.zeppamobile.api;

/**
 * 
 * @author Pete Schuette
 * 
 * Static file to set runtime configuration
 *
 */
public class AppConfig {

	
	/**
	 * @Constructor hidden constructor as this is a configuration file
	 */
	private AppConfig(){}
	
	
	private static boolean isTest = false;
	
	/** 
	 * Set this configuration when setting up a test so the program knows it is a test run. May not be necessary
	 */
	public static void setTestConfig(){
		isTest = true;
	}
	
	public static void doneTesting(){
		isTest = false;
	}
	
	/**
	 * Determine if currently running a junit test
	 * 
	 * @return true if this is a test
	 */
	public static boolean isTest(){
		return isTest;
	}
	
	
}
