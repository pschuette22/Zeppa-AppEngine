package com.zeppamobile.api.datamodel;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

/**
 * 
 * @author Pete Schuette
 * 
 * Employee class represents a user with access rights to a vendor account
 *
 */
@PersistenceCapable
public class Employee {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;
	
	@Persistent
	private Long created;

	@Persistent
	private Long updated;
	
	@Persistent(embeddedElement = "true", dependent = "true", defaultFetchGroup = "true")
	private ZeppaUserInfo userInfo;
	
	@Persistent
	private Long vendorId;
	
	@Persistent
	private String emailAddress;
	
	@Persistent
	private String password;
	
	@Persistent
	private Boolean isEmailVerified;
	
	@Persistent
	private String privakeyGuid;

	
	
	// TODO: permissions
	
	
	
	public Long getCreated() {
		return created;
	}

	//Default constructor
	public Employee()
	{
		super();
	}
	
	public Employee(Long created, Long updated, ZeppaUserInfo userInfo,
			Long vendorId, String emailAddress, String password,
			Boolean isEmailVerified, String privakeyGuid) {
		super();
		this.created = created;
		this.updated = updated;
		this.userInfo = userInfo;
		this.vendorId = vendorId;
		this.emailAddress = emailAddress;
		this.password = password;
		this.isEmailVerified = isEmailVerified;
		this.privakeyGuid = privakeyGuid;
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

	public ZeppaUserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(ZeppaUserInfo userInfo) {
		this.userInfo = userInfo;
	}

	public Long getVendorId() {
		return vendorId;
	}

	public void setVendorId(Long vendorId) {
		this.vendorId = vendorId;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getIsEmailVerified() {
		return isEmailVerified;
	}

	public void setIsEmailVerified(Boolean isEmailVerified) {
		this.isEmailVerified = isEmailVerified;
	}

	public String getPrivakeyGuid() {
		return privakeyGuid;
	}

	public void setPrivakeyGuid(String privakeyGuid) {
		this.privakeyGuid = privakeyGuid;
	}

	public Key getKey() {
		return key;
	}
	
	
	
	
	
}
