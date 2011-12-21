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
package it.eng.spagobi.services.scheduler;

public interface SchedulerService {

	String getJobList(String token,String user);
	
	String getJobSchedulationList(String token,String user,String jobName, String jobGroup);
	
	String deleteSchedulation(String token,String user,String triggerName, String triggerGroup);
	
	String deleteJob(String token,String user,String jobName, String jobGroupName);
	
	String defineJob(String token,String user,String xmlRequest);
	
	String getJobDefinition(String token,String user,String jobName, String jobGroup);
	
	String scheduleJob(String token,String user,String xmlRequest);
	
	String getJobSchedulationDefinition(String token,String user,String triggerName, String triggerGroup);	
	
	String existJobDefinition(String token,String user,String jobName, String jobGroup);
	
}
