package com.zeppamobile.smartfollow.task;

import org.json.JSONObject;

import com.google.gwt.thirdparty.json.JSONArray;
import com.zeppamobile.smartfollow.agent.UserAgent;

public class CreateInitialTagFollows extends SmartFollowTask {

	// Ids of users to determine what to follow automatically.
	private UserAgent userAgent1,userAgent2;
	
	/**
	 * Create task to autofollow tags between users when they first connect.
	 * @param userId1
	 * @param userId2
	 */
	
	public CreateInitialTagFollows(String taskName, Long userId1, Long userId2) {
		super(taskName);
		userAgent1 = new UserAgent(userId1, userId2);
		userAgent1 = new UserAgent(userId2, userId1);
		
		
	}

	@Override
	public void execute() {
		super.execute();
		// TODO Auto-generated method stub
		/*
		 * execute task to create follows for user1
		 */
		
		/*
		 * execute task to create follows for user2
		 */
	}

	@Override
	public void finalize() {
		// TODO Auto-generated method stub
		/*
		 * execute task to create follows for user1
		 */
		
		/*
		 * execute task to create follows for user2
		 */
	}

	@Override
	public String abort(boolean doResume) {
		// TODO Auto-generated method stub
		JSONArray array = new JSONArray();
		
		/*
		 * Get json of userAgent1 
		 */
		JSONObject userAgent1Json = new JSONObject();
		
		array.put(userAgent1Json);
		/*
		 * Get json of userAgent2 
		 */
		JSONObject userAgent2Json = new JSONObject();
		array.put(userAgent2Json);
		
		// Return resulting string
		return array.toString();
	}
	
	
}
