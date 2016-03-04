package com.zeppamobile.common.cerealwrapper;

import java.io.Serializable;

/**
 * 
 * @author Pete Schuette
 * 
 * 
 *
 */
public class FilterInfo implements Serializable {

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
