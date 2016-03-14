package com.zeppamobile.common.cerealwrapper;

import java.io.Serializable;

/**
 * 
 * @author Pete Schuette
 * 
 *         <p>Base Class for CerealWrapper classes. CerealWrapper is a pun on
 *         serialized wrapper and the objects are used to pass data between
 *         modules.</p>
 *
 */
public abstract class CerealWrapper implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Entity id (key.getId)
	 */
	protected Long identifier;

	public Long getIdentifier() {
		return identifier;
	}

	public void setIdentifier(Long identifier) {
		this.identifier = identifier;
	}
		
	
}
