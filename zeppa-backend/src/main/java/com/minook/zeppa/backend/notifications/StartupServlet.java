package com.minook.zeppa.backend.notifications;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.modules.ModulesService;
import com.google.appengine.api.modules.ModulesServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.utils.SystemProperty;

public class StartupServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ModulesService modulesApi = ModulesServiceFactory.getModulesService();

	// Logger log = Logger.getLogger(StartupServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		// super.doGet(req, resp);
		// log.info("Performing startup request");
		// If there are notifications to be delivered, make sure they are
		Queue queue = QueueFactory.getQueue("notification-delivery");
		if (queue.fetchStatistics().getNumTasks() > 0
				&& SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {

			// log.info("Production run with existing tasks in queue");

			// If not running locally
			// form URL to execute notification worker
			URL url = new URL("http://"
					+ modulesApi.getVersionHostname("backend", "1")
					+ "/notifications/notificationworker");

			try {
				// execute post method
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				connection.setDoOutput(true);
				connection.setRequestMethod("POST");
				connection.setInstanceFollowRedirects(false);

				OutputStreamWriter writer = new OutputStreamWriter(
						connection.getOutputStream());

				String message = URLEncoder.encode("Send Notifications",
						"UTF-8");
				writer.write("message=" + message);
				writer.close();

				connection.getOutputStream();

				if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
					// OK
				} else {
					// Server returned HTTP error code.
				}

			} catch (MalformedURLException e) {
				// ...
			} catch (IOException e) {
				// ...
			}

		}

		resp.setStatus(HttpServletResponse.SC_OK);

	}

}
