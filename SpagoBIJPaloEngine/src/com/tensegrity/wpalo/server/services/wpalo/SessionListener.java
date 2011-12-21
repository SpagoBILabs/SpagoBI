/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

package com.tensegrity.wpalo.server.services.wpalo;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.servlet.http.HttpSession;

import com.tensegrity.palo.gwt.core.server.services.ISessionListener;
import com.tensegrity.palo.gwt.core.server.services.UserSession;

class SessionListener implements ISessionListener {

	private final WPaloControllerServiceImpl service;
	private long t0;
    public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";

	public static String now() {
	    Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
	    return sdf.format(cal.getTime());
	}

	SessionListener(WPaloControllerServiceImpl service) {
		this.service = service;
	}

	public void sessionEnded(UserSession userSession) {
		long t1 = System.currentTimeMillis();
		int seconds = (int) ((t1 - t0) / 1000d);
//		service.terminateSession();		
		System.err.println("[palo pivot - " + now() + "] session destroyed after " + seconds + "sec");
		service.stopIgnoreException(userSession);
	}

	public void sessionStarted(HttpSession session) {
		t0 = System.currentTimeMillis();
		System.err.println("[palo pivot - " + now() + "] session created");
		initialize(session);
	}

	private void initialize(HttpSession session) {
		// sets the timeout interval in seconds:
//		session.setMaxInactiveInterval(10);
	}
}
