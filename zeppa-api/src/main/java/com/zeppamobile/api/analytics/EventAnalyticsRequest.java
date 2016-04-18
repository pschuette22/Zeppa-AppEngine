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
public class EventAnalyticsRequest extends AnalyticsRequest {

	// Event that is being queried on
	private VendorEvent event;

	// Explicit relationships created for this event
	private List<VendorEventRelationship> relationships;

	private Map<EventTag, TagAnalyticsRequest> tagAnalytics;

	public EventAnalyticsRequest(VendorEvent event, DemographicsFilter filter) {
		super(filter);
		this.event = event;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute() {

		// Fetch the relationships made for this event
		fetchVendorEventRelationships();

		// Get analytics on attached tags
		tagAnalytics = new HashMap<EventTag, TagAnalyticsRequest>();
		PersistenceManager mgr = getPersistenceManager();
		try {
			for (Long tagId : event.getTagIds()) {
				try {
					EventTag tag = mgr.getObjectById(EventTag.class, tagId);
					TagAnalyticsRequest req = new TagAnalyticsRequest(tag, this.filter);

					tagAnalytics.put(tag, req);
					req.execute();

				} catch (JDOObjectNotFoundException e) {
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
		} catch (NullPointerException e) {
			// Occurs if the event has not been inserted into db yet (upcoming
			// events)
		} finally {
			mgr.close();
		}
	}

}
