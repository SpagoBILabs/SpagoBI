package it.eng.spagobi.engine.mobile.template;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.engine.mobile.MobileConstants;

import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TableTemplateInstance implements IMobileTemplateInstance{
	
	//table template properties
	private JSONObject title = new JSONObject();
	private JSONArray columns = new JSONArray();
	private JSONArray fields = new JSONArray();
	private JSONArray conditions = new JSONArray();
	
	private JSONObject features = new JSONObject();
	


	private SourceBean template;
	
	private static transient Logger logger = Logger.getLogger(TableTemplateInstance.class);


	public TableTemplateInstance(SourceBean template) {
		this.template = template;
		
	}

	private void buildColumnsJSON() throws Exception {

		logger.debug("IN");
		List cols = (List)template.getAttributeAsList(MobileConstants.COLUMNS_TAG+"."+MobileConstants.COLUMN_TAG);
		if(cols == null) {
			logger.warn("Cannot find columns configuration settings: tag name " + MobileConstants.COLUMNS_TAG+"."+MobileConstants.COLUMN_TAG);
		}
		Vector alarms = new Vector();
		for(int i=0; i<cols.size(); i++){
			SourceBean column = (SourceBean)cols.get(i);
			JSONObject colJSON = new JSONObject();
			String value = (String)column.getAttribute(MobileConstants.COLUMN_VALUE_ATTR);
			colJSON.put("mapping", value);
			
			String header = (String)column.getAttribute(MobileConstants.COLUMN_HEADER_ATTR);
			colJSON.put("header", header);

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
		}
		titleName = (String)confSB.getAttribute(MobileConstants.TITLE_VALUE_ATTR);
		String titleStyle = (String)confSB.getAttribute(MobileConstants.TITLE_STYLE_ATTR);
		
		title.put("value", titleName);
		title.put("style", titleStyle);

		logger.debug("OUT");		

	}

	@Override
	public void loadTemplateFeatures() throws Exception {
		buildTitleJSON();
		buildColumnsJSON();
		setFeatures();
	}

	@Override
	public String getDocumentType() {
		// TODO Auto-generated method stub
		return MobileConstants.TABLE_TYPE;
	}

	public void setFeatures() {
		try {
			features.put("title", title);
			features.put("columns", columns);
			features.put("fields", fields);
			features.put("conditions", conditions);
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
