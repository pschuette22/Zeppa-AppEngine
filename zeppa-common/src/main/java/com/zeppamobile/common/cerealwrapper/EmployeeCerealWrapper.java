package com.zeppamobile.common.cerealwrapper;


/**
 * 
 * @author Pete Schuette
 * 
 * <p>Serializable wrapper class for Employee object</p>
 *
 */
public class EmployeeCerealWrapper extends CerealWrapper {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	protected Long created;

	protected Long updated;
	
	protected UserInfoCerealWrapper userInfoCereal;
	
	protected Long vendorId;
	
	protected String emailAddress;
	
	protected String password;
	
	protected Boolean isEmailVerified;
	
	protected String privakeyGuid;

	
	
	
	
	public EmployeeCerealWrapper(Long created, Long updated,
			UserInfoCerealWrapper userInfoCereal, Long vendorId,
			String emailAddress, String password, Boolean isEmailVerified,
			String privakeyGuid) {
		super();
		this.created = created;
		this.updated = updated;
		this.userInfoCereal = userInfoCereal;
		this.vendorId = vendorId;
		this.emailAddress = emailAddress;
		this.password = password;
		this.isEmailVerified = isEmailVerified;
		this.privakeyGuid = privakeyGuid;
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

	public UserInfoCerealWrapper getUserInfoCereal() {
		return userInfoCereal;
	}

	public void setUserInfoCereal(UserInfoCerealWrapper userInfoCereal) {
		this.userInfoCereal = userInfoCereal;
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	
	
	

}
