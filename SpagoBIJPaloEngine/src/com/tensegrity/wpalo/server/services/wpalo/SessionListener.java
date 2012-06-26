/*
*
* @file SessionListener.java
*
* Copyright (C) 2006-2009 Tensegrity Software GmbH
*
* This program is free software; you can redistribute it and/or modify it
* under the terms of the GNU General Public License (Version 2) as published
* by the Free Software Foundation at http://www.gnu.org/copyleft/gpl.html.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
* FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
* more details.
*
* You should have received a copy of the GNU General Public License along with
* this program; if not, write to the Free Software Foundation, Inc., 59 Temple
* Place, Suite 330, Boston, MA 02111-1307 USA
*
* If you are developing and distributing open source applications under the
* GPL License, then you are free to use JPalo Modules under the GPL License.  For OEMs,
* ISVs, and VARs who distribute JPalo Modules with their products, and do not license
* and distribute their source code under the GPL, Tensegrity provides a flexible
* OEM Commercial License.
*
* @author Philipp Bouillon <Philipp.Bouillon@tensegrity-software.com>
*
* @version $Id: SessionListener.java,v 1.11 2010/04/13 09:45:15 PhilippBouillon Exp $
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
