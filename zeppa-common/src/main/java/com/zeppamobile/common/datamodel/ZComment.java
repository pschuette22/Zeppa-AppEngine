package com.zeppamobile.common.datamodel;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Parent;

@Entity
public class ZComment extends ZData {

	@Parent Key<ZActivity> activity;
	
	private Key<ZUser> user;
	
	private String text;

	public ZComment(Key<ZActivity> activity, Key<ZUser> user, String text) {
		super();
		this.activity = activity;
		this.user = user;
		this.text = text;
	}

	public Key<ZActivity> getActivity() {
		return activity;
	}

	public Key<ZUser> getUser() {
		return user;
	}

	public String getText() {
		return text;
	}
	
	
	
}
