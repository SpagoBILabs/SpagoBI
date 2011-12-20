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

