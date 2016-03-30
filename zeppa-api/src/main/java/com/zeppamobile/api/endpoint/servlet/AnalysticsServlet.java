package com.zeppamobile.api.endpoint.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zeppamobile.common.cerealwrapper.FilterCerealWrapper;

/**
 * 
 * @author Pete Schuette
 * 
 * Servlet class for interacting with analytics
 *
 */
public class AnalysticsServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		
	}
	
	
	private String getAllEventInfoDemographic(FilterCerealWrapper filter, long vendorId){
		return "";
	}
	
	private String getAllEventTags(FilterCerealWrapper filter, long vendorId){
		return "";
	}
	
	private String getAllEventPopularDay(FilterCerealWrapper filter, long vendorId) {
		return "";
	}
	
	private String getAllEventPopularEvents(FilterCerealWrapper filter, long vendorId) {
		return "";
	}
	
	private String getIndividualEventInfoTags(FilterCerealWrapper filter, long vendorId) {
		return "";
	}
	
	private String getIndividualEventInfoDemographics(FilterCerealWrapper filter, long vendorId) {
		return "";
	}

}
