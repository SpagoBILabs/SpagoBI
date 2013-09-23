package it.eng.spagobi.rest.interceptors;

import it.eng.spago.base.Constants;
import it.eng.spago.base.RequestContainer;
import it.eng.spago.base.SessionContainer;
import it.eng.spago.error.EMFInternalError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.utilities.GeneralUtilities;
import it.eng.spagobi.security.ExternalServiceController;
import it.eng.spagobi.services.common.SsoServiceFactory;
import it.eng.spagobi.services.common.SsoServiceInterface;
import it.eng.spagobi.services.exceptions.ExceptionUtilities;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;
import org.jboss.resteasy.annotations.interception.Precedence;
import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.LoggableFailure;
import org.jboss.resteasy.spi.interception.AcceptedByMethod;
import org.jboss.resteasy.spi.interception.MessageBodyWriterContext;
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
@Precedence("SECURITY")
public class SecurityServerInterceptor implements PreProcessInterceptor, AcceptedByMethod {

	static private Logger logger = Logger.getLogger(SecurityServerInterceptor.class);
	
	@Context
	private HttpServletRequest servletRequest;
	
	/**
	 * Preprocess all the REST requests..
	 * Get the UserProfile from the session and checks if has
	 * the grants to execute the service
	 */
	public ServerResponse preProcess(HttpRequest req, ResourceMethod arg1)throws Failure, WebApplicationException {

		HttpSession session = servletRequest.getSession();
		
		String serviceUrl = InterceptorUtilities.getServiceUrl(req);
		
		//Check for Services that can be invoked externally without user login in SpagoBI
		ExternalServiceController externalServiceController = ExternalServiceController.getInstance();
		boolean isExternalService = externalServiceController.isExternalService(serviceUrl);

		if (!isExternalService){
			//Other checks are required
			boolean res = checkUserAuthentication(session, req.getUri().getRequestUri().getPath());
			if(!res){
				return null;
			}
			
			boolean isTheUserEnabled = false;
			logger.debug("SecurityServerInterceptor:preProcess IN");
			//String serviceUrl = InterceptorUtilities.getServiceUrl(req);
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
		}
		
		
		logger.debug("SecurityServerInterceptor:preProcess OUT");
		return null;
	}
	
	private boolean checkUserAuthentication(HttpSession session, String uri){
		
		boolean ret = true;

		RequestContainer requestContainer = (RequestContainer) session.getAttribute(Constants.REQUEST_CONTAINER);
		if (requestContainer == null) {
			// RequestContainer does not exists yet (maybe it is the
			// first call to SpagoBI)
			// initializing SpagoBI objects (user profile object must
			// be put into PermanentContainer)
			requestContainer = new RequestContainer();
			SessionContainer sessionContainer = new SessionContainer(true);
			requestContainer.setSessionContainer(sessionContainer);
			session.setAttribute(Constants.REQUEST_CONTAINER, requestContainer);
		}
		SessionContainer sessionContainer = requestContainer.getSessionContainer();
		SessionContainer permanentSession = sessionContainer.getPermanentContainer();
		IEngUserProfile engProfile = (IEngUserProfile) permanentSession.getAttribute(IEngUserProfile.ENG_USER_PROFILE);
		if (engProfile == null) {

				logger.debug("User profile not found in session, creating a new one and putting in session....");
				// in case the profile does not exist, creates a new one
				
				String userId = null;
				try {
					userId = findUserId(servletRequest);
				} catch (Exception e) {
					logger.debug("User identifier not found.");
				}
				
				// in case the user is not specified, does nothing
				if (userId == null || userId.trim().equals("")) {
					logger.debug("User identifier not found.");
					//login page redirect
				      {
				         //throws unlogged user exception that will be managed by RestExcepionMapper
				    	  ret = false;
				    	  logger.info("User not logged");
				    	  throw new LoggableFailure(uri);
				    	  
				      }
				}
				logger.debug("User id = " + userId);
				if (userId!=null) {
	
					try {
						engProfile = GeneralUtilities.createNewUserProfile(userId);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
					permanentSession.setAttribute(IEngUserProfile.ENG_USER_PROFILE, engProfile);

				}

		}
		return ret;
	}
	
	/**
	 * Finds the user identifier from http request or from SSO system (by the
	 * http request in input). Use the SsoServiceInterface for read the userId
	 * in all cases, if SSO is disabled use FakeSsoService. Check
	 * spagobi_sso.xml
	 * 
	 * @param httpRequest
	 *            The http request
	 * 
	 * @return the current user unique identified
	 * 
	 * @throws Exception
	 *             in case the SSO is enabled and the user identifier specified
	 *             on http request is different from the SSO detected one.
	 */

	private static String findUserId(HttpServletRequest request) throws Exception {
		logger.debug("IN");
		String userId = null;
		try {
			SsoServiceInterface userProxy = SsoServiceFactory.createProxyService();
			userId = userProxy.readUserIdentifier(request);
		} finally {
			logger.debug("OUT");
		}
		return userId;
	}

	public boolean accept(Class arg0, Method arg1) {
		// TODO Auto-generated method stub
		return true;
	}



}
