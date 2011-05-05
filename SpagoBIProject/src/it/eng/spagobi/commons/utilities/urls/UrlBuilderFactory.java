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
package it.eng.spagobi.commons.utilities.urls;

import it.eng.spago.base.ApplicationContainer;
import it.eng.spagobi.commons.SingletonConfig;
import it.eng.spagobi.commons.constants.SpagoBIConstants;

public class UrlBuilderFactory {

	/**
	 * Gets the url builder.
	 * 
	 * @return the url builder
	 */
	public static IUrlBuilder getUrlBuilder() {
		ApplicationContainer spagoContext = ApplicationContainer.getInstance();
		IUrlBuilder urlBuilder = (IUrlBuilder)spagoContext.getAttribute(SpagoBIConstants.URL_BUILDER);
		if(urlBuilder==null) {
			SingletonConfig spagoconfig = SingletonConfig.getInstance();
			// get mode of execution
			String sbiMode = (String)spagoconfig.getConfigValue("SPAGOBI.SPAGOBI-MODE.mode");   
			// based on mode get spago object and url builder
			if (sbiMode.equalsIgnoreCase("WEB")) {
				urlBuilder = new WebUrlBuilder();		
			} else if  (sbiMode.equalsIgnoreCase("PORTLET")){
				urlBuilder = new PortletUrlBuilder();
			}
			spagoContext.setAttribute(SpagoBIConstants.URL_BUILDER, urlBuilder);
		}	
		return urlBuilder;
	}
	
	/**
	 * Gets the url builder.
	 * 
	 * @param channelType the channel type
	 * 
	 * @return the url builder
	 */
	public static IUrlBuilder getUrlBuilder(String channelType) {
		IUrlBuilder urlBuilder = null;
		// based on mode get spago object and url builder
		if (channelType.equalsIgnoreCase("WEB") || channelType.equalsIgnoreCase("HTTP")) {
			urlBuilder = new WebUrlBuilder();		
		} else if  (channelType.equalsIgnoreCase("PORTLET")){
			urlBuilder = new PortletUrlBuilder();
		}
		return urlBuilder;
	}	
		
	
}
