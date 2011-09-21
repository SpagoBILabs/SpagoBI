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
package it.eng.spagobi.engines.qbe.services.worksheet;

import it.eng.qbe.serializer.SerializationManager;
import it.eng.spago.base.SourceBean;
import it.eng.spagobi.commons.QbeEngineStaticVariables;
import it.eng.spagobi.engines.qbe.FormState;
import it.eng.spagobi.engines.qbe.services.core.AbstractQbeEngineAction;
import it.eng.spagobi.engines.qbe.services.initializers.WorksheetEngineStartAction;
import it.eng.spagobi.engines.qbe.worksheet.Sheet;
import it.eng.spagobi.engines.qbe.worksheet.WorkSheetDefinition;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceExceptionHandler;
import it.eng.spagobi.utilities.service.JSONAcknowledge;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONObject;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public class SetWorkSheetDefinitionAction extends AbstractQbeEngineAction {

	private static final long serialVersionUID = -7253525210753136929L;
	public static transient Logger logger = Logger.getLogger(SetWorkSheetDefinitionAction.class);
	public static final String FORM_STATE = "formState";
	
	/**
	 * Get the definition of the worksheet from the request, serialize and save it into the qbe engine instance
	 */
	public void service(SourceBean request, SourceBean response)  {				

		logger.debug("IN");
		
		super.service(request, response);	
		try {
			//get the worksheet from the request
			JSONObject workSheetDefinitionJSON = getAttributeAsJSONObject(QbeEngineStaticVariables.WORKSHEET_DEFINITION_LOWER );
			Assert.assertNotNull(workSheetDefinitionJSON, "Parameter [" + QbeEngineStaticVariables.WORKSHEET_DEFINITION_LOWER + "] cannot be null in oder to execute " + this.getActionName() + " service");
			logger.debug("Parameter [" + workSheetDefinitionJSON + "] is equals to [" + workSheetDefinitionJSON.toString() + "]");

			//set the worksheet into the qbe instance
			WorkSheetDefinition workSheetDefinition = (WorkSheetDefinition)SerializationManager.deserialize(workSheetDefinitionJSON, "application/json", WorkSheetDefinition.class);
			List<Sheet> ws = workSheetDefinition.getSheets();
			for(int i=0; i<ws.size();i++){
				WorksheetEngineStartAction.setImageWidth((ws.get(i)).getHeader());
				WorksheetEngineStartAction.setImageWidth((ws.get(i)).getFooter());
			}
			
			getEngineInstance().setWorkSheetDefinition(workSheetDefinition);
			
			try {
				JSONObject jsonEncodedFormState = getAttributeAsJSONObject(FORM_STATE);
				
				FormState formState = getEngineInstance().getFormState();
				if(formState==null){
					formState = new FormState();
					getEngineInstance().setFormState(formState);
				}
				formState.setFormStateValues(jsonEncodedFormState);
				

			} catch (Exception e) {
				logger.debug("No Form State defined");
			}

			
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
	