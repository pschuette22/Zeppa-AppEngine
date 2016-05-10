package com.zeppamobile.common.cerealwrapper;

/**
 * 
 * @author Pete Schuette
 * 
 * 
 *
 */
public class FilterCerealWrapper extends CerealWrapper {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @author Pete Schuette
	 * 
	 * defined gender types
	 *
	 */
	public enum Gender {
		ALL,
		MALE,
		FEMALE,
		UNDEFINED
	}
	
	private long vendorId;
	
	private double maxDistance;
	
	private String maxAge;
	
	private String minAge;
	
	private Gender gender;
	
	private long startDate;
	
	private long endDate;
	

	public FilterCerealWrapper(long vendorId, double maxDistance, String maxAge, String minAge, Gender gender, long startDate, long endDate) {
		super();
		this.vendorId = vendorId;
		this.maxDistance = maxDistance;
		this.maxAge = maxAge;
		this.minAge = minAge;
		this.gender = gender;
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	public FilterCerealWrapper() {}

	public long getVendorId() {
		return vendorId;
	}

	public void setVendorId(long vendorId) {
		this.vendorId = vendorId;
	}

	public double getMaxDistance() {
		return maxDistance;
	}

	public void setMaxDistance(double maxDistance) {
		this.maxDistance = maxDistance;
	}

	public String getMaxAge() {
		return maxAge;
	}

	public void setMaxAge(String maxAge) {
		this.maxAge = maxAge;
	}

	public String getMinAge() {
		return minAge;
	}

	public void setMinAge(String minAge) {
		this.minAge = minAge;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public long getStartDate() {
		return startDate;
	}

	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}

	public long getEndDate() {
		return endDate;
	}

	public void setEndDate(long endDate) {
		this.endDate = endDate;
	}
	
}
