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
package it.eng.spagobi.engines.qbe.worksheet.serializer.json;

import java.util.List;

import it.eng.qbe.serializer.ISerializer;
import it.eng.qbe.serializer.SerializationException;
import it.eng.spagobi.engines.qbe.worksheet.WorkSheet;
import it.eng.spagobi.engines.qbe.worksheet.WorkSheetDefinition;
import it.eng.spagobi.utilities.assertion.Assert;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public class WorkSheetJSONSerializer implements ISerializer {

	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(WorkSheetJSONSerializer.class);
    
    
	public Object serialize(Object o) throws SerializationException {
		
		logger.debug("IN");
		logger.debug("Serializing the worksheet");
		
		JSONObject toReturn = null;
		WorkSheetDefinition workSheetDefinition;
				
		Assert.assertNotNull(o, "Input parameter cannot be null");
		Assert.assertTrue(o instanceof WorkSheetDefinition, "Unable to serialize objects of type [" + o.getClass().getName() + "]");
		
		try {
			toReturn = new JSONObject();
			
			workSheetDefinition = (WorkSheetDefinition)o;
			
			JSONArray sheets = serializeSheets(workSheetDefinition.getWorkSheet());
			toReturn.put(WorkSheetSerializationCostants.SHEETS, sheets);
		} catch (Throwable t) {
			throw new SerializationException("An error occurred while serializing object: " + o, t);
		} finally {
			logger.debug("OUT");
		}
		logger.debug("Worksheet serialized");
		return toReturn;
	}
	
	private JSONArray serializeSheets(List<WorkSheet> sheets) throws SerializationException{
		JSONArray jsonSheets = new JSONArray();
		for(int i=0; i<sheets.size(); i++){
			jsonSheets.put(serializeSheet(sheets.get(i)));
		}
		return jsonSheets;
	}
	
	private JSONObject serializeSheet(WorkSheet sheet) throws SerializationException{
		logger.debug("IN");
		logger.debug("Serializing the sheet "+sheet.getName());
		JSONObject jsonSheet = new JSONObject();
		try {
			jsonSheet.put(WorkSheetSerializationCostants.NAME, sheet.getName());
			jsonSheet.put(WorkSheetSerializationCostants.HEADER, sheet.getHeader());
			jsonSheet.put(WorkSheetSerializationCostants.FILTERS, sheet.getFilters());
			jsonSheet.put(WorkSheetSerializationCostants.CONTENT, sheet.getContent());
			jsonSheet.put(WorkSheetSerializationCostants.FOOTER, sheet.getFooter());
		} catch (Exception e) {
			logger.error("Error serializing the sheet "+sheet.getName(),e);
			throw new SerializationException("Error serializing the sheet "+sheet.getName(),e);
		} finally{
			logger.debug("OUT");
		}
		logger.debug("Serialized the sheet "+sheet.getName());
		return jsonSheet;
		
	}

}
