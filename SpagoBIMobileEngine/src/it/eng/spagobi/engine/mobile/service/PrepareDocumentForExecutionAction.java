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
package it.eng.spagobi.engine.mobile.service;

import it.eng.spagobi.analiticalmodel.document.handlers.ExecutionInstance;
import it.eng.spagobi.commons.services.AbstractSpagoBIAction;
import it.eng.spagobi.engine.mobile.MobileConstants;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.service.JSONAcknowledge;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.json.JSONObject;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public class PrepareDocumentForExecutionAction extends AbstractSpagoBIAction {
	
	private static final long serialVersionUID = -4708339969302528709L;

	// logger component
	private static Logger logger = Logger.getLogger(PrepareDocumentForExecutionAction.class);

	public void doService() {
		
		ExecutionInstance executionInstance;

		logger.debug("IN");

		try {
			executionInstance = getContext().getExecutionInstance( ExecutionInstance.class.getName() );
			Assert.assertNotNull(executionInstance, "Execution instance cannot be null in order to properly generate execution url");
			
			// we are not executing a subobject or a snapshot, so delete subobject/snapshot if existing
			executionInstance.setSubObject(null);
			executionInstance.setSnapshot(null);
			JSONObject executionInstanceJSON = this.getAttributeAsJSONObject( MobileConstants.PARAMETERS );
			executionInstance.refreshParametersValues(executionInstanceJSON, false);
			
			writeBackToClient( new JSONAcknowledge());
			
		} catch (IOException e) {
			logger.debug("Impossible to write back the responce to the client", e);
			throw new SpagoBIServiceException("Impossible to write back the responce to the client", e);
		}finally {
			logger.debug("OUT");
		}
	}

	
}
