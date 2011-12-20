/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.
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
package it.eng.spagobi.engine.mobile.util;

import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IField;
import it.eng.spagobi.tools.dataset.common.datastore.IRecord;
import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MobileDatasetTableSerializer {
	private static final String MODEL_ERROR = "error";

	public static transient Logger logger = Logger.getLogger(MobileDatasetTableSerializer.class);
	public Object write(IDataStore dataStore, JSONArray conditions) throws RuntimeException {
		JSONObject  result = null;
		int recNo;
		IRecord record;
		JSONObject recordJSON;
		try {
			result = new JSONObject();

			result.put(MODEL_ERROR, false);
			// put udpValues assocated to ModelInstance Node

			JSONArray dsValuesJSON = new JSONArray();

			// records
			recNo = 0;
			Iterator records = dataStore.iterator();
			while(records.hasNext()) {
				record = (IRecord)records.next();
				recordJSON = new JSONObject();
				
				for(int i = 0; i < dataStore.getMetaData().getFieldCount(); i++) {
					IFieldMetaData fieldMetaData = dataStore.getMetaData().getFieldMeta(i);
					String key = fieldMetaData.getAlias() != null ? fieldMetaData.getAlias() : fieldMetaData.getName();
					
					IField field = record.getFieldAt( dataStore.getMetaData().getFieldIndex( key ) );

					String fieldValue = "";
					if(field.getValue() != null) {

						fieldValue =  field.getValue().toString();
					}
					
					recordJSON.put(key, fieldValue);
				}
				
				dsValuesJSON.put(recordJSON);
				recNo++;
			}
			result.put("columns", dsValuesJSON);
			result.put("conditions", conditions);
			result.put("total", recNo);
		} catch (JSONException e) {
			logger.error(e);
		} finally {
			logger.debug("OUT");
		}
		
		return result;
	}
}
