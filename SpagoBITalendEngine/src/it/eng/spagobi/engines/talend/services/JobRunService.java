/**
Copyright (c) 2005-2008, Engineering Ingegneria Informatica s.p.a.
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
