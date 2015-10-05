package com.zeppamobile.smartfollow.task;


/**
 * 
 * @author Pete Schuette
 * 
 * Create initial tag follows as new tags are created
 *
 */
public class CreateNewTagFollows extends SmartFollowTask {

	
	private Long tagId;
	
	/**
	 * Instantiate a task to create 
	 * @param taskName
	 * @param tagId
	 */
	public CreateNewTagFollows(String taskName, Long tagId) {
		super(taskName);
		this.tagId =  tagId;
	}

	
	@Override
	public void execute() {
		// TODO Auto-generated method stub
		super.execute();
		
		
	}


	@Override
	public void finalize() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String abort(boolean doResume) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	/**
	 * Initialize all the agents used in calculations
	 * 
	 */
	private void initAgents(){
		
		
		
	}

}
