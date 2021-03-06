package com.zeppamobile.api.endpoint.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServlet;

import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.utils.SystemProperty;
import com.zeppamobile.api.AppConfig;
import com.zeppamobile.api.datamodel.Address;
import com.zeppamobile.api.datamodel.Employee;
import com.zeppamobile.api.datamodel.EmployeeUserInfo;
import com.zeppamobile.api.datamodel.EventTag;
import com.zeppamobile.api.datamodel.Vendor;
import com.zeppamobile.api.datamodel.VendorEvent;
import com.zeppamobile.api.datamodel.VendorEventRelationship;
import com.zeppamobile.api.datamodel.ZeppaUser;
import com.zeppamobile.api.datamodel.ZeppaUserInfo;
import com.zeppamobile.api.datamodel.ZeppaUserInfo.Gender;
import com.zeppamobile.api.endpoint.InviteGroupEndpoint;
import com.zeppamobile.api.endpoint.ZeppaUserEndpoint;
import com.zeppamobile.common.utils.TestUtils;
import com.zeppamobile.api.datamodel.EventTag.TagType;
import com.zeppamobile.api.datamodel.EventTagFollow;
import com.zeppamobile.api.datamodel.InviteGroup;

/**
 * Servlet implementation class StartupServlet
 */
public class StartupServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	/**
	 * This is run each time the api module is started up.
	 * Create any data objects you need for testing and and insert them in the data store
	 * in this method.
	 */
    @SuppressWarnings("unused")
	public void init() {

    	// If not running in Development environment then just return without running anything
    	if(SystemProperty.environment.value() != SystemProperty.Environment.Value.Development) {
    		return;
    	}
    	
    	
    	// Kevin Account
    	EmployeeUserInfo kevinUI = new EmployeeUserInfo();
    	kevinUI.setGivenName("Kevin");
    	kevinUI.setFamilyName("Moratelli");
    	Employee employeeKevin = new Employee();
    	employeeKevin.setUserInfo(kevinUI);
    	employeeKevin.setEmailAddress("kevin.moratelli@gmail.com");
    	
    	// Kieran Account
    	EmployeeUserInfo kieranUI = new EmployeeUserInfo();
    	kieranUI.setGivenName("Kieran");
    	kieranUI.setFamilyName("Lynn");
    	Employee employeeKieran = new Employee();
    	employeeKieran.setUserInfo(kieranUI);
    	employeeKieran.setEmailAddress("kieran.j.lynn@gmail.com");
    	
    	// Pete Account
    	EmployeeUserInfo peteUI = new EmployeeUserInfo();
    	peteUI.setGivenName("Pete");
    	peteUI.setFamilyName("Schuette");
    	Employee employeePete = new Employee();
    	employeePete.setUserInfo(peteUI);
    	employeePete.setEmailAddress("pschuette22@gmail.com");
    	
    	// Brendan Account
    	EmployeeUserInfo brendanUI = new EmployeeUserInfo();
    	brendanUI.setGivenName("Brendan");
    	brendanUI.setFamilyName("Kennedy");
    	Employee employeeBrendan = new Employee();
    	employeeBrendan.setUserInfo(brendanUI);
    	employeeBrendan.setEmailAddress("bken123@gmail.com");
    	
    	// Eric Account
    	EmployeeUserInfo ericUI = new EmployeeUserInfo();
    	ericUI.setGivenName("Eric");
    	ericUI.setFamilyName("Most");
    	Employee employeeEric = new Employee();
    	employeeEric.setUserInfo(ericUI);
    	employeeEric.setEmailAddress("ericmmost@gmail.com");
    	
    	try {
    		employeeKevin = EmployeeServlet.insertVendor(employeeKevin);
    		employeeKieran = EmployeeServlet.insertVendor(employeeKieran);
    		employeePete = EmployeeServlet.insertVendor(employeePete);
    		employeeBrendan = EmployeeServlet.insertVendor(employeeBrendan);
    		employeeEric = EmployeeServlet.insertVendor(employeeEric);
		} catch (UnauthorizedException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    	
    	
    	// Add Vendor and address to data store
		Address add = new Address();
		add.setAddressLine1("123 Test Street");
		add.setCity("Philadelphia");
		add.setState("PA");
		add.setZipCode(19104);
		Vendor vendor = new Vendor();
		vendor.setAddress(add);
		vendor.setCompanyName("Test Company 1");
		vendor.setMasterUserId(Long.valueOf(employeeKevin.getKey().getId()));
		
		try {
			vendor = VendorServlet.insertVendor(vendor, employeeKevin);
		} catch (UnauthorizedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Vendor vendor1 = new Vendor();
		Address add2 = new Address();
		add2.setAddressLine1("2 street");
		add2.setCity("Philadelphia");
		add2.setState("PA");
		add2.setZipCode(19104);
		vendor1.setAddress(add2);
		vendor1.setCompanyName("Test Company 2");
		vendor1.setMasterUserId(Long.valueOf(employeeKieran.getKey().getId()));
		
		try {
			vendor1 = VendorServlet.insertVendor(vendor1, employeeKieran);
		} catch (UnauthorizedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		Vendor vendor2 = new Vendor();
		Address add3 = new Address();
		add3.setAddressLine1("2 street");
		add3.setCity("Philadelphia");
		add3.setState("PA");
		add3.setZipCode(19104);
		vendor2.setAddress(add3);
		vendor2.setCompanyName("Test Company 3");
		vendor2.setMasterUserId(Long.valueOf(employeeBrendan.getKey().getId()));
		
		try {
			vendor2 = VendorServlet.insertVendor(vendor2, employeeBrendan);
		} catch (UnauthorizedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Add EventTags to the datastore
		EventTag tag = new EventTag();
		tag.setOwnerId(vendor.getKey().getId());
		tag.setTagText("HappyHour");
		tag.setType(TagType.VENDOR);
		
		EventTag tag2 = new EventTag();
		tag2.setOwnerId(vendor.getKey().getId());
		tag2.setTagText("DrinkSpecial");
		tag2.setType(TagType.VENDOR);
		
		EventTag tag3 = new EventTag();
		tag3.setOwnerId(vendor.getKey().getId());
		tag3.setTagText("Marathon");
		tag3.setType(TagType.VENDOR);
		
		EventTag tag4 = new EventTag();
		tag4.setOwnerId(vendor.getKey().getId());
		tag4.setTagText("PlayBasketball");
		tag4.setType(TagType.USER);
		
		EventTag tag5 = new EventTag();
		tag5.setOwnerId(vendor.getKey().getId());
		tag5.setTagText("WatchFootball");
		tag5.setType(TagType.VENDOR);
		
		EventTag tag6 = new EventTag();
		tag6.setOwnerId(vendor.getKey().getId());
		tag6.setTagText("PlayPoker");
		tag6.setType(TagType.USER);
		
		try {
			EventTagServlet.insertTag(tag);
			EventTagServlet.insertTag(tag2);
			EventTagServlet.insertTag(tag3);
			EventTagServlet.insertTag(tag4);
			EventTagServlet.insertTag(tag5);
			EventTagServlet.insertTag(tag6);
		} catch (UnauthorizedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		List<Long> tagIds = new ArrayList<Long>();
		tagIds.add(tag.getId());
		tagIds.add(tag2.getId());
		tagIds.add(tag3.getId());
		tagIds.add(tag4.getId());
		tagIds.add(tag5.getId());
		tagIds.add(tag6.getId());
		VendorEvent event = new VendorEvent("Test Event", "test event description", (System.currentTimeMillis() + 80000000L), 
				(System.currentTimeMillis() + 10000), vendor.getKey().getId(), tagIds, "Address Holder");
		String LongDescription = "Long descriptioin Long descriptioin Long descriptioin Long descriptioin Long descriptioin Long descriptioin Long descriptioin Long descriptioin Long descriptioin Long descriptioin ";
		VendorEvent event2 = new VendorEvent("Test Event2", LongDescription, (System.currentTimeMillis() + 800000000L), 
				(System.currentTimeMillis() + 10000), vendor.getKey().getId(), tagIds, "Drexel University, Philadelphia PA");
		VendorEvent event3 = new VendorEvent("Test Event3", LongDescription, 1459036800000L, 
				(System.currentTimeMillis() + 10000), vendor.getKey().getId(), tagIds, "Drexel University");
		VendorEvent event4 = new VendorEvent("Test Event4", LongDescription, 1458950400000L, 
				(System.currentTimeMillis() + 10000), vendor.getKey().getId(), tagIds, "Drexel University");
		VendorEvent event5 = new VendorEvent("Test Event5", LongDescription, 1459209600000L, 
				(System.currentTimeMillis() + 10000), vendor.getKey().getId(), tagIds, "Drexel University");
		VendorEvent event6 = new VendorEvent("Test Event6", LongDescription, 1459296000000L, 
				(System.currentTimeMillis() + 10000), vendor.getKey().getId(), tagIds, "Drexel University");

		
		try {
			VendorEventServlet.insertEvent(event);
			VendorEventServlet.insertEvent(event2);
			VendorEventServlet.insertEvent(event3);
			VendorEventServlet.insertEvent(event4);
			VendorEventServlet.insertEvent(event5);
			VendorEventServlet.insertEvent(event6);
		} catch (UnauthorizedException | IOException e) {
			e.printStackTrace();
		}
		

		// Create Users
		AppConfig.setTestConfig();
		String u1AuthEmail = "testuser1@example.com";
		String u2AuthEmail = "testuser2@example.com";
		String u3AuthEmail = "testuser3@example.com";
		List<String> initialTags = Arrays.asList("TestTag1", "TestTag2", "TestTag3", "TestTag4", "TestTag5",
				"TestTag6");
		ZeppaUser testUser = new ZeppaUser(u1AuthEmail, "User1", "Test", "19876543210", -1L, -1L, initialTags);
		ZeppaUserInfo ui = testUser.getUserInfo();
		// 1993
		ui.setGender(Gender.MALE);
		ui.setDateOfBirth(746232615000L);
		testUser.setUserInfo(ui);
		String testToken = TestUtils.buildTestAuthToken(u1AuthEmail);
		
		ZeppaUser testUser2 = new ZeppaUser(u2AuthEmail, "User2", "Test2", "19876543210", -1L, -1L, initialTags);
		ZeppaUserInfo ui2 = testUser2.getUserInfo();
		ui2.setGender(Gender.FEMALE);
		// 1983
		ui2.setDateOfBirth(430613415000L);
		testUser2.setUserInfo(ui2);
		String testToken2 = TestUtils.buildTestAuthToken(u2AuthEmail);
		
		ZeppaUser testUser3 = new ZeppaUser(u3AuthEmail, "User3", "Test3", "19876543210", -1L, -1L, initialTags);
		ZeppaUserInfo ui3 = testUser3.getUserInfo();
		ui3.setGender(Gender.FEMALE);
		// 2003
		ui3.setDateOfBirth(1061765415000L);
		testUser3.setUserInfo(ui3);
		String testToken3 = TestUtils.buildTestAuthToken(u3AuthEmail);
		
		// Make sure this user is invited
		InviteGroup group = new InviteGroup();
		group.setEmails(Arrays.asList(u1AuthEmail, u2AuthEmail, u3AuthEmail));
		group.setSuggestedTags(Arrays.asList("TestTag1", "TestTag2",
				"TestTag3", "TestTag4", "TestTag5", "TestTag6"));

		// Insert the invite group to make sure this user is authorized to make
		// an account
		InviteGroup insertedGroup = (new InviteGroupEndpoint())
				.insertInviteGroup(group);
		try {
			// Insert and assert
			testUser = (new ZeppaUserEndpoint()).insertZeppaUser(
					testUser, testToken);

			testUser2 = (new ZeppaUserEndpoint()).insertZeppaUser(
					testUser2, testToken2);
			
			testUser3 = (new ZeppaUserEndpoint()).insertZeppaUser(
					testUser3, testToken3);
		} catch (UnauthorizedException e) {
			// Auth exception (probably didn't set to test)
			e.printStackTrace();
		} catch (IOException e) {
			// IO Exception.. here to make compiler happy
			e.printStackTrace();
		}
		
		// Create user relationships to event
		VendorEventRelationship ver = new VendorEventRelationship(testUser.getId(), event.getId(), true, false, true, false, new ArrayList<Long>());
		VendorEventRelationship ver2 = new VendorEventRelationship(testUser2.getId(), event.getId(), true, false, false, false, new ArrayList<Long>());
		VendorEventRelationship ver3 = new VendorEventRelationship(testUser3.getId(), event.getId(), true, false, false, false, new ArrayList<Long>());
		VendorEventRelationship ver4 = new VendorEventRelationship(testUser.getId(), event2.getId(), true, false, false, false, new ArrayList<Long>());
		VendorEventRelationship ver5 = new VendorEventRelationship(testUser2.getId(), event2.getId(), true, false, false, false, new ArrayList<Long>());
		VendorEventRelationship ver6 = new VendorEventRelationship(testUser.getId(), event3.getId(), true, false, false, false, new ArrayList<Long>());
		VendorEventRelationship ver7 = new VendorEventRelationship(testUser.getId(), event4.getId(), true, false, false, false, new ArrayList<Long>());
		VendorEventRelationship ver8 = new VendorEventRelationship(testUser3.getId(), event5.getId(), true, false, true, false, new ArrayList<Long>());
		VendorEventRelationship ver9 = new VendorEventRelationship(testUser.getId(), event6.getId(), true, false, false, false, new ArrayList<Long>());
		VendorEventRelationship ver10 = new VendorEventRelationship(testUser.getId(), event6.getId(), true, false, false, false, new ArrayList<Long>());
		
		try {
			VendorEventRelationshipServlet.insertEventRelationship(ver);
			VendorEventRelationshipServlet.insertEventRelationship(ver2);
			VendorEventRelationshipServlet.insertEventRelationship(ver3);
			VendorEventRelationshipServlet.insertEventRelationship(ver4);
			VendorEventRelationshipServlet.insertEventRelationship(ver5);
			VendorEventRelationshipServlet.insertEventRelationship(ver6);
			VendorEventRelationshipServlet.insertEventRelationship(ver7);
			VendorEventRelationshipServlet.insertEventRelationship(ver8);
			VendorEventRelationshipServlet.insertEventRelationship(ver9);
			VendorEventRelationshipServlet.insertEventRelationship(ver10);
		} catch (UnauthorizedException | IOException e1) {
			e1.printStackTrace();
		}
		
		EventTagFollow etf1 = new EventTagFollow(tag, testUser.getId());
		EventTagFollow etf2 = new EventTagFollow(tag2, testUser.getId());
		EventTagFollow etf3 = new EventTagFollow(tag, testUser2.getId());
		EventTagFollow etf4 = new EventTagFollow(tag3, testUser2.getId());
		EventTagFollow etf5 = new EventTagFollow(tag4, testUser2.getId());
		EventTagFollow etf6 = new EventTagFollow(tag5, testUser3.getId());
		EventTagFollow etf7 = new EventTagFollow(tag5, testUser3.getId());
		
		try {
			EventTagFollowServlet.insertTagFollow(etf1);
			EventTagFollowServlet.insertTagFollow(etf2);
			EventTagFollowServlet.insertTagFollow(etf3);
			EventTagFollowServlet.insertTagFollow(etf4);
			EventTagFollowServlet.insertTagFollow(etf5);
			EventTagFollowServlet.insertTagFollow(etf6);
			EventTagFollowServlet.insertTagFollow(etf7);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		AppConfig.doneTesting();
    }

}
