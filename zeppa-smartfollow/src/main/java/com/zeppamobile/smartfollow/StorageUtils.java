package com.zeppamobile.smartfollow;

import java.io.ByteArrayInputStream;
import java.io.FileReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.StorageScopes;
import com.google.appengine.api.utils.SystemProperty;

public class StorageUtils {
	private static final String APPLICATION_NAME = "zeppamobile";
	private static final String CREDENTIALS_PATH = "src/main/webapp/config/serviceAccountCredentials.json";
	private static StorageUtils instance;
	private static Storage storageService;
	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY = JacksonFactory
			.getDefaultInstance();

	private StorageUtils() {
		storageService = getService();
	}

	public static StorageUtils getInstance() {
		try {
			if (instance == null)
				instance = new StorageUtils();
			return instance;
		} catch (Exception e) {
			throw new RuntimeException("Could not init StorageUtils: "
					+ e.getMessage());
		}
	}

	/**
	 * Returns an authenticated Storage object used to make service calls to
	 * Google Cloud Storage.
	 */
	public Storage getService() {
		try {
			if (null == storageService) {
				GoogleCredential credential = null;
				// If app is is not running on AppEngine use service account key
				if (SystemProperty.environment.value() != SystemProperty.Environment.Value.Production) {
					// Read JSON file
					JSONParser parser = new JSONParser();
					JSONObject jsonCredentials = (JSONObject) parser
							.parse(new FileReader(System
									.getProperty("user.dir") +"/"+ CREDENTIALS_PATH));
					String jsonFile = jsonCredentials.toString();
					credential = GoogleCredential
							.fromStream(new ByteArrayInputStream(jsonFile
									.getBytes()));
				} else {
					credential = GoogleCredential.getApplicationDefault();
				}

				// Depending on the environment that provides the default
				// credentials (e.g. Compute Engine,
				// App Engine), the credentials may require us to specify the
				// scopes
				// we need explicitly.
				// Check for this case, and inject the Cloud Storage scope if
				// required.
				if (credential.createScopedRequired()) {
					credential = credential.createScoped(StorageScopes.all());
				}
				HttpTransport httpTransport = GoogleNetHttpTransport
						.newTrustedTransport();
				storageService = new Storage.Builder(httpTransport,
						JSON_FACTORY, credential).setApplicationName(
						APPLICATION_NAME).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return storageService;
	}
}
