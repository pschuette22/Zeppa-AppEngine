package com.zeppamobile.smartfollow.agent;

import com.zeppamobile.common.report.SmartfollowReport;

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
	
//	/**
//	 * Convert this agent to JSON
//	 * @return resulting object
//	 */
//	public abstract JSONObject toJson();
//	
//	/**
//	 * Rebuild this agent from JSON string
//	 * @param jsonString
//	 * @return 
//	 */
//	public abstract BaseAgent fromJSON(String jsonString);
	
	/**
	 * Report associated with this agent. 
	 */
	protected SmartfollowReport report;
	
	/**
	 * Set the SmartfollowReport that should handle logging for this agent
	 * 
	 * @param report - for logging
	 */
	public void setReport(SmartfollowReport report) {
		this.report = report;
	}
	
	/**
	 * Log information if a report is being taken
	 * 
	 * @param text - to be logged
	 */
	protected void log(String text) {
		if(report != null){
			report.log(text);
		}
	}
	
}
