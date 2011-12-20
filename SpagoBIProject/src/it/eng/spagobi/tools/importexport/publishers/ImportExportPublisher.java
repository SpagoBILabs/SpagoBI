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

package it.eng.spagobi.tools.importexport.publishers;

import it.eng.spago.base.RequestContainer;
import it.eng.spago.base.ResponseContainer;
import it.eng.spago.base.SourceBean;
import it.eng.spago.error.EMFErrorHandler;
import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.presentation.PublisherDispatcherIFace;
import it.eng.spagobi.commons.utilities.GeneralUtilities;
import it.eng.spagobi.tools.importexport.ImportExportConstants;

import org.apache.log4j.Logger;


/**
 * A publisher used to lead execution flow after a import / export service.
 */
public class ImportExportPublisher implements PublisherDispatcherIFace {

    static private Logger logger = Logger.getLogger(ImportExportPublisher.class);
	
	/**
	 * Given the request at input, gets the name of the reference publisher,driving
	 * the execution into the correct jsp page, or jsp error page, if any error occurred.
	 * 
	 * @param requestContainer The object containing all request information
	 * @param responseContainer The object containing all response information
	 * 
	 * @return A string representing the name of the correct publisher, which will
	 * call the correct jsp reference.
	 */
	public String getPublisherName(RequestContainer requestContainer, ResponseContainer responseContainer) {
	    logger.debug("IN");
	    EMFErrorHandler errorHandler = responseContainer.getErrorHandler();
		SourceBean moduleResponse = (SourceBean)responseContainer.getServiceResponse().getAttribute("ImportExportModule");
		if(moduleResponse==null) {
			logger.warn( "Module response null");
			EMFUserError error = new EMFUserError(EMFErrorSeverity.ERROR, "10", "component_impexp_messages");
			errorHandler.addError(error);
			logger.warn("OUT. Error");
			return "error";
		}
		
		String pubName = (String)moduleResponse.getAttribute(ImportExportConstants.PUBLISHER_NAME);
		
		// if there are errors and they are only validation errors return the name for the detail publisher
		if(!errorHandler.isOK()) {
			if(GeneralUtilities.isErrorHandlerContainingOnlyValidationError(errorHandler)) {
				if((pubName!=null) && !(pubName.trim().equals(""))) {
					return pubName;
				} else { 
					return new String("ImportExportLoopback");
				}
			}
		}
		
				
		if(errorHandler.isOKBySeverity(EMFErrorSeverity.ERROR)) {
			if((pubName!=null) && !(pubName.trim().equals(""))) {
			    logger.debug("OUT.pubName="+pubName);
				return pubName;
			} else {
			    logger.debug("OUT.pubName=ImportExportLoopback");
				return new String("ImportExportLoopback");
			}
		} else {
		    logger.debug("OUT.pubName=error");
			return new String("error");
		}

		
	}
	
	
}
