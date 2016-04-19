package com.zeppamobile.api.notifications;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.zeppamobile.api.PMF;
import com.zeppamobile.api.datamodel.EventComment;
import com.zeppamobile.api.datamodel.ZeppaEvent;
import com.zeppamobile.api.datamodel.ZeppaEventToUserRelationship;
import com.zeppamobile.api.datamodel.ZeppaNotification;
import com.zeppamobile.api.datamodel.ZeppaNotification.NotificationType;
import com.zeppamobile.api.datamodel.ZeppaUser;
import com.zeppamobile.api.datamodel.ZeppaUserToUserRelationship;

public class NotificationBuilder {

	private static Logger log = Logger.getLogger(NotificationBuilder.class
			.getName());

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
	 * @throws JSONException
	 */
	public static List<ZeppaNotification> buildNotifications(String objectType,
			Long id, String action) {
		if (objectType.equals(EventComment.class.getName())) {
			return buildNotificationsForEventComment(id.longValue());
		} else if (objectType.equals(ZeppaEvent.class.getName())) {
			return buildNotificationsForZeppaEvent(id.longValue(), action);
		} else if (objectType.equals(ZeppaEventToUserRelationship.class
				.getName())) {
			return buildNotificationsForZeppaEventToUserRelationship(
					id.longValue(), action);
		} else if (objectType.equals(ZeppaUserToUserRelationship.class
				.getName())) {
			return buildNotificationsForZeppaUserToUserRelationship(
					id.longValue(), action);
		} else {
			return null;
		}
	}

	/**
	 * Queue the build and send of notifications for a Zeppa Event
	 * 
	 * @param comment
	 * 
	 *            returns list of Created notifications or null
	 * 
	 */
	@SuppressWarnings("unchecked")
	private static List<ZeppaNotification> buildNotificationsForEventComment(
			long eventCommentId) {

		PersistenceManager mgr = getPersistenceManager();
		Transaction txn = mgr.currentTransaction();
		List<ZeppaNotification> notifications = null;
		try {
			txn.begin();
			// Comment that was made
			EventComment comment = mgr.getObjectById(EventComment.class,
					eventCommentId);
			// Person who commented
			ZeppaUser commenter = mgr.getObjectById(ZeppaUser.class,
					comment.getCommenterId());

			// Event that is being commented on
			ZeppaEvent event = mgr.getObjectById(ZeppaEvent.class,
					comment.getEventId());

			String filterString = "eventId == " + comment.getEventId()
					+ " && isWatching == " + Boolean.TRUE + " && userId != "
					+ comment.getCommenterId();

			List<ZeppaEventToUserRelationship> relationships = (List<ZeppaEventToUserRelationship>) mgr
					.newQuery(ZeppaEventToUserRelationship.class, filterString)
					.execute();

			if (relationships == null || relationships.isEmpty()) {
				// No relationships found, return without further operation;
				return null;
			}

			// Build the notification message associated with this comment
			String message = getUserDisplayName(commenter, false)
					+ " commented on " + event.getTitle();

			notifications = new ArrayList<ZeppaNotification>();

			if (!relationships.isEmpty()) {
				Iterator<ZeppaEventToUserRelationship> iterator = relationships
						.iterator();

				while (iterator.hasNext()) {
					ZeppaEventToUserRelationship r = iterator.next();
					ZeppaNotification notification = new ZeppaNotification(
							comment.getCommenterId(), r.getUserId(),
							comment.getEventId(), event.getEnd(),
							NotificationType.COMMENT_ON_POST, message,
							comment.getText(), Boolean.FALSE);
					notifications.add(notification);
				}

			}
			// If it was not the host which commented, send notification to host
			if (comment.getCommenterId().longValue() != event.getHostId()
					.longValue()) {
				ZeppaNotification notification = new ZeppaNotification(
						comment.getCommenterId(), event.getHostId(),
						event.getId(), event.getEnd(),
						NotificationType.COMMENT_ON_POST, message,
						comment.getText(), Boolean.FALSE);

				notifications.add(notification);
			}

			// If some notifications were made, put em in the database
			if (!notifications.isEmpty()) {
				notifications = (List<ZeppaNotification>) mgr
						.makePersistentAll(notifications);
			}

			txn.commit();

		} catch (JDOObjectNotFoundException e) {

			e.printStackTrace();

		} catch (IllegalArgumentException e) {
			// Event was not found.
			// This is in place to handle this gracefully

		} finally {
			if (txn.isActive()) {
				txn.rollback();
				notifications = null;
			}
			mgr.close();
		}

		return notifications;
	}

