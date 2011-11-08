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
package it.eng.spagobi.engines.worksheet.services.export;

import it.eng.qbe.serializer.SerializationException;
import it.eng.spagobi.engines.qbe.crosstable.serializer.json.CrosstabSerializationConstants;
import it.eng.spagobi.tools.dataset.common.metadata.IMetaData;

import java.text.DecimalFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public class MeasureFormatter {

		String[][] measureMetadata;
		boolean measureOnRow;
		DecimalFormat numberFormat;
		String pattern;
		
		public MeasureFormatter(JSONObject crosstabDefinitionJSON, DecimalFormat numberFormat, String pattern) throws SerializationException, JSONException{
			JSONArray measuresJSON = crosstabDefinitionJSON.optJSONArray(CrosstabSerializationConstants.MEASURES);
			JSONObject config =  crosstabDefinitionJSON.optJSONObject(CrosstabSerializationConstants.CONFIG);
			//Assert.assertTrue(rows != null && rows.length() > 0, "No measures specified!");
			this.pattern = pattern;
			this.numberFormat=numberFormat;
			if (measuresJSON != null) {
				measureMetadata = new String[measuresJSON.length()][3];
				for (int i = 0; i < measuresJSON.length(); i++) {
					JSONObject obj = (JSONObject) measuresJSON.get(i);
					measureMetadata[i][0] = obj.getString("name");
					measureMetadata[i][1] = obj.getString("format");
					measureMetadata[i][2] = obj.getString("type");
				}
			}
			measureOnRow = false;
			if(config!=null){
				measureOnRow = config.optString(CrosstabSerializationConstants.MEASURESON).equals(CrosstabSerializationConstants.ROWS);
			}
		}
		
		public String getFormat(Float f, int positionI, int positionJ) {
			int pos;
			String formatted="";
			if(measureOnRow){
				pos = positionI%measureMetadata.length;
			}else{
				pos = positionJ%measureMetadata.length;
			}
			try {
				String decimalPrecision =  (new JSONObject(measureMetadata[pos][1])).optString(IMetaData.DECIMALPRECISION);
				if(decimalPrecision!=null){
					DecimalFormat numberFormat = new DecimalFormat(pattern);
					numberFormat.setMinimumFractionDigits(new Integer(decimalPrecision));
					numberFormat.setMaximumFractionDigits(new Integer(decimalPrecision));
					formatted = numberFormat.format(f);
				}
			} catch (Exception e) {
				formatted = numberFormat.format(f);
			}
			return formatted;
		}
		
		public int getFormatXLS(Float f, int positionI, int positionJ) {
			int pos;
			String formatted="";
			if(measureOnRow){
				pos = positionI%measureMetadata.length;
			}else{
				pos = positionJ%measureMetadata.length;
			}
			try {
				String decimalPrecision =  (new JSONObject(measureMetadata[pos][1])).optString(IMetaData.DECIMALPRECISION);
				return new Integer(decimalPrecision);
			} catch (Exception e) {
				formatted = numberFormat.format(f);
			}
			return 2;
		}

}
