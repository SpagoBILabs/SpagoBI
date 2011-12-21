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
package it.eng.spagobi.engines.talend.services;

import java.util.Date;

import it.eng.spagobi.engines.talend.TalendEngine;
import it.eng.spagobi.utilities.engines.AbstractEngineStartServlet;
import it.eng.spagobi.utilities.engines.EngineStartServletIOManager;
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * @author Andrea Gioia
 *
 */
public class EngineInfoService extends AbstractEngineStartServlet {
	
	private static final String INFO_TYPE_PARAM_NAME = "infoType"; 
	private static final String INFO_TYPE_VERSION = "version"; 
	private static final String INFO_TYPE_COMPLIANCE_VERSION = "complianceVersion"; 
	private static final String INFO_TYPE_NAME = "name"; 
	
	private static final long serialVersionUID = 1L;
	
	private static transient Logger logger = Logger.getLogger(EngineInfoService.class);
	
	
	public void doService(EngineStartServletIOManager servletIOManager) throws SpagoBIEngineException {
		
		String infoType;
		String responseMessage;
		
		logger.debug("IN");
		
		try {	
				
			infoType = servletIOManager.getParameterAsString(INFO_TYPE_PARAM_NAME);
		
			if(INFO_TYPE_VERSION.equalsIgnoreCase( infoType )) {
				responseMessage = TalendEngine.getVersion().toString();
			} else if(INFO_TYPE_COMPLIANCE_VERSION.equalsIgnoreCase( infoType)) {
				responseMessage = TalendEngine.getVersion().getComplianceVersion();
			} else if (INFO_TYPE_NAME.equalsIgnoreCase( infoType )) {
				responseMessage = TalendEngine.getVersion().getFullName();
			} else {
				responseMessage = TalendEngine.getVersion().getInfo();
			}
			
			servletIOManager.tryToWriteBackToClient( responseMessage );
			
		} finally {
			logger.debug("OUT");
		}		
	}
	
	public void auditServiceStartEvent() {
		logger.info("EXECUTION_STARTED: " + new Date(System.currentTimeMillis()));
	}

	public void auditServiceErrorEvent(String msg) {
		logger.info("EXECUTION_FAILED: " + new Date(System.currentTimeMillis()));
	}

	public void auditServiceEndEvent() {
		logger.info("EXECUTION_PERFORMED: " + new Date(System.currentTimeMillis()));	
	}
	
	
}

