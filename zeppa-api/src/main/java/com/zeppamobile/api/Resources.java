package com.zeppamobile.api;

/**
 * Class to hold application wide values that may change
 * 
 * @author DrunkWithFunk21
 */

public class Resources {

//	/**
//	 * Event privacy of a given event
//	 * 
//	 */
//	public enum EventPrivacyType {
//		CASUAL, // Friends
//		PRIVATE // Invite Only
//	}
//
//	/**
//	 * 
//	 * Type of Persistent Notification
//	 * 
//	 */
//	public enum NotificationType {
//		MINGLE_REQUEST, // 0
//		MINGLE_ACCEPTED, // 1
//		EVENT_RECOMMENDATION, // 2
//		DIRECT_INVITE, // 3
//		COMMENT_ON_POST, // 4
//		EVENT_CANCELED, // 5
//		EVENT_UPDATED, // 6
//		USER_JOINED, // 7
//		USER_LEAVING, // 8
//	}
//
//	public enum UserRelationshipType {
//		PENDING_REQUEST, MINGLING
//	}
//
//	public enum DeviceType {
//		ANDROID, iOS
//	}

	public enum UpdateEventRelationshipNotificationAction {
		NONE, USER_LEFT, USER_JOINED, USER_INVITED
	}

}
