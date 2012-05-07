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
