/*
*
* @file WPaloControllerServiceProvider.java
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
* @version $Id: WPaloControllerServiceProvider.java,v 1.5 2010/02/16 13:53:42 PhilippBouillon Exp $
*
*/

package com.tensegrity.wpalo.client.services.wpalo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.tensegrity.palo.gwt.core.client.models.admin.XUser;

public class WPaloControllerServiceProvider implements
		WPaloControllerServiceAsync {

	//--------------------------------------------------------------------------
	//FACTORY
	//
	private final static WPaloControllerServiceProvider instance =
		new WPaloControllerServiceProvider();
	private final WPaloControllerServiceAsync proxy;
	
	public static final WPaloControllerServiceProvider getInstance() {
		return instance;
	}
	
	//--------------------------------------------------------------------------
	//INSTANCE
	//
	private final BrowserListener browserListener;
		
	private WPaloControllerServiceProvider() {
		proxy = (WPaloControllerServiceAsync) GWT
				.create(WPaloControllerService.class);
		((ServiceDefTarget) proxy).setServiceEntryPoint(GWT.getModuleBaseURL()
				+ "wpalo-controller");
		browserListener = new BrowserListener();
	}

	
	public void login(String login, String password, String locale, 
			AsyncCallback<XUser> callback) {
		proxy.login(login, password, locale, callback);
	}

	public void loginHash(String login, String password, String locale,
			AsyncCallback<XUser> callback) {
		proxy.loginHash(login, password, locale, callback);
	}

	public void logout(String sessionId, AsyncCallback<Void> callback) {
		proxy.logout(sessionId, callback);
	}

	public final void start(AsyncCallback<Void> callback) {
		Window.addWindowCloseListener(browserListener);
		proxy.start(callback);
	}

	public final void stop(String sessionId, AsyncCallback<Void> callback) {
		Window.removeWindowCloseListener(browserListener);
		proxy.stop(sessionId, callback);
	}
}
