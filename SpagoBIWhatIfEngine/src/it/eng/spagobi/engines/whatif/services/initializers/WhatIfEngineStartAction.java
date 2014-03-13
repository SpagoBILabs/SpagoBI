/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
/**
 * @author Alberto Ghedin (alberto.ghedin@eng.it)
 */
package it.eng.spagobi.engines.whatif.services.initializers;

import it.eng.spagobi.engines.whatif.WhatIfEngine;
import it.eng.spagobi.engines.whatif.WhatIfEngineInstance;
import it.eng.spagobi.engines.whatif.services.common.AbstractWhatIfEngineService;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.apache.log4j.Logger;


@Path("/start")
public class WhatIfEngineStartAction extends AbstractWhatIfEngineService {
	

	// INPUT PARAMETERS
	
	// OUTPUT PARAMETERS
	public static final String LANGUAGE = "LANGUAGE";
	public static final String COUNTRY = "COUNTRY";
	
	// SESSION PARAMETRES	
	public static final String ENGINE_INSTANCE = EngineConstants.ENGINE_INSTANCE;
	
	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(WhatIfEngineStartAction.class);
    
    private static final String ENGINE_NAME = "SpagoBIConsoleEngine";
    private static final String REQUEST_DISPATCHER_URL = "/WEB-INF/jsp/whatIf.jsp";
	
    @GET
    @Produces("text/html")
	public void startAction(@Context HttpServletResponse response)  {
		logger.debug("IN");		
		Locale locale;
		WhatIfEngineInstance whatIfEngineInstance = null;
//			
			logger.debug("Creating engine instance ...");
			
			try {
				whatIfEngineInstance = WhatIfEngine.createInstance( "", getEnv() );
			} catch(Throwable t) {
				logger.error("Error starting the engine what if: error while generating the engine instance.", t);
				throw new SpagoBIEngineRuntimeException("Error starting the engine what if: error while generating the engine instance.", t);
			}
			logger.debug("Engine instance succesfully created");
			
			locale = (Locale)whatIfEngineInstance.getEnv().get(EngineConstants.ENV_LOCALE);
			
			getExecutionSession().setAttributeInSession( ENGINE_INSTANCE, whatIfEngineInstance);		


			try {
				servletRequest.getRequestDispatcher(REQUEST_DISPATCHER_URL).forward(servletRequest, response);
			} catch (Exception e) {
				logger.error("Error starting the engine what if: error while forwarding the execution to the jsp "+REQUEST_DISPATCHER_URL, e);
				throw new SpagoBIEngineRuntimeException("Error starting the engine what if: error while forwarding the execution to the jsp "+REQUEST_DISPATCHER_URL, e);
			}
	}
	
	 public Map getEnv() {
		 Map env = new HashMap();

		 it.eng.spagobi.tools.datasource.bo.DataSource ds = new it.eng.spagobi.tools.datasource.bo.DataSource();
		 ds.setUser("root");
		 ds.setPwd("root");
		 ds.setDriver("com.mysql.jdbc.Driver");
		 
	 
		 env.put(EngineConstants.ENV_DATASOURCE, ds);
//		 env.put(EngineConstants.ENV_USER_PROFILE, getUserProfile());
//		 env.put(EngineConstants.ENV_CONTENT_SERVICE_PROXY, getContentServiceProxy());
//		 env.put(EngineConstants.ENV_AUDIT_SERVICE_PROXY, getAuditServiceProxy() );
//		 env.put(EngineConstants.ENV_DATASET_PROXY, getDataSetServiceProxy());
//		 env.put(EngineConstants.ENV_DATASOURCE_PROXY, getDataSourceServiceProxy()); 
		 env.put(EngineConstants.ENV_LOCALE, Locale.ITALY); 

		 return env;
	 }
	 
	 

	
}