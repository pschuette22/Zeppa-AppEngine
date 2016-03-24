package com.zeppamobile.api.datamodel;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class MetaTagEntity {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	@Persistent
	private Key tagKey;

	@Persistent
	private Key ownerKey;
	
	@Persistent 
	private Key metaTagKey;

	/*
	 * True the tag this maps to is owned by a user, false if owned by a vendor
	 */
	@Persistent
	private Boolean isUserTag;
	
	/*
	 * Hold the weight of this indexed word in the tag
	 */
	@Persistent
	private Double weightInTag;
	
	
	

	public MetaTagEntity(Key tagKey, Key ownerKey, Key metaTagKey,
			Boolean isUserTag, Double weightInTag) {
		super();
		this.tagKey = tagKey;
		this.ownerKey = ownerKey;
		this.metaTagKey = metaTagKey;
		this.isUserTag = isUserTag;
		this.weightInTag = weightInTag;
	}

	public Key getTagKey() {
		return tagKey;
	}

	public void setTagKey(Key tagKey) {
		this.tagKey = tagKey;
	}

	public Key getOwnerKey() {
		return ownerKey;
	}

	public void setOwnerKey(Key ownerKey) {
		this.ownerKey = ownerKey;
	}

	public Key getMetaTagKey() {
		return metaTagKey;
	}

	public void setMetaTagKey(Key metaTagKey) {
		this.metaTagKey = metaTagKey;
	}

	public Boolean getIsUserTag() {
		return isUserTag;
	}

	public void setIsUserTag(Boolean isUserTag) {
		this.isUserTag = isUserTag;
	}

	public Double getWeightInTag() {
		return weightInTag;
	}

	public void setWeightInTag(Double weightInTag) {
		this.weightInTag = weightInTag;
	}

	public Key getKey() {
		return key;
	}
	
}
