/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.tools.catalogue.service;

import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.serializer.SerializationException;
import it.eng.spagobi.commons.serializer.SerializerFactory;
import it.eng.spagobi.commons.services.AbstractSpagoBIAction;
import it.eng.spagobi.tools.catalogue.bo.MetaModel;
import it.eng.spagobi.tools.catalogue.dao.IMetaModelsDAO;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.service.JSONSuccess;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GetMetaModelsAction extends AbstractSpagoBIAction {

	// logger component
	public static Logger logger = Logger.getLogger(GetMetaModelsAction.class);

	public static String START = "start";
	public static String LIMIT = "limit";
	public static Integer START_DEFAULT = 0;
	public static Integer LIMIT_DEFAULT = 15;
	
	@Override
	public void doService() {
		
		logger.debug("IN");
		
		try {
			IMetaModelsDAO dao = DAOFactory.getMetaModelsDAO();
			dao.setUserProfile(this.getUserProfile());
			List<MetaModel> allModels = dao.loadAllMetaModels();
			logger.debug("Read " + allModels.size() + " existing models");
			
			Integer start = this.getStart();
			logger.debug("Start : " + start );
			Integer limit = this.getLimit();
			logger.debug("Limit : " + limit );
			
			int startIndex = Math.min(start, allModels.size());
			int stopIndex = Math.min(start + limit, allModels.size());
			List<MetaModel> models = allModels.subList(startIndex, stopIndex);

			try {
				JSONArray modelsJSON = (JSONArray) SerializerFactory.getSerializer("application/json").serialize(models, null);
				JSONObject rolesResponseJSON = createJSONResponse(
						modelsJSON, allModels.size());
				writeBackToClient(new JSONSuccess(rolesResponseJSON));
			} catch (IOException e) {
				throw new SpagoBIServiceException(SERVICE_NAME,
						"Impossible to write back the responce to the client",
						e);
			} catch (JSONException e) {
				throw new SpagoBIServiceException(SERVICE_NAME,
						"Cannot serialize objects into a JSON object", e);
			} catch (SerializationException e) {
				throw new SpagoBIServiceException(SERVICE_NAME,
						"Cannot serialize objects into a JSON object", e);
			}

		} finally {
			logger.debug("OUT");
		}
		
	}
	
	private Integer getStart() {
		Integer start = getAttributeAsInteger( START );
		if (start == null) {
			start = START_DEFAULT;
		}
		return start;
	}
	
	private Integer getLimit() {
		Integer limit = getAttributeAsInteger( LIMIT );
		if (limit == null) {
			limit = LIMIT_DEFAULT;
		}
		return limit;
	}
	
	private JSONObject createJSONResponse(JSONArray rows, Integer totalResNumber)
			throws JSONException {
		JSONObject results;
		results = new JSONObject();
		results.put("total", totalResNumber);
		results.put("title", "MetaModels");
		results.put("rows", rows);
		return results;
	}
	
}
