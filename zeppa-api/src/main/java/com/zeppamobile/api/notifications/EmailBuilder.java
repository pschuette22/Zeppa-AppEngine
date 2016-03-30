package com.zeppamobile.api.notifications;

import java.util.List;
import java.util.logging.Logger;

import com.zeppamobile.api.datamodel.Employee;
import com.zeppamobile.api.datamodel.Vendor;
import com.zeppamobile.api.datamodel.ZeppaNotification;

public class EmailBuilder {

	private static Logger log = Logger.getLogger(NotificationBuilder.class
			.getName());
	
	public static List<ZeppaNotification> buildVerificationEmail(Vendor vendor) {
		
		// PLACEHOLDER for documentation
		return null;
	}
	
	public static List<ZeppaNotification> buildLostPasswordEmail(Employee employee) {
		
		// PLACEHOLDER for documentation
		return null;
	}
	
	public static List<ZeppaNotification> buildBillingEmail(Vendor vendor, long startCycle, long endCycle) {
		
		// PLACEHOLDER for documentation
		return null;
	}
	
	
}
