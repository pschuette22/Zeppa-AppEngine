package com.zeppamobile.smartfollow.agent;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
		
		assertTrue(true);
	}
	
	
}
