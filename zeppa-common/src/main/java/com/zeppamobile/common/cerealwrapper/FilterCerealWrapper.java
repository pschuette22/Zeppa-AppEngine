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
	
	private int maxAge;
	
	private int minAge;
	
	private Gender gender;
	
	private long startDate;
	
	private long endDate;
	

	public FilterCerealWrapper(int maxAge, int minAge, Gender gender, long startDate, long endDate) {
		super();
		this.maxAge = maxAge;
		this.minAge = minAge;
		this.gender = gender;
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	public FilterCerealWrapper() {}

	public int getMaxAge() {
		return maxAge;
	}

	public void setMaxAge(int maxAge) {
		this.maxAge = maxAge;
	}

	public int getMinAge() {
		return minAge;
	}

	public void setMinAge(int minAge) {
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
