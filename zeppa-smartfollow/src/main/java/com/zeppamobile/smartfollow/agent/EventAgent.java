package com.zeppamobile.smartfollow.agent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import com.zeppamobile.common.datamodel.ZeppaEvent;
import com.zeppamobile.common.datamodel.ZeppaEventToUserRelationship;
import com.zeppamobile.common.utils.JSONUtils;
import com.zeppamobile.common.utils.ModuleUtils;

/**
 * 
 * Agent wrapper for a ZeppaEvent
 * 
 * @author Pete Schuette
 *
 */
public class EventAgent {

	private ZeppaEvent event;
	private List<ZeppaEventToUserRelationship> relationships = new ArrayList<ZeppaEventToUserRelationship>();

	// Total calculated popularity of a given event
	// -1 before it is instantiated
	private double calculatedPopularity = -1;

	/**
	 * Initialize an event agent
	 * 
	 * @param event
	 *            - event to wrap around
	 */
	public EventAgent(ZeppaEvent event) {
		this.event = event;
		fetchEventRelationships();
		calculatePopularity();
	}

//	/**
//	 * 
//	 * This takes a list of all relationships to events created by a given user
//	 * and adds those relative to this event This is implemented in this way to
//	 * minimize datastore reads
//	 * 
//	 * @param relationships
//	 *            - all Event Relationships to events with a similar host
//	 * @return relationships without those relative to this event
//	 */
//	public List<ZeppaEventToUserRelationship> pruneRelationships(
//			List<ZeppaEventToUserRelationship> relationships) {
//
//		// Pull out all relationships to this event from all relationships
//		for (ZeppaEventToUserRelationship relationship : relationships) {
//			if (relationship.getEventId().longValue() == event.getId()
//					.longValue()) {
//				this.relationships.add(relationship);
//			}
//		}
//
//		// Remove relationships that were picked out
//		relationships.removeAll(this.relationships);
//
//		return relationships;
//	}

	/**
	 * Fetch all event relationships for this event
	 * 
	 * @throws MalformedURLException
	 * 
	 */
	private void fetchEventRelationships() {

		// Call the API module via HTTP GET request
		Dictionary<String, String> params = new Hashtable<String, String>();
		params.put("filter", "eventId==" + event.getId());

		try {
			URL url = ModuleUtils.getZeppaAPIUrl(
					"listZeppaEventToUserRelationshipJson/", params);

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					url.openStream()));

			StringBuilder responseBuilder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				responseBuilder.append(line);
			}

			this.relationships = JSONUtils
					.convertEventRelationshipListString(responseBuilder
							.toString());

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// Network or auth error
			// } catch (JSONException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// // Bad response
		} catch (Exception e) {
			// issue with the JSON
			// Treated as if there are no relationships to event
			this.relationships = new ArrayList<ZeppaEventToUserRelationship>();
		}

	}

	/**
	 * Determine if event has tag attached
	 * 
	 * @param tagId
	 * @return true if provided tagId matches an id event tags
	 */
	public boolean containsTag(long tagId) {
		try {
			for (long l : event.getTagIds()) {
				if (l == tagId) {
					return true;
				}
			}
		} catch (Exception e) {
			// Event has no tags
			// This could happen if they deleted all tags attached to this event
		}

		return false;
	}

	/*
	 * 
	 * Constant ratio weight values
	 */

	private static final double RECOMMENDED_RATIO_WEIGHT = .4;
	private static final double INVITED_RATIO_WEIGHT = .5;
	private static final double TOTAL_RATIO_WEIGHT = .1; // low weight because
															// user wasn't
															// notified

	/**
	 * This is the bulk of this agent. Determine calculated popularity of this
	 * event
	 */
	public void calculatePopularity() {

		if (relationships.isEmpty()) {
			// No relationship to this event.
			calculatedPopularity = .5;
			return;
		}

		/*
		 * recommend:interest ratio
		 */
		double recommended = 0;
		double recInterested = 0;

		/*
		 * invited:interest ratio
		 */

		double invited = 0;
		double invInterested = 0;

		/*
		 * total:interested ratio
		 */
		double total = relationships.size();
		double totInterested = 0;

		/*
		 * Iterate through all relationships and determine
		 */
		for (ZeppaEventToUserRelationship relationship : relationships) {
			// Event was recommended
			if (relationship.getIsRecommended()) {
				recommended += 1;
				if (relationship.getIsAttending()) {
					// If they joined, increment
					recInterested += 1;
				} else if (relationship.getIsWatching()) {
					// If they didnt join but watched it (interested), partially
					// increment
					recInterested += .6;
				}
			}

			// Was invited to event
			// TODO: implement logic if host invited or another user did
			if (relationship.getWasInvited()) {
				invited += 1;
				if (relationship.getIsAttending()) {
					// If they joined, increment
					invInterested += 1;
				} else if (relationship.getIsWatching()) {
					// If they didnt join but watched it (interested), partially
					// increment
					invInterested += .6;
				}
			}

			if (relationship.getIsAttending()) {
				// If they joined, increment
				totInterested += 1;
			} else if (relationship.getIsWatching()) {
				// If they didnt join but watched it (interested), partially
				// increment
				totInterested += .6;
			}

		}

		// initalize popularity to 1
		double unweighedPopularity = 0;
		double calculatedWeight = TOTAL_RATIO_WEIGHT;

		// Determine ratio of recommended to join, if event was recommended
		if (recommended > 0) {
			double recInterestRatio = (recInterested / recommended);
			unweighedPopularity += (recInterestRatio * RECOMMENDED_RATIO_WEIGHT);
			calculatedWeight += RECOMMENDED_RATIO_WEIGHT;
		}
		// Determine ratio of invited to join, if invited to event
		if (invited > 0) {
			double invInterestRatio = (invInterested / invited);
			unweighedPopularity += (invInterestRatio * INVITED_RATIO_WEIGHT);
			calculatedWeight += INVITED_RATIO_WEIGHT;
		}

		// Determine total number of people that joined vs total number who
		// could
		double totInterestRatio = (totInterested / total);
		unweighedPopularity += (totInterestRatio * TOTAL_RATIO_WEIGHT);

		calculatedPopularity = (unweighedPopularity / calculatedWeight);

	}

	/**
	 * 
	 * @return popularity as decimal percent (-1 if uncalculated or error)
	 */
	public double getCalculatedPopularity() {
		return calculatedPopularity;
	}

}
