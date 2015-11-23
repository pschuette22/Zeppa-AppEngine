package com.zeppamobile.common.datainfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.zeppamobile.common.report.SmartfollowReport;

/**
 * Class used to let modules outside of zeppa-api use data objects
 * 
 * 
 * @author Pete Schuette
 *
 */
public class EventTagInfo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Identifier of tag
	private long tagId = -1;
	
	// Text of tag
	private String tagText = null;
	
	// Identifier of user who owns this tag
	private long ownerId = -1;
	
	// List of Ids of users who follow this tag
	private List<Long> followerIds = new ArrayList<Long>();
	
	// True if tag was just created
	private boolean isNewTag = false;
	

	/**
	 * @return identifier of the EventTag
	 */
	public long getTagId() {
		return tagId;
	}

	public void setTagId(long tagId) {
		this.tagId = tagId;
	}

	

	/**
	 * @return Text of tag as seen in database
	 */
	public String getTagText() {
		return tagText;
	}
	
	public void setTagText(String tagText) {
		this.tagText = tagText;
	}


	/**
	 * 
	 * @return Identifier of user who owns this tag
	 */
	public long getOwnerId() {
		return ownerId;
	}
	
	public void setOwnerId(long ownerId) {
		this.ownerId = ownerId;
	}


	/**
	 * @return List of user identifiers who follow this tag
	 */
	public List<Long> getFollowerIds() {
		return followerIds;
	}
	
	public void setFollowerIds(List<Long> followerIds) {
		this.followerIds = followerIds;
	}

	/**
	 * Determine if given user follows this id
	 * 
	 * @param userId
	 * @return true if userId is found in list of followers
	 */
	public boolean doesFollow(long userId){
		for(Long id: followerIds){
			if(id.longValue() == userId){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 
	 * @return True if tag was just created and no followers should be expected
	 * 
	 */
	public boolean isNewTag(){
		return isNewTag;
	}


	public void setNewTag(boolean isNewTag) {
		this.isNewTag = isNewTag;
	}
	
}
