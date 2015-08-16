package com.zeppamobile.notifications;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javapns.communication.exceptions.CommunicationException;
import javapns.communication.exceptions.KeystoreException;
import javapns.devices.Device;
import javapns.devices.Devices;
import javapns.devices.exceptions.InvalidDeviceTokenFormatException;
import javapns.devices.implementations.basic.BasicDevice;
import javapns.notification.AppleNotificationServer;
import javapns.notification.AppleNotificationServerBasicImpl;
import javapns.notification.PushNotificationManager;
import javapns.notification.PushNotificationPayload;
import javapns.notification.PushedNotification;
import javapns.notification.PushedNotifications;
import javapns.notification.ResponsePacket;

class PushNotificationSender {

	private static final Logger log = Logger
			.getLogger(PushNotificationSender.class.getName());

	// /Zeppa-AppEngine/war/WEB-INF/
	private static final boolean PRODUCTION = false;
	private static String CERTIFICATE_NAME = PRODUCTION ? "zeppaProduction.p12"
			: "zeppaDevelopment.p12";

	private PushNotificationManager pushManager;
	private Object keystore;
	private static byte[] certificateBytes;
	private static final String PASSWORD = "zepp@m0bile";
	private boolean isConnected = false;
	Method processedFailedNotificationsMethod = null;

	public PushNotificationSender() {

		keystore = getKeystore();

		try {
			pushManager = new PushNotificationManager();

		} catch (Exception e) {
			log.log(Level.SEVERE,
					"Failed to initialize PushNotificationManager: "
							+ e.getLocalizedMessage() + "\n\n Message: "
							+ e.getMessage());
			throw e;
		}

		try {
			processedFailedNotificationsMethod = pushManager.getClass()
					.getDeclaredMethod("processedFailedNotifications");
			processedFailedNotificationsMethod.setAccessible(true);

		} catch (NoSuchMethodException e) {
			log.severe("Incompatible JavaPNS library."
					+ e.getLocalizedMessage());
		} catch (SecurityException e) {
			log.severe("Required Reflection Permission."
					+ e.getLocalizedMessage());

		}

	}

	private InputStream getCertificateStream() {
		return PushNotificationSender.class.getClassLoader()
				.getResourceAsStream(CERTIFICATE_NAME);
	}

	protected byte[] getKeystore() {

		try {
			if (certificateBytes == null) {
				InputStream certificateStream = getCertificateStream();
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

				byte[] buffer = new byte[4096];

				try {
					int bytesRead = 0;
					while ((bytesRead = certificateStream.read(buffer, 0,
							buffer.length)) != -1) {
						outputStream.write(buffer, 0, bytesRead);
					}
				} catch (IOException e) {
					log.log(Level.SEVERE, "Error reading the certificate", e);
				}

				certificateBytes = outputStream.toByteArray();
			}
			return certificateBytes;

		} catch (NullPointerException e) {
			e.printStackTrace();
			log.log(Level.SEVERE,
					"Null Pointer Exception thrown when getting .p12 certificate");
			return null;
		}
	}

	private boolean initializeConnection() throws KeystoreException,
			CommunicationException {
		if (keystore != null) {
			AppleNotificationServer server = new AppleNotificationServerBasicImpl(
					keystore, PASSWORD, PRODUCTION);

			pushManager.initializeConnection(server);
			isConnected = true;
		} else {
			isConnected = false;
		}

		return isConnected;
	}

	/**
	 * Stop connection and closes the socket
	 * 
	 * @throws CommunicationException
	 *             thrown if an error occurred while communicating with the
	 *             target server even after a few retries.
	 * @throws KeystoreException
	 *             thrown if an error occurs with using the certificate.
	 */
	public void stopConnection() throws CommunicationException,
			KeystoreException {
		pushManager.stopConnection();
		isConnected = false;
	}

	/**
	 * Sends a payload to a list of devices.
	 * 
	 * @param payload
	 *            preformatted payload to be sent as a push notification
	 * @param deviceTokens
	 *            the list of tokens for devices to which the notifications
	 *            should be sent
	 * @return a list of pushed notifications that contain details on
	 *         transmission results.
	 * @throws Exception
	 */
	public PushedNotifications sendPayload(PushNotificationPayload payload,
			String[] deviceTokens) throws Exception {
		PushedNotifications notifications = new PushedNotifications();

		if (payload == null) {
			return notifications;
		}

		try {
			if (!isConnected && !initializeConnection()) {
				return null;
			}

			List<Device> deviceList = Devices.asDevices(deviceTokens);
			notifications.setMaxRetained(deviceList.size());
			for (Device device : deviceList) {
				PushedNotification notification = null;
				try {
					// Deliver the notifications
					BasicDevice.validateTokenFormat(device.getToken());
					notification = pushManager.sendNotification(device,
							payload, false);

					// Add delivered notification instance to array

					log.warning("iOS Notification: " + payload.toString()
							+ " \nTo device: " + device.toString());

					if (notification.isSuccessful()) {
						try {
							log.warning("Delivery Succcess: payload size "
									+ payload.getPayloadSize() + " / "
									+ payload.getMaximumPayloadSize());
						} catch (Exception e) {
							log.warning("Delivery Success but couldn't retrieve size");
						}
					} else {
						log.warning("Notification was not delivered successfully");

						Exception theProblem = notification.getException();
						log.warning(theProblem.getMessage());
						log.warning(theProblem.toString());

						/*
						 * If the problem was an error-response packet returned
						 * by Apple, get it
						 */
						ResponsePacket theErrorResponse = notification
								.getResponse();
						if (theErrorResponse != null) {
							log.warning(theErrorResponse.getMessage());
						}

					}

				} catch (InvalidDeviceTokenFormatException e) {
					notification = new PushedNotification(device, payload, e);
					log.warning("Error Sending notificaiton to device "
							+ device.toString());

					/* Find out more about what the problem was */

				}
				// Add result notification to pushed notifications list result
				notifications.add(notification);
			}

			// Push.payload(payload, notifications, PASSWORD, production,
			// devices)
		} catch (CommunicationException e) {
			stopConnection();
			throw e;
		}
		return notifications;
	}

	/**
	 * Read and process any pending error-responses.
	 */
	public void processedPendingNotificationResponses() {
		log.log(Level.INFO, "Processing sent notifications.");

		if (processedFailedNotificationsMethod == null) {
			return;
		}

		try {
			processedFailedNotificationsMethod.invoke(pushManager);
		} catch (Exception e) {
			// Catching all exception as the method requires handling 3+
			// reflection related exceptions
			// and 2+ JavaPNS exceptions. And there is nothing much that can be
			// done when any of them
			// happens other than logging the exception.
			log.log(Level.WARNING, "Processing failed notifications failed", e);
		}
	}

}
