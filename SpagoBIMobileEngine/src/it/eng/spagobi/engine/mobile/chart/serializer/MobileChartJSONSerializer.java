/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.engine.mobile.chart.serializer;

import it.eng.spagobi.engine.mobile.template.AbstractMobileTemplateJSONSerializer;
import it.eng.spagobi.engine.mobile.template.IMobileTemplateInstance;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datawriter.JSONDataWriter;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public class MobileChartJSONSerializer extends AbstractMobileTemplateJSONSerializer{
	
	private static Logger logger = Logger.getLogger(MobileChartJSONSerializer.class);
	
	public Object write(IDataStore dataStore, IMobileTemplateInstance templateInstance) throws Exception {
		JSONObject toReturn = new JSONObject();
		JSONObject chartConfigFromTemplate = templateInstance.getFeatures();
		logger.debug("Finished to get the chart config from the template. ");
		
		logger.debug("Getting the document dataset...");

		logger.debug("Got document dataset");

		
		Map<String,Object> parametersForWriter = new HashMap<String,Object>();
		parametersForWriter.put(JSONDataWriter.PROPERTY_ADJUST, true);
		
		JSONDataWriter dataSetWriter = new JSONDataWriter(parametersForWriter);
		JSONObject dataStroreJSON =  (JSONObject) dataSetWriter.write(dataStore);
		JSONObject dataStroreJSONMetdaData = dataStroreJSON.getJSONObject(JSONDataWriter.METADATA);

		JSONObject extDataStore = new JSONObject();
		String dataPosition = dataStroreJSONMetdaData.getString("root");
		JSONArray data = dataStroreJSON.getJSONArray(dataPosition);
		extDataStore.put("fields", dataStroreJSONMetdaData.getJSONArray("fields"));
		extDataStore.put("data", data);
		//extDataStore.put("xtype", "jsonstore");
		logger.debug("Data store builded");

		chartConfigFromTemplate.put("store", extDataStore);
		toReturn.put("config", chartConfigFromTemplate);
		toReturn.put("store", extDataStore);
		toReturn.put("documentProperties", getDocumentPropertiesJSON(templateInstance));
		return toReturn;
	}

}
