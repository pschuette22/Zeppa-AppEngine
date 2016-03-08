package com.zeppamobile.api.datamodel;

import javax.jdo.annotations.Persistent;

/**
 * 
 * @author Pete Schuette
 * 
 * This is the base object necessary for our machine learning objects
 *
 */
public class Interesting {

	
	@Persistent 
	protected double calculatedInterest;
	
	
	@Persistent
	protected long calculationsCount;
	
	
	
	
	
	
}
