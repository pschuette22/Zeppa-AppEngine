package com.zeppamobile.api.endpoint.servlet;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.zeppamobile.api.PMF;
import com.zeppamobile.api.datamodel.Employee;
import com.zeppamobile.api.datamodel.Vendor;
import com.zeppamobile.api.datamodel.ZeppaUserInfo;

/**
 * 
 * @author Pete Schuette
 * 
 * Servlet for interacting with Vendor data objects
 *
 */
public class VendorServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		try {

			// Fetch the current ID token
			Long vendorID = Long.parseLong(req.getParameter("vendorID"));

			Vendor vendor = getVendor(vendorID);

			resp.setStatus(HttpServletResponse.SC_OK);
			
			// Convert the object to json and return in the writer
			JSONObject json = vendor.toJson();
			resp.getWriter().write(json.toJSONString());
		} catch (UnauthorizedException e) {
			// user is not authorized to make this call
			resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			e.printStackTrace(resp.getWriter());
		} 
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		try {
			//Set vendor info
			System.out.println("In the Vendor Servlet");
			Vendor vendor = new Vendor();
			vendor.setCompanyName("Zeppa");//req.getParameter("companyName"));
			vendor.setIsPrivakeyEnabled(false);
			vendor.setMasterUserId(null);
			vendor.setAddressLine1("Line 1");//req.getParameter("addressLine1"));
			vendor.setAddressLine2("Line 2");//req.getParameter("addressLine2"));
			vendor.setCity("City");//req.getParameter("city"));
			vendor.setState("PA");//req.getParameter("state"));
			vendor.setZipcode(19103);//Integer.parseInt(req.getParameter("zipcode")));
			
			//Set employee info
			Employee employee = new Employee();
			employee.setEmailAddress("Email");//req.getParameter("emailAddress"));
			employee.setIsEmailVerified(false);
			employee.setPassword("Password");//req.getParameter("password"));
			employee.setPrivakeyGuid("");
			
			//Set employees user info
			ZeppaUserInfo userInfo = new ZeppaUserInfo();
			userInfo.setFamilyName("Last");//req.getParameter("lastName"));
			userInfo.setGivenName("First");//req.getParameter("firstName"));
			employee.setUserInfo(userInfo);
			
			insertVendor(vendor, employee);

			// Convert the object to json and return in the writer
			JSONObject json = vendor.toJson();
			resp.getWriter().write(json.toJSONString());

		} catch (Exception e) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			resp.getWriter().print(e.getStackTrace());
		}
	}
	
	
	/**
	 * This method gets the entity having primary key id. It uses HTTP GET
	 * method.
	 * 
	 * @param id
	 *            the primary key of the java bean.
	 * @return The entity with primary key id.
	 * @throws OAuthRequestException
	 */

	@ApiMethod(name = "getVendor", path = "getVendor")
	public Vendor getVendor(@Named("id") Long id) throws UnauthorizedException {


		PersistenceManager mgr = getPersistenceManager();
		Vendor vendor = null;
		try {
			vendor = mgr.getObjectById(Vendor.class, id);

		} finally {
			mgr.close();
		}
		return vendor;
	}

	/**
	 * This inserts a new entity into App Engine datastore. If the entity
	 * already exists in the datastore, an exception is thrown. It uses HTTP
	 * POST method.
	 * 
	 * @param vendor
	 *            the entity to be inserted.
	 * @return The inserted entity.
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	@ApiMethod(name = "insertVendor")
	public Vendor insertVendor(Vendor vendor, Employee employee) throws UnauthorizedException,
			IOException {


		// Manager to insert vendor and master employee
		PersistenceManager mgr = getPersistenceManager();
		Transaction txn = mgr.currentTransaction();

		try {
			// Start the transaction
			txn.begin();

			// Set vendor characteristics
			vendor.setCreated(System.currentTimeMillis());
			vendor.setUpdated(System.currentTimeMillis());

			// Persist Vendor
			vendor = mgr.makePersistent(vendor);

			//Create employee with vendor key
			employee.setCreated(System.currentTimeMillis());
			employee.setUpdated(System.currentTimeMillis());
			employee.setVendorId(vendor.getKey().getId());
			
			employee = mgr.makePersistent(employee);
			
			//Update vendor with the employee master key
			vendor.setMasterUserId(employee.getKey().getId());
			vendor = mgr.makePersistent(vendor);
			
			// commit the changes
			txn.commit();

		} catch (Exception e) {
			// catch any errors that might occur
			e.printStackTrace();
		} finally {

			if (txn.isActive()) {
				txn.rollback();
				vendor = null;
			}

			mgr.close();

		}

		return vendor;
	}

	// /**
	// * This method is used for updating an existing entity. If the entity does
	// * not exist in the datastore, an exception is thrown. It uses HTTP PUT
	// * method.
	// *
	// * @param zeppaevent
	// * the entity to be updated.
	// * @return The updated entity.
	// * @throws OAuthRequestException
	// */
	// @ApiMethod(name = "updateZeppaEvent")
	// public ZeppaEvent updateZeppaEvent(ZeppaEvent zeppaevent,
	// @Named("idToken") String tokenString) throws UnauthorizedException {
	//
	// ZeppaUser user = getAuthorizedZeppaUser(auth);
	//
	// PersistenceManager mgr = getPersistenceManager();
	// try {
	// ZeppaEvent current = mgr.getObjectById(ZeppaEvent.class,
	// zeppaevent.getId());
	//
	// current.setTitle(zeppaevent.getTitle());
	// current.setDescription(zeppaevent.getDescription());
	// current.setStart(zeppaevent.getStart());
	// current.setEnd(zeppaevent.getEnd());
	// current.setMapsLocation(zeppaevent.getMapsLocation());
	// current.setDisplayLocation(zeppaevent.getDisplayLocation());
	//
	// current.setUpdated(System.currentTimeMillis());
	// mgr.makePersistent(current);
	// zeppaevent = current;
	// } finally {
	// mgr.close();
	// }
	// return zeppaevent;
	// }


	/**
	 * Get the persistence manager for interacting with datastore
	 */
	private static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}
	
	

}
