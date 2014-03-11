/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2010 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.tools.scheduler.services.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import it.eng.spago.base.SourceBean;
import it.eng.spago.error.EMFErrorSeverity;
import it.eng.spago.error.EMFUserError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.commons.constants.SpagoBIConstants;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.deserializer.TriggerXMLDeserializer;
import it.eng.spagobi.commons.serializer.JSONSerializer;
import it.eng.spagobi.commons.serializer.JobJSONSerializer;
import it.eng.spagobi.commons.serializer.SerializationException;
import it.eng.spagobi.commons.serializer.SerializerFactory;
import it.eng.spagobi.commons.serializer.XMLSerializer;
import it.eng.spagobi.commons.utilities.AuditLogUtilities;
import it.eng.spagobi.services.exceptions.ExceptionUtilities;
import it.eng.spagobi.services.scheduler.service.ISchedulerServiceSupplier;
import it.eng.spagobi.services.scheduler.service.SchedulerServiceSupplierFactory;
import it.eng.spagobi.tools.scheduler.bo.Job;
import it.eng.spagobi.tools.scheduler.bo.Trigger;
import it.eng.spagobi.tools.scheduler.dao.ISchedulerDAO;
import it.eng.spagobi.tools.scheduler.utils.SchedulerUtilities;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Marco Cortella (marco.cortella@eng.it)
 *
 */

@Path("/scheduler")
public class SchedulerService {
	static private Logger logger = Logger.getLogger(SchedulerService.class);
	static private String canNotFillResponseError = "error.mesage.description.generic.can.not.responce";

	
	@GET
	@Path("/listAllJobs")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public String getAllJobs(){
		JSONObject JSONReturn = new JSONObject();
		JSONArray jobsJSONArray = new JSONArray();
		
		
		ISchedulerDAO schedulerDAO;
    	try {
    		schedulerDAO = DAOFactory.getSchedulerDAO();
		} catch (Throwable t) {
			throw new SpagoBIRuntimeException("Impossible to load scheduler DAO", t);
		}
		List<Job> jobs = schedulerDAO.loadJobs();

		JSONSerializer jsonSerializer = (JSONSerializer)SerializerFactory.getSerializer("application/json");
		try {
			jobsJSONArray = (JSONArray) jsonSerializer.serialize(jobs, null);
			
			
		//add the triggers part for each job
			
			for (int i = 0; i < jobsJSONArray.length(); i++) {

				JSONObject jobJSONObject = jobsJSONArray.getJSONObject(i);
				String jobName = jobJSONObject.getString(JobJSONSerializer.JOB_NAME);
				String jobGroup = jobJSONObject.getString(JobJSONSerializer.JOB_GROUP);

				for (Job job : jobs){
					if ((job.getName().equals(jobName)) && (job.getGroupName().equals(jobGroup))){
						String triggersSerialized = getJobTriggers(job);
						JSONObject triggersJSONObject = new JSONObject(triggersSerialized);
						JSONArray triggersJSONArray = triggersJSONObject.getJSONArray("triggers");
						//put the triggersJSONArray inside the correct jobJSONObject
						jobJSONObject.put("triggers", triggersJSONArray);
					}
				}

			}
			
		} catch (SerializationException e) {
			throw new SpagoBIRuntimeException("Error serializing Jobs objects in SchedulerService", e);

		} catch (JSONException e) {
			throw new SpagoBIRuntimeException("Error serializing Jobs objects in SchedulerService", e);

		}
		

		
		
		try {
			JSONReturn.put("root", jobsJSONArray);
		} catch (JSONException e) {
			throw new SpagoBIRuntimeException("JSONException in SchedulerService", e);

		}
		//TODO: to remove
		for(Job job: jobs){
			String jobTriggers = getJobTriggers(job);
		}

		return JSONReturn.toString();
	}
	
	
	//Triggers are the Schedulation instances
	public String getJobTriggers(Job job){
		JSONObject JSONReturn = new JSONObject();
		JSONArray triggersJSONArray = new JSONArray();
		
		String jobGroupName = job.getGroupName();
		String jobName = job.getName();
		
		try {
			Assert.assertNotNull(jobName, "Input parameter [" + jobName + "] cannot be null");
			Assert.assertNotNull(jobName, "Input parameter [" + jobGroupName + "] cannot be null");
			ISchedulerDAO schedulerDAO = DAOFactory.getSchedulerDAO();

			List<Trigger> triggers = schedulerDAO.loadTriggers(jobGroupName, jobName);
			// filter out trigger whose property runImmediately is equal to true
			List<Trigger> triggersToSerialize = new ArrayList<Trigger>();
			for (Trigger trigger  : triggers) {
				//if(trigger.getName().startsWith("schedule_uuid_") == false) {
				if(!trigger.isRunImmediately()) {
					triggersToSerialize.add(trigger);
				}
			}
			logger.trace("Succesfully loaded [" + triggersToSerialize.size() + "] trigger(s)");
			
			JSONSerializer jsonSerializer = (JSONSerializer)SerializerFactory.getSerializer("application/json");
			try {
				triggersJSONArray = (JSONArray) jsonSerializer.serialize(triggersToSerialize, null);
			} catch (SerializationException e) {
				throw new SpagoBIRuntimeException("Error serializing Jobs objects in SchedulerService", e);

			}
			
			try {
				JSONReturn.put("triggers", triggersJSONArray);
			} catch (JSONException e) {
				throw new SpagoBIRuntimeException("JSONException in SchedulerService", e);

			}
			
			
			logger.debug("Trigger list succesfully serialized");
		} catch (Throwable t) {
			throw new SpagoBIRuntimeException("An unexpected error occured while loading trigger list", t);
		} finally {
			logger.debug("OUT");
		}
		
		return JSONReturn.toString();
	}
	
