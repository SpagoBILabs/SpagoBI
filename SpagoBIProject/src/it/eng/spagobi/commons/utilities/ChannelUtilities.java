/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2008 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

**/
package it.eng.spagobi.commons.utilities;

import it.eng.spago.base.RequestContainer;
import it.eng.spago.base.RequestContainerAccess;
import it.eng.spago.base.RequestContainerPortletAccess;
import it.eng.spago.base.ResponseContainer;
import it.eng.spago.base.ResponseContainerAccess;
import it.eng.spago.base.ResponseContainerPortletAccess;
import it.eng.spago.base.SourceBean;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.SingletonConfig;
import it.eng.spagobi.commons.constants.SpagoBIConstants;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

public class ChannelUtilities {

	/**
	 * Gets the request container.
	 * 
	 * @param httpRequest the http request
	 * 
	 * @return the request container
	 */
	public static RequestContainer getRequestContainer(HttpServletRequest httpRequest) {
		RequestContainer reqCont = null;
		// try to find the RequestContainer
		reqCont = RequestContainerPortletAccess.getRequestContainer(httpRequest);
		if (reqCont == null) reqCont = RequestContainerAccess.getRequestContainer(httpRequest);
		return reqCont;
	}
	
	/**
	 * Gets the response container.
	 * 
	 * @param httpRequest the http request
	 * 
	 * @return the response container
	 */
	public static ResponseContainer getResponseContainer(HttpServletRequest httpRequest) {
		ResponseContainer respCont = null;
		// try to find the ResponseContainer
		respCont = ResponseContainerPortletAccess.getResponseContainer(httpRequest);
		if (respCont == null) respCont = ResponseContainerAccess.getResponseContainer(httpRequest);
		return respCont;
	}
	
	
	/**
	 * Gets the preference value.
	 * 
	 * @param requestContainer the request container
	 * @param preferenceName the preference name
	 * @param defaultValue the default value
	 * 
	 * @return the preference value
	 */
	public static String getPreferenceValue(RequestContainer requestContainer, String preferenceName, String defaultValue) {
		String prefValue = defaultValue;
		try{
			// get mode of execution
			String channelType = requestContainer.getChannelType();
			String sbiMode = null;
			if ("PORTLET".equalsIgnoreCase(channelType)) sbiMode = "PORTLET";
			else sbiMode = "WEB";
			// based on mode get spago object and url builder
			if (sbiMode.equalsIgnoreCase("WEB")) {
				SourceBean request = requestContainer.getServiceRequest();
				Object prefValueObj = request.getAttribute(preferenceName);
				if (prefValueObj != null) prefValue = prefValueObj.toString();
				else prefValue = defaultValue;
			} else if  (sbiMode.equalsIgnoreCase("PORTLET")){
				PortletRequest portReq = PortletUtilities.getPortletRequest();
				PortletPreferences prefs = portReq.getPreferences();
				prefValue = (String)prefs.getValue(preferenceName, defaultValue);
			}
		} catch (Exception e) {
			SpagoBITracer.major(SpagoBIConstants.NAME_MODULE, ChannelUtilities.class.getName(), 
					            "getPreferenceValue", "Error while recovering preference value", e);
			prefValue = defaultValue;
		}
		return prefValue;
	}
	
	
	/**
	 * Gets the spago bi context name.
	 * 
	 * @param httpRequest the http request
	 * 
	 * @return the spago bi context name
	 * @throws EMFUserError 
	 */
	public static String getSpagoBIContextName(HttpServletRequest httpRequest) {
		String contextName = "Spagobi";
		SingletonConfig spagoconfig = SingletonConfig.getInstance();
		// get mode of execution
		String sbiMode = spagoconfig.getConfigValue("SPAGOBI.SPAGOBI-MODE.mode");   
		// based on mode get spago object and url builder
		if (sbiMode.equalsIgnoreCase("WEB")) {
			contextName = httpRequest.getContextPath();
		} else if  (sbiMode.equalsIgnoreCase("PORTLET")){
			PortletRequest portletRequest = PortletUtilities.getPortletRequest();
			contextName = portletRequest.getContextPath();
		}
		return contextName;
	}
	
	
	/**
	 * Checks if is web running.
	 * 
	 * @return true, if is web running
	 */
	public static boolean isWebRunning() {
		SingletonConfig spagoconfig = SingletonConfig.getInstance();
		// get mode of execution
		String sbiMode = (String)spagoconfig.getConfigValue("SPAGOBI.SPAGOBI-MODE.mode");   
		if( (sbiMode!=null) && sbiMode.equalsIgnoreCase("WEB") ) {
			return true;
		} else {
			return false;
		}
	}
	
	
	/**
	 * Checks if is portlet running.
	 * 
	 * @return true, if is portlet running
	 */
	public static boolean isPortletRunning() {
		SingletonConfig spagoconfig = SingletonConfig.getInstance();
		// get mode of execution
		String sbiMode = (String)spagoconfig.getConfigValue("SPAGOBI.SPAGOBI-MODE.mode");   
		if( (sbiMode==null) || !sbiMode.equalsIgnoreCase("WEB")){
			return true;
		} else {
			return false;
		}
	}
	
	
}
