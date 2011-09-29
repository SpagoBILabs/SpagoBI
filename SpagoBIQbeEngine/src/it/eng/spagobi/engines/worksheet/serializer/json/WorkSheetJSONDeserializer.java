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
package it.eng.spagobi.engines.worksheet.serializer.json;

import it.eng.qbe.query.serializer.json.QuerySerializationConstants;
import it.eng.qbe.serializer.IDeserializer;
import it.eng.qbe.serializer.SerializationException;
import it.eng.qbe.serializer.SerializationManager;
import it.eng.spagobi.engines.worksheet.bo.Attribute;
import it.eng.spagobi.engines.worksheet.bo.Measure;
import it.eng.spagobi.engines.worksheet.bo.Serie;
import it.eng.spagobi.engines.worksheet.bo.Sheet;
import it.eng.spagobi.engines.worksheet.bo.Sheet.FiltersPosition;
import it.eng.spagobi.engines.worksheet.bo.SheetContent;
import it.eng.spagobi.engines.worksheet.bo.WorkSheetDefinition;
import it.eng.spagobi.engines.worksheet.widgets.ChartDefinition;
import it.eng.spagobi.engines.worksheet.widgets.CrosstabDefinition;
import it.eng.spagobi.engines.worksheet.widgets.TableDefinition;
import it.eng.spagobi.utilities.assertion.Assert;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public class WorkSheetJSONDeserializer implements IDeserializer {

    public static transient Logger logger = Logger.getLogger(WorkSheetJSONDeserializer.class);
    
	public WorkSheetDefinition deserialize(Object o) throws SerializationException {
		WorkSheetDefinition workSheetDefinition = null;
		JSONObject workSheetDefinitionJSON = null;
		
		logger.debug("IN");
		
		try {
			Assert.assertNotNull(o, "Object to be deserialized cannot be null");
			
			if(o instanceof String) {
				logger.debug("Deserializing string [" + (String)o + "]");
				try {
					workSheetDefinitionJSON = new JSONObject( (String)o );
				} catch(Throwable t) {
					logger.debug("Object to be deserialized must be string encoding a JSON object");
					throw new SerializationException("An error occurred while deserializing query: " + (String)o, t);
				}
			} else if(o instanceof JSONObject) {
				workSheetDefinitionJSON = (JSONObject)o;
			} else {
				Assert.assertUnreachable("Object to be deserialized must be of type string or of type JSONObject, not of type [" + o.getClass().getName() + "]");
			}
			
			workSheetDefinition  = new WorkSheetDefinition();
			
			try {
				deserializeSheets(workSheetDefinitionJSON, workSheetDefinition);
				deserializeGlobalFilters(workSheetDefinitionJSON, workSheetDefinition);
			} catch (Exception e) {
				throw new SerializationException("An error occurred while deserializing worksheet: " + workSheetDefinitionJSON.toString(), e);
			}

		} finally {
			logger.debug("OUT");
		}
		logger.debug("Worksheet deserialized");
		return workSheetDefinition;
	}
	
	private void deserializeGlobalFilters(JSONObject workSheetDefinitionJSON,
			WorkSheetDefinition workSheetDefinition) throws Exception {
		JSONArray gfJSON = workSheetDefinitionJSON.getJSONArray(WorkSheetSerializationCostants.GLOBAL_FILTERS);
		List<Attribute> globalFilters = new ArrayList<Attribute>();
		for (int i = 0; i < gfJSON.length(); i++) {
			globalFilters.add(deserializeAttribute(gfJSON.getJSONObject(i)));
		}
		workSheetDefinition.setGlobalFilters(globalFilters);
	}

	private Attribute deserializeAttribute(JSONObject jsonObject) throws SerializationException {
		Attribute attribute = (Attribute) SerializationManager.deserialize(jsonObject, "application/json", Attribute.class);
		return attribute;
	}

	/**
	 * Deserialize the list of sheets
	 * @param crosstabDefinitionJSON
	 * @param crosstabDefinition
	 * @throws Exception
	 */
	private void deserializeSheets(JSONObject crosstabDefinitionJSON, WorkSheetDefinition crosstabDefinition) throws Exception {
		JSONArray sheetsJSON = crosstabDefinitionJSON.getJSONArray(WorkSheetSerializationCostants.SHEETS);
		List<Sheet> workSheets = new ArrayList<Sheet>();
		for(int i=0; i<sheetsJSON.length(); i++){
			workSheets.add(deserializeSheet(sheetsJSON.getJSONObject(i)));
		}
		crosstabDefinition.setSheets(workSheets);
	}
	
	/**
	 * Deserialize the Sheet
	 * @param sheetJSON
	 * @return
	 * @throws Exception
	 */
	private Sheet deserializeSheet(JSONObject sheetJSON) throws Exception {
		String name = sheetJSON.getString(WorkSheetSerializationCostants.NAME);
		JSONObject header = sheetJSON.optJSONObject(WorkSheetSerializationCostants.HEADER);
		String layout = sheetJSON.optString(WorkSheetSerializationCostants.LAYOUT);
		JSONObject footer = sheetJSON.optJSONObject(WorkSheetSerializationCostants.FOOTER);
		
		List<Attribute> filters = deserializeSheetFilters(sheetJSON);
		FiltersPosition position = FiltersPosition.TOP;
		JSONObject filtersJSON = sheetJSON.optJSONObject(WorkSheetSerializationCostants.FILTERS);
		if (filtersJSON != null) {
			position = FiltersPosition.valueOf(filtersJSON.getString(WorkSheetSerializationCostants.POSITION).toUpperCase());
		}
		
		logger.debug("Deserializing sheet " + name);
		SheetContent content = deserializeContent(sheetJSON);
		logger.debug("Sheet " + name + " deserialized successfully");
		
		return new Sheet(name, layout, header, filters, position, content, footer);
	}
	
	private SheetContent deserializeContent(JSONObject sheetJSON) throws Exception {
		SheetContent toReturn = null;
		JSONObject content = sheetJSON.optJSONObject(WorkSheetSerializationCostants.CONTENT);
		if (content == null) {
			logger.warn("Sheet content not found for sheet [" + sheetJSON.getString(WorkSheetSerializationCostants.NAME) + "].");
			return null;
		}
		String designer = content.getString(WorkSheetSerializationCostants.DESIGNER);
		if (WorkSheetSerializationCostants.DESIGNER_PIVOT.equals(designer)) {
			toReturn = (CrosstabDefinition) SerializationManager.deserialize(content.getJSONObject(WorkSheetSerializationCostants.CROSSTABDEFINITION), "application/json", CrosstabDefinition.class);
		} else if (WorkSheetSerializationCostants.DESIGNER_TABLE.equals(designer)) {
			toReturn = deserializeTable(content);
		} else {
			toReturn = deserializeChart(content);
		}
		return toReturn;
	}

	private SheetContent deserializeChart(JSONObject content)
			throws JSONException, SerializationException {
		SheetContent toReturn;
		ChartDefinition chart = new ChartDefinition();
		
		JSONObject categoryJSON = content.getJSONObject(WorkSheetSerializationCostants.CATEGORY);
		Attribute category = (Attribute) SerializationManager.deserialize(categoryJSON, "application/json", Attribute.class);
		chart.setCategory(category);
		
		List<Serie> series = new ArrayList<Serie>();
		JSONArray seriesJSON = content.getJSONArray(WorkSheetSerializationCostants.SERIES);
		SerieJSONDeserializer deserialier = new SerieJSONDeserializer();
		for (int i = 0; i < seriesJSON.length(); i++) {
			JSONObject aSerie = seriesJSON.getJSONObject(i);
			Serie serie = deserialier.deserialize(aSerie);
			series.add(serie);
		}
		chart.setSeries(series);
		
		content.remove(WorkSheetSerializationCostants.CATEGORY);
		content.remove(WorkSheetSerializationCostants.SERIES);
		chart.setConfig(content);
		
		toReturn = chart;
		return toReturn;
	}

	private SheetContent deserializeTable(JSONObject content)
			throws JSONException, SerializationException {
		SheetContent toReturn;
		TableDefinition table = new TableDefinition();
		JSONArray fields = content.getJSONArray(WorkSheetSerializationCostants.VISIBLE_SELECT_FIELDS);
		for (int i = 0; i < fields.length(); i++) {
			JSONObject aField = fields.getJSONObject(i);
			String nature = aField.getString("nature");
			if (nature.equals("postLineCalculated") || nature.equals("segment_attribute") || nature.equals("attribute")) {
				Attribute attribute = (Attribute) SerializationManager.deserialize(aField, "application/json", Attribute.class);
				table.addField(attribute);
			} else {
				Measure measure = (Measure) SerializationManager.deserialize(aField, "application/json", Measure.class);
				table.addField(measure);
			}
		}
		toReturn = table;
		return toReturn;
	}

	private List<Attribute> deserializeSheetFilters(JSONObject sheetJSON) throws Exception {
		List<Attribute> toReturn = new ArrayList<Attribute>();
		JSONObject filtersJSON = sheetJSON.optJSONObject(WorkSheetSerializationCostants.FILTERS);
		if (filtersJSON != null) {
			JSONArray filters = filtersJSON.optJSONArray(QuerySerializationConstants.FILTERS);
			if (filters != null && filters.length() > 0) {
				for (int i = 0; i < filters.length(); i++) {
					Attribute attribute = deserializeAttribute(filters.getJSONObject(i));
					toReturn.add(attribute);
				}
			}
		}
		return toReturn;
	}
		
}