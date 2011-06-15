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

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.commons.presentation.DynamicPublisher;
import it.eng.spagobi.engines.qbe.QbeEngineInstance;
import it.eng.spagobi.engines.qbe.services.core.AbstractQbeEngineAction;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceExceptionHandler;

import org.apache.log4j.Logger;

/**
 * @author Davide Zerbetto (davide.zerbetto@eng.it)
 */
public class WorksheetStartEditAction extends AbstractQbeEngineAction {	
	
	public static final String ENGINE_INSTANCE = EngineConstants.ENGINE_INSTANCE;
	
	/** Logger component. */
    private static transient Logger logger = Logger.getLogger(WorksheetStartEditAction.class);
    
    public void service(SourceBean serviceRequest, SourceBean serviceResponse) {
    	QbeEngineInstance qbeEngineInstance = null;
    	
    	logger.debug("IN");
       
    	try {
			super.service(serviceRequest, serviceResponse);
			
			qbeEngineInstance = getEngineInstance();
			
			setAttribute(ENGINE_INSTANCE, qbeEngineInstance);
			
			//publisher for the qbe edit
			String publisherName = "WORKSHEET_START_EDIT_ACTION_QBE_PUBLISHER";
			
			if (qbeEngineInstance.getFormState()!=null) {
				//publisher for the smart filter edit
				publisherName = "WORKSHEET_START_EDIT_ACTION_FORM_PUBLISHER";
				serviceRequest.setAttribute("MODALITY", "WORKSHEET_EDIT");
			}
			
			serviceResponse.setAttribute(DynamicPublisher.PUBLISHER_NAME, publisherName);
			
		} catch(Throwable t) {
			throw SpagoBIEngineServiceExceptionHandler.getInstance().getWrappedException(getActionName(), getEngineInstance(), t);
		} finally {
			logger.debug("OUT");
		}	
		
	}
    
}