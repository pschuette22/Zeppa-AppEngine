package com.zeppamobile.smartfollow;

import java.io.FileInputStream;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.StorageScopes;
import com.google.appengine.api.utils.SystemProperty;

/**
 * Storage class to get the storage service and handle local storage
 * @author Eric
 *
 */
public class StorageUtils {
	private static final String APPLICATION_NAME = "zeppa-cloud-1821";
	//Local
//	private static String CREDENTIALS_PATH = "src/main/webapp/WEB-INF/config/serviceAccountCredentials.json";
	//AppEngine
	private static String CREDENTIALS_PATH = "WEB-INF/config/serviceAccountCredentials.json";

	private static StorageUtils instance;
	private static Storage storageService;
	/** Global instance of the JSON factory. */
//	private static final JsonFactory JSON_FACTORY = JacksonFactory
//			.getDefaultInstance();

	private StorageUtils() {
		storageService = getService();
		
		// since we will be downloading semsigs locally, go ahead and
		// register a shutdown hook to clean them up on exit
		// since this is a singleton only one hook is ever registered
		Runtime.getRuntime().addShutdownHook(new OnShutdown());
	}
	
	public static void setCredentialsPath(String path) {
		CREDENTIALS_PATH = path;
	}
	
	public String getCredentialsPath() {
		return CREDENTIALS_PATH;

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
//					JSONParser parser = new JSONParser();
//					JSONObject jsonCredentials = (JSONObject) parser
//							.parse(new FileReader(CREDENTIALS_PATH));
//					String jsonFile = jsonCredentials.toString();
//					credential = GoogleCredential
//							.fromStream(new ByteArrayInputStream(jsonFile
//									.getBytes()));
					System.out.println("Attempting to read credential file input stream");
					credential = GoogleCredential.fromStream(new FileInputStream(CREDENTIALS_PATH));
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
				System.out.println("Checking scope stuff");
				if (credential.createScopedRequired()) {
					credential = credential.createScoped(StorageScopes.all());
				}
				System.out.println("Building http transport");
				HttpTransport httpTransport = GoogleNetHttpTransport
						.newTrustedTransport();
				System.out.println("Init storage service");
				
				JsonFactory jsonFactory = new JacksonFactory();
				
				storageService = new Storage.Builder(httpTransport,
						jsonFactory, credential).setApplicationName(
						APPLICATION_NAME).build();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return storageService;
	}
}
