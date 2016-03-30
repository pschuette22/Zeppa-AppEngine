package com.zeppamobile.common.datainfo;

import java.io.Serializable;

public class UserRelationshipInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private long relationshipId = -1;
	
	private long creatorId = -1;
	
	private long subjectId = -1;
	
	private boolean isMingling = false;
	
	

	public long getRelationshipId() {
		return relationshipId;
	}

	public void setRelationshipId(long relationshipId) {
		this.relationshipId = relationshipId;
	}

	public long getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(long creatorId) {
		this.creatorId = creatorId;
	}

	public long getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(long subjectId) {
		this.subjectId = subjectId;
	}

	public boolean isMingling() {
		return isMingling;
	}

	public void setMingling(boolean isMingling) {
		this.isMingling = isMingling;
	}
	
	
	
	

}
