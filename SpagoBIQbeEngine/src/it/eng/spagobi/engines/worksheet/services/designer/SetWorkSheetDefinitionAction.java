/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
package it.eng.spagobi.engines.worksheet.services.designer;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.commons.QbeEngineStaticVariables;
import it.eng.spagobi.engines.worksheet.services.AbstractWorksheetEngineAction;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceExceptionHandler;
import it.eng.spagobi.utilities.service.JSONAcknowledge;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.json.JSONObject;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public class SetWorkSheetDefinitionAction extends AbstractWorksheetEngineAction {

	private static final long serialVersionUID = -7253525210753136929L;
	public static transient Logger logger = Logger.getLogger(SetWorkSheetDefinitionAction.class);
	
	
	/**
	 * Get the definition of the worksheet from the request, serialize and save it into the qbe engine instance
	 */
	public void service(SourceBean request, SourceBean response)  {				

		logger.debug("IN");
		
		super.service(request, response);	
		try {
			//get the worksheet from the request
			JSONObject worksheetDefinitionJSON = getAttributeAsJSONObject(QbeEngineStaticVariables.WORKSHEET_DEFINITION_LOWER );
			Assert.assertNotNull(worksheetDefinitionJSON, "Parameter [" + QbeEngineStaticVariables.WORKSHEET_DEFINITION_LOWER + "] cannot be null in oder to execute " + this.getActionName() + " service");
			logger.debug("Parameter [" + QbeEngineStaticVariables.WORKSHEET_DEFINITION_LOWER + "] is equals to [" + worksheetDefinitionJSON.toString() + "]");

			updateWorksheetDefinition(worksheetDefinitionJSON);
			
		} catch(Throwable t) {
			throw SpagoBIEngineServiceExceptionHandler.getInstance().getWrappedException(getActionName(), getEngineInstance(), t);
		} finally {
			logger.debug("OUT");
		}	
		try {
			writeBackToClient(new JSONAcknowledge());	
		} catch (IOException e) {
			String message = "Impossible to write back the responce to the client";
			throw new SpagoBIEngineServiceException(getActionName(), message, e);
		}
	}
}
	