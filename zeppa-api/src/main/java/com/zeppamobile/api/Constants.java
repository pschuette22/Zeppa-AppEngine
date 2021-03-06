package com.zeppamobile.api;

/**
 * 
 * @author Pete Schuette
 * 
 *         This class contains application wide constants
 * 
 */
public class Constants {

	/**
	 * @constructor private constructor as this is a constants class
	 */
	private Constants() {}
	
	/**
	 * set to @true when deployed, @false when running locally
	 */
	public static final boolean PRODUCTION = false;
	
	// Name of generated API library
	public static final String API_NAME = "zeppaclientapi";
	
	// Current Android version
	public final static int androidClientVersion = 1;
	public final static int androidClientUpdate = 0;
	public final static int androidClientBugfix = 6;

	// Current iOS Version: 1.0.0
	public final static int iOSClientVersion = 1;
	public final static int iOSClientUpdate = 0;
	public final static int iOSClientBugfix = 9;
	

	/*
	 * Client IDs
	 */
	
	public static final String TYPE_OTHER_CLIENT_ID = "587859844920-omigp57t8e55cvnvjh73l1pmijl3dlbn.apps.googleusercontent.com";
	public static final String ANDROID_DEBUG_CLIENT_ID = "587859844920-nntv7duprkooi09urrsigsvia4kcu6s5.apps.googleusercontent.com";
	public static final String ANDROID_RELEASE_CLIENT_ID = "587859844920-mk76ab83am6tmgu8aedvddn7rqgjikp1.apps.googleusercontent.com";
	public static final String IOS_CLIENT_ID_OLD = "587859844920-gv8jpk5r0bk1esvg4bnsra91ks87bcn9.apps.googleusercontent.com";
	public static final String IOS_DEBUG_CLIENT_ID = "587859844920-7eie3deskfrbhmkm800jarhbq95h7ejl.apps.googleusercontent.com";

	
	public static final String PROJECT_NUMBER = "587859844920";
	public static final String WEB_CLIENT_ID = "587859844920-jiqoh8rn4j8d0941vunu4jfdcl2huv4l.apps.googleusercontent.com";
	public static final String ANDROID_AUDIENCE = WEB_CLIENT_ID; 
	public static final String SERVICE_ACCOUNT_CLIENT_ID = "587859844920-6fbteep6nm9lv8qoaijqo8r3v16ir87f.apps.googleusercontent.com";
	public static final String SERVICE_ACCOUNT_EMAIL = "zeppa-cloud-1821@appspot.gserviceaccount.com";
		

	
	/*
	 * Authorization Scopes
	 */
	public static final String EMAIL_SCOPE = "https://www.googleapis.com/auth/userinfo.email";
	
	
	/*
	 * Privakey 
	 */
	public static final String PRIVAKEY_CLIENT_ID = "047b5731c6fe4fc79d1eda8dae46e4d2";
	public static final String PRIVAKEY_CLIENT_SECRET = "clglJQEdtMw2794KI53e0q9SYHJ5_pCCy1sZOF-P0";

	/*
	 * GAE DataStore encryption/decryption private key
	 */
	public static final byte[] PRIVATE_KEY = {-2, -92, 121, 74, -65, 6, -58, 97, -110, -64, 93, -108, -46, -43, 33, -6};
	
}
