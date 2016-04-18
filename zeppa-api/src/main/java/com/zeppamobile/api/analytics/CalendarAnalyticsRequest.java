package com.zeppamobile.api.analytics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.api.services.calendar.model.FreeBusyCalendar;
import com.google.api.services.calendar.model.FreeBusyGroup;
import com.google.api.services.calendar.model.FreeBusyResponse;
import com.google.api.services.calendar.model.TimePeriod;
import com.zeppamobile.api.datamodel.ZeppaUser;
import com.zeppamobile.api.googlecalendar.GoogleCalendarService;

/**
 * Request object used to check for Google calendar availability
 * 
 * @author PSchuette
 *
 */
public class CalendarAnalyticsRequest extends AnalyticsRequest {

	/*
	 * Request fields
	 */

	/** start time of event */
	private Calendar startTime;

	/** end time of event */
	private Calendar endTime;

	// Default interval set to 30min
	private static final long intervalInMillis = 30 * 60 * 1000;

	/*
	 * Response fields
	 */

	/**
	 * Construct calendar analytics request
	 * 
	 * @param filter
	 */
	protected CalendarAnalyticsRequest(Calendar startTime, Calendar endTime, DemographicsFilter filter) {
		super(filter);
		this.startTime = startTime;
		this.endTime = endTime;
	}

	@Override
	public void execute() {
		// Grab a list of all gmail addresses of users matching this filter for
		// free-busy calculations
		List<String> emails = new ArrayList<String>();

		// Persistence manager for DB calls
		PersistenceManager mgr = getPersistenceManager();

		try {

			Query q = mgr.newQuery(ZeppaUser.class);
			q.declareImports("import java.util.List;");
			q.declareParameters("List userKeys");
			q.setFilter("userKeys.contains(key.geId())");
			@SuppressWarnings("unchecked")
			List<ZeppaUser> users = (List<ZeppaUser>) q.execute(this.filter.getUserIds());

			for (ZeppaUser user : users) {
				// TODO: decrypt email for calculations
				emails.add(user.getAuthEmail());
			}

		} finally {
			mgr.close();
		}

		// Make free-busy request for users
		try {
			FreeBusyResponse response = GoogleCalendarService.makeFreeBusyRequest(startTime.getTimeInMillis(),
					endTime.getTimeInMillis(), emails);
			// Maintain a map of calendars
			Map<String, FreeBusyCalendar> responseCalendarMap = response.getCalendars();

			// Iterate through the response groups
			Map<String, FreeBusyGroup> responseGroupMap = response.getGroups();
			for (Entry<String, FreeBusyGroup> entry : responseGroupMap.entrySet()) {

				String id = entry.getKey();
				FreeBusyGroup group = entry.getValue();

				if (group.isEmpty()) {
					// Perhaps this means they didn't want to disclose free-busy
					// data

				} else {

					// Build an array of true/false for this users availability
					// TODO: verify safe cast or add event time limits
					int intervals = (int) ((endTime.getTimeInMillis() - startTime.getTimeInMillis()) / intervalInMillis)
							+ 1;

					// Construct array initialized to all true (available)
					boolean[] freeBusyData = new boolean[intervals];
					for (int i = 0; i < intervals; i++) {
						freeBusyData[i] = true;
					}

					// Iterate through calendars for this user
					for (String calendarId : group.getCalendars()) {
						FreeBusyCalendar freeBusyCalendar = responseCalendarMap.get(calendarId);
						if (freeBusyCalendar != null) {
							// Quick null check just in case

							// Iterate through busy times
							for (TimePeriod busyTime : freeBusyCalendar.getBusy()) {

								long startBusyTimeMillis = busyTime.getStart().getValue();
								
							}

						}
					}

				}

			}

		} catch (IOException e) {
			e.printStackTrace();
			// TODO: notify there was an error, determine if recoverable
		}

		// Finalize the request
		finalize();
	}

}
