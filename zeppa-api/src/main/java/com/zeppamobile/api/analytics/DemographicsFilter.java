package com.zeppamobile.api.analytics;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.datastore.Key;
import com.zeppamobile.api.PMF;
import com.zeppamobile.api.datamodel.ZeppaUser;
import com.zeppamobile.api.datamodel.ZeppaUserInfo.Gender;
import com.zeppamobile.common.utils.DistanceUtils;

/**
 * Filter user objects to be considered in analytics request
 * 
 * @author PSchuette
 *
 */
public class DemographicsFilter {

	/*
	 * Data relevant to the center point of location to be queried for
	 */
	private double latitude;

	private double longitude;

	/** max distance from latitude/longitude pair in kilometers */
	private double distance;

	/*
	 * User attributes filters
	 */

	private Gender gender;

	private int minAge;

	private int maxAge;

	/*
	 * Specific to filter
	 */

	/** Keys that match this filters params */
	private List<Key> userKeys;

	/** Millis since epoch userKeys were last updated */
	private long updateTimeInMillis;

	/**
	 * Instantiate a DemographicsFilter for
	 * 
	 * @param latitude
	 * @param longitude
	 * @param distance
	 * @param gender
	 * @param minAge
	 * @param maxAge
	 */
	public DemographicsFilter(double latitude, double longitude, double distance, Gender gender, int minAge,
			int maxAge) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.distance = distance;
		this.gender = gender;
		this.minAge = minAge;
		this.maxAge = maxAge;
	}

	/**
	 * Update the held user keys
	 * 
	 * @return
	 */
	public List<Key> updateUserKeys() {

		// Query for users and update keys
		PersistenceManager mgr = PMF.get().getPersistenceManager();
		try {

			// Initialize the Query and set params
			Query query = mgr.newQuery(ZeppaUser.class);

			StringBuilder filterBuilder = new StringBuilder();
			double[] bounds = DistanceUtils.getDistanceBounds(distance, latitude, longitude);
			// Add location constraints to users in query
			double latitude_tl = bounds[0];
			double longitude_tl = bounds[1];
			double latitude_br = bounds[2];
			double longitude_br = bounds[3];

			// Add location window to filter
			filterBuilder.append("(latitude<" + latitude_tl + " && latitude>" + latitude_br + ") && (longitude<"
					+ longitude_tl + " && longitude>" + longitude_br + ")");

			// if there is a gender constraint, add to filter
			if (gender != null) {

				filterBuilder.append(" && (userInfo.gender=='" + gender.toString() + "')");
			}

			// If there are age constraints, add min age filter
			if (minAge > 0) {

				long minAgeInMillis = System.currentTimeMillis();
				// Subtract this many years in milliseconds
				// TODO: verify this is the best way to handle this..
				// considering leap year
				minAgeInMillis -= (1000 * 60 * 60 * 24 * 365 * minAge);

				filterBuilder.append(" && (userInfo.dateOfBirth<" + minAgeInMillis + ")");
			}

			// If there is a max age, add max age filter
			if (maxAge > 0) {
				long maxAgeInMillis = System.currentTimeMillis();
				// Subtract this many years in milliseconds
				maxAgeInMillis -= (1000 * 60 * 60 * 24 * 365 * maxAge);

				filterBuilder.append(" && (userInfo.dateOfBirth>" + maxAgeInMillis + ")");
			}

			// After all user filters have been applied, fetch the keys relevant
			// users
			query.setFilter(filterBuilder.toString());
			@SuppressWarnings("unchecked")
			List<ZeppaUser> matchingUsers = (List<ZeppaUser>) query.execute();
			// quickly iterate through response object of matching users adding
			// keys
			// If join queries are not acceptable, execute add logic here
			// Perhaps we should move gender/birthday to zeppa user object..?
			userKeys = new ArrayList<Key>();
			for (ZeppaUser u : matchingUsers) {
				userKeys.add(u.getKey());
			}

		} finally {
			mgr.close();
		}

		return userKeys;
	}

	/**
	 * Get the currently held user keys or update them if null
	 * 
	 * @return
	 */
	public List<Key> getUserKeys() {
		return (userKeys == null ? updateUserKeys() : userKeys);
	}

}