	/**
	 * Queue the build and send notifications for a ZeppaEvent
	 * 
	 * @param event
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	private static List<ZeppaNotification> buildNotificationsForZeppaEvent(
			long eventId, String action) {

		List<ZeppaNotification> notifications = null;

		PersistenceManager mgr = getPersistenceManager();
		Transaction txn = mgr.currentTransaction();

		try {
			txn.begin();

			List<ZeppaEventToUserRelationship> relationships = (List<ZeppaEventToUserRelationship>) mgr
					.newQuery(ZeppaEventToUserRelationship.class,
							"eventId == " + eventId).execute();

			// If no relationships were found, return null because no
			// notifications will be created
			if (relationships == null || relationships.isEmpty()) {
				return null;
			}

			notifications = new ArrayList<ZeppaNotification>();

			Iterator<ZeppaEventToUserRelationship> iterator = relationships
					.iterator();

			// This is for when we put in the option to edit or whatever else
			if (action.equals("created")) {

				// Fetch the event and the host of this event. If either are not
				// found, JDO Exception will be thrown
				ZeppaEvent event = mgr.getObjectById(ZeppaEvent.class, eventId);
				ZeppaUser host = mgr.getObjectById(ZeppaUser.class,
						event.getHostId());

				String inviteMessage = host.getUserInfo().getGivenName() + " "
						+ host.getUserInfo().getFamilyName()
						+ " directly invited you to" + event.getTitle();
				String recommendMessage = "Recommended activity: "
						+ event.getTitle() + " started by "
						+ host.getUserInfo().getGivenName() + " "
						+ host.getUserInfo().getFamilyName();

				while (iterator.hasNext()) {
					ZeppaEventToUserRelationship relationship = iterator.next();
					ZeppaNotification notification = null;
					if (relationship.getWasInvited()) {
						// Create Invited Relationship notification
						notification = new ZeppaNotification(event.getHostId(),
								relationship.getUserId(), event.getId(),
								event.getEnd(), NotificationType.DIRECT_INVITE,
								"Direct Invite", inviteMessage, Boolean.FALSE);
					} else if (relationship.getIsRecommended()) {
						// Create recommended relationship notification
						notification = new ZeppaNotification(event.getHostId(),
								relationship.getUserId(), event.getId(),
								event.getEnd(),
								NotificationType.EVENT_RECOMMENDATION,
								"Recommended Activity", recommendMessage,
								Boolean.FALSE);
					} else
						continue;

					notifications.add(notification);
				}

			}

			// If some notifications were made, put em in the database
			if (!notifications.isEmpty()) {
				notifications = (List<ZeppaNotification>) mgr
						.makePersistentAll(notifications);
			}

			txn.commit();
		} catch (JDOObjectNotFoundException e) {
			// Couldn't find an important object

		} finally {
			if (txn.isActive()) {
				txn.rollback();
				notifications = null;
			}
			mgr.close();
		}

		return notifications;
	}

	/**
	 * Queue the build and send notifications for a ZeppaEventToUserRelationship
	 * 
	 * @param relationship
	 */
	private static List<ZeppaNotification> buildNotificationsForZeppaEventToUserRelationship(
			long id, String action) {

		List<ZeppaNotification> notifications = null;
		PersistenceManager mgr = getPersistenceManager();
		Transaction txn = mgr.currentTransaction();

		try {

			txn.begin();
			ZeppaEventToUserRelationship relationship = mgr.getObjectById(
					ZeppaEventToUserRelationship.class, id);
			ZeppaEvent event = mgr.getObjectById(ZeppaEvent.class,
					relationship.getEventId());
			ZeppaUser user = mgr.getObjectById(ZeppaUser.class,
					relationship.getUserId());

			ZeppaNotification notification = null;
			// Send notification if appropriate
			if (action.equals("joined")) {

				notification = new ZeppaNotification(relationship.getUserId(),
						relationship.getEventHostId(),
						relationship.getEventId(), relationship.getExpires(),
						NotificationType.USER_JOINED, "Squad Was Joined",
						getUserDisplayName(user, false) + " joined "
								+ event.getTitle(), Boolean.FALSE);

			} else if (action.equals("left")) {

				notification = new ZeppaNotification(relationship.getUserId(),
						relationship.getEventHostId(),
						relationship.getEventId(), relationship.getExpires(),
						NotificationType.USER_LEAVING, "Someone Left Squad",
						getUserDisplayName(user, false) + " left "
								+ event.getTitle(), Boolean.FALSE);

			} else if (action.equals("invited")) {

				ZeppaUser inviter = mgr.getObjectById(ZeppaUser.class,
						relationship.getInvitedByUserId());

				notification = new ZeppaNotification(
						relationship.getInvitedByUserId(),
						relationship.getUserId(), relationship.getEventId(),
						relationship.getExpires(),
						NotificationType.DIRECT_INVITE, "Direct Invite",
						getUserDisplayName(inviter, false) + " invited you to "
								+ event.getTitle(), Boolean.FALSE);

			} else {
				throw new IllegalArgumentException("Bad action request");
			}

			// If there was a notification made, pop it in the database
			if (notification != null) {
				notification = mgr.makePersistent(notification);
			}

			txn.commit();

			notifications = Arrays.asList(notification);

		} catch (JDOObjectNotFoundException e) {
			// Couldn't find an important object
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();

		} finally {
			if (txn.isActive()) {
				txn.rollback();
				notifications = null;
			}
			mgr.close();

		}

		return notifications;

	}

