package com.zeppamobile.api.analytics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.datastore.Key;
import com.zeppamobile.api.PMF;
import com.zeppamobile.api.datamodel.Vendor;
import com.zeppamobile.api.datamodel.ZeppaUser;
import com.zeppamobile.common.cerealwrapper.FilterCerealWrapper;
import com.zeppamobile.common.cerealwrapper.FilterCerealWrapper.Gender;
import com.zeppamobile.common.utils.DistanceUtils;

/**
 * Base object for making analytics requests
 * 
 * @author PSchuette
 *
 */
public abstract class AnalyticsRequest {

	protected DemographicsFilter filter;

	protected Set<AnalyticsResponseListener> responseListeners;

	protected AnalyticsRequest(DemographicsFilter filter) {
		this.filter = filter;
		responseListeners = new HashSet<AnalyticsResponseListener>();
	}

	
	/**
	 * Add a response listener to this request
	 * @param listener
	 */
	public void registerResponseListener(AnalyticsResponseListener listener){
		responseListeners.add(listener);
	}
	
	/**
	 * Remove a previously registered response listener
	 * @param listener
	 * @return true if the response was successfully removed
	 */
	public boolean unregisterResponseListener(AnalyticsResponseListener listener) {
		return responseListeners.add(listener);
	}
		
	/**
	 * Execute the analytics request
	 */
	public abstract void execute();

	/**
	 * Call this to notify listening entities the request has completed
	 */
	protected void finalize(){
		for(AnalyticsResponseListener responseListener: responseListeners){
			responseListener.onAnalyticsRequestFinalized();
		}
	}
	
	/**
	 * Fetch the users who match this analytics request demographics filter
	 * 
	 * @return list of keys pointing to users who match this demographic
	 */
	protected List<Key> fetchUserKeys() {
		List<Key> result = new ArrayList<Key>();

		PersistenceManager mgr = getPersistenceManager();

		try {
			

		} finally {
			// Make sure the persistence manager is closed after executing op
			mgr.close();
		}

		return result;
	}

	/**
	 * get the persistence manager for datastore queries
	 * 
	 * @return PersistenceManager
	 */
	protected static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}

	/**
	 * Interface to be attached to classes who wish to be notified when a
	 * request finalizes
	 * 
	 * @author PSchuette
	 *
	 */
	public abstract interface AnalyticsResponseListener {
		/**
		 * AnalyticsRequest 
		 */
		public void onAnalyticsRequestFinalized();
	}

}
