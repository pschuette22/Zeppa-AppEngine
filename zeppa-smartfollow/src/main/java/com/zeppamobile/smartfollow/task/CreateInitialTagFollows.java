package com.zeppamobile.smartfollow.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

import com.zeppamobile.common.datamodel.EventTagFollow;
import com.zeppamobile.smartfollow.agent.TagAgent;
import com.zeppamobile.smartfollow.agent.UserAgent;

public class CreateInitialTagFollows extends SmartFollowTask {

	// Ids of users to determine what to follow automatically.
	private Long userId1, userId2;
	private UserAgent userAgent1, userAgent2;
	private List<Long> mutualMinglerIds = new ArrayList<Long>();

	// List of follows to be created as determined by this task
	private List<EventTagFollow> result = new ArrayList<EventTagFollow>();

	/**
	 * Create task to autofollow tags between users when they first connect.
	 * 
	 * @param userId1
	 * @param userId2
	 */

	public CreateInitialTagFollows(String taskName, Long userId1, Long userId2) {
		super(taskName);
		this.userId1 = userId1;
		this.userId2 = userId2;

	}

	@Override
	public void execute() {
		super.execute();
		// TODO Auto-generated method stub

		/*
		 * Prepare for calculations
		 */
		initAgents();

		/*
		 * Execute the calculation.
		 * This is a REALLY heavy opp
		 */
		result = getInitialTagFollows();

	}

	@Override
	public void finalize() {
		// TODO Auto-generated method stub
		/*
		 * execute task to create resulting follows
		 */
		System.out.print("Would be finalizing");
		/*
		 * do any cleanup
		 */

	}

	@Override
	public String abort(boolean doResume) {
		// // TODO Auto-generated method stub
		// JSONArray array = new JSONArray();
		//
		// /*
		// * Get json of userAgent1
		// */
		// JSONObject userAgent1Json = new JSONObject();
		//
		// array.put(userAgent1Json);
		// /*
		// * Get json of userAgent2
		// */
		// JSONObject userAgent2Json = new JSONObject();
		// array.put(userAgent2Json);
		//
		// // Return resulting string
		// return array.toString();
		return null;
	}

	/**
	 * Initialize all the agents needed to execute this task
	 */
	private void initAgents() {

		/*
		 * 
		 * Instantiate and initialize user1
		 */
		userAgent1 = new UserAgent(userId1);
		userAgent1.init(userId2);

		/*
		 * 
		 * Instantiate and initialize user2
		 */
		userAgent2 = new UserAgent(userId2);
		userAgent2.init(userId2);

		// Can assume that the User Agents have fetched all their mingling
		// relationships

		/*
		 * The next block deals with figuring out who the two minglers have in
		 * common.
		 */
		Iterator<Long> i1 = userAgent1.getOrderedMinglerList().iterator();
		Iterator<Long> i2 = userAgent2.getOrderedMinglerList().iterator();
		Long user1Mingler = i1.next();
		Long user2Mingler = i2.next();

		// Keep going till NoSuchElement exception thrown
		try {
			do {
				if (user1Mingler.longValue() == user2Mingler.longValue()) {
					// Mingler id's are equal. This is a common mingler
					mutualMinglerIds.add(user1Mingler);
					user1Mingler = i1.next();
					user2Mingler = i2.next();
				} else if (user1Mingler.longValue() > user2Mingler.longValue()) {
					// Increment user 2 minglers
					user2Mingler = i2.next();
				} else { // user1Mingler < user2Mingler
					user1Mingler = i1.next();
				}

			} while (true);
		} catch (NoSuchElementException e) {
			// Expected this to happen
		}
	}

