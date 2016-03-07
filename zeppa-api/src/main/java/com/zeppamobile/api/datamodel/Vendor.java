package com.zeppamobile.api.datamodel;

import java.util.List;
import com.zeppamobile.api.datamodel.BillingInfo;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.json.simple.JSONObject;

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
	 * Convert this object to a json object
	 * 
	 * @return jsonObject
	 */
	@SuppressWarnings("unchecked")
	public JSONObject toJson() {
		JSONObject obj = new JSONObject();

		obj.put("key", key);
		obj.put("created", created == null ? Long.valueOf(-1) : created);
		obj.put("updated", updated == null ? Long.valueOf(-1) : updated);

		obj.put("companyName", companyName);
		obj.put("companyLogoUrl", companyLogoUrl);
//		obj.put("addressLine1", addressLine1);
//		obj.put("addressLine2", addressLine2);
//		obj.put("city", city);
//		obj.put("state", state);
//		obj.put("zipcode", zipcode);
		obj.put("masterUserId", masterUserId);
		obj.put("isPrivakeyEnabled", isPrivakeyEnabled);


		return obj;
	}
	
	/**
	 * Default vendor constructor 
	 **/
	public Vendor()
	{
		super();
	}
	
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
//		this.addressLine1 = addressLine1;
//		this.addressLine2 = addressLine2;
//		this.city = city;
//		this.state = state;
//		this.zipcode = zipcode;
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

//	public String getAddressLine1() {
//		return addressLine1;
//	}
//
//	public void setAddressLine1(String addressLine1) {
//		this.addressLine1 = addressLine1;
//	}
//
//	public String getAddressLine2() {
//		return addressLine2;
//	}
//
//	public void setAddressLine2(String addressLine2) {
//		this.addressLine2 = addressLine2;
//	}
//
//	public String getCity() {
//		return city;
//	}
//
//	public void setCity(String city) {
//		this.city = city;
//	}
//
//	public String getState() {
//		return state;
//	}
//
//	public void setState(String state) {
//		this.state = state;
//	}
//
//	public Integer getZipcode() {
//		return zipcode;
//	}
//
//	public void setZipcode(Integer zipcode) {
//		this.zipcode = zipcode;
//	}

	
	
	public Long getMasterUserId() {
		return masterUserId;
	}

	public String getCompanyLogoUrl() {
		return companyLogoUrl;
	}

	public void setCompanyLogoUrl(String companyLogoUrl) {
		this.companyLogoUrl = companyLogoUrl;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public BillingInfo getBillingInfo() {
		return billingInfo;
	}

	public void setBillingInfo(BillingInfo billingInfo) {
		this.billingInfo = billingInfo;
	}

	public List<Bill> getBillHistory() {
		return billHistory;
	}

	public void setBillHistory(List<Bill> billHistory) {
		this.billHistory = billHistory;
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