	@POST
	@Path("/deleteJob")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public String deleteJob(@Context HttpServletRequest req){
		IEngUserProfile profile = (IEngUserProfile) req.getSession().getAttribute(IEngUserProfile.ENG_USER_PROFILE);

		String jobGroupName = req.getParameter("jobGroup");
		String jobName = req.getParameter("jobName");
		HashMap<String, String> logParam = new HashMap();
		try {

			ISchedulerServiceSupplier schedulerService = SchedulerServiceSupplierFactory.getSupplier();

			String xmlSchedList = schedulerService.getJobSchedulationList(jobName, jobGroupName);
			SourceBean rowsSB_JSL = SourceBean.fromXMLString(xmlSchedList);
			if(rowsSB_JSL==null) {
				throw new Exception("List of job triggers not returned by Web service ");
			}
			logParam.put("JOB NAME", jobName);
			logParam.put("JOB GROUP NAME", jobGroupName);
			
			// delete each schedulation
			List schedules = rowsSB_JSL.getAttributeAsList("ROW");
			Iterator iterSchedules = schedules.iterator();
			while(iterSchedules.hasNext()) {
				SourceBean scheduleSB = (SourceBean)iterSchedules.next();
				String triggerName = (String)scheduleSB.getAttribute("triggerName");
				String triggerGroup = (String)scheduleSB.getAttribute("triggerGroup");
				DAOFactory.getDistributionListDAO().eraseAllRelatedDistributionListObjects(triggerName);
				String delResp = schedulerService.deleteSchedulation(triggerName, triggerGroup);
				SourceBean schedModRespSB_DS = SchedulerUtilities.getSBFromWebServiceResponse(delResp);
				if(schedModRespSB_DS==null) {
					try {
						updateAudit(req,  profile, "SCHEDULER.DELETE",null , "KO");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					throw new Exception("Imcomplete response returned by the Web service " +
							"during schedule "+triggerName+" deletion");
				}	
				if(!SchedulerUtilities.checkResultOfWSCall(schedModRespSB_DS)){
					try {
						updateAudit(req,  profile, "SCHEDULER.DELETE",logParam , "KO");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					throw new Exception("Schedule "+triggerName+" not deleted by the Web Service");
				}
			}			
			// delete job	
			String resp_DJ = schedulerService.deleteJob(jobName, jobGroupName);
			SourceBean schedModRespSB_DJ = SchedulerUtilities.getSBFromWebServiceResponse(resp_DJ);
			if(schedModRespSB_DJ==null) {
				try {
					updateAudit(req,  profile, "SCHEDULER.DELETE",logParam , "KO");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				throw new Exception("Imcomplete response returned by the Web service " +
						"during job "+jobName+" deletion");
			}	
			if(!SchedulerUtilities.checkResultOfWSCall(schedModRespSB_DJ)){
				try {
					updateAudit(req,  profile, "SCHEDULER.DELETE",logParam , "KO");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				throw new Exception("JOb "+jobName+" not deleted by the Web Service");
			}
			// fill response
			updateAudit(req,  profile, "SCHEDULER.DELETE",logParam , "OK");

			return ("{resp:'ok'}");
		} catch (Exception ex) {
			updateAudit(req,  profile, "SCHEDULER.DELETE",logParam , "KO");
			logger.error("Error while deleting job", ex);
			logger.debug(canNotFillResponseError);
			try {
				return ( ExceptionUtilities.serializeException(canNotFillResponseError,null));
			} catch (Exception e) {
				logger.debug("Cannot fill response container.");
				throw new SpagoBIRuntimeException(
						"Cannot fill response container", e);
			}
		}

	}
	
	@POST
	@Path("/deleteTrigger")
	@Produces(MediaType.APPLICATION_JSON + "; charset=UTF-8")
	public String deleteTrigger(@Context HttpServletRequest req){
		IEngUserProfile profile = (IEngUserProfile) req.getSession().getAttribute(IEngUserProfile.ENG_USER_PROFILE);
		HashMap<String, String> logParam = new HashMap();

		String jobGroupName = req.getParameter("jobGroup");
		String jobName = req.getParameter("jobName");
		String triggerGroup = req.getParameter("triggerGroup");
		String triggerName = req.getParameter("triggerName");
		logParam.put("JOB NAME", jobName);
		logParam.put("JOB GROUP", jobGroupName);
		logParam.put("TRIGGER NAME", triggerName);
		logParam.put("TRIGGER GROUP", triggerGroup);

		try {
			DAOFactory.getDistributionListDAO().eraseAllRelatedDistributionListObjects(triggerName);
			ISchedulerServiceSupplier schedulerService = SchedulerServiceSupplierFactory.getSupplier();
			String resp = schedulerService.deleteSchedulation(triggerName, triggerGroup);
			SourceBean schedModRespSB = SchedulerUtilities.getSBFromWebServiceResponse(resp);
			if(schedModRespSB!=null) {
				String outcome = (String)schedModRespSB.getAttribute("outcome");
				if(outcome.equalsIgnoreCase("fault")){
					try {
						updateAudit(req,  profile, "SCHED_TRIGGER.DELETE",logParam , "KO");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					throw new Exception("Trigger not deleted by the service");
				}
			}
			updateAudit(req,  profile, "SCHED_TRIGGER.DELETE",logParam , "OK");

			return ("{resp:'ok'}");


		} catch (Exception e) {
			updateAudit(req,  profile, "SCHEDULER.DELETE",logParam , "KO");
			logger.error("Error while deleting schedule (trigger) ", e);
			logger.debug(canNotFillResponseError);
			try {
				return ( ExceptionUtilities.serializeException(canNotFillResponseError,null));
			} catch (Exception ex) {
				logger.debug("Cannot fill response container.");
				throw new SpagoBIRuntimeException(
						"Cannot fill response container", ex);
			}
		}


	}
	
	private static void updateAudit(HttpServletRequest request,
			IEngUserProfile profile, String action_code,
			HashMap<String, String> parameters, String esito) {
		try {
			AuditLogUtilities.updateAudit(request, profile, action_code,
					parameters, esito);
		} catch (Exception e) {
			logger.debug("Error writing audit", e);
		}
	}



}
