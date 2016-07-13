package com.zeppamobile.common.datamodel;

import java.util.List;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;

@Entity
public class ZActivityRelationship extends ZData {
	
	@Parent ZActivity activity;
	
	@Index
	private Key<ZUser> user;
	
	private Boolean isRecommended;
	
	private Boolean isAttending;
	
	private Boolean isFollowing;
	
	private List<Key<ZUser>> invitedBy;
	
	
	
	
}
