package com.zeppamobile.common.cerealwrapper;


public class UserInfoCerealWrapper extends CerealWrapper {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected Long created;
	
	protected Long updated;
	
	protected String givenName;

	protected String familyName;

	protected String imageUrl;	
	
	protected String gender;
	
	protected Long dateOfBirth;
	
	
	
	
	/**
	 * Construct a user info cereal wrapper
	 * 
	 * @param created
	 * @param updated
	 * @param givenName
	 * @param familyName
	 * @param imageUrl
	 * @param gender
	 * @param dateOfBirth
	 */
	public UserInfoCerealWrapper(Long created, Long updated, String givenName,
			String familyName, String imageUrl, String gender, Long dateOfBirth) {
		super();
		this.created = created;
		this.updated = updated;
		this.givenName = givenName;
		this.familyName = familyName;
		this.imageUrl = imageUrl;
		this.gender = gender;
		this.dateOfBirth = dateOfBirth;
	}

	
	
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

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Long getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Long dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	
	
	
}
