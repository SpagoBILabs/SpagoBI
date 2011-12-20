/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.

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

import it.eng.spago.navigation.LightNavigationManager;

public class UrlUtilities {

	/**
	 * Adds the navigator disabled parameter.
	 * 
	 * @param url the url
	 * 
	 * @return the string
	 */
	public static String addNavigatorDisabledParameter(String url) {
		String urltoreturn = url;
		if(url.indexOf("?")==-1) {
			urltoreturn = url + "?" + LightNavigationManager.LIGHT_NAVIGATOR_DISABLED + "=TRUE";
		} else {
			if(url.indexOf(LightNavigationManager.LIGHT_NAVIGATOR_DISABLED)==-1) {
				if(url.endsWith("&") || url.endsWith("&amp;") || url.endsWith("?")) {
					urltoreturn = url + LightNavigationManager.LIGHT_NAVIGATOR_DISABLED + "=TRUE";
				} else {
					urltoreturn = url + "&" + LightNavigationManager.LIGHT_NAVIGATOR_DISABLED + "=TRUE";
				}
			}
		} 
		return urltoreturn;
	}
	
}
