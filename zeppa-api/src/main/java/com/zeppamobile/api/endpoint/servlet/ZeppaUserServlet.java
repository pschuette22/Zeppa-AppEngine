package com.zeppamobile.api.endpoint.servlet;

import java.io.IOException;
import java.net.URLDecoder;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.zeppamobile.api.PMF;
import com.zeppamobile.api.datamodel.VendorEvent;
import com.zeppamobile.api.datamodel.ZeppaUser;
import com.zeppamobile.api.datamodel.ZeppaUserInfo;
import com.zeppamobile.common.UniversalConstants;

/**
 * Servlet implementation class ZeppaUserServlet
 */
public class ZeppaUserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ZeppaUserServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String userId = request.getParameter(UniversalConstants.PARAM_USER_ID);
		JSONArray results = new JSONArray();

		JSONObject obj = new JSONObject();
		if (userId != null && !userId.isEmpty()) {
			userId = URLDecoder.decode(userId, "UTF-8");
			ZeppaUser user = getUser(Long.valueOf(userId));
			obj.put("email", user.getAuthEmail());
			if(user.getUserInfo().getGender() != null && user.getUserInfo().getGender().equals(ZeppaUserInfo.Gender.MALE)) {
				obj.put("gender", "MALE");
			} else if((user.getUserInfo().getGender() != null && user.getUserInfo().getGender().equals(ZeppaUserInfo.Gender.FEMALE))) {
				obj.put("gender", "FEMALE");
			}
		}

		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_OK);
		// send Json back
		response.getWriter().write(obj.toJSONString());
	}

	public static ZeppaUser getUser(Long userId) {
		PersistenceManager mgr = getPersistenceManager();
		ZeppaUser user = null;
		try {
			// Query for the event with the given ID
			user = mgr.getObjectById(ZeppaUser.class, Long.valueOf(userId));
			// touch the attributes that are needed in other classes
			user.getKey();
			user.getId();
			user.getUserInfo();
			user.getUserInfo().getGivenName();
			user.getUserInfo().getGender();
			user.getInitialTags();
		} finally {
			mgr.close();
		}

		return user;
	}

	/**
	 * Get the persistence manager for interacting with datastore
	 */
	private static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}

}
