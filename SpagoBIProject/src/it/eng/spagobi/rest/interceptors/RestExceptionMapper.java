/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.rest.interceptors;

import java.util.HashMap;

import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.utilities.AuditLogUtilities;
import it.eng.spagobi.services.exceptions.ExceptionUtilities;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;
import org.jboss.resteasy.spi.HttpRequest;

/**
 * 
 * @author Alberto Ghedin (alberto.ghedin@eng.it)
 *
 * Updates the audit log for the services that throw exceptions
 *
 */

@Provider
public class RestExceptionMapper implements ExceptionMapper<Throwable>
{

	static private Logger logger = Logger.getLogger(RestExceptionMapper.class);
	
	@Context
	private HttpRequest request;
	@Context
	private HttpServletRequest servletRequest;
	
	public Response toResponse(Throwable e) {
		logger.debug("RestExceptionMapper:toResponse IN");
		String ex = "Service Exception";
		try {
			String serviceUrl = InterceptorUtilities.getServiceUrl(request);
			HashMap<String,String> parameters = InterceptorUtilities.getRequestParameters(request, servletRequest);
			ex = ExceptionUtilities.serializeException(e.getMessage(),null);
			parameters.put("Exception", ex);
					
			UserProfile profile = (UserProfile) servletRequest.getSession().getAttribute(IEngUserProfile.ENG_USER_PROFILE);
				
			String actionCode = "[Service:"+ serviceUrl+"]";
			String result ="";

			AuditLogUtilities.updateAudit(servletRequest, profile, actionCode,	parameters, "ERR");
		} catch (Exception e2) {
			String serviceUrl = InterceptorUtilities.getServiceUrl(request);
			logger.error("Error in the service "+serviceUrl);
		}

		
		Response response =  Response.status(500).entity(ex).build();
		logger.debug("RestExceptionMapper:toResponse OUT");
		
		return response;
	}
	
	



}
