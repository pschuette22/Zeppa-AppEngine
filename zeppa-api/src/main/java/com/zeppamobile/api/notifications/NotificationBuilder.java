package com.zeppamobile.api.notifications;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;

import com.zeppamobile.api.PMF;
import com.zeppamobile.common.datamodel.EventComment;
import com.zeppamobile.common.datamodel.ZeppaEvent;
import com.zeppamobile.common.datamodel.ZeppaEventToUserRelationship;
import com.zeppamobile.common.datamodel.ZeppaNotification;
import com.zeppamobile.common.datamodel.ZeppaNotification.NotificationType;
import com.zeppamobile.common.datamodel.ZeppaUserToUserRelationship;

public class NotificationBuilder {

	private static Logger log = Logger.getLogger(NotificationBuilder.class.getName());

	/**
	 * @constructor hidden constructor
	 */
	private NotificationBuilder() {

	}

	/**
	 * Method returns list of notifications for appropriate object type
	 * 
	 * @param objectType
	 * @param id
	 * @param action
	 * @return list of result notifications or null
	 */
	public static List<ZeppaNotification> buildNotifications(String objectType, Long id, String action) {
		if (objectType.equals(EventComment.class.getName())) {
			return buildNotificationsForEventComment(id.longValue());
		} else if (objectType.equals(ZeppaEvent.class.getName())) {
			return buildNotificationsForZeppaEvent(id.longValue(), action);
		} else if (objectType.equals(ZeppaEventToUserRelationship.class.getName())) {
			return buildNotificationsForZeppaEventToUserRelationship(id.longValue(), action);
		} else if (objectType.equals(ZeppaUserToUserRelationship.class.getName())) {
			return buildNotificationsForZeppaUserToUserRelationship(id.longValue(), action);
		} else {
			return null;
		}
	}

	// private static Queue getNotificationCreationQueue() {
	// Queue notifictionCreatingQueue = QueueFactory
	// .getQueue("notification-creation");
	//
	// return notifictionCreatingQueue;
	// }

	/**
	 * Queue the build and send of notifications for a Zeppa Event
	 * 
	 * @param comment
	 * 
	 *            returns list of Created notifications or null
	 * 
	 */
	@SuppressWarnings("unchecked")
	private static List<ZeppaNotification> buildNotificationsForEventComment(long eventCommentId) {

		PersistenceManager mgr = getPersistenceManager();
		List<ZeppaNotification> notifications = null;
		try {
			EventComment comment = mgr.getObjectById(EventComment.class, eventCommentId);

			// Fetch the event in question.
			// Fails if event was deleted
			ZeppaEvent event = null;
			PersistenceManager emgr = getPersistenceManager();
			try {

				event = emgr.getObjectById(ZeppaEvent.class, comment.getEventId());
			} catch (JDOObjectNotFoundException e) {
				throw new IllegalArgumentException("Zeppa Event Not Found");
			} finally {
				emgr.close();
			}

			String filterString = "eventId == " + comment.getEventId() + " && isWatching == " + Boolean.TRUE
					+ " && userId != " + comment.getCommenterId();

			List<ZeppaEventToUserRelationship> relationships = null;
			PersistenceManager rmgr = getPersistenceManager();

			try {
				relationships = (List<ZeppaEventToUserRelationship>) rmgr
						.newQuery(ZeppaEventToUserRelationship.class, filterString).execute();

			} finally {
				rmgr.close();
			}

			if (relationships == null || relationships.isEmpty()) {
				// No relationships found, return without further operation;
				return null;
			}

			notifications = new ArrayList<ZeppaNotification>();

			if (!relationships.isEmpty()) {
				Iterator<ZeppaEventToUserRelationship> iterator = relationships.iterator();

				while (iterator.hasNext()) {
					ZeppaEventToUserRelationship r = iterator.next();
					ZeppaNotification notification = new ZeppaNotification();
					notification.init(comment.getCommenterId(), r.getUserId(), comment.getEventId(), event.getEnd(),
							NotificationType.COMMENT_ON_POST, comment.getText(), Boolean.FALSE);
					notifications.add(notification);
				}

			}
			// If it was not the host which commented, send notification to host
			if (comment.getCommenterId().longValue() != event.getHostId().longValue()) {
				ZeppaNotification notification = new ZeppaNotification();
				notification.init(comment.getCommenterId(), event.getHostId(), event.getId(), event.getEnd(),
						NotificationType.COMMENT_ON_POST, comment.getText(), Boolean.FALSE);

				notifications.add(notification);
			}

			PersistenceManager nmgr = getPersistenceManager();
			try {
				notifications = (List<ZeppaNotification>) nmgr.makePersistentAll(notifications);

			} finally {
				nmgr.close();
			}

		} catch (JDOObjectNotFoundException e) {

			e.printStackTrace();

		} catch (IllegalArgumentException e) {
			// Event was not found.
			// This is in place to handle this gracefully

		} finally {
			mgr.close();
		}

		return notifications;
	}

