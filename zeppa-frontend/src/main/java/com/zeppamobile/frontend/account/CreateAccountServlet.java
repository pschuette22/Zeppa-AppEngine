package com.zeppamobile.frontend.account;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;

import com.zeppamobile.common.UniversalConstants;
import com.zeppamobile.common.cerealwrapper.CerealWrapperFactory;
import com.zeppamobile.common.cerealwrapper.UserInfoCerealWrapper;
import com.zeppamobile.common.utils.ModuleUtils;
import com.zeppamobile.common.utils.Utils;

/**
 * 
 * @author Brendan
 * 
 *         Creat Account Servlet 
 *
 */
public class CreateAccountServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6074841711114263838L;


    

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html");
	    String token = request.getParameter("token");
	    String email = request.getParameter("email");
		request.setAttribute("token", token);
		request.setAttribute("email", email);
		
		request.getRequestDispatcher("WEB-INF/pages/create-account.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		
		/*
		 * Get the vendor and employee info from parameters
		 */
	    String firstName = request.getParameter("firstName");
	    String lastName = request.getParameter("lastName");
	    String emailAddress = request.getParameter("emailAddress");
	    String companyName = request.getParameter("companyName");
	    String addressLine1 = request.getParameter("addressLine1");
	   	String addressLine2 = request.getParameter("addressLine2");
	   	String city = request.getParameter("city");
	   	String state = request.getParameter("state");
	   	String zipcode = request.getParameter("zipcode");
	   	//String password = request.getParameter("password");
		
		if (Utils.isWebSafe(firstName)) {

			/*
			 * Parameters accepted, making call to api servlet
			 * Encode each param before building the URL
			 */
			Map<String, String> params = new HashMap<String, String>();
			params.put("firstName", URLEncoder.encode(firstName, "UTF-8"));
			params.put("lastName", URLEncoder.encode(lastName, "UTF-8"));
			params.put("emailAddress", URLEncoder.encode(emailAddress, "UTF-8"));
			params.put("companyName", URLEncoder.encode(companyName, "UTF-8"));
			params.put("addressLine1", URLEncoder.encode(addressLine1, "UTF-8"));
			params.put("addressLine2", URLEncoder.encode(addressLine2, "UTF-8"));
			params.put("city", URLEncoder.encode(city, "UTF-8"));
			params.put("state", URLEncoder.encode(state, "UTF-8"));
			params.put("zipcode", URLEncoder.encode(zipcode, "UTF-8"));
			//params.put("password", URLEncoder.encode(password, "UTF-8"));
			
			/*
			 * Read from the request
			 */
			try {
				
				// Generate the url to go to the VendorServlet on the api module
				URL url = ModuleUtils.getZeppaModuleUrl("zeppa-api",
						"/endpoint/vendor-servlet/", params);
				
	            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	            connection.setDoOutput(false);
	            connection.setRequestMethod("POST");

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(connection.getInputStream()));
				String line;
				
	            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
					// Read from the buffer line by line and write to the response
					// item	
	            	String s = ""; 
					while ((line = reader.readLine()) != null) {
						s += line;
					}
					
					CerealWrapperFactory fact = new CerealWrapperFactory();
					UserInfoCerealWrapper userInfo = (UserInfoCerealWrapper)fact.getFromCereal(s);
					
					HttpSession session = request.getSession(true);
					session.setAttribute("UserInfo", userInfo);
					
					JSONObject obj = new JSONObject();
	            	obj.put("redirectURL", "/dashboard?accountCreated=true");
	            	response.getWriter().println(obj.toString());

					
	            }
	            else if(connection.getResponseCode() == HttpURLConnection.HTTP_FORBIDDEN)
	            {
	            	JSONObject obj = new JSONObject();
	            	obj.put("forbidden", "true");
	            	response.getWriter().println(obj.toString());
	            }
	            else {
	                // Server returned HTTP error code.
	            	response.getWriter().println("Connection Response Error: " + connection.getResponseMessage());
	            	
					// Read from the buffer line by line and write to the response
					// item					
					while ((line = reader.readLine()) != null) {
						response.getWriter().println(line);
					}
	            }
	            
	            reader.close();
	        } catch (MalformedURLException e) {
				e.printStackTrace(response.getWriter());
	        } catch (IOException e) {
				e.printStackTrace(response.getWriter());
			} catch (Exception e) {
				e.printStackTrace(response.getWriter());
				
			}

		} else {
			/*
			 * If bad parameters were passed
			 */
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}
	

}
