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
package it.eng.spagobi.tools.scheduler.dao.quartz;

import it.eng.qbe.datasource.configuration.dao.DAOException;
import it.eng.spagobi.commons.dao.SpagoBIDOAException;
import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.tools.scheduler.bo.Job;
import it.eng.spagobi.tools.scheduler.bo.Trigger;
import it.eng.spagobi.tools.scheduler.dao.ISchedulerDAO;
import it.eng.spagobi.utilities.assertion.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class QuarzSchedulerDAO implements ISchedulerDAO {
	
	private Scheduler scheduler;
	
	static private Logger logger = Logger.getLogger(QuarzSchedulerDAO.class);
	
	public QuarzSchedulerDAO() {
		logger.debug("IN");
		try {
			scheduler = StdSchedulerFactory.getDefaultScheduler(); 
		} catch(Throwable t) {
			throw new SpagoBIDOAException("Impossible to access to the default quartz scheduler", t);
		} finally {
			logger.debug("OUT");
		}
	}
	
	public boolean jobGroupExists(String jobGroupName) {
		Assert.assertNotNull(jobGroupName, "Input parameter [jobGroupName] cannot be null");
		List<String> jobGroupNames = getJobGroupNames();
		return jobGroupNames.contains(jobGroupName);
	}
	
	public boolean jobExists(String jobGroupName, String jobName) {
		boolean exists = false;
		try {
			Assert.assertNotNull(jobGroupName, "Input parameter [jobGroupName] cannot be null");
			Assert.assertNotNull(jobGroupName, "Input parameter [jobName] cannot be null");
			
			if( jobGroupExists(jobGroupName) == false ) return false;
			String[] jobNames = scheduler.getJobNames(jobGroupName);
			for(int i = 0; i < jobNames.length; i++) {
				if( jobName.equalsIgnoreCase(jobNames[i]) ){
					exists = true;
					break;
				}
			}
		} catch(Throwable t) {
			throw new SpagoBIDOAException("An unexpected error occured while checking for the existence of job [" + jobName + "] in group [" + jobGroupName +"]", t);
		} finally {
			logger.debug("OUT");
		}	
		return exists;
	}
	
	public List<String> getJobGroupNames() {
		List<String> jobGroupNames;
		
		logger.debug("IN");
		
		jobGroupNames = new ArrayList<String>();
		try {
			String[] names = scheduler.getJobGroupNames();
			List<String> l = Arrays.asList(names);
			if(l != null) jobGroupNames.addAll(l);
		} catch(Throwable t) {
			throw new SpagoBIDOAException("An unexpected error occured while loading job group names", t);
		} finally {
			logger.debug("OUT");
		}
		
		return jobGroupNames;
	}
	
	/**
	 * @return all jobs. If there are no jobs already stored it returns an empty list
	 */
	public List<Job> loadJobs() {
		List<Job> jobs;
		
		logger.debug("IN");
		
		jobs = new ArrayList<Job>();
		
		try {
			List<String> jobGroupNames = getJobGroupNames();
			jobs = loadJobs(jobGroupNames);
		} catch(Throwable t) {
			throw new SpagoBIDOAException("An unexpected error occured while loading jobs", t);
		} finally {
			logger.debug("OUT");
		}
		
		return jobs;
	}
	
	/**
	 * @param jobGroupNames the list of group names in which to look for jobs. It can be empty but it cannot be null. 
	 * If it is an empty list an empty list of jobs will be returned.
	 * 
	 * @return the jobs contained in the specified groups. Never returns null. If there are no jobs in the specified groups
	 * it returns an empty list of jobs
	 */
	public List<Job> loadJobs(List<String> jobGroupNames) {
		List<Job> jobs;
		
		logger.debug("IN");
		
		jobs = new ArrayList<Job>();
		
		try {
			Assert.assertNotNull(jobGroupNames, "Input parameter [jobGroupNames] cannot be null");
			
			for (String jobGroupName : jobGroupNames) {
				List<Job> jobDetailsInGroup = loadJobs(jobGroupName);
				jobs.addAll( jobDetailsInGroup );
			}			
		} catch(Throwable t) {
			throw new SpagoBIDOAException("An unexpected error occured while loading jobs", t);
		} finally {
			logger.debug("OUT");
		}
		
		return jobs;
	}
	
	/**
	 * @param jobGroupName the name of the group in which to look for jobs. It it cannot be empty.
	 * 
	 * @return the jobs contained in the specified group. Never returns null. If there are no jobs in the specified group
	 * it returns an empty list of jobs
	 */
	public List<Job> loadJobs(String jobGroupName) {
		List<Job> jobs;
		
		logger.debug("IN");
		
		jobs = new ArrayList<Job>();
		
		try {
			Assert.assertTrue(StringUtilities.isNotEmpty(jobGroupName), "Input parameter [jobGroupName] cannot be empty");
			
			String[] jobNames = scheduler.getJobNames(jobGroupName);
			if (jobNames != null) {
				logger.debug("Job group [" + jobGroupName + "] contains [" + jobNames.length + "] job(s)");
				for (int j = 0; j < jobNames.length; j++) {
					Job job = loadJob(jobNames[j], jobGroupName);
					if(job != null) {
						jobs.add(job);
					} else {
						logger.warn("Impossible to load job [" + jobNames[j] + "] from group [" + jobGroupName + "]");
					}
				}
			} else {
				logger.debug("Job group [" + jobGroupName + "] does not contain jobs");
			}		
		} catch(Throwable t) {
			throw new SpagoBIDOAException("An unexpected error occured while loading jobs of group [" + jobGroupName + "]", t);
		} finally {
			logger.debug("OUT");
		}
		
		return jobs;
	}
	
	/**
	 * @param jobGroupName the name of the group in which to look up. It it cannot be empty.
	 * @param jobName the name of the job to load. It it cannot be empty.
	 * 
	 * @return the job if exists a job named jobName in group jobGroupName. null otherwise
	 */
	public Job loadJob(String jobGroupName, String jobName) {
		Job job;
		
		logger.debug("IN");
		
		job = null;
		try {
			Assert.assertTrue(StringUtilities.isNotEmpty(jobGroupName), "Input parameter [jobGroupName] cannot be empty");
			Assert.assertTrue(StringUtilities.isNotEmpty(jobName), "Input parameter [jobName] cannot be empty");
			
			JobDetail jobDetail = scheduler.getJobDetail(jobName, jobGroupName);
			if(jobDetail != null){
				job = QuartzNativeObjectsConverter.convertJobFromNativeObject(jobDetail);
				logger.debug("Job [" + jobName + "] succesfully loaded from group [" + jobGroupName + "]");
			} else {
				logger.debug("Job [" + jobName + "] not found in group [" + jobGroupName + "]");
			}
		} catch(Throwable t) {
			throw new SpagoBIDOAException("An unexpected error occured while loading job [" + jobName + "] in group [" + jobGroupName + "]", t);
		} finally {
			logger.debug("OUT");
		}
		
		return job;
	}
	
	
	public void deleteJob(String jobName, String jobGroupName) {
		logger.debug("IN");
		
		try {
			// TODO delete trigger associated to the job first (?)
			scheduler.deleteJob(jobName, jobGroupName);
		} catch(Throwable t) {
			throw new SpagoBIDOAException("An unexpected error occured while deleting job [" + jobName + "] of job group [" + jobGroupName + "]", t);
		} finally {
			logger.debug("OUT");
		}
	}
	
	public void insertJob(Job spagobiJob) {
		
		logger.debug("IN");
		
		try {
			Assert.assertNotNull(spagobiJob, "Input parameter [spagobiJob] cannot be null");
			JobDetail quartzJob = QuartzNativeObjectsConverter.convertJobToNativeObject(spagobiJob);
			if(quartzJob.getDescription() == null) quartzJob.setDescription("");
			if(quartzJob.getGroup() == null) quartzJob.setGroup(Scheduler.DEFAULT_GROUP);
			
			scheduler.addJob(quartzJob, true);
		} catch(Throwable t) {
			throw new SpagoBIDOAException("An unexpected error occured while inserting job [" + spagobiJob + "]", t);
		} finally {
			logger.debug("OUT");
		}
	}
	
	
	
	
	
	
	public boolean triggerExists(Trigger spagobiTrigger) {
		boolean exists;
		
		logger.debug("IN");
		
		exists = false;
		try {
			Assert.assertNotNull(spagobiTrigger, "Input parameter [trigger] cannot be null");
			Assert.assertNotNull(spagobiTrigger.getJob(), "The attribute [job] of trigger cannot be null");
			Assert.assertTrue(StringUtilities.isNotEmpty(spagobiTrigger.getJob().getName()), "The attribute [name] of the job associated to the trigger cannot be empty]");
			Assert.assertTrue(StringUtilities.isNotEmpty(spagobiTrigger.getJob().getGroupName()), "The attribute [groupName] of the job associated to the trigger cannot be empty]");
			
			List<Trigger> jobTriggers = loadTriggers( spagobiTrigger.getJob().getName(), spagobiTrigger.getJob().getGroupName() );
	        for(Trigger jobTrigger : jobTriggers) {
	           	if(jobTrigger.getName().equals(spagobiTrigger.getName())) {
	           		exists = true;
	           		break;
	           	}
	        }
		} catch(Throwable t) {
			throw new SpagoBIDOAException("An unexpected error occured while checking for the existence of trigger [" + spagobiTrigger.getName() + "] of trigger group [" + spagobiTrigger.getGroupName() + "]", t);
		} finally {
			logger.debug("OUT");
		}	
		
		return exists;
	}
	
	public Trigger loadTrigger(String triggerGroupName, String triggerName) {
		Trigger spagobiTrigger;
		
		logger.debug("IN");
		
		spagobiTrigger = null;
		try {
			Assert.assertTrue(StringUtilities.isNotEmpty(triggerGroupName), "Input parameter [triggerGroupName] cannot be null");
			Assert.assertTrue(StringUtilities.isNotEmpty(triggerName), "Input parameter [triggerName] cannot be null");
			
			org.quartz.Trigger quartzTrigger = scheduler.getTrigger(triggerName, triggerGroupName);
			if(quartzTrigger != null) {
				spagobiTrigger = QuartzNativeObjectsConverter.convertTriggerFromNativeObject(quartzTrigger);
			}
		} catch(Throwable t) {
			throw new SpagoBIDOAException("An unexpected error occured while checking for the existence of trigger [" + spagobiTrigger.getName() + "] of trigger group [" + spagobiTrigger.getGroupName() + "]", t);
		} finally {
			logger.debug("OUT");
		}	
		
		return spagobiTrigger;
	}
	
	public List<Trigger> loadTriggers(String jobGroupName, String jobName) {
		List<Trigger> spagobiTriggers;
		
		logger.debug("IN");
		
		spagobiTriggers = new ArrayList<Trigger>();
		try {
			org.quartz.Trigger[] t = scheduler.getTriggersOfJob(jobName, jobGroupName);
			List<org.quartz.Trigger> quartzTriggers = Arrays.asList(t);
			if(quartzTriggers != null) {
				for(org.quartz.Trigger quartzTrigger: quartzTriggers) {
					Trigger spagobiTrigger = QuartzNativeObjectsConverter.convertTriggerFromNativeObject(quartzTrigger);
					spagobiTriggers.add(spagobiTrigger);
				}
			}
		} catch(Throwable t) {
			throw new SpagoBIDOAException("An unexpected error occured while loading trigger s of job [" + jobName + "] in job group [" + jobGroupName + "]", t);
		} finally {
			logger.debug("OUT");
		}
		
		return spagobiTriggers;
	}
	
	public void deleteTrigger(String triggerName, String triggerGroupName) {
		logger.debug("IN");
		
		try {
			Assert.assertTrue(StringUtilities.isNotEmpty(triggerName), "Input parameter [triggerName] cannot be empty");
			Assert.assertTrue(StringUtilities.isNotEmpty(triggerGroupName), "Input parameter [triggerGroupName] cannot be empty");
			
			scheduler.unscheduleJob(triggerName, triggerGroupName);
		} catch(Throwable t) {
			throw new SpagoBIDOAException("An unexpected error occured while deleting trigger [" + triggerName + "] of trigger group [" + triggerGroupName + "]", t);
		} finally {
			logger.debug("OUT");
		}		
	}
	
	public boolean saveTrigger(Trigger spagobiTrigger) {
		boolean overwrite;
		
		logger.debug("IN");
		
		try {
			Assert.assertNotNull(spagobiTrigger, "Input parameter [spagobiTrigger] cannot be null");
			
			org.quartz.Trigger quartzTrigger = QuartzNativeObjectsConverter.convertTriggerToNativeObject(spagobiTrigger);
			if(quartzTrigger.getGroup() == null) quartzTrigger.setGroup(Scheduler.DEFAULT_GROUP);
			if(quartzTrigger.getJobGroup() == null) quartzTrigger.setJobGroup(Scheduler.DEFAULT_GROUP);
				
			if( triggerExists(spagobiTrigger) ) {
				scheduler.rescheduleJob(quartzTrigger.getName(), quartzTrigger.getGroup(), quartzTrigger);
				overwrite = true;
			} else {
				scheduler.scheduleJob(quartzTrigger);
				overwrite = false;
			}
			
		} catch(DAOException t) {
			throw t;
		} catch(Throwable t) {
			throw new SpagoBIDOAException("An unexpected error occured while inserting trigger[" + spagobiTrigger + "]", t);
		} finally {
			logger.debug("OUT");
		}	
		
		return overwrite;
	}
	
	public void insertTrigger(Trigger spagobiTrigger) {
		logger.debug("IN");
		
		try {
			Assert.assertNotNull(spagobiTrigger, "Input parameter [spagobiTrigger] cannot be null");
			if( triggerExists(spagobiTrigger) ) {
				throw new DAOException("Trigger [" + spagobiTrigger + "] already exists");
			}
			org.quartz.Trigger quartzTrigger = QuartzNativeObjectsConverter.convertTriggerToNativeObject(spagobiTrigger);
			scheduler.scheduleJob(quartzTrigger);
		} catch(DAOException t) {
			throw t;
		} catch(Throwable t) {
			throw new SpagoBIDOAException("An unexpected error occured while inserting trigger[" + spagobiTrigger + "]", t);
		} finally {
			logger.debug("OUT");
		}	
	}
	
	public void updateTrigger(Trigger spagobiTrigger) {
		logger.debug("IN");
		
		try {
			Assert.assertNotNull(spagobiTrigger, "Input parameter [spagobiTrigger] cannot be null");
			if( !triggerExists(spagobiTrigger) ) {
				throw new DAOException("Trigger [" + spagobiTrigger + "] does not exist");
			}
			org.quartz.Trigger quartzTrigger = QuartzNativeObjectsConverter.convertTriggerToNativeObject(spagobiTrigger);
			scheduler.rescheduleJob(quartzTrigger.getName(), quartzTrigger.getGroup(), quartzTrigger);
		} catch(DAOException t) {
			throw t;
		} catch(Throwable t) {
			throw new SpagoBIDOAException("An unexpected error occured while inserting trigger[" + spagobiTrigger + "]", t);
		} finally {
			logger.debug("OUT");
		}	
	}
	
}
