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
	
	public static void setTestConfig(){
		isTest = true;
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
