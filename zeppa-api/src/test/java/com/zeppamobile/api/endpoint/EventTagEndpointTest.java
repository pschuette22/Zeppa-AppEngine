package com.zeppamobile.api.endpoint;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.zeppamobile.api.AppConfig;
import com.zeppamobile.api.datamodel.EventTag;
import com.zeppamobile.api.datamodel.ZeppaUser;
import com.zeppamobile.common.utils.TestUtils;

/**
 * 
 * @author Pete Schuette
 * 
 *         Test class to verify functionality of EventTagEndpoint
 *
 */
public class EventTagEndpointTest {

	private final LocalServiceTestHelper helper = new LocalServiceTestHelper(
			new LocalDatastoreServiceTestConfig());

	@Before
	public void setUp() {
		AppConfig.setTestConfig();
		helper.setUp();
	}

	@After
	public void tearDown() {
		helper.tearDown();
	}

	/**
	 * Verify an event tag can be inserted and removed successfully
	 */
	@Test
	public void testInsertEventTag() {

		// Make sure the default test user has been inserted
//		(new ZeppaUserEndpointTest()).testInsertZeppaUser();

		// Insert event tag for this user
		String testToken = TestUtils
				.buildTestAuthToken("testuser1@example.com");
		ZeppaUser user = null;
		try {
			user = (new ZeppaUserEndpoint()).fetchCurrentZeppaUser(testToken);
		} catch (UnauthorizedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail("Couldn't fetch user");
		}

		String newEventTagText = "NewEventTag";
		EventTag newTag = new EventTag(user, newEventTagText);

		try {
			// Try to insert the tag
			EventTag result = (new EventTagEndpoint()).insertEventTag(newTag, testToken);
		
			// Assert the tag is not null and has proper text
			Assert.assertNotNull(result);
			Assert.assertNotNull(result.getKey());
			Assert.assertTrue(newEventTagText.equals(result.getTagText()));
			
			// Fetch the user again
			user = (new ZeppaUserEndpoint()).fetchCurrentZeppaUser(testToken);
			
			// Assert the user and tag are mapped to each other
			Assert.assertTrue(user.getTags().contains(result));
			Assert.assertTrue(result.getOwner().getKey().equals(user.getKey()));
			
		} catch (UnauthorizedException e) {
			Assert.fail("Rejected Auth Token");
		}

	}

}
