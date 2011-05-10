/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2009 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.kpi.ou.service;

import it.eng.spagobi.commons.services.AbstractSpagoBIAction;
import it.eng.spagobi.kpi.ou.util.OrganizationalUnitSynchronizer;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.service.JSONAcknowledge;

import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * 
 * @author Zerbetto Davide
 *
 */
public class SynchronizeOUsAction extends AbstractSpagoBIAction {
	
	public static final String SERVICE_NAME = "SYNCHRONIZE_OUS_ACTION";
	
	// logger component
	private static Logger logger = Logger.getLogger(SynchronizeOUsAction.class);
	
	public void doService() {
		logger.debug("IN");
		try {
			try {
				OrganizationalUnitSynchronizer sinc = new OrganizationalUnitSynchronizer();
				sinc.synchronize();
			} catch (Throwable t) {
				throw new SpagoBIServiceException(SERVICE_NAME, "Error while synchronizating organizational units: " + t.getMessage(), t);
			}
			try {
				writeBackToClient( new JSONAcknowledge() );
			} catch (IOException e) {
				throw new SpagoBIServiceException(SERVICE_NAME, "Impossible to write back the responce to the client", e);
			}
		} finally {
			logger.debug("OUT");
		}
	}

}
