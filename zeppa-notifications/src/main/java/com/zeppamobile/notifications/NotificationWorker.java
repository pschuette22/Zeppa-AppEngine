package com.zeppamobile.notifications;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javapns.communication.exceptions.CommunicationException;
import javapns.communication.exceptions.KeystoreException;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotifications;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.appengine.api.LifecycleManager;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.appengine.api.taskqueue.TransientFailureException;
import com.google.apphosting.api.ApiProxy;
import com.google.apphosting.api.ApiProxy.ApiDeadlineExceededException;
import com.google.gson.Gson;

public class NotificationWorker {

	private static final Logger log = Logger.getLogger(NotificationWorker.class
			.getName());
	private static final MemcacheService cache = MemcacheServiceFactory
			.getMemcacheService();
	private static final DatastoreService dataStore = DatastoreServiceFactory
			.getDatastoreService();

	private static final PushNotificationSender pushNotificationSender = new PushNotificationSender();
	private static final CloudMessagingSender cloudMessagingSender = new CloudMessagingSender();
	static final String PROCESSED_NOTIFICATION_TASKS_ENTITY_KIND = "_ProcessedNotificationsTasks";

	private Queue queue;

	protected NotificationWorker(Queue queue) {
		this.queue = queue;
		cache.setErrorHandler(ErrorHandlers
				.getConsistentLogAndContinue(Level.INFO));

	}
	