	/**
	 * Queue the build and send notifications for a ZeppaEvent
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	private static List<ZeppaNotification> buildNotificationsForZeppaEvent(long eventId, String action) {

		List<ZeppaEventToUserRelationship> relationships = null;
		PersistenceManager rmgr = getPersistenceManager();

		try {
			relationships = (List<ZeppaEventToUserRelationship>) rmgr
					.newQuery(ZeppaEventToUserRelationship.class, "eventId == " + eventId).execute();

		} finally {
			rmgr.close();
		}

		// If no relationships were found, return
		if (relationships == null || relationships.isEmpty()) {
			return null;
		}

		Iterator<ZeppaEventToUserRelationship> iterator = relationships.iterator();
		List<ZeppaNotification> notifications = new ArrayList<ZeppaNotification>();

		if (action.equals("created")) {

			ZeppaEvent event = null;
			PersistenceManager emgr = getPersistenceManager();
			try {
				event = emgr.getObjectById(ZeppaEvent.class, eventId);
			} catch (JDOObjectNotFoundException e) {
				// If event was not found, don't keep trying
			} finally {
				emgr.close();
			}

			if (event == null) {
				return null; // event wasnt found or returned null
			}

			while (iterator.hasNext()) {
				ZeppaEventToUserRelationship relationship = iterator.next();
				ZeppaNotification notification = null;
				if (relationship.getWasInvited()) {
					// Create Invited Relationship notification
					notification = new ZeppaNotification();
					notification.init(event.getHostId(), relationship.getUserId(), event.getId(), event.getEnd(),
							NotificationType.DIRECT_INVITE, "User Invited To Event", Boolean.FALSE);
				} else if (relationship.getIsRecommended()) {
					// Create recommended relationship notification
					notification = new ZeppaNotification();
					notification.init(event.getHostId(), relationship.getUserId(), event.getId(), event.getEnd(),
							NotificationType.EVENT_RECOMMENDATION, "Recommended Event For User", Boolean.FALSE);
				} else
					continue;

				notifications.add(notification);
			}

		} else if (action.equals("deletedEvent")) {
			while (iterator.hasNext()) {
				ZeppaEventToUserRelationship relationship = iterator.next();
				if (relationship.getIsAttending().booleanValue() || relationship.getIsWatching().booleanValue()) {

					// Create notification item so the user sees the event was
					// canceled
					ZeppaNotification notification = new ZeppaNotification();
					notification.init(relationship.getEventHostId(), relationship.getUserId(),
							relationship.getEventId(), relationship.getExpires(), NotificationType.EVENT_CANCELED,
							"Canceled Event", Boolean.FALSE);
					notifications.add(notification);
				} else {
					// If user is not attending, send a payload to notify
					// devices to remove this event
					String payload = PayloadBuilder.silentEventDeletedPayload(eventId);
					NotificationUtility.preprocessNotificationDelivery(payload, relationship.getUserId().longValue());
				}

			}
		}

		PersistenceManager mgr = getPersistenceManager();
		try {
			notifications = (List<ZeppaNotification>) mgr.makePersistentAll(notifications);
		} finally {
			mgr.close();
		}

		return notifications;
	}

	/**
	 * Queue the build and send notifications for a ZeppaEventToUserRelationship
	 * 
	 * @param relationship
	 */
	private static List<ZeppaNotification> buildNotificationsForZeppaEventToUserRelationship(long id, String action) {

		ZeppaEventToUserRelationship relationship = null;

		PersistenceManager rmgr = getPersistenceManager();
		try {
			relationship = rmgr.getObjectById(ZeppaEventToUserRelationship.class, id);
		} catch (JDOObjectNotFoundException e) {
			log.warning("ZeppaEventToUserRelationship not found for id: " + id);
		} finally {
			rmgr.close();
		}

		if (relationship == null) {
			// Error occured. Don't send notification regarding this operation
			return null;
		}

		ZeppaNotification notification = null;
		// Send notification if appropriate
		if (action.equals("joined")) {

			notification = new ZeppaNotification();
			notification.init(relationship.getUserId(), relationship.getEventHostId(), relationship.getEventId(),
					relationship.getExpires(), NotificationType.USER_JOINED, "User Joined Event", Boolean.FALSE);

		} else if (action.equals("left")) {

			notification = new ZeppaNotification();
			notification.init(relationship.getUserId(), relationship.getEventHostId(), relationship.getEventId(),
					relationship.getExpires(), NotificationType.USER_LEAVING, "User Leaving Event", Boolean.FALSE);

		} else if (action.equals("invited")) {

			notification = new ZeppaNotification();
			notification.init(relationship.getInvitedByUserId(), relationship.getUserId(), relationship.getEventId(),
					relationship.getExpires(), NotificationType.DIRECT_INVITE, "Invited To Event", Boolean.FALSE);

		}
		if (notification == null) {
			return null;
		}

		PersistenceManager mgr = getPersistenceManager();
		try {
			notification = mgr.makePersistent(notification);
		} finally {
			mgr.close();
		}

		return Arrays.asList(notification);

	}

