package com.zeppamobile.smartfollow.agent;

import java.util.ArrayList;
import java.util.List;

import com.google.api.server.spi.response.CollectionResponse;
import com.zeppamobile.api.datamodel.EventTag;
import com.zeppamobile.api.datamodel.EventTagFollow;
import com.zeppamobile.api.endpoint.EventTagFollowEndpoint;

public class TagAgent {

	private EventTag tag;

	private List<EventTagFollow> tagFollows = new ArrayList<EventTagFollow>();

	
	
	public TagAgent(EventTag tag) {
		this.tag = tag;
		fetchTagFollows();
	}

	/**
	 * Quickly fetch all the follows for this tag
	 */
	private void fetchTagFollows() {
		EventTagFollowEndpoint endpoint = new EventTagFollowEndpoint();

		CollectionResponse<EventTagFollow> response = endpoint
				.listEventTagFollow("tagId==" + tag.getId(), null, null, null);
		tagFollows.addAll(response.getItems());
	}
	
	
	

}
