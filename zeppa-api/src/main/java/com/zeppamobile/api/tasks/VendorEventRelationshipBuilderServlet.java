package com.zeppamobile.api.tasks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Key;
import com.zeppamobile.api.PMF;
import com.zeppamobile.api.datamodel.MetaTag;
import com.zeppamobile.api.datamodel.MetaTagEntity;
import com.zeppamobile.api.datamodel.VendorEvent;
import com.zeppamobile.api.datamodel.VendorEventRelationship;
import com.zeppamobile.common.utils.Utils;

public class VendorEventRelationshipBuilderServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Make a post request to the servlet
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("Building vendor event relationships");
		String eventIdString = req.getParameter("vendor-event-id");
		if (Utils.isWebSafe(eventIdString)) {
			Long eventId = Long.valueOf(eventIdString);

			PersistenceManager mgr = getPersistenceManager();
			try {
				// Grab the event in question
				VendorEvent event = mgr.getObjectById(VendorEvent.class, eventId);

				// Grab all the meta tag entities that are linked to this
				Query q = mgr.newQuery(MetaTagEntity.class);
				q.declareImports("import java.util.List");
				q.declareParameters("List tagIds");
				q.setFilter("tagIds.contains(tagId)");
				@SuppressWarnings("unchecked")
				Collection<MetaTagEntity> entities = (Collection<MetaTagEntity>) q.execute(event.getTagIds());

				Map<Long, Double> calculatedInterest = new HashMap<Long, Double>();

				if (entities != null && !entities.isEmpty()) {
					System.out.println("Found relevant entities");
					// Grab each metatag from it's child entity
					for (MetaTagEntity entity : entities) {
						Query mq = mgr.newQuery(MetaTag.class);
						mq.setUnique(true);
						mq.declareImports("import com.google.appengine.api.datastore.Key");
						mq.declareParameters("Key key");
						mq.setFilter("entities.contains(key)");
						Key k = entity.getKey();
						try {

							MetaTag metatag = (MetaTag) mq.execute(k);

							// Build the list of similar words
							Collection<String> indexIds = new HashSet<String>();
							indexIds.add(metatag.getIndexedWordId());
							indexIds.addAll(metatag.getSynonymIndexWordIds());

							// Query for tag entities with the same meaning
							Query eq = mgr.newQuery(MetaTagEntity.class);
							eq.declareImports("import java.util.List");
							eq.declareParameters("List indexedWordIds");
							eq.setFilter("indexedWordIds.contains(indexedWordId) && isUserTag=='TRUE'");

							@SuppressWarnings("unchecked")
							List<MetaTagEntity> similarEntities = (List<MetaTagEntity>) eq.execute(indexIds);

							// If there were relevant entities for this query...
							if (similarEntities != null && !similarEntities.isEmpty()) {
								System.out.println("Found relevant entities");
								Map<Long, Double> entityInterest = new HashMap<Long, Double>();

								for (MetaTagEntity entity2 : similarEntities) {

									if (!entityInterest.containsKey(entity2.getOwnerId())) {
										System.out.println("Adding entity interest");
										entityInterest.put(entity2.getOwnerId(),
												entity.getWeightInTag() * getSimilarityPercent(entity, entity2));
									} else {
										// Check to see if this entity
										// represents higher interest in this
										// metatag
										// update if it does

										double newSimilarity = entity.getWeightInTag()
												* getSimilarityPercent(entity, entity2);
										if (newSimilarity > entityInterest.get(entity2.getOwnerId())) {
											// updated mapped interest
											System.out.println("Updating entity interest");
											entityInterest.put(entity2.getOwnerId(), newSimilarity);
										}
									}

								}

								// Iterate through the entries adding to the
								// total interest
								if (entityInterest.isEmpty()) {
									System.out.println("No interest in this entity");
								} else {
									for (Entry<Long, Double> mapEntity : entityInterest.entrySet()) {
										if (calculatedInterest.containsKey(mapEntity.getKey())) {
											System.out.println("Adding tag interest");
											calculatedInterest.put(mapEntity.getKey(),
													calculatedInterest.get(mapEntity.getKey()) + mapEntity.getValue());
										} else {
											System.out.println("Initializing tag interest");
											calculatedInterest.put(mapEntity.getKey(), mapEntity.getValue());
										}
									}
								}
							}

						} catch (JDOObjectNotFoundException e) {
							e.printStackTrace();
						}
					}

					// Build Vendor event relationships for vendor events people
					// have a high calculated interest in
					List<VendorEventRelationship> relationships = new ArrayList<VendorEventRelationship>();
					for (Entry<Long, Double> interestEntry : calculatedInterest.entrySet()) {

						System.out.println("User: " + interestEntry.getKey() + ", Interest: " + interestEntry.getValue());
						
						// If there is a high interest, create a relationship to
						// this vendor event
						if (interestEntry.getValue() > 0) {
							System.out.println("Adding relationship");
							VendorEventRelationship relationship = new VendorEventRelationship(eventId,
									interestEntry.getKey(), false, false, false, false, null);
							relationships.add(relationship);
						}
					}

					// If there are relevant relationships, make them all
					// persistent
					if (!relationships.isEmpty()) {
						System.out.println("Persisting Relationships");
						mgr.makePersistentAll(relationships);
					}
				}
				// all went through ok
				resp.setStatus(HttpServletResponse.SC_OK);

			} catch (JDOObjectNotFoundException e) {
				e.printStackTrace();
			} finally {
				mgr.close();
			}
		} else {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}

	}

	/**
	 * Determine the percent similarity two tag entities have based on their
	 * weight in parent tag. Assumes these tag entities represent members of
	 * synonym or equal metatag parents
	 * 
	 * @param e1
	 * @param e2
	 * @return similarity percent
	 */
	private double getSimilarityPercent(MetaTagEntity e1, MetaTagEntity e2) {
		double similarityPercent = e2.getWeightInTag() / e1.getWeightInTag();
		if (similarityPercent > 1) {
			similarityPercent = 1 / similarityPercent;
		}
		return similarityPercent;
	}

	/**
	 * Get the persistence manager for interacting with datastore
	 */
	private static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}
}
