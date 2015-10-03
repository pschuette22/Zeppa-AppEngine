package com.zeppamobile.common.datamodel;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class InviteGroup {

	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	@Persistent(defaultFetchGroup="false")
	private List<String> emails = new ArrayList<String>();
	
	@Persistent(defaultFetchGroup="false")
	private List<ZeppaUser> groupMembers = new ArrayList<ZeppaUser>();
	
	@Persistent
	private List<String> suggestedTags = new ArrayList<String>();
	
	
	/**
	 * Add a group member to an invite group
	 * @param user
	 * @return
	 */
	public boolean addGroupMember(ZeppaUser user){
		return this.groupMembers.add(user);
	}

	public List<ZeppaUser> getGroupMembers(){
		return this.groupMembers;
	}
	
}
