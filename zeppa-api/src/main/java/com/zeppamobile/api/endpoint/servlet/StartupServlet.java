package com.zeppamobile.api.endpoint.servlet;

import java.io.IOException;
import javax.servlet.http.HttpServlet;

import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.utils.SystemProperty;
import com.zeppamobile.api.datamodel.Address;
import com.zeppamobile.api.datamodel.Employee;
import com.zeppamobile.api.datamodel.EventTag;
import com.zeppamobile.api.datamodel.Vendor;
import com.zeppamobile.api.datamodel.ZeppaUserInfo;
import com.zeppamobile.api.datamodel.EventTag.TagType;

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
    	ZeppaUserInfo kevinUI = new ZeppaUserInfo();
    	kevinUI.setGivenName("Kevin");
    	kevinUI.setFamilyName("Moratelli");
    	Employee employeeKevin = new Employee();
    	employeeKevin.setUserInfo(kevinUI);
    	employeeKevin.setEmailAddress("kevin.moratelli@gmail.com");
    	
    	// Kieran Account
    	ZeppaUserInfo kieranUI = new ZeppaUserInfo();
    	kieranUI.setGivenName("Kieran");
    	kieranUI.setFamilyName("Lynn");
    	Employee employeeKieran = new Employee();
    	employeeKieran.setUserInfo(kieranUI);
    	employeeKieran.setEmailAddress("kieran.j.lynn@gmail.com");
    	
    	// Pete Account
    	ZeppaUserInfo peteUI = new ZeppaUserInfo();
    	peteUI.setGivenName("Pete");
    	peteUI.setFamilyName("Schuette");
    	Employee employeePete = new Employee();
    	employeePete.setUserInfo(peteUI);
    	employeePete.setEmailAddress("pschuette22@gmail.com");
    	
    	// Brendan Account
    	ZeppaUserInfo brendanUI = new ZeppaUserInfo();
    	brendanUI.setGivenName("Brendan");
    	brendanUI.setFamilyName("Kennedy");
    	Employee employeeBrendan = new Employee();
    	employeeBrendan.setUserInfo(brendanUI);
    	employeeBrendan.setEmailAddress("bken123@gmail.com");
    	
    	// Eric Account
    	ZeppaUserInfo ericUI = new ZeppaUserInfo();
    	ericUI.setGivenName("Eric");
    	ericUI.setFamilyName("Most");
    	Employee employeeEric = new Employee();
    	employeeEric.setUserInfo(ericUI);
    	employeeEric.setEmailAddress("ericmmost@gmail.com");
    	
    	try {
			EmployeeServlet.insertVendor(employeeKevin);
			EmployeeServlet.insertVendor(employeeKieran);
			EmployeeServlet.insertVendor(employeePete);
			EmployeeServlet.insertVendor(employeeBrendan);
			EmployeeServlet.insertVendor(employeeEric);
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
		vendor.setMasterUserId(Long.valueOf("123"));
		
		try {
			VendorServlet.insertVendor(vendor, employeeKevin);
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
    }

}
