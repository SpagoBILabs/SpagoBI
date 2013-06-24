/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.engine.mobile.template;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public class AbstractMobileTemplateJSONSerializer {

	private static Logger logger = Logger.getLogger(AbstractMobileTemplateJSONSerializer.class);
	
	public static JSONObject getDocumentPropertiesJSON(IMobileTemplateInstance istance) throws JSONException{
		JSONObject toreturn = new JSONObject();
		Iterator<String> keyIter = istance.getDocumentProperties().keySet().iterator();
		while (keyIter.hasNext()) {
			String string = (String) keyIter.next();
			Object value = istance.getDocumentProperties().get(string);
			if(value instanceof Number || value instanceof String || value instanceof JSONObject){
				toreturn.put(string, value);
			}else{
				if(value!=null){
					logger.error("TYPEERROR: the type for values for the documents properties can be Numeric, String or JSONObject");
					throw new JSONException("TYPEERROR: the type for values for the documents properties can be Numeric, String or JSONObject");
				}
			}
		}
		return toreturn;
	}
	
}
