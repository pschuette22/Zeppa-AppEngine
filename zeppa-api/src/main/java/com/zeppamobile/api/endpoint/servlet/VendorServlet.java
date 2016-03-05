package com.zeppamobile.api.endpoint.servlet;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.zeppamobile.api.PMF;
import com.zeppamobile.api.datamodel.Address;
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
			vendor.setCompanyName(URLDecoder.decode(req.getParameter("companyName"), "UTF-8"));
			vendor.setIsPrivakeyEnabled(false);
			vendor.setMasterUserId(Long.valueOf(-1));
			
			Address address = new Address();
			address.setCity(URLDecoder.decode(req.getParameter("city"), "UTF-8"));
			address.setState(URLDecoder.decode(req.getParameter("state"), "UTF-8"));
			address.setZipCode(Integer.parseInt(URLDecoder.decode(req.getParameter("zipcode"), "UTF-8")));
			address.setAddressLine1(URLDecoder.decode(req.getParameter("addressLine1"), "UTF-8"));
			address.setAddressLine2(URLDecoder.decode(req.getParameter("addressLine2"), "UTF-8"));
			vendor.setAddress(address);
			
			 
			//Set employee info
			Employee employee = new Employee();
			employee.setEmailAddress(URLDecoder.decode(req.getParameter("emailAddress"), "UTF-8"));
			employee.setIsEmailVerified(false);
			employee.setPassword(URLDecoder.decode(req.getParameter("password"), "UTF-8"));
			employee.setPrivakeyGuid("");
			
			
			//Set employees user info
			ZeppaUserInfo userInfo = new ZeppaUserInfo();
			userInfo.setFamilyName(URLDecoder.decode(req.getParameter("lastName"), "UTF-8"));
			userInfo.setGivenName(URLDecoder.decode(req.getParameter("firstName"), "UTF-8"));
			employee.setUserInfo(userInfo);
			
			vendor = insertVendor(vendor, employee);

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
	public Vendor getVendor(Long id) throws UnauthorizedException {


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
	public static Vendor insertVendor(Vendor vendor, Employee employee) throws UnauthorizedException,
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
