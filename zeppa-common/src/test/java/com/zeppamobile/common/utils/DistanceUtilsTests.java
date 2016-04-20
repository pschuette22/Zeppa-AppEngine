package com.zeppamobile.common.utils;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test Class for verifying distance utility
 *
 * @author PSchuette
 *
 */
public class DistanceUtilsTests {

	@Test
	public void doGetDistanceBoundsTest() {
		// Verify proper bounds from Drexel University

		double distance = 20;// km
		double drexelLat = 39.9572;
		double drexelLon = -75.1629;
		double[] bounds = DistanceUtils.getDistanceBounds(distance, drexelLat, drexelLon);

		// Verify the point exists with the bounds
		assertTrue(bounds[0] > drexelLat);
		assertTrue(bounds[1] > drexelLon);
		assertTrue(bounds[2] < drexelLat);
		assertTrue(bounds[3] < drexelLon);
		
		// TODO: more comprehensive tests, this is super lazy!!!

		System.out.println("Coordinates: (" + drexelLat + "," + drexelLon + ")");
		System.out.println("Bounds Coordinates: Top Left - (" + bounds[0] + "," + bounds[1] + "); Bottom Right - ("
				+ bounds[2] + "," + bounds[3] + ")");

	}

}