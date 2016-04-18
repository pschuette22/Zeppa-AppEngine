package com.zeppamobile.common.report;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class SmartfollowReport {
	
	private List<String> stacktrace = new ArrayList<String>();
	
	public void log(String text) {
		stacktrace.add(text);
	}
	
	/**
	 * print the trace of the report
	 * 
	 * @param out - median to which you wish to print the report
	 * @throws IOException - if an IOExeption occurs
	 */
	public void print(Writer out) throws IOException {
		for(String s: stacktrace){
			out.write(s);
		}
	}

}
