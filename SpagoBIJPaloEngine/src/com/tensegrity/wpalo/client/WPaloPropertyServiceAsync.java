/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

package com.tensegrity.wpalo.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface WPaloPropertyServiceAsync {

	public void getIntProperty(String name, int defaultValue, AsyncCallback<Integer> cb);
	public void getBooleanProperty(String name, boolean defaultValue, AsyncCallback<Boolean> cb);
	public void getStringProperty(String name, AsyncCallback<String> cb);
	public void getBuildNumber(AsyncCallback <String> cb);
	public void getCurrentBuildNumber(AsyncCallback <String []> cb);
}
