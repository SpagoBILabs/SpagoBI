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
package it.eng.spagobi.tools.scheduler.bo;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class Job {

	String name;
	String groupName;
	String description;
	Class jobClass;
	boolean durable;
	boolean requestsRecovery;
	boolean _volatile;
	Map<String, String> parameters;
	
	public Job() {
		parameters = new HashMap<String, String>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Class getJobClass() {
		return jobClass;
	}

	public void setJobClass(Class jobClass) {
		this.jobClass = jobClass;
	}

	public boolean isDurable() {
		return durable;
	}

	public void setDurable(boolean durable) {
		this.durable = durable;
	}

	public boolean isRequestsRecovery() {
		return requestsRecovery;
	}

	public void setRequestsRecovery(boolean requestsRecovery) {
		this.requestsRecovery = requestsRecovery;
	}

	public boolean isVolatile() {
		return _volatile;
	}

	public void setVolatile(boolean _volatile) {
		this._volatile = _volatile;
	}
	
	public void addParameter(String name, String value) {
		parameters.put(name, value);
	}
	
	public void addParameters(Map<String, String> parameters) {
		this.parameters.putAll(parameters);
	}
	
	public Map<String, String> getParameters() {
		return this.parameters;
	}
}
