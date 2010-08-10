/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2008 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.tools.dataset.presentation;

import it.eng.spago.base.RequestContainer;
import it.eng.spago.base.ResponseContainer;
import it.eng.spago.base.SourceBean;
import it.eng.spago.error.EMFAbstractError;
import it.eng.spago.error.EMFErrorHandler;
import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.presentation.PublisherDispatcherIFace;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.utilities.GeneralUtilities;
import it.eng.spagobi.tools.dataset.service.DetailDataSetModule;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;


/**
 * Publishes the results of a detail request for a dataSet into the correct 
 * jsp page according to what contained into request. If Any errors occurred during the 
 * execution of the <code>DetailDataSetModule</code> class, the publisher
 * is able to call the error page with the error message caught before and put into 
 * the error handler. If the input information don't fall into any of the cases declared,
 * another error is generated. 
 * 
 */
public class DetailDataSetPublisher implements PublisherDispatcherIFace {
	static private Logger logger = Logger.getLogger(DetailDataSetPublisher.class);
	private SourceBean listTestLovMR 	= null;
	private SourceBean detailMR 	= null;
	public static final String DETAIL_DATA_SET_MODULE = "DetailDataSetModule";
	public static final String LIST_TEST_DATA_SET_MODULE = "ListTestDataSetModule";

	/**
	 * Given the request at input, gets the name of the reference publisher,driving
	 * the execution into the correct jsp page, or jsp error page, if any error occurred.
	 * 
	 * @param responseContainer The object containing all response information
	 * @param moduleName the module name
	 * 
	 * @return A string representing the name of the correct publisher, which will
	 * call the correct jsp reference.
	 */


	public SourceBean getModuleResponse(ResponseContainer responseContainer, String moduleName) {
		return (SourceBean) responseContainer.getServiceResponse().getAttribute(moduleName);
	}

	/**
	 * Gets the module responses.
	 * 
	 * @param responseContainer the response container
	 * 
	 * @return the module responses
	 */
	public void getModuleResponses(ResponseContainer responseContainer) {
		detailMR = getModuleResponse(responseContainer, DETAIL_DATA_SET_MODULE);
		listTestLovMR = getModuleResponse(responseContainer, LIST_TEST_DATA_SET_MODULE);
	}



	/* (non-Javadoc)
	 * @see it.eng.spago.presentation.PublisherDispatcherIFace#getPublisherName(it.eng.spago.base.RequestContainer, it.eng.spago.base.ResponseContainer)
	 */
	public String getPublisherName(RequestContainer requestContainer, ResponseContainer responseContainer) {
		logger.debug("IN");

		EMFErrorHandler errorHandler = responseContainer.getErrorHandler();

		// get the module response

		getModuleResponses(responseContainer);	

		// if the module response is null throws an error and return the name of the errors publisher

		if (noModuledResponse()) {
			logger.error("Module response null");
			EMFUserError error = new EMFUserError(EMFErrorSeverity.ERROR, 10 );
			errorHandler.addError(error);			
			return "error";
		}


		if(!errorHandler.isOK()) {
			logger.error("error handler contains errors");
			// if the error was while testing
			if(listTestLovMR!=null && listTestLovMR.getAttribute(DetailDataSetModule.DATASET)!=null){
				logger.error("errors while testing");
				return "detailDataSetTestResult";
			}
			else{
				// if the error was in detail				
				logger.info("errors from detail page"  );
				return "detailDataSet";

			}

		}

		// if there are some errors into the errorHandler (not validation errors), return the name for the errors publisher
		if(!errorHandler.isOKBySeverity(EMFErrorSeverity.ERROR)) {
			try{
				Collection list_errori=errorHandler.getErrors();
				for (Iterator iterator = list_errori.iterator(); iterator.hasNext();) {
					EMFUserError error = (EMFUserError)iterator.next();
					int code=error.getCode();
					if(code==9214){ // if it is not the error of wrong assigned parameter type or of not correct query
						return new String("error");	
					}
				}
			}
			catch (Exception e) {
				return new String("error");
			}

		}

		// check if the request want to do the test but he must fill profile attributes
		boolean fillProfAttr = false;
		Object profAttToFillList = getAttributeFromModuleResponse(detailMR, SpagoBIConstants.PROFILE_ATTRIBUTES_TO_FILL);
		if(profAttToFillList != null) {
			fillProfAttr = true;
		}


		boolean parametersToFill=false;
		List parameters=null;
		if(detailMR!=null){
			parameters=(List)detailMR.getAttribute("parameters");
			if(parameters!=null && parameters.size()>0){
				parametersToFill=true;
			}
		}

		boolean afterTest = false;
		Object testExecuted = getAttributeFromModuleResponse(listTestLovMR, DetailDataSetModule.TEST_EXECUTED);
		if(testExecuted != null) {
			afterTest = true;
		}




		// switch to correct publisher
		if (isLoop()) {
			return new String("detailDataSetLoop");
		} else if (afterTest) {
			return new String("detailDataSetTestResult");
		} 
		else if(parametersToFill) {
			return new String("detailDatasetFillParameters");
		} else {
			return new String("detailDataSet");
		}


	}

	private boolean noModuledResponse() {
		return (detailMR == null && listTestLovMR == null);
	}

	private Object getAttributeFromModuleResponse(SourceBean moduleResponse, String attributeName) {
		return ( (moduleResponse == null)? null: moduleResponse.getAttribute(attributeName));
	}

	private boolean isLoop() {
		return isAttrbuteDefinedInModuleResponse(detailMR, "loopback");
	}


	private boolean isAttrbuteDefinedInModuleResponse(SourceBean moduleResponse, String attributeName) {
		return (getAttributeFromModuleResponse(moduleResponse, attributeName) != null);
	}

}
