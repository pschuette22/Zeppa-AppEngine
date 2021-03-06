package com.zeppamobile.api.endpoint.servlet;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.appengine.repackaged.org.apache.commons.codec.binary.StringUtils;
import com.google.gson.JsonElement;
import com.zeppamobile.api.Constants;
import com.zeppamobile.api.datamodel.Employee;
import com.zeppamobile.common.UniversalConstants;
import com.zeppamobile.common.cerealwrapper.UserInfoCerealWrapper;

/**
 * Servlet implementation class PrivaKey
 */
public class PrivaKeyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PrivaKeyServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		try
		{
			String token = request.getParameter("id_token");
			Long employeeID = Long.parseLong(request.getParameter("employeeID"));
			String nonce = request.getParameter("nonce");
	
			String sub = validateToken(token, nonce);
			
			response.getWriter().append("PrivaKey Servlet Sub: " + sub);
			response.getWriter().append("PrivaKey Servlet Employee ID: " + employeeID);
			if(sub != null && employeeID != null)
			{
			
				Employee employee = EmployeeServlet.getEmployeeByID(employeeID);
				
				//Check that the PrivaKey GUID we have in the datastore matches the subject identifier given by PrivaKey
				if(employee != null && employee.getPrivakeyGuid().equals(sub))
				{
					response.setStatus(HttpServletResponse.SC_OK);
				}
				else
				{
					response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				}
			}
			else
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
		} catch (Exception e) {
			response.getWriter().append("PrivaKey Servlet Error: " + e.getMessage());
			response.setStatus(HttpServletResponse.SC_CONFLICT);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			String token = request.getParameter("id_token");
			Long employeeID = Long.parseLong(request.getParameter("employeeID"));
			String nonce = request.getParameter("nonce");

			String sub = validateToken(token, nonce);
			
			response.getWriter().append("PrivaKey Servlet Sub: " + sub);
			response.getWriter().append("PrivaKey Servlet Employee ID: " + employeeID);
			if(sub != null && employeeID != null)
			{
			
				Employee employee = EmployeeServlet.updateEmployeePrivaKeyID(employeeID, sub);
				
				if(employee != null)
				{
					response.setStatus(HttpServletResponse.SC_CREATED);
				}
				else
				{
					response.setStatus(HttpServletResponse.SC_CONFLICT);
				}
			}
			else
			{
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
		} catch (Exception e) {
			response.getWriter().append("PrivaKey Servlet Error: " + e.getMessage());
			response.setStatus(HttpServletResponse.SC_CONFLICT);
		}
	}
	
	private String validateToken(String token, String nonce)
	{
		
		try 
		{
			String[] base64EncodedSegments = token.split("\\.");		 
	
			String base64EncodedHeader = base64EncodedSegments[0];
			String base64EncodedClaims = base64EncodedSegments[1];
			JSONParser parser = new JSONParser();
			JSONObject claims = (JSONObject)parser.parse(StringUtils.newStringUtf8(Base64.decodeBase64(base64EncodedClaims)));
			
			
			String sub = (String) claims.get("sub");
			String tokenNonce = (String) claims.get("nonce");// This value should be checked to match the one sent in the request
			String iss = (String) claims.get("iss"); // Should be https://idp.privakeyapp.com/identityserver
			String aud = (String) claims.get("aud"); //Should contain our client id
			Long expTime = (Long) claims.get("exp");
			
			if(tokenNonce.equals(nonce) && aud.equals(UniversalConstants.PRIVAKEY_CLIENT_ID))
			{
				return sub;
			}
			else
			{
				return null;
			}
		} catch (Exception e) {
			return null;
		}
		
	}

}
