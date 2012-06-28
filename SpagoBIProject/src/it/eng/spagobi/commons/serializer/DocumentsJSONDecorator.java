/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.commons.serializer;

import it.eng.spago.error.EMFInternalError;
import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.commons.utilities.ObjectsAccessVerifier;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public class DocumentsJSONDecorator {

	public static final String DECORATORS = "decorators";
	public static final String IS_SAVABLE = "isSavable";
	
	static private Logger logger = Logger.getLogger(DocumentsJSONDecorator.class);
	
	public static JSONObject decoreDocument(JSONObject document, IEngUserProfile profile) throws JSONException{
		JSONObject decorators = new JSONObject();
		document.put(DECORATORS, decorators);
		addExecutabilityToFunctionalities(document, profile);
		return document; 
	}
	
	private static void addExecutabilityToFunctionalities(JSONObject document, IEngUserProfile profile) throws JSONException{
		if(profile!=null){
			JSONArray functionalities =  (JSONArray)document.get(DocumentsJSONSerializer.FUNCTIONALITIES);
			try {
				document.getJSONObject(DECORATORS).put(IS_SAVABLE, ObjectsAccessVerifier.isAbleToSave(functionalities, profile));
			} catch (EMFInternalError e) {
				logger.error("Error reading if the object is savable by the user "+profile.getUserUniqueIdentifier(), e);
				throw new SpagoBIRuntimeException("Error reading if the object is savable by the user "+profile.getUserUniqueIdentifier(), e);
			}
		}
	}
	
}
