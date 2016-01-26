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

/**
 * 
 * @author Pete Schuette
 * 
 *         This servlet is used to create notifications if a slight delay isn't
 *         a problem
 *
 */
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

			NotificationUtility.enqueueNotificationsDelivery(notifications);

		} finally {
			// resp.setStatus(HttpServletResponse.SC_OK);
			// ServletUtility.deleteTask(req);
		}
	}

}