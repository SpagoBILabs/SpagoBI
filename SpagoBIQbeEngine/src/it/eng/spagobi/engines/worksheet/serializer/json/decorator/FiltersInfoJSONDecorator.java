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
package it.eng.spagobi.engines.worksheet.serializer.json.decorator;


import it.eng.spagobi.engines.worksheet.bo.FiltersInfo;
import it.eng.spagobi.engines.worksheet.bo.WorkSheetDefinition;
import it.eng.spagobi.engines.worksheet.serializer.json.WorkSheetSerializationUtils;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.utilities.json.AbstractJSONDecorator;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Davide Zerbetto (davide.zerbetto@eng.it)
 *
 */
public class FiltersInfoJSONDecorator extends AbstractJSONDecorator {

	public final static String VALUES_SEPARATOR = ", ";
	
	private WorkSheetDefinition workSheetDefinition = null;
	private IDataSet dataSet = null;
	
	public FiltersInfoJSONDecorator(WorkSheetDefinition workSheetDefinition, IDataSet dataSet) {
		this.workSheetDefinition = workSheetDefinition;
		this.dataSet = dataSet;
	}
	
	@Override
	protected void doDecoration(JSONObject json) {
		try {
			JSONArray sheets = json.getJSONArray(WorkSheetSerializationUtils.SHEETS);
			for (int i = 0 ; i < sheets.length() ; i++) {
				JSONObject sheet = sheets.getJSONObject(i);
				addFiltersInfo(sheet);
			}
		} catch (Exception e) {
			throw new RuntimeException("Error while decorating JSON Object", e);
		}
	}

	private void addFiltersInfo(JSONObject sheetJSON) throws Exception {
		String sheetName = sheetJSON.getString(WorkSheetSerializationUtils.NAME);
		FiltersInfo infos = new FiltersInfo(workSheetDefinition, dataSet);
		Map<String, List<String>> filters = infos.getFiltersInfoAsMap(sheetName);
		JSONArray array = toJSONArray(filters);
		sheetJSON.put(WorkSheetSerializationUtils.SHEET_FILTERS_INFO, array);
	}



	private JSONArray toJSONArray(Map<String, List<String>> filters) {
		JSONArray array = new JSONArray();
		Iterator<String> iterator = filters.keySet().iterator();
		while (iterator.hasNext()) {
			String fieldName = iterator.next();
			List<String> values = filters.get(fieldName);
			StringBuffer buffer = new StringBuffer();
			for ( int i = 0 ; i < values.size() ; i++ ) {
				String aValue = values.get(i);
				buffer.append(aValue);
				if ( i < values.size() - 1 ) {
					buffer.append(VALUES_SEPARATOR);
				}
			}
			String value = buffer.toString();
			JSONArray temp = new JSONArray();
			temp.put(fieldName);
			temp.put(value);
			array.put(temp);
		}
		return array;
	}

}
