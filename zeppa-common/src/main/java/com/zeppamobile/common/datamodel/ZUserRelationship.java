package com.zeppamobile.common.datamodel;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;

@Entity
public class ZUserRelationship extends ZData {

	public enum RelationshipType {
		REQUEST_SENT, REQUEST_PENDING, CONNECTED
	};

	@Parent
	Key<ZUser> user;

	@Index
	private Key<ZUser> subject;

	@Index
	private RelationshipType type;

	
	public ZUserRelationship(Key<ZUser> user, Key<ZUser> subject,
			RelationshipType type) {
		super();
		this.user = user;
		this.subject = subject;
		this.type = type;
	}


	public RelationshipType getType() {
		return type;
	}


	public void setType(RelationshipType type) {
		this.type = type;
	}


	public Key<ZUser> getUser() {
		return user;
	}


	public Key<ZUser> getSubject() {
		return subject;
	}

	
	
}
