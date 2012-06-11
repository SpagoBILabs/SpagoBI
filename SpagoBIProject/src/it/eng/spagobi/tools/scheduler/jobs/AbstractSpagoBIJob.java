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

import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.tenant.Tenant;
import it.eng.spagobi.tenant.TenantManager;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 
 * @author Davide Zerbetto (davide.zerbetto@eng.it)
 *
 */
public abstract class AbstractSpagoBIJob {

	static private Logger logger = Logger.getLogger(AbstractSpagoBIJob.class);	
	
	protected void setTenant(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		logger.debug("IN");
		JobDetail jobDetail = jobExecutionContext.getJobDetail();
		Tenant tenant;
		try {
			tenant = DAOFactory.getSchedulerDAO().findTenant(jobDetail);
		} catch (Throwable t) {
			logger.error("Cannot retrieve tenant for job " + jobDetail.toString(), t);
			throw new SpagoBIRuntimeException("Cannot retrieve tenant for job " + jobDetail.toString(), t);
		}
		logger.debug("Tenant : " + tenant);
		TenantManager.setTenant(tenant);
		logger.debug("OUT");
	}
	
	protected void unsetTenant() {
		logger.debug("IN");
		TenantManager.unset();
		logger.debug("OUT");
	}
	
}
