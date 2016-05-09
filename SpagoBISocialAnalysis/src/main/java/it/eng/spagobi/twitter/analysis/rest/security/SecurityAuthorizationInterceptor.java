/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.twitter.analysis.rest.security;

import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.services.rest.annotations.CheckFunctionalitiesParser;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;
import it.eng.spagobi.utilities.filters.FilterIOManager;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;
import org.jboss.resteasy.annotations.interception.Precedence;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.LoggableFailure;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;

import twitter4j.JSONArray;
import twitter4j.JSONObject;

/**
 * @author Giorgio Federici (giorgio.federici@eng.it)
 */

@Provider
@ServerInterceptor
@Precedence("SECURITY")
public class SecurityAuthorizationInterceptor implements PreProcessInterceptor {

	static private Logger logger = Logger.getLogger(SecurityAuthorizationInterceptor.class);

	@Context
	private HttpServletRequest servletRequest;

	/**
	 * Preprocess all the REST requests.
	 *
	 * Get the UserProfile from the session and checks if has the grants to execute the service
	 */
	@Override
	public ServerResponse preProcess(HttpRequest request, ResourceMethodInvoker resourceMethod) throws Failure, WebApplicationException {

		ServerResponse response;

		logger.trace("IN");

		response = null;
		try {

			String methodName = resourceMethod.getMethod().getName();

			logger.info("Receiving request from: " + servletRequest.getRemoteAddr());
			logger.info("Attempt to invoke method [" + methodName + "] on class [" + resourceMethod.getResourceClass().getName() + "]");

			// Functionalities annotation parser
			CheckFunctionalitiesParser checkFunctionalitiesParser = new CheckFunctionalitiesParser();

			// if the security annotation is not present on the method, this method is public
			boolean isPublicService = checkFunctionalitiesParser.isPublicService(resourceMethod.getMethod());

			if (!isPublicService) {

				// the method is not public, need to check if profile has the rights functionalities
				UserProfile profile = null;

				profile = (UserProfile) getUserProfileFromSession();

				if (profile == null) {
					// TODO check if the error can be processed by the client
					// throws unlogged user exception that will be managed by RestExcepionMapper
					logger.error("Error checking user profile calling the method [" + methodName + "]. Profile null ");
					throw new LoggableFailure(request.getUri().getRequestUri().getPath());
				}

				boolean authorized = false;
				try {
					// authorized = profile.isAbleToExecuteService(serviceUrl);
					authorized = checkFunctionalitiesParser.checkFunctionalitiesByAnnotation(resourceMethod.getMethod(), profile);
				} catch (Throwable e) {
					logger.debug("Error checking if the user [" + profile.getUserName() + "] has the rights to call the method [" + methodName + "] ", e);
					throw new SpagoBIRuntimeException("Error checking if the user [" + profile.getUserName() + "] has the rights to call the method ["
							+ methodName + "] ", e);
				}

				if (!authorized) {
					try {
						logger.error("The user don't have the rights to call the method [" + methodName + "]. Functionalities constraint violation ");
						return new ServerResponse(serializeException("not-enabled-to-call-service", null), 400, new Headers<Object>());
					} catch (Exception e) {
						throw new SpagoBIRuntimeException("Error checking if the user [" + profile.getUserName() + "] has the rights to call the service ["
								+ methodName + "]", e);
					}
				} else {
					logger.debug("The user [" + profile.getUserName() + "] is enabled to execute the method [" + methodName + "] ");
				}

			}

		} catch (Throwable t) {
			if (t instanceof SpagoBIRuntimeException) {
				// ok it's a known exception
			} else {
				new SpagoBIRuntimeException("An unexpected error occured while preprocessing service request", t);
			}
			String msg = t.getMessage();
			if (t.getCause() != null && t.getCause().getMessage() != null)
				msg += ": " + t.getCause().getMessage();
			response = new ServerResponse(msg, 400, new Headers<Object>());
		} finally {
			logger.trace("OUT");
		}

		return response;
	}

	// these methods should be abstract because are different in SpagoBI and in the engines. In the first case the object is
	// set and get from session in the second the object is set and get from a specific context withing session(in particular there should
	// be one different context for each distinct executions lunched by the same user on the same borwser.
	private IEngUserProfile getUserProfileFromSession() {
		IEngUserProfile engProfile = null;
		FilterIOManager ioManager = new FilterIOManager(servletRequest, null);
		ioManager.initConetxtManager();

		engProfile = (IEngUserProfile) servletRequest.getSession().getAttribute(IEngUserProfile.ENG_USER_PROFILE);
		if (engProfile == null) {
			engProfile = (IEngUserProfile) ioManager.getContextManager().get(IEngUserProfile.ENG_USER_PROFILE);
		}

		return engProfile;
	}

	private String serializeException(String message, String localizedMessage) {

		try {
			JSONArray ja = new JSONArray();
			JSONObject jo = new JSONObject();
			JSONObject je = new JSONObject();
			if (message != null) {
				jo.put("message", message);
			}
			if (localizedMessage != null) {
				jo.put("localizedMessage", localizedMessage);
			}
			ja.put(jo);
			je.put("errors", ja);
			return je.toString();
		} catch (Exception e) {
			throw new SpagoBIRuntimeException("Cannot fill response container", e);
		}
	}

}
