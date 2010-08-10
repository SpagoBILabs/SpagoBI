/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2009 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

**/
package it.eng.spagobi.engines.georeport.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.mapfish.geo.MfFeature;
import org.mapfish.geo.MfFeatureCollection;
import org.mapfish.geo.MfGeoJSONWriter;
import org.mapfish.geo.MfGeometry;

import it.eng.spagobi.engines.georeport.GeoReportEngineInstance;
import it.eng.spagobi.engines.georeport.features.SbiFeature;
import it.eng.spagobi.engines.georeport.features.SbiFeatureFactory;
import it.eng.spagobi.engines.georeport.features.provider.FeaturesProviderDAOFactory;
import it.eng.spagobi.engines.georeport.features.provider.IFeaturesProviderDAO;
import it.eng.spagobi.services.proxy.DataSetServiceProxy;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStoreMetaData;
import it.eng.spagobi.tools.dataset.common.datastore.IField;
import it.eng.spagobi.tools.dataset.common.datastore.IRecord;
import it.eng.spagobi.utilities.engines.BaseServletIOManager;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;
import it.eng.spagobi.utilities.service.AbstractBaseServlet;


/**
 * @authors Andrea Gioia (andrea.gioia@eng.it), Fabio D'Ovidio (f.dovidio@inovaos.it)
 */
public class MapOlAction extends AbstractBaseServlet {
	
	public static final String FEATURE_SOURCE_TYPE = "featureSourceType";
	public static final String FEATURE_SOURCE = "featureSource";
	
	public static final String LAYER_NAME = "layer";
	public static final String BUSINESSID_PNAME = "businessId";
	public static final String GEOID_PNAME = "geoId";
	
	/** Logger component. */
    private static transient Logger logger = Logger.getLogger(GeoReportEngineStartAction.class);
    
    
	public void doService( BaseServletIOManager servletIOManager ) throws SpagoBIEngineException {
		 
		// srsName=EPSG:4326
		
		String featureSourceType;
		String featureSource;
		String layerName;
		String businessIdPName;
		String geoIdPName;
		String geoIdPValue;
		
		GeoReportEngineInstance engineInstance;
		
		IDataSet dataSet;
		IDataStore dataStore;
		IDataStoreMetaData dataStoreMeta;
		
		logger.debug("IN");
		
		try {
			
			featureSourceType = servletIOManager.getParameterAsString(FEATURE_SOURCE_TYPE);
			logger.debug("Parameter [" + FEATURE_SOURCE_TYPE + "] is equal to [" + featureSourceType + "]");
			
			featureSource = servletIOManager.getParameterAsString(FEATURE_SOURCE);
			logger.debug("Parameter [" + FEATURE_SOURCE + "] is equal to [" + featureSourceType + "]");
			
			layerName = servletIOManager.getParameterAsString(LAYER_NAME);
			logger.debug("Parameter [" + LAYER_NAME + "] is equal to [" + layerName + "]");
			
			businessIdPName = servletIOManager.getParameterAsString(BUSINESSID_PNAME);
			logger.debug("Parameter [" + BUSINESSID_PNAME + "] is equal to [" + businessIdPName + "]");
			
			geoIdPName = servletIOManager.getParameterAsString(GEOID_PNAME);
			logger.debug("Parameter [" + GEOID_PNAME + "] is equal to [" + geoIdPName + "]");
			
			
			
			
			engineInstance =  (GeoReportEngineInstance)servletIOManager.getHttpSession().getAttribute(EngineConstants.ENGINE_INSTANCE);
			
			//DataSet
			dataSet = engineInstance.getDataSet();
			dataSet.setParamsMap(engineInstance.getEnv());
			dataSet.loadData();
			
			//Datastore 
			dataStore = dataSet.getDataStore();
			dataStoreMeta = dataStore.getMetaData();
			      
			// # COL NUMBER
			int nc = dataStoreMeta.getFieldCount();

			
			
			//Create Output Collection of Features
			Collection outputFeatureCollection = new ArrayList();
			
			// used to avoid multiple creation of the same geographical feature
			Set geoIdPValues = new HashSet();
			

			// for each row
			Iterator it = dataStore.iterator();

			IFeaturesProviderDAO featuresProvider = FeaturesProviderDAOFactory.getFeaturesProviderDAO(featureSourceType);
			
			while(it.hasNext()) {
			       
				IRecord record = (IRecord)it.next();
			    IField field;
				field = record.getFieldAt( dataStoreMeta.getFieldIndex(businessIdPName) );
			        
				      
			        
			    //IDfetaure
				geoIdPValue = "" + field.getValue();

				
				if(geoIdPValues.contains(geoIdPValue)) {
					continue;
				} else {
					geoIdPValues.add(geoIdPValue);
				}
				
			    Map parameters = new HashMap();
			    parameters.put("layerName", layerName);
			    parameters.put("geoIdPName", geoIdPName);
			    parameters.put("geoIdPValue", geoIdPValue);
							    
				// geoserver call
			    try {
			    	
			        MfFeatureCollection featureCollection = featuresProvider.getFeatures(featureSource, parameters);
			        List featureList = new ArrayList(featureCollection.getCollection());
			        MfFeature feature = (MfFeature) featureList.get(0);
			        
			        //Geometry from GeoServer
			        MfGeometry geom = feature.getMfGeometry();
			        
			        
			        // JSON creation
			        JSONObject jsonProperties = new JSONObject();
					
			        //for each col
			        for(int j=0; j<nc; j++){
			        	jsonProperties.accumulate(
			        			dataStoreMeta.getFieldName(j), 
			        			record.getFieldAt( dataStoreMeta.getFieldIndex(dataStoreMeta.getFieldName(j)) ).getValue()
			        	);  
			        }
			        
			        //for each prop
			        if(feature instanceof SbiFeature) {
			        	JSONObject properties = ((SbiFeature)feature).getProperties();
			        	Iterator keysIterator = properties.sortedKeys();
			        	while(keysIterator.hasNext()) {
			        		String key = (String)keysIterator.next();
				        	jsonProperties.accumulate(key, properties.get(key));
			        	}
			        }
			        
			        MfFeature featureToCollect = SbiFeatureFactory.getInstance().createFeature(geoIdPValue, geom, jsonProperties);
			        outputFeatureCollection.add(featureToCollect);
			      } catch (Exception e) {
			    	  e.printStackTrace();
			      }
				}
				
			    JSONStringer stringer = new JSONStringer();
			    MfGeoJSONWriter builder = new MfGeoJSONWriter(stringer);
			    builder.encodeFeatureCollection(new MfFeatureCollection(outputFeatureCollection));
			    
			    servletIOManager.tryToWriteBackToClient(stringer.toString());
			
		} catch(Throwable t) {
			t.printStackTrace();
		} finally {
			logger.debug("OUT");
		}
	}

	public void handleException(BaseServletIOManager servletIOManager,
			Throwable t) {
		t.printStackTrace();		
	}
	
	private IDataSet getDataSet(BaseServletIOManager servletIOManager) {
		IDataSet dataSet;
		DataSetServiceProxy datasetProxy;
		String user;
		String label;
		
		user = servletIOManager.getParameterAsString("userId");
		label = servletIOManager.getParameterAsString("label");
		
		datasetProxy = new DataSetServiceProxy(user, servletIOManager.getHttpSession());
		dataSet =  datasetProxy.getDataSetByLabel(label);
		
		return dataSet;
	}

}
