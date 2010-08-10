/**
Copyright (c) 2005-2008, Engineering Ingegneria Informatica s.p.a.
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this list of 
      conditions and the following disclaimer.
      
 * Redistributions in binary form must reproduce the above copyright notice, this list of 
      conditions and the following disclaimer in the documentation and/or other materials 
      provided with the distribution.
      
 * Neither the name of the Engineering Ingegneria Informatica s.p.a. nor the names of its contributors may
      be used to endorse or promote products derived from this software without specific
      prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND 
CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, 
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE

 **/
package it.eng.spagobi.utilities.filters;

import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.services.common.EnginConf;
import it.eng.spagobi.services.common.SsoServiceFactory;
import it.eng.spagobi.services.common.SsoServiceInterface;
import it.eng.spagobi.services.proxy.SecurityServiceProxy;
import it.eng.spagobi.services.security.exceptions.SecurityException;
import it.eng.spagobi.utilities.callbacks.audit.AuditAccessUtils;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

public class SpagoBIAccessFilter implements Filter {

	
	public final String AUDIT_ID_PARAM_NAME = "SPAGOBI_AUDIT_ID";
	public final String DOCUMENT_ID_PARAM_NAME = "document";

	public final String IS_BACKEND_ATTR_NAME= "isBackend";
	
	
	private static final String EXECUTION_ID = "SBI_EXECUTION_ID";
	
	
	private static transient Logger logger = Logger.getLogger(SpagoBIAccessFilter.class);


    public void init(FilterConfig config) throws ServletException {
    	// do nothing
    }
	
