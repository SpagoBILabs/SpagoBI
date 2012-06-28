/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.geo.commons.presentation;

import it.eng.spago.base.RequestContainer;
import it.eng.spago.base.ResponseContainer;
import it.eng.spago.base.SourceBean;
import it.eng.spago.presentation.PublisherDispatcherIFace;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.engines.IEngineInstance;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceExceptionHandler;

import org.apache.log4j.Logger;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */

public class DynamicPublisher implements PublisherDispatcherIFace {
	
	// SERVICE RESPONSE PARAMETERS
	public static String PUBLISHER_NAME = "PUBLISHER_NAME";
	
	// DEFAULT VALUES
	//...
	
	// Logger component
    public static transient Logger logger = Logger.getLogger(DynamicPublisher.class);
	
	/**
	 * Class constructor.
	 */
	public DynamicPublisher() {
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
		SourceBean serviceResponse = null;
		String publisherName = null;
		
		logger.debug("IN");
		
		try {				
			serviceResponse = (SourceBean)response.getServiceResponse();
			Assert.assertNotNull(serviceResponse, "Response provided by service cannot be null");
			logger.debug("Service response: \n" + serviceResponse.toString());
			
			publisherName = (String) serviceResponse.getAttribute(PUBLISHER_NAME);
			Assert.assertNotNull(serviceResponse, "Response parameter [" + PUBLISHER_NAME + "] provided by service cannot be null");
			logger.debug("Publisher name: \n" + publisherName);
			
			// TODO check in config if such a publisher exists. Otherwise log a worn message and use the default publisher
		} catch (Throwable t) {
			throw SpagoBIEngineServiceExceptionHandler.getInstance().getWrappedException(this.getPublisherName(request, response), 
					(IEngineInstance)request.getSessionContainer().getAttribute( EngineConstants.ENGINE_INSTANCE ), t);
		} finally {
			// no resources need to be released
		}
		logger.debug("OUT");
		
		return publisherName;		
	}
}
