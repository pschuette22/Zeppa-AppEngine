package com.zeppamobile.api.endpoint;

import java.io.Serializable;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Named;
import com.zeppamobile.api.Constants;
import com.zeppamobile.api.datamodel.DeviceInfo.DeviceType;

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
	class ClientInfo implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		// Values corresponding to the most recently released version of the
		// android client
		private int currentVersion = -1;
		private int currentUpdate = -1;
		private int currentBugfix = -1;

		// Values for the minimum client value allowed to use Zeppa before
		// requiring update
		private int minVersion = 1;
		private int minUpdate = 0;
		private int minBugfix = 0;

		// Message to display to users when asking them to update
		private String message = null;

		
		public void initAndroidClientInfo(){
			currentVersion=Constants.androidClientVersion;
			currentUpdate=Constants.androidClientUpdate;
			currentBugfix=Constants.androidClientBugfix;
			
		}
		
		public void initIosClientInfo(){
			currentVersion=Constants.iOSClientVersion;
			currentUpdate=Constants.iOSClientUpdate;
			currentBugfix=Constants.iOSClientBugfix;
		}
		
		public int getCurrentVersion() {
			return currentVersion;
		}

		public void setCurrentVersion(int currentVersion) {
			this.currentVersion = currentVersion;
		}

		public int getCurrentUpdate() {
			return currentUpdate;
		}

		public void setCurrentUpdate(int currentUpdate) {
			this.currentUpdate = currentUpdate;
		}

		public int getCurrentBugfix() {
			return currentBugfix;
		}

		public void setCurrentBugfix(int currentBugfix) {
			this.currentBugfix = currentBugfix;
		}

		public int getMinVersion() {
			return minVersion;
		}

		public void setMinVersion(int minVersion) {
			this.minVersion = minVersion;
		}

		public int getMinUpdate() {
			return minUpdate;
		}

		public void setMinUpdate(int minUpdate) {
			this.minUpdate = minUpdate;
		}

		public int getMinBugfix() {
			return minBugfix;
		}

		public void setMinBugfix(int minBugfix) {
			this.minBugfix = minBugfix;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

	}

	/**
	 * GET Request to fetch latest client info for a device
	 * 
	 * @return
	 */
	@ApiMethod(name = "getClientInfo")
	public ClientInfo getClientInfo(@Named("deviceType") DeviceType deviceType){
		ClientInfo info = new ClientInfo();
		
		if(deviceType.equals(DeviceType.ANDROID)){
			info.initAndroidClientInfo();
		} else if (deviceType.equals(DeviceType.iOS)){
			// Init ios device info
			info.initIosClientInfo();
		} else {
			info = null;
		}
		
		return info;
	}


	
}
