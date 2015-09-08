package com.zeppamobile.smartfollow.agent;

import org.json.simple.JSONObject;

public abstract class BaseAgent {

	
	/**
	 * Hold whether or not this agent has completed its calculations
	 *
	 */
	protected boolean didFinishedCalculations = false;
	
	/**
	 * Ask if agent has finished computing everything
	 * @return true if relative calculations were completed.
	 */
	public boolean didFinishCalculations(){
		return didFinishedCalculations;
	}
	
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
