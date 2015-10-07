package com.zeppamobile.api.endpoint;

import java.io.Serializable;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Named;
import com.zeppamobile.api.Constants;

/**
 * Client endpoint for retrieving information about the latest deployed client apps
 * This way, client apps will know when an update is available
 * Also used as a reference for Client Endpoint constraints 
 * 
 * @author Pete Schuette
 *
 */
@Api(name = Constants.API_NAME, version = "v1", scopes = { Constants.EMAIL_SCOPE }, audiences = { Constants.WEB_CLIENT_ID })
public class AppInfoEndpoint {

	/**
	 * Class with information on the latest version of the Android client
	 * 
	 * @author Pete Schuette
	 *
	 */
	class AndroidClientInfo implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		// Values corresponding to the most recently released version of the
		// android client
		final int currentVersion = Constants.androidClientVersion;
		final int currentUpdate = Constants.androidClientUpdate;
		final int currentBugfix = Constants.androidClientBugfix;

		// Values for the minimum client value allowed to use Zeppa before
		// requiring update
		final int minVersion = 1;
		final int minUpdate = 0;
		final int minBugfix = 0;

		// Message to display to users when asking them to update
		String message = null;

	}

	/**
	 * Class with information on the latest version of the iOS client
	 * 
	 * @author Pete Schuette
	 *
	 */
	class IOSClientInfo implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		// Values of the latest release of the ios app
		final int currentVersion = Constants.iOSClientVersion;
		final int currentUpdate = Constants.iOSClientUpdate;
		final int currentBugfix = Constants.iOSClientBugfix;

		// Minimum values installed app can have without requiring update
		final int minVersion = 1;
		final int minUpdate = 0;
		final int minBugfix = 0;

		// Message to display to users when asking them to update
		String message = null;

	}

	/**
	 * GET Request to fetch latest Android client info
	 * 
	 * @return
	 */
	@ApiMethod(name = "getAndroidClientInfo")
	public AndroidClientInfo getAndroidClientInfo(@Named("idToken") String tokenString){
		return new AndroidClientInfo();
	}

	/**
	 * GET Request to fetch latest iOS client info
	 * 
	 * @return IOSClientInfo instance - all instances have the same data
	 */
	@ApiMethod(name = "getIOSClientInfo")
	public IOSClientInfo getIOSClientInfo(@Named("idToken") String tokenString) {
		return new IOSClientInfo();
	}

	
}
