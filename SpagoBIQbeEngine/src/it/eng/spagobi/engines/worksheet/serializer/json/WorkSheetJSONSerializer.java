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

import it.eng.qbe.serializer.ISerializer;
import it.eng.qbe.serializer.SerializationException;
import it.eng.qbe.serializer.SerializationManager;
import it.eng.spagobi.engines.worksheet.bo.Attribute;
import it.eng.spagobi.engines.worksheet.bo.Field;
import it.eng.spagobi.engines.worksheet.bo.Serie;
import it.eng.spagobi.engines.worksheet.bo.Sheet;
import it.eng.spagobi.engines.worksheet.bo.Sheet.FiltersPosition;
import it.eng.spagobi.engines.worksheet.bo.SheetContent;
import it.eng.spagobi.engines.worksheet.bo.WorkSheetDefinition;
import it.eng.spagobi.engines.worksheet.widgets.ChartDefinition;
import it.eng.spagobi.engines.worksheet.widgets.CrosstabDefinition;
import it.eng.spagobi.engines.worksheet.widgets.TableDefinition;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
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
			
			JSONArray sheets = serializeSheets(workSheetDefinition.getSheets());
			toReturn.put(WorkSheetSerializationCostants.SHEETS, sheets);
			
			JSONArray globalFilters = serializeFilters(workSheetDefinition.getGlobalFilters());
			toReturn.put(WorkSheetSerializationCostants.GLOBAL_FILTERS, globalFilters);
			
		} catch (Throwable t) {
			throw new SerializationException("An error occurred while serializing object: " + o, t);
		} finally {
			logger.debug("OUT");
		}
		logger.debug("Worksheet serialized");
		return toReturn;
	}
	
	private JSONObject serializeSheetFilters(List<Attribute> globalFilters, FiltersPosition filtersPosition) throws SerializationException, JSONException {
		JSONArray globalFiltersJSON = serializeFilters(globalFilters);
		JSONObject toReturn = new JSONObject();
		toReturn.put(WorkSheetSerializationCostants.FILTERS, globalFiltersJSON);
		toReturn.put(WorkSheetSerializationCostants.POSITION, filtersPosition.name().toLowerCase());
		return toReturn;
	}
	
	private JSONArray serializeFilters(List<Attribute> globalFilters) throws SerializationException, JSONException {
		JSONArray globalFiltersJSON = new JSONArray();
		Iterator<Attribute> it = globalFilters.iterator();
		while (it.hasNext()) {
			JSONObject js = (JSONObject) SerializationManager.serialize(it.next(), "application/json");
			globalFiltersJSON.put(js);
		}
		return globalFiltersJSON;
	}

	private JSONArray serializeSheets(List<Sheet> sheets) throws SerializationException {
		JSONArray jsonSheets = new JSONArray();
		for(int i=0; i<sheets.size(); i++){
			jsonSheets.put(serializeSheet(sheets.get(i)));
		}
		return jsonSheets;
	}
	
	private JSONObject serializeSheet(Sheet sheet) throws SerializationException {
		logger.debug("IN");
		logger.debug("Serializing the sheet " + sheet.getName());
		JSONObject jsonSheet = new JSONObject();
		try {
			jsonSheet.put(WorkSheetSerializationCostants.NAME, sheet.getName());
			jsonSheet.put(WorkSheetSerializationCostants.LAYOUT, sheet.getLayout());
			jsonSheet.put(WorkSheetSerializationCostants.HEADER, sheet.getHeader());
			jsonSheet.put(WorkSheetSerializationCostants.FILTERS, serializeSheetFilters(sheet.getFilters(), sheet.getFiltersPosition()));
			jsonSheet.put(WorkSheetSerializationCostants.CONTENT, serializeContent(sheet.getContent()));
			jsonSheet.put(WorkSheetSerializationCostants.FILTERS_ON_DOMAIN_VALUES, serializeFilters(sheet.getFiltersOnDomainValues()));
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

	private JSONObject serializeContent(SheetContent content) throws SerializationException, JSONException {
		if (content == null) {
			return new JSONObject();
		}
		if (content instanceof CrosstabDefinition) {
			JSONObject toReturn = new JSONObject();
			toReturn.put(WorkSheetSerializationCostants.CROSSTABDEFINITION, 
					(JSONObject) SerializationManager.serialize(content, "application/json"));
			toReturn.put(WorkSheetSerializationCostants.DESIGNER, WorkSheetSerializationCostants.DESIGNER_PIVOT);
			return toReturn;
		}
		if (content instanceof ChartDefinition) {
			return serializeChart((ChartDefinition) content);
		}
		if (content instanceof TableDefinition) {
			return serializeTable((TableDefinition) content);
		}
		else
			throw new SpagoBIEngineRuntimeException("Unknown sheet content type: " + content.getClass().getName());
	}

	private JSONObject serializeTable(TableDefinition table) throws SerializationException, JSONException {
		JSONObject toReturn = new JSONObject();
		JSONArray fieldsJSON = new JSONArray();
		List<Field> fields = table.getFields();
		for (int i = 0; i < fields.size(); i++) {
			Field field = fields.get(i);
			fieldsJSON.put(SerializationManager.serialize(field, "application/json"));
		}
		toReturn.put(WorkSheetSerializationCostants.VISIBLE_SELECT_FIELDS, fieldsJSON);
		toReturn.put(WorkSheetSerializationCostants.DESIGNER, WorkSheetSerializationCostants.DESIGNER_TABLE);
		return toReturn;
	}

	private JSONObject serializeChart(ChartDefinition chart) throws SerializationException, JSONException {
		String config = chart.getConfig().toString();
		JSONObject toReturn = new JSONObject(config);
		toReturn.put(WorkSheetSerializationCostants.CATEGORY, SerializationManager.serialize(chart.getCategory(), "application/json"));

		JSONArray seriesJSON = new JSONArray();
		List<Serie> series = chart.getSeries();
		SerieJSONSerializer serialier = new SerieJSONSerializer();
		for (int i = 0; i < series.size(); i++) {
			seriesJSON.put(serialier.serialize(series.get(i)));
		}
		toReturn.put(WorkSheetSerializationCostants.SERIES, seriesJSON);
		
		return toReturn;
	}

}
