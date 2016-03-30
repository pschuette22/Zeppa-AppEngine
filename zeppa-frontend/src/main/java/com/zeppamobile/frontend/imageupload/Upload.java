package com.zeppamobile.frontend.imageupload;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;

public class Upload extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8314517074841790776L;
	private BlobstoreService blobstoreService = BlobstoreServiceFactory
			.getBlobstoreService();

	// private static final Logger LOG =
	// Logger.getLogger(Upload.class.getName());

	@SuppressWarnings("unchecked")
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		List<BlobKey> blobKeys = blobstoreService.getUploads(req).get("data");
		BlobKey key = blobKeys.get(0);
		ImagesService imagesService = ImagesServiceFactory.getImagesService();
		ServingUrlOptions servingOptions = ServingUrlOptions.Builder
				.withBlobKey(key);

		String servingUrl = imagesService.getServingUrl(servingOptions);
		resp.setStatus(HttpServletResponse.SC_OK);

		resp.setContentType("application/json");
		JSONObject json = new JSONObject();

		json.put("servingUrl", servingUrl);
		json.put("blobKey", key.getKeyString());

		PrintWriter out = resp.getWriter();
		out.print(json.toString());
		out.flush();
		out.close();

	}

}
