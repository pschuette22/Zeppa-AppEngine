package com.zeppamobile.api.endpoint.utils;

import javax.servlet.http.HttpServletRequest;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;

public class ServletUtility {

	
	private static final String QUEUE_NAME = "X-AppEngine-QueueName";
	private static final String TASK_NAME = "X-AppEngine-TaskName";
	
	/**
	 * @constructor hidden constructor as this is utility class
	 */
	private ServletUtility() { }
	
	/**
	 * Delete the request task from the queue.
	 * @param req
	 * @return
	 */
	public static boolean deleteTask(HttpServletRequest req){
		
		String queueName = req.getHeader(QUEUE_NAME);
		String taskName = req.getHeader(TASK_NAME);
		
		Queue queue = QueueFactory.getQueue(queueName);
		return queue.deleteTask(taskName);
	}
	
}
