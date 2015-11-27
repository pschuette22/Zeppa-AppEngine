package com.zeppamobile.api.endpoint.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

		String method = req.getParameter(UniversalConstants.PARAM_METHOD);

		// If method is to insert an event, consume here
		if (method.equals("insert")) {

			// Fetch the current ID token
			String idToken = req
					.getParameter(UniversalConstants.PARAM_ID_TOKEN);

			try {
				// Get the authroized user from the passed id token
				ZeppaUser user = ClientEndpointUtility
						.getAuthorizedZeppaUser(idToken);

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
				 * Initialize a Dummy event starting now, ending in an hour with the passed title
				 * 
				 */
				ZeppaEvent event = new ZeppaEvent(System.currentTimeMillis(),
						System.currentTimeMillis(), user.getZeppaCalendarId(),
						"not-set", "not-set", EventPrivacyType.CASUAL,
						user.getId(), eventName, "Event made from the server",
						Boolean.TRUE, System.currentTimeMillis(),
						(System.currentTimeMillis() + 1000 * 60 * 60),
						"Some Fun Locatio", null, tagIds, null);
				
				// Initialize the endpoint and execute the insert
				ZeppaEventEndpoint endpoint = new ZeppaEventEndpoint();
				endpoint.insertZeppaEvent(event, idToken);

			} catch (UnauthorizedException | NullPointerException e) {
				resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			} catch (Exception e) {
				resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}

		}

	}

}
