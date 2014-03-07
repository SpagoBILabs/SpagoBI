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
import java.util.List;

import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.deserializer.TriggerXMLDeserializer;
import it.eng.spagobi.commons.serializer.JSONSerializer;
import it.eng.spagobi.commons.serializer.JobJSONSerializer;
import it.eng.spagobi.commons.serializer.SerializationException;
import it.eng.spagobi.commons.serializer.SerializerFactory;
import it.eng.spagobi.commons.serializer.XMLSerializer;
import it.eng.spagobi.tools.scheduler.bo.Job;
import it.eng.spagobi.tools.scheduler.bo.Trigger;
import it.eng.spagobi.tools.scheduler.dao.ISchedulerDAO;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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

}
