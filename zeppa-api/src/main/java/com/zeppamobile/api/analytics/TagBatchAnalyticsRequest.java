package com.zeppamobile.api.analytics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.zeppamobile.api.datamodel.EventTag;

/**
 * Execute a batch of tag analytics requests
 * 
 * @author PSchuette
 *
 */
public class TagBatchAnalyticsRequest extends AnalyticsRequest {

	/**
	 * Map the analytics request to the provided tag
	 */
	private Map<EventTag, TagAnalyticsRequest> tagAnalytics;

	/**
	 * Construct a batch request
	 * 
	 * @param filter
	 * @param tags
	 */
	public TagBatchAnalyticsRequest(DemographicsFilter filter, List<EventTag> tags) {
		super(filter);
		// TODO Auto-generated constructor stub
		tagAnalytics = new HashMap<EventTag, TagAnalyticsRequest>();
		for (EventTag tag : tags) {
			tagAnalytics.put(tag, null);
		}
	}

	@Override
	public void execute() {
		// Iterate through the tags and set analytics/ execute request
		for (EventTag tag : tagAnalytics.keySet()) {
			System.out.println("---------TAG: "+tag.getTagText());
			TagAnalyticsRequest req = new TagAnalyticsRequest(tag, filter);
			req.execute();
			tagAnalytics.put(tag, req);
		}

		// Finalize the request
		finalize();
	}

	/**
	 * Returns a mapping of interest for tags involved in this request
	 * 
	 * @return
	 */
	public Map<String, Double> getAllTagInterest() {
		Map<String, Double> interestMapping = new HashMap<String, Double>();

		for (Entry<EventTag, TagAnalyticsRequest> entry : tagAnalytics.entrySet()) {
			interestMapping.put(entry.getKey().getTagText(), entry.getValue().getAverageInterest());
		}
		return interestMapping;
	}

	/**
	 * Get the tag analytics request for a defined tag
	 * 
	 * @param tag
	 * @return request
	 */
	public TagAnalyticsRequest getTagAnalyticsRequest(EventTag tag) {
		return tagAnalytics.get(tag);
	}

}
