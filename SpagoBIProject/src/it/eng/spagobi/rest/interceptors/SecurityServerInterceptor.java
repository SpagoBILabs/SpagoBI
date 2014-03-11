package it.eng.spagobi.rest.interceptors;

import it.eng.spago.base.Constants;
import it.eng.spago.base.RequestContainer;
import it.eng.spago.base.SessionContainer;
import it.eng.spago.error.EMFInternalError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.utilities.GeneralUtilities;
import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.security.ExternalServiceController;
import it.eng.spagobi.services.common.SsoServiceFactory;
import it.eng.spagobi.services.common.SsoServiceInterface;
import it.eng.spagobi.services.exceptions.ExceptionUtilities;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

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

		logger.trace("IN");
		
		String serviceUrl = InterceptorUtilities.getServiceUrl(req);
		
		//Check for Services that can be invoked externally without user login in SpagoBI
		ExternalServiceController externalServiceController = ExternalServiceController.getInstance();
		boolean isExternalService = externalServiceController.isExternalService(serviceUrl);

		if (!isExternalService){
			//Other checks are required
			boolean authenticated = isUserAuthenticated();
			if(!authenticated){
				//throws unlogged user exception that will be managed by RestExcepionMapper
			    logger.info("User not logged");
			    throw new LoggableFailure( req.getUri().getRequestUri().getPath() );
			}
			
			UserProfile profile = (UserProfile) getUserProfile();
			
			boolean authorized = false;
			try {
				authorized = profile.isAbleToExecuteService(serviceUrl);
			} catch (EMFInternalError e) {
				logger.debug("Error checking if the user ["+profile.getUserName()+"] has the rights to call the service ["+serviceUrl+"]",e);
				throw new SpagoBIRuntimeException("Error checking if the user ["+profile.getUserName()+"] has the rights to call the service ["+serviceUrl+"]",e);
			}
			
			if(!authorized){
				try {
					return new ServerResponse( ExceptionUtilities.serializeException("not-enabled-to-call-service", null),	400, new Headers<Object>());
				} catch (Exception e) {
					throw new SpagoBIRuntimeException("Error checking if the user ["+profile.getUserName()+"] has the rights to call the service ["+serviceUrl+"]",e);
				}				
			}else{
				logger.debug("The user ["+profile.getUserName()+"] is enabled to execute the service ["+serviceUrl+"]");
			}
		}
		
		
		logger.trace("OUT");
		
		return null;
	}
	
	private boolean isUserAuthenticated(){
		
		boolean authenticated = true;

		IEngUserProfile engProfile = getUserProfile();
		if (engProfile == null) {
			logger.debug("User profile not found in session, creating a new one and putting in session....");
			// in case the profile does not exist, creates a new one
				
			String userId = null;
			try {
				userId = getUserIdentifier();
			} catch (Exception e) {
				logger.debug("User identifier not found.");
			}
								
			logger.debug("User id = " + userId);
			if (StringUtilities.isNotEmpty(userId)) {	
				try {
					engProfile = GeneralUtilities.createNewUserProfile(userId);
				} catch (Exception e) {
					e.printStackTrace();
				}	
				setUserProfile(engProfile);
			}
		}
		
		// in case the user is not specified, does nothing
		if (engProfile == null) {
		    authenticated = false;
		}
		
		return authenticated;
	}
	
	private IEngUserProfile getUserProfile() {
		IEngUserProfile engProfile = (IEngUserProfile) getSessionContainer().getAttribute(IEngUserProfile.ENG_USER_PROFILE);
		return engProfile;
	}
	
	private void setUserProfile(IEngUserProfile engProfile) {
		getSessionContainer().setAttribute(IEngUserProfile.ENG_USER_PROFILE, engProfile);
	}
	
	private SessionContainer getSessionContainer() {
		HttpSession session = servletRequest.getSession();
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
		
		return permanentSession;
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

	private String getUserIdentifier() throws Exception {
		logger.debug("IN");
		String userId = null;
		try {
			SsoServiceInterface userProxy = SsoServiceFactory.createProxyService();
			userId = userProxy.readUserIdentifier(servletRequest);
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
