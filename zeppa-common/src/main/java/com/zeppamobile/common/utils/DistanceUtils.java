package com.zeppamobile.common.utils;

public class DistanceUtils {

	private static final double EARTH_RADIUS = 6371; // Approximate radius of
														// the earth in
														// kilometers

	/**
	 * private constructor as this is a utility
	 */
	private DistanceUtils() {
	}

	/**
	 * top left and bottom right lat/lon pairs that are a given distance away
	 * from the center point
	 * 
	 * @param distance
	 *            - kilometers away from point
	 * @param centerLat
	 *            - starting point latitude
	 * @param centerLon
	 *            - starting point longitude
	 * @return array of four doubles as such: (top left lat, top left lon,
	 *         bottom right lat, bottom right lon)
	 */
	public static double[] getDistanceBounds(double distance, double centerLat, double centerLon) {
		double[] result = new double[4];

		// Normalize the distance
		double distNorm = distance/EARTH_RADIUS;
		
		// Do calculations for top-left point
		// 3 pi over 4
		double radians_tl = 3*Math.PI/4;
		double latitude_tl = Math.asin(Math.sin(centerLat)*Math.cos(distNorm)+Math.cos(centerLat)*Math.sin(distNorm)*Math.cos(radians_tl));
		double longitude_tl = centerLon+Math.atan2(Math.sin(radians_tl)*Math.sin(distNorm)*Math.cos(centerLat),Math.cos(distNorm)-Math.sin(centerLat)*Math.sin(latitude_tl));
		
		// Do calculations for bottom-right point
		double radians_br = -1*Math.PI/4;
		double latitude_br = Math.asin(Math.sin(centerLat)*Math.cos(distNorm)+Math.cos(centerLat)*Math.sin(distNorm)*Math.cos(radians_br));
		double longitude_br = centerLon+Math.atan2(Math.sin(radians_br)*Math.sin(distNorm)*Math.cos(centerLat),Math.cos(distNorm)-Math.sin(centerLat)*Math.sin(latitude_br));
		
		
		// set the results
		result[0] = latitude_tl;
		result[1] = longitude_tl;
		result[2] = latitude_br;
		result[3] = longitude_br;
		
		return result;
	}

}
