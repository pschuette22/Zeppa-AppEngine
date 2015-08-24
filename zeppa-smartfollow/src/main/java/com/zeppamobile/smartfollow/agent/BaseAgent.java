package com.zeppamobile.smartfollow.agent;

import org.json.JSONObject;

public abstract class BaseAgent {

	/**
	 * Convert this agent to JSON
	 * @return resulting object
	 */
	public abstract JSONObject toJson();
	
	/**
	 * Rebuild this agent from JSON string
	 * @param jsonString
	 * @return 
	 */
	public abstract BaseAgent fromJSON(String jsonString);
	
	
}
