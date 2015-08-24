package com.zeppamobile.smartfollow.agent;

import java.util.ArrayList;
import java.util.List;

import com.zeppamobile.api.datamodel.ZeppaUserToUserRelationship;

public class UserAgent {

	private Long userId;
	private List<ZeppaUserToUserRelationship> minglers = new ArrayList<ZeppaUserToUserRelationship>();
	private List<EventAgent> events = new ArrayList<EventAgent>();
	
	public UserAgent(Long userId, Long otherUser){
		this.userId = userId;
	}
	
	/**
	 * Fetch all relationships this user has to others
	 * @param otherUser- user in question's ID to be ignored when fetching relationships
	 */
	private void fetchMinglingRelationships(Long otherUser){
		
	}
	
	/**
	 * Fetch events relative to other user and create agents
	 */
	private void fetchEvents(){
		
	}
	
}
