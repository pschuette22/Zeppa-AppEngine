package com.zeppamobile.smartfollow.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zeppamobile.smartfollow.task.CreateInitialTagFollows;

public class CreateInitialFollowsServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 942591766413889581L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			String u1IdString = req.getParameter("userId1");
			String u2IdString = req.getParameter("userId2");

			Long user1Id = Long.valueOf(u1IdString);
			Long user2Id = Long.valueOf(u2IdString);

			// Create the task object
			CreateInitialTagFollows createTags = new CreateInitialTagFollows(
					getServletContext(), "Preloaded Task", user1Id, user2Id);

			// Execute the tast object
			createTags.execute();

			//
			createTags.finalize();

			resp.setStatus(HttpServletResponse.SC_OK);

		} catch (NumberFormatException e) {
			// Failed to format user IDS
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}

}
