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

/**
 * @authors Monica Franceschini (Monica.Franceschini@eng.it)
 *
 */
package it.eng.spagobi.engine.mobile.template;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.engine.mobile.MobileConstants;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class AbstractTemplateInstance {
	//table template properties
	protected JSONObject drill = new JSONObject();

	protected SourceBean template;
	protected HashMap<String, String> paramsMap = new HashMap<String, String>();
	
	private static transient Logger logger = Logger.getLogger(AbstractTemplateInstance.class);
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
}
