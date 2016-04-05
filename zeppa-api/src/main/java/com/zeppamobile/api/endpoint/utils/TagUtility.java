package com.zeppamobile.api.endpoint.utils;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import com.zeppamobile.api.PMF;
import com.zeppamobile.api.datamodel.EventTag;
import com.zeppamobile.api.datamodel.ZeppaUser;

/**
 * 
 * @author Pete Schuette
 * 
 * Tag utility is used to manage tag tasks that are not considered time sensitive
 *
 */
public class TagUtility {

	
	/**
	 * index a tag a store it's meta-tag data
	 * @param tagId
	 * @param isUserTag
	 */
	public static void indexTag(Long tagId, boolean isUserTag){
		
		PersistenceManager mgr = getPersistenceManager();
		Transaction txn = mgr.currentTransaction();
		try {
			txn.begin();
			
			EventTag tag = mgr.getObjectById(EventTag.class, tagId);
			ZeppaUser tagOwner = mgr.getObjectById(ZeppaUser.class, tag.getOwnerId());

			// TODO: Get the indexed words from smartfollow
			
			// TODO: Determine the weight based on word POS
			
			// TODO: Add metatag objects pointing to this tag/ user into the datastore 
			
			txn.commit();
			
		} catch (JDOObjectNotFoundException e) { 
			// Event tag could not be found..
			// TODO: flag the error and notify system there was an issue
			e.printStackTrace();
		} finally {
			if(txn.isActive()){
				txn.rollback();
			}
			mgr.close();
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
