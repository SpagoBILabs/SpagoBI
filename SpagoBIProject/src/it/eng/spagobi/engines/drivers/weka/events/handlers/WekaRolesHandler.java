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
package it.eng.spagobi.engines.drivers.weka.events.handlers;

import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFInternalError;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.engines.drivers.handlers.IRolesHandler;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;


public class WekaRolesHandler implements IRolesHandler {
	
    static private Logger logger = Logger.getLogger(WekaRolesHandler.class);

    /**
     * Ritorna i ruoli che possono eseguire il documento tramite il suo ID.
     * 
     * @param parameters the parameters
     * 
     * @return the list
     * 
     * @throws EMFInternalError the EMF internal error
     * @throws EMFUserError the EMF user error
     */
	public List calculateRoles(String parameters) throws EMFInternalError, EMFUserError {
		logger.debug("IN");
		String[] splittedParameters = parameters.split("&");
		if (splittedParameters == null || splittedParameters.length == 0) {
			logger.error("Missing parameters for roles retrieval");
			throw new EMFInternalError(EMFErrorSeverity.ERROR, "Missing parameters for roles retrieval");
		}
		String biobjectIdStr = null;
		for (int i = 0; i < splittedParameters.length; i++) {
			String parameter = splittedParameters[i].trim();
			String[] splittedParameter = parameter.split("=");
			String parameterName = splittedParameter[0];
			if (parameterName.trim().equalsIgnoreCase("document")) {
				if (splittedParameter.length != 2) {
					logger.error("Malformed parameter for roles retrieval");
					throw new EMFInternalError(EMFErrorSeverity.ERROR, "Malformed parameter for roles retrieval");
				}
				biobjectIdStr = splittedParameter[1];
				break;
			}
		}
		if (biobjectIdStr == null) {
			logger.error("Missing parameters for roles retrieval");
			throw new EMFInternalError(EMFErrorSeverity.ERROR, "Missing parameters for roles retrieval");
		}
		Integer biobjectId = null;
		try {
			biobjectId = new Integer(biobjectIdStr);
		} catch (Exception e) {
			logger.error("Malformed BIObject id: " + biobjectIdStr, e);
			throw new EMFInternalError(EMFErrorSeverity.ERROR, "Malformed BIObject id: " + biobjectIdStr);
		}
		List roles = null;
		try {
			roles = DAOFactory.getBIObjectDAO().getCorrectRolesForExecution(biobjectId);
			Iterator iter=roles.iterator();
			while (iter.hasNext()){
				String roleName=(String)iter.next();
				logger.debug("roleName:"+roleName);
			}
		} catch (EMFUserError e) {
			logger.error("Error while loading correct roles for execution for document with id = " + biobjectId, e);
			throw e;
		}
		logger.debug("OUT");
		return roles;
	}

}
