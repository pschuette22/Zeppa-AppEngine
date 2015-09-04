package com.zeppamobile.smartfollow.task;

import java.util.List;

import com.zeppamobile.smartfollow.agent.TagAgent;

/**
 * Base class for tasks associated with smartfollow
 * 
 * @author Pete Schuette
 *
 */
public abstract class SmartFollowTask {

	private String name;
	protected boolean isRunning = false;
	
	public SmartFollowTask(String taskName){
		this.name = taskName;
	}
	
	/**
	 * <p>Execute smartfollow task</p>
	 * <p>Do processing and hold information</p>
	 */
	public void execute(){
		this.isRunning = false;
	}
	
	/**
	 * <p>Finalize smartfollow task.</p>
	 * <p>Clean up and insert data.</p>
	 */
	public abstract void finalize();
	
	/**
	 * Abort this task - occurs when instance is asked to shut down
	 * @param doResume - true if processed data should be stored to be resumed later
	 * @return json array of stored data. Null if task should be executed again
	 */
	public abstract String abort(boolean doResume);
	
	
	/**
	 * Get the unique name of this task
	 * @return name
	 */
	public String getTaskName(){
		return name;
	}
	
	
	/**
	 * Get a 2d array of calculated similarity between all tags of two given userAgents
	 * @return calculatedSimilarities array
	 */
	protected double[][] calculateTagSimilarities(List<TagAgent> tagAgentList1, List<TagAgent> tagAgentList2){
		
		
		double[][] result = new double[tagAgentList1.size()][tagAgentList2.size()];
	
		/*
		 * Loop through all the tags for the user 1
		 * For each u1tag, loop through u2 tags
		 * Calculate similarity and place in appropriate location
		 */
		for(int i = 0; i< tagAgentList1.size(); i++){
			TagAgent ta1 = tagAgentList1.get(i);
			for(int j = 0; j< tagAgentList2.size(); j++){
				TagAgent ta2 = tagAgentList2.get(j);
				result[i][j] = ta1.calculateSimilarity(ta2);
			}
		}
		
		return result;
	}
	
}
