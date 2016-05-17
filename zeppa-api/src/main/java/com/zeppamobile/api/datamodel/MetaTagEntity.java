package com.zeppamobile.api.datamodel;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class MetaTagEntity implements Comparable<MetaTagEntity> {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	@Persistent
	private Long tagId;

	@Persistent
	private Long ownerId;

	@Persistent // For bi-directional mapping
	private String indexedWordId;

	/*
	 * True if tag is owned by a user, false if owned by a vendor
	 */
	@Persistent
	private Boolean isUserTag;

	/*
	 * Hold the weight of this indexed word in the tag
	 */
	@Persistent
	private Double weightInTag;

	@Persistent
	private Double weightDifference;

	public MetaTagEntity(Long tagId, Long ownerId, String indexedWordId, Boolean isUserTag, Double weightInTag) {
		super();
		this.tagId = tagId;
		this.ownerId = ownerId;
		this.indexedWordId = indexedWordId;
		this.isUserTag = isUserTag;
		this.weightInTag = weightInTag;
	}

	public Long getTagId() {
		return tagId;
	}

	public void setTagId(Long tagId) {
		this.tagId = tagId;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
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

	public Double getWeightDifference() {
		return weightDifference;
	}

	public void calculateWeightDifference(Double relativeWeight) {
		weightDifference = Math.abs(relativeWeight - weightInTag);
	}

	@Override
	public int compareTo(MetaTagEntity tag) {
		Double result = (this.getWeightDifference() - tag.getWeightDifference());
		return (int) Math.ceil(result);
	}

}
