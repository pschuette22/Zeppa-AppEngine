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
	private static final int MILLISECONDS_TO_WAIT_WHEN_NO_TASKS_LEASED = 1500;
	// Stay idle for 5 minutes max
	private static final int MILLISECONDS_TO_STAY_IDLE = (5 * 60 * 1000); 

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
		

		for (int workerNo = counter.value(); workerNo < MAX_WORKER_COUNT; workerNo++) {

			// Get the current queue to check it's statistics
			Queue notificationQueue = QueueFactory
					.getQueue("notification-delivery");
			if (notificationQueue.fetchStatistics().getNumTasks() > 30 * counter.value()) {
				
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
	 * If there are no tasks in queue, wait up to specified time
	 * 
	 */
	private void doPolling() {

		NotificationWorker worker = null;
		try {

			int loopsWithoutProcessedTasks = 0;
			Queue notificationQueue = QueueFactory
					.getQueue("notification-delivery");
			worker = new NotificationWorker(
					notificationQueue);

			while (!LifecycleManager.getInstance().isShuttingDown()) {
				boolean tasksProcessed = worker.processBatchOfTasks();
				ApiProxy.flushLogs();

				if (tasksProcessed) {
					loopsWithoutProcessedTasks = 0;

				} else {
					// Wait before trying to lease tasks again.
					try {
						loopsWithoutProcessedTasks++;

						// If worker hasn't had any tasks idle timeout time, kill it
						if (loopsWithoutProcessedTasks >= (MILLISECONDS_TO_STAY_IDLE / MILLISECONDS_TO_WAIT_WHEN_NO_TASKS_LEASED)) {
							break;
						} else {
							// Else, wait and try again (to avoid tearing down
							// useful Notification Senders)
							Thread.sleep(MILLISECONDS_TO_WAIT_WHEN_NO_TASKS_LEASED);
						}

					} catch (InterruptedException e) {
						break;
					}
				}
			}
		} catch (Exception e) {
			logMessage(
					1,
					"Exception caught and handled in notification worker: "
							+ e.getLocalizedMessage());
		} finally {
			
			// Retire the worker
			if(worker != null){
				logMessage(1, "Worker is retiring");
				worker.retire();
			}
			
			// Decrement the counter
			counter.decrement();
			
		}

		logMessage(0, "Instance is shutting down");

		
	}

	/**
	 * Log a message in sync
	 * @param level
	 * @param message
	 */
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
