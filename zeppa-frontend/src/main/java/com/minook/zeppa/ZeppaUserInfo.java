package com.minook.zeppa;

import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
@Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
public class ZeppaUserInfo{

	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY, defaultFetchGroup = "true")
	private Key key;
	
	@Persistent(defaultFetchGroup = "true")
	private Long created;
	
	@Persistent(defaultFetchGroup = "true")
	private Long updated;
	
	@Persistent(defaultFetchGroup = "true")
	private String givenName;

	@Persistent(defaultFetchGroup = "true")
	private String familyName;

	@Persistent(defaultFetchGroup = "true")
	private String imageUrl;

	@Persistent(defaultFetchGroup = "true")
	private String googleAccountEmail;

	@Persistent(defaultFetchGroup = "true")
	private String primaryUnformattedNumber;

	
	// For guice
	public ZeppaUserInfo(){}
	
//	/**
//	 * @param givenName
//	 * @param familyName
//	 * @param imageUrl
//	 * @param googleAccountEmail
//	 * @param primaryUnformatedNumber
//	 */
//	public ZeppaUserInfo(String givenName, String familyName, String imageUrl,
//			String googleAccountEmail, String primaryUnformattedNumber) {
//
//		this.created = System.currentTimeMillis();
//		this.updated = System.currentTimeMillis();
//		this.givenName = givenName;
//		this.familyName = familyName;
//		this.imageUrl = imageUrl;
//		this.googleAccountEmail = googleAccountEmail;
//		this.primaryUnformattedNumber = primaryUnformattedNumber;
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
	
	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getFamilyName() {
		return familyName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getGoogleAccountEmail() {
		return googleAccountEmail;
	}

	public void setGoogleAccountEmail(String googleAccountEmail) {
		this.googleAccountEmail = googleAccountEmail;
	}

	public String getPrimaryUnformattedNumber() {
		return primaryUnformattedNumber;
	}

	public void setPrimaryUnformattedNumber(String primaryUnformattedNumber) {
		this.primaryUnformattedNumber = primaryUnformattedNumber;
	}

}
