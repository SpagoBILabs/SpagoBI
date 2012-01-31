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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerUtils;

import it.eng.spagobi.commons.deserializer.TriggerXMLDeserializer;
import it.eng.spagobi.tools.scheduler.bo.Job;
import it.eng.spagobi.tools.scheduler.bo.Trigger;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;


/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
class QuartzNativeObjectsConverter {
	
	private static Logger logger = Logger.getLogger(QuartzNativeObjectsConverter.class);
	
	public static org.quartz.JobDetail convertJobToNativeObject(Job spagobiJob) {
		org.quartz.JobDetail quartzJob;
		
		quartzJob = new org.quartz.JobDetail();
		quartzJob.setName( spagobiJob.getName() );
		quartzJob.setGroup( spagobiJob.getGroupName() );
		quartzJob.setDescription( spagobiJob.getDescription() );
		quartzJob.setJobClass( spagobiJob.getJobClass() );
		quartzJob.setDurability( spagobiJob.isDurable() );
		quartzJob.setRequestsRecovery( spagobiJob.isRequestsRecovery() );
		quartzJob.setVolatility( spagobiJob.isVolatile() );
		
		return quartzJob;
	}
	
	public static Job convertJobFromNativeObject(org.quartz.JobDetail quartzJob) {
		Job spagobiJob;
		
		spagobiJob = new Job();
		spagobiJob.setName( quartzJob.getName() );
		spagobiJob.setGroupName( quartzJob.getGroup() );
		spagobiJob.setDescription( quartzJob.getDescription() );
		spagobiJob.setJobClass( quartzJob.getJobClass() );
		spagobiJob.setDurable( quartzJob.isDurable() );
		spagobiJob.setRequestsRecovery( quartzJob.requestsRecovery() );
		spagobiJob.setVolatile( quartzJob.isVolatile() );
		
		return spagobiJob;
	}
	
	public static org.quartz.Trigger convertTriggerToNativeObject(Trigger spagobiTrigger) {
		org.quartz.Trigger quartzTrigger;
		
		logger.debug("IN");
		
		quartzTrigger = null;
		try {
			Assert.assertNotNull(spagobiTrigger, "Input parameter [spagobiTrigger] csannot be null");
			
			if(spagobiTrigger.isRunImmediately()) {
				
				quartzTrigger = TriggerUtils.makeImmediateTrigger(spagobiTrigger.getName(), 0, 10000);
				quartzTrigger.setJobName(spagobiTrigger.getJob().getName());
				quartzTrigger.setJobGroup(spagobiTrigger.getGroupName());
				JobDataMap jobDataMap = convertParametersToNativeObject(spagobiTrigger.getJob().getParameters());
				quartzTrigger.setJobDataMap(jobDataMap);
				
			} else {
				
				if(spagobiTrigger.isSimpleTrigger()) {
					quartzTrigger = new org.quartz.SimpleTrigger();
				} else {
					org.quartz.CronTrigger quartzCronTrigger = new org.quartz.CronTrigger();
					quartzCronTrigger.setCronExpression(spagobiTrigger.getChronExpression());
					quartzTrigger = quartzCronTrigger;
				}	
				
				quartzTrigger.setName(spagobiTrigger.getName());
				quartzTrigger.setDescription(spagobiTrigger.getDescription());
				if(spagobiTrigger.getGroupName() == null) {
					quartzTrigger.setGroup(Scheduler.DEFAULT_GROUP);
				} else {
					quartzTrigger.setGroup(spagobiTrigger.getGroupName());
				}
						
				quartzTrigger.setStartTime(spagobiTrigger.getStartTime());
				if(spagobiTrigger.getEndTime() != null) {
					quartzTrigger.setEndTime(spagobiTrigger.getEndTime());
				}
				quartzTrigger.setJobName(spagobiTrigger.getJob().getName());
				if(spagobiTrigger.getJob().getGroupName() == null) {
					quartzTrigger.setJobGroup(Scheduler.DEFAULT_GROUP);
				} else {
					quartzTrigger.setJobGroup(spagobiTrigger.getJob().getGroupName());
				}
					
				quartzTrigger.setVolatility(spagobiTrigger.getJob().isVolatile());   
					
				JobDataMap jobDataMap = convertParametersToNativeObject(spagobiTrigger.getJob().getParameters());
				quartzTrigger.setJobDataMap(jobDataMap);
				
			}
		} catch(Throwable t) {
			throw new SpagoBIRuntimeException("An unexpected error occured while converting Trigger to native object", t);
		} finally {
			logger.debug("OUT");
		}
		
		return quartzTrigger;
	}
	
	public static Trigger convertTriggerFromNativeObject(org.quartz.Trigger quartzTrigger) {
		Trigger spagobiTrigger;
		
		spagobiTrigger = new Trigger();
		spagobiTrigger.setName( quartzTrigger.getName() );
		spagobiTrigger.setGroupName( quartzTrigger.getGroup() );
		spagobiTrigger.setDescription( quartzTrigger.getDescription() );
		spagobiTrigger.setCalendarName( quartzTrigger.getCalendarName() );
		spagobiTrigger.setStartTime( quartzTrigger.getStartTime() );
		spagobiTrigger.setEndTime( quartzTrigger.getEndTime() );
		
		// triggers that run immediately have a generated name that starts with schedule_uuid_ (see TriggerXMLDeserializer)
		// It would be better anyway to relay on a specific property to recognize if a trigger is thinked to run immediately
		spagobiTrigger.setRunImmediately( spagobiTrigger.getName().startsWith("schedule_uuid_" ) );
	
		if(quartzTrigger instanceof org.quartz.CronTrigger) {
			org.quartz.CronTrigger quartzCronTrigger = (org.quartz.CronTrigger)quartzTrigger;
			spagobiTrigger.setChronExpression( quartzCronTrigger.getCronExpression() ) ;
		} 
		
		Job job = new Job();
		job.setName( quartzTrigger.getJobName() );
		job.setGroupName( quartzTrigger.getJobGroup() );
		job.setVolatile(quartzTrigger.isVolatile() );
		Map<String, String> parameters = convertParametersFromNativeObject( quartzTrigger.getJobDataMap() );
		job.addParameters(parameters);
				
		return spagobiTrigger;
	}
	
	
	public static org.quartz.JobDataMap convertParametersToNativeObject(Map<String,String> spagobiParameters) {
		JobDataMap quartzParameters = new JobDataMap();
		
		Set<String> parameterNames = spagobiParameters.keySet();
		for(String parameterName : parameterNames) {
			String parameterValue = spagobiParameters.get(parameterName);
			quartzParameters.put(parameterName, parameterValue);
		}
		return quartzParameters;
	}
	
	public static Map<String, String> convertParametersFromNativeObject(org.quartz.JobDataMap quartzParameters) {
		Map<String, String> spagobiParameters = new HashMap<String, String>();
		
		Set<String> parameterNames = quartzParameters.keySet();
		for(String parameterName : parameterNames) {
			String parameterValue = (String)quartzParameters.get(parameterName);
			spagobiParameters.put(parameterName, parameterValue);
		}
		return spagobiParameters;
	}
}
