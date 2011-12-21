/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
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
