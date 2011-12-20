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
package it.eng.spagobi.behaviouralmodel.check.presentation;

import it.eng.spago.base.RequestContainer;
import it.eng.spago.base.ResponseContainer;
import it.eng.spago.base.SourceBean;
import it.eng.spago.error.EMFErrorHandler;
import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.presentation.PublisherDispatcherIFace;
import it.eng.spagobi.commons.utilities.GeneralUtilities;
import it.eng.spagobi.commons.utilities.SpagoBITracer;

public class CheckLinksDispatcherPublisher implements PublisherDispatcherIFace {
	
	/**
	 * Class constructor.
	 */
	public CheckLinksDispatcherPublisher() {
		super();

	}
	
	/**
	 * Given the request at input, gets the name of the reference publisher,driving
	 * the execution into the correct jsp page, or jsp error page, if any error occurred.
	 * 
	 * @param request The request container object containing all request information
	 * @param response The response container object containing all response information
	 * 
	 * @return A string representing the name of the correct publisher, which will
	 * call the correct jsp reference.
	 */
	public String getPublisherName(RequestContainer request, ResponseContainer response) {
		
		SourceBean moduleResponse = (SourceBean)response.getServiceResponse().getAttribute("CheckLinksModule");
				
		// SourceBean serviceRequest = requestContainer.getServiceRequest();
		EMFErrorHandler errorHandler = response.getErrorHandler();
		
		// if there are errors and they are only validation errors
		if(!errorHandler.isOK()) {
			if(GeneralUtilities.isErrorHandlerContainingOnlyValidationError(errorHandler)) {
				return getPublisherName(moduleResponse);
			}
		}
		
		if (errorHandler.isOKBySeverity(EMFErrorSeverity.ERROR))
			return getPublisherName(moduleResponse);
		else
			return new String("error");
		
		
	}
	
	/**
	 * Gets the publisher name.
	 * 
	 * @param moduleResponse the module response
	 * 
	 * @return the publisher name
	 */
	public String getPublisherName(SourceBean moduleResponse) {
		if(moduleResponse == null) {			
			return "SERVICE_ERROR_PUBLISHER";
		}
		
		String publisherName = (String) moduleResponse.getAttribute("PUBLISHER_NAME");
		
		SpagoBITracer.debug("", "DynamicPublisher","service",
				" PUBLISHER_NAME = "  + publisherName);
		
		if (publisherName != null) {
			return publisherName;
		} else {
			return "SERVICE_ERROR_PUBLISHER";
		}
	}
}

