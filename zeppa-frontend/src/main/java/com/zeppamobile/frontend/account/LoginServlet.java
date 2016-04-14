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

import com.zeppamobile.common.UniversalConstants;
import com.zeppamobile.common.cerealwrapper.CerealWrapperFactory;
import com.zeppamobile.common.cerealwrapper.UserInfoCerealWrapper;
import com.zeppamobile.common.utils.ModuleUtils;
import com.zeppamobile.common.utils.Utils;

/**
 * 
 * @author Kieran Lynn
 * 
 *         Blank servlet for testing
 *
 */
public class LoginServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6074841711114263838L;


	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html");
		
    	String privakeySuccess = request.getParameter("privakeySuccess");
		
		if(privakeySuccess != null && Boolean.parseBoolean(privakeySuccess))
		{
			request.setAttribute("successDivText", "You have successfully authenticated with PrivaKey");
		}
		else if(privakeySuccess != null)
		{
			request.setAttribute("errorDivText", "There was a problem when authenticating with PrivaKey, please try again.");
		}
		
		request.getRequestDispatcher("WEB-INF/pages/login.jsp").forward(request, response);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

	    String token = req.getParameter("token");
		
		if (Utils.isWebSafe(token)) {

			/*
			 * Parameters accepted, making call to api servlet
			 */
			Map<String, String> params = new HashMap<String, String>();
			params.put("token", token);
			
			/*
			 * Read from the request
			 */
			try {

				URL url = ModuleUtils.getZeppaModuleUrl("zeppa-api",
						"/endpoint/authentication-servlet/", params);

	            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	            connection.setDoOutput(false);
	            connection.setRequestMethod("GET");
	            connection.setReadTimeout(10000); //10 Sec

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(connection.getInputStream()));
				String line;
				
	    
	            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
	                // OK
	            	//resp.getWriter().println("Connection Response OK: " + connection.getResponseMessage());

					// Read from the buffer line by line and write to the response
					// item	
	            	String s = ""; 
					while ((line = reader.readLine()) != null) {
						s += line;
					}
					
					//resp.getWriter().println(s);
					
					CerealWrapperFactory fact = new CerealWrapperFactory();
					UserInfoCerealWrapper userInfo = (UserInfoCerealWrapper)fact.getFromCereal(s);
					
					//resp.getWriter().println(userInfo);
					
					HttpSession session = req.getSession(true);
					session.setAttribute("UserInfo", userInfo);
					
					resp.setStatus(HttpURLConnection.HTTP_OK);
					
					//TODO - Check if the vendor has privakey authentication enabled
					if(userInfo.isPrivaKeyRequired())
					{
						String nonce = Utils.nextSessionId();
						session.setAttribute("PrivaKeyNonce", nonce);
						
						String privakeyURL = "https://idp.privakey.com/identityserver/connect/authorize?";
						privakeyURL += "response_type=id_token";
						privakeyURL += "&response_mode=form_post";
						privakeyURL += "&client_id=" + UniversalConstants.PRIVAKEY_CLIENT_ID;
						privakeyURL += "&scope=openid";
						privakeyURL += "&redirect_uri=" + URLEncoder.encode("https://1-dot-zeppa-frontend-dot-zeppa-cloud-1821.appspot.com/dashboard", "UTF-8");
						privakeyURL += "&nonce=" + URLEncoder.encode(nonce, "UTF-8");
						
						resp.getWriter().append(privakeyURL);
					}
					else
					{
						resp.getWriter().append("/dashboard");
						//resp.sendRedirect("/dashboard");
					}
	            }
	            else if(connection.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED)
	            {
	            	resp.getWriter().println("Employee Not Found: " + connection.getResponseMessage());
	            	
	            	resp.setStatus(HttpURLConnection.HTTP_UNAUTHORIZED);
	            	
	            	//resp.addHeader("isAuthorized", "false");
	            }
	            else {
	                // Server returned HTTP error code.
	            	resp.getWriter().println("Connection Response Error: " + connection.getResponseMessage());
	            	
	            	resp.addHeader("isAuthorized", "false");
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
