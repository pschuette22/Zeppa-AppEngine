package com.zeppamobile.api.endpoint.servlet;

import java.io.IOException;
import javax.servlet.http.HttpServlet;

import com.google.api.server.spi.response.UnauthorizedException;
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

    	// Add Vendor and Employee to the datastore
		Address add = new Address();
		add.setAddressLine1("123 Test Street");
		add.setCity("Philadelphia");
		add.setState("PA");
		add.setZipCode(19104);
		Vendor vendor = new Vendor();
		vendor.setAddress(add);
		vendor.setCompanyName("Test Company 1");
		vendor.setMasterUserId(Long.valueOf("123"));
		ZeppaUserInfo ui = new ZeppaUserInfo();
		ui.setGivenName("Jim");
		ui.setFamilyName("McGreevey");
		Employee employee = new Employee();
		employee.setUserInfo(ui);
		
		try {
			VendorServlet.insertVendor(vendor, employee);
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
			EventTagServlet.insertVendor(tag);
			EventTagServlet.insertVendor(tag2);
		} catch (UnauthorizedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

}
