package com.zeppamobile.smartfollow.agent;

import java.util.ArrayList;
import java.util.List;

import com.google.api.server.spi.response.CollectionResponse;
import com.zeppamobile.api.datamodel.EventTagFollow;
import com.zeppamobile.api.datamodel.ZeppaEvent;
import com.zeppamobile.api.datamodel.ZeppaEventToUserRelationship;
import com.zeppamobile.api.datamodel.ZeppaUserToUserRelationship;
import com.zeppamobile.api.endpoint.ZeppaEventEndpoint;
import com.zeppamobile.api.endpoint.ZeppaEventToUserRelationshipEndpoint;
import com.zeppamobile.api.endpoint.ZeppaUserToUserRelationshipEndpoint;

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
	 * Calculated popularity
	 */

	// Calculation determining if users are into the same stuff
	private double similarIntestCalculation;
	
	// Calculated popularity of this user
	private double calculatedPopularity;
	
	
	/*
	 * The resulting list of follows that should be created for this users tags
	 */
	private List<EventTagFollow> result = new ArrayList<EventTagFollow>();

	/**
	 * UserAgent is a wrapper class for zeppaUser to calculate other user's interest in joining their stuff
	 * @param userId
	 * @param otherUserId
	 */
	public UserAgent(Long userId, Long otherUserId) {
		this.userId = userId;
		fetchMinglingRelationships(otherUserId);
		fetchEvents();
		fetchEventRelationships();
	}
	
	
	
	public List<ZeppaUserToUserRelationship> getMinglingRelationships(){
		return minglingRelationships;
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
	private void fetchEventRelationships(){
		ZeppaEventToUserRelationshipEndpoint endpoint = new ZeppaEventToUserRelationshipEndpoint();
		CollectionResponse<ZeppaEventToUserRelationship> response = endpoint.listZeppaEventToUserRelationship("userId=="+userId.longValue(), null, null, null);
		try {
			eventRelationships.addAll(response.getItems());
		} catch (NullPointerException e){
		}
	}

	/**
	 * Fetch events relative to other user and create agents
	 */
	private void fetchEvents() {
		
		ZeppaEventEndpoint endpoint = new ZeppaEventEndpoint();
		CollectionResponse<ZeppaEvent> response = endpoint.listZeppaEvent("hostId=="+userId, null, null, null);
		
		ZeppaEventToUserRelationshipEndpoint relationshipEndpoint = new ZeppaEventToUserRelationshipEndpoint();
		
		// Concerned about memory
		// TODO: implement cursor and fetch limit
		// Possibly only fetch events created in the past 6 months
		CollectionResponse<ZeppaEventToUserRelationship> relationshipResponse = relationshipEndpoint.listZeppaEventToUserRelationship("hostId=="+userId, null, "eventId DESC", null);
		List<ZeppaEventToUserRelationship> allEventRelationships = new ArrayList<ZeppaEventToUserRelationship>();
		try {
			allEventRelationships.addAll(relationshipResponse.getItems());
		} catch (NullPointerException e){
			// catch it if there are no relationships to events. Shouldnt happen
			// TODO: log this
		}
		
		try {
			for(ZeppaEvent event: response.getItems()){
				// TODO: initialize event agent with event relationships
				EventAgent agent = new EventAgent(event);
				agent.pruneRelationships(allEventRelationships);
				events.add(agent);
			}
		} catch (NullPointerException e){
			// null response returned
		}
		
	}

}
