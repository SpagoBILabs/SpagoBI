/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
package it.eng.spagobi.engines.qbe.worksheet;

import it.eng.qbe.serializer.SerializationManager;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public class WorkSheetDefinition {
	public static final WorkSheetDefinition EMPTY_WORKSHEET;
	
	static {
		EMPTY_WORKSHEET = new WorkSheetDefinition();
	}
	
	private List<WorkSheet> workSheet;
	
	public WorkSheetDefinition(){
		workSheet = new ArrayList<WorkSheet>();
	}
	
	public WorkSheetDefinition(List<WorkSheet> workSheet){
		this.workSheet = workSheet;
	}

	public List<WorkSheet> getWorkSheet() {
		return workSheet;
	}

	public void setWorkSheet(List<WorkSheet> workSheet) {
		this.workSheet = workSheet;
	}
	
	public JSONObject getConf(){
		try {
			return (JSONObject)SerializationManager.serialize(this, "application/json");
		} catch (Exception e) {
			 return new JSONObject(); 
		}

	}
}
