package com.zeppamobile.smartfollow.agent;

import com.zeppamobile.common.cerealwrapper.EventCerealWrapper;
import com.zeppamobile.common.cerealwrapper.EventRelationshipCerealWrapper;
import com.zeppamobile.common.cerealwrapper.EventTagCerealWrapper;

/**
 * 
 * Agent wrapper for a ZeppaEvent
 * 
 * @author Pete Schuette
 *
 */
public class EventAgent extends BaseAgent {

	private EventCerealWrapper event;

	// Total calculated popularity of a given event
	// -1 before it is instantiated
	private double calculatedPopularity = -1;

	/**
	 * Initialize an event agent
	 * 
	 * @param event
	 *            - event to wrap around
	 */
	public EventAgent(EventCerealWrapper event) {
		this.event = event;
		if (!event.isNewEvent()) {
			calculatePopularity();
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
			for (EventTagCerealWrapper info : event.getTags()) {
				if (info.getTagId() == tagId) {
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

		if (event.getRelationships().isEmpty()) {
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
		double total = event.getRelationships().size();
		double totInterested = 0;

		/*
		 * Iterate through all relationships and determine
		 */
		for (EventRelationshipCerealWrapper relationship : event.getRelationships()) {
			// Event was recommended
			if (relationship.isRecommended()) {
				recommended += 1;
				if (relationship.isAttending()) {
					// If they joined, increment
					recInterested += 1;
				} else if (relationship.isAttending()) {
					// If they didnt join but watched it (interested), partially
					// increment
					recInterested += .6;
				}
			}

			// Was invited to event
			// TODO: implement logic if host invited or another user did
			if (relationship.getInviterId() > 0) {
				invited += 1;
				if (relationship.isAttending()) {
					// If they joined, increment
					invInterested += 1;
				} else if (relationship.isWatching()) {
					// If they didnt join but watched it (interested), partially
					// increment
					invInterested += .6;
				}
			}

			if (relationship.isAttending()) {
				// If they joined, increment
				totInterested += 1;
			} else if (relationship.isWatching()) {
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
