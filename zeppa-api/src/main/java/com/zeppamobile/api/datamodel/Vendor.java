package com.zeppamobile.api.datamodel;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;


@PersistenceCapable
public class Vendor {

	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;
	
	@Persistent
	private Long created;

	@Persistent
	private Long updated;
	
	@Persistent
	private String companyName;
	
	@Persistent
	private String companyLogoUrl;
	
	@Persistent
	private String addressLine1,addressLine2;
	
	@Persistent
	private String city;
	
	@Persistent
	private String state;
	
	@Persistent
	private Integer zipcode;
	
	@Persistent
	private Long masterUserId;
	
	@Persistent
	private Boolean isPrivakeyEnabled;

	
	/**
	 * Construct a vendor object
	 * 
	 * @param created
	 * @param updated
	 * @param companyName
	 * @param addressLine1
	 * @param addressLine2
	 * @param city
	 * @param state
	 * @param zipcode
	 * @param masterUserId
	 * @param isPrivakeyEnabled
	 */
	public Vendor(Long created, Long updated, String companyName,
			String addressLine1, String addressLine2, String city,
			String state, Integer zipcode, Long masterUserId,
			Boolean isPrivakeyEnabled) {
		super();
		this.created = created;
		this.updated = updated;
		this.companyName = companyName;
		this.addressLine1 = addressLine1;
		this.addressLine2 = addressLine2;
		this.city = city;
		this.state = state;
		this.zipcode = zipcode;
		this.masterUserId = masterUserId;
		this.isPrivakeyEnabled = isPrivakeyEnabled;
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

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Integer getZipcode() {
		return zipcode;
	}

	public void setZipcode(Integer zipcode) {
		this.zipcode = zipcode;
	}

	public Long getMasterUserId() {
		return masterUserId;
	}

	public void setMasterUserId(Long masterUserId) {
		this.masterUserId = masterUserId;
	}

	public Boolean getIsPrivakeyEnabled() {
		return isPrivakeyEnabled;
	}

	public void setIsPrivakeyEnabled(Boolean isPrivakeyEnabled) {
		this.isPrivakeyEnabled = isPrivakeyEnabled;
	}

	public Key getKey() {
		return key;
	}
	
	
	
	
	
}
