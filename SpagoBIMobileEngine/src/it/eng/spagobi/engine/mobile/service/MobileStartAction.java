/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.
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
package it.eng.spagobi.engine.mobile.service;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.engine.mobile.MobileEngine;
import it.eng.spagobi.engine.mobile.MobileEngineInstance;
import it.eng.spagobi.services.proxy.DataSetServiceProxy;
import it.eng.spagobi.utilities.engines.AbstractEngineStartAction;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.engines.SpagoBIEngineStartupException;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;

public class MobileStartAction extends AbstractEngineStartAction {
	// OUTPUT PARAMETERS
	public static final String LANGUAGE = "LANGUAGE";
	public static final String COUNTRY = "COUNTRY";
	public static final String PROXY_DATASET = "PROXY_DATASET";
	
	// SESSION PARAMETRES	
	public static final String ENGINE_INSTANCE = EngineConstants.ENGINE_INSTANCE;
	
	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(MobileStartAction.class);
    
    private static final String ENGINE_NAME = "SpagoBIMobileEngine";

	public void service(SourceBean serviceRequest, SourceBean serviceResponse)  {
		logger.debug("IN");	
		Locale locale;
		DataSetServiceProxy proxyDS = null;
		MobileEngineInstance mobileEngineInstance = null;
		
		try {
			setEngineName(ENGINE_NAME);
			super.service(serviceRequest, serviceResponse);

						
			if(getAuditServiceProxy() != null) {
				logger.debug("Audit enabled: [TRUE]");
				getAuditServiceProxy().notifyServiceStartEvent();
			} else {
				logger.debug("Audit enabled: [FALSE]");
			}
			
			logger.debug("Creating engine instance ...");
			
			try {
				SourceBean templStr = getTemplateAsSourceBean();
				Map env = getEnv();
				mobileEngineInstance = MobileEngine.createInstance( templStr, env );
			} catch(Throwable t) {
				SpagoBIEngineStartupException serviceException;
				String msg = "Impossible to create engine instance for document [" + getDocumentId() + "].";
				Throwable rootException = t;
				while(rootException.getCause() != null) {
					rootException = rootException.getCause();
				}
				String str = rootException.getMessage()!=null? rootException.getMessage(): rootException.getClass().getName();
				msg += "\nThe root cause of the error is: " + str;
				serviceException = new SpagoBIEngineStartupException(ENGINE_NAME, msg, t);
				
				
				throw serviceException;
			}
			logger.debug("Engine instance succesfully created");
			
			locale = (Locale)mobileEngineInstance.getEnv().get(EngineConstants.ENV_LOCALE);
			mobileEngineInstance.getEnv().put(EngineConstants.ENV_DATASET_PROXY, getDataSetServiceProxy());
			
			//getSpagoBIHttpSessionContainer().set( EngineConstants.ENGINE_INSTANCE, mobileEngineInstance);
			setAttributeInSession( EngineConstants.ENGINE_INSTANCE, mobileEngineInstance);		
			setAttribute(ENGINE_INSTANCE, mobileEngineInstance);
			
			setAttribute(LANGUAGE, locale.getLanguage());
			setAttribute(COUNTRY, locale.getCountry());

			
		} catch (Exception e) {
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
