/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.geo.service;

import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanException;
import it.eng.spagobi.engines.geo.GeoEngineException;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceExceptionHandler;
import it.eng.spagobi.utilities.service.JSONSuccess;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

// TODO: Auto-generated Javadoc
/**
 * The Class GetMapsAction.
 * 
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class GetMapsAction extends AbstractGeoEngineAction {
	
	// REQUEST PARAMETERS	
	public static final String FEATURE_NAME = "featureName";

	// RESPONSE PARAMETERS
	// ...
	
	// DEFAULT VALUES
	//...
	
	// Default serial version number (just to keep eclipse happy).
	private static final long serialVersionUID = 1L;
	
	// Logger component
	private static transient Logger logger = Logger.getLogger(GetMapsAction.class);
	

	public void service(SourceBean request, SourceBean response) {
		
		String featureName = null;
		
		
		List maps = null;
		StringBuffer buffer = null;
		SourceBean result = null;
		
		logger.debug("IN");
		
		try {
			super.service(request, response);
			
			featureName = getAttributeAsString(FEATURE_NAME);
			logger.debug("Parameter [" + FEATURE_NAME + "] is equal to [" + featureName + "]");
			
			Assert.assertNotNull(featureName, "Parameter [" + FEATURE_NAME + "] cannot be null");
			Assert.assertNotNull(getEngineInstance(), "It's not possible to execute " + this.getActionName() + " service before having properly created an instance of GeoInstance class");
			Assert.assertNotNull(getEngineInstance(), "It's not possible to execute " + this.getActionName() + " service before having properly created a MapProvider into GeoInstance object");
			
			try {
				maps = getGeoEngineInstance().getMapProvider().getMapNamesByFeature(featureName);
				if(maps == null) throw new SpagoBIEngineServiceException(getActionName(),"The map list returned by the map provider is NULL");
			} catch (Exception e) {
				logger.error("Impossible to get a list of map containing layer [" + featureName + "] ");
				throw new SpagoBIEngineServiceException(getActionName(),"Impossible to get a list of map containing layer [" + featureName + "] ", e);
			}
			
			if(maps.size() == 0) {
				logger.warn("The list of map containing layer [" + featureName + "] is empty");
			}
			
			
			JSONObject responseJSON = new JSONObject();
			JSONArray mapsJSON = new JSONArray();
			for(int i = 0; i < maps.size(); i++) {
				String mapName = (String)maps.get(i);
				JSONObject mapJSON = new JSONObject();
				
				mapJSON.put("id", mapName);
				mapJSON.put("name", mapName);
				mapJSON.put("description", mapName);
				
				List features = getGeoEngineInstance().getMapProvider().getFeatureNamesInMap(mapName);
				JSONArray featuresJSON = new JSONArray();
				for(int j = 0; j < features.size(); j++) {
					String layerName = (String)features.get(j);
					JSONObject featureJSON = new JSONObject();
					featureJSON.put("id", layerName);
					featureJSON.put("name", layerName);
					featureJSON.put("description", layerName);
					featureJSON.put("category", "undefined");
					featuresJSON.put(featureJSON);
				}
				
				JSONObject featureJSON= null;
				
				// grafici
				featureJSON = new JSONObject();
				featureJSON.put("id", "grafici");
				featureJSON.put("name", "grafici");
				featureJSON.put("description", "grafici");
				featureJSON.put("category", "undefined");
				featuresJSON.put(featureJSON);
				
				// valori
				featureJSON = new JSONObject();
				featureJSON.put("id", "valori");
				featureJSON.put("name", "valori");
				featureJSON.put("description", "valori");
				featureJSON.put("category", "undefined");
				featuresJSON.put(featureJSON);
				
				mapJSON.put("features", featuresJSON);
				mapsJSON.put(mapJSON);
				
			}
			responseJSON.put("maps", mapsJSON);
			
			logger.debug( "Generated service response: " + responseJSON.toString() );
			
			try {
				writeBackToClient( new JSONSuccess(responseJSON) );
			} catch (IOException e) {
				String message = "Impossible to write back the responce to the client";
				throw new SpagoBIEngineServiceException(getActionName(), message, e);
			}		
		} catch (Throwable t) {
			throw SpagoBIEngineServiceExceptionHandler.getInstance().getWrappedException(getActionName(), getEngineInstance(), t);
		} finally {
			// no resources need to be released
		}	
		
		logger.debug("OUT");
	}
}
