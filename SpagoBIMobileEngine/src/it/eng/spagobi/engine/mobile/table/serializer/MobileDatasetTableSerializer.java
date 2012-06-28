/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engine.mobile.table.serializer;

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
	
	public Object write(IDataStore dataStore, JSONObject features) throws RuntimeException {
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
			result.put("values", dsValuesJSON);

			result.put("features", features);
			result.put("total", recNo);
		} catch (JSONException e) {
			logger.error(e);
		} finally {
			logger.debug("OUT");
		}
		
		return result;
	}
}
