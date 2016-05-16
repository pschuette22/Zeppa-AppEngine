package com.zeppamobile.api.googlecalendar;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Acl;
import com.google.api.services.calendar.model.AclRule;
import com.google.api.services.calendar.model.AclRule.Scope;
import com.google.appengine.api.utils.SystemProperty;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.FreeBusyRequest;
import com.google.api.services.calendar.model.FreeBusyRequestItem;
import com.google.api.services.calendar.model.FreeBusyResponse;
import com.zeppamobile.api.AppConfig;
import com.zeppamobile.api.datamodel.ZeppaEvent;
import com.zeppamobile.api.datamodel.ZeppaUser;

public class GoogleCalendarService {

	/**
	 * Private Contructor
	 */
	private GoogleCalendarService() {
	}

	/**
	 * This method creates a Zeppa Calendar in a given users' account
	 * 
	 * @param zeppaUser
	 * @param user
	 * @return updated zeppaUser with calendar Id
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public static ZeppaUser insertZeppaCalendar(ZeppaUser zeppaUser) throws IOException {

		if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Development) {
			zeppaUser.setZeppaCalendarId("local-calendar-id");
		} else if (AppConfig.isTest()) {
			// If this is a test, simulate that the calendar was inserted
			zeppaUser.setZeppaCalendarId("test-calendar-id");
		} else {
			// else initialize the Google Calendar service and make the calendar
			com.google.api.services.calendar.Calendar service = GoogleCalendarUtils.makeCalendarServiceInstance();

			// TODO: check to see if there is already a Zeppa Calendar for this
			// user

			// Create the calendar
			Calendar calendar = new Calendar();
			calendar.setSummary("Zeppa");
			calendar.setDescription("Calendar holding activities made on Zeppa");
			calendar = service.calendars().insert(calendar).execute();

			// Give user reader access to this calendar
			AclRule userRule = new AclRule();
			Scope userScope = new AclRule.Scope();
			userScope.setType("user");
			userScope.setValue(zeppaUser.getAuthEmail());
			userRule.setRole("reader");
			userRule.setScope(userScope);
			userRule = service.acl().insert(calendar.getId(), userRule).execute();

			zeppaUser.setZeppaCalendarId(calendar.getId());
		}
		return zeppaUser;
	}

	/**
	 * This Method inserts the Event into user's Google Calendar
	 * 
	 * @param zeppaUser
	 * @param event
	 * @param user
	 * @return updated event with event + calendar ID
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public static ZeppaEvent insertGCalEvent(ZeppaUser zeppaUser, ZeppaEvent event) throws IOException {

		if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Development) {
			zeppaUser.setZeppaCalendarId("local-event-id");
		} else if (AppConfig.isTest()) {
			// If this is a test, set the test ids
			event.setGoogleCalendarId(zeppaUser.getZeppaCalendarId());
			event.setGoogleCalendarEventId("test-calendar-event-id");
		} else {
			// If it is not, insert the event into the calendar
			try {
				com.google.api.services.calendar.Calendar service = GoogleCalendarUtils.makeCalendarServiceInstance();

				Event calEvent = new Event();

				// Basic Calendar Event Info
				calEvent.setSummary(event.getTitle());
				calEvent.setDescription(event.getDescription());
				calEvent.setLocation(
						event.getMapsLocation() != null ? event.getMapsLocation() : event.getDisplayLocation());
				calEvent.setOrganizer(GoogleCalendarUtils.getServiceAsOrganizer());

				// Event Times
				EventDateTime start = new EventDateTime();
				start.setDateTime(new DateTime(event.getStart()));
				calEvent.setStart(start);
				EventDateTime end = new EventDateTime();
				end.setDateTime(new DateTime(event.getEnd()));
				calEvent.setEnd(end);

				// Let People Add Self
				calEvent.setAnyoneCanAddSelf(true);
				calEvent.setGuestsCanInviteOthers(event.getGuestsMayInvite());
				calEvent.setGuestsCanModify(false);
				calEvent.setLocked(false);
				calEvent.setGuestsCanSeeOtherGuests(true);

				// Insert and update object
				calEvent = service.events().insert(zeppaUser.getZeppaCalendarId(), calEvent).execute();

				event.setGoogleCalendarId(zeppaUser.getZeppaCalendarId());
				event.setGoogleCalendarEventId(calEvent.getId());

			} catch (IOException e) {
				throw wrappedIOException(e);
			}
		}

		return event;
	}

	/**
	 * This method adds a copy of the event to the User's Zeppa Calendar
	 * 
	 * @param zeppaEvent
	 *            , ZeppaEvent user is joining
	 * @param zeppaUser
	 *            , ZeppaUser Object of calling user
	 * @param user
	 *            , User verification object
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	public static void joinEvent(ZeppaEvent zeppaEvent, ZeppaUser zeppaUser) throws IOException {
		if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Development) {
			return;
		}
		com.google.api.services.calendar.Calendar service = GoogleCalendarUtils.makeCalendarServiceInstance();

		Event calEvent = service.events().get(zeppaEvent.getGoogleCalendarId(), zeppaEvent.getGoogleCalendarEventId())
				.execute();

		List<EventAttendee> attendees = calEvent.getAttendees();
		EventAttendee attendee = null;
		if (attendees == null) {
			attendees = new ArrayList<EventAttendee>();
		} else {
			Iterator<EventAttendee> iterator = calEvent.getAttendees().iterator();

			while (iterator.hasNext()) {
				EventAttendee temp = iterator.next();
				if (temp.getEmail().equals(zeppaUser.getAuthEmail())) {
					attendee = temp;
					break;
				}
			}

		}
		if (attendee == null) {
			attendee = GoogleCalendarUtils.getUserAsAttendee(zeppaUser);
		}

		attendee.setResponseStatus("accepted");
		attendees.add(attendee);
		calEvent.setAttendees(attendees);

		calEvent = service.events()
				.update(zeppaEvent.getGoogleCalendarId(), zeppaEvent.getGoogleCalendarEventId(), calEvent).execute();

	}

	/**
	 * This method removes the private copy of the Calendar Event
	 * 
	 * @param event
	 * @param zeppaUser
	 * @param user
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	public static boolean leaveEvent(ZeppaEvent event, ZeppaUser zeppaUser) throws IOException {

		if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Development) {
			return true;
		}
		
		boolean success = false;
		com.google.api.services.calendar.Calendar service = GoogleCalendarUtils.makeCalendarServiceInstance();

		Event calEvent = service.events().get(event.getGoogleCalendarId(), event.getGoogleCalendarEventId()).execute();
		Iterator<EventAttendee> iterator = calEvent.getAttendees().iterator();

		while (iterator.hasNext()) {
			EventAttendee attendee = iterator.next();
			if (attendee.getEmail().equals(zeppaUser.getAuthEmail())) {
				success = calEvent.getAttendees().remove(attendee);
				break;
			}
		}

		if (success) {
			service.events().update(event.getGoogleCalendarId(), event.getGoogleCalendarEventId(), calEvent).execute();
		}

		return success;
	}

	/**
	 * Delete the GoogleCalendar for this user
	 * 
	 * @param zeppaUser
	 *            - ZeppaUser who's calendar should be deleted
	 * @throws IOException
	 */
	public static void deleteCalendar(ZeppaUser zeppaUser) throws IOException {

		if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Development) {
			return;
		} 
		
