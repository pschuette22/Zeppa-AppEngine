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
		
	    HttpSession session = req.getSession(true);
		Object obj = session.getAttribute("UserInfo");
		if(obj != null)
		{
			UserInfoCerealWrapper userInfo = (UserInfoCerealWrapper)obj;

			String email = req.getParameter("email");
		    String isEnablePrivaKey = req.getParameter("isEnablePrivaKey");
		    
		    if(isEnablePrivaKey != null && isEnablePrivaKey.equals("true") && email != null)
		    {
				try {							
					//resp.getWriter().append("PrivaKey Email: " + email);
					
					String nonce = Utils.nextSessionId();
					session.setAttribute("PrivaKeyNonce", nonce);
					
					String s = "https://idp.privakey.com/identityserver/connect/authorize?";
					s += "response_type=id_token";
					s += "&response_mode=form_post";
					s += "&client_id=" + UniversalConstants.PRIVAKEY_CLIENT_ID;
					s += "&scope=openid";
					s += "&redirect_uri=" + URLEncoder.encode("https://1-dot-zeppa-frontend-dot-zeppa-cloud-1821.appspot.com/account-settings", "UTF-8");
					s += "&nonce=" + URLEncoder.encode(nonce, "UTF-8");
					s += "&login_hint=" + URLEncoder.encode(email, "UTF-8");
					//URI url = new URI(s);
					
					resp.getWriter().append(s);
					
					//req.setAttribute("redirectURL", s);
					//Desktop.getDesktop().browse(url); //PrivaKey requires the URL to be open in a new browser
					
		        
				} catch (Exception e) {
					// TODO Auto-generated catch block
					resp.getWriter().append("Error Message:" + e.getMessage());
				}
		    }
		    else
		    {	
		    	
		    	String privakeySuccess = req.getParameter("privakeySuccess");
		    	String error = req.getParameter("error");
	
				resp.setContentType("text/html");
				req.setAttribute("userInfo", userInfo.toJSON());
				if(privakeySuccess != null && Boolean.parseBoolean(privakeySuccess))
				{
					req.setAttribute("successDivText", "You have successfully authenticated with PrivaKey");
				}
				else if(privakeySuccess != null || error != null)
				{
					req.setAttribute("errorDivText", "There was a problem when authenticating with PrivaKey, please try again.");
				}
				
				req.getRequestDispatcher("WEB-INF/pages/account.jsp").forward(req, resp);

		    }
		}
		else{
			req.getRequestDispatcher("WEB-INF/pages/login.jsp").forward(req, resp);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

	    String id_token = req.getParameter("id_token");		
	    resp.getWriter().println("Account Settings id_token: " + id_token);
	    
		HttpSession session = req.getSession(false);
		String nonce = (String) session.getAttribute("PrivaKeyNonce");
		Long employeeID = null;
		Object obj = session.getAttribute("UserInfo");
		if(obj != null) {
			UserInfoCerealWrapper userInfo = (UserInfoCerealWrapper)obj;
			employeeID = userInfo.getEmployeeID();
		}
		resp.getWriter().append("Account Settings Nonce: " + nonce);
		
		if (Utils.isWebSafe(id_token) && employeeID != null && employeeID > 0) {

			/*
			 * Parameters accepted, making call to api servlet
			 */
			Map<String, String> params = new HashMap<String, String>();
			params.put("id_token", id_token);
			params.put("nonce", nonce);
			params.put("employeeID", employeeID.toString());
			
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
	            connection.setReadTimeout(10000); //10 Sec
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(connection.getInputStream()));
				String line;
				
				resp.getWriter().println("Connection Response Message: " + connection.getResponseMessage());
	            
				String s = ""; 
				while ((line = reader.readLine()) != null) {
					s += line;
				}
				
				resp.getWriter().println("Response: " + s);
				
				if (connection.getResponseCode() == HttpURLConnection.HTTP_CREATED) {
	            	resp.getWriter().println("Connection Response Created: " + connection.getResponseMessage());
	            	resp.sendRedirect("/account-settings?privakeySuccess=true");
										
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
		
		resp.sendRedirect("/account-settings?privakeySuccess=false");
	}

	
	

}