    public void destroy() {
    	// do nothing
    }
    
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
    	throws IOException, ServletException {
	
    	String auditId;
    	String userId=null;
    	String documentId;
    	String executionId;
    	IEngUserProfile profile = null;
    	String requestUrl;
    	
    	logger.debug("IN");
    	
    	try {
    		FilterIOManager ioManager = new FilterIOManager(request, response);
    		
    		 
		    documentId = (String) request.getParameter(DOCUMENT_ID_PARAM_NAME);
		    logger.info("Filter documentId  from request:" + documentId);		    
		    
			auditId = request.getParameter( AUDIT_ID_PARAM_NAME );
			logger.debug("Filter auditId from request::" + auditId);
			
			executionId = (String)request.getParameter(EXECUTION_ID);
			logger.debug("Filter executionId from request::" + executionId);
			
			userId = request.getParameter( SsoServiceInterface.USER_ID );
			logger.debug("Filter userId from request::" + userId);		   
		   
			
			if (request instanceof HttpServletRequest) {
			    
				
				
				HttpServletRequest httpRequest = (HttpServletRequest) request;
				requestUrl = httpRequest.getRequestURL().toString();
				logger.info("requestUrl: " + requestUrl);
						
				ioManager.initConetxtManager();			    
		
				ioManager.setInSession(DOCUMENT_ID_PARAM_NAME, documentId);
				ioManager.setInSession(IS_BACKEND_ATTR_NAME, "false");
				ioManager.contextManager.set(DOCUMENT_ID_PARAM_NAME, documentId);
				ioManager.contextManager.set(IS_BACKEND_ATTR_NAME, "false");
			    
				
				boolean isBackend = false;		
			    if (requestUrl.endsWith("BackEnd")) {		
					String passTicket = (String) request.getParameter(SpagoBIConstants.PASS_TICKET);
					if (passTicket != null && passTicket.equalsIgnoreCase(EnginConf.getInstance().getPass())) {
					    // if a request is coming from SpagoBI context
					    isBackend = true;
					    profile=UserProfile.createSchedulerUserProfile();
					    ioManager.setInSession(IS_BACKEND_ATTR_NAME, "true");
					    ioManager.contextManager.set(IS_BACKEND_ATTR_NAME, "true");
					    
					    if (userId!=null && UserProfile.isSchedulerUser(userId)){
					    	
				    		ioManager.setInSession(IEngUserProfile.ENG_USER_PROFILE, UserProfile.createSchedulerUserProfile());
				    		ioManager.contextManager.set(IEngUserProfile.ENG_USER_PROFILE, UserProfile.createSchedulerUserProfile());
				    		logger.info("IS a Scheduler Request ...");
				    		
					    }else if(userId!=null && UserProfile.isWorkflowUser(userId)){
					    	
					    	ioManager.setInSession(IEngUserProfile.ENG_USER_PROFILE, UserProfile.createWorkFlowUserProfile());
				    		ioManager.contextManager.set(IEngUserProfile.ENG_USER_PROFILE, UserProfile.createWorkFlowUserProfile());
				    		logger.info("IS a Workflow Request ...");
				    		
					    }else{
					    	logger.info("IS a backEnd Request ...");
					    }
					} else {
					    logger.warn("PassTicked is NULL in BackEnd call");
					    throw new ServletException();
					}
			    }else {
			    	userId = getUserWithSSO(httpRequest);
			    }

			    String spagobiContext = request.getParameter(SpagoBIConstants.SBI_CONTEXT);
			    String spagoUrl = request.getParameter(SpagoBIConstants.SBI_HOST);
			    if (spagobiContext != null) {
					logger.debug("spagobiContext:" + spagobiContext);
					ioManager.setInSession(SpagoBIConstants.SBI_CONTEXT, spagobiContext);
					ioManager.contextManager.set(SpagoBIConstants.SBI_CONTEXT, spagobiContext);
			    } else {
			    	logger.warn("spagobiContext is null.");
			    }
			    
			    if (spagoUrl != null) {
					logger.debug("spagoUrl:" + spagoUrl);
					ioManager.setInSession(SpagoBIConstants.SBI_HOST, spagoUrl);
					ioManager.contextManager.set(SpagoBIConstants.SBI_HOST, spagoUrl);
			    } else {
			    	logger.warn("spagoUrl is null.");
			    }
				
			
			    
			    if(userId != null) {
			    		try {
			    			// this is not correct. profile in session can come also from a concurrent execution 
						    profile = (IEngUserProfile) ioManager.getFromSession(IEngUserProfile.ENG_USER_PROFILE);
						    if (profile == null || !profile.getUserUniqueIdentifier().toString().equals(userId)) {
								SecurityServiceProxy proxy = new SecurityServiceProxy(userId, ioManager.getSession());
								profile = proxy.getUserProfile();
								if (profile!=null){
								ioManager.setInSession(IEngUserProfile.ENG_USER_PROFILE, profile);
								ioManager.setInSession("userId", profile.getUserUniqueIdentifier());
								ioManager.contextManager.set(IEngUserProfile.ENG_USER_PROFILE, profile);
								ioManager.contextManager.set("userId", profile.getUserUniqueIdentifier());
								}else {
									logger.error("ERROR WHILE GETTING USER PROFILE!!!!!!!!!!!");
								}
						    } else {
						    	logger.debug("Found user profile in session");
						    	// replicate anyway the profile in this execution context. Even if the profile can come from
						    	// a different concurrent execution at least we have somethings that can be consumed by engines
						    	ioManager.contextManager.set(IEngUserProfile.ENG_USER_PROFILE, profile);
						    }
						} catch (SecurityException e) {
						    logger.error("SecurityException while reeding user profile", e);
						    throw new ServletException("Message: " + e.getMessage() + "; Cause: " + (e.getCause()!=null?e.getCause().getMessage(): "none"));
						}
			    }
			    
			    
			    if (auditId != null) {
					AuditAccessUtils auditAccessUtils = (AuditAccessUtils) ioManager.getSession().getAttribute("SPAGOBI_AUDIT_UTILS");
					if (auditAccessUtils == null) {
					    auditAccessUtils = new AuditAccessUtils(auditId);
					    ioManager.setInSession("SPAGOBI_AUDIT_UTILS", auditAccessUtils);
					    ioManager.contextManager.set("SPAGOBI_AUDIT_UTILS", auditAccessUtils);
					} else {
					    auditAccessUtils.addAuditId(auditId);
					}
			    }
			}
			
			List list = ioManager.contextManager.getKeys();
			
			chain.doFilter(request, response);
			
    } catch(Throwable t) {
    	logger.error("--------------------------------------------------------------------------------");
	    logger.error("SpagoBIAccessFilter" + ":doFilter ServletException!!",t); 
		logger.error(" msg: [" + t.getMessage() + "]"); 
		Throwable z = t.getCause(); 
		if(z != null) {
			logger.error("-----------------------------");
			logger.error("ROOT CAUSE:");
			logger.error("-----------------------------"); 
			logger.error(" msg: ["+ z.getMessage() + "]"); 
			logger.error(" stacktrace:");
		}
		t.printStackTrace(); 
    	throw new ServletException(t);
	} finally {
		logger.debug("OUT");
	}
	
	
    }


	private String getUserWithSSO(HttpServletRequest request) throws ServletException {
		logger.debug("IN");
		SsoServiceInterface userProxy = SsoServiceFactory.createProxyService();
		String ssoUserIdentifier = userProxy.readUserIdentifier(request);
		logger.debug("OUT. got ssoUserId from IProxyService=" + ssoUserIdentifier);
		return ssoUserIdentifier;
    }

    private String checkUserWithSSO(String userId, HttpServletRequest request) throws ServletException {
		logger.debug("IN");
		SsoServiceInterface userProxy = SsoServiceFactory.createProxyService();
	    String ssoUserIdentifier = userProxy.readUserIdentifier(request);
	    logger.debug("got ssoUserId from IProxyService=" + ssoUserIdentifier);
	    logger.debug("OU: got userId from IProxyService=" + userId);
	    return ssoUserIdentifier;
    }
    
   


}
