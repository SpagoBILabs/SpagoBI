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
package it.eng.spagobi.tools.scheduler.jobs;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * TODO: create an initializer that look up for all job whose job class is equal to XExecuteBIDocumentJob
 * and replace it with ExecuteBIDocumentJob. The remove class ExecuteBIDocumentJob and rename 
 * XExecuteBIDocumentJob to ExecuteBIDocumentJob. NOTE: the old implementation of ExecuteBIDocumentJob has
 * been saved in CopyOfExecuteBIDocumentJob
 * 
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class ExecuteBIDocumentJob implements Job {

	private static Logger logger = Logger.getLogger(ExecuteBIDocumentJob.class);
	
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		logger.debug("IN");
		Job job = new XExecuteBIDocumentJob();
		job.execute(jobExecutionContext);
		logger.debug("OUT");
	}

}
