/*
*
* @file UserSession.java
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
* @version $Id: UserSession.java,v 1.9 2010/03/12 12:49:14 PhilippBouillon Exp $
*
*/

/*
 * (c) Tensegrity Software 2009
 * All rights reserved
 */
package com.tensegrity.palo.gwt.core.server.services;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import org.palo.viewapi.AuthUser;

/**
 * <code>UserSession</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: UserSession.java,v 1.9 2010/03/12 12:49:14 PhilippBouillon Exp $
 **/
public class UserSession {

	private AuthUser user;
//	private final String sessionId;
	private final String id;
	private NumberFormat format = new DecimalFormat("#,##0.00");
	private String locale = "en";
	private static int userSessionID = 0;	
	private ResourceBundle messages;
		
	public UserSession(AuthUser user) {
		this.user = user;
		this.id = "" + userSessionID++;
		messages = ResourceBundle.getBundle("com.tensegrity.palo.gwt.core.server.services.messages", new Locale(locale), new UTF8ResourceBundleControl());
	}
	
	UserSession(AuthUser user, String id) {
		this.user = user;
		this.id = id;		
		messages = ResourceBundle.getBundle("com.tensegrity.palo.gwt.core.server.services.messages", new Locale(locale), new UTF8ResourceBundleControl());
	}
		
	public void setUser(AuthUser user) {
		this.user = user;
	}
	
	public void setLocale(String loc) {
		if (loc != null && !loc.isEmpty()) {
			this.locale = loc;
			if (locale.length() > 2) {
				locale = locale.substring(0, 2);
			}
			try {
				messages = ResourceBundle.getBundle("com.tensegrity.palo.gwt.core.server.services.messages", new Locale(locale), new UTF8ResourceBundleControl());
			} catch (Throwable t) {
				locale = "en";
				messages = ResourceBundle.getBundle("com.tensegrity.palo.gwt.core.server.services.messages", new Locale("en"), new UTF8ResourceBundleControl());
			}
		}
	}
	
	
	public String getLocale() {
		return locale;
	}
	
	public void setNumberFormat(NumberFormat format) {
		if (format == null) {
			this.format = new DecimalFormat("#,##0.00");		
		} else {
			this.format = format;
			if (this.format instanceof DecimalFormat) {
				DecimalFormat df = (DecimalFormat) this.format;
				df.setMaximumFractionDigits(2);
				df.setMinimumFractionDigits(2);
				this.format = df;
			}
		}
	}
	
	public NumberFormat getNumberFormat() {
		return format;
	}
	
	public final AuthUser getUser() {
		return user;
	}
	
	public final String getSessionId() {
		return id;
	}

	public static String trans(String locale, String key) {
		if (locale != null && !locale.isEmpty()) {
			if (locale.length() > 2) {
				locale = locale.substring(0, 2);
			}
			ResourceBundle messages;
			try {
				messages = ResourceBundle.getBundle("com.tensegrity.palo.gwt.core.server.services.messages", new Locale(locale), new UTF8ResourceBundleControl());
			} catch (Throwable t) {
				locale = "en";
				messages = ResourceBundle.getBundle("com.tensegrity.palo.gwt.core.server.services.messages", new Locale("en"), new UTF8ResourceBundleControl());
			}
			return messages.getString(key);
		}
		return "<UNKNOWN_RESOURCE>";
	}
	
	public static String trans(String locale, String key, Object ... params) {
		if (locale != null && !locale.isEmpty()) {
			ResourceBundle messages;
			if (locale.length() > 2) {
				locale = locale.substring(0, 2);
			}
			try {
				messages = ResourceBundle.getBundle("com.tensegrity.palo.gwt.core.server.services.messages", new Locale(locale), new UTF8ResourceBundleControl());
			} catch (Throwable t) {
				locale = "en";
				messages = ResourceBundle.getBundle("com.tensegrity.palo.gwt.core.server.services.messages", new Locale("en"), new UTF8ResourceBundleControl());
			}
			return MessageFormat.format(messages.getString(key), params); 
		}
		return "<UNKNOWN_RESOURCE>";
	}

	public String translate(String text) {
		return messages.getString(text);
	}
	
	public String translate(String key, Object ... params) {
		return MessageFormat.format(messages.getString(key), params);
	}
	
//	public final String getId() {
//		return id;
//	}
}
