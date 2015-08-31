package com.zeppamobile.common.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Dictionary;
import java.util.Enumeration;

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
	public static URL getZeppaAPIUrl(String methodName,
			Dictionary<String, String> params) throws MalformedURLException {

		 String paramString= null;

		 /*
		  * Append arguments for GET request
		  */
		if (params != null && !params.isEmpty()) {
			StringBuilder paramsBuilder = new StringBuilder();
			paramsBuilder.append("?");
			Enumeration<String> keySet = params.keys();
			while(keySet.hasMoreElements()) {
				String key = keySet.nextElement();
				String param = params.get(key);
				if(paramsBuilder.length()>0){
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
				+ modulesApi.getVersionHostname("zeppa-api", "v1") + "/"
				+ methodName + (paramString==null?"":paramString));
		

		return url;
	}

}
