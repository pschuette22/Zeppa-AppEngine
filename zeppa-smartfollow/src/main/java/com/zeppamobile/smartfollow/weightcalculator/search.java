package com.zeppamobile.smartfollow.weightcalculator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;


public class search {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		long numResults = getResultsCount("Black","Dog");

		System.out.println(numResults);
	}
	
	/**
	 * Execute a google search and scrape the results count
	 * @param word1 - first word to search
	 * @param word2 - second word to search
	 * @return number of results
	 */
	private static long getResultsCount(final String word1, final String word2) {
		try {
			//attempt to create the URL
			String query = word1 + " " + word2;
			final URL url = new URL("https://www.google.com/search?q=" + URLEncoder.encode(query, "UTF-8"));
			System.out.println(url.toString());
		    final URLConnection connection = url.openConnection();
		    
		    //set connection properties
		    connection.setConnectTimeout(60000);
		    connection.setReadTimeout(60000);
		    connection.addRequestProperty("User-Agent", "Mozilla/5.0");
		    
		    //set up reader
		    BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
		    
		    String line;
		    //grab a line of HTML
		    while((line = input.readLine()) != null) {
		    	//check for stats
		        if(!line.contains("id=\"resultStats\"")) {
		            continue;
		        }
		    
		        //split line into tokens by spaces
		        String[] tokens = line.split(" ");
		        for (int i=1; i<tokens.length-1; i++) {
		        	if (tokens[i-1].contains("About") && tokens[i+1].contains("results")) {
		        		//99.9% we have the right token here
		        		//delete commas
		        		String num = tokens[i].replaceAll(",","");
		        		try {
		        			return Long.parseLong(num);
		        		} catch(NumberFormatException nfe) {
		        			System.err.println("Error parsing web data");
		        			System.err.println("Input: " + num);
		        			nfe.printStackTrace();
		        			return -1;
		        		}
		        	}
		        }
		    }
		} catch(IOException ie) {
			System.err.println("Error reading web page");
			ie.printStackTrace();
		} catch(Exception e) {
			System.err.println("Uncaught exception");
			e.printStackTrace();
		}

		return -1;
	}
}
