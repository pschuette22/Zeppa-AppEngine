package com.zeppamobile.api.tasks;

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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.mortbay.log.Log;

import com.google.api.client.util.Lists;
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
 * Servlet to handle indexing tags asyncronously
 * 
 * @author PSchuette
 *
 */
public class TagIndexingServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * indexing occurs in post request
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String tagIdString = req.getParameter(UniversalConstants.PARAM_TAG_ID);
		String isUserTagString = req.getParameter(UniversalConstants.PARAM_IS_USER_TAG);

		Log.warn("Indexing tag: " + tagIdString);
		
		if (Utils.isWebSafe(tagIdString)) {

			Long tagId = Long.valueOf(tagIdString);
			boolean isUserTag = Boolean.valueOf(isUserTagString);

			PersistenceManager mgr = getPersistenceManager();
//			Transaction txn = mgr.currentTransaction();
			try {
//				txn.begin();
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
				connection.setReadTimeout(60 * 1000);

				

				if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
					// Read from the buffer line by line and write to the
					// response
					// item

					// Read and close the response
					BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					String line;
					String responseString = "";
					while ((line = reader.readLine()) != null) {
						responseString += line;
					}
					reader.close();


					if (Utils.isWebSafe(responseString)) {
						JSONParser parser = new JSONParser();
						JSONObject json = (JSONObject) parser.parse(responseString);

						@SuppressWarnings("unchecked")
						Map<String, List<String>> synsMap = (Map<String, List<String>>) json
								.get(UniversalConstants.kJSON_INDEX_WORD_SYNS_MAP);

						// TODO: Determine the weight based on word POS
						double totalWeight = 0.0;

						for (String indexWord : synsMap.keySet()) {
							totalWeight += getIndexWordWeight(indexWord);
						}

						// TODO: Add metatag objects pointing to this tag/ user
						// into
						// the datastore

						for (String indexWord : synsMap.keySet()) {

							MetaTag metatag = null;
							try {
								// try to fetch the appropriate metatag
								Query q = mgr.newQuery(MetaTag.class);
								q.setFilter("indexedWordId=='" + indexWord + "'");
								q.setUnique(true);
								metatag = (MetaTag) q.execute();

							} catch (JDOObjectNotFoundException e) {
								// Catch it not being found quickly
							}

							// if the metatag was not found, assume a new one
							// must
							// be made
							if (metatag == null) {
								metatag = new MetaTag(indexWord, synsMap.get(indexWord));
								metatag = mgr.makePersistent(metatag);
							}

							// Assume the metatag was created or fetched
							// Create the entity
							// NOTE: set to .5 weight for all right now. Needs
							// to be
							// changed ASAP

							MetaTagEntity entity = new MetaTagEntity(tag.getId(),
									(isUserTag ? userOwner.getId() : vendorOwner.getKey().getId()), isUserTag,
									(getIndexWordWeight(indexWord) / totalWeight));
							entity = mgr.makePersistent(entity);
							List<Key> entities = metatag.getEntities();
							entities.add(entity.getKey());
							metatag.setEntities(entities);
							metatag = mgr.makePersistent(metatag);
						}

						// Update the tag so it has index words for computation
						tag.setIndexedWords(Lists.newArrayList(synsMap.keySet()));
						tag = mgr.makePersistent(tag);
						
						
					}
				} else {
					// TODO: reschedule task if recoverable exception
					// TODO: notify admins there was an issue
				}


//				txn.commit();

			} catch (JDOObjectNotFoundException e) {
				// Event tag could not be found..
				// TODO: flag the error and notify system there was an issue
				e.printStackTrace();
				throw(e);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw(e);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw(e);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw(e);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
//				if (txn.isActive()) {
//					txn.rollback();
//					Log.warn("Tag failed to index: " + tagIdString);
//				}
				mgr.close();
			}
		}

	}

	/**
	 * Get the tag word weight based on the part of speech
	 * 
	 * @param indexWord
	 * @return associated weight or 0;
	 */
	public static double getIndexWordWeight(String indexWord) {
		if (indexWord.endsWith("-n")) {
			// noun
			return UniversalConstants.WEIGHT_NOUN;
		} else if (indexWord.endsWith("-v")) {
			// verb
			return UniversalConstants.WEIGHT_VERB;
		} else if (indexWord.endsWith("-r")) {
			// adverb
			return UniversalConstants.WEIGHT_ADVERB;
		} else if (indexWord.endsWith("-a")) {
			// adjective
			return UniversalConstants.WEIGHT_ADJECTIVE;
		} else {
			return 0;
		}
	}

	/**
	 * Get the persistence manager for interacting with datastore
	 */
	private static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}
}
