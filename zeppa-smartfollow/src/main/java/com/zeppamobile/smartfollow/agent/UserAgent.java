package com.zeppamobile.smartfollow.agent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.api.server.spi.response.CollectionResponse;
import com.zeppamobile.api.datamodel.EventTag;
import com.zeppamobile.api.datamodel.EventTagFollow;
import com.zeppamobile.api.datamodel.ZeppaEvent;
import com.zeppamobile.api.datamodel.ZeppaEventToUserRelationship;
import com.zeppamobile.api.datamodel.ZeppaUserToUserRelationship;
import com.zeppamobile.api.endpoint.EventTagEndpoint;
import com.zeppamobile.api.endpoint.ZeppaEventEndpoint;
import com.zeppamobile.api.endpoint.ZeppaEventToUserRelationshipEndpoint;
import com.zeppamobile.api.endpoint.ZeppaUserToUserRelationshipEndpoint;
import com.zeppamobile.smartfollow.Utils;

public class UserAgent {

	/*
	 * Values needed to calculations for this agent
	 */
	private Long userId;
	private List<ZeppaUserToUserRelationship> minglingRelationships = new ArrayList<ZeppaUserToUserRelationship>();
	private List<ZeppaEventToUserRelationship> eventRelationships = new ArrayList<ZeppaEventToUserRelationship>();
	private List<EventAgent> events = new ArrayList<EventAgent>();
	private List<TagAgent> tags = new ArrayList<TagAgent>();

	/*
	 * The resulting list of follows that should be created for this users tags
	 */
	private List<EventTagFollow> result = new ArrayList<EventTagFollow>();

	/**
	 * UserAgent is a wrapper class for zeppaUser to calculate other user's
	 * interest in joining their stuff
	 * 
	 * @param userId
	 *            - userId of user agent should represent
	 */
	public UserAgent(Long userId) {
		this.userId = userId;
	}

	/**
	 * Fetch all entities associated with this user
	 * 
	 * @param otherUserId
	 *            - userId of user agent is being compared to
	 */
	public void init(Long otherUserId) {
		fetchMinglingRelationships(otherUserId);
		fetchEvents();
		fetchEventRelationships();
		fetchTags();
	}

	public Long getUserId(){
		return userId;
	}
	
	public List<TagAgent> getTagAgents() {
		return tags;
	}

	public List<ZeppaUserToUserRelationship> getMinglingRelationships() {
		return minglingRelationships;
	}

	/**
	 * @return List of ID's user is mingling with ordered from low to high
	 */
	public List<Long> getOrderedMinglerList() {
		List<Long> result = new ArrayList<Long>();

		if (!minglingRelationships.isEmpty()) {
			Iterator<ZeppaUserToUserRelationship> iterator = minglingRelationships
					.iterator();
			while (iterator.hasNext()) {
				try {
					result.add(iterator.next().getOtherUserId(userId));
				} catch (NullPointerException e) {
					// catch this if the relationship doesn't actually involve
					// this user
					// TODO: remove it?
				}
			}
			// Make sure list can be sorted
			if (result.size() > 1) {
				Collections.sort(result);
			}

		}

		return result;
	}

	/**
	 * get calculated popularity of this user. Adjusted involves a mutual
	 * mingler
	 * 
	 * @param mutualMinglers
	 *            - list of ids of people both users mingle with.
	 * @return popularity calculation as decimal percent
	 */
	public double getCalculatedPopularity(List<Long> mutualMinglerIds) {
		double calculation = -1;

		/*
		 * If there are event relationships, can estimate popularity among
		 * minglers
		 */
		if (!eventRelationships.isEmpty()) {
			calculation = 0;

			int invitedCount = 0, mmInvitedCount = 0, recommendedCount = 0, attendedCount = 0;

			for (ZeppaEventToUserRelationship relationship : eventRelationships) {

				// If user was invited to event, increment
				if (relationship.getWasInvited()) {
					invitedCount++;
					// If a mutual mingler invited, even better
					if (!mutualMinglerIds.isEmpty()
							&& Utils.listContainsId(mutualMinglerIds,
									relationship.getInvitedByUserId())) {
						mmInvitedCount++;
					}
				}

				// If event was recommended, increment
				if (relationship.getIsRecommended()) {
					recommendedCount++;
				}

				// If user attended this event, increment
				if (relationship.getIsAttending()) {
					attendedCount++;
				}

			}

			/*
			 * Perform adjustment calculations
			 */

			// 25 percent weight to being invited to an event
			calculation += .25 * (invitedCount / eventRelationships.size());
			// 40 percent weight to being invited to an event by a mutual
			// mingler
			calculation += .4 * (mmInvitedCount / eventRelationships.size());
			// 15 percent weight to event being recommended
			calculation += .15 * (recommendedCount / eventRelationships.size());
			// 20 percent weight to attending event
			calculation += .2 * (attendedCount / eventRelationships.size());

		}

		return calculation;
	}

