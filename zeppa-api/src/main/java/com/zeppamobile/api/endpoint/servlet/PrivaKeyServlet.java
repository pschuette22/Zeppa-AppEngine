package com.zeppamobile.api.endpoint.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.zeppamobile.api.Constants;

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
		try {
		
		response.getWriter().append("Served at: ").append(request.getContextPath());
		
		String email = request.getParameter("email");
		
		response.getWriter().append("PrivaKey Email: " + email);
		
		String s = "https://idp.privakeyapp.com/identityserver/connect/authorize?";
		s += "response_type=id_token";
		s += "&response_mode=form_post";
		s += "&client_id=" + Constants.PRIVAKEY_CLIENT_ID;
		s += "&scope=openid";
		s += "&redirect_uri=" + URLEncoder.encode("https://1-dot-zeppa-api-dot-zeppa-cloud-1821.appspot.com/privakey/", "UTF-8");
		s += "&nonce=" + URLEncoder.encode(email, "UTF-8");
		//s += "&login_hint=openid";
		URL url = new URL(s);
		
		response.getWriter().append("PrivaKey URL: " + s);
		
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(false);
        connection.setRequestMethod("GET");
        
        connection.connect();
        
        response.getWriter().append("Tesing after connect ");
        
		} catch (Exception e) {
			// TODO Auto-generated catch block
			response.getWriter().append("Error Message:" + e.getMessage());
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String token = request.getParameter("id_token");
		
		response.getWriter().append("PrivaKey ID Token: " + token);
		try {
			JSONObject json = (JSONObject)new JSONParser().parse(token);
			
			String sub = (String) json.get("sub");
			String email = URLDecoder.decode((String) json.get("nonce"), "UTF-8");// This value should be checked to match the one sent in the request
			String iss = (String) json.get("iss"); // Should be https://idp.privakeyapp.com/identityserver
			String aud = (String) json.get("aud"); //Should contain our client id
			String expTime = (String) json.get("exp");
			response.getWriter().append("PrivaKey ID Sub Value:" + sub);
			
			EmployeeServlet.updateEmployeePrivaKeyID(email, sub);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