	/**
	 * Queue the build and send notifications for a ZeppaUserToUserRelationship
	 * 
	 * @param relationship
	 */
	private static List<ZeppaNotification> buildNotificationsForZeppaUserToUserRelationship(
			long relationshipId, String action) {

		ZeppaUserToUserRelationship relationship = null;
		List<ZeppaNotification> notifications = null;
		PersistenceManager mgr = getPersistenceManager();
		Transaction txn = mgr.currentTransaction();
		try {
			txn.begin();

			// Fetch the relationship to the
			relationship = mgr.getObjectById(ZeppaUserToUserRelationship.class,
					relationshipId);

			// Expires in a week
			Long expires = System.currentTimeMillis()
					+ (7 * 24 * 60 * 60 * 1000);
			ZeppaNotification notification = null;

			if (action.equals("mingling")) {

				ZeppaUser sender = mgr.getObjectById(ZeppaUser.class,
						relationship.getCreatorId());

				notification = new ZeppaNotification(
						relationship.getSubjectId(),
						relationship.getCreatorId(), null, expires,
						NotificationType.MINGLE_ACCEPTED, "Mingling",
						getUserDisplayName(sender, true) + " and you mingle",
						Boolean.FALSE);

			} else if (action.equals("mingle-request")) {
				ZeppaUser sender = mgr.getObjectById(ZeppaUser.class,
						relationship.getSubjectId());

				notification = new ZeppaNotification(
						relationship.getCreatorId(),
						relationship.getSubjectId(), null, expires,
						NotificationType.MINGLE_REQUEST, "Request to Mingle",
						getUserDisplayName(sender, true) + " wants to mingle",
						Boolean.FALSE);

			}

			// If the notification was instantiated, make it persistent and set
			// the result notification
			if (notification != null) {
				notification = mgr.makePersistent(notification);
			}
			txn.commit();

			notifications = Arrays.asList(notification);

		} catch (JDOObjectNotFoundException e) {
			// Couldn't find an important object
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();

		} finally {
			if (txn.isActive()) {
				txn.rollback();
				notifications = null;
			}
			mgr.close();
		}

		return notifications;
	}

	/**
	 * Get persistence manager to interact with the datastore
	 * 
	 * @return persistence manager factory instance
	 */
	private static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}

	/**
	 * Convenience method to get a users human readable name
	 * 
	 * @param user
	 * @return
	 */
	private static String getUserDisplayName(ZeppaUser user, boolean isFullName) {

		return user.getUserInfo().getGivenName()
				+ " "
				+ (isFullName ? user.getUserInfo().getFamilyName() : user
						.getUserInfo().getFamilyName().charAt(0));
	}

}
