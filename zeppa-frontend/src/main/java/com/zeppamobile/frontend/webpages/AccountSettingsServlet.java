package com.zeppamobile.frontend.webpages;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.zeppamobile.common.UniversalConstants;
import com.zeppamobile.common.cerealwrapper.CerealWrapperFactory;
import com.zeppamobile.common.cerealwrapper.UserInfoCerealWrapper;
import com.zeppamobile.common.utils.ModuleUtils;
import com.zeppamobile.common.utils.Utils;

/**
 * 
 * @author Pete Schuette
 * 
 *         Blank servlet for testing
 *
 */
public class AccountSettingsServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6074841711114263838L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		String email = req.getParameter("email");
	    String isEnablePrivaKey = req.getParameter("isEnablePrivaKey");
	    
	    if(isEnablePrivaKey != null && isEnablePrivaKey.equals("true") && email != null)
	    {
			try {							
				resp.getWriter().append("PrivaKey Email: " + email);
				
				String s = "https://idp.privakeyapp.com/identityserver/connect/authorize?";
				s += "response_type=id_token";
				s += "&response_mode=form_post";
				s += "&client_id=" + UniversalConstants.PRIVAKEY_CLIENT_ID;
				s += "&scope=openid";
				s += "&redirect_uri=" + URLEncoder.encode("https://1-dot-zeppa-frontend-dot-zeppa-cloud-1821.appspot.com/account-settings", "UTF-8");
				s += "&nonce=" + URLEncoder.encode(email, "UTF-8");
				s += "&login_hint=" + URLEncoder.encode(email, "UTF-8");
				URI url = new URI(s);
				
				resp.getWriter().append("PrivaKey URL: " + s);
				
				Desktop.getDesktop().browse(url); //PrivaKey requires the URL to be open in a new browser
				
	        
			} catch (Exception e) {
				// TODO Auto-generated catch block
				resp.getWriter().append("Error Message:" + e.getMessage());
			}
	    }
	    else
	    {		
			resp.setContentType("text/html");
			req.setAttribute("attribute1", "This is attribute 1");
			
			req.getRequestDispatcher("WEB-INF/pages/account.jsp").forward(req, resp);
	    }
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

	    String id_token = req.getParameter("id_token");
		
	    resp.getWriter().println("Account Settings id_token: " + id_token);
	    
		if (Utils.isWebSafe(id_token)) {

			/*
			 * Parameters accepted, making call to api servlet
			 */
			Map<String, String> params = new HashMap<String, String>();
			params.put("id_token", id_token);
			
			/*
			 * Read from the request
			 */
			try {
				URL url = ModuleUtils.getZeppaModuleUrl("zeppa-api",
						"privakey/", params);

				resp.getWriter().println("Account Settings URL: " + url);
	            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	            connection.setDoOutput(false);
	            connection.setRequestMethod("POST");

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(connection.getInputStream()));
				String line;
					    
	            if (connection.getResponseCode() == HttpURLConnection.HTTP_CREATED) {
	            	resp.getWriter().println("Connection Response Created: " + connection.getResponseMessage());
	            	resp.sendRedirect("/account-settings");
										
	            }
	            
	            reader.close();
	        } catch (MalformedURLException e) {
				e.printStackTrace(resp.getWriter());
	        } catch (IOException e) {
				e.printStackTrace(resp.getWriter());
			} catch (Exception e) {
				e.printStackTrace(resp.getWriter());
				
			}

		} else {
			/*
			 * If bad parameters were passed
			 */
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

	
	

}
