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
package it.eng.spagobi.commons.utilities;

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.bo.Domain;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.engines.config.bo.Engine;

public class EngineUtilities {

	/**
	 * Checks if is internal.
	 * 
	 * @param engine the engine
	 * 
	 * @return true, if is internal
	 */
	public static boolean isInternal(Engine engine) {
		boolean response = false;
		Domain engineType = getEngTypeDom(engine);
		if("INT".equalsIgnoreCase(engineType.getValueCd())) 
			response=true;
		return response;
	}
	
	/**
	 * Checks if is external.
	 * 
	 * @param engine the engine
	 * 
	 * @return true, if is external
	 */
	public static boolean isExternal(Engine engine) {
		boolean response = false;
		Domain engineType = getEngTypeDom(engine);
		if("EXT".equalsIgnoreCase(engineType.getValueCd())) 
			response=true;
		return response;
	}
	
	
	private static Domain getEngTypeDom(Engine engine) {
		Domain engineType = null;
		try {
			engineType = DAOFactory.getDomainDAO().loadDomainById(engine.getEngineTypeId());
		} catch (EMFUserError e) {
			 SpagoBITracer.major(SpagoBIConstants.NAME_MODULE, EngineUtilities.class.getName(), 
		 				        "getEngTypeDom", "Error retrieving engine type domain", e);
		}
		return engineType;
	}
}
