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
	enum Gender {
		MALE,
		FEMALE,
		UNDEFINED
	}
	
	private int maxAge;
	
	private int minAge;
	
	private Gender gender;
	

	public FilterCerealWrapper(int maxAge, int minAge, Gender gender) {
		super();
		this.maxAge = maxAge;
		this.minAge = minAge;
		this.gender = gender;
	}

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

}
