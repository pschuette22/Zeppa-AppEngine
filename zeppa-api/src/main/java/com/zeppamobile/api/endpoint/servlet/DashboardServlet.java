package com.zeppamobile.api.endpoint.servlet;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;

import com.zeppamobile.api.datamodel.VendorEvent;
import com.zeppamobile.common.UniversalConstants;
import com.zeppamobile.common.cerealwrapper.VendorEventWrapper;

/**
 * Servlet implementation class DashboardServlet
 */
public class DashboardServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String jsonString = "";
		try {
		// Get the parameters from the request
		String vendorId = request.getParameter(UniversalConstants.PARAM_VENDOR_ID);
		String upcomingEvents = request.getParameter(UniversalConstants.PARAM_UPCOMING_EVENTS);
		String pastEvents = request.getParameter(UniversalConstants.PARAM_PAST_EVENTS);

		JSONArray results = new JSONArray();
		if(upcomingEvents != null && vendorId != null && !vendorId.isEmpty()){ // for dashboard
			vendorId = URLDecoder.decode(vendorId,"UTF-8");
			results = getUpcomingEventsJSON(getUpcomingEvents(Long.valueOf(vendorId)));
			jsonString = "{\"events\":" + results.toJSONString() + "}";
		} else if(pastEvents != null && vendorId != null && !vendorId.isEmpty()){ // for dashboard
			vendorId = URLDecoder.decode(vendorId,"UTF-8");
			results = getPastEventsJSON(getPastEvents(Long.valueOf(vendorId)));
			jsonString = "{\"events\":" + results.toJSONString() + "}";
		}
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		//send Json back
		response.getWriter().write(jsonString);
	
		} catch(Exception e) {
			// TODO: auto
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Get the next 5 events scheduled for the given vendor (if they exist)
	 * This is used for the dashboard view
	 * @param vendorId - the vendor to get events for
	 * @return - list of the next 5 events for the given vendor
	 */
	public static List<VendorEventWrapper> getUpcomingEvents(Long vendorId) {
		List<VendorEventWrapper> upcomingEvents = new ArrayList<VendorEventWrapper>();
		List<VendorEvent> allEvents = VendorEventServlet.getAllEvents(vendorId);
		// Iterate over all events for the vendor
		for(VendorEvent event : allEvents) {
			// If the event hasn't happened add it to the current list
			if(event.getStart() >= System.currentTimeMillis()) {
				VendorEventWrapper wrap = new VendorEventWrapper(event.getId(), event.getCreated(), event.getUpdated(), 
							event.getHostId(), event.getTitle(), event.getDescription(), event.getStart(), event.getEnd(), 
							event.getTagIds(), event.getDisplayLocation(), event.getMapsLocation());
				// Set the joined count to be shown on the dashboard
				wrap.setJoinedCount(VendorEventRelationshipServlet.getAllJoinedRelationshipsForEvent(event.getId(), null).size());
				upcomingEvents.add(wrap);
			}
		}
		
		// If there are 5 or fewer then return all
		if(upcomingEvents.size() <= 5)
			return upcomingEvents;
		
		List<VendorEventWrapper> returnList = new ArrayList<VendorEventWrapper>();
		// If there are more than 5 return the next 5 most current
		for(int i=0; i < 5; i++) {
			VendorEventWrapper mostCurrent = null;
			for(VendorEventWrapper event : upcomingEvents) {
				if(mostCurrent == null || event.getStart() < mostCurrent.getStart()) {
					mostCurrent = event;
				}
			}
			// Add the most current to the return list
			returnList.add(mostCurrent);
			// Remove the most current so the next most current can be found
			upcomingEvents.remove(mostCurrent);
		}
		
		return returnList;
	}

	/**
	 * Add all the current events to a JSON array
	 * @param events - the current events
	 * @return - a json array with all of the current events
	 */
	@SuppressWarnings("unchecked")
	private JSONArray getUpcomingEventsJSON(List<VendorEventWrapper> events) {
		JSONArray array = new JSONArray();
		for(VendorEventWrapper wrap : events) {
			array.add(wrap.toJson());
		}
		
		return array;
	}
	
	/**
	 * Get the next 5 events scheduled for the given vendor (if they exist)
	 * This is used for the dashboard view
	 * @param vendorId - the vendor to get events for
	 * @return - list of the next 5 events for the given vendor
	 */
	public static List<VendorEventWrapper> getPastEvents(Long vendorId) {
		List<VendorEventWrapper> pastEvents = new ArrayList<VendorEventWrapper>();
		List<VendorEvent> allEvents = VendorEventServlet.getAllEvents(vendorId);
		// Iterate over all events for the vendor
		for(VendorEvent event : allEvents) {
			// If the event has happened add it to the past list
			if(event.getStart() < System.currentTimeMillis()) {
				VendorEventWrapper wrap = new VendorEventWrapper(event.getId(), event.getCreated(), event.getUpdated(), 
							event.getHostId(), event.getTitle(), event.getDescription(), event.getStart(), event.getEnd(), 
							event.getTagIds(), event.getDisplayLocation(), event.getMapsLocation());
				// Set the joined count to be shown on the dashboard
				wrap.setJoinedCount(VendorEventRelationshipServlet.getAllJoinedRelationshipsForEvent(event.getId(), null).size());
				pastEvents.add(wrap);
				System.out.println("------Wrap :" + event.getTitle() +" START: "+event.getStart()+" CURRENT: "+System.currentTimeMillis());
			}
		}
		
		// If there are 5 or fewer then return all
		if(pastEvents.size() <= 5)
			return pastEvents;
		
		List<VendorEventWrapper> returnList = new ArrayList<VendorEventWrapper>();
		// If there are more than 5 return the next 5 most current
		for(int i=0; i < 5; i++) {
			VendorEventWrapper mostCurrent = null;
			for(VendorEventWrapper event : pastEvents) {
				if(mostCurrent == null || event.getStart() > mostCurrent.getStart()) {
					mostCurrent = event;
				}
			}
			// Add the most current to the return list
			returnList.add(mostCurrent);
			// Remove the most current so the next most current can be found
			pastEvents.remove(mostCurrent);
		}
		
		return returnList;
	}
	
	/**
	 * Add the 5 most recent past events to a JSON array
	 * @param events - the past events events
	 * @return - a json array with all of the current events
	 */
	@SuppressWarnings("unchecked")
	private JSONArray getPastEventsJSON(List<VendorEventWrapper> events) {
		JSONArray array = new JSONArray();
		for(VendorEventWrapper wrap : events) {
			array.add(wrap.toJson());
		}
		
		return array;
	}
}
