/**
Copyright (C) 2004 - 2011, Engineering Ingegneria Informatica s.p.a.
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
package it.eng.spagobi.commons.filters;

import it.eng.spagobi.commons.utilities.GeneralUtilities;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

/**
 * This filter is in charge of checking if the session has expired on SpagoBI core.
 * If the session has expired and there is no request to open a new session,
 * call is forwarded to configured (in spagobi.xml) session expired URL.
 * This filter is required when using CAS: if the SpagoBI session has expired, 
 * the request will not be correctly processed with a new clean session. 
 * Moreover, if the CAS ticket is not valid, the call is redirected to CAS login page 
 * and Ajax requests will not be able to handle the resulting HTML page.
 * Therefore this filter must be put just before the CAS filter.
 * 
 * @author Davide Zerbetto (davide.zerbetto@eng.it)
 *
 */
public class SpagoBICoreCheckSessionFilter implements Filter {

	public static final String NEW_SESSION = "NEW_SESSION";
	
	private static transient Logger logger = Logger.getLogger(SpagoBICoreCheckSessionFilter.class);

    public void init(FilterConfig config) throws ServletException {
    	// do nothing
    }
	
    public void destroy() {
    	// do nothing
    }
    
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
    	throws IOException, ServletException {
    	
    	logger.debug("IN");
    	
    	try {
    		
			if (request instanceof HttpServletRequest) {
				HttpServletRequest httpRequest = (HttpServletRequest) request;
				HttpSession session = httpRequest.getSession(false);
				boolean isValidSession = session != null;
				boolean isRequiredNewSession = false; // for those requests that require a new session anyway, 
													  // do not forward to session expired url
				String newSessionRequestAttr = httpRequest.getParameter(NEW_SESSION);
	        	isRequiredNewSession = newSessionRequestAttr != null && newSessionRequestAttr.equalsIgnoreCase("TRUE");
	        	boolean isRequestedSessionIdValid = httpRequest.isRequestedSessionIdValid();
				if (!isValidSession && !isRequestedSessionIdValid && !isRequiredNewSession) {
					// session has expired
					logger.debug("Session has expired!!");
					String sessionExpiredUrl = GeneralUtilities.getSessionExpiredURL();
					if (sessionExpiredUrl == null || sessionExpiredUrl.trim().equals("")) {
						logger.warn("Session expired URL not set!!! check engine-config.xml configuration");
					} else {
						logger.debug("Forwarding to " + sessionExpiredUrl);
						httpRequest.getRequestDispatcher(sessionExpiredUrl).forward(request, response);
						return;
					}
				}
			}
			
			chain.doFilter(request, response);
			
	    } catch(Throwable t) {
	    	logger.error("--------------------------------------------------------------------------------");
		    logger.error("EngineCheckSessionFilter" + ":doFilter ServletException!!",t); 
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
    
}
