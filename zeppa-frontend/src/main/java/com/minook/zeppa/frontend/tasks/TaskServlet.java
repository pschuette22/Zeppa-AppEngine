package com.minook.zeppa.frontend.tasks;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.minook.zeppa.ZeppaEvent;
import com.minook.zeppa.frontend.notifications.NotificationUtility;
import com.minook.zeppa.frontend.servlets.ServletUtility;

public class TaskServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(TaskServlet.class
			.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		log.warning("doGet called in task servlet");
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
			log.warning("Executing task with encoding: "
					+ req.getCharacterEncoding());

			// All tasks should have thus
			String action = req.getParameter("action");

			if (action == null || action.isEmpty()) {
				throw new IllegalArgumentException("Null Action");
			}

			if (action.equals("newEvent")) {
				String eventIdString = req.getParameter("id");

				if (eventIdString == null || eventIdString.isEmpty()) {
					ServletUtility.deleteTask(req);
					throw new IllegalArgumentException("No Event Id Specified");
				}

				log.warning("New Event Task With ID: " + eventIdString);
				long eventId = Long.parseLong(eventIdString);

				// Execute task to create event relationships
				RelationshipUtility.createEventRelationships(eventId);

				// Schedule task to build appropriate notifications
				NotificationUtility.scheduleNotificationBuild(
						ZeppaEvent.class.getName(), eventId, "created");

			} else if (action.equals("deletedEvent")) {
				/*
				 * Delete a given event's event relationships by that event's id
				 */
				String eventIdString = req.getParameter("id");

				if (eventIdString == null | eventIdString.isEmpty()) {
					ServletUtility.deleteTask(req);
					throw new IllegalArgumentException("No Event Id Specified");
				}

				long eventId = Long.parseLong(eventIdString);

				RelationshipUtility.deleteEventRelationships(eventId);

			} else if (action.equals("makeEventRelationshipsBetweenUsers")) {
				// Make relationships

				String userId1String = req.getParameter("userId1");
				String userId2String = req.getParameter("userId2");

				if (isEmptyString(userId1String)
						|| isEmptyString(userId2String)) {
					throw new IllegalArgumentException("Missing UserId String");
				}

				long userId1 = Long.parseLong(userId1String);
				long userId2 = Long.parseLong(userId2String);

				RelationshipUtility.createRelevantRelationshipsForUsers(
						userId1, userId2);

			} else if (action.equals("deleteRelationshipsBetweenUsers")) {
				// Remove event relationships to events user hosts.
				String userId1String = req.getParameter("userId1");
				String userId2String = req.getParameter("userId2");

				if (isEmptyString(userId1String)
						|| isEmptyString(userId2String)) {
					throw new IllegalArgumentException("Missing UserId String");
				}

				long userId1 = Long.parseLong(userId1String);
				long userId2 = Long.parseLong(userId2String);

				RelationshipUtility.removeRelationshipsBetweenUsers(userId1,
						userId2);

			} else if (action.equals("deletedTag")) {
				String tagIdString = req.getParameter("id");

				if (isEmptyString(tagIdString)) {
					throw new IllegalArgumentException("Tag id String is empty");
				}

				long tagId = Long.parseLong(tagIdString);
				RelationshipUtility.removeEventTagFollows(tagId);

			} else if (action.equals("deletedUser")) {
				String userIdString = req.getParameter("userId");

				if (isEmptyString(userIdString)) {
					throw new IllegalArgumentException(
							"User Id string is empty");
				}
				long userId = Long.parseLong(userIdString);
				RelationshipUtility.removeZeppaAccountEntities(userId);

			}

		} finally {
			// Close out task and remove from queue
			// ServletUtility.deleteTask(req);
			// resp.setStatus(HttpServletResponse.SC_OK);
		}
	}

	private boolean isEmptyString(String s) {
		if (s == null || s.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

}