		com.google.api.services.calendar.Calendar service = GoogleCalendarUtils.makeCalendarServiceInstance();

		// Delete all associated rules so calendar removes cleanly
		Acl acl = service.acl().list(zeppaUser.getZeppaCalendarId()).execute();
		if (acl != null && !acl.getItems().isEmpty()) {
			for (AclRule rule : acl.getItems()) {
				service.acl().delete(zeppaUser.getZeppaCalendarId(), rule.getId()).execute();
			}
		}

		service.calendars().delete(zeppaUser.getZeppaCalendarId()).execute();
	}

	/**
	 * Remove Google Calendar event corresponding to ZeppaEvent
	 * 
	 * @param event
	 *            - ZeppaEvent corresponding to Google Calendar Event to be
	 *            removed
	 * @throws IOException
	 */
	public static void deleteCalendarEvent(ZeppaEvent event) throws IOException {
		if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Development) {
			return;
		} 
		

		com.google.api.services.calendar.Calendar service = GoogleCalendarUtils.makeCalendarServiceInstance();

		service.events().delete(event.getGoogleCalendarId(), event.getGoogleCalendarEventId()).execute();
	}

	/**
	 * Get a list of percentages of users who are free during a given time by
	 * time interval
	 * 
	 * @param startTime
	 *            - start time in question
	 * @param endTime
	 *            - end time in question
	 * @param userEmails
	 *            - list of emails you desire to retrieve free/busy data for
	 * @return ordered list of percentages of users for the given list who are
	 *         free
	 * @throws IOException
	 */
	public static FreeBusyResponse makeFreeBusyRequest(long startTimeMillis, long endTimeMillis,
			List<String> userEmails) throws IOException {

		if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Development) {
			return null;
		}
		
		// Build the calendar service
		com.google.api.services.calendar.Calendar service = GoogleCalendarUtils.makeCalendarServiceInstance();

		FreeBusyRequest request = new FreeBusyRequest();
		request.setTimeMin(new DateTime(startTimeMillis));
		request.setTimeMax(new DateTime(endTimeMillis));

		// Build request items
		List<FreeBusyRequestItem> requestItems = new ArrayList<FreeBusyRequestItem>();
		for (String email : userEmails) {
			FreeBusyRequestItem item = new FreeBusyRequestItem();
			item.setId(email);
			requestItems.add(item);
		}
		request.setItems(requestItems);

		return service.freebusy().query(request).execute();
	}

	/**
	 * Returns an {@link IOException} (but not a subclass) in order to work
	 * around restrictive GWT serialization policy.
	 */
	public static IOException wrappedIOException(IOException e) {
		if (e.getClass() == IOException.class) {
			return e;
		}
		return new IOException(e.getMessage());
	}

}
