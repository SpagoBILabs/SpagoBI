/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

package com.tensegrity.wpalo.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

public class WPaloPropertyServiceProvider implements WPaloPropertyServiceAsync {

	private final static WPaloPropertyServiceProvider instance = new WPaloPropertyServiceProvider();
	private final WPaloPropertyServiceAsync proxy;

	private WPaloPropertyServiceProvider() {
		proxy = GWT.create(WPaloPropertyService.class);
		((ServiceDefTarget) proxy).setServiceEntryPoint(GWT.getModuleBaseURL()
				+ "wpalo-property-service");
	}

	public static WPaloPropertyServiceProvider getInstance() {
		return instance;
	}

	public void getIntProperty(String name, int defaultValue,
			AsyncCallback<Integer> cb) {
		proxy.getIntProperty(name, defaultValue, cb);
	}

	public void getBooleanProperty(String name, boolean defaultValue,
			AsyncCallback<Boolean> cb) {
		proxy.getBooleanProperty(name, defaultValue, cb);		
	}

	public void getStringProperty(String name, AsyncCallback<String> cb) {
		proxy.getStringProperty(name, cb);		
	}

	public void getBuildNumber(AsyncCallback<String> cb) {
		proxy.getBuildNumber(cb);
	}

	public void getCurrentBuildNumber(AsyncCallback<String[]> cb) {
		proxy.getCurrentBuildNumber(cb);
	}
}
