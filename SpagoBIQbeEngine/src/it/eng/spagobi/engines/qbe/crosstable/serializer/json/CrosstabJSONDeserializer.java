/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2009 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.engines.qbe.crosstable.serializer.json;

import it.eng.qbe.query.serializer.SerializationException;
import it.eng.qbe.serializer.IDeserializer;
import it.eng.qbe.serializer.SerializationManager;
import it.eng.spagobi.engines.qbe.crosstable.CrosstabDefinition;
import it.eng.spagobi.engines.qbe.crosstable.CrosstabDefinition.Column;
import it.eng.spagobi.engines.qbe.crosstable.CrosstabDefinition.Measure;
import it.eng.spagobi.engines.qbe.crosstable.CrosstabDefinition.Row;
import it.eng.spagobi.utilities.assertion.Assert;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Davide Zerbetto (davide.zerbetto@eng.it)
 */
public class CrosstabJSONDeserializer implements IDeserializer {

    public static transient Logger logger = Logger.getLogger(CrosstabJSONDeserializer.class);
    
	public CrosstabDefinition deserialize(Object o) throws SerializationException {
		CrosstabDefinition crosstabDefinition = null;
		JSONObject crosstabDefinitionJSON = null;
		
		logger.debug("IN");
		
		try {
			Assert.assertNotNull(o, "Object to be deserialized cannot be null");
			
			if(o instanceof String) {
				logger.debug("Deserializing string [" + (String)o + "]");
				try {
					crosstabDefinitionJSON = new JSONObject( (String)o );
				} catch(Throwable t) {
					logger.debug("Object to be deserialized must be string encoding a JSON object");
					throw new SerializationException("An error occurred while deserializing query: " + (String)o, t);
				}
			} else if(o instanceof JSONObject) {
				crosstabDefinitionJSON = (JSONObject)o;
			} else {
				Assert.assertUnreachable("Object to be deserialized must be of type string or of type JSONObject, not of type [" + o.getClass().getName() + "]");
			}
			
			crosstabDefinition  = new CrosstabDefinition();
			
			try {
				deserializeRows(crosstabDefinitionJSON, crosstabDefinition);
				deserializeColumns(crosstabDefinitionJSON, crosstabDefinition);
				deserializeMeasures(crosstabDefinitionJSON, crosstabDefinition);
				
				// config (measures on rows/columns, totals/subototals on rows/columns) remains a JSONObject 
				JSONObject config = crosstabDefinitionJSON.optJSONObject(CrosstabSerializationConstants.CONFIG);
				crosstabDefinition.setConfig(config);
				
				JSONArray calculatedFields = crosstabDefinitionJSON.optJSONArray(CrosstabSerializationConstants.CALCULATED_FIELDS);
				crosstabDefinition.setCalculatedFields(calculatedFields);
				
			} catch (Exception e) {
				throw new SerializationException("An error occurred while deserializing query: " + crosstabDefinitionJSON.toString(), e);
			}

		} finally {
			logger.debug("OUT");
		}
		
		return crosstabDefinition;
	}
	
	private void deserializeRows(JSONObject crosstabDefinitionJSON, CrosstabDefinition crosstabDefinition) throws Exception {
		List<Row> rows = new ArrayList<Row>();
		JSONArray rowsJSON = crosstabDefinitionJSON.optJSONArray(CrosstabSerializationConstants.ROWS);
		//Assert.assertTrue(rows != null && rows.length() > 0, "No rows specified!");
		if (rowsJSON != null) {
			for (int i = 0; i < rowsJSON.length(); i++) {
				JSONObject obj = (JSONObject) rowsJSON.get(i);
				rows.add(crosstabDefinition.new Row(
						obj.getString(CrosstabSerializationConstants.ID), 
						obj.getString(CrosstabSerializationConstants.ALIAS), 
						obj.getString(CrosstabSerializationConstants.ICON_CLS), 
						obj.getString(CrosstabSerializationConstants.NATURE)));
			}
		}
		crosstabDefinition.setRows(rows);
	}
	
	private void deserializeMeasures(JSONObject crosstabDefinitionJSON, CrosstabDefinition crosstabDefinition) throws Exception {
		List<Measure> measures = new ArrayList<Measure>();
		JSONArray measuresJSON = crosstabDefinitionJSON.optJSONArray(CrosstabSerializationConstants.MEASURES);
		//Assert.assertTrue(rows != null && rows.length() > 0, "No measures specified!");
		if (measuresJSON != null) {
			for (int i = 0; i < measuresJSON.length(); i++) {
				JSONObject obj = (JSONObject) measuresJSON.get(i);
				measures.add(crosstabDefinition.new Measure(
						obj.getString(CrosstabSerializationConstants.ID), 
						obj.getString(CrosstabSerializationConstants.ALIAS), 
						obj.getString(CrosstabSerializationConstants.ICON_CLS), 
						obj.getString(CrosstabSerializationConstants.NATURE),
						obj.getString(CrosstabSerializationConstants.FUNCTION)));
			}
		}
		crosstabDefinition.setMeasures(measures);
	}
	
	private void deserializeColumns(JSONObject crosstabDefinitionJSON, CrosstabDefinition crosstabDefinition) throws Exception {
		List<Column> columns = new ArrayList<Column>();
		JSONArray columnsJSON = crosstabDefinitionJSON.optJSONArray(CrosstabSerializationConstants.COLUMNS);
		//Assert.assertTrue(rows != null && rows.length() > 0, "No columns specified!");
		if (columnsJSON != null) {
			for (int i = 0; i < columnsJSON.length(); i++) {
				JSONObject obj = (JSONObject) columnsJSON.get(i);
				columns.add(crosstabDefinition.new Column(
						obj.getString(CrosstabSerializationConstants.ID), 
						obj.getString(CrosstabSerializationConstants.ALIAS), 
						obj.getString(CrosstabSerializationConstants.ICON_CLS), 
						obj.getString(CrosstabSerializationConstants.NATURE)));
			}
		}
		crosstabDefinition.setColumns(columns);
	}
		
}
