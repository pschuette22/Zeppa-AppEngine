package com.zeppamobile.common.cerealwrapper;


public class EventTagFollowInfo extends CerealWrapper {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long followId = -1;
	
	private long tagId = -1;
	
	private long tagOwnerId = -1;
	
	private long followerId = -1;

	
	
	
	public EventTagFollowInfo(long followId, long tagId, long tagOwnerId,
			long followerId) {
		super();
		this.followId = followId;
		this.tagId = tagId;
		this.tagOwnerId = tagOwnerId;
		this.followerId = followerId;
	}

	public long getFollowId() {
		return followId;
	}

	public void setFollowId(long followId) {
		this.followId = followId;
	}

	public long getTagId() {
		return tagId;
	}

	public void setTagId(long tagId) {
		this.tagId = tagId;
	}

	public long getTagOwnerId() {
		return tagOwnerId;
	}

	public void setTagOwnerId(long tagOwnerId) {
		this.tagOwnerId = tagOwnerId;
	}

	public long getFollowerId() {
		return followerId;
	}

	public void setFollowerId(long followerId) {
		this.followerId = followerId;
	}

	
	
}
