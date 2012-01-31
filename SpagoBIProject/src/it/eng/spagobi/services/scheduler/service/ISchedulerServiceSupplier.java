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
package it.eng.spagobi.services.scheduler.service;


/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public interface ISchedulerServiceSupplier {
	String getJobList() ;
	String getJobDefinition(String jobName, String jobGroup) ;
	String getJobSchedulationList(String jobName, String jobGroupName) ;
	String getJobSchedulationDefinition(String triggerName, String triggerGroupName);
	String deleteSchedulation(String triggerName, String triggerGroup);
	String deleteJob(String jobName, String jobGroupName) ;
	String defineJob(String xmlRequest);
	String scheduleJob(String xmlRequest) ;
	String existJobDefinition(String jobName, String jobGroupName) ;
}
