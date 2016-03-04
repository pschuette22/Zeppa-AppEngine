package com.zeppamobile.common;

/**
 * 
 * @author DrunkWithFunk21
 * 
 *         This class contains application wide constants
 * 
 */
public class UniversalConstants {

	/*
	 * Zeppa Branding Constants
	 */

	/**
	 * @constructor private constructor as this is a constants class
	 */
	private UniversalConstants() {
	}

	public static final boolean PRODUCTION = true;

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

	public static final String[] ALL_CLIENT_IDS = { WEB_CLIENT_ID,
			ANDROID_DEBUG_CLIENT_ID, ANDROID_RELEASE_CLIENT_ID,
			IOS_CLIENT_ID_OLD, IOS_DEBUG_CLIENT_ID };

	/**
	 * Client ids for basic accounts to call server
	 */
	public static final String[] APP_CLIENT_IDS = { ANDROID_DEBUG_CLIENT_ID,
			ANDROID_RELEASE_CLIENT_ID, IOS_CLIENT_ID_OLD, IOS_DEBUG_CLIENT_ID };

	/*
	 * Authorization Scopes
	 */
	public static final String EMAIL_SCOPE = "https://www.googleapis.com/auth/userinfo.email";

	/*
	 * Param keys are for parameters that are to be passed into calls
	 */
	public static final String PARAM_ID_TOKEN = "id-token";
	public static final String PARAM_METHOD = "method";
	public static final String PARAM_EVENT_NAME = "event-name";
	public static final String PARAM_FILTER = "filter";
	public static final String PARAM_CURSOR = "cursor";
	public static final String PARAM_ORDERING = "ordering";
	public static final String PARAM_LIMIT = "limit";
	public static final String PARAM_EMAIL_LIST = "email-list";
	public static final String PARAM_TAG_LIST = "tag-list";

	/*
	 * Return argument keys
	 */

	public static final String KEY_CURSOR = "cursor";
	public static final String KEY_OBJECTS = "objects";

}
