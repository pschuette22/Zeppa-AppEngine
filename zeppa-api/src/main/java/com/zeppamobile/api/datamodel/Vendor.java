package com.zeppamobile.api.datamodel;

import java.util.List;

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
	protected String addressLine1,addressLine2;
	
	@Persistent
	protected String city;
	
	@Persistent
	protected String state;
	
	@Persistent
	protected Integer zipcode;
	
	// TODO: replace the above attributes with this one
	@Persistent
	private Address address;
	
	@Persistent
	private Long masterUserId;
	
	@Persistent
	private Boolean isPrivakeyEnabled;
	
	@Persistent
	private BillingInfo billingInfo;
	
	@Persistent
	private List<Bill> billHistory;

	
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

	protected String getAddressLine1() {
		return addressLine1;
	}

	protected void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	protected String getAddressLine2() {
		return addressLine2;
	}

	protected void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	protected String getCity() {
		return city;
	}

	protected void setCity(String city) {
		this.city = city;
	}

	protected String getState() {
		return state;
	}

	protected void setState(String state) {
		this.state = state;
	}

	protected Integer getZipcode() {
		return zipcode;
	}

	protected void setZipcode(Integer zipcode) {
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