	/**
	 * Calculates relative popularity of events
	 * 
	 * @return popularity as a decimal percent (-1 if user hasnt started
	 *         anything)
	 */
	public double getCalculatedEventPopularity() {

		if (events.isEmpty()) {
			return -1;
		} else {
			double calculation = 0;
			int addedEvents = 0;
			for (EventAgent event : events) {
				double popularity = event.getCalculatedPopularity();
				if (popularity >= 0) {
					calculation += popularity;
					addedEvents++;
				}
			}

			if (addedEvents == 0) {
				return -1;
			} else {
				return (calculation / addedEvents);
			}
		}
	}

	private void fetchTags() {
		EventTagEndpoint endpoint = new EventTagEndpoint();
		// Consider fetching some amount at a time and using cursors for
		// efficiency
		CollectionResponse<EventTag> response = endpoint.listEventTag(
				"ownerId==" + userId, null, null, null);
		try {
			for (EventTag tag : response.getItems()) {
				tags.add(new TagAgent(this, tag));
			}
		} catch (NullPointerException e) {

		}

	}

	/**
	 * Fetch all relationships this user has to others
	 * 
	 * @param otherUser
	 *            - user in question's ID to be ignored when fetching
	 *            relationships
	 */
	private void fetchMinglingRelationships(Long otherUserId) {

		// Fetch data from zeppa-api
		ZeppaUserToUserRelationshipEndpoint endpoint = new ZeppaUserToUserRelationshipEndpoint();
		CollectionResponse<ZeppaUserToUserRelationship> response = endpoint
				.listZeppaUserToUserRelationship(
						"creatorUserId=="
								+ userId
								+ " && relationshipType == 'MINGLING' && subjectUserId !="
								+ otherUserId, null, null, null);
		CollectionResponse<ZeppaUserToUserRelationship> response2 = endpoint
				.listZeppaUserToUserRelationship(
						"subjectUserId=="
								+ userId
								+ " && relationshipType == 'MINGLING' && creatorUserId !="
								+ otherUserId, null, null, null);

		// Add all the responses. ignore if null response is returned
		try {
			minglingRelationships.addAll(response.getItems());
		} catch (NullPointerException e) {
		}
		try {
			minglingRelationships.addAll(response2.getItems());
		} catch (NullPointerException e) {
		}
	}

	/**
	 * This fetches all the relationships this user has to other events.
	 */
	private void fetchEventRelationships() {
		ZeppaEventToUserRelationshipEndpoint endpoint = new ZeppaEventToUserRelationshipEndpoint();
		CollectionResponse<ZeppaEventToUserRelationship> response = endpoint
				.listZeppaEventToUserRelationship(
						"userId==" + userId.longValue(), null, null, null);
		try {
			eventRelationships.addAll(response.getItems());
		} catch (NullPointerException e) {
		}
	}

	/**
	 * Fetch events relative to other user and create agents
	 */
	private void fetchEvents() {

		ZeppaEventEndpoint endpoint = new ZeppaEventEndpoint();
		CollectionResponse<ZeppaEvent> response = endpoint.listZeppaEvent(
				"hostId==" + userId, null, null, null);

		ZeppaEventToUserRelationshipEndpoint relationshipEndpoint = new ZeppaEventToUserRelationshipEndpoint();

		// Concerned about memory
		// TODO: implement cursor and fetch limit
		// Possibly only fetch events created in the past 6 months
		CollectionResponse<ZeppaEventToUserRelationship> relationshipResponse = relationshipEndpoint
				.listZeppaEventToUserRelationship("hostId==" + userId, null,
						"eventId DESC", null);
		List<ZeppaEventToUserRelationship> allEventRelationships = new ArrayList<ZeppaEventToUserRelationship>();
		try {
			allEventRelationships.addAll(relationshipResponse.getItems());
		} catch (NullPointerException e) {
			// catch it if there are no relationships to events. Shouldnt happen
			// TODO: log this
		}

		try {
			for (ZeppaEvent event : response.getItems()) {
				// TODO: initialize event agent with event relationships
				EventAgent agent = new EventAgent(event);
				agent.pruneRelationships(allEventRelationships);
				events.add(agent);
			}
		} catch (NullPointerException e) {
			// null response returned
		}

	}

}
