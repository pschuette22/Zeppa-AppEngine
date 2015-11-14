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
import java.util.List;

import javax.servlet.ServletContext;

import com.zeppamobile.common.datainfo.EventRelationshipInfo;
import com.zeppamobile.common.datainfo.UserDataInfo;
import com.zeppamobile.common.utils.JSONUtils;
import com.zeppamobile.common.utils.ModuleUtils;
import com.zeppamobile.smartfollow.Utils;

public class UserAgent {

	/*
	 * Values needed to calculations for this agent
	 */
	private UserDataInfo userData;
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
	public UserAgent(UserDataInfo userData) {
		this.userData = userData;
	}

	public Long getUserId() {
		return userData.getIdentifier();
	}

	public List<TagAgent> getTagAgents() {
		return tags;
	}

	/**
	 * @return List of ID's user is mingling with ordered from low to high
	 */
	public List<Long> getOrderedMinglerList() {
		List<Long> result = userData.getMinglerIds();

		// Make sure list can be sorted
		if (result.size() > 1) {
			Collections.sort(result);
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
		if (!userData.getEventRelationships().isEmpty()) {
			calculation = 0;

			int invitedCount = 0, mmInvitedCount = 0, recommendedCount = 0, attendedCount = 0;

			for (EventRelationshipInfo relationship : userData.getEventRelationships()) {

				// If user was invited to event, increment
				if (relationship.getInviterId() > 0) {
					invitedCount++;
					// If a mutual mingler invited, even better
					if (!mutualMinglerIds.isEmpty()
							&& Utils.listContainsId(mutualMinglerIds,
									relationship.getInviterId())) {
						mmInvitedCount++;
					}
				}

				// If event was recommended, increment
				if (relationship.isRecommended()) {
					recommendedCount++;
				}

				// If user attended this event, increment
				if (relationship.isAttending()) {
					attendedCount++;
				}

			}

			/*
			 * Perform adjustment calculations
			 */

			int relationshipCount = userData.getEventRelationships().size();
			
			// 25 percent weight to being invited to an event
			calculation += .25 * (invitedCount / relationshipCount);
			// 40 percent weight to being invited to an event by a mutual
			// mingler
			calculation += .4 * (mmInvitedCount / relationshipCount);
			// 15 percent weight to event being recommended
			calculation += .15 * (recommendedCount / relationshipCount);
			// 20 percent weight to attending event
			calculation += .2 * (attendedCount / relationshipCount);

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

}
