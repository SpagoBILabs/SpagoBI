/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.engines.geo.service;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.engines.geo.GeoEngine;


import org.apache.log4j.Logger;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class GetGeoEngineInfosAction  extends AbstractGeoEngineAction {
	
	private static final String INFO_TYPE_PARAM_NAME = "infoType"; 
	private static final String INFO_TYPE_VERSION = "version"; 
	private static final String INFO_TYPE_NAME = "name"; 
	
	private static final long serialVersionUID = 1L;
	
	private static transient Logger logger = Logger.getLogger(GetGeoEngineInfosAction.class);
	
	
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) {	
		
		String infoType;
		String responseMessage;
		
		logger.debug("IN");
		
		try {	
				
			infoType = getAttributeAsString(INFO_TYPE_PARAM_NAME);
		
			if(INFO_TYPE_VERSION.equalsIgnoreCase( infoType )) {
				responseMessage = GeoEngine.getVersion().toString();
			} else if (INFO_TYPE_NAME.equalsIgnoreCase( infoType )) {
				responseMessage = GeoEngine.getVersion().getFullName();
			} else {
				responseMessage = GeoEngine.getVersion().getInfo();
			}
			
			
			tryToWriteBackToClient( responseMessage );
			
		} finally {
			logger.debug("OUT");
		}		
	}
}
	
