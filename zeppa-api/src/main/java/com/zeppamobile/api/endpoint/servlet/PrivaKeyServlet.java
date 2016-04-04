package com.zeppamobile.api.endpoint.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
		
		String email = request.getParameter("email");
		
		response.getWriter().append("PrivaKey Email: " + email);
		
		String s = "https://idp.privakeyapp.com/identityserver/connect/authorize?";
		s += "response_type=id_token";
		s += "&response_mode=form_post";
		s += "&client_id=" + Constants.PRIVAKEY_CLIENT_ID;
		s += "&scope=" + URLEncoder.encode("openid " + email, "UTF-8");
		s += "&redirect_uri=" + URLEncoder.encode("https://1-dot-zeppa-api-dot-zeppa-cloud-1821.appspot.com/privakey/", "UTF-8");
		s += "&nonce=" + URLEncoder.encode(email, "UTF-8");
		//s += "&login_hint=openid";
		URL url = new URL(s);
		
		response.getWriter().append("PrivaKey URL: " + s);
		
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(false);
        connection.setRequestMethod("GET");
        
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(connection.getInputStream()));
		
		String line;
		
	    
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            // OK
			// Read from the buffer line by line and write to the response
			// item	
        	String st = ""; 
			while ((line = reader.readLine()) != null) {
				st += line;
			}
			
			response.getWriter().append(st);
        }
        
        reader.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String token = request.getParameter("id_token");
		
		response.getWriter().append(token);
		try {
			JSONObject json = (JSONObject)new JSONParser().parse(token);
			
			String sub = (String) json.get("sub");
			
			response.getWriter().append(sub);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
