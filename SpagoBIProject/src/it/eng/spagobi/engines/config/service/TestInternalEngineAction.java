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
package it.eng.spagobi.engines.config.service;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import it.eng.spago.base.SourceBean;
import it.eng.spago.dispatching.action.AbstractHttpAction;
import it.eng.spagobi.tools.datasource.service.TestConnectionAction;

public class TestInternalEngineAction extends AbstractHttpAction{
	
	static private Logger logger = Logger.getLogger(TestInternalEngineAction.class);
	
	public void service(SourceBean serviceRequest, SourceBean serviceResponse)
	throws Exception {
		
		logger.debug("IN");
		String message = null;
		freezeHttpResponse();
		HttpServletResponse httResponse = getHttpResponse();

    	String driverName = (String)serviceRequest.getAttribute("driverName");	
		String className = (String) serviceRequest.getAttribute("className");
		
		try {
			if(driverName!=null){
				Class.forName(driverName);
			}else if(className!=null){
				Class.forName(className);
			}else{
				message = "ClassNameError";
			}
			message = "sbi.connTestOk";
			
		} catch (ClassNotFoundException e) {
			 message = "ClassNameError";
			e.printStackTrace();
		}
		finally {
			httResponse.getOutputStream().write(message.getBytes());
			httResponse.getOutputStream().flush();
	    	logger.debug("OUT");  	
		}  	
	}

}
