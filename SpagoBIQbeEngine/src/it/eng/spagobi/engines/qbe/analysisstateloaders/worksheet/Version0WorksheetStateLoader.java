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
package it.eng.spagobi.engines.qbe.analysisstateloaders.worksheet;

import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class Version0WorksheetStateLoader extends AbstractWorksheetStateLoader {

	public final static String FROM_VERSION = "0";
    public final static String TO_VERSION = "1";
    
	/** Logger component. */
    private static transient Logger logger = Logger.getLogger(Version0WorksheetStateLoader.class);
	
    public Version0WorksheetStateLoader() {
    	super();
    }
    
    public Version0WorksheetStateLoader(IWorksheetStateLoader loader) {
    	super(loader);
    }
    
	@Override
	public JSONObject convert(JSONObject data) {
		logger.debug("IN");
		
		try {
			logger.debug( "Converting from encoding version [" + FROM_VERSION + "] to encoding version [" + TO_VERSION + "] ..." );
			
			convertSheets(data);
			putGlobalFilters(data);

			logger.debug( "Conversion from encoding version [" + FROM_VERSION + "] to encoding version [" + TO_VERSION + "] terminated succesfully" );
			
		} catch(Throwable t) {
			throw new SpagoBIEngineRuntimeException("Impossible to load from rowData [" + data + "]", t);
		} finally {
			logger.debug("OUT");
		}
		
		return data;
	}

	private void putGlobalFilters(JSONObject data) throws Exception {
		JSONArray filters = new JSONArray();
		data.put("globalFilters", filters);
	}

	private void convertSheets(JSONObject data) throws Exception {
		JSONArray sheets = data.optJSONArray("sheets");
		if (sheets != null && sheets.length() > 0) {
			for (int i = 0; i < sheets.length(); i++) {
				JSONObject aSheet = sheets.getJSONObject(i);
				aSheet.put("filtersOnDomainValues", new JSONArray());
				convertContent(aSheet);
				convertFilters(aSheet);
			}
		}
	}

	private void convertContent(JSONObject aSheet) throws Exception {
		JSONObject content = aSheet.getJSONObject("content");
		String designer = content.getString("designer");
		if (designer.equals("Pivot Table")) {
			JSONObject crosstabDefinition = content.getJSONObject("crosstabDefinition");
			JSONArray rows = crosstabDefinition.getJSONArray("rows");
			convertAttributes(rows);
			JSONArray columns = crosstabDefinition.getJSONArray("columns");
			convertAttributes(columns);
		}
		if (designer.equals("Bar Chart") || designer.equals("Line Chart") || designer.equals("Pie Chart")) {
			JSONObject category = content.getJSONObject("category");
			convertAttribute(category);
		}
		if (designer.equals("Table")) {
			JSONObject fields = content.getJSONObject("visibleselectfields");
			convert(fields);
		}
	}
	
	private void convertFilters(JSONObject aSheet) throws Exception {
		JSONObject filtersObj = aSheet.getJSONObject("filters");
		JSONArray filters = filtersObj.getJSONArray("filters");
		convertAttributes(filters);
	}

	private void convertAttributes(JSONArray attributes) throws Exception {
		if (attributes != null && attributes.length() > 0) {
			for (int i = 0; i < attributes.length(); i++) {
				JSONObject anAttribute = attributes.getJSONObject(i);
				convertAttribute(anAttribute);
			}
		}
	}
	
	private void convertAttribute(JSONObject attribute) throws Exception {
		JSONArray values = new JSONArray();
		attribute.put("values", values.toString());
	}

}
