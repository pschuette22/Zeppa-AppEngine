package com.zeppamobile.smartfollow.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Simple Servlet to compare the similarity of two tags
 * 
 * @author Pete Schuette
 *
 */
public class CompareTagsServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	
	
	/**
	 * Get request made to servlet to compare two tags
	 * 
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

//		// Set as Internal Error at first then change
//
//		String tagText1 = req.getParameter("tagText1");
//		String tagText2 = req.getParameter("tagText2");
//
//		/*
//		 * Make sure that we can calculate use this text
//		 */
//		if (Utils.isWebSafe(tagText1) && Utils.isWebSafe(tagText2)) {
//			// Initialize the Info Classes for computing
//			EventTagCerealWrapper tagInfo1 = new EventTagCerealWrapper();
//			tagInfo1.setTagText(tagText1);
//
//			EventTagCerealWrapper tagInfo2 = new EventTagCerealWrapper();
//			tagInfo2.setTagText(tagText2);
//
//			// Create report
//			TagComparisonReport report = new TagComparisonReport(tagText1,
//					tagText2, resp.getWriter());
//			
//			report.log("Initializing agents for calculations");
//			
//			TagAgent agent1 = new TagAgent(getServletContext(), tagInfo1,
//					report);
//			
//			TagAgent agent2 = new TagAgent(getServletContext(), tagInfo2,
//					report);
//
//			/*
//			 * Calculate the similarity of the two
//			 */
//			double calculatedSimilarity = agent1.calculateSimilarity(agent2);
//			report.setSimilarity(calculatedSimilarity);
//
//			if (calculatedSimilarity >= 0) {
//
//				report.log("Calculated similarity between "
//						+ agent1.getTagText() + " and " + agent2.getTagText()
//						+ " - " + calculatedSimilarity);
//			} else {
//				report.log("Couldn't calculate similarity");
//			}
//			resp.setStatus(HttpServletResponse.SC_OK);
//
//		} else {
//			// didn't supply sufficient arguments
//			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//		}

	}

}
