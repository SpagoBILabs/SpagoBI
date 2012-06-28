/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.services.common;

import it.eng.spagobi.services.security.exceptions.SecurityException;
import it.eng.spagobi.utilities.assertion.Assert;

import java.io.IOException;
import java.util.Enumeration;

import javax.portlet.PortletSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


import org.apache.log4j.Logger;

/**
 * This class contain the specific code for ENEL(distribuzione) SSO
 */
public class RequestHeaderSsoService implements SsoServiceInterface {

	static private final String USER_IDENTIFIER_REQUEST_ATTRIBUTE_NAME = "REMOTE_USER";
	
    static private Logger logger = Logger.getLogger(RequestHeaderSsoService.class);
    
    /**
     * Read user id.
     * 
     * @param session HttpSession
     * 
     * @return String
     */
    public String readUserIdentifier(HttpServletRequest request){
    	String user;
    	
    	logger.debug("IN");
    	
    	Assert.assertNotNull(request, "Input parameter [request] cannot be null");
    	
     	user = null;
    	
    	try {
    		
    		user = (String)request.getParameter(USER_IDENTIFIER_REQUEST_ATTRIBUTE_NAME);
    		logger.debug("Request parameter [" + USER_IDENTIFIER_REQUEST_ATTRIBUTE_NAME + "] is equal to [" + user + "]");
    	    
    		user = request.getHeader( USER_IDENTIFIER_REQUEST_ATTRIBUTE_NAME );
    		logger.debug("Request header [" + USER_IDENTIFIER_REQUEST_ATTRIBUTE_NAME + "] is equal to [" + user + "]");
        		
    		user = request.getRemoteUser();
    		logger.debug("Remote user is equal to [" + user + "]");
        	
    		user = (String)request.getAttribute(USER_IDENTIFIER_REQUEST_ATTRIBUTE_NAME);
    		logger.debug("Request attribute [" + USER_IDENTIFIER_REQUEST_ATTRIBUTE_NAME + "] is equal to [" + user + "]");
    	    
    		
    		if( user != null ) {
    			
    			if( user.lastIndexOf('@') != -1 ) {
        			user = user.substring(0, user.lastIndexOf('@') );
        		}
    			
    			user = user.toUpperCase();
    			logger.debug("Incoming request come from the autenthicated user [" + user + "]");
    		} else {
    			// if "Proxy-Remote-User" is null dump all header in the request just for debug purpose
    			logger.debug("Impossible to read  header [" + USER_IDENTIFIER_REQUEST_ATTRIBUTE_NAME + "] from request");
    			Enumeration headerNames = request.getHeaderNames();
    			while(headerNames.hasMoreElements()) {
    				String headerName = (String)headerNames.nextElement();
    				logger.debug("Request header [" + headerName + "] is equal to [" + request.getHeader(headerName) + "]");
    			}
    			
    			logger.debug("Incoming request come from a user not yet authenticated");
    		}

    	} catch(Throwable t) {
    		// fail fast
    		throw new RuntimeException("An unpredicted error occurred while reading user identifier", t);
    	} finally {
    		logger.debug("OUT");
    	}
    	
		return user;
    }
    
    /**
     * Read user id.
     * 
     * @param session PortletSession
     * 
     * @return String
     */
    public String readUserIdentifier(PortletSession session){
    	logger.debug("NOT Implemented");
    	return "";
    }
    
    /**
     * Get a new ticket.
     * 
     * @param session HttpSession
     * @param filterReceipt String
     * 
     * @return String
     * 
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public String readTicket(HttpSession session) throws IOException{
	    return "NA";
    }

    /**
     * This method verify the ticket.
     * 
     * @param ticket String, ticket to validate
     * @param userId String, user id
     * @param validateUrl String
     * @param validateService String
     * 
     * @return String
     * 
     * @throws SecurityException the security exception
     */
    public void validateTicket(String ticket, String userId)throws SecurityException {
    	
    }
    
    public static void main(String[] s) {
    	String user = "risorse\\AE11716";
    	if( user.lastIndexOf('\\') != -1 ) {
			user = user.substring(user.lastIndexOf('\\') + 1);
		}
    	System.out.println(user);
    }

}
