package com.zeppamobile.api.endpoint.utils;

import org.mortbay.log.Log;

import com.google.appengine.api.modules.ModulesServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.zeppamobile.api.datamodel.EventTag;
import com.zeppamobile.common.UniversalConstants;

public class TaskUtility {

	/**
	 * @constructor private constructor as this is a utility class
	 */
	private static final String TASK_SERVLET_URL = "/tasks/servlet";

	private static final String TAG_INDEXING_SERVLET_URL = "/tasks/tag-indexing";

	private TaskUtility() {
	}

	/**
	 * get the queue for scheduling relationship tasks
	 * 
	 * @return
	 */
	private static Queue getRelationshipQueue() {
		return QueueFactory.getQueue("relationship-management");
	}

	/**
	 * Get the queue responsible for indexing tags
	 * 
	 * @return
	 */
	private static Queue getTagIndexingQueue() {
		return QueueFactory.getQueue("tag-indexing");
	}

	// /**
	// *
	// */
	// private static Queue getTaskQueue() {
	//
	// }

	/**
	 * Schedule a task to create the relationships of an event This also
	 * schedules sending a notification as necessary
	 * 
	 * @param eventId
	 */
	public static void scheduleCreateEventRelationships(Long eventId) {

		getRelationshipQueue().add(TaskOptions.Builder.withUrl(TASK_SERVLET_URL).method(Method.GET)
				.param("action", "newEvent").param("id", String.valueOf(eventId.longValue()))
				.header("Host", ModulesServiceFactory.getModulesService().getVersionHostname(null, null)));

	}

	/**
	 * Schedule a task to delete all the relationships for a given event
	 * 
	 * @param eventId
	 */
	public static void scheduleDeleteEventRelationships(Long eventId) {
		getRelationshipQueue().add(TaskOptions.Builder.withUrl(TASK_SERVLET_URL).method(Method.GET)
				.param("action", "deletedEvent").param("id", String.valueOf(eventId.longValue()))
				.header("Host", ModulesServiceFactory.getModulesService().getVersionHostname(null, null)));

	}

	/**
	 * Schedules a task to create relationships to events for two given users
	 * specified by id
	 * 
	 * @param userId1
	 * @param userId2
	 */
	public static void scheduleCreateEventRelationshipsForUsers(Long userId1, Long userId2) {

		getRelationshipQueue().add(TaskOptions.Builder.withUrl(TASK_SERVLET_URL).method(Method.GET)
				.param("action", "makeEventRelationshipsBetweenUsers")
				.param("userId1", String.valueOf(userId1.longValue()))
				.param("userId2", String.valueOf(userId2.longValue()))
				.header("Host", ModulesServiceFactory.getModulesService().getVersionHostname(null, null)));

	}

	/**
	 * This method schedules a task to delete user relationships between two
	 * given users specified by id.
	 * 
	 * @param userId1
	 * @param userId2
	 */
	public static void scheduleDeleteRelationshipsBetweenUsers(Long userId1, Long userId2) {

		getRelationshipQueue().add(TaskOptions.Builder.withUrl(TASK_SERVLET_URL).method(Method.GET)
				.param("action", "deleteRelationshipsBetweenUsers")
				.param("userId1", String.valueOf(userId1.longValue()))
				.param("userId2", String.valueOf(userId2.longValue()))
				.header("Host", ModulesServiceFactory.getModulesService().getVersionHostname(null, null)));

	}

	/**
	 * Schedule a task to delete all EventTagFollows for a given event tag
	 * 
	 * @param tagId
	 */
	public static void scheduleDeleteTagFollows(Long tagId) {

		getRelationshipQueue().add(TaskOptions.Builder.withUrl(TASK_SERVLET_URL).method(Method.GET)
				.param("action", "deletedTag").param("id", String.valueOf(tagId.longValue()))
				.header("Host", ModulesServiceFactory.getModulesService().getVersionHostname(null, null)));

	}

	/**
	 * Schedule a task to delete
	 * 
	 * @param userId
	 */
	public static void scheduleDeleteUser(Long userId) {

		getRelationshipQueue().add(TaskOptions.Builder.withUrl(TASK_SERVLET_URL).method(Method.GET)
				.param("action", "deletedUser").param("userId", String.valueOf(userId.longValue()))
				.header("Host", ModulesServiceFactory.getModulesService().getVersionHostname(null, null)));

	}

	/**
	 * Schedule a task to index a tag
	 * 
	 * @param tag
	 * @param isUserTag
	 */
	public static void scheduleIndexEventTag(EventTag tag, boolean isUserTag) {

		System.out.println("Indexing tag: " + tag.getTagText());
		String hostHeader = ModulesServiceFactory.getModulesService().getVersionHostname("zeppa-api", "v1");
		Log.warn("Index Event Tag Host Header: " + hostHeader + " for tag: " + tag.getId());
		getTagIndexingQueue().add(TaskOptions.Builder.withUrl(TAG_INDEXING_SERVLET_URL).method(Method.POST)
				.param(UniversalConstants.PARAM_TAG_ID, String.valueOf(tag.getId()))
				.param(UniversalConstants.PARAM_IS_USER_TAG, String.valueOf(isUserTag))
				.header("Host", hostHeader));

	}

}