	/**
	 * This is the bulk algorithm Use calculated similarities and collaborative
	 * filtering to figure out who should follow this tag
	 * 
	 * @param agent1
	 * @param agent2
	 * @param tagSimilarities
	 * @return
	 */
	public List<EventTagFollow> getInitialTagFollows() {
		List<EventTagFollow> result = new ArrayList<EventTagFollow>();

		List<TagAgent> u1TagAgents = userAgent1.getTagAgents();
		List<TagAgent> u2TagAgents = userAgent2.getTagAgents();

		double[][] calculatedSimilarities = calculateTagSimilarities(
				u1TagAgents, u2TagAgents);

		/*
		 * Calculate adjusted interest and store in hash maps where the agent is
		 * key Adjusted interest is used in case there are multiple tags that
		 * are similar
		 */
		Map<TagAgent, Double> ua1AdjustedInterest = new HashMap<TagAgent, Double>();
		Map<TagAgent, Double> ua2AdjustedInterest = new HashMap<TagAgent, Double>();

		/*
		 * Calculate adjusted Interest for user1 tags
		 */
		for (int i = 0; i < u1TagAgents.size(); i++) {
			double adjustedInterest = 0;
			List<Double> sortedInterestValues = new ArrayList<Double>();
			for (int j = 0; j < u2TagAgents.size(); i++) {
				sortedInterestValues.add(Double
						.valueOf(calculatedSimilarities[i][j]));
			}
			// Sort the corresponding interests from high to low
			Collections.sort(sortedInterestValues, Collections.reverseOrder());

			// Only average up to 5 tags
			for (int k = 0; k < (u2TagAgents.size() > 5 ? 5 : u2TagAgents
					.size()); k++) {
				double interest = sortedInterestValues.get(k);
				if (interest < .2) {
					// Only consider interests matching with at least 20 percent
					break;
				} else if (interest < 1) {

					// Take a percent of the remaining interest percentage
					adjustedInterest += ((1 - adjustedInterest) * interest);

				} else {
					// Tag complete match
					// Set interest to 100% and break
					adjustedInterest = 1;
					break;
				}
			}
			ua1AdjustedInterest.put(u1TagAgents.get(i), adjustedInterest);

		}

		/*
		 * Calculate adjusted interest for user2 tags
		 */
		for (int i = 0; i < u2TagAgents.size(); i++) {
			double adjustedInterest = 0;
			List<Double> sortedInterestValues = new ArrayList<Double>();
			for (int j = 0; j < u1TagAgents.size(); i++) {
				sortedInterestValues.add(Double
						.valueOf(calculatedSimilarities[i][j]));
			}
			// Sort the corresponding interests from high to low
			Collections.sort(sortedInterestValues, Collections.reverseOrder());

			// Only average up to 5 tags
			for (int k = 0; k < (u1TagAgents.size() > 5 ? 5 : u1TagAgents
					.size()); k++) {
				double interest = sortedInterestValues.get(k);
				if (interest < .1) {
					// Only consider interests matching with at least 10 percent
					break;
				} else if (interest < 1) {

					// Interest should be added in as a percent of the
					// percent remaining
					adjustedInterest += ((1 - adjustedInterest) * interest);

				} else {
					// Tag complete match
					// Set interest to 100% and break
					adjustedInterest = 1;
					break;
				}
			}
			ua2AdjustedInterest.put(u2TagAgents.get(i), adjustedInterest);
		}

		/*
		 * 
		 * Calculate adjusted amount of overlapping interest
		 * 
		 * Average adjusted interests to calculate total overlap as a percent
		 */
		Collection<Double> ua1InterestSimilarities = ua2AdjustedInterest
				.values();
		Collection<Double> ua2InterestSimilarities = ua1AdjustedInterest
				.values();
		double ua1InterestSimilarity = 0, ua2InterestSimilarity = 0;
		for (Double d : ua1InterestSimilarities) {
			ua1InterestSimilarity += d.doubleValue();
		}
		// Calculated percentage of interest overlap
		ua1InterestSimilarity /= ua1InterestSimilarities.size();
		for (Double d : ua2InterestSimilarities) {
			ua2InterestSimilarity += d.doubleValue();
		}
		// Calculated percentage of interest overlap
		ua2InterestSimilarity /= ua2InterestSimilarities.size();

		/*
		 * Set multiplier adjustment for calculated user popularity The bigger
		 * the disparage in calculated popularity, the less likely to follow a
		 * tag
		 */
		double u1Popularity = userAgent1
				.getCalculatedPopularity(mutualMinglerIds);
		double u2Popularity = userAgent2
				.getCalculatedPopularity(mutualMinglerIds);
		double userPopularityAdjustment = -1;
		if (u1Popularity >= 0 && u2Popularity >= 0) {
			// Determine the calculated difference in user popularity
			userPopularityAdjustment = 1 - (Math
					.abs((u1Popularity - u2Popularity)));
		}

		/*
		 * Set multiplier adjustment for calculated event popularity The bigger
		 * the disparage in calculated event popularity, the less likely to
		 * follow a tag
		 */
		double ue1Popularity = userAgent1.getCalculatedEventPopularity();
		double ue2Popularity = userAgent2.getCalculatedEventPopularity();
		double eventPopularityAdjustment = -1;
		if (ue1Popularity >= 0 && ue2Popularity >= 0) {
			eventPopularityAdjustment = 1 - (Math.abs(ue1Popularity
					- ue2Popularity));
		}

		/*
		 * Get calculated similarity both users have to the other
		 */
		double u1Similarity = calculateRelativeUserSimilarity(
				ua1InterestSimilarity, userPopularityAdjustment,
				eventPopularityAdjustment);
		double u2Similarity = calculateRelativeUserSimilarity(
				ua2InterestSimilarity, userPopularityAdjustment,
				eventPopularityAdjustment);

		/*
		 * Necessary calculations have been made, run through and determine who
		 * to follow and who not to follow
		 */

		/*
		 * Iterate through entry set of user1's tag agents with calculated
		 * similarities to user2's tags If nothing else, this is the base for
		 * predicting if user2 should follow this tag
		 */
		int u1MinglerCount = userAgent1.getMinglingRelationships().size();
		Set<Entry<TagAgent, Double>> u1Set = ua1AdjustedInterest.entrySet();
		for (Entry<TagAgent, Double> entry : u1Set) {
			// Get the base interest of this TagAgent
			TagAgent agent = entry.getKey();
			double interest = entry.getValue().doubleValue();
			// Don't bother if adjusted interest is below 15%
			if (interest <= .15) {
				continue;
			}
			double tagPopularity = agent.getCalculatedPopularity(
					u1MinglerCount, mutualMinglerIds);
			// Initial interest calculation
			// This holds half the weight

			double calculation = interest * .5;
			double calculationWeight = .5;

			if (u2Similarity > 0) {
				calculation += .3 * u2Similarity;
				calculationWeight += .3;
			}

			if (tagPopularity > 0) {
				calculation += .2 * tagPopularity;
				calculationWeight += .2;
			}

//			if ((calculation / calculationWeight) >= Constants.MIN_INTEREST_TO_FOLLOW) {
//				EventTagFollow follow = new EventTagFollow(agent.getTag(),
//						userAgent2.getUserId());
//				result.add(follow);
//			}

		}

		/*
		 * Do the same thing for the other user
		 */
		int u2MinglerCount = userAgent2.getMinglingRelationships().size();
		Set<Entry<TagAgent, Double>> u2Set = ua2AdjustedInterest.entrySet();
		for (Entry<TagAgent, Double> entry : u2Set) {
			// Get the base interest of this TagAgent
			TagAgent agent = entry.getKey();
			double interest = entry.getValue().doubleValue();
			// Don't bother if adjusted interest is at or below 15%
			if (interest <= .15) {
				continue;
			}
			double tagPopularity = agent.getCalculatedPopularity(
					u2MinglerCount, mutualMinglerIds);

			double calculation = interest * .5;
			double calculationWeight = .5;

			if (u1Similarity > 0) {
				calculation += .3 * u1Similarity;
				calculationWeight += .3;
			}

			if (tagPopularity > 0) {
				calculation += .2 * tagPopularity;
				calculationWeight += .2;
			}

//			if ((calculation / calculationWeight) >= Constants.MIN_INTEREST_TO_FOLLOW) {
//				EventTagFollow follow = new EventTagFollow(agent.getTag(),
//						userAgent1.getUserId());
//				result.add(follow);
//			}

		}

		return result;
	}

