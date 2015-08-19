package com.zeppamobile.api.endpoint.Utils;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

import com.google.api.client.googleapis.extensions.appengine.auth.oauth2.AppIdentityCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event.Creator;
import com.google.api.services.calendar.model.Event.Organizer;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.appengine.api.users.User;
import com.zeppamobile.api.datamodel.ZeppaUser;
import com.zeppamobile.api.datamodel.ZeppaUserInfo;

public class GoogleCalendarUtils {

	private static final JsonFactory FACTORY = new JacksonFactory();
	private static final HttpTransport TRANSPORT = new NetHttpTransport();
	private static final List<String> SCOPES = Arrays.asList(CalendarScopes.CALENDAR);

	public static final String SERVICE_ACCOUNT_DISPLAY_NAME = "Zeppa Service Account";
	/**
	 * 
	 * @return Calendar service object
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	public static Calendar makeCalendarServiceInstance() {

		AppIdentityCredential credential = makeServiceAccountCredential();

		Calendar service = new Calendar.Builder(TRANSPORT, FACTORY, credential).setApplicationName("Zeppa").build();

		return service;
	}

	/**
	 * Generates a new Google Credential for the service account
	 * 
	 * @param httpTransport
	 * @param jsonFactory
	 * @return
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	private static AppIdentityCredential makeServiceAccountCredential() {

		AppIdentityCredential credential = new AppIdentityCredential(SCOPES);
		return credential;
	}

	public static String getServiceAccountEmail() {

		return makeServiceAccountCredential().getAppIdentityService().getServiceAccountName();
	}

	public static Creator getServiceAsCreator() {

		Creator creator = new Creator();
		creator.setDisplayName(SERVICE_ACCOUNT_DISPLAY_NAME);
		creator.setEmail(getServiceAccountEmail());

		return creator;
	}

	public static Organizer getServiceAsOrganizer() {
		Organizer organizer = new Organizer();
		organizer.setDisplayName(SERVICE_ACCOUNT_DISPLAY_NAME);
		organizer.setEmail(getServiceAccountEmail());

		return organizer;
	}

	/**
	 * 
	 * @param userInfo
	 * @param user
	 * @return EventAttendee as created user
	 */
	public static EventAttendee getUserAsAttendee(ZeppaUser zeppaUser) {

		EventAttendee attendee = new EventAttendee();
		attendee.setEmail(zeppaUser.getAuthEmail());

		ZeppaUserInfo userInfo = zeppaUser.getUserInfo();
		attendee.setDisplayName(userInfo.getGivenName() + " " + userInfo.getFamilyName());

		return attendee;
	}

	/**
	 * Get Calling user as an Organizer object
	 * 
	 * @param zeppaUser
	 * @param user
	 * @return
	 */
	public static Organizer getUserAsOrganizer(ZeppaUser zeppaUser, User user) {
		Organizer organizer = new Organizer();
		ZeppaUserInfo info = zeppaUser.getUserInfo();
		organizer.setDisplayName(info.getGivenName() + " " + info.getFamilyName());
		organizer.setEmail(user.getEmail());

		return organizer;

	}

}
