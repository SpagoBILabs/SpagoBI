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

import it.eng.qbe.serializer.ISerializer;
import it.eng.qbe.serializer.SerializationException;
import it.eng.spagobi.engines.qbe.crosstable.CrosstabDefinition;
import it.eng.spagobi.engines.qbe.crosstable.CrosstabDefinition.Column;
import it.eng.spagobi.engines.qbe.crosstable.CrosstabDefinition.Measure;
import it.eng.spagobi.engines.qbe.crosstable.CrosstabDefinition.Row;
import it.eng.spagobi.utilities.assertion.Assert;

import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
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

		} catch (Throwable t) {
			throw new SerializationException("An error occurred while serializing object: " + o, t);
		} finally {
			
		}
		
		return toReturn;
	}
	
	private JSONArray serializeRows(CrosstabDefinition crosstabDefinition) throws JSONException {
		List<Row> rows = crosstabDefinition.getRows();
		JSONArray toReturn = new JSONArray();
		for (int i = 0; i < rows.size(); i++) {
			Row row = rows.get(i);
			JSONObject obj = new JSONObject();
			obj.put(CrosstabSerializationConstants.ID, row.getEntityId());
			obj.put(CrosstabSerializationConstants.ALIAS, row.getAlias());
			obj.put(CrosstabSerializationConstants.ICON_CLS, row.getIconCls());
			obj.put(CrosstabSerializationConstants.NATURE, row.getNature());
			toReturn.put(obj);
		}
		return toReturn;
	}
	
	private JSONArray serializeColumns(CrosstabDefinition crosstabDefinition) throws JSONException {
		List<Column> columns = crosstabDefinition.getColumns();
		JSONArray toReturn = new JSONArray();
		for (int i = 0; i < columns.size(); i++) {
			Column column = columns.get(i);
			JSONObject obj = new JSONObject();
			obj.put(CrosstabSerializationConstants.ID, column.getEntityId());
			obj.put(CrosstabSerializationConstants.ALIAS, column.getAlias());
			obj.put(CrosstabSerializationConstants.ICON_CLS, column.getIconCls());
			obj.put(CrosstabSerializationConstants.NATURE, column.getNature());
			toReturn.put(obj);
		}
		return toReturn;
	}
	
	private JSONArray serializeMeasures(CrosstabDefinition crosstabDefinition) throws JSONException {
		List<Measure> measures = crosstabDefinition.getMeasures();
		JSONArray toReturn = new JSONArray();
		for (int i = 0; i < measures.size(); i++) {
			Measure measure = measures.get(i);
			JSONObject obj = new JSONObject();
			obj.put(CrosstabSerializationConstants.ID, measure.getEntityId());
			obj.put(CrosstabSerializationConstants.ALIAS, measure.getAlias());
			obj.put(CrosstabSerializationConstants.ICON_CLS, measure.getIconCls());
			obj.put(CrosstabSerializationConstants.NATURE, measure.getNature());
			obj.put(CrosstabSerializationConstants.FUNCTION, measure.getAggregationFunction().getName());
			toReturn.put(obj);
		}
		return toReturn;
	}

}
