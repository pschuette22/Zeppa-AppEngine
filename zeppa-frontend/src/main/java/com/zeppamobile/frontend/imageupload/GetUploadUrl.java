package com.zeppamobile.frontend.imageupload;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

public class GetUploadUrl extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6249114039238286102L;
	BlobstoreService service = BlobstoreServiceFactory.getBlobstoreService();
	
//	private static final Logger LOG = Logger.getLogger(GetUploadUrl.class
//			.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
				
		String uploadUrl = service.createUploadUrl("/image/upload/");
		resp.setStatus(HttpStatus.SC_OK);
		resp.setContentType("text/plain");
		
		PrintWriter writer = resp.getWriter();
		writer.print(uploadUrl);
		writer.flush();
		writer.close();
	
	}
	
}

