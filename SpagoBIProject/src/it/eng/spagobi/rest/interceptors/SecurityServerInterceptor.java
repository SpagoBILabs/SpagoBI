package it.eng.spagobi.rest.interceptors;

import it.eng.spago.error.EMFInternalError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.services.exceptions.ExceptionUtilities;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;

/**
 * The org.jboss.resteasy.spi.interception.PreProcessInterceptor runs after a JAX-RS resource 
 * method is found to invoke on, but before the actual invocation happens
 * 
 * @author Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
@Provider
@ServerInterceptor
public class SecurityServerInterceptor implements PreProcessInterceptor{

	static private Logger logger = Logger.getLogger(SecurityServerInterceptor.class);
	
	@Context
	private HttpServletRequest servletRequest;
	
	/**
	 * Preprocess all the REST requests..
	 * Get the UserProfile from the session and checks if has
	 * the grants to execute the service
	 */
	public ServerResponse preProcess(HttpRequest req, ResourceMethod arg1)	throws Failure, WebApplicationException {
		boolean isTheUserEnabled = false;
		logger.debug("SecurityServerInterceptor:preProcess IN");
		String serviceUrl = InterceptorUtilities.getServiceUrl(req);
		UserProfile profile = (UserProfile) servletRequest.getSession().getAttribute(IEngUserProfile.ENG_USER_PROFILE);
		
		logger.debug("Checking if the user ["+profile.getUserName()+"] has the rights to call the service ["+serviceUrl+"]");
		
		try {
			isTheUserEnabled = profile.isAbleToExecuteService(serviceUrl);
		} catch (EMFInternalError e) {
			logger.debug("Error checking if the user ["+profile.getUserName()+"] has the rights to call the service ["+serviceUrl+"]",e);
			throw new SpagoBIRuntimeException("Error checking if the user ["+profile.getUserName()+"] has the rights to call the service ["+serviceUrl+"]",e);
		}
		if(!isTheUserEnabled){
			logger.error("NOT ENABLED TO EXECUTE SERVICE-- The user ["+profile.getUserName()+"] is not enabled to execute the service ["+serviceUrl+"]");
			try {
				return new ServerResponse( ExceptionUtilities.serializeException("not-enabled-to-call-service",null),	400, new Headers<Object>());
			} catch (Exception e) {
				throw new SpagoBIRuntimeException("Error checking if the user ["+profile.getUserName()+"] has the rights to call the service ["+serviceUrl+"]",e);
			}
			
		}else{
			logger.debug("The user ["+profile.getUserName()+"] is enabled to execute the service ["+serviceUrl+"]");
		}
		logger.debug("SecurityServerInterceptor:preProcess OUT");
		return null;
	}
	




}
