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
package it.eng.spagobi.services.event.service;

import it.eng.spagobi.engines.drivers.handlers.IRolesHandler;
import it.eng.spagobi.events.EventsManager;
import it.eng.spagobi.services.common.AbstractServiceImpl;
import it.eng.spagobi.services.security.exceptions.SecurityException;

import java.util.List;

import org.apache.log4j.Logger;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

public class EventServiceImpl extends AbstractServiceImpl {
    static private Logger logger = Logger.getLogger(EventServiceImpl.class);

    /**
     * Fire event.
     * 
     * @param token the token
     * @param user the user
     * @param description the description
     * @param parameters the parameters
     * @param rolesHandler the roles handler
     * @param presentationHandler the presentation handler
     * 
     * @return the string
     */
    public String fireEvent(String token, String user, String description,
	    String parameters, String rolesHandler, String presentationHandler) {
	logger.debug("IN");
	Monitor monitor =MonitorFactory.start("spagobi.service.event.fireEvent");
	try {
	    validateTicket(token, user);
	    return fireEvent(user, description, parameters,rolesHandler, presentationHandler);
	} catch (SecurityException e) {
	    logger.error("SecurityException", e);
	    return null;
	} finally {
	    monitor.stop();
	    logger.debug("OUT");
	}	

    }

    private String fireEvent(String user, String description,
	    String parameters, String rolesHandler, String presentationHandler) {
	logger.debug("IN");
	String returnValue = null;

	try {
	    if (user != null) {

		IRolesHandler rolesHandlerClass = (IRolesHandler) Class.forName(rolesHandler).newInstance();
		List roles = rolesHandlerClass.calculateRoles(parameters);
		Integer id = EventsManager.getInstance().registerEvent(user,description, parameters, roles, presentationHandler);
		returnValue = id.toString();
		logger.debug("Service executed succesfully");
	    } else {
		logger.warn("User is NULL");
	    }
	    return returnValue;
	} catch (Exception e) {
		logger.error("TalendRolesHandler not found",e);

	}finally{
	    logger.debug("OUT");
	}
	return null;
    }

}
