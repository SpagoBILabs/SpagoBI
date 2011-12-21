/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */
package it.eng.spagobi.sdk.proxy;

import org.apache.axis.AxisProperties;
import org.apache.axis.client.Stub;

public abstract class AbstractSDKServiceProxy {

	boolean proxyRequirementAuthentication;

	public void setProxyHost(String proxyHost) {
		if(proxyHost!=null){
			AxisProperties.setProperty("http.proxyHost", proxyHost);
		}	
	}
	public void setProxyPort(String proxyPort) {
		if(proxyPort!=null){
			AxisProperties.setProperty("http.proxyPort", proxyPort);
		}
	}
	public void setProxyUserId(String proxyUserId) {
		if(proxyUserId!=null){
			AxisProperties.setProperty("http.proxyUser", proxyUserId);
		}	
	}
	public void setProxyPassword(String proxyPassword) {
		if(proxyPassword!=null){
			AxisProperties.setProperty("http.proxyPassword", proxyPassword); 
		}
	}


//	public void initProxyProperties(){
//	if(proxyHost!=null)
//	AxisProperties.setProperty("http.proxyHost", proxyHost);
//	if(proxyPort!=null)
//	AxisProperties.setProperty("http.proxyPort", proxyPort);
//	if(proxyUserId!=null)
//	AxisProperties.setProperty("http.proxyUser", proxyUserId);
//	if(proxyPassword!=null)
//	AxisProperties.setProperty("http.proxyPassword", proxyPassword); 
//	}

}
