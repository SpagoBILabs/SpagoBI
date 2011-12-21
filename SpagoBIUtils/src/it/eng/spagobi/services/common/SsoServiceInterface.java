/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */
package it.eng.spagobi.services.common;

import it.eng.spagobi.services.security.exceptions.SecurityException;

import java.io.IOException;

import javax.portlet.PortletSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 
 * Interface for read and validate a proxy ticket
 *
 */
public interface SsoServiceInterface {
	
	public static final String USER_ID="user_id";

    /**
     * 
     * @param ticket String
     * @param userId String
     * @throws SecurityException String
     */
    void validateTicket(String ticket, String userId) throws SecurityException;
    /**
     * 
     * @param session Http Session
     * @return String
     * @throws IOException
     */
    String readTicket(HttpSession session) throws IOException;
    /**
     * 
     * @param request Http request
     * @return
     */
    String readUserIdentifier(HttpServletRequest request);
    
    
    /**
     * 
     * @param session Portlet Session
     * @return
     */
    String readUserIdentifier(PortletSession session);
}
