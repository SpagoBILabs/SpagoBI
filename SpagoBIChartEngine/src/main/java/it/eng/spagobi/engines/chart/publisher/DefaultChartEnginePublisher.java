/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.chart.publisher;

import it.eng.spago.base.RequestContainer;
import it.eng.spago.base.ResponseContainer;
import it.eng.spago.presentation.PublisherDispatcherIFace;

public class DefaultChartEnginePublisher implements PublisherDispatcherIFace {

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
	public String getPublisherName(RequestContainer request, ResponseContainer response) {
		
		/*
		EXTCHART_START_ACTION_PUBLISHER
		D3CHART_START_ACTION_PUBLISHER
		*/	
		if(response.getServiceResponse().getAttribute("ENGINE") != null){
			
			String engine = (String) response.getServiceResponse().getAttribute("ENGINE");
			return engine + "_START_ACTION_PUBLISHER";
			
//			if(response.getServiceResponse().getAttribute("ENGINE").toString().contains("D3CHART"))
//				return new String("CHART_ENGINE_D3_START_ACTION_PUBLISHER");
//			else
//				return new String("CHART_ENGINE_EXTJS_START_ACTION_PUBLISHER");				
		}
		else
			return new String("error");

//		EMFErrorHandler errorHandler = responseContainer.getErrorHandler();				
//		if (errorHandler.isOKBySeverity(EMFErrorSeverity.ERROR))
//			return new String("ViewSnapshot");
//		else
//			return new String("error");
		
	}

}
