package com.zeppamobile.api.datamodel;

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
	
	@Persistent
	private List<Key> groupMembers = new ArrayList<Key>();
	
	@Persistent
	private List<String> suggestedTags = new ArrayList<String>();
	
	
	
	
	public List<String> getEmails() {
		return emails;
	}

	public void setEmails(List<String> emails) {
		this.emails = emails;
	}

	public List<String> getSuggestedTags() {
		return suggestedTags;
	}

	public void setSuggestedTags(List<String> suggestedTags) {
		this.suggestedTags = suggestedTags;
	}

	/**
	 * Add a group member to an invite group
	 * @param user
	 * @return
	 */
	public boolean addGroupMember(ZeppaUser user){
		return this.groupMembers.add(user.getKey());
	}

	public List<Key> getGroupMemberKeys(){
		return this.groupMembers;
	}
	
}
