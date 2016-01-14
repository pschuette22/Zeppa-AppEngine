package com.zeppamobile.api.endpoint;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.zeppamobile.api.AppConfig;

/**
 * 
 * @author PSchuette
 * 
 *         Testing class used to verify functionality of ZeppaUserEndpoint
 * 
 */
public class ZeppaUserEndpointTest {

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
	 * Test that a ZeppaUser can be inserted without error
	 */
	@Test
	public void testInsertZeppaUser() {

//		/*
//		 * Setup the ZeppaUser object to be inserted into db
//		 */
//		String u1AuthEmail = "testuser1@example.com";
//
//		List<String> initialTags = Arrays.asList("TestTag1", "TestTag2",
//				"TestTag3", "TestTag4", "TestTag5", "TestTag6");
//		ZeppaUser testUser = new ZeppaUser(u1AuthEmail, "User1", "Test",
//				"19876543210", -1L, -1L, initialTags);
//		String testToken = TestUtils.buildTestAuthToken(u1AuthEmail);
//
//		// Make sure this user is invited
//		InviteGroup group = new InviteGroup();
//		group.setEmails(Arrays.asList(u1AuthEmail, "example@email.com"));
//		group.setSuggestedTags(Arrays.asList("TestTag1", "TestTag2",
//				"TestTag3", "TestTag4", "TestTag5", "TestTag6"));
//
//		// Insert the invite group to make sure this user is authorized to make
//		// an account
//		InviteGroup insertedGroup = (new InviteGroupEndpoint())
//				.insertInviteGroup(group);
//
//		Assert.assertNotNull(insertedGroup);
//
//		try {
//			// Insert and assert
//			ZeppaUser result = (new ZeppaUserEndpoint()).insertZeppaUser(
//					testUser, testToken);
//			Assert.assertNotNull(result);
//			testUser = result;
//
//		} catch (UnauthorizedException e) {
//			// Auth exception (probably didn't set to test)
//			e.printStackTrace();
//			Assert.fail("Unauthorized");
//		} catch (IOException e) {
//			// IO Exception.. here to make compiler happy
//			e.printStackTrace();
//			Assert.fail("IOException");
//		}
//
//		/*
//		 * Assert the tags were put into datastore
//		 */
//		List<EventTag> tags = testUser.getTags();
//		// Assert that all initial tags were created
//		Assert.assertTrue(tags.size() == initialTags.size());
//		for (int i = 0; i < tags.size(); i++) {
//			EventTag t = tags.get(i);
//			// Assert tag has a db key
//			Assert.assertNotNull(t.getKey());
//			// Assert tag is mapped to user
//			Assert.assertEquals(t.getOwner(), testUser);
//			// Assert tag has proper text
//			Assert.assertEquals(t.getTagText(), initialTags.get(i));
//		}
//
//		// Assert that the invite group contains the test users DB key
//		// Assert.assertTrue(insertedGroup.getGroupMemberKeys().contains(testUser.getKey()));
//
//		// TODO: assert that another user added to the DB in invite group is
//		// automatically connected
	}

	// /**
	// * Test that a user can be updated after is inserted into the datastore
	// */
	// @Test
	// public void testUpdateZeppaUser() {
	//
	// /*
	// * Setup the ZeppaUser object to be inserted into db
	// */
	// String u1AuthEmail = "testuser1@example.com";
	// String givenName = "User1";
	// ZeppaUser testUser = new ZeppaUser(u1AuthEmail, givenName, "Test",
	// "19876543210", -1L, -1L);
	// String testToken = TestUtils.buildTestAuthToken(u1AuthEmail);
	//
	// try {
	//
	// // Insert and assert
	// ZeppaUser result = (new ZeppaUserEndpoint()).insertZeppaUser(
	// testUser, testToken);
	// Assert.assertNotNull(result);
	// testUser = result;
	// } catch (UnauthorizedException e) {
	// // Auth exception (probably didn't set to test)
	// e.printStackTrace();
	// Assert.fail("Unauthorized");
	// } catch (IOException e) {
	// // IO Exception.. here to make compiler happy
	// e.printStackTrace();
	// Assert.fail("IOException");
	// }
	//
	// // Assert that the given name is correct
	// Assert.assertTrue(testUser.getUserInfo().getGivenName()
	// .equals(givenName));
	//
	// // Set set a new name for this user
	// String newName = "ChangedName";
	// testUser.getUserInfo().setGivenName(newName);
	//
	// try {
	// // Update and assert
	// ZeppaUser result = (new ZeppaUserEndpoint()).updateZeppaUser(testUser,
	// testToken);
	// Assert.assertTrue(result.getUserInfo().getGivenName().equals(newName));
	// } catch (UnauthorizedException e) {
	// // Auth exception?
	// e.printStackTrace();
	// Assert.fail("Unauthorized, second use");
	// }
	//
	// }

}
