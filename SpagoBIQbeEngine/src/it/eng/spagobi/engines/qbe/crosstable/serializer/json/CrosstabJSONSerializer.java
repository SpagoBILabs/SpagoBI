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
package it.eng.spagobi.engines.qbe.crosstable.serializer.json;

import it.eng.qbe.serializer.ISerializer;
import it.eng.qbe.serializer.SerializationException;
import it.eng.qbe.serializer.SerializationManager;
import it.eng.spagobi.engines.worksheet.bo.Measure;
import it.eng.spagobi.engines.worksheet.widgets.CrosstabDefinition;
import it.eng.spagobi.engines.worksheet.widgets.CrosstabDefinition.Column;
import it.eng.spagobi.engines.worksheet.widgets.CrosstabDefinition.Row;
import it.eng.spagobi.utilities.assertion.Assert;

import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Davide Zerbetto (davide.zerbetto@eng.it)
 */
public class CrosstabJSONSerializer implements ISerializer {

	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(CrosstabJSONSerializer.class);
    
    
	public Object serialize(Object o) throws SerializationException {
		JSONObject toReturn = null;
		CrosstabDefinition crosstabDefinition;
				
		Assert.assertNotNull(o, "Input parameter cannot be null");
		Assert.assertTrue(o instanceof CrosstabDefinition, "Unable to serialize objects of type [" + o.getClass().getName() + "]");
		
		try {
			toReturn = new JSONObject();
			
			crosstabDefinition = (CrosstabDefinition)o;
			
			// config (measures on rows/columns, totals/subototals on rows/columns)
			JSONObject config = crosstabDefinition.getConfig();
			config.put("maxcellnumber", crosstabDefinition.getCellLimit());

			toReturn.put(CrosstabSerializationConstants.CONFIG, config);
			
			// calculated fields definition
			JSONArray calculatedFields = crosstabDefinition.getCalculatedFields();
			toReturn.put(CrosstabSerializationConstants.CALCULATED_FIELDS, calculatedFields);
			
			// rows 
			JSONArray rows = this.serializeRows(crosstabDefinition);
			toReturn.put(CrosstabSerializationConstants.ROWS, rows);
			
			// columns
			JSONArray columns = this.serializeColumns(crosstabDefinition);
			toReturn.put(CrosstabSerializationConstants.COLUMNS, columns);
			
			// measures
			JSONArray measures = this.serializeMeasures(crosstabDefinition);
			toReturn.put(CrosstabSerializationConstants.MEASURES, measures);
			
			//additional data
			JSONObject additionalData = crosstabDefinition.getAdditionalData();
			toReturn.put(CrosstabSerializationConstants.ADDITIONAL_DATA, additionalData);
			
			
		} catch (SerializationException se) {
			throw se;
		} catch (Throwable t) {
			throw new SerializationException("An error occurred while serializing object: " + o, t);
		} finally {
			
		}
		
		return toReturn;
	}
	
	private JSONArray serializeRows(CrosstabDefinition crosstabDefinition) throws SerializationException {
		List<Row> rows = crosstabDefinition.getRows();
		JSONArray toReturn = new JSONArray();
		for (int i = 0; i < rows.size(); i++) {
			Row row = rows.get(i);
			JSONObject obj = (JSONObject) SerializationManager.serialize(row, "application/json");
			toReturn.put(obj);
		}
		return toReturn;
	}
	
	private JSONArray serializeColumns(CrosstabDefinition crosstabDefinition) throws SerializationException {
		List<Column> columns = crosstabDefinition.getColumns();
		JSONArray toReturn = new JSONArray();
		for (int i = 0; i < columns.size(); i++) {
			Column column = columns.get(i);
			JSONObject obj = (JSONObject) SerializationManager.serialize(column, "application/json");
			toReturn.put(obj);
		}
		return toReturn;
	}
	
	private JSONArray serializeMeasures(CrosstabDefinition crosstabDefinition) throws SerializationException {
		List<Measure> measures = crosstabDefinition.getMeasures();
		JSONArray toReturn = new JSONArray();
		for (int i = 0; i < measures.size(); i++) {
			Measure measure = measures.get(i);
			JSONObject obj = (JSONObject) SerializationManager.serialize(measure, "application/json");
			toReturn.put(obj);
		}
		return toReturn;
	}

}
