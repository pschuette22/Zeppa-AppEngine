package com.minook.zeppa.backend.notifications;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.ThreadManager;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

public class NotificationCleanupServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger
			.getLogger(NotificationCleanupServlet.class.getCanonicalName());
	private static final int HOURS_TO_KEEP_RECORDS_OF_PROCESSED_NOTIFICATIONS = 12;
	private static ClassLoader cl;

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res)
			throws IOException {
		
		cl = getClass().getClassLoader();

		Thread thread = ThreadManager.createBackgroundThread(new Runnable() {
			@Override
			public void run() {
				Thread.currentThread().setContextClassLoader(cl);

				doCleanup();
			}
		});

		thread.start();
	}

	private void doCleanup() {
		log.log(Level.INFO,
				"Starting a job to clean up processed notification records");

		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();

		Calendar cutoffTime = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		cutoffTime.add(Calendar.HOUR,
				-HOURS_TO_KEEP_RECORDS_OF_PROCESSED_NOTIFICATIONS);

		Query query = new Query(
				NotificationWorker.PROCESSED_NOTIFICATION_TASKS_ENTITY_KIND)
				.setFilter(
						new FilterPredicate("processedAt",
								FilterOperator.LESS_THAN, cutoffTime.getTime()))
				.setKeysOnly();

		PreparedQuery preparedQuery = datastore.prepare(query);

		// Delete in batches
		List<Entity> entitiesToBeDeleted = null;
		do {
			entitiesToBeDeleted = preparedQuery.asList(FetchOptions.Builder
					.withLimit(5));

			List<Key> keys = new ArrayList<Key>();

			for (Entity entity : entitiesToBeDeleted) {
				keys.add(entity.getKey());
			}

			datastore.delete(keys);
		} while (entitiesToBeDeleted.size() > 0);

		log.log(Level.INFO,
				"Finished a job to clean up processed notification records");
	}

}
