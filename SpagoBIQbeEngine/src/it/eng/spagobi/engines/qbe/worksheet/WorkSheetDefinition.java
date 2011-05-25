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
import it.eng.spagobi.engines.qbe.analysisstateloaders.worksheet.IWorksheetStateLoader;
import it.eng.spagobi.engines.qbe.analysisstateloaders.worksheet.WorksheetStateLoaderFactory;
import it.eng.spagobi.utilities.engines.EngineAnalysisState;
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 * 			Davide Zerbetto (davide.zerbetto@eng.it)
 *
 */
public class WorkSheetDefinition extends EngineAnalysisState {
	
	private static transient Logger logger = Logger.getLogger(WorkSheetDefinition.class);
	
	public static final String CURRENT_VERSION = "0";
	
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
			return (JSONObject) SerializationManager.serialize(this, "application/json");
		} catch (Exception e) {
			 throw new SpagoBIEngineRuntimeException("Error while serializing worksheet definition", e);
		}

	}

	public void load(byte[] rowData) throws SpagoBIEngineException {
		String str = null;
		JSONObject worksheetStateJSON = null;
		JSONObject rowDataJSON = null;
		String encodingFormatVersion;
		
		logger.debug("IN");

		try {
			str = new String( rowData );
			logger.debug("loading form state from row data [" + str + "] ...");
			
			rowDataJSON = new JSONObject(str);
			try {
				encodingFormatVersion = rowDataJSON.getString("version");
			} catch (JSONException e) {
				logger.debug("no version found, default is 0");
				encodingFormatVersion = "0";
			}
			
			logger.debug("Row data encoding version  [" + encodingFormatVersion + "]");
			
			if (encodingFormatVersion.equalsIgnoreCase(CURRENT_VERSION)) {				
				worksheetStateJSON = rowDataJSON;
			} else {
				logger.warn("Row data encoding version [" + encodingFormatVersion + "] does not match with the current version used by the engine [" + CURRENT_VERSION + "] ");
				logger.debug("Converting from encoding version [" + encodingFormatVersion + "] to encoding version [" + CURRENT_VERSION + "]....");
				IWorksheetStateLoader worksheetViewerStateLoader;
				worksheetViewerStateLoader = WorksheetStateLoaderFactory.getInstance().getLoader(encodingFormatVersion);
				if (worksheetViewerStateLoader == null) {
					throw new SpagoBIEngineException("Unable to load data stored in format [" + encodingFormatVersion + "] ");
				}
				worksheetStateJSON = (JSONObject) worksheetViewerStateLoader.load(str);
				logger.debug("Encoding conversion has been executed succesfully");
			}
			
			logger.debug("analysis state loaded succsfully from row data");
			
			//set the worksheet into the qbe instance
			WorkSheetDefinition workSheetDefinition = (WorkSheetDefinition) SerializationManager.deserialize(worksheetStateJSON, "application/json", WorkSheetDefinition.class);
			this.setWorkSheet(workSheetDefinition.getWorkSheet());
			
		} catch (Exception e) {
			throw new SpagoBIEngineException("Impossible to load form state from raw data", e);
		} finally {
			logger.debug("OUT");
		}
		
	}

	public byte[] store() throws SpagoBIEngineException {
		JSONObject worksheetJSON = null;
		String rowData = null;	
				
		try {
			worksheetJSON = (JSONObject) SerializationManager.serialize(this, "application/json");
			worksheetJSON.put("version", CURRENT_VERSION);
			rowData = worksheetJSON.toString();
		} catch (Throwable e) {
			throw new SpagoBIEngineException("Impossible to store form state", e);
		}
		
		return rowData.getBytes();
	}
}
