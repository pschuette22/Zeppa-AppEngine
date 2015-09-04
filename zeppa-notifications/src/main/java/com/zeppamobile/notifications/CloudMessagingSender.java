package com.zeppamobile.notifications;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Sender;

public class CloudMessagingSender {

	private final static Logger LOG = Logger.getLogger(CloudMessagingSender.class.getName());

	private static Sender sender = new Sender(Constants.API_KEY_SERVER_APPLICATIONS);

	public CloudMessagingSender() {

	}

	public MulticastResult sendMessageToDevice(Message message, String[] registrationIds) {

		// Build the message
		MulticastResult result = null;

		// Quick convert array to list
		List<String> registrationIdsList = new ArrayList<String>();
		for (int i = 0; i < registrationIds.length; i++)
			registrationIdsList.add(registrationIds[i]);

		try {
			result = sender.sendNoRetry(message, registrationIdsList);
			int success = result.getSuccess();
			int failed = result.getFailure();

			LOG.warning("Sent cloud messages: " + success + " Success/ " + failed + " Failed");

		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

}
