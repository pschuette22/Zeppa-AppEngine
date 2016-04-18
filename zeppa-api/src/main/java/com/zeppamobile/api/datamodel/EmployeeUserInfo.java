package com.zeppamobile.api.datamodel;

import java.util.List;

import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import org.json.simple.JSONObject;

@PersistenceCapable
@Discriminator(strategy=DiscriminatorStrategy.CLASS_NAME)
public class EmployeeUserInfo extends ZeppaUserInfo {

	
	@Persistent
	private Long vendorID;
	private Long employeeID;
	private Boolean isPrivaKeyRequired; 
	
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
	
	public Boolean isPrivaKeyRequired() {
		if(isPrivaKeyRequired == null)
			return false;
		
		return isPrivaKeyRequired;
	}

	public void setIsPrivaKeyRequired(Boolean isPrivaKeyRequired) {
		this.isPrivaKeyRequired = isPrivaKeyRequired;
	}
}
