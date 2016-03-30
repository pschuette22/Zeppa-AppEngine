package com.zeppamobile.common.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.appengine.api.modules.ModulesService;
import com.google.appengine.api.modules.ModulesServiceFactory;

public class ModuleUtils {

	/**
	 * @constructor Private constructor as this is a Utility file
	 */
	private ModuleUtils() {
	}

	/**
	 * Make a URL pointing to the zeppa-api module to make http request
	 * 
	 * @param methodName
	 *            for the request
	 * @return URL for request
	 * @throws MalformedURLException
	 */
	public static URL getZeppaModuleUrl(String moduleName, String methodName,
			Map<String, String> params) throws MalformedURLException {

		String paramString = null;

		/*
		 * Append arguments for GET request
		 */
		if (params != null && !params.isEmpty()) {
			StringBuilder paramsBuilder = new StringBuilder();
			paramsBuilder.append("?");
			Set<Entry<String,String>> keySet = params.entrySet();
			Iterator<Entry<String,String>> i = keySet.iterator();
			while (i.hasNext()) {
				Entry<String,String> entry = i.next();
				
				String key = entry.getKey();
				String param = entry.getValue();

				if (paramsBuilder.length() > 1) { //
					paramsBuilder.append("&");
				}
				paramsBuilder.append(key);
				paramsBuilder.append("=");
				paramsBuilder.append(param);
			}

			paramString = paramsBuilder.toString();
		}

		ModulesService modulesApi = ModulesServiceFactory.getModulesService();

		URL url = new URL("http://"
				+ modulesApi.getVersionHostname(moduleName, "v1") + "/"
				+ methodName + (paramString == null ? "" : paramString));

		return url;
	}
	
}
