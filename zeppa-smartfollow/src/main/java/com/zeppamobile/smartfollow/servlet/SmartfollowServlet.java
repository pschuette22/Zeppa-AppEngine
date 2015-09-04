package com.zeppamobile.smartfollow.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zeppamobile.smartfollow.task.CreateInitialTagFollows;

/**
 * Servlet for handling requests to execute smartfollow tasks
 * 
 * @author Pete Schuette
 *
 */
public class SmartfollowServlet extends HttpServlet {

	
	/**
	 * 
	 * @author Pete Schuette
	 * Count the number of running task executors
	 *
	 */
	private class SyncCounter {

		private int c = 0;

		public SyncCounter(){
		}
		
		public synchronized void increment() {
			c++;
		}

		public synchronized void decrement() {
			c--;
		}

		public synchronized int value() {
			return c;
		}
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
	}
	
	

}
