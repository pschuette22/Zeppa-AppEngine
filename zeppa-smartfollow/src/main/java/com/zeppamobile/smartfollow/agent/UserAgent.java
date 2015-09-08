package com.zeppamobile.smartfollow.agent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import com.zeppamobile.common.datamodel.EventTag;
import com.zeppamobile.common.datamodel.ZeppaEvent;
import com.zeppamobile.common.datamodel.ZeppaEventToUserRelationship;
import com.zeppamobile.common.datamodel.ZeppaUserToUserRelationship;
import com.zeppamobile.common.utils.JSONUtils;
import com.zeppamobile.common.utils.ModuleUtils;
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

	public Long getUserId() {
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

	/**
	 * Fetch the EventTag objects owned by this user and add them as tag agents
	 */
	private void fetchTags() {

		try {
			Dictionary<String, String> params = new Hashtable<String, String>();
			params.put("filter", "ownerId==" + userId);

			// TODO: implement cursor
			URL url = ModuleUtils.getZeppaAPIUrl("listEventTag", params);

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					url.openStream()));

			StringBuilder builder = new StringBuilder();

			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}

			List<EventTag> tags = JSONUtils.convertEventTagListString(builder
					.toString());

			if (!tags.isEmpty()) {
				for (EventTag tag : tags) {
					TagAgent agent = new TagAgent(this, tag);
					this.tags.add(agent);
				}
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

		// // Fetch data from zeppa-api
		

		// Get all the relationships this user created
		try {

			Dictionary<String, String> params = new Hashtable<String, String>();

			params.put("filter", "creatorUserId==" + userId
					+ " && relationshipType == 'MINGLING' && subjectUserId !="
					+ otherUserId);

			URL urlMinglingCreated = ModuleUtils.getZeppaAPIUrl(
					"listZeppaUserToUserRelationship", params);

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					urlMinglingCreated.openStream()));

			StringBuilder builder = new StringBuilder();

			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}

			List<ZeppaUserToUserRelationship> result = JSONUtils
					.convertUserRelationshipListString(builder.toString());
			minglingRelationships.addAll(result);

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Get all the relationships this user is the subject of
		try {

			Dictionary<String, String> params = new Hashtable<String, String>();

			params.put("filter", "subjectUserId==" + userId
					+ " && relationshipType == 'MINGLING' && creatorUserId !="
					+ otherUserId);

			URL urlMinglingCreated = ModuleUtils.getZeppaAPIUrl(
					"listZeppaUserToUserRelationship", params);

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					urlMinglingCreated.openStream()));

			StringBuilder builder = new StringBuilder();

			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}

			List<ZeppaUserToUserRelationship> result = JSONUtils
					.convertUserRelationshipListString(builder.toString());
			minglingRelationships.addAll(result);

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * This fetches all the relationships this user has to other events.
	 */
	private void fetchEventRelationships() {


		try {

			Dictionary<String, String> params = new Hashtable<String, String>();
			params.put("filter", "userId==" + userId.longValue());

			URL eventRelationshipsURL = ModuleUtils.getZeppaAPIUrl(
					"listZeppaEventToUserRelationship", params);

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					eventRelationshipsURL.openStream()));

			StringBuilder builder = new StringBuilder();

			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}

			List<ZeppaEventToUserRelationship> result = JSONUtils
					.convertEventRelationshipListString(builder.toString());
			eventRelationships.addAll(result);

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Fetch events relative to other user and create agents
	 */
	private void fetchEvents() {
		
		// Fetch all events for this user
		try {

			Dictionary<String, String> params = new Hashtable<String, String>();
			params.put("filter", "hostId==" + userId);

			URL eventRelationshipsURL = ModuleUtils.getZeppaAPIUrl(
					"listZeppaEvent", params);

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					eventRelationshipsURL.openStream()));

			StringBuilder builder = new StringBuilder();

			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}

			List<ZeppaEvent> result = JSONUtils
					.convertEventListString(builder.toString());
			
			for(ZeppaEvent event: result){
				EventAgent eventAgent = new EventAgent(event);
//				eventAgent.pruneRelationships(allEventRelationships);
				events.add(eventAgent);
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		

	}

}
