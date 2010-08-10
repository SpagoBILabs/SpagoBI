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
	public static String FUNCTION = "funct";
	
	
	private List<Row> rows = null;
	private List<Column> columns = null;
	private List<Measure> measures = null;
	
	public CrosstabDefinition(JSONObject crosstabDefinition) {
		try {
			Assert.assertNotNull(crosstabDefinition, "Input JSON crosstab definition cannot be null");
			rows = parseRows(crosstabDefinition);
			columns = parseColumns(crosstabDefinition);
			measures = parseMeasures(crosstabDefinition);
		} catch (Exception e) {
			throw new SpagoBIEngineRuntimeException("Wrong input JSON crosstab definition", e);
		}
		
	}
	
	private List<Row> parseRows(JSONObject crosstabDefinition) throws Exception {
		List<Row> toReturn = new ArrayList<Row>();
		JSONArray rows = crosstabDefinition.optJSONArray(ROWS);
		Assert.assertTrue(rows != null && rows.length() > 0, "No rows specified!");
		for (int i = 0; i < rows.length(); i++) {
			JSONObject obj = (JSONObject) rows.get(i);
			toReturn.add(new Row(obj.getString(ID), obj.getString(ALIAS)));
		}
		return toReturn;
	}
	
	private List<Measure> parseMeasures(JSONObject crosstabDefinition) throws Exception {
		List<Measure> toReturn = new ArrayList<Measure>();
		JSONArray rows = crosstabDefinition.optJSONArray(MEASURES);
		Assert.assertTrue(rows != null && rows.length() > 0, "No measures specified!");
		for (int i = 0; i < rows.length(); i++) {
			JSONObject obj = (JSONObject) rows.get(i);
			toReturn.add(new Measure(obj.getString(ID), obj.getString(ALIAS), obj.getString(FUNCTION)));
		}
		return toReturn;
	}
	
	private List<Column> parseColumns(JSONObject crosstabDefinition) throws Exception {
		List<Column> toReturn = new ArrayList<Column>();
		JSONArray rows = crosstabDefinition.optJSONArray(COLUMNS);
		Assert.assertTrue(rows != null && rows.length() > 0, "No columns specified!");
		for (int i = 0; i < rows.length(); i++) {
			JSONObject obj = (JSONObject) rows.get(i);
			toReturn.add(new Column(obj.getString(ID), obj.getString(ALIAS)));
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
	
	public class CrosstabElement {
		String entityId = null;
		String alias = null;
		public CrosstabElement(String entityId, String alias) {
			this.entityId = entityId;
			this.alias = alias;
		}
		public String getEntityId() {
			return entityId;
		}
		public String getAlias() {
			return alias;
		}
	}
	
	public class Row extends CrosstabElement {
		public Row(String entityId, String alias) {
			super(entityId, alias);
		}
	}
	
	public class Column extends CrosstabElement {
		public Column(String entityId, String alias) {
			super(entityId, alias);
		}
	}
	
	public class Measure extends CrosstabElement {
		IAggregationFunction function = null;
		public Measure(String entityId, String alias, String function) {
			super(entityId, alias);
			this.function = AggregationFunctions.get(function);
		}
		public IAggregationFunction getAggregationFunction() {
			return function;
		}
	}
	
}
