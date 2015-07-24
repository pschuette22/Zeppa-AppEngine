package com.minook.zeppa;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.minook.zeppa.DeviceInfo.DeviceType;

@PersistenceCapable
public class ZeppaFeedback {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	@Persistent
	private Long created;

	@Persistent
	private Long updated;

	@Persistent
	private Long userId;

	@Persistent
	private String releaseCode;

	@Persistent
	private Double rating;

	@Persistent
	private DeviceInfo.DeviceType deviceType;

	@Persistent
	private String subject;

	@Persistent
	private String feedback;

	
	// For guice
	public ZeppaFeedback(){}
	
//	public ZeppaFeedback(Long userId, String releaseCode, Double rating, DeviceType deviceType,
//			String subject, String feedback) {
//
//		this.created = System.currentTimeMillis();
//		this.updated = System.currentTimeMillis();
//		this.userId = userId;
//		this.releaseCode = releaseCode;
//		this.rating = rating;
//		this.deviceType = deviceType;
//		this.subject = subject;
//		this.feedback = feedback;
//	}

	public Long getCreated() {
		return created;
	}

	public void setCreated(Long created) {
		this.created = created;
	}

	public Long getUpdated() {
		return updated;
	}

	public void setUpdated(Long updated) {
		this.updated = updated;
	}

	public Key getKey() {
		return key;
	}

	public Long getId() {
		return key.getId();
	}

	public String getReleaseCode() {
		return releaseCode;
	}

	public void setReleaseCode(String versionCode) {
		this.releaseCode = versionCode;
	}

	public Double getRating() {
		return rating;
	}

	public void setRating(Double rating) {
		this.rating = rating;
	}

	public DeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getFeedback() {
		return feedback;
	}

	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

}
