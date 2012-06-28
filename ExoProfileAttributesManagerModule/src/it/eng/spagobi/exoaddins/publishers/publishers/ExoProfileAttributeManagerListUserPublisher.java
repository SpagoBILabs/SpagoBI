/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.exoaddins.publishers;

import org.apache.log4j.Logger;

import it.eng.spago.base.RequestContainer;
import it.eng.spago.base.ResponseContainer;
import it.eng.spago.error.EMFErrorHandler;
import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.presentation.PublisherDispatcherIFace;


public class ExoProfileAttributeManagerListUserPublisher implements PublisherDispatcherIFace {
	static private Logger logger = Logger.getLogger(ExoProfileAttributeManagerListUserPublisher.class);

	public String getPublisherName(RequestContainer requestContainer, ResponseContainer responseContainer) {
		logger.debug("IN");
		//SourceBean serviceRequest = requestContainer.getServiceRequest();
		EMFErrorHandler errorHandler = responseContainer.getErrorHandler();
		
		if (errorHandler.isOKBySeverity(EMFErrorSeverity.ERROR)){
			logger.debug("PublisherName: ExoProfileAttributeManagerListUser");
			logger.debug("OUT");
			return new String("ExoProfileAttributeManagerListUser");
		}
		else{
			logger.debug("PublisherName: error");
			logger.debug("OUT");
			return new String("error");
		}
	}

}
