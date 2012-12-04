/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

/**
 * @authors Monica Franceschini (Monica.Franceschini@eng.it)
 *
 */
package it.eng.spagobi.engine.mobile.template;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.engine.mobile.MobileConstants;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TableTemplateInstance extends AbstractTemplateInstance implements IMobileTemplateInstance{
	
	//table template properties
	private JSONObject title = new JSONObject();
	private JSONArray columns = new JSONArray();
	private JSONArray fields = new JSONArray();
	private JSONArray conditions = new JSONArray();

	private JSONObject features = new JSONObject();

	
	private static transient Logger logger = Logger.getLogger(TableTemplateInstance.class);


	public TableTemplateInstance(SourceBean template, HashMap<String, String> params) {
		this.template = template;
		this.paramsMap = params;
	}

	private void buildColumnsJSON() throws Exception {

		logger.debug("IN");
		List cols = (List)template.getAttributeAsList(MobileConstants.COLUMNS_TAG+"."+MobileConstants.COLUMN_TAG);
		if(cols == null) {
			logger.warn("Cannot find columns configuration settings: tag name " + MobileConstants.COLUMNS_TAG+"."+MobileConstants.COLUMN_TAG);
			return;
		}
		Vector alarms = new Vector();
		for(int i=0; i<cols.size(); i++){
			SourceBean column = (SourceBean)cols.get(i);
			JSONObject colJSON = new JSONObject();
			String value = (String)column.getAttribute(MobileConstants.COLUMN_VALUE_ATTR);
			colJSON.put("mapping", value);
			
			String header = (String)column.getAttribute(MobileConstants.COLUMN_HEADER_ATTR);
			colJSON.put("header", header);
			
			String colWidth = (String)column.getAttribute(MobileConstants.COLUMN_WIDTH_ATTR);
			colJSON.putOpt("width", colWidth);

			String style = (String)column.getAttribute(MobileConstants.COLUMN_STYLE_ATTR);
			if(style!= null){
				colJSON.put("style", style);
			}
			String alarm = (String)column.getAttribute(MobileConstants.COLUMN_ALARM_ATTR);
			if(alarm!= null){
				colJSON.put("alarm", alarm);
			}
			List conditionslist = (List)column.getAttributeAsList(MobileConstants.CONDITIONS_TAG+"."+MobileConstants.CONDITION_TAG);
			if(conditionslist != null){
				for(int k=0; k<conditionslist.size(); k++){
					SourceBean condition = (SourceBean)conditionslist.get(k);
					String styleCond = (String)condition.getAttribute(MobileConstants.CONDITION_STYLE_ATTR);
					String conditionCond = (String)condition.getCharacters();
					JSONObject condJSON = new JSONObject();
					condJSON.put("column", value);
					condJSON.put("style", styleCond);
					condJSON.put("alarm", alarm);
					condJSON.put("condition", conditionCond);
					conditions.put(condJSON);
					if(!alarms.contains(alarm)){
						fields.put(alarm);//check that it has been put only once!!!!
						alarms.add(alarm);
					}
				}
			}
			
			fields.put(value);
			columns.put(colJSON);
		}


		logger.debug("OUT");		

	}

	private void buildTitleJSON() throws Exception {
		
		SourceBean confSB = null;
		String titleName = null;
		
		logger.debug("IN");
		confSB = (SourceBean)template.getAttribute(MobileConstants.TITLE_TAG);
		if(confSB == null) {
			logger.warn("Cannot find title configuration settings: tag name " + MobileConstants.TITLE_TAG);
			return;
		}
		titleName = (String)confSB.getAttribute(MobileConstants.TITLE_VALUE_ATTR);
		Map parNotNull = this.paramsMap;
		Iterator it = parNotNull.keySet().iterator();
		while(it.hasNext()){
			String key = (String)it.next();

			if(parNotNull.get(key)== null){
				parNotNull.put(key, " ");			
			}
		}
		String titleWithPars = StringUtilities.substituteParametersInString(titleName, ((Map)parNotNull), null, false);
		
		String titleStyle = (String)confSB.getAttribute(MobileConstants.TITLE_STYLE_ATTR);
		
		title.put("value", titleWithPars);
		title.put("style", titleStyle);

		logger.debug("OUT");		

	}

	@Override
	public void loadTemplateFeatures() throws Exception {
		buildTitleJSON();
		buildColumnsJSON();
		buildDrillJSON();
		setFeatures();
	}

	public void setFeatures() {
		try {
			features.put("title", title);
			features.put("columns", columns);
			features.put("fields", fields);
			features.put("conditions", conditions);
			features.put("drill", drill);
		} catch (JSONException e) {
			logger.error("Unable to set features");
		}		 
	}

	@Override
	public JSONObject getFeatures() {
		// TODO Auto-generated method stub
		return features;
	}

}
