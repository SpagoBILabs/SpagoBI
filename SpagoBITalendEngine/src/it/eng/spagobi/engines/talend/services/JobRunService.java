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

import org.apache.log4j.Logger;

import it.eng.spagobi.engines.talend.TalendEngine;
import it.eng.spagobi.engines.talend.exception.ContextNotFoundException;
import it.eng.spagobi.engines.talend.exception.JobExecutionException;
import it.eng.spagobi.engines.talend.exception.JobNotFoundException;
import it.eng.spagobi.engines.talend.runtime.Job;
import it.eng.spagobi.engines.talend.runtime.RuntimeRepository;
import it.eng.spagobi.utilities.engines.AbstractEngineStartServlet;
import it.eng.spagobi.utilities.engines.EngineStartServletIOManager;
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;

public class JobRunService extends AbstractEngineStartServlet {
	
	public static final String JS_FILE_ZIP = "JS_File";
	public static final String JS_EXT_ZIP = ".zip";	
	
	private static final long serialVersionUID = 1L;
	
	private static transient Logger logger = Logger.getLogger(JobRunService.class);
	
	
	public void doService( EngineStartServletIOManager servletIOManager ) throws SpagoBIEngineException {
		
		RuntimeRepository runtimeRepository;
		Job job;

		logger.debug("IN");
		
		try {		
			
			//servletIOManager.auditServiceStartEvent();
				
			super.doService(servletIOManager);
				
			job = new Job( servletIOManager.getTemplateAsSourceBean() );			
			runtimeRepository = TalendEngine.getRuntimeRepository();
			
			try {
				runtimeRepository.runJob(job, servletIOManager.getEnv());
			} catch (JobNotFoundException ex) {
				logger.error(ex.getMessage());

				throw new SpagoBIEngineException("Job not found",
						"job.not.existing");
		
			} catch (ContextNotFoundException ex) {
				logger.error(ex.getMessage(), ex);
				
				throw new SpagoBIEngineException("Context script not found",
						"context.script.not.existing");
			
			} catch(JobExecutionException ex) {
				logger.error(ex.getMessage(), ex);
				
				throw new SpagoBIEngineException("Job execution error",
						"job.exectuion.error");
				
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				
				throw new SpagoBIEngineException("Job execution error",
						"job.exectuion.error");
			}

			
			servletIOManager.tryToWriteBackToClient("etl.process.started");
			
		} finally {
			logger.debug("OUT");
		}
	}
}
