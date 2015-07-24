package com.minook.zeppa;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

/**
 *	This holds and distributes the Persistence Manager for the Zeppa Application
 *	<p>
 *	Enhancements made to methods involving inserting objects
 */
public final class PMF {
	private static final PersistenceManagerFactory pmfInstance = JDOHelper
			.getPersistenceManagerFactory("transactions-optional");

	private PMF() {
	}

	public static PersistenceManagerFactory get() {
		return pmfInstance;
	}
	
}