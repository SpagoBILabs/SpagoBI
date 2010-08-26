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
package it.eng.spagobi.engines.qbe.bo;

import it.eng.qbe.query.AggregationFunctions;
import it.eng.qbe.query.IAggregationFunction;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class wrap the crosstab configuration state (a JSONObject) and provide parsing methods.
 * 
 * @author Davide Zerbetto (davide.zerbetto@eng.it)
 *
 */
public class CrosstabDefinition {
	
	// constants
	public static String ROWS = "rows";
	public static String COLUMNS = "columns";
	public static String MEASURES = "measures";
	public static String ID = "id";
	public static String ALIAS = "alias";
	public static String ICON_CLS = "iconCls";
	public static String NATURE = "nature";
	public static String FUNCTION = "funct";
	public static String CONFIG = "config";
	public static String MEASURESON = "measureson";

	
	private List<Row> rows = null;
	private List<Column> columns = null;
	private List<Measure> measures = null;
	private JSONObject config = null;
	
	
	public CrosstabDefinition(JSONObject crosstabDefinition) {
		try {
			Assert.assertNotNull(crosstabDefinition, "Input JSON crosstab definition cannot be null");
			rows = parseRows(crosstabDefinition);
			columns = parseColumns(crosstabDefinition);
			measures = parseMeasures(crosstabDefinition);
			config = crosstabDefinition.optJSONObject(CONFIG);
		} catch (Exception e) {
			throw new SpagoBIEngineRuntimeException("Wrong input JSON crosstab definition", e);
		}
		
	}
	
	private List<Row> parseRows(JSONObject crosstabDefinition) throws Exception {
		List<Row> toReturn = new ArrayList<Row>();
		JSONArray rows = crosstabDefinition.optJSONArray(ROWS);
		//Assert.assertTrue(rows != null && rows.length() > 0, "No rows specified!");
		if (rows != null) {
			for (int i = 0; i < rows.length(); i++) {
				JSONObject obj = (JSONObject) rows.get(i);
				toReturn.add(new Row(obj.getString(ID), obj.getString(ALIAS), obj.getString(ICON_CLS), obj.getString(NATURE)));
			}
		}
		return toReturn;
	}
	
	private List<Measure> parseMeasures(JSONObject crosstabDefinition) throws Exception {
		List<Measure> toReturn = new ArrayList<Measure>();
		JSONArray rows = crosstabDefinition.optJSONArray(MEASURES);
		//Assert.assertTrue(rows != null && rows.length() > 0, "No measures specified!");
		if (rows != null) {
			for (int i = 0; i < rows.length(); i++) {
				JSONObject obj = (JSONObject) rows.get(i);
				toReturn.add(new Measure(obj.getString(ID), obj.getString(ALIAS), obj.getString(ICON_CLS), obj.getString(NATURE), obj.getString(FUNCTION)));
			}
		}
		return toReturn;
	}
	
	private List<Column> parseColumns(JSONObject crosstabDefinition) throws Exception {
		List<Column> toReturn = new ArrayList<Column>();
		JSONArray rows = crosstabDefinition.optJSONArray(COLUMNS);
		//Assert.assertTrue(rows != null && rows.length() > 0, "No columns specified!");
		if (rows != null) {
			for (int i = 0; i < rows.length(); i++) {
				JSONObject obj = (JSONObject) rows.get(i);
				toReturn.add(new Column(obj.getString(ID), obj.getString(ALIAS), obj.getString(ICON_CLS), obj.getString(NATURE)));
			}
		}
		return toReturn;
	}
	
	public List<Row> getRows() {
		return rows;
	}

	public List<Column> getColumns() {
		return columns;
	}
	
	public List<Measure> getMeasures() {
		return measures;
	}
		
	public JSONObject getConfig() {
		return config;
	}

	public boolean isMeasuresOnRows(){
		try{
			String value = config.getString(MEASURESON);
			if(value!=null){
				return value.equalsIgnoreCase("rows");
			}
		}catch (Exception e) {}
		return false;
	}
	
	public boolean isMeasuresOnColumns(){
		try{
			String value = config.getString(MEASURESON);
			if(value!=null){
				return value.equalsIgnoreCase("columns");
			}
		}catch (Exception e) {}
		return true;
	}
	
	public class CrosstabElement {
		String entityId = null;
		String alias = null;
		String iconCls = null;
		String nature = null;
		public CrosstabElement(String entityId, String alias, String iconCls, String nature) {
			this.entityId = entityId;
			this.alias = alias;
			this.iconCls = iconCls;
			this.nature = nature;
		}
		public String getEntityId() {
			return entityId;
		}
		public String getAlias() {
			return alias;
		}
		public String getIconCls() {
			return iconCls;
		}
		public String getNature() {
			return nature;
		}
	}
	
	public class Row extends CrosstabElement {
		public Row(String entityId, String alias, String iconCls, String nature) {
			super(entityId, alias, iconCls, nature);
		}
	}
	
	public class Column extends CrosstabElement {
		public Column(String entityId, String alias, String iconCls, String nature) {
			super(entityId, alias, iconCls, nature);
		}
	}
	
	public class Measure extends CrosstabElement {
		IAggregationFunction function = null;
		public Measure(String entityId, String alias, String iconCls, String nature, String function) {
			super(entityId, alias, iconCls, nature);
			this.function = AggregationFunctions.get(function);
		}
		public IAggregationFunction getAggregationFunction() {
			return function;
		}
	}

	public JSONObject toJSONObject() throws JSONException {
		JSONObject toReturn = new JSONObject();
		
		// config 
		toReturn.put(CONFIG, getConfig());
		
		// rows 
		JSONArray rows = new JSONArray();
		for (int i = 0; i < this.rows.size(); i++) {
			Row row = this.rows.get(i);
			JSONObject obj = new JSONObject();
			obj.put(ID, row.getEntityId());
			obj.put(ALIAS, row.getAlias());
			obj.put(ICON_CLS, row.getIconCls());
			obj.put(NATURE, row.getNature());
			rows.put(obj);
		}
		toReturn.put(ROWS, rows);
		
		
		// columns
		JSONArray columns = new JSONArray();
		for (int i = 0; i < this.columns.size(); i++) {
			Column column = this.columns.get(i);
			JSONObject obj = new JSONObject();
			obj.put(ID, column.getEntityId());
			obj.put(ALIAS, column.getAlias());
			obj.put(ICON_CLS, column.getIconCls());
			obj.put(NATURE, column.getNature());
			columns.put(obj);
		}
		toReturn.put(COLUMNS, columns);
		
		
		// measures
		JSONArray measures = new JSONArray();
		for (int i = 0; i < this.measures.size(); i++) {
			Measure measure = this.measures.get(i);
			JSONObject obj = new JSONObject();
			obj.put(ID, measure.getEntityId());
			obj.put(ALIAS, measure.getAlias());
			obj.put(ICON_CLS, measure.getIconCls());
			obj.put(NATURE, measure.getNature());
			obj.put(FUNCTION, measure.getAggregationFunction().getName());
			measures.put(obj);
		}
		toReturn.put(MEASURES, measures);
		
		return toReturn;
	}
}
