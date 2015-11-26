/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
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
