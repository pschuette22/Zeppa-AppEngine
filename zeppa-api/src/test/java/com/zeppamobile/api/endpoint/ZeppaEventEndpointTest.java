package com.zeppamobile.api.endpoint;

import org.junit.After;
import org.junit.Before;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.zeppamobile.api.AppConfig;

/**
 * 
 * @author PSchuette
 *
 *	Testing class used to verify functionality of ZeppaEventEndpoint
 *
 */
public class ZeppaEventEndpointTest {

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
			new LocalDatastoreServiceTestConfig()
					.setDefaultHighRepJobPolicyRandomSeed(100));

	@Before
	public void setUp() {
		AppConfig.setTestConfig();
		helper.setUp();
	}

	@After
	public void tearDown() {
		helper.tearDown();
		AppConfig.doneTesting();
	}
	
	
	
	
	
}
