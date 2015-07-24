package com.minook.zeppa.backend.notifications;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.LifecycleManager;
import com.google.appengine.api.ThreadManager;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.apphosting.api.ApiProxy;

/**
 * Notification Worker Servlet for sending notifications efficiently
 * 
 * @author DrunkWithFunk21
 * 
 */
public class NotificationWorkerServlet extends HttpServlet {

	/**
	 * 
	 */
	
	private static final Logger log = Logger.getLogger(NotificationWorkerServlet.class
			.getName());
	
	private static final long serialVersionUID = 1L;
	private static final int MAX_WORKER_COUNT = 5;
	private static final int MILLISECONDS_TO_WAIT_WHEN_NO_TASKS_LEASED = 2500;
	private static final int TEN_MINUTES = (10 * 60 * 1000);

	private static SyncCounter counter;
	private static ClassLoader cl;

	/**
	 * Used to keep number of running workers in sync
	 * 
	 * @author DrunkWithFunk21
	 *
	 */
	private class SyncCounter {

		private int c = 0;

		public SyncCounter(){
			log.warning("instantiating sync counter");
		}
		
		public synchronized void increment() {
			c++;
		}

		public synchronized void decrement() {
			c--;
		}

		public synchronized int value() {
			return c;
		}
	}

	/**
	 * Call made from module when notification was added to task queue
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
//		super.doPost(req, resp);

		if(counter == null){
			counter = new SyncCounter();
		}
		
		logMessage(0, "Starting to build workers");

		for (int workerNo = counter.value(); workerNo < MAX_WORKER_COUNT; workerNo++) {

			logMessage(0, "Checking if more notification workers are needed");
			// Get the current queue to check it's statistics
			Queue notificationQueue = QueueFactory
					.getQueue("notification-delivery");
			if (notificationQueue.fetchStatistics().getNumTasks() > 30 * counter.value()) {
				
				logMessage(0, "Starting thread for worker: " + workerNo);
				counter.increment();
				
				cl = getClass().getClassLoader();
				
				Thread thread = ThreadManager
						.createBackgroundThread(new Runnable() {

							public void run() {
						        Thread.currentThread().setContextClassLoader(cl);
								
								try {
									doPolling();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}

						});
				thread.start();

			} else {
				break; // Current number of threads is sufficient.
			}
		}

//		resp.setStatus(HttpServletResponse.SC_OK);

	}

	/**
	 * poll the task queue and lease the tasks
	 * 
	 * Wait for up to 10 minutes for tasks to be added to queue before killing
	 * tasks
	 * 
	 */
	private void doPolling() {
		logMessage(0, "Doing pulling");

		try {

			int loopsWithoutProcessedTasks = 0;
			Queue notificationQueue = QueueFactory
					.getQueue("notification-delivery");
			NotificationWorker worker = new NotificationWorker(
					notificationQueue);

			while (!LifecycleManager.getInstance().isShuttingDown()) {
				boolean tasksProcessed = worker.processBatchOfTasks();
				ApiProxy.flushLogs();

				if (!tasksProcessed) {
					logMessage(0, "waiting for tasks");

					// Wait before trying to lease tasks again.
					try {
						loopsWithoutProcessedTasks++;

						// If worker hasn't had any tasks for 10 min, kill it.
						if (loopsWithoutProcessedTasks >= (TEN_MINUTES / MILLISECONDS_TO_WAIT_WHEN_NO_TASKS_LEASED)) {
							break;
						} else {
							// Else, wait and try again (to avoid tearing down
							// useful Notification Senders)
							Thread.sleep(MILLISECONDS_TO_WAIT_WHEN_NO_TASKS_LEASED);
						}

					} catch (InterruptedException e) {
						logMessage(0, "Notification worker thread interrupted");
						break;
					}
				} else {
					logMessage(0, "processed batch of tasks");
					loopsWithoutProcessedTasks = 0;
				}
			}
		} catch (Exception e) {
			logMessage(
					1,
					"Exception caught and handled in notification worker: "
							+ e.getLocalizedMessage());
		} finally {
			counter.decrement();
		}

		logMessage(0, "Instance is shutting down");

		
	}

	private synchronized void logMessage(int level, String message) {
//		Logger log = Logger
//				.getLogger(NotificationWorkerServlet.class.getName());

		switch (level) {

		case 0: // DEBUG
			log.warning(message);
			break;

		case 1: // INFO
			log.warning(message);
			break;

		default:
			log.warning(message);
			break;
		}

	}

}
