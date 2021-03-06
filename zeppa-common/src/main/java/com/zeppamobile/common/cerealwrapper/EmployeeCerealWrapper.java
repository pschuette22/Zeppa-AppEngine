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
	
	
	protected long created;

	protected long updated;
	
	protected UserInfoCerealWrapper userInfoCereal;
	
	protected long vendorId;
	
	protected String emailAddress;
	
	protected String password;
	
	protected Boolean isEmailVerified;
	
	protected String privakeyGuid;

	
	public EmployeeCerealWrapper(long created, long updated,
			UserInfoCerealWrapper userInfoCereal, long vendorId,
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

	public long getCreated() {
		return created;
	}

	public void setCreated(long created) {
		this.created = created;
	}

	public long getUpdated() {
		return updated;
	}

	public void setUpdated(long updated) {
		this.updated = updated;
	}

	public UserInfoCerealWrapper getUserInfoCereal() {
		return userInfoCereal;
	}

	public void setUserInfoCereal(UserInfoCerealWrapper userInfoCereal) {
		this.userInfoCereal = userInfoCereal;
	}

	public long getVendorId() {
		return vendorId;
	}

	public void setVendorId(long vendorId) {
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