	/**
	 * Queue the build and send notifications for a ZeppaUserToUserRelationship
	 * 
	 * @param relationship
	 */
	private static List<ZeppaNotification> buildNotificationsForZeppaUserToUserRelationship(long relationshipId,
			String action) {

		ZeppaUserToUserRelationship relationship = null;
		PersistenceManager rmgr = getPersistenceManager();

		try {
			relationship = rmgr.getObjectById(ZeppaUserToUserRelationship.class, relationshipId);

		} catch (JDOObjectNotFoundException e) {
			log.info("ZeppaUserToUserRelationship not found for id: " + relationshipId);
		} finally {
			rmgr.close();
		}

		if (relationship == null) {
			// Operation was unsuccessful, exit gracefully.
			log.info("ZeppaUserToUserRelationship update notification was not created");
			return null;
		}

		Long expires = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000);
		ZeppaNotification notification = null;

		if (action.equals("mingling")) {

			notification = new ZeppaNotification();
			notification.init(relationship.getSubjectId(), relationship.getCreatorId(), null, expires,
					NotificationType.MINGLE_ACCEPTED, "Now Mingling", Boolean.FALSE);

		} else if (action.equals("mingle-request")) {

			notification = new ZeppaNotification();
			notification.init(relationship.getCreatorId(), relationship.getSubjectId(), null, expires,
					NotificationType.MINGLE_REQUEST, "User Requested To Mingle", Boolean.FALSE);

		} else {
			log.info("Notification request does not have valid action");
			return null;
		}

		// Persist the notification
		PersistenceManager mgr = getPersistenceManager();
		try {

			notification = mgr.makePersistent(notification);

		} finally {
			mgr.close();
		}

		// Handle fail gracefully
		if (notification == null) {
			log.warning("Notification was not made, action: " + action + ", id: " + relationshipId);
			return null;
		}

		return Arrays.asList(notification);
	}

	private static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}

}
