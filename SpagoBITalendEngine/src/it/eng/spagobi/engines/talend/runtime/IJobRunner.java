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
package it.eng.spagobi.engines.talend.runtime;

import it.eng.spagobi.engines.talend.exception.ContextNotFoundException;
import it.eng.spagobi.engines.talend.exception.JobExecutionException;
import it.eng.spagobi.engines.talend.exception.JobNotFoundException;
import it.eng.spagobi.utilities.callbacks.audit.AuditAccessUtils;

import java.util.Map;

import javax.servlet.http.HttpSession;

/**
 * @author Andrea Gioia
 *
 */
public interface IJobRunner {
	
	
	 /**
 	 * Run.
 	 * 
 	 * @param job the job
 	 * @param parameters the parameters
 	 * @param auditAccessUtils the audit access utils
 	 * @param auditId the audit id
 	 * 
 	 * @throws JobNotFoundException the job not found exception
 	 * @throws ContextNotFoundException the context not found exception
 	 * @throws JobExecutionException the job execution exception
 	 */
 	public abstract void run(Job job, Map env) 
	 	throws JobNotFoundException, ContextNotFoundException, JobExecutionException;

}
