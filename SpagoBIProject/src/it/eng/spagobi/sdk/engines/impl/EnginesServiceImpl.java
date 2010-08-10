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
package it.eng.spagobi.sdk.engines.impl;

import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.engines.config.bo.Engine;
import it.eng.spagobi.sdk.AbstractSDKService;
import it.eng.spagobi.sdk.engines.EnginesService;
import it.eng.spagobi.sdk.engines.bo.SDKEngine;
import it.eng.spagobi.sdk.exceptions.NotAllowedOperationException;
import it.eng.spagobi.sdk.utilities.SDKObjectsConverter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

public class EnginesServiceImpl extends AbstractSDKService implements EnginesService {

	static private Logger logger = Logger.getLogger(EnginesServiceImpl.class);
	
	public SDKEngine getEngine(Integer engineId) throws NotAllowedOperationException {
		SDKEngine toReturn = null;
		logger.debug("IN: engineId in input = " + engineId);
        try {
            super.checkUserPermissionForFunctionality(SpagoBIConstants.ENGINES_MANAGEMENT, "User cannot see engines congifuration.");
            if (engineId == null) {
            	logger.warn("Engine identifier in input is null!");
            	return null;
            }
        	Engine engine = DAOFactory.getEngineDAO().loadEngineByID(engineId);
        	if (engine == null) {
        		logger.warn("Engine with identifier [" + engineId + "] not existing.");
        		return null;
        	}
        	toReturn = new SDKObjectsConverter().fromEngineToSDKEngine(engine);
        } catch(NotAllowedOperationException e) {
        	throw e;
        } catch(Exception e) {
            logger.error("Error while retrieving SDKEngine", e);
            logger.debug("Returning null");
            return null;
        } finally {
        	logger.debug("OUT");
        }
        return toReturn;
	}

	public SDKEngine[] getEngines() throws NotAllowedOperationException {
		SDKEngine[] toReturn = null;
        logger.debug("IN");
        try {
        	super.checkUserPermissionForFunctionality(SpagoBIConstants.ENGINES_MANAGEMENT, "User cannot see engines congifuration.");
        	List enginesList = DAOFactory.getEngineDAO().loadAllEngines();
        	List sdkEnginesList = new ArrayList();
    		if (enginesList != null && enginesList.size() > 0) {
                for (Iterator it = enginesList.iterator(); it.hasNext();) {
                    Engine engine = (Engine) it.next();
                    SDKEngine sdkEngine = new SDKObjectsConverter().fromEngineToSDKEngine(engine);
                    sdkEnginesList.add(sdkEngine);
                }
    		}
    		toReturn = new SDKEngine[sdkEnginesList.size()];
    		toReturn = (SDKEngine[]) sdkEnginesList.toArray(toReturn);
        } catch(NotAllowedOperationException e) {
        	throw e;
        } catch(Exception e) {
            logger.error("Error while retrieving SDKEngine list", e);
            logger.debug("Returning null");
            return null;
        } finally {
        	logger.debug("OUT");
        }
        return toReturn;
	}

}
