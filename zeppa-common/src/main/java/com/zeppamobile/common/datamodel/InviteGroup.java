package com.zeppamobile.common.datamodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	
	@Persistent
	private Map<String,Long> invitedUserEmailMap = new HashMap<String,Long>();
	
	
	@Persistent
	private List<String> suggestedTags = new ArrayList<String>();
	
	
	
	

}
