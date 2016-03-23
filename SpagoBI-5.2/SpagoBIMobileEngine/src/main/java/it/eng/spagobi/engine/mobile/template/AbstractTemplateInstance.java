/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

/**
 * @authors Monica Franceschini (Monica.Franceschini@eng.it), Alberto Ghedin (alberto.ghedin@eng.it)
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

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class AbstractTemplateInstance {
	//table template properties
	protected JSONObject drill = new JSONObject();
	protected Map<String, Object> documentProperties = new HashMap<String, Object>();
	protected SourceBean template;
	protected Map<String, String> paramsMap = new HashMap<String, String>();
	protected Map<String, String> notNullParamsMap = null;
	private JSONObject title = new JSONObject();
	
	private static transient Logger logger = Logger.getLogger(AbstractTemplateInstance.class);
	
	public void loadTemplateFeatures() throws Exception {
		buildGenericComponents();
	}
	
	protected void buildDrillJSON() throws Exception {
		
		SourceBean confSB = null;
		String documentName = null;
		
		logger.debug("IN");
		confSB = (SourceBean)template.getAttribute(MobileConstants.DRILL_TAG);
		if(confSB == null) {
			logger.debug("Cannot find title drill settings: tag name " + MobileConstants.DRILL_TAG);
			return;
		}
		documentName = (String)confSB.getAttribute(MobileConstants.DRILL_DOCUMENT_ATTR);
		List paramslist = (List)template.getAttributeAsList(MobileConstants.DRILL_TAG+"."+MobileConstants.PARAM_TAG);

		if(paramslist != null){
			JSONArray params = new JSONArray();
			for(int k=0; k<paramslist.size(); k++){
				SourceBean param = (SourceBean)paramslist.get(k);
				String paramName = (String)param.getAttribute(MobileConstants.PARAM_NAME_ATTR);
				String paramType = (String)param.getAttribute(MobileConstants.PARAM_TYPE_ATTR);
				String paramValue = (String)param.getAttribute(MobileConstants.PARAM_VALUE_ATTR);
				JSONObject paramJSON = new JSONObject();
				paramJSON.put("paramName", paramName);
				paramJSON.put("paramType", paramType);
				
				//FILLS RELATIVE TYPE PARAMETERS' VALUE FROM REQUEST
				if(paramType.equalsIgnoreCase(MobileConstants.PARAM_TYPE_RELATIVE)){
					paramJSON.putOpt("paramValue", paramsMap.get(paramName));
				}else{
					paramJSON.putOpt("paramValue", paramValue);//should be applied only on absolute type
				}
				params.put(paramJSON);
			}
			drill.put("params", params);
		}
		
		drill.put("document", documentName);

		logger.debug("OUT");		

	}
	
	protected void buildTitleJSON() throws Exception {
		
		SourceBean confSB = null;
		String titleName = null;
		
		logger.debug("IN");
		confSB = (SourceBean)template.getAttribute(MobileConstants.TITLE_TAG);
		if(confSB == null) {
			logger.warn("Cannot find title configuration settings: tag name " + MobileConstants.TITLE_TAG);
			return;
		}
		titleName = (String)confSB.getAttribute(MobileConstants.TITLE_VALUE_ATTR);

//		Map<String, String> params = getNotNullPrameters();
//		
		String titleWithPars = titleName;
//		if(params!=null && titleName!=null){
//			titleWithPars = StringUtilities.substituteParametersInString(titleName, params, null, false);	
//		}
		
		
		String titleStyle = (String)confSB.getAttribute(MobileConstants.TITLE_STYLE_ATTR);
		
		title.put("value", titleWithPars);
		title.put("style", titleStyle);
		documentProperties.put("title", title);
		logger.debug("OUT");		

	}
	
	public void buildGenericComponents() throws Exception {
		buildDrillJSON();
		buildTitleJSON();
		getHeader();
		getFooter();
		getDocumentContainerStyle();
	}
	
	public void getHeader() throws Exception{
		String header = (String)template.getCharacters(MobileConstants.HEADER);
		if(header !=null){
//			Map<String, String> params = getNotNullPrameters();
//			if(params!=null){
//				header= StringUtilities.substituteParametersInString(header, params, null, false);
//			}
			
		}
		documentProperties.put("header",header);
	}
	
	public void getFooter() throws Exception{
		String footer = (String)template.getCharacters(MobileConstants.FOOTER);
		if(footer !=null){
//			Map<String, String> params = getNotNullPrameters();
//			if(params!=null){
//				footer= StringUtilities.substituteParametersInString(footer,params , null, false);
//			}
		}
		documentProperties.put("footer",footer);
	}
	
	public void getDocumentContainerStyle(){
		documentProperties.put("style", (String)template.getAttribute(MobileConstants.DOCUMENT_STYLE_ATTR));
	}

	public Map<String, Object> getDocumentProperties() {
		return documentProperties;
	}
	
	protected Map<String, String> getNotNullPrameters(){
		if(notNullParamsMap==null || notNullParamsMap.size()==0){
			notNullParamsMap = this.paramsMap;
			Iterator it = notNullParamsMap.keySet().iterator();
			while(it.hasNext()){
				String key = (String)it.next();

				if(notNullParamsMap.get(key)== null){
					notNullParamsMap.put(key, " ");			
				}
			}
		}
		return notNullParamsMap;
	}

}
