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
package it.eng.spagobi.tools.scheduler.dao;

import it.eng.spagobi.tenant.Tenant;
import it.eng.spagobi.tools.scheduler.bo.Job;
import it.eng.spagobi.tools.scheduler.bo.Trigger;

import java.util.List;

import org.quartz.JobDetail;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public interface ISchedulerDAO {
	
	void setTenant(String tenant);
	
	Tenant findTenant(JobDetail jobDetail);
	
	boolean jobGroupExists(String jobGroupName) ;
	boolean jobExists(String jobGroupName, String jobName);
	List<String> getJobGroupNames() ;
	
	/**
	 * @return all jobs. If there are no jobs already stored it returns an empty list
	 */
	List<Job> loadJobs() ;
	
	/**
	 * @param jobGroupNames the list of group names in which to look for jobs. It can be empty but it cannot be null. 
	 * If it is an empty list an empty list of jobs will be returned.
	 * 
	 * @return the jobs contained in the specified groups. Never returns null. If there are no jobs in the specified groups
	 * it returns an empty list of jobs
	 */
	List<Job> loadJobs(List<String> jobGroupNames);
	
	/**
	 * @param jobGroupName the name of the group in which to look for jobs. It it cannot be empty.
	 * 
	 * @return the jobs contained in the specified group. Never returns null. If there are no jobs in the specified group
	 * it returns an empty list of jobs
	 */
	List<Job> loadJobs(String jobGroupName);
	
	/**
	 * @param jobGroupName the name of the group in which to look up. It it cannot be empty.
	 * @param jobName the name of the job to load. It it cannot be empty.
	 * 
	 * @return the job if exists a job named jobName in group jobGroupName. null otherwise
	 */
	Job loadJob(String jobGroupName, String jobName);
	void deleteJob(String jobName, String jobGroupName);
	void insertJob(Job spagobiJob);
	
	boolean triggerExists(Trigger spagobiTrigger) ;
	Trigger loadTrigger(String triggerGroupName, String triggerName);
	List<Trigger> loadTriggers(String jobGroupName, String jobName);	
	void deleteTrigger(String triggerName, String triggerGroupName);	
	boolean saveTrigger(Trigger spagobiTrigger);	
	void insertTrigger(Trigger spagobiTrigger);	
	void updateTrigger(Trigger spagobiTrigger);

}
