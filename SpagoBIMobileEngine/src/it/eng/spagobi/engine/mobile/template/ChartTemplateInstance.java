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
