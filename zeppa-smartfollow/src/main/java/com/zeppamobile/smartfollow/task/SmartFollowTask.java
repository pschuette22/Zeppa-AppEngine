package com.zeppamobile.smartfollow.task;

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
	
}