	/**
	 * Calculate the adjusted similarity value provided given user
	 * 
	 * @param interestSimilarityAdjustment
	 * @param userPopularityAdjustment
	 * @param eventPopularityAdjustment
	 * @return calculated adjusted user similarity
	 */
	private double calculateRelativeUserSimilarity(
			double interestSimilarityAdjustment,
			double userPopularityAdjustment, double eventPopularityAdjustment) {
		double result = 0;
		double totalWeight = 0;

		/*
		 * 40% weight for interest similarity adjustment
		 * 
		 * Overall similarity of users tags If users tend to be into similar
		 * things, likelihood of following tag should be increased
		 */
		if (interestSimilarityAdjustment >= 0) {
			result += (interestSimilarityAdjustment * .4);
			totalWeight += .4;
		}

		/*
		 * 30% weight
		 * 
		 * User popularity adjustment is the calculated difference in overall
		 * user popularity based on number of connections, times invited to
		 * something, and mutual percentage of minglers users have in common
		 * 
		 * popularity adjustment represents the difference between provided
		 * users.
		 */
		if (userPopularityAdjustment >= 0) {
			result += userPopularityAdjustment * .3;
			totalWeight += .3;
		}

		/*
		 * 30% weight for event popularity adjustment if applicable
		 */
		if (eventPopularityAdjustment >= 0) {
			result += eventPopularityAdjustment * .3;
			totalWeight += .3;
		}

		/*
		 * This shouldnt happen, but make sure that if calculated relative user
		 * similarity could be calculated
		 */
		if (totalWeight == 0) {
			return -1;
		}

		return result;
	}

}
