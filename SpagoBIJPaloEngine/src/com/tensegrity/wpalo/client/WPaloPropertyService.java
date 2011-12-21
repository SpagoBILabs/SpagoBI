/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

package com.tensegrity.wpalo.client;

import com.google.gwt.user.client.rpc.RemoteService;

//TODO just for quick fixing PR 569. we need a more sophisticated method to
//load property files...
public interface WPaloPropertyService extends RemoteService {
	
	public int getIntProperty(String name, int defaultValue);
	public boolean getBooleanProperty(String name, boolean defaultValue);
	public String getStringProperty(String name);
	public String getBuildNumber();
	public String [] getCurrentBuildNumber();
}
