/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2009 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.engines.qbe.services.initializers;

import java.util.Locale;

import org.apache.log4j.Logger;

import it.eng.spago.base.SourceBean;
import it.eng.spago.presentation.Publisher;
import it.eng.spagobi.commons.presentation.DynamicPublisher;
import it.eng.spagobi.utilities.engines.AbstractEngineStartAction;
import it.eng.spagobi.utilities.engines.SpagoBIEngineStartupException;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class FormEngineTemplateBuilder extends AbstractEngineStartAction {	
	
	// INPUT PARAMETERS
	private final static String PARAM_MODALITY = "MODALITY";
	
	// OUTPUT PARAMETERS
	public static final String LANGUAGE = "LANGUAGE";
	public static final String COUNTRY = "COUNTRY";
	
	// SESSION PARAMETRES	


	
	/** Logger component. */
    private static transient Logger logger = Logger.getLogger(FormEngineStartAction.class);
    
    public static final String ENGINE_NAME = "SpagoBIFormEngine";
		
    public void service(SourceBean serviceRequest, SourceBean serviceResponse) {
    	
    	Locale locale;
    	
    	
    	logger.debug("IN");
       
    	try {
    		setEngineName(ENGINE_NAME);
			super.service(serviceRequest, serviceResponse);			
			
			locale = this.getLocale();
					
			setAttribute(LANGUAGE, locale.getLanguage());
			setAttribute(COUNTRY, locale.getCountry());
			
			String publisherName = "NEW_FORM_ENGINE_TEMPLATE_BUILD_ACTION_PUBLISHER";
			
			String modality = this.getAttributeAsString(PARAM_MODALITY);
			logger.debug("Input " + PARAM_MODALITY + " parameter is " + modality);
			if (modality != null && modality.trim().equalsIgnoreCase("EDIT")) {
				// edit template
				publisherName = "EDIT_FORM_ENGINE_TEMPLATE_BUILD_ACTION_PUBLISHER";
			}
			
			serviceResponse.setAttribute(DynamicPublisher.PUBLISHER_NAME, publisherName);
			
		} catch (Throwable e) {
			SpagoBIEngineStartupException serviceException = null;
			
			if(e instanceof SpagoBIEngineStartupException) {
				serviceException = (SpagoBIEngineStartupException)e;
			} else {
				Throwable rootException = e;
				while(rootException.getCause() != null) {
					rootException = rootException.getCause();
				}
				String str = rootException.getMessage()!=null? rootException.getMessage(): rootException.getClass().getName();
				String message = "An unpredicted error occurred while executing " + getEngineName() + " service."
								 + "\nThe root cause of the error is: " + str;
				
				serviceException = new SpagoBIEngineStartupException(getEngineName(), message, e);
			}
			
			throw serviceException;
		} finally {
			logger.debug("OUT");
		}		

		
	}    
}
