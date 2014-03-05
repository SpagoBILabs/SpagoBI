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

import java.util.List;

import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.serializer.JSONSerializer;
import it.eng.spagobi.commons.serializer.SerializationException;
import it.eng.spagobi.commons.serializer.SerializerFactory;
import it.eng.spagobi.tools.scheduler.bo.Job;
import it.eng.spagobi.tools.scheduler.dao.ISchedulerDAO;
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

		
		//TODO: creare serializer JSON per oggetti JOB
		JSONSerializer jsonSerializer = (JSONSerializer)SerializerFactory.getSerializer("application/json");
		try {
			jobsJSONArray = (JSONArray) jsonSerializer.serialize(jobs, null);
		} catch (SerializationException e) {
			throw new SpagoBIRuntimeException("Error serializing Jobs objects in SchedulerService", e);

		}
		try {
			JSONReturn.put("root", jobsJSONArray);
		} catch (JSONException e) {
			throw new SpagoBIRuntimeException("JSONException in SchedulerService", e);

		}

		//TODO: restituire il risultato serializzato in return 


		return JSONReturn.toString();
	}

}
