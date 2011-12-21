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
package it.eng.spagobi.engines.dossier.utils;

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.utilities.ChannelUtilities;
import it.eng.spagobi.commons.utilities.GeneralUtilities;
import it.eng.spagobi.engines.dossier.actions.DossierDownloadAction;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;


public class DossierUtilities {

	static private Logger logger = Logger.getLogger(DossierUtilities.class);
	
	/**
	 * Gets the dossier service url.
	 * 
	 * @return the dossier service url
	 */
	public static String getDossierServiceUrl() {
		logger.debug("IN");
		String sbiContAdd = GeneralUtilities.getSpagoBiHost()+GeneralUtilities.getSpagoBiContext();
		String toReturn = sbiContAdd + "/servlet/AdapterHTTP?NEW_SESSION=TRUE&ACTION_NAME=" + DossierDownloadAction.ACTION_NAME;
		logger.debug("OUT");
		return toReturn;
	}
	
	/**
	 * Gets the dossier service url.
	 * 
	 * @param request the request
	 * 
	 * @return the dossier service url
	 * @throws EMFUserError 
	 */
	public static String getDossierServiceUrl(HttpServletRequest request) {
		logger.debug("IN");
		String sbiContAdd = ChannelUtilities.getSpagoBIContextName(request);
		String toReturn = sbiContAdd + "/servlet/AdapterHTTP?NEW_SESSION=TRUE&ACTION_NAME=" + DossierDownloadAction.ACTION_NAME;
		logger.debug("OUT");
		return toReturn;
	}
	
}
