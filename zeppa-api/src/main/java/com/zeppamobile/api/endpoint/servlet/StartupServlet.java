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
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
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
		vendor.setMasterUserId(Long.valueOf(employeeBrendan.getKey().getId()));
		
		try {
			vendor = VendorServlet.insertVendor(vendor, employeeBrendan);
		} catch (UnauthorizedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Vendor vendor1 = new Vendor();
		vendor1.setAddress(add);
		vendor1.setCompanyName("Test Company 2");
		vendor1.setMasterUserId(Long.valueOf(employeeKieran.getKey().getId()));
		
		try {
			vendor1 = VendorServlet.insertVendor(vendor1, employeeKieran);
		} catch (UnauthorizedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Add EventTags to the datastore
		EventTag tag = new EventTag();
		tag.setOwnerId(vendor.getKey().getId());
		tag.setTagText("Happy Hour");
		tag.setType(TagType.VENDOR);
		
		EventTag tag2 = new EventTag();
		tag2.setOwnerId(vendor.getKey().getId());
		tag2.setTagText("Drink Special");
		tag2.setType(TagType.VENDOR);
		
		try {
			EventTagServlet.insertTag(tag);
			EventTagServlet.insertTag(tag2);
		} catch (UnauthorizedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<Long> tagIds = new ArrayList<Long>();
		tagIds.add(tag.getId());
		tagIds.add(tag2.getId());
		VendorEvent event = new VendorEvent("Test Event", "test event description", System.currentTimeMillis(), 
				(System.currentTimeMillis() + 10000), vendor.getKey().getId(), tagIds, "Address Holder");
		
		try {
			VendorEventServlet.insertEvent(event);
		} catch (UnauthorizedException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		// Create Users
		AppConfig.setTestConfig();
//		LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()
//		.setDefaultHighRepJobPolicyRandomSeed(100)
//		.setDefaultHighRepJobPolicyUnappliedJobPercentage(100));
//		helper.setUp();
		String u1AuthEmail = "testuser1@example.com";
		String u2AuthEmail = "testuser2@example.com";
		List<String> initialTags = Arrays.asList("TestTag1", "TestTag2", "TestTag3", "TestTag4", "TestTag5",
				"TestTag6");
		ZeppaUser testUser = new ZeppaUser(u1AuthEmail, "User1", "Test", "19876543210", -1L, -1L, initialTags);
		ZeppaUserInfo ui = testUser.getUserInfo();
		ui.setGender(Gender.MALE);
		testUser.setUserInfo(ui);
		String testToken = TestUtils.buildTestAuthToken(u1AuthEmail);
		
		ZeppaUser testUser2 = new ZeppaUser(u2AuthEmail, "User2", "Test2", "19876543210", -1L, -1L, initialTags);
		ZeppaUserInfo ui2 = testUser2.getUserInfo();
		ui2.setGender(Gender.FEMALE);
		testUser2.setUserInfo(ui2);
		String testToken2 = TestUtils.buildTestAuthToken(u2AuthEmail);
		
		// Make sure this user is invited
		InviteGroup group = new InviteGroup();
		group.setEmails(Arrays.asList(u1AuthEmail, u2AuthEmail));
		group.setSuggestedTags(Arrays.asList("TestTag1", "TestTag2",
				"TestTag3", "TestTag4", "TestTag5", "TestTag6"));

		// Insert the invite group to make sure this user is authorized to make
		// an account
		InviteGroup insertedGroup = (new InviteGroupEndpoint())
				.insertInviteGroup(group);
		try {
			// Insert and assert
			(new ZeppaUserEndpoint()).insertZeppaUser(
					testUser, testToken);

			(new ZeppaUserEndpoint()).insertZeppaUser(
					testUser2, testToken2);
		} catch (UnauthorizedException e) {
			// Auth exception (probably didn't set to test)
			e.printStackTrace();
		} catch (IOException e) {
			// IO Exception.. here to make compiler happy
			e.printStackTrace();
		}
		
		System.out.println("------------" + testUser.getAuthEmail());
		System.out.println("------------" + testUser.getKey());
		// Create user relationships to event
		VendorEventRelationship ver = new VendorEventRelationship(5699868278390784L, event.getId(), true, false, false, false, new ArrayList<Long>());
		VendorEventRelationship ver2 = new VendorEventRelationship(5840605766746112L, event.getId(), true, false, false, false, new ArrayList<Long>());
		
		try {
			VendorEventRelationshipServlet.insertEventRelationship(ver);
			VendorEventRelationshipServlet.insertEventRelationship(ver2);
		} catch (UnauthorizedException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
//		helper.tearDown();
		AppConfig.doneTesting();
    }

}
