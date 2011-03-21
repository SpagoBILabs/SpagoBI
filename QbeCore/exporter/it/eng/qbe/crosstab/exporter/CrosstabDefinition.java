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
package it.eng.qbe.crosstab.exporter;

import it.eng.qbe.crosstab.serializer.json.CrosstabSerializationConstants;
import it.eng.spagobi.tools.dataset.common.query.AggregationFunctions;
import it.eng.spagobi.tools.dataset.common.query.IAggregationFunction;

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
	
	public static CrosstabDefinition EMPTY_CROSSTAB;
	
	static {
		EMPTY_CROSSTAB = new CrosstabDefinition();
		EMPTY_CROSSTAB.setColumns(new ArrayList<CrosstabDefinition.Column>());
		EMPTY_CROSSTAB.setRows(new ArrayList<CrosstabDefinition.Row>());
		EMPTY_CROSSTAB.setMeasures(new ArrayList<CrosstabDefinition.Measure>());
		EMPTY_CROSSTAB.setConfig(new JSONObject());
		EMPTY_CROSSTAB.setCalculatedFields(new JSONArray());
	}
	
	private int cellLimit;
	private List<Row> rows = null;
	private List<Column> columns = null;
	private List<Measure> measures = null;
	private JSONObject config = null;
	private JSONArray calculatedFields = null;
	
	public CrosstabDefinition() {}
	
	public int getCellLimit() {
		return cellLimit;
	}

	public void setCellLimit(int cellLimit) {
		this.cellLimit = cellLimit;
	}
	
	public List<Row> getRows() {
		return rows;
	}

	public void setRows(List<Row> rows) {
		this.rows = rows;
	}

	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	public List<Measure> getMeasures() {
		return measures;
	}

	public void setMeasures(List<Measure> measures) {
		this.measures = measures;
	}

	public JSONObject getConfig() {
		return config;
	}

	public void setConfig(JSONObject config) {
		this.config = config;
	}

	public JSONArray getCalculatedFields() {
		return calculatedFields;
	}

	public void setCalculatedFields(JSONArray calculatedFields) {
		this.calculatedFields = calculatedFields;
	}

	public boolean isMeasuresOnRows() {
		String value = config.optString(CrosstabSerializationConstants.MEASURESON);
		if (value != null) {
			return value.equalsIgnoreCase("rows");
		} else return false;
	}
	
	public boolean isMeasuresOnColumns() {
		String value = config.optString(CrosstabSerializationConstants.MEASURESON);
		if (value != null) {
			return value.equalsIgnoreCase("columns");
		} else return true;
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

}
