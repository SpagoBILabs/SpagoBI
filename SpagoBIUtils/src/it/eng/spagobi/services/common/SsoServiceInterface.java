/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2008 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

**/
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
     * @param session Http Session
     * @return
     */
    String readUserIdentifier(HttpServletRequest session);
    
    
    /**
     * 
     * @param session Portlet Session
     * @return
     */
    String readUserIdentifier(PortletSession session);
}
