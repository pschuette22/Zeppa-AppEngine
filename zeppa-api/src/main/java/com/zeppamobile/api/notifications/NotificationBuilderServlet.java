package com.zeppamobile.api.notifications;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.modules.ModulesService;
import com.google.appengine.api.modules.ModulesServiceFactory;
import com.zeppamobile.api.datamodel.ZeppaNotification;
import com.zeppamobile.api.endpoint.utils.TaskUtility;
import com.zeppamobile.common.utils.ModuleUtils;

public class NotificationBuilderServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger
			.getLogger(NotificationBuilderServlet.class.getName());

	ModulesService modulesApi = ModulesServiceFactory.getModulesService();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		// super.doGet(req, resp);
		try {
			doPost(req, resp);

		} catch (ServletException | IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// super.doPost(req, resp);

		try {
			String objectType = req.getParameter("objectType");
			if (objectType == null || objectType.isEmpty()) {
				throw new IllegalArgumentException("Null Object Type");
			}

			String idString = req.getParameter("id");
			if (idString == null || idString.isEmpty()) {
				throw new IllegalArgumentException("Null Id");
			}

			Long id = Long.valueOf(idString);

			String action = req.getParameter("action");
			if (action == null || action.isEmpty()) {
				throw new IllegalArgumentException("Action is Null");
			}

			List<ZeppaNotification> notifications = NotificationBuilder
					.buildNotifications(objectType, id, action);

			if (notifications != null && !notifications.isEmpty()) {
				// Schedule delivery of notification
				for (ZeppaNotification notification : notifications) {

					// Enqueue notifications to be delivered to appropriate
					// users
					String payload = PayloadBuilder
							.zeppaNotificationPayload(notification);

					NotificationUtility.preprocessNotificationDelivery(payload,
							notification.getRecipientId().longValue());
				}

				// if (SystemProperty.environment.value() ==
				// SystemProperty.Environment.Value.Production) {

				// log.info("Production run with existing tasks in queue");

				// If not running locally
				// form URL to execute notification worker
				
				URL url = ModuleUtils.getZeppaModuleUrl("zeppa-notifications", "/notifications/notificationworker", null);

				try {
					// execute post method
					HttpURLConnection connection = (HttpURLConnection) url
							.openConnection();
					// connection.setDoOutput(true);
					connection.setRequestMethod("POST");
					connection.setRequestProperty("content-type",
							"application/x-www-form-urlencoded");
					connection.setInstanceFollowRedirects(false);

					// OutputStreamWriter writer = new OutputStreamWriter(
					// connection.getOutputStream());
					//
					// String message =
					// URLEncoder.encode("Send Notifications",
					// "UTF-8");
					// writer.write("message=" + message);
					// writer.close();
					//
					// connection.getOutputStream();

					connection.connect();

					if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
						// OK
						log.warning("Responded OK, notification worker started fine");
					} else {
						// Server returned HTTP error code.
						log.warning("Something went wrong trying to start notification worker");
					}

				} catch (MalformedURLException e) {
					// ...
				} catch (IOException e) {
					// ...
				}
				// }

			} else {
				log.info("Tried to create null number of notifications");
			}

			// If the notifications were for a deleted event, schedule removing
			// the
			// event relationships.
			// This is a temporary measure until I determine the best way to do
			// it.
			if (action.equals("deletedEvent")) {
				TaskUtility.scheduleDeleteEventRelationships(id.longValue());
			}

		} finally {
			// resp.setStatus(HttpServletResponse.SC_OK);
			// ServletUtility.deleteTask(req);
		}
	}

}