package com.zeppamobile.api.endpoint.utils;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;

public class TaskUtility {

	/**
	 * @constructor private constructor as this is a utility class
	 */
	private static final String TASK_SERVLET_URL = "/tasks/servlet/";

	private TaskUtility() {
	}

	/**
	 * get the queue for scheduling relationship tasks
	 * 
	 * @return
	 */
	private static Queue getRelationshipQueue() {
		Queue queue = QueueFactory.getQueue("relationship-management");
		return queue;
	}

	/**
	 * Schedule a task to create the relationships of an event This also
	 * schedules sending a notification as necessary
	 * 
	 * @param eventId
	 */
	public static void scheduleCreateEventRelationships(Long eventId) {

		getRelationshipQueue().add(
				TaskOptions.Builder.withUrl(TASK_SERVLET_URL)
						.method(Method.GET).param("action", "newEvent")
						.param("id", String.valueOf(eventId.longValue())));

	}

	/**
	 * Schedule a task to delete all the relationships for a given event
	 * 
	 * @param eventId
	 */
	public static void scheduleDeleteEventRelationships(Long eventId) {
		getRelationshipQueue().add(
				TaskOptions.Builder.withUrl(TASK_SERVLET_URL)
						.method(Method.GET).param("action", "deletedEvent")
						.param("id", String.valueOf(eventId.longValue())));
	}

	/**
	 * Schedules a task to create relationships to events for two given users
	 * specified by id
	 * 
	 * @param userId1
	 * @param userId2
	 */
	public static void scheduleCreateEventRelationshipsForUsers(Long userId1,
			Long userId2) {
		getRelationshipQueue().add(
				TaskOptions.Builder.withUrl(TASK_SERVLET_URL)
						.method(Method.GET)
						.param("action", "makeEventRelationshipsBetweenUsers")
						.param("userId1", String.valueOf(userId1.longValue()))
						.param("userId2", String.valueOf(userId2.longValue())));
	}

	/**
	 * This method schedules a task to delete user relationships between two
	 * given users specified by id.
	 * 
	 * @param userId1
	 * @param userId2
	 */
	public static void scheduleDeleteRelationshipsBetweenUsers(Long userId1,
			Long userId2) {

		getRelationshipQueue().add(
				TaskOptions.Builder.withUrl(TASK_SERVLET_URL)
						.method(Method.GET)
						.param("action", "deleteRelationshipsBetweenUsers")
						.param("userId1", String.valueOf(userId1.longValue()))
						.param("userId2", String.valueOf(userId2.longValue())));

	}

	/**
	 * Schedule a task to delete all EventTagFollows for a given event tag
	 * 
	 * @param tagId
	 */
	public static void scheduleDeleteTagFollows(Long tagId) {

		getRelationshipQueue().add(
				TaskOptions.Builder.withUrl(TASK_SERVLET_URL)
						.method(Method.GET).param("action", "deletedTag")
						.param("id", String.valueOf(tagId.longValue())));
	}

	/**
	 * Schedule a task to delete
	 * 
	 * @param userId
	 */
	public static void scheduleDeleteUser(Long userId) {

		getRelationshipQueue().add(
				TaskOptions.Builder.withUrl(TASK_SERVLET_URL)
						.method(Method.GET).param("action", "deletedUser")
						.param("userId", String.valueOf(userId.longValue())));

	}

	// /**
	// * Schedule a task to delete all UserToUserRelationships for a given user
	// * @param userId
	// */
	// public static void scheduleDeleteUserRelationships(Long userId) {
	// getRelationshipQueue().add(
	// TaskOptions.Builder.withMethod(Method.POST)
	// .param("action", "deleteUserRelationships")
	// .param("id", String.valueOf(userId)));
	//
	// }
	//
	// /**
	// * Schedule a task to delete all event relationships for a user
	// * This method is ONLY Called from
	// * @param userId
	// */
	// public static void scheduleDeleteEventRelationshipsForUser(Long userId){
	// getRelationshipQueue().add(
	// TaskOptions.Builder.withMethod(Method.POST)
	// .param("action", "deleteEventRelationships")
	// .param("id", String.valueOf(userId)));
	// }

}
