/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.engine.mobile.template;

import java.util.HashMap;
import java.util.Iterator;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.engine.mobile.MobileConstants;
import it.eng.spagobi.utilities.json.JSONTemplateUtils;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ChartTemplateInstance extends AbstractTemplateInstance  implements IMobileTemplateInstance{
	
	private JSONObject features;

	
	private static transient Logger logger = Logger.getLogger(ChartTemplateInstance.class);


	public ChartTemplateInstance(SourceBean template, HashMap<String, String> paramsMap) {
		this.template = template;
		this.paramsMap = paramsMap;
	}


	@Override
	public void loadTemplateFeatures() throws Exception {
		JSONTemplateUtils ju = new JSONTemplateUtils();
		JSONArray array = toJSONArray(this.paramsMap);
		features = ju.getJSONTemplateFromXml(this.template, array);
		if(features == null){
			features = new JSONObject(); 
		}
		buildDrillJSON();
		setFeatures();
	}

	private JSONArray toJSONArray(HashMap<String, String> paramsMap) {
		JSONArray array = new JSONArray();
		if (paramsMap != null && !paramsMap.isEmpty()) {
			Iterator<String> it = paramsMap.keySet().iterator();
			while (it.hasNext()) {
				String name = it.next();
				JSONObject obj = new JSONObject();
				try {
					obj.put("name", name);
					obj.put("value", paramsMap.get(name));
				} catch (JSONException e) {
					throw new RuntimeException("cannot convert [" + paramsMap.toString() + "] into a JSONArray");
				}
				array.put(obj);
			}
		}
		return array;
	}


	@Override
	public JSONObject getFeatures() {
		return features;
	}
	
	public void setFeatures() {
		try {
			features.put("drill", drill);
		} catch (JSONException e) {
			logger.error("Unable to set features");
		}		 
	}
}