	/**
	 * Gracefully clean up this worker.
	 * What happens:
	 * - Stops connection for pushNotificationSender
	 * 
	 */
	public void retire(){
		try {
			pushNotificationSender.stopConnection();
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeystoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected boolean processBatchOfTasks() {

		List<TaskHandle> tasks = leaseTasks();

		if (tasks == null || tasks.size() == 0) {
			return false;
		}

		processLeasedTasks(tasks);
		return true;
	}

	private List<TaskHandle> leaseTasks() {
		List<TaskHandle> tasks;
		for (int attemptNo = 1; !LifecycleManager.getInstance()
				.isShuttingDown(); attemptNo++) {
			try {
				/*
				 * Each task may include many device tokens. For example when a
				 * task is enqueued by PushPreprocessingServlet it can contain
				 * up to BATCH_SIZE (e.g., 250) tokens.
				 * 
				 * When choosing the number of tasks to lease and the duration
				 * of lease, make sure that the total number of notifications (=
				 * the_number_of_leased_tasks multiplied by
				 * the_number_of_device_tokens_in_one_task) can be sent to APNS
				 * in the time shorter than the lease time.
				 * 
				 * Leave some buffer for handling transient errors, e.g., when
				 * deleting tasks Leasing several hundreds of tasks with one
				 * call may get a higher throughput than leasing smaller batches
				 * of tasks. However, the larger the batch, the longer the lease
				 * time needs to be. And the longer the lease time, the longer
				 * it takes for the tasks to be processed in case an instance is
				 * restarted.
				 * 
				 * Assumption used in the sample: Lease time of 30 minutes is
				 * reasonable as per the discussion above. A single thread
				 * should be able to process 100 tasks or 25,000 notifications
				 * in that time. You may need to optimize these values to your
				 * scenario.
				 */
				tasks = queue.leaseTasks(30, TimeUnit.MINUTES, 100);
				return tasks;
			} catch (TransientFailureException e) {
				log.warning("TransientFailureException when leasing tasks from queue '"
						+ queue.getQueueName() + "'");
			} catch (ApiDeadlineExceededException e) {
				log.warning("ApiDeadlineExceededException when when leasing tasks from queue '"
						+ queue.getQueueName() + "'");
			}
			if (!backOff(attemptNo)) {
				return null;
			}
		}
		return null;
	}

	/**
	 * Delete previously executed tasks (good house keeping!)
	 * 
	 * @param tasks - tasks that have been executed
	 */
	private void deleteTasks(List<TaskHandle> tasks) {
		for (int attemptNo = 1;!backOff(attemptNo); attemptNo++) {
			try {
				queue.deleteTask(tasks);
				break;
			} catch (TransientFailureException e) {
				log.warning("TransientFailureException when deleting tasks from queue '"
						+ queue.getQueueName() + "'. Attempt=" + attemptNo);
			} catch (ApiDeadlineExceededException e) {
				log.warning("ApiDeadlineExceededException when deleting tasks from queue '"
						+ queue.getQueueName() + "'. Attempt=" + attemptNo);
			}
		}
	}

	/**
	 * Process a batch of tasks and send notifications appropriately
	 * 
	 * @param tasks - tasks to be executed
	 */
	private void processLeasedTasks(List<TaskHandle> tasks) {

		Set<String> previouslyProcessedTaskNames = getAlreadyProcessedTaskNames(tasks);
		List<TaskHandle> processedTasks = new ArrayList<TaskHandle>();

		long messagesAndNotificationCount = 0;

		Map<String, MulticastResult> sentMessagesForTasks = new HashMap<String, MulticastResult>();
		Map<String, PushedNotifications> pushedNotificationsForTasks = new HashMap<String, PushedNotifications>();
		
		boolean backOff = false;

		// Iterate through the tasks and execute them
		for (TaskHandle task : tasks) {
			// Unless the instance is being sut down
			if (LifecycleManager.getInstance().isShuttingDown()) {
				break;
			}

			// Mark a task as being processed so other processes don't run it simultaniously 
			processedTasks.add(task);
			if (previouslyProcessedTaskNames.contains(task.getName())) {
				log.info("Ignoring a task " + task.getName()
						+ " that has been already processed "
						+ "to avoid sending duplicated notification.");
				continue;
			}

			try {

				// Process the task and figure out what's what
				Object result = processLeasedTask(task);

				if(result == null){
					log.warning("Error processing task: " + task.getName()
							+ ", not notifications were sent on its' behalf");
					continue;
				} else if (result instanceof MulticastResult) {

					MulticastResult multicastResult = (MulticastResult) result;
					sentMessagesForTasks.put(task.getName(), multicastResult);
					messagesAndNotificationCount += multicastResult.getTotal();

				} else if (result instanceof PushedNotifications){
					
					PushedNotifications notifications = (PushedNotifications) result;
					pushedNotificationsForTasks.put(task.getName(), notifications);
					
					messagesAndNotificationCount+=notifications.size();
					
					
					
				}

				if (messagesAndNotificationCount >= 1000) {
					deleteTasks(processedTasks);
					processedTasks.clear();
					messagesAndNotificationCount = 0;
					// TODO: enqueue removing failed device counts
				}

			} catch (CommunicationException e) {
				log.log(Level.WARNING,
						"Sending push alert failed with CommunicationException:"
								+ e.toString(), e);
				/*
				 * This exception may be thrown when socket time out or similar
				 * issues occurred a few times in a row. Retrying right away
				 * likely won't succeed and will only make another task
				 * potentially only partially processed.
				 */
				backOff = true;
			} catch (KeystoreException e) {
				log.log(Level.WARNING,
						"Sending push alert failed with KeystoreException:"
								+ e.toString(), e);
				/*
				 * It is likely a configuration issue. Retrying right away
				 * likely won't succeed and will only make another task
				 * potentially only partially processed.
				 */
				backOff = true;
			} finally {
				log.warning("recording processed task, " + task.getQueueName()
						+ "::" + task.getName());
				recordTaskProcessed(task);
			}

		}

		deleteTasks(processedTasks);

		if (backOff) {
			log.log(Level.INFO,
					"Pausing processing to recover from an exception");
			ApiProxy.flushLogs();

			// Wait 5 minutes, but do it in 10 seconds increments to gracefully
			// handle instance restarts.

			for (int i = 0; i < 30; i++) {
				if (LifecycleManager.getInstance().isShuttingDown()) {
					break;
				}
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					break;
				}
			}
		}

	}

	/**
	 * 
	 * @param task
	 *            the task to be processed.
	 * @return
	 * @throws CommunicationException
	 * @throws KeystoreException
	 */
	private Object processLeasedTask(TaskHandle task)
			throws CommunicationException, KeystoreException {

		// Method variables
		String payload = null;
		String deviceType = null;
		String[] deviceTokens = null;
		List<Entry<String, String>> params = null;

		// log.warning("processing leased task");

		// attempt to extract params. Error out if unsuccessful
		try {
			params = task.extractParams();
		} catch (UnsupportedEncodingException e) {
			log.warning("Ignoring a task with invalid encoding. This indicates a bug.");
			return null;
		} catch (UnsupportedOperationException e) {
			log.warning("Ignoring a task with invalid payload. This indicates a bug.");
			return null;
		}

		// Iterate through params and populate method variables
		for (Entry<String, String> param : params) {
			String paramKey = param.getKey();
			String paramVal = param.getValue();
			if (paramKey.equals("payload")) {
				payload = paramVal;
			} else if (paramKey.equals("devices")) {
				deviceTokens = new Gson().fromJson(paramVal, String[].class);
			} else if (paramKey.equals("deviceType")) {
				deviceType = paramVal;
			}
		}

		// If one is missing, this is a bad task (Exception?)
		if (payload == null || deviceTokens == null || deviceType == null) {
			log.warning("issue with params");
			return null;
		} else if (deviceType.equalsIgnoreCase("ANDROID")) {

			// Send the payload to the devices.
			MulticastResult result = sendPayloadToDevices(payload, deviceTokens);

			// TODO: handle failed delivery
			return result;
		} else if (deviceType.equalsIgnoreCase("iOS")) {

			PushedNotifications notifications = sendPushNotficationsToiOSDevices(payload, deviceTokens);
			
			return notifications;
		} else {
			return null;
		}
	}

	/**
	 * Handles sending cloud notifications to devices
	 * 
	 * @param payload
	 * @param registrationIds
	 * @return MulticastResult of delivered notifications
	 */
	private MulticastResult sendPayloadToDevices(String jsonPayload,
			String[] registrationIds) {

		Message.Builder builder = new Message.Builder();

		try {
			// Rebuild JSON payload
			JSONObject json = new JSONObject(jsonPayload);

			// builder.addData("content_available", "true");
			String purpose = json.getString("purpose");
			builder.addData("purpose", purpose);

			if (purpose.equals("zeppaNotification")) {
				
				builder.addData("id",json.getString("id"));
				builder.addData("title", json.getString("title"));
				builder.addData("message", json.getString("message"));
				builder.addData("type", json.getString("type"));
				builder.addData("expires", json.getString("expires"));
				
				if (json.getString("eventId").equals("-1")) {
					builder.collapseKey(json.getString("senderId"));
				} else {
					builder.collapseKey(json.getString("eventId"));
				}

			} else if (purpose.equals("userRelationshipDeleted")) {
				builder.addData("senderId", json.getString("senderId"));
				builder.addData("recipientId", json.getString("recipientId"));

			} else if (purpose.equals("eventDeleted")) {
				builder.addData("eventId", json.getString("eventId"));
				builder.collapseKey(json.getString("eventId"));

			}

		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}

		Message message = builder.build();

		MulticastResult result = cloudMessagingSender.sendMessageToDevice(
				message, registrationIds);

		// try {
		// List<Result> results = result.getResults();
		// for (Result r : results) {
		// r.getCanonicalRegistrationId();
		// }
		//
		// } catch (NullPointerException e) {
		// // result == null
		// }

		return result;
	}

	// Removed because IOS notifications are being delivered via GCM
	// Kept in case we revert back due to unforseen circumstances.
	/**
	 * Handles sending push notifications to iOS devices
	 * 
	 * @param payload
	 * @param deviceTokens
	 * @return
	 * @throws CommunicationException
	 * @throws KeystoreException
	 */
	private PushedNotifications sendPushNotficationsToiOSDevices(
			String payload, String[] deviceTokens) {

		PushNotificationPayload pnPayload = PushNotificationPayload.complex();
		try {
			JSONObject json = new JSONObject(payload);
			
			// Fetch the purpose of this notification
			String purpose = json.getString("purpose");
			pnPayload.addCustomDictionary("purpose", purpose);

			// Zeppa Notifications are notifications to be displayed to the user
			// The come accompanied with a message that corresponds to a notification object
			if (purpose.equals("zeppaNotification")) {
				
				// Build custom library for delivering a notification
				pnPayload.addCustomDictionary("id",json.getString("id"));
				pnPayload.addCustomDictionary("title", json.getString("title"));
				pnPayload.addCustomDictionary("message", json.getString("message"));
				pnPayload.addCustomDictionary("type", json.getString("type"));
				pnPayload.addCustomDictionary("expires",json.getString("expires"));

			} else if (purpose.equals("userRelationshipDeleted")) {
				// If a user to user relationship is deleted, silently clear everything between the two of you
				pnPayload.addCustomDictionary("senderId",
						json.getString("senderId"));
				pnPayload.addCustomDictionary("recipId",
						json.getString("recipientId"));

			} else if (purpose.equals("eventDeleted")) {
				// If an event was deleted, make sure it is not longer in app
				pnPayload.addCustomDictionary("eventId",
						json.getString("eventId"));
			}
			
			// set content available value
			JSONObject apsDictionary = (JSONObject) pnPayload.getPayload().get("aps");
			apsDictionary.put("content-available", 1);
			
			
		} catch (JSONException e) {
			e.printStackTrace();
			log.log(Level.SEVERE, "JSONExceptin sending push notification: " + e.getMessage());
			return null;
		}
		
		PushedNotifications notifications = null;
		try {
			log.log(Level.WARNING, "Sending " + pnPayload.getPayload().toString() + " ("+pnPayload.getPayloadSize()+"/"+pnPayload.getMaximumPayloadSize()+") to devices:" + deviceTokens.toString());
			notifications = pushNotificationSender.sendPayload(pnPayload,
					deviceTokens);

		} catch (CommunicationException e) {
			e.printStackTrace();
			log.log(Level.SEVERE, "Communcation Exception: " + e.getMessage());
		} catch (KeystoreException e) {
			e.printStackTrace();
			log.log(Level.SEVERE, "Keystore Exception: " + e.getMessage());
		} catch (Exception e){
			e.printStackTrace();
			log.log(Level.SEVERE, "General Exception: " + e.getMessage());
		}

		return notifications;
	}

	private void recordTaskProcessed(TaskHandle task) {
		cache.put(task.getName(), 1, Expiration.byDeltaSeconds(60 * 60 * 2));
		Entity entity = new Entity(PROCESSED_NOTIFICATION_TASKS_ENTITY_KIND,
				task.getName());
		entity.setProperty("processedAt", new Date());
		dataStore.put(entity);
	}

	private boolean backOff(int attemptNo) {
		// Exponential back off between 2 seconds and 64 seconds with jitter
		// 0..1000 ms.
		attemptNo = Math.min(6, attemptNo);
		int backOffTimeInSeconds = 1 << attemptNo;
		try {
			Thread.sleep(backOffTimeInSeconds * 1000
					+ (int) (Math.random() * 1000));
		} catch (InterruptedException e) {
			return false;
		}
		return true;
	}

	/**
	 * Check for already processed tasks.
	 * 
	 * @param tasks
	 *            the list of the tasks to be checked.
	 * @result The set of task names that have already been processed.
	 */
	private Set<String> getAlreadyProcessedTaskNames(List<TaskHandle> tasks) {
		/*
		 * To optimize for performance check in memcache first. A value from
		 * Memcache may have been evicted. Datastore is the authoritative
		 * source, so for any task not found in memcache check in Datastore.
		 */

		List<String> taskNames = new ArrayList<String>();

		for (TaskHandle task : tasks) {
			taskNames.add(task.getName());
		}

		Map<String, Object> alreadyProcessedTaskNames = cache.getAll(taskNames);

		List<Key> keys = new ArrayList<Key>();

		for (String taskName : taskNames) {
			if (!alreadyProcessedTaskNames.containsKey(taskName)) {
				keys.add(KeyFactory.createKey(
						PROCESSED_NOTIFICATION_TASKS_ENTITY_KIND, taskName));
			}
		}

		if (keys.size() > 0) {
			Map<Key, Entity> entityMap = dataStore.get(keys);
			for (Key key : entityMap.keySet()) {
				alreadyProcessedTaskNames.put(key.getName(), 1);
			}
		}

		return alreadyProcessedTaskNames.keySet();
	}
}
