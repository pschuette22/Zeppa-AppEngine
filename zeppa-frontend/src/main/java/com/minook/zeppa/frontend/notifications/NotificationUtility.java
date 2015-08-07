package com.minook.zeppa.frontend.notifications;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.gson.Gson;
import com.minook.zeppa.DeviceInfo;
import com.minook.zeppa.PMF;
import com.minook.zeppa.DeviceInfo.DeviceType;

public class NotificationUtility {

	private static final Logger log = Logger
			.getLogger(NotificationUtility.class.getName());
	private static final String TASKQUEUE_NAME_HEADER = "X-AppEngine-QueueName";

	/**
	 * @Constructor - private constructor as this is a utility class
	 */
	private NotificationUtility() {
	}

	public static void scheduleNotificationBuild(String objectType, Long id,
			String action) {
		if (objectType == null || objectType.isEmpty()) {
			throw new IllegalArgumentException("No Specified Object Type");
		}

		if (id == null) {
			throw new IllegalArgumentException("No Specified Object Id");
		}

		if (action == null || action.isEmpty()) {
			throw new IllegalArgumentException("No Action Specified");
		}

		// Execute the servlet to pull from the queue
		Queue executionQueue = QueueFactory.getQueue("notification-building");
		executionQueue
				.add(TaskOptions.Builder
						.withUrl(
								"/notifications/builder/")
						.method(Method.GET).param("objectType", objectType)
						.param("id", String.valueOf(id.longValue()))
						.param("action", action));

	}

	// /**
	// * Enqueue notifications to a given user by id
	// *
	// * @param payload
	// * @param recipientId
	// */
	// public static void enqueueNotificationPreprocessing(String payload,
	// long recipientId) {
	//
	// if (payload == null || payload.isEmpty()) {
	// throw new IllegalArgumentException("Notification Payload is Empty");
	// }
	//
	// if (recipientId <= 0) {
	// throw new IllegalArgumentException("Invalid Recipient UserId");
	// }
	//
	// Queue preProcessingQueue = QueueFactory
	// .getQueue("notification-preprocessing");
	// preProcessingQueue.add(TaskOptions.Builder
	// .withUrl("/admin/notifications/preprocessing")
	// .method(Method.POST).param("payload", payload)
	// .param("recipientId", String.valueOf(recipientId)));
	//
	// }

	/**
	 * Enqueue notifications to a list of devices based on the user Id passed.
	 * Separately enqueue iOS and Android notifications
	 * 
	 * @param payload
	 * @param devicesAsJson
	 * @param deviceType
	 */
	public static void enqueueNotificationDeliveryToDevices(String payload,
			List<String> registrationIds, String deviceType) {

		String[] registrationIdArray = registrationIds
				.toArray(new String[registrationIds.size()]);
		String devicesAsJson = new Gson().toJson(registrationIdArray);


		Queue notificationQueue = QueueFactory
				.getQueue("notification-delivery");
		// Add notification to the appropriate queue
		notificationQueue.add(TaskOptions.Builder
				.withMethod(TaskOptions.Method.PULL)
				.param("payload", payload)
				.param("devices", devicesAsJson)
				.param("deviceType", deviceType));

	}

	static boolean isRequestFromTaskQueue(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String queueName = request.getHeader(TASKQUEUE_NAME_HEADER);
		if (queueName == null || queueName.isEmpty()) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			return false;
		}
		return true;
	}

	/**
	 * preprocess payload to be delivered to all devices for user
	 * 
	 * @param payload
	 * @param recipientId
	 */
	public static void preprocessNotificationDelivery(String payload,
			long recipientId) {

		PersistenceManager mgr = getPersistenceManager();

		List<String> androidDeviceTokens = new ArrayList<String>();
		List<String> iosDeviceTokens = new ArrayList<String>();
		
		log.info("Processing notification to: " + recipientId + ", payload: "
				+ payload);

		try {
			@SuppressWarnings("unchecked")
			List<DeviceInfo> devices = (List<DeviceInfo>) mgr.newQuery(
					DeviceInfo.class, "ownerId == " + recipientId).execute();

			Iterator<DeviceInfo> iterator = devices.iterator();

			while (iterator.hasNext()) {
				DeviceInfo device = iterator.next();
				// If device info is null (device has not updated) or device
				// is
				// logged in, send notification
				// Must have an updated version of the app
				if (device.getLoggedIn() != null
						&& device.getLoggedIn().booleanValue()) {
					if(device.getPhoneType() == DeviceType.ANDROID){
						androidDeviceTokens.add(device.getRegistrationId());
					} else if (device.getPhoneType() == DeviceType.iOS)
						iosDeviceTokens.add(device.getRegistrationId());	
				} else {
					log.info("device not logged in or not most recent type");
				}

			}

		} finally {
			mgr.close();

		}

		// enqueue notifications if user has logged in devices
		if (!androidDeviceTokens.isEmpty()) {
			log.info("enqueueing notfication to android devices");
			NotificationUtility.enqueueNotificationDeliveryToDevices(payload,
					androidDeviceTokens, "ANDROID");
		}
		
		if(!iosDeviceTokens.isEmpty()){
			NotificationUtility.enqueueNotificationDeliveryToDevices(payload,
					iosDeviceTokens, "iOS");
		}



	}

	private static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}

}
