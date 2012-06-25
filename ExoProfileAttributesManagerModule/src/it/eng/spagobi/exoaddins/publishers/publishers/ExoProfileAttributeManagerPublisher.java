/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.exoaddins.publishers;

import org.apache.log4j.Logger;

import it.eng.spago.base.RequestContainer;
import it.eng.spago.base.ResponseContainer;
import it.eng.spago.base.SourceBean;
import it.eng.spago.error.EMFErrorHandler;
import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.presentation.PublisherDispatcherIFace;
import it.eng.spagobi.commons.constants.SpagoBIConstants;

public class ExoProfileAttributeManagerPublisher implements PublisherDispatcherIFace {

	static private Logger logger = Logger.getLogger(ExoProfileAttributeManagerPublisher.class);
	
	public String getPublisherName(RequestContainer requestContainer, ResponseContainer responseContainer) {
		logger.debug("IN");
		EMFErrorHandler errorHandler = responseContainer.getErrorHandler();
		if(!errorHandler.isOKBySeverity(EMFErrorSeverity.ERROR)) {
			return "error";
		}
		SourceBean serviceResponse = responseContainer.getServiceResponse();
		SourceBean moduleResponse = (SourceBean)serviceResponse.getAttribute("ExoProfileAttributeManagerModule");
		if(moduleResponse == null) {
			logger.error("Module response null");
			EMFUserError error = new EMFUserError(EMFErrorSeverity.ERROR, 10 );
			errorHandler.addError(error);
			logger.debug("publisherName: error");
			logger.debug("OUT");
			return "error";
		}
		String publisherName = (String)moduleResponse.getAttribute(SpagoBIConstants.PUBLISHER_NAME );
		logger.debug("publisherName: " + publisherName);
		if((publisherName==null) || (publisherName.trim().equals("")) ){
			return "ExoProfileAttributeManagerHome";
		} else {
			logger.debug("OUT");
			return publisherName;
		}
	}
}
