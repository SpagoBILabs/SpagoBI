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
package it.eng.spagobi.engines.dossier.bo;

import java.io.Serializable;

public class WorkflowConfiguration implements Serializable{

	private String nameWorkflowPackage = "";
	private String nameWorkflowProcess = "";
	
	
	/**
	 * Gets the name workflow package.
	 * 
	 * @return the name workflow package
	 */
	public String getNameWorkflowPackage() {
		return nameWorkflowPackage;
	}
	
	/**
	 * Sets the name workflow package.
	 * 
	 * @param nameWorkflowPackage the new name workflow package
	 */
	public void setNameWorkflowPackage(String nameWorkflowPackage) {
		this.nameWorkflowPackage = nameWorkflowPackage;
	}
	
	/**
	 * Gets the name workflow process.
	 * 
	 * @return the name workflow process
	 */
	public String getNameWorkflowProcess() {
		return nameWorkflowProcess;
	}
	
	/**
	 * Sets the name workflow process.
	 * 
	 * @param nameWorkflowProcess the new name workflow process
	 */
	public void setNameWorkflowProcess(String nameWorkflowProcess) {
		this.nameWorkflowProcess = nameWorkflowProcess;
	}
	
}
