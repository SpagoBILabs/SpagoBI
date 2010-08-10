/**

Copyright 2005 Engineering Ingegneria Informatica S.p.A.

This file is part of SpagoBI.

SpagoBI is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
any later version.

SpagoBI is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Spago; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA

**/
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
