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
* @version $Id: SessionListener.java,v 1.2 2009/12/17 16:14:29 PhilippBouillon Exp $
*
*/

package com.tensegrity.palo.gwt.core.server.services;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionListener;


class SessionListener implements HttpSessionBindingListener {
	
	private static final String SESSION_LIFECYCLE = 
				BasePaloServiceServlet.class.getName() + "lifecycle";

	private final HttpSessionListener delegate;

	SessionListener(HttpSessionListener delegate) {
		this.delegate = delegate;
	}

	public void valueBound(HttpSessionBindingEvent be) {
		delegate.sessionCreated(be);
	}

	public void valueUnbound(HttpSessionBindingEvent be) {
		Object obj = 
			be.getSession().getAttribute(BasePaloServiceServlet.SESSION_USER);
		System.out.println("invalidate session for user: "+obj);
		delegate.sessionDestroyed(be);
	}

	public void registerTo(HttpSession session) {
		session.setAttribute(SESSION_LIFECYCLE, this);
	}
	
	public void unregisterFrom(HttpSession session) {
		session.removeAttribute(SESSION_LIFECYCLE);
	}
}
