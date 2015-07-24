package com.minook.zeppa.backend.notifications;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Sender;
import com.minook.zeppa.common.Constants;

public class CloudMessagingSender {

	private final static Logger LOG = Logger.getLogger(CloudMessagingSender.class.getName());

	private static Sender sender = new Sender(Constants.API_KEY_SERVER_APPLICATIONS);

	public CloudMessagingSender() {

	}

	public MulticastResult sendMessageToAndroidDevices(Message message, String[] registrationIds) {

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

			LOG.info(success + " Success/ " + failed + " Failed");

		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

}
