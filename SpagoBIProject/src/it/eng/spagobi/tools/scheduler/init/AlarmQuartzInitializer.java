/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.tools.scheduler.init;

import it.eng.spago.base.SourceBean;
import it.eng.spago.init.InitializerIFace;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.metadata.SbiTenant;
import it.eng.spagobi.kpi.alarm.service.AlarmInspectorJob;
import it.eng.spagobi.tools.scheduler.bo.CronExpression;
import it.eng.spagobi.tools.scheduler.bo.Job;
import it.eng.spagobi.tools.scheduler.bo.Trigger;
import it.eng.spagobi.tools.scheduler.dao.ISchedulerDAO;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.safehaus.uuid.UUIDGenerator;

public class AlarmQuartzInitializer implements InitializerIFace {

	private SourceBean _config = null;
	private transient Logger logger = Logger
			.getLogger(AlarmQuartzInitializer.class);

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * it.eng.spago.init.InitializerIFace#init(it.eng.spago.base.SourceBean)
	 */
	public void init(SourceBean config) {
		logger.debug("IN");
		try {
			List<SbiTenant> tenants = DAOFactory.getTenantsDAO().loadAllTenants();
			for (SbiTenant tenant : tenants) {
				initInternal(config, tenant);
			}
		} finally {
			logger.debug("OUT");
		}
	}
	

	private void initInternal(SourceBean config, SbiTenant tenant) {
		try {
			logger.debug("IN");
			boolean alreadyInDB = false;
			ISchedulerDAO schedulerDAO = DAOFactory.getSchedulerDAO();
			schedulerDAO.setTenant(tenant.getName());
			alreadyInDB = schedulerDAO.jobExists("AlarmInspectorJob", "AlarmInspectorJob");
			if (!alreadyInDB) {

				// CREATE JOB DETAIL
				Job jobDetail = new Job();
				jobDetail.setName("AlarmInspectorJob");
				jobDetail.setGroupName("AlarmInspectorJob");
				jobDetail.setDescription("AlarmInspectorJob");
				jobDetail.setDurable(true);
				jobDetail.setVolatile(false);
				jobDetail.setRequestsRecovery(true);
				jobDetail.setJobClass(AlarmInspectorJob.class);

				schedulerDAO.insertJob(jobDetail);

				Calendar startDate = new java.util.GregorianCalendar(2008,
						Calendar.DECEMBER, 24);
				startDate.set(Calendar.HOUR, 06);
				startDate.set(Calendar.MINUTE, 00);
				startDate.set(Calendar.SECOND, 0);
				startDate.set(Calendar.MILLISECOND, 0);

				Calendar endDate = new java.util.GregorianCalendar(2009,
						Calendar.DECEMBER, 24);
				startDate.set(Calendar.HOUR, 06);
				startDate.set(Calendar.MINUTE, 00);
				startDate.set(Calendar.SECOND, 0);
				startDate.set(Calendar.MILLISECOND, 0);

//				Calendar startCal = new GregorianCalendar(
//						new Integer(2008).intValue(),
//						new Integer(12).intValue() - 1,
//						new Integer(1).intValue());

				String nameTrig = "schedule_uuid_"
						+ UUIDGenerator.getInstance().generateTimeBasedUUID()
								.toString();
//				CronTrigger trigger = new CronTrigger();
//				trigger.setName(nameTrig);
//				trigger.setCronExpression("0 0/5 * * * ? *");
//				trigger.setJobName("AlarmInspectorJob");
//				trigger.setJobGroup("AlarmInspectorJob");
//				trigger.setStartTime(startCal.getTime());
//				trigger.setJobDataMap(data);
//				trigger.setVolatility(false);
//				trigger.setMisfireInstruction(CronTrigger.MISFIRE_INSTRUCTION_SMART_POLICY);

				CronExpression cronExpression = new CronExpression("0 0/5 * * * ? *");
				
				Trigger simpleTrigger = new Trigger();
//				simpleTrigger.setRepeatCount(100);
				simpleTrigger.setName(nameTrig);
				// simpleTrigger.setRepeatInterval(24L * 60L * 60L * 1000L);
//				simpleTrigger.setRepeatInterval(5 * 60L * 1000L);
				simpleTrigger.setStartTime(startDate.getTime());
				simpleTrigger.setEndTime(endDate.getTime());
				simpleTrigger.setJob(jobDetail);
				simpleTrigger.setCronExpression(cronExpression);
				simpleTrigger.setRunImmediately(false);
//				simpleTrigger.setMisfireInstruction(SimpleTrigger.INSTRUCTION_RE_EXECUTE_JOB);

				schedulerDAO.insertTrigger(simpleTrigger);

				logger.debug("Added job with name AlarmInspectorJob");
			}
			logger.debug("OUT");
		} catch (Exception e) {
			logger.error("Error while initializing scheduler ", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.eng.spago.init.InitializerIFace#getConfig()
	 */
	public SourceBean getConfig() {
		return _config;
	}

}
