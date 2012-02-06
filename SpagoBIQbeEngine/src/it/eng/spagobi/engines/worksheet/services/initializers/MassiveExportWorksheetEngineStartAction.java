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
package it.eng.spagobi.engines.worksheet.services.initializers;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.engines.worksheet.services.export.ExportWorksheetAction;
import it.eng.spagobi.services.proxy.SbiDocumentServiceProxy;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @authors Giulio Gavardi
 *          
 *
 */
public class MassiveExportWorksheetEngineStartAction extends WorksheetEngineStartAction {	


	/** Logger component. */
	private static transient Logger logger = Logger.getLogger(MassiveExportWorksheetEngineStartAction.class);

	@Override
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) {
		logger.debug("IN");
		super.service(serviceRequest, serviceResponse);
		Locale locale = getLocale();
		String docId = getDocumentId();
		logger.debug("document Id: "+docId);

		SbiDocumentServiceProxy proxy = new SbiDocumentServiceProxy(getUserProfile().getUserId().toString(), getHttpSession());
		logger.debug("recover paramters for metadata");		
		try {
			String jSonPars = proxy.getDocumentAnalyticalDriversJSON(Integer.valueOf(docId), locale.getLanguage(), locale.getCountry());
			logger.debug("parameters for metadata "+jSonPars);		
			if(jSonPars != null){
				JSONArray array = new JSONArray(jSonPars);
				// add name and values
				for (int i =0; i <array.length(); i++){
					JSONObject par = (JSONObject) array.get(i);
					//String name = par.getString("label");
					String id = par.getString("id");
					String name = par.getString("label");

					String nameDescription = getAttributeAsString(id+"_description");
					String value = getAttributeAsString(id);
					// put name, description and value
					par.put("name", name);
					if(nameDescription != null) par.put("description", nameDescription);
					if(value != null) par.put("value", value);
					else par.put("value", "");
				}
				logger.debug("add parameters JSON array "+array);
				serviceResponse.setAttribute(ExportWorksheetAction.PARAMETERS, array);
			}

		} catch (Exception e) {
			logger.debug("Error in retrieving parameters information for metadata purpose, go on aniway");
		}





		logger.debug("OUT");
	}





















}
