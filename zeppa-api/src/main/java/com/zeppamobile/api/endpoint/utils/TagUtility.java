package com.zeppamobile.api.endpoint.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.appengine.api.datastore.Key;
import com.zeppamobile.api.PMF;
import com.zeppamobile.api.datamodel.EventTag;
import com.zeppamobile.api.datamodel.MetaTag;
import com.zeppamobile.api.datamodel.MetaTagEntity;
import com.zeppamobile.api.datamodel.Vendor;
import com.zeppamobile.api.datamodel.ZeppaUser;
import com.zeppamobile.common.UniversalConstants;
import com.zeppamobile.common.utils.ModuleUtils;
import com.zeppamobile.common.utils.Utils;

/**
 * 
 * @author Pete Schuette
 * 
 *         Tag utility is used to manage tag tasks that are not considered time
 *         sensitive
 *
 */
public class TagUtility {

	/**
	 * index a tag a store it's meta-tag data
	 * 
	 * @param tagId
	 * @param isUserTag
	 */
	public static void indexTag(Long tagId, boolean isUserTag) {

		PersistenceManager mgr = getPersistenceManager();
		Transaction txn = mgr.currentTransaction();
		try {
			txn.begin();

			EventTag tag = mgr.getObjectById(EventTag.class, tagId);

			ZeppaUser userOwner = null;
			Vendor vendorOwner = null;

			if (isUserTag) {
				userOwner = mgr.getObjectById(ZeppaUser.class, tag.getOwnerId());
			} else {
				vendorOwner = mgr.getObjectById(Vendor.class, tag.getOwnerId());
			}

			// Get the indexed words from smartfollow
			Map<String, String> params = new HashMap<String, String>();
			params.put(UniversalConstants.kREQ_TAG_TEXT, tag.getTagText());

			URL url = ModuleUtils.getZeppaModuleUrl("zeppa-smartfollow", "word-tagger", params);

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(false);
			connection.setRequestMethod("GET");
			connection.setReadTimeout(60*1000);

			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;

			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				// Read from the buffer line by line and write to the response
				// item

				String responseString = "";
				while ((line = reader.readLine()) != null) {
					responseString += line;
				}
				if (Utils.isWebSafe(responseString)) {
					JSONParser parser = new JSONParser();
					JSONObject json = (JSONObject) parser.parse(responseString);

					@SuppressWarnings("unchecked")
					List<String> indexWords = (List<String>) json.get(UniversalConstants.kJSON_INDEX_WORD_LIST);
					@SuppressWarnings("unchecked")
					Map<String, List<String>> synsMap = (Map<String, List<String>>) json
							.get(UniversalConstants.kJSON_INDEX_WORD_SYNS_MAP);

					// TODO: Determine the weight based on word POS
					double totalWeight = 0.0;

					for (String indexWord : indexWords) {
						totalWeight += getIndexWordWeight(indexWord);
					}

					// TODO: Add metatag objects pointing to this tag/ user into
					// the datastore

					for (String indexWord : indexWords) {

						MetaTag metatag = null;
						try {
							// try to fetch the appropriate metatag
							Query q = mgr.newQuery(MetaTag.class);
							q.setFilter("indexedWordId=='"+indexWord+"'");
							q.setUnique(true);
							metatag = (MetaTag) q.execute();

						} catch (JDOObjectNotFoundException e) {
							// Catch it not being found quickly
						}

						// if the metatag was not found, assume a new one must
						// be made
						if (metatag == null) {
							metatag = new MetaTag(indexWord, synsMap.get(indexWord));
							metatag = mgr.makePersistent(metatag);
						}

						// Assume the metatag was created or fetched
						// Create the entity
						// NOTE: set to .5 weight for all right now. Needs to be
						// changed ASAP

						MetaTagEntity entity = new MetaTagEntity(tag.getId(),
								(isUserTag ? userOwner.getId() : vendorOwner.getKey().getId()),indexWord, isUserTag,
								(getIndexWordWeight(indexWord) / totalWeight));
						entity = mgr.makePersistent(entity);
						List<Key> entities = metatag.getEntities();
						entities.add(entity.getKey());
						metatag.setEntities(entities);
					}

					// Update the tag so it has index words for computation
					tag.setIndexedWords(indexWords);
				}
			} else {
				// TODO: reschedule task if recoverable exception
				// TODO: notify admins there was an issue
			}

			reader.close();

			txn.commit();

		} catch (JDOObjectNotFoundException e) {
			// Event tag could not be found..
			// TODO: flag the error and notify system there was an issue
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (txn.isActive()) {
				txn.rollback();
			}
			mgr.close();
		}

	}

	/**
	 * Get the tag word weight based on the part of speech
	 * 
	 * @param indexWord
	 * @return associated weight or 0;
	 */
	public static double getIndexWordWeight(String indexWord) {
		if (indexWord.contains("-N-")) {
			// noun
			return UniversalConstants.WEIGHT_NOUN;
		} else if (indexWord.contains("-V-")) {
			// verb
			return UniversalConstants.WEIGHT_VERB;
		} else if (indexWord.contains("-R-")) {
			// adverb
			return UniversalConstants.WEIGHT_ADVERB;
		} else if (indexWord.contains("-A-")) {
			// adjective
			return UniversalConstants.WEIGHT_ADJECTIVE;
		} else {
			return 0;
		}
	}

	/**
	 * Get the persistence manager
	 * 
	 * @return
	 */
	public static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}
}
