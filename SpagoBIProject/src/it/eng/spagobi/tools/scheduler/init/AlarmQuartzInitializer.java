/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2008 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

**/
package it.eng.spagobi.tools.scheduler.init;

import it.eng.spago.base.SourceBean;
import it.eng.spago.init.InitializerIFace;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.utilities.SpagoBITracer;
import it.eng.spagobi.engines.kpi.service.KpiEngineJob;
import it.eng.spagobi.kpi.alarm.service.AlarmInspectorJob;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;
import org.quartz.impl.StdSchedulerFactory;
import org.safehaus.uuid.UUIDGenerator;


public class AlarmQuartzInitializer implements InitializerIFace {
	
	private SourceBean _config = null;
	private transient Logger logger = Logger.getLogger(AlarmQuartzInitializer.class);

	
	/* (non-Javadoc)
	 * @see it.eng.spago.init.InitializerIFace#init(it.eng.spago.base.SourceBean)
	 */
	public void init(SourceBean config) {
	    try{
	    	logger.debug("IN");
	    	boolean alreadyInDB = false;
	    	Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
	    	String[] jobNames = scheduler.getJobNames("AlarmInspectorJob");
	    	if(jobNames != null){ 
	    		int length = jobNames.length;
    			for (int i=0;i<length;i++){
    				if(jobNames[i].equalsIgnoreCase("AlarmInspectorJob")){
    					alreadyInDB = true;
    					break;
    				}
    			}
	    	}
	    	
	    	if(!alreadyInDB){
		    JobDataMap data=new JobDataMap();
		    
			// CREATE JOB DETAIL 
			JobDetail jobDetail = new JobDetail();
			jobDetail.setName("AlarmInspectorJob");
			jobDetail.setGroup("AlarmInspectorJob");
			jobDetail.setDescription("AlarmInspectorJob");
			jobDetail.setDurability(true);
			jobDetail.setVolatility(false);
			jobDetail.setRequestsRecovery(true);
			jobDetail.setJobDataMap(data);
			jobDetail.setJobClass(AlarmInspectorJob.class);
			
			
			//scheduler.addJob(jobDetail, true);
			
			java.util.Calendar cal = new java.util.GregorianCalendar(2008, Calendar.DECEMBER, 24);
			  cal.set(cal.HOUR, 06);
			  cal.set(cal.MINUTE, 00);
			  cal.set(cal.SECOND, 0);
			  cal.set(cal.MILLISECOND, 0);
			  
			java.util.Calendar cal2 = new java.util.GregorianCalendar(2009, Calendar.DECEMBER, 24);
				  cal.set(cal.HOUR, 06);
				  cal.set(cal.MINUTE, 00);
				  cal.set(cal.SECOND, 0);
				  cal.set(cal.MILLISECOND, 0);
			  
			  
			Calendar startCal = new GregorianCalendar(new Integer(2008).intValue(), 
	                        new Integer(12).intValue()-1, 
	                        new Integer(1).intValue());
			
			String nameTrig = "schedule_uuid_" + UUIDGenerator.getInstance().generateTimeBasedUUID().toString();
			CronTrigger trigger = new CronTrigger();
			trigger.setName(nameTrig);
			trigger.setCronExpression("0 0/5 * * * ? *");
			trigger.setJobName("AlarmInspectorJob");
		        trigger.setJobGroup("AlarmInspectorJob");
		        trigger.setStartTime(startCal.getTime());
			trigger.setJobDataMap(data);
			trigger.setVolatility(false); 
			trigger.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_SMART_POLICY);
			
			  SimpleTrigger simpleTrigger = new SimpleTrigger();
			  simpleTrigger.setRepeatCount(100);
			  simpleTrigger.setName(nameTrig);
			  //simpleTrigger.setRepeatInterval(24L * 60L * 60L * 1000L);
			  simpleTrigger.setRepeatInterval(5 * 60L * 1000L);
			  simpleTrigger.setStartTime(cal.getTime());
			  simpleTrigger.setEndTime(cal2.getTime());
			  simpleTrigger.setJobName("AlarmInspectorJob");
			  simpleTrigger.setJobGroup("AlarmInspectorJob");
			  simpleTrigger.setJobDataMap(data);
			  simpleTrigger.setVolatility(false);
			 
			  simpleTrigger.setMisfireInstruction(SimpleTrigger.INSTRUCTION_RE_EXECUTE_JOB);
			  
			scheduler.scheduleJob(jobDetail,simpleTrigger);
			
			logger.debug("Added job with name AlarmInspectorJob");
	    	}
			logger.debug("OUT");
	    } catch (Exception e) {
	    	logger.error("Error while initializing scheduler ",e);
	    	e.printStackTrace();
	    }
	}
	
	/* (non-Javadoc)
	 * @see it.eng.spago.init.InitializerIFace#getConfig()
	 */
	public SourceBean getConfig() {
		return _config;
	}

	
}
