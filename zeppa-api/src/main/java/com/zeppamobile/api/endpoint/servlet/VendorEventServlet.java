package com.zeppamobile.api.endpoint.servlet;

import java.io.IOException;
import java.net.URLDecoder;
import java.security.GeneralSecurityException;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import com.google.api.server.spi.response.UnauthorizedException;
import com.zeppamobile.api.PMF;
import com.zeppamobile.api.datamodel.Address;
import com.zeppamobile.api.datamodel.Employee;
import com.zeppamobile.api.datamodel.Vendor;
import com.zeppamobile.api.datamodel.VendorEvent;
import com.zeppamobile.api.datamodel.ZeppaUserInfo;

/**
 * @author Pete Schuette
 * 
 * What are the 
 *
 */
public class VendorEventServlet extends HttpServlet {

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
		// TODO Auto-generated method stub
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			//Set vendor info
			
			VendorEvent event = new VendorEvent();
			event.setTitle(URLDecoder.decode(req.getParameter("title"), "UTF-8"));
			event.setDescription(URLDecoder.decode(req.getParameter("description"), "UTF-8"));
			event.setStart(Long.parseLong(URLDecoder.decode(req.getParameter("start"), "UTF-8")));
			event.setEnd(Long.parseLong(URLDecoder.decode(req.getParameter("end"), "UTF-8")));
			event.setVendorId(Long.parseLong(URLDecoder.decode(req.getParameter("vendorId"), "UTF-8")));			
			
			event = insertEvent(event);

			// Convert the object to json and return in the writer
			JSONObject json = event.toJson();
			resp.getWriter().write(json.toJSONString());

		} catch (Exception e) {
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			resp.getWriter().print(e.getStackTrace());
		}
	}
	
	
	/**
	 * This inserts a new entity into App Engine datastore. If the entity
	 * already exists in the datastore, an exception is thrown. It uses HTTP
	 * POST method.
	 * 
	 * @param event
	 *            the entity to be inserted.
	 * @return The inserted entity.
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	public VendorEvent insertEvent(VendorEvent event) throws UnauthorizedException,
			IOException {


		// Manager to insert vendor and master employee
		PersistenceManager mgr = getPersistenceManager();
		Transaction txn = mgr.currentTransaction();

		try {
			// Start the transaction
			txn.begin();

			// Set event characteristics
			event.setCreated(System.currentTimeMillis());

			// Persist event
			event = mgr.makePersistent(event);
			
			// commit the changes
			txn.commit();

		} catch (Exception e) {
			// catch any errors that might occur
			e.printStackTrace();
		} finally {

			if (txn.isActive()) {
				txn.rollback();
				event = null;
			}

			mgr.close();

		}

		return event;
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
	
	
	/**
	 * Get the persistence manager for interacting with datastore
	 */
	private static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}
	
	
	

}
