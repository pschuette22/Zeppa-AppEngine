package com.zeppamobile.api.endpoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.zeppamobile.api.AppConfig;
import com.zeppamobile.api.datamodel.ZeppaEvent;
import com.zeppamobile.api.datamodel.ZeppaEvent.EventPrivacyType;
import com.zeppamobile.api.datamodel.ZeppaUser;
import com.zeppamobile.common.utils.TestUtils;

/**
 * 
 * @author PSchuette
 *
 *         Testing class used to verify functionality of ZeppaEventEndpoint
 *
 */
public class ZeppaEventEndpointTest {

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
			new LocalDatastoreServiceTestConfig()
					.setDefaultHighRepJobPolicyRandomSeed(100)
					.setDefaultHighRepJobPolicyUnappliedJobPercentage(100));

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

	/**
	 * Test the functionality of the ZeppaEvent endpoint.
	 */
	@Test
	public void testZeppaEvent() {

//		// test user 1 email and auth token (for testing)
//		String u1AuthEmail = "testuser1@example.com";
//		String testToken = TestUtils.buildTestAuthToken(u1AuthEmail);
//
//		// Make sure the test user is inserted into the database
//		(new ZeppaUserEndpointTest()).testInsertZeppaUser();
//		ZeppaUser user = null;
//		try {
//			user = (new ZeppaUserEndpoint()).fetchCurrentZeppaUser(testToken);
//		} catch (UnauthorizedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			Assert.fail("Auth Token Rejected");
//		}
//
//		// Make test Zeppa Event instance
//		ZeppaEvent event = new ZeppaEvent("google-calendar-id",
//				"google-calendar-event-id", "iCal-UID",
//				EventPrivacyType.CASUAL, user.getId(), "Event Title",
//				"Event Description", Boolean.TRUE, System.currentTimeMillis(),
//				System.currentTimeMillis() + 1000 * 60 * 60,
//				"Display Location", "Maps Location", Arrays.asList(-1L, -2L),
//				new ArrayList<Long>());
//
//		try {
//			// Insert and assert
//			event = (new ZeppaEventEndpoint()).insertZeppaEvent(event,
//					testToken);
//			Assert.assertNotNull(event);
//			Assert.assertNotNull(event.getKey());
//
//		} catch (UnauthorizedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			Assert.fail("Auth Token Rejected");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			Assert.fail("Auth Token Rejected");
//		}

	}

}
