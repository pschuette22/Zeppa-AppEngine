package com.zeppamobile.api.endpoint.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.zeppamobile.api.PMF;
import com.zeppamobile.api.datamodel.DeviceInfo;
import com.zeppamobile.api.datamodel.EventComment;
import com.zeppamobile.api.datamodel.EventTag;
import com.zeppamobile.api.datamodel.EventTagFollow;
import com.zeppamobile.api.datamodel.ZeppaEvent;
import com.zeppamobile.api.datamodel.ZeppaEventToUserRelationship;
import com.zeppamobile.api.datamodel.ZeppaNotification;
import com.zeppamobile.api.datamodel.ZeppaUserToUserRelationship;
import com.zeppamobile.api.datamodel.ZeppaEvent.EventPrivacyType;
import com.zeppamobile.api.datamodel.ZeppaUserToUserRelationship.UserRelationshipType;
import com.zeppamobile.api.notifications.NotificationUtility;
import com.zeppamobile.api.notifications.PayloadBuilder;
import com.zeppamobile.common.utils.Utils;

public class RelationshipUtility {

	private static final Logger log = Logger
			.getLogger(RelationshipUtility.class.getName());

	/**
	 * this method creates relationships/ notifications for event
	 * 
	 * @param event
	 * @param mgr
	 * @param user
	 */
	@SuppressWarnings("unchecked")
	public static Collection<ZeppaEventToUserRelationship> createEventRelationships(
			long eventId) {

		ZeppaEvent event = null;
		PersistenceManager emgr = getPersistenceManager();
		try {
			event = emgr.getObjectById(ZeppaEvent.class, eventId);

		} catch (JDOObjectNotFoundException e) {
			e.printStackTrace();
			return null;
		} finally {
			emgr.close();
		}

		if (event == null) {
			log.info("Error Fetching Event");
			return null;
		}

		Collection<ZeppaEventToUserRelationship> result = new ArrayList<ZeppaEventToUserRelationship>();
		List<Long> userIds = new ArrayList<Long>();

		if (event.getPrivacy() == EventPrivacyType.PRIVATE) {
			userIds.addAll(event.getInvitedUserIds());
		} else {
			// Execute Query to find all relationships user initiated
			PersistenceManager mgr = getPersistenceManager();

			try {
				Query query = mgr.newQuery(ZeppaUserToUserRelationship.class);
				query.setFilter("creatorId == " + event.getHostId().longValue()
						+ " && relationshipType == '"
						+ UserRelationshipType.MINGLING.toString() + "'");

				List<ZeppaUserToUserRelationship> connections = (List<ZeppaUserToUserRelationship>) query
						.execute();

				if (!connections.isEmpty()) {

					Iterator<ZeppaUserToUserRelationship> iterator = connections
							.iterator();
					while (iterator.hasNext()) {
						userIds.add(iterator.next().getSubjectId());
					}
				}

				// Execute new query to find all relationships user accepted
				query = mgr.newQuery(ZeppaUserToUserRelationship.class);
				query.setFilter("subjectId == " + event.getHostId().longValue()
						+ " && relationshipType == '"
						+ UserRelationshipType.MINGLING.toString() + "'");

				List<ZeppaUserToUserRelationship> connections2 = (List<ZeppaUserToUserRelationship>) query
						.execute();

				if (!connections2.isEmpty()) {

					Iterator<ZeppaUserToUserRelationship> iterator = connections2
							.iterator();
					while (iterator.hasNext()) {
						userIds.add(iterator.next().getCreatorId());
					}
				}

			} finally {
				mgr.close();
			}

		}

		if (!userIds.isEmpty()) {

			// Iterate through recipient ids and send notifications

			// First send direct notifications
			// Remove user ids as they get notifications
			List<Long> invitedUserIds = event.getInvitedUserIds();
			// invitedUserIds.addAll(event.getInvitedUserIds());
			if (invitedUserIds != null && !invitedUserIds.isEmpty()) {
				Iterator<Long> recipientIterator = invitedUserIds.iterator();
				while (recipientIterator.hasNext()) {
					Long l = recipientIterator.next();
					if (Utils.listContainsLong(userIds, l)) {

						ZeppaEventToUserRelationship relationship = new ZeppaEventToUserRelationship(
								event, l, Boolean.TRUE, Boolean.TRUE,
								event.getHostId());
						result.add(relationship);
						userIds.remove(l);
					}

				}
			}

			// Create relationships for users following tags
			if (!userIds.isEmpty() && event.getTagIds() != null
					&& !event.getTagIds().isEmpty()) {
				Iterator<Long> i = event.getTagIds().iterator();
				StringBuilder filterBuilder = new StringBuilder();

				while (i.hasNext()) {
					filterBuilder.append("tagId == ");
					filterBuilder.append(i.next().longValue());
					if (i.hasNext()) {
						filterBuilder.append(" || ");
					}
				}

				PersistenceManager mgr = getPersistenceManager();

				try {
					Query query = mgr.newQuery(EventTagFollow.class);
					query.setFilter(filterBuilder.toString());

					List<EventTagFollow> follows = (List<EventTagFollow>) query
							.execute();

					if (!follows.isEmpty()) {
						Iterator<EventTagFollow> iterator = follows.iterator();
						while (iterator.hasNext()) {
							Long l = iterator.next().getFollowerId();
							if (Utils.listContainsLong(userIds, l)) {
								ZeppaEventToUserRelationship relationship = new ZeppaEventToUserRelationship(
										event, l, Boolean.FALSE, Boolean.TRUE,
										Long.valueOf(-1));
								result.add(relationship);
								userIds.remove(l);
							}

						}
					}

				} finally {
					mgr.close();
				}

			}

			if (!userIds.isEmpty()) {
				Iterator<Long> iterator = userIds.iterator();
				while (iterator.hasNext()) {
					ZeppaEventToUserRelationship relationship = new ZeppaEventToUserRelationship(
							event, iterator.next().longValue(), Boolean.FALSE,
							Boolean.TRUE, Long.valueOf(-1));

					result.add(relationship);
				}
			}

		}

		PersistenceManager mgr = getPersistenceManager();
		try {
			result = mgr.makePersistentAll(result);
		} finally {
			mgr.close();
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public static void createRelevantRelationshipsForUsers(Long userId1,
			Long userId2) {

		List<ZeppaEventToUserRelationship> relationships = new ArrayList<ZeppaEventToUserRelationship>();

		/*
		 * Create relationships between from all upcoming user2 hosted events to
		 * user1
		 */
		String filter = "hostId == " + userId2 + " && end > "
				+ System.currentTimeMillis() + " && privacy == 'CASUAL'";
		PersistenceManager mgr = getPersistenceManager();

		try {
			List<ZeppaEvent> events = (List<ZeppaEvent>) mgr.newQuery(
					ZeppaEvent.class, filter).execute();

			if (events != null && !events.isEmpty()) {

				Iterator<ZeppaEvent> iterator = events.iterator();
				while (iterator.hasNext()) {
					ZeppaEvent event = iterator.next();
					PersistenceManager emgr = getPersistenceManager();
					ZeppaEventToUserRelationship relationship = null;
					try {
						relationship = (ZeppaEventToUserRelationship) emgr
								.newQuery(
										ZeppaEventToUserRelationship.class,
										"eventId == "
												+ event.getId().longValue()
												+ " && userId == "
												+ userId1.longValue())
								.execute();

					} catch (javax.jdo.JDOObjectNotFoundException e) {
						// No object was found. This is stellar
					} finally {
						emgr.close();
					}

					if (relationship == null) {
						relationship = new ZeppaEventToUserRelationship(event,
								userId1, Boolean.FALSE, Boolean.TRUE,
								Long.valueOf(-1));
						relationships.add(relationship);
					}
				}

			}

			/*
			 * Create relationships between from all upcoming user1 hosted
			 * events to user2
			 */
			filter = "hostId == " + userId1 + " && end > "
					+ System.currentTimeMillis() + " && privacy == 'CASUAL'";
			events = (List<ZeppaEvent>) mgr.newQuery(ZeppaEvent.class, filter)
					.execute();

			if (events != null && !events.isEmpty()) {

				Iterator<ZeppaEvent> iterator = events.iterator();
				while (iterator.hasNext()) {

					ZeppaEvent event = iterator.next();
					PersistenceManager emgr = getPersistenceManager();
					ZeppaEventToUserRelationship relationship = null;
					try {
						relationship = (ZeppaEventToUserRelationship) emgr
								.newQuery(
										ZeppaEventToUserRelationship.class,
										"eventId == "
												+ event.getId().longValue()
												+ " && userId == "
												+ userId2.longValue())
								.execute();

					} catch (javax.jdo.JDOObjectNotFoundException e) {
						// No object was found. This is stellar
					} finally {
						emgr.close();
					}

					if (relationship == null) {
						relationship = new ZeppaEventToUserRelationship(event,
								userId2, Boolean.FALSE, Boolean.TRUE,
								Long.valueOf(-1));
						relationships.add(relationship);
					}
				}

			}

		} finally {
			mgr.close();
		}

		PersistenceManager rmgr = getPersistenceManager();
		try {
			rmgr.makePersistentAll(relationships);
		} finally {
			rmgr.close();
		}

	}

	/**
	 * This is a static method to remove all existing relationships between two
	 * users. This is assumed to be called just before removing the user to user
	 * relationship Relationships: EventToUserRelationship EventTagFollow
	 * ZeppaNotification
	 * 
	 * @param user1
	 * @param user2
	 * @param user
	 *            // Authorization
	 */

	public static void removeRelationshipsBetweenUsers(long userId1,
			long userId2) {

		// Delete Tag follows between users
		PersistenceManager tmgr = getPersistenceManager();
		String filter = "(tagOwnerId == " + userId1 + " || tagOwnerId == "
				+ userId2 + ") && (followerId == " + userId1
				+ " || followerId == " + userId2 + ")";
		try {
			tmgr.newQuery(EventTagFollow.class, filter).deletePersistentAll();

		} finally {
			tmgr.close();
		}

		// Delete Notifications Between users
		PersistenceManager nmgr = getPersistenceManager();
		filter = "(senderId == " + userId1 + " || senderId == " + userId2
				+ ") && (recipientId == " + userId1 + " || recipientId == "
				+ userId2 + ")";
		try {

			nmgr.newQuery(ZeppaNotification.class, filter)
					.deletePersistentAll();

		} finally {
			nmgr.close();
		}

		// Delete event relationships
		PersistenceManager emgr = getPersistenceManager();
		filter = "(eventHostId == " + userId1 + " || eventHostId == " + userId2
				+ ") && (userId == " + userId1 + " || userId == " + userId2
				+ ") && isAttending == " + Boolean.FALSE;
		try {

			emgr.newQuery(ZeppaEventToUserRelationship.class, filter)
					.deletePersistentAll();

		} finally {
			emgr.close();
		}

	}

	/**
	 * Deletes all relationships and notifications pointing to this event
	 * 
	 * @param event
	 */

	// public static void removeRelationshipsToEvent(ZeppaEvent event) {
	// PersistenceManager rmgr = getPersistenceManager();
	// String filter = "eventId == " + event.getId();
	// try {
	//
	// rmgr.newQuery(ZeppaEventToUserRelationship.class, filter)
	// .deletePersistentAll();
	//
	// } finally {
	// rmgr.close();
	// }
	//
	// PersistenceManager nmgr = getPersistenceManager();
	//
	// try {
	//
	// nmgr.newQuery(ZeppaNotification.class, filter)
	// .deletePersistentAll();
	//
	// } finally {
	// nmgr.close();
	// }
	//
	// PersistenceManager cmgr = getPersistenceManager();
	// try {
	//
	// cmgr.newQuery(EventComment.class, filter).deletePersistentAll();
	//
	// } finally {
	// cmgr.close();
	// }
	//
	// }

	public static void deleteEventRelationships(long eventId) {

		PersistenceManager mgr = getPersistenceManager();

		try {

			mgr.newQuery(ZeppaEventToUserRelationship.class,
					"eventId == " + eventId).deletePersistentAll();

		} finally {
			mgr.close();
		}

	}

	public static void removeEventTagFollows(long tagId) {
		PersistenceManager mgr = getPersistenceManager();

		try {

			mgr.newQuery(EventTagFollow.class, "tagId == " + tagId)
					.deletePersistentAll();

		} finally {
			mgr.close();
		}

	}

	/**
	 * Remove all stored data regarding this user ... They probably sucked
	 * anyway.
	 * 
	 * @param userId
	 */
	public static void removeZeppaAccountEntities(long userId) {

		// Delete User Devices
		PersistenceManager dmgr = getPersistenceManager();
		try {

			String filter = "ownerId == " + userId;
			dmgr.newQuery(DeviceInfo.class, filter).deletePersistentAll();

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			// Tried to delete no items
		} finally {
			dmgr.close();
		}

		// Delete all their comments
		PersistenceManager ecmgr = getPersistenceManager();
		try {
			String filter = "commenterId == " + userId;
			ecmgr.newQuery(EventComment.class, filter).deletePersistentAll();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			// Tried to delete no items
		} finally {
			ecmgr.close();
		}

		// Delete All Their Tags and these tag follows
		PersistenceManager tmgr = getPersistenceManager();
		try {

			String filter = "userId == " + userId;
			tmgr.newQuery(EventTag.class, filter).deletePersistentAll();

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			// Tried to delete no items
		} finally {
			tmgr.close();
		}

		// Delete Tag Follows
		// Delete their follows and people following their tags
		// Probably arent that many if they're deleting their account
		// =) LOSER.
		PersistenceManager tfmgr = getPersistenceManager();
		try {

			String filter = "tagOwnerId == " + userId;
			tfmgr.newQuery(EventTagFollow.class, filter).deletePersistentAll();

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			// Tried to delete no items
		} finally {
			tfmgr.close();
		}

		// Delete Tag Follows
		// Delete their follows and people following their tags
		// Probably arent that many if they're deleting their account
		// =) LOSER.
		PersistenceManager tfmgr2 = getPersistenceManager();
		try {

			String filter = "followerId == " + userId;
			tfmgr2.newQuery(EventTagFollow.class, filter).deletePersistentAll();

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			// Tried to delete no items
		} finally {
			tfmgr2.close();
		}

		// Delete Zeppa Events
		PersistenceManager emgr = getPersistenceManager();
		try {

			String filter = "hostId == " + userId;
			emgr.newQuery(ZeppaEvent.class, filter).deletePersistentAll();

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			// Tried to delete no items
		} finally {
			emgr.close();
		}

		// Delete Event to User Relationships involving this user
		PersistenceManager ermgr = getPersistenceManager();
		try {

			String filter = "userId == " + userId;
			ermgr.newQuery(ZeppaEventToUserRelationship.class, filter)
					.deletePersistentAll();

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			// Tried to delete no items
		} finally {
			ermgr.close();
		}

		// Delete Event to User Relationships involving this user
		PersistenceManager ermgr2 = getPersistenceManager();
		try {

			String filter = "eventHostId == " + userId;
			ermgr2.newQuery(ZeppaEventToUserRelationship.class, filter)
					.deletePersistentAll();

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			// Tried to delete no items
		} finally {
			ermgr2.close();
		}

		// Keep their feedback for now

		// Delete User To User Relationships they created
		PersistenceManager umgr = getPersistenceManager();
		try {

			String filter = "creatorId == " + userId;
			@SuppressWarnings("unchecked")
			List<ZeppaUserToUserRelationship> relationships = (List<ZeppaUserToUserRelationship>) umgr
					.newQuery(ZeppaUserToUserRelationship.class, filter)
					.execute();

			if (relationships != null && !relationships.isEmpty()) {
				Iterator<ZeppaUserToUserRelationship> iterator = relationships
						.iterator();
				while (iterator.hasNext()) {
					ZeppaUserToUserRelationship relationship = iterator.next();
					String payload = PayloadBuilder
							.silentUserRelationshipDeletedPayload(relationship
									.getSubjectId().longValue(), userId);
					NotificationUtility.preprocessNotificationDelivery(payload,
							relationship.getCreatorId().longValue());
				}

				umgr.deletePersistentAll(relationships);
			}

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			// Tried to delete no items
		} finally {
			umgr.close();
		}

		// Delete User To User Relationships their the subject to
		PersistenceManager umgr2 = getPersistenceManager();
		try {

			String filter = "subjectId == " + userId;
			@SuppressWarnings("unchecked")
			List<ZeppaUserToUserRelationship> relationships = (List<ZeppaUserToUserRelationship>) umgr2
					.newQuery(ZeppaUserToUserRelationship.class, filter)
					.execute();

			if (relationships != null && !relationships.isEmpty()) {
				Iterator<ZeppaUserToUserRelationship> iterator = relationships
						.iterator();
				while (iterator.hasNext()) {
					ZeppaUserToUserRelationship relationship = iterator.next();
					String payload = PayloadBuilder
							.silentUserRelationshipDeletedPayload(userId,
									relationship.getCreatorId().longValue());
					NotificationUtility.preprocessNotificationDelivery(payload,
							relationship.getCreatorId().longValue());
				}

				umgr2.deletePersistentAll(relationships);
			}

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			// Tried to delete no items
		} finally {
			umgr2.close();
		}

		// Delete Zeppa Notifications sent
		PersistenceManager nmgr = getPersistenceManager();
		try {
			String filter = "senderId == " + userId;
			nmgr.newQuery(ZeppaNotification.class, filter)
					.deletePersistentAll();

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			// Tried to delete no items
		} finally {
			nmgr.close();
		}

		// Delete Zeppa notifications received
		PersistenceManager nmgr2 = getPersistenceManager();
		try {
			String filter = "recipientId == " + userId;
			nmgr2.newQuery(ZeppaNotification.class, filter)
					.deletePersistentAll();

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			// Tried to delete no items
		} finally {
			nmgr2.close();
		}

	}

	private static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}

}
