package com.minook.zeppa.common;

/**
 * 
 * @author DrunkWithFunk21
 * 
 *         This class contains application wide constants
 * 
 */
public class Constants {

	/*
	 * Zeppa Branding Constants
	 */

	/**
	 * @constructor private constructor as this is a constants class
	 */
	private Constants() {}
	
	public static final boolean PRODUCTION = true;

	// TODO: update these ASAP
	// Current Android Version: 1.0.0
	public final static int androidClientVersion = 1;
	public final static int androidClientUpdate = 0;
	public final static int androidClientBugfix = 0;

	// Current iOS Version: 1.0.0
	public final static int iOSClientVersion = 1;
	public final static int iOSClientUpdate = 0;
	public final static int iOSClientBugfix = 0;
	

	/*
	 * Client IDs
	 */
	
	public static final String ANDROID_DEBUG_CLIENT_ID = "587859844920-nntv7duprkooi09urrsigsvia4kcu6s5.apps.googleusercontent.com";
	public static final String ANDROID_RELEASE_CLIENT_ID = "587859844920-mk76ab83am6tmgu8aedvddn7rqgjikp1.apps.googleusercontent.com";
	public static final String IOS_CLIENT_ID = "587859844920-gv8jpk5r0bk1esvg4bnsra91ks87bcn9.apps.googleusercontent.com";
	public static final String AGICENT_CLIENT_ID = "587859844920-eo2hccah1bc40q7ba3s1gci3c5qcdgev.apps.googleusercontent.com";
	public static final String AGICENT_CLIENT_ID2 = "587859844920-377evocp3ver0a6uskooijpmfmq4a6nj.apps.googleusercontent.com";
	
	
	public static final String PROJECT_NUMBER = "587859844920";
	public static final String API_KEY_SERVER_APPLICATIONS = "AIzaSyCfajjYXipLDjYbK1u0BnahtFY8h1DNFaU";
	public static final String WEB_CLIENT_ID = "587859844920-jiqoh8rn4j8d0941vunu4jfdcl2huv4l.apps.googleusercontent.com";
	public static final String ANDROID_AUDIENCE = WEB_CLIENT_ID; 
	public static final String SERVICE_ACCOUNT_CLIENT_ID = "587859844920-6fbteep6nm9lv8qoaijqo8r3v16ir87f.apps.googleusercontent.com";
	public static final String SERVICE_ACCOUNT_EMAIL = "zeppa-cloud-1821@appspot.gserviceaccount.com";
	
	
	public static final String[] CLIENT_IDS = { WEB_CLIENT_ID, ANDROID_DEBUG_CLIENT_ID,ANDROID_RELEASE_CLIENT_ID, IOS_CLIENT_ID, AGICENT_CLIENT_ID };

	
	/*
	 * Authorization Scopes
	 */
	public static final String EMAIL_SCOPE = "https://www.googleapis.com/auth/userinfo.email";
	
	
	
	

}
