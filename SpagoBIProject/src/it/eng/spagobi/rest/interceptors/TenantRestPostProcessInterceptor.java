/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.rest.interceptors;

import it.eng.spagobi.tenant.TenantManager;

import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.interception.PostProcessInterceptor;

/**
 * The org.jboss.resteasy.spi.interception.PreProcessInterceptor runs after a JAX-RS resource 
 * method is found to invoke on, but before the actual invocation happens
 * 
 * @author Davide Zerbetto (davide.zerbetto@eng.it)
 *
 */
@Provider
@ServerInterceptor
public class TenantRestPostProcessInterceptor implements PostProcessInterceptor {

	private static Logger logger = Logger
			.getLogger(TenantRestPostProcessInterceptor.class);

	/**
	 * Post-processes all the REST requests. Remove tenant's information from thread
	 */
	public void postProcess(ServerResponse response){
		logger.debug("IN");
		TenantManager.unset();
		logger.debug("OUT");
	}

}