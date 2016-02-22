package com.zeppamobile.api.endpoint.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zeppamobile.common.datainfo.FilterInfo;

/**
 * 
 * @author Pete Schuette
 * 
 * Servlet class for interacting with analytics
 *
 */
public class AnalyticsServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		
	}
	
	
	private String getAllEventInfoDemographic(FilterInfo filter, long vendorId){
		return "";
	}
	
	private String getAllEventTags(FilterInfo filter, long vendorId){
		return "";
	}
	
	private String getAllEventPopularDay(FilterInfo filter, long vendorId) {
		return "";
	}
	
	private String getAllEventPopularEvents(FilterInfo filter, long vendorId) {
		return "";
	}
	
	private String getIndividualEventInfoTags(FilterInfo filter, long vendorId) {
		return "";
	}
	
	private String getIndividualEventInfoDemographics(FilterInfo filter, long vendorId) {
		return "";
	}

}
