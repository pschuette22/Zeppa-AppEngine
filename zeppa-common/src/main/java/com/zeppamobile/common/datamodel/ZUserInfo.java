package com.zeppamobile.common.datamodel;

import com.google.api.client.util.Data;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;

@Entity
public class ZUserInfo extends ZData {

	public enum Gender {
		MALE,FEMALE,UNANSWERED
	}
	
	/**
	 * Parent user  
	 * */
	@Parent Key<ZUser> user;
	
	public String givenName;
	
	public String familyName;
	
	public String imageUrl;
	
	@Index
	public Gender gender;
	
	@Index
	public Data birthday;

	/**
	 * Construct user info object
	 * @param user
	 * @param givenName
	 * @param familyName
	 * @param imageUrl
	 * @param gender
	 * @param birthday
	 */
	public ZUserInfo(Key<ZUser> user, String givenName, String familyName, String imageUrl, Gender gender,
			Data birthday) {
		super();
		this.user = user;
		this.givenName = givenName;
		this.familyName = familyName;
		this.imageUrl = imageUrl;
		this.gender = gender;
		this.birthday = birthday;
	}
	
	
	
	
}
