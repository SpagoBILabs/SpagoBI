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
package it.eng.spagobi.engines.weka.services.initializers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import org.apache.log4j.Logger;

import it.eng.spagobi.engines.weka.WekaEngine;
import it.eng.spagobi.engines.weka.WekaEngineInstance;
import it.eng.spagobi.engines.weka.runtime.RuntimeRepository;
import it.eng.spagobi.utilities.engines.AbstractEngineStartServlet;
import it.eng.spagobi.utilities.engines.EngineStartServletIOManager;
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

/**
 * Process weka execution requests and returns bytes of the filled
 * reports
 */
public class WekaEngineStartServlet extends AbstractEngineStartServlet {

	/**
	 * Logger component
	 */
	private static transient Logger logger = Logger.getLogger(WekaEngineStartServlet.class);
	
	public void doService( EngineStartServletIOManager servletIOManager ) throws SpagoBIEngineException {
		
		String responseMsg;
		WekaEngineInstance engineInstance;
		Map env;
		String template;
		
		
		logger.debug("IN");
		
		try {
			
			
			env = servletIOManager.getEnv();
			template = servletIOManager.getTemplateAsString();
					
			responseMsg = servletIOManager.getLocalizedMessage("weka.correct.execution");
			engineInstance = null;
					
			try {
				engineInstance = WekaEngine.createInstance(template, env);
				logger.debug("Engine instance succesfully created");	
			} catch (Exception e) {
				logger.error("Impossible to create engine instance", e);
				responseMsg = servletIOManager.getLocalizedMessage("an.unpredicted.error.occured");
			}
			
			if(engineInstance != null) {
				try {
					//engineInstance.run();
					RuntimeRepository rt = new RuntimeRepository();
					rt.runEngineInstance(engineInstance);
					logger.debug("Engine instance succesfully started");
				} catch (Exception e) {
					logger.error("Impossible to start-up engine instance", e);
					responseMsg = servletIOManager.getLocalizedMessage("an.unpredicted.error.occured");
				}
			}
	
			// prepare response
			StringBuffer buffer = new StringBuffer();
			buffer.append("<html>\n");
			buffer.append("<head><title>Service Response</title></head>\n");
			buffer.append("<body>");
			buffer
					.append("<p style=\"text-align:center;font-size:13pt;font-weight:bold;color:#000033;\">");
			buffer.append(responseMsg);
			buffer.append("</p>");
			buffer.append("</body>\n");
			buffer.append("</html>\n");
	
			servletIOManager.getResponse().setContentLength(buffer.length());
			servletIOManager.getResponse().setContentType("text/html");
			PrintWriter writer;
			
			// flush response
			try {
				writer = servletIOManager.getResponse().getWriter();
				writer.print(buffer.toString());
				writer.flush();
			} catch (IOException e) {
				throw new SpagoBIRuntimeException("Impossible to write back response to client", e);
			}
		
		} catch (Throwable t) {
			throw new SpagoBIRuntimeException("An unpredicted error occurred while executing engine", t);
		} finally {
			logger.info("OUT");
		}
		
	}
}
