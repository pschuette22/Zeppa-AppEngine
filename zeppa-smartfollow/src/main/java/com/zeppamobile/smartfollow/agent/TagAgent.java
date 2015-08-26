package com.zeppamobile.smartfollow.agent;

import java.util.ArrayList;
import java.util.List;

import com.google.api.server.spi.response.CollectionResponse;
import com.zeppamobile.api.datamodel.EventTag;
import com.zeppamobile.api.datamodel.EventTagFollow;
import com.zeppamobile.api.endpoint.EventTagFollowEndpoint;

public class TagAgent {

	private EventTag tag;
	private List<EventTagFollow> tagFollows = new ArrayList<EventTagFollow>();
	private List<String> convertedTag = new ArrayList<String>();

	/*
	 * Values used to calculate predicted interest
	 */
	private double calculatedPopularity = 1;
	private static final double POPULARITY_WEIGHT = .3;

	private double calculatedPopularityForMutualMinglers = 1;
	private static final double POPULARITY_AMONG_MINGLERS_WEIGHT = .5;

	private double calculatedHeldEventPopularity = 1;
	private static final double EVENT_POPULARITY_WEIGHT = .2;

	// This is the total calculated interest for this tag
	private double calculatedInterest;

	public TagAgent(UserAgent userAgent, EventTag tag) {
		this.tag = tag;
		fetchTagFollows();
	}

	/**
	 * Quickly fetch all the follows for this tag
	 */
	private void fetchTagFollows() {
		EventTagFollowEndpoint endpoint = new EventTagFollowEndpoint();

		CollectionResponse<EventTagFollow> response = endpoint
				.listEventTagFollow("tagId==" + tag.getId(), null, null, null);
		tagFollows.addAll(response.getItems());
	}

	/**
	 * Calculate the interest a given user has in this tag
	 * 
	 * @param ownerAgent
	 *            - UserAgent representing the tag owner
	 * @param followerAgent
	 *            - UserAgent for the potential follower
	 */
	public void calculateInterest(UserAgent ownerAgent, UserAgent followerAgent) {

	}

	public double getCalculatedInterest() {
		return calculatedInterest;
	}

	/**
	 * Calculate the similarity of this tag to another (between 0 and 1)
	 * 
	 * @param tag
	 *            - tag to compare this tag to
	 * @return calculatedSimilarity as a decimal percent
	 */
	public double calculateSimilarity(TagAgent tag) {
		double similarity = 0;

		if (tag.getTagText().equalsIgnoreCase(tag.getTagText())) {
			// Tags are the same, ignoring case
			similarity = 1; // 100 percent similar.
		} else {

		}
		return similarity;
	}

	/**
	 * Get the separated list of words in this tag with openNLP Part of Speech
	 * tag added
	 * 
	 * @return
	 */
	public List<String> setTaggedWords() {

		
		List<String> result = new ArrayList<String>();
		StringBuilder builder = new StringBuilder();
		String tagText = tag.getTagText();

		// If characters are all upper case, we'll
		if (allUpperCase(tagText)) {
			result.add(tagText);
		} else {

			for (int i = tagText.length(); i >= 0; --i) {
				char ch = tagText.charAt(i);

				if (Character.isUpperCase(ch)) {
					// Words are separated by Upper Case Character by default
					// If an upper case character is encountered, separate it by
					builder.append(ch);
					result.add(0, builder.toString());
					builder = new StringBuilder();
				} else if (Character.isDigit(ch)) {

				}

			}
		}

		return result;
	}

	/**
	 * Determine if all characters in a string are upper case
	 * 
	 * @param tagText
	 * @return
	 */
	private boolean allUpperCase(String tagText) {

		for (int i = 0; i < tagText.length(); i++) {
			char c = tagText.charAt(i);
			if (Character.isAlphabetic(c) && !Character.isUpperCase(c)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Get the text for this tag as users see it
	 * 
	 * @return
	 */
	public String getTagText() {
		return tag.getTagText();
	}

}
