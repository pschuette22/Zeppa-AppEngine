package com.zeppamobile.smartfollow.agent;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.zeppamobile.common.datamodel.EventTag;
import com.zeppamobile.smartfollow.Configuration;


/**
 * Basic testing for TagAgent 
 * @author Pete Schuette
 *
 */
public class TestTagAgent {
	
	@Before
	public void setup(){
		Configuration.startTesting();
		
	}
	
	@After
	public void teardown(){
		Configuration.stopTesting();
	}
	
	@Test
	public void doTesting(){
		UserAgent userAgent1 = new UserAgent(Long.valueOf(1));
		UserAgent userAgent2 = new UserAgent(Long.valueOf(2));
		EventTag tag1 = new EventTag(userAgent1.getUserId(), "TestingPrograms");
		EventTag tag2 = new EventTag(userAgent2.getUserId(), "PlayingFootball");
		
		TagAgent tagAgent1 = new TagAgent(userAgent1, tag1);
		TagAgent tagAgent2 = new TagAgent(userAgent2, tag2);
		
		double similarity = tagAgent1.calculateSimilarity(tagAgent2);
		System.out.println("Calculated Similarity: " + similarity);
		assertTrue(true);
	}
	
	
}
