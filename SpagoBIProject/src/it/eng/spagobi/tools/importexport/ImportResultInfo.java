/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.

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
