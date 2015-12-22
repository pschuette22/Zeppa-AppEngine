package com.zeppamobile.api.endpoint;

import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.zeppamobile.api.AppConfig;
import com.zeppamobile.api.datamodel.ZeppaUser;
import com.zeppamobile.api.datamodel.ZeppaUserInfo;
import com.zeppamobile.common.utils.TestUtils;

public class ZeppaUserEndpointTest {

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

//	/**
//	 * Test that a ZeppaUser can be inserted without error
//	 */
//	@Test
//	public void testInsertZeppaUser() {
//		/*
//		 * Setup the ZeppaUser object to be inserted into db
//		 */
//		String u1AuthEmail = "testuser1@example.com";
//		ZeppaUser testUser = new ZeppaUser();
//		ZeppaUserInfo u1Info = new ZeppaUserInfo();
//		u1Info.setGivenName("User1");
//		u1Info.setFamilyName("Test");
//		u1Info.setImageUrl("testuser1imageurl.jpg");
//
//		// user data
//		testUser.setAuthEmail(u1AuthEmail);
//		testUser.setPhoneNumber("19876543210");
//		testUser.setLongitude(-1L);
//		testUser.setLatitude(-1L);
//
//		String testToken = TestUtils.buildTestAuthToken(u1AuthEmail);
//
//		// Assess the insertion
//		try {
//			ZeppaUser result = (new ZeppaUserEndpoint()).insertZeppaUser(
//					testUser, testToken);
//			Assert.assertNotNull(result);
//			Assert.assertTrue(result.getAuthEmail().equals(u1AuthEmail));
//		} catch (UnauthorizedException e) {
//			e.printStackTrace();
//			Assert.fail("Unauthorized");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			Assert.fail("IOException");
//
//		}
//
//	}

}
