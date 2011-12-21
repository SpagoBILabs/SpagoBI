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
package it.eng.spagobi.tools.importexport;

import java.util.HashMap;
import java.util.Map;

public class ImportResultInfo {

	String folderName = "";
	String logFileName = "";
	String associationsFileName = "";
	Map manualTasks = new HashMap();
	
	/**
	 * Gets the manual tasks.
	 * 
	 * @return the manual tasks
	 */
	public Map getManualTasks() {
		return manualTasks;
	}
	
	/**
	 * Sets the manual tasks.
	 * 
	 * @param manualTasks the new manual tasks
	 */
	public void setManualTasks(Map manualTasks) {
		this.manualTasks = manualTasks;
	}
	
	/**
	 * Gets the Name of log file.
	 * 
	 * @return the Name log file
	 */
	public String getLogFileName() {
		return logFileName;
	}
	
	/**
	 * Sets the Name of log file.
	 * 
	 * @param logFileName the Name of log file.
	 */
	public void setLogFileName(String logFileName) {
		this.logFileName = logFileName;
	}
	
	/**
	 * Gets the Name of associations file.
	 * 
	 * @return the Name of associations file
	 */
	public String getAssociationsFileName() {
		return associationsFileName;
	}
	
	/**
	 * Sets the Name of associations file.
	 * 
	 * @param associationsFileName the Name of associations file.
	 */
	public void setAssociationsFileName(String associationsFileName) {
		this.associationsFileName = associationsFileName;
	}

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}
	
}
