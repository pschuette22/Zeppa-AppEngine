package com.zeppamobile.api.endpoint.admin;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;

import com.zeppamobile.api.PMF;

/**
 * 
 * @author Pete Schuette
 *
 *         This is the base class of HTTP servlets that can be used to make
 *         calls to the datastore from other Zeppa Modules.
 *
 */
public class BaseAdminServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Get the persistence manager
	 * 
	 * @return Persistence Manager instance
	 */
	protected static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}
}
