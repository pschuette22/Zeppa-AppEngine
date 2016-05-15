package com.zeppamobile.api.analytics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.datastore.Key;
import com.zeppamobile.api.datamodel.EventTag;
import com.zeppamobile.api.datamodel.EventTagFollow;
import com.zeppamobile.api.datamodel.MetaTag;
import com.zeppamobile.api.datamodel.MetaTagEntity;
import com.zeppamobile.api.endpoint.utils.TagUtility;

/**
 * Make a request to review analytics regarding a given event tag
 * 
 * @author PSchuette
 *
 */
public class TagAnalyticsRequest extends AnalyticsRequest {

	// Event tag the user would like analytics on
	private EventTag tag;

	// Maintain a list of metatags used in this request
	private List<MetaTag> metaTags;

	// List of explicit follow entities
	private List<EventTagFollow> follows;

	// Associate calculated interest with a
	private Map<Long, Double> mappedUserInterest;

	// weight of all the tag parts added together
	private double totalTagWeight;
	
	/**
	 * Initialize a request to receive analytics for a given event tag
	 * 
	 * @param tag
	 *            - tag user would like analytics on
	 * @param filter
	 *            - user filter for this request
	 */
	public TagAnalyticsRequest(EventTag tag, DemographicsFilter filter) {
		super(filter);
		this.tag = tag;
		this.metaTags = new ArrayList<MetaTag>();
		// Do basic initialization
		mappedUserInterest = new HashMap<Long, Double>();
		// quickly calculate the total tag weight
		this.totalTagWeight = 0;
		System.out.println("---------REQ: "+tag.getTagText()+" ---- "+tag.getIndexedWords());
		for (String wordId : tag.getIndexedWords()) {
			totalTagWeight += TagUtility.getIndexWordWeight(wordId);
		}
	}

	@Override
	public void execute() {

		// Create list that can be 
		List<Long> userIds = new ArrayList<Long>();
		userIds.addAll(filter.getUserIds());
		// First, fetch all explicit follows for this tag
		fetchEventTagFollows(userIds);

		// Map explicit interest and remove from user keys to avoid multiple
		// queries
		for (EventTagFollow follow : follows) {
			
			if (userIds.contains(follow.getTagOwnerId())) {
				// Add the predefined interest
				mappedUserInterest.put(follow.getTagOwnerId(), follow.getInterest());
				userIds.remove(follow.getTagOwnerId());
			}
		}

		// If there are users with unidentified interest, calculate interest
		// dynamically
		if (!userIds.isEmpty()) {

			// Initialize mapping for users who do not have explicit follow
			for (Long userId: userIds) {
				mappedUserInterest.put(userId, new Double(0));
			}

			PersistenceManager mgr = getPersistenceManager();
			try {
				// Iterate through MetaTags by id
				for (String indexedWordId : tag.getIndexedWords()) {

					try {
						// Grab metatag
						MetaTag metaTag = mgr.getObjectById(MetaTag.class, indexedWordId);
						this.metaTags.add(metaTag);

						// Add all entity keys to entity list
						List<Key> entityKeys = new ArrayList<Key>();
						entityKeys.addAll(metaTag.getEntities());

						// Iterate through syn set adding all potential entities
						for (String synWordId : metaTag.getSynonymIndexWordIds()) {
							try {
								MetaTag synMetaTag = mgr.getObjectById(MetaTag.class, synWordId);
								entityKeys.addAll(synMetaTag.getEntities());
							} catch (JDOObjectNotFoundException e) {
								// Ignore, there are no metaTag mappings for
								// this synonym
							}

						}

						// Build the query
						Query q = mgr.newQuery(MetaTagEntity.class);
						q.declareImports("import java.util.List;");
						q.declareParameters("List entityKeys, List userIds");
						q.setFilter("isUserTag && entityKeys.contains(key) && userIds.contains(ownerId)");
						double relativeWeight = TagUtility.getIndexWordWeight(metaTag.getIndexedWordId())
								/ totalTagWeight;
//						q.setOrdering("weightDifference ASC");
						// execute the query
						@SuppressWarnings("unchecked")
						List<MetaTagEntity> tagEntities = (List<MetaTagEntity>) q.execute(entityKeys, userIds);
						for(MetaTagEntity mte : tagEntities) {
							mte.calculateWeightDifference(relativeWeight);
						}
						Collections.sort(tagEntities);
						if (tagEntities!=null && !tagEntities.isEmpty()) {
							List<Long> unmappedUserIds = new ArrayList<Long>();
							unmappedUserIds.addAll(userIds);
							// Iterate through tag entities in order of highest
							// to lowest
							for (MetaTagEntity tagEntity : tagEntities) {
								// If the tag entity's owner isn't mapped, do mapping
								if (unmappedUserIds.contains(tagEntity.getOwnerId())) {
									// Calculate interest adjustment and add to mapping
									double interestAdjustment = (relativeWeight-Math.abs(relativeWeight-tagEntity.getWeightInTag()))/relativeWeight;
									Long ownerId = tagEntity.getOwnerId();
									mappedUserInterest.put(ownerId, mappedUserInterest.get(ownerId)+interestAdjustment);
									unmappedUserIds.remove(ownerId);
									// If there are no more unmapped user keys, break out
									if(unmappedUserIds.isEmpty()){
										break;
									}
								}
							}

						}

					} catch (JDOObjectNotFoundException e) {
						// TODO: handle when a metatag is not found
					}
				}
			} finally {
				mgr.close();
			}
		}
		
		// Request finalized
		finalize();
	}

	/**
	 * Fetch all explicitly following relationships created by users for this
	 * tag
	 * 
	 * @param userKeys
	 *            - relevant users to query
	 * @return EventTagFollow objects mapping relevant users to this tag
	 */
	@SuppressWarnings("unchecked")
	private List<EventTagFollow> fetchEventTagFollows(List<Long> followerIds) {
		PersistenceManager mgr = getPersistenceManager();
		
		try {
			Query q = mgr.newQuery(EventTagFollow.class);
			q.setFilter("tagId==" + tag.getId().longValue() + " && followerIds.contains(followerId)");
			q.declareImports("import java.util.List;");
			q.declareParameters("List followerIds");
			follows = (List<EventTagFollow>) q.execute(followerIds);
		} finally {
			mgr.close();
		}

		return follows;
	}
	
	
	/**
	 * Get the average calculated interest
	 * @return
	 */
	public double getAverageInterest(){
		double interestSum = 0;
		for(Double interest: mappedUserInterest.values()){
			interestSum+=interest;
		}
		return interestSum/Double.valueOf(mappedUserInterest.size());
	}

//	/**
//	 * Get the key object for a given id from the list of users relevant to this
//	 * search
//	 * 
//	 * @param id
//	 *            - user db identifier
//	 * @return matching key or null
//	 */
//	private Key getMatchingUserKeyFromId(long id) {
//		for (Key k : filter.getUserKeys()) {
//			if (k.getId() == id) {
//				return k;
//			}
//		}
//		return null;
//	}

}
