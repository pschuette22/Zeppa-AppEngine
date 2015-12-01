package com.zeppamobile.frontend.emailform;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zeppamobile.common.utils.Utils;

/**
 * Servlet implementation class EmailFormServlet
 */
public class EmailFormServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		
		// Set status to found at first just in case uncaught error occurs
		response.setStatus(HttpServletResponse.SC_FOUND);
		
		
		String toAddress = request.getParameter("toAddress");
		String fromAddress = "zeppa-cloud-1821@appspot.gserviceaccount.com";
		String subject = request.getParameter("subject");
		String body = request.getParameter("body");
		
//		response.getWriter().println(toAddress + "\n}");
		
		if (Utils.isWebSafe(toAddress) && Utils.isWebSafe(fromAddress) &&
				Utils.isWebSafe(subject) && Utils.isWebSafe(body)) {
			
		      // Assuming you are sending email from localhost
		      String host = "localhost";

		      // Get system properties
		      Properties properties = System.getProperties();

		      // Setup mail server
		      properties.setProperty("mail.smtp.host", host);
		      
			Session session = Session.getDefaultInstance(properties, null);

			try {
//				response.getWriter().println("In try Block \n}");
				
			    Message msg = new MimeMessage(session);
			    msg.setFrom(new InternetAddress(fromAddress));
			    msg.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));
			    msg.setSubject(subject);
			    msg.setText(body);
			    
			    Transport.send(msg);

			} catch (AddressException e) {
			    // ...
				response.getWriter().println(e.toString() + "\n}");
				response.getWriter().println("Stack Trace:<br/>");
				e.printStackTrace(response.getWriter());
				response.getWriter().println("<br/><br/>Stack Trace (for web display):</br>");
				response.getWriter().println(displayErrorForWeb(e));
			} catch (MessagingException e) {
			    // ...
				response.getWriter().println(e.toString() + "\n}");
				response.getWriter().println("Stack Trace:<br/>");
				e.printStackTrace(response.getWriter());
				response.getWriter().println("<br/><br/>Stack Trace (for web display):</br>");
				response.getWriter().println(displayErrorForWeb(e));
			}
		}
	}
	
	public String displayErrorForWeb(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		String stackTrace = sw.toString();
		return stackTrace.replace(System.getProperty("line.separator"), "<br/>\n");
	}

}
