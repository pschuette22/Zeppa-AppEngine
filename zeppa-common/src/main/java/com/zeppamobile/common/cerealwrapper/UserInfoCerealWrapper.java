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
	
	protected Long vendorID;
	
	protected Long employeeID;
	
	protected Boolean isPrivaKeyRequired;
	
	
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
	public UserInfoCerealWrapper(Long vendorID, Long employeeID, Long created, Long updated, String givenName,
			String familyName, String imageUrl, String gender, Long dateOfBirth, Boolean isPrivaKeyRequired) {
		super();
		this.vendorID = vendorID;
		this.employeeID = employeeID;
		this.created = created;
		this.updated = updated;
		this.givenName = givenName;
		this.familyName = familyName;
		this.imageUrl = imageUrl;
		this.gender = gender;
		this.dateOfBirth = dateOfBirth;
		this.isPrivaKeyRequired = isPrivaKeyRequired;
	}

	public Long getVendorID() {
		return vendorID;
	}

	public void setVendorID(Long vendorID) {
		this.vendorID = vendorID;
	}
	
	public Long getEmployeeID() {
		return employeeID;
	}

	public void setEmployeeID(Long employeeID) {
		this.employeeID = employeeID;
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
	
	public Boolean isPrivaKeyRequired() {
		return isPrivaKeyRequired;
	}

	public void setIsPrivaKeyRequired(Boolean isPrivaKeyRequired) {
		this.isPrivaKeyRequired = isPrivaKeyRequired;
	}
	
	public String toJSON(){		
		String JSONString = "{\"vendorId\" : \""+this.vendorID+"\",";
		JSONString += "\"givenName\" : \""+ this.givenName +"\",";
		JSONString += "\"familyName\" : \""+ this.familyName +"\",";
		JSONString += "\"imageUrl\" : \""+ this.imageUrl +"\",";
		JSONString += "\"gender\" : \""+ this.gender +"\",";
		JSONString += "\"dateOfBirth\" : \""+ this.dateOfBirth +"\"}";
			
		return JSONString;
	}

}
