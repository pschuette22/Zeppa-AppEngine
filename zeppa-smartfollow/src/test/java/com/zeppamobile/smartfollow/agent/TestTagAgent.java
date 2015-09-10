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
		EventTag tag1 = new EventTag(userAgent1.getUserId(), "Partying");
		EventTag tag3 = new EventTag(userAgent2.getUserId(), "TheRoudyRoofdeck");
		
		TagAgent tagAgent1 = new TagAgent(userAgent1, tag1);
		TagAgent tagAgent3 = new TagAgent(userAgent2, tag3);
		
		
		System.out.println(tag1.getTagText() + " Calculated Similarity to " + tag3.getTagText() +": " + tagAgent1.calculateSimilarity(tagAgent3));

		assertTrue(true);
	}
	
	
}
