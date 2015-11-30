package com.zeppamobile.api.endpoint.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import com.google.api.server.spi.response.UnauthorizedException;
import com.zeppamobile.api.datamodel.ZeppaEvent;
import com.zeppamobile.api.datamodel.ZeppaEvent.EventPrivacyType;
import com.zeppamobile.api.datamodel.ZeppaUser;
import com.zeppamobile.api.endpoint.ZeppaEventEndpoint;
import com.zeppamobile.api.endpoint.utils.ClientEndpointUtility;
import com.zeppamobile.common.UniversalConstants;

public class EventAdminServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Make a post request as an admin to the
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		try {
			String method = req.getParameter(UniversalConstants.PARAM_METHOD);

			// Fetch the current ID token
			String idToken = req
					.getParameter(UniversalConstants.PARAM_ID_TOKEN);

			// Get the authorized user from the passed id token
			ZeppaUser user = ClientEndpointUtility
					.getAuthorizedZeppaUser(idToken);

			if (user == null) {
				// If a valid user cannot be retrieved, throw an
				// unauthorized
				throw new UnauthorizedException("Bad or missing client id");
			}

			// If method is to insert an event, consume here
			if (method.equals(UniversalConstants.METHOD_INSERT)) {

				String eventName = req
						.getParameter(UniversalConstants.PARAM_EVENT_NAME);

				/*
				 * public ZeppaEvent(Long created, Long updated, String
				 * googleCalendarId, String googleCalendarEventId, String
				 * iCalUID, EventPrivacyType privacy, Long hostId, String title,
				 * String description, Boolean guestsMayInvite, Long start, Long
				 * end, String displayLocation, String mapsLocation, List<Long>
				 * tagIds, List<Long> invitedUserIds) {
				 */

				List<Long> tagIds = new ArrayList<Long>();
				for (int i = 0; i < 6; i++) {
					try {
						tagIds.add(user.getTags().get(i).getId());
					} catch (IndexOutOfBoundsException e) {
						e.printStackTrace();
					}
				}

				/*
				 * Initialize a Dummy event starting now, ending in an hour with
				 * the passed title
				 */
				ZeppaEvent event = new ZeppaEvent(System.currentTimeMillis(),
						System.currentTimeMillis(), user.getZeppaCalendarId(),
						"not-set", "not-set", EventPrivacyType.CASUAL,
						user.getId(), eventName, "Event made from the server",
						Boolean.TRUE, System.currentTimeMillis(),
						(System.currentTimeMillis() + 1000 * 60 * 60),
						"Some Fun Location", null, tagIds, null);

				// Initialize the endpoint and execute the insert
				ZeppaEventEndpoint endpoint = new ZeppaEventEndpoint();
				event = endpoint.insertZeppaEvent(event, idToken);

				// Convert the object to json and return in the writer
				JSONObject json = event.toJson();
				resp.getWriter().write(json.toJSONString());

			} else if (method.equals(UniversalConstants.METHOD_LIST)) {
				/*
				 * This method is called to return a list of this users events
				 * 
				 */
				
				
			} else {
				resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				resp.getWriter().write("No such method");
			}

		} catch (UnauthorizedException e) {
			// user is not authorized to make this call
			resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		} catch (Exception e) {
			// An uncaught exception occured
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}

	}

}
