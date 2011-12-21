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
package it.eng.spagobi.tools.scheduler.to;

import it.eng.spagobi.analiticalmodel.document.bo.BIObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class JobInfo implements Serializable{

	private String jobName = "";
	private String jobGroupName = "";
	private String jobDescription = "";
	private String schedulerAdminstratorIdentifier = "";
	private List biobjects = new ArrayList();
	
	/**
	 * Gets the job description.
	 * 
	 * @return the job description
	 */
	public String getJobDescription() {
		return jobDescription;
	}
	
	/**
	 * Sets the job description.
	 * 
	 * @param jobDescription the new job description
	 */
	public void setJobDescription(String jobDescription) {
		this.jobDescription = jobDescription;
	}
	
	/**
	 * Gets the job name.
	 * 
	 * @return the job name
	 */
	public String getJobName() {
		return jobName;
	}
	
	/**
	 * Sets the job name.
	 * 
	 * @param jobName the new job name
	 */
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	
	/**
	 * Gets the biobjects.
	 * 
	 * @return the biobjects
	 */
	public List getBiobjects() {
		return biobjects;
	}
	
	/**
	 * Sets the biobjects.
	 * 
	 * @param biobjects the new biobjects
	 */
	public void setBiobjects(List biobjects) {
		this.biobjects = biobjects;
	}
	
	/**
	 * Gets the biobject ids.
	 * 
	 * @return the biobject ids
	 */
	public List getBiobjectIds() {
		List biobjIds = new ArrayList();
		Iterator iterBiobjects = biobjects.iterator();
		while(iterBiobjects.hasNext()) {
			BIObject biobj = (BIObject)iterBiobjects.next();
			Integer id =  biobj.getId();
			biobjIds.add(id);
		}
		return biobjIds;
	}
	
	/**
	 * Gets the job group name.
	 * 
	 * @return the job group name
	 */
	public String getJobGroupName() {
		return jobGroupName;
	}
	
	/**
	 * Sets the job group name.
	 * 
	 * @param jobGroupName the new job group name
	 */
	public void setJobGroupName(String jobGroupName) {
		this.jobGroupName = jobGroupName;
	}

	public String getSchedulerAdminstratorIdentifier() {
		return schedulerAdminstratorIdentifier;
	}

	public void setSchedulerAdminstratorIdentifier(
			String schedulerAdminstratorIdentifier) {
		this.schedulerAdminstratorIdentifier = schedulerAdminstratorIdentifier;
	}
	
}
