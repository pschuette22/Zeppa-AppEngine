package com.zeppamobile.api.analytics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.datastore.Key;
import com.zeppamobile.api.datamodel.EventTag;
import com.zeppamobile.api.datamodel.VendorEvent;
import com.zeppamobile.api.datamodel.VendorEventRelationship;
import com.zeppamobile.common.cerealwrapper.FilterCerealWrapper;

/**
 * Perform a request to information about a past
 * 
 * @author PSchuette
 *
 */
public class PastEventAnalyticsRequest extends AnalyticsRequest {

	// Event that is being queried on
	private VendorEvent event;

	// Explicit relationships created for this event
	private List<VendorEventRelationship> relationships;

	// Users that match the given filter
	private List<Key> matchingUserKeys;
	
	private Map<EventTag, TagAnalyticsRequest> tagAnalytics;

	public PastEventAnalyticsRequest(VendorEvent event, FilterCerealWrapper filter) {
		super(filter);
		this.event = event;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute() {
		matchingUserKeys = fetchUserKeys();

		// Fetch the relationships made for this event
		fetchVendorEventRelationships();

		// Get analytics on attached tags
		tagAnalytics = new HashMap<EventTag, TagAnalyticsRequest>();
		PersistenceManager mgr = getPersistenceManager();
		try {
			for (Long tagId : event.getTagIds()) {
				try {
					
				} catch (JDOObjectNotFoundException e){
					// Slight chance tag was deleted and event data is stale
				}
			}
		} finally {
			mgr.close();
		}

	}

	/**
	 * Query for all explicit relationships defined for this vendor event
	 */
	@SuppressWarnings("unchecked")
	private void fetchVendorEventRelationships() {
		PersistenceManager mgr = getPersistenceManager();

		try {
			Query q = mgr.newQuery(VendorEventRelationship.class);
			q.setFilter("eventId==" + event.getId().longValue());
			relationships = (List<VendorEventRelationship>) q.execute();
		} finally {
			mgr.close();
		}
	}

}
