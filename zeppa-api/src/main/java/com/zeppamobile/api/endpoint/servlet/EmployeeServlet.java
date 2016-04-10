package com.zeppamobile.api.endpoint.servlet;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.google.api.server.spi.response.UnauthorizedException;
import com.zeppamobile.api.PMF;
import com.zeppamobile.api.datamodel.Employee;

/**
 * 
 * @author Pete Schuette
 * 
 * Servlet for interacting with employee objects
 *
 */
public class EmployeeServlet extends HttpServlet {

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

		//try {

			// Fetch the current ID token
			boolean isLogin = Boolean.parseBoolean(req.getParameter("isLogin"));
			
			if(isLogin)
			{
				String email = req.getParameter("email");
				String password = req.getParameter("password");
				
				if(!email.isEmpty() && !password.isEmpty())
					resp.setStatus(HttpServletResponse.SC_OK);
				else
					resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
			else
			{
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}


			
			
		//} catch (UnauthorizedException e) {
		//	// user is not authorized to make this call
		//	resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		//	e.printStackTrace(resp.getWriter());
		//} 
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		
	}
	
	/**
	 * This inserts a new Employee entity into App Engine datastore. If the entity
	 * already exists in the datastore, an exception is thrown.
	 * 
	 * @param employee - the entity to be inserted.
	 * @return The inserted entity.
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	public static Employee insertVendor(Employee employee) throws UnauthorizedException,
			IOException {
		// Manager to insert the tag
		PersistenceManager mgr = getPersistenceManager();
		Transaction txn = mgr.currentTransaction();

		try {
			// Start the transaction
			txn.begin();

			// Set tag time characteristics
			employee.setCreated(System.currentTimeMillis());
			employee.setUpdated(System.currentTimeMillis());

			// Persist the tag
			employee = mgr.makePersistent(employee);
			// commit the changes
			txn.commit();

		} catch (Exception e) {
			// catch any errors that might occur
			e.printStackTrace();
		} finally {

			if (txn.isActive()) {
				txn.rollback();
				employee = null;
			}

			mgr.close();

		}

		return employee;
	}

	/**
	 * This updates the employees privaKey identifier in the data store
	 * 
	 * @param employeeID - the employee ID to be updated.
	 * @return The updated entity.
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	public static Employee updateEmployeePrivaKeyID(Long employeeID, String privakeyGuid)  {
		// Manager to insert the tag
		PersistenceManager mgr = getPersistenceManager();
		Transaction txn = mgr.currentTransaction();

		Employee employee = null;
		try {
			// Start the transaction
			txn.begin();
			
			employee = mgr.getObjectById(Employee.class, employeeID);

			// Set tag time characteristics
			employee.setUpdated(System.currentTimeMillis());

			employee.setPrivakeyGuid(privakeyGuid);
			
			// Persist the tag
			employee = mgr.makePersistent(employee);
			// commit the changes
			txn.commit();

		} catch (Exception e) {
			// catch any errors that might occur
			e.printStackTrace();
		} finally {

			if (txn.isActive()) {
				txn.rollback();
				employee = null;
			}

			mgr.close();

		}

		return employee;
	}
	
	/**
	 * Get the persistence manager for interacting with datastore
	 */
	private static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}
}
