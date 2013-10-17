/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.engines.georeport.services;

import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.engines.georeport.GeoReportEngineInstance;
import it.eng.spagobi.engines.georeport.features.provider.FeaturesProviderDAOFactory;
import it.eng.spagobi.engines.georeport.features.provider.IFeaturesProviderDAO;
import it.eng.spagobi.services.proxy.DataSetServiceProxy;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IField;
import it.eng.spagobi.tools.dataset.common.datastore.IRecord;
import it.eng.spagobi.tools.dataset.common.metadata.IMetaData;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.BaseServletIOManager;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;
import it.eng.spagobi.utilities.service.AbstractBaseServlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.geotools.data.DataUtilities;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geojson.feature.FeatureJSON;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;

import com.vividsolutions.jts.geom.Geometry;


/**
 * This service return the target layer...
 * TODO describe more
 * TODO move to REST?
 * 
 * 
 * @authors Andrea Gioia (andrea.gioia@eng.it)
 */
public class GetTargetLayerAction extends AbstractBaseServlet {
	
	private static final long serialVersionUID = 1L;
	
	public static final String FEATURE_SOURCE_TYPE = "featureSourceType";
	public static final String FEATURE_SOURCE = "featureSource";
	public static final String LAYER_NAME = "layer";
	
	
	/** Logger component. */
    private static transient Logger logger = Logger.getLogger(GeoReportEngineStartAction.class);
    
    
	public void doService( BaseServletIOManager servletIOManager ) throws SpagoBIEngineException {
		 
		// srsName=EPSG:4326
		
		String featureSourceType = null;
		String featureSource  = null;
		String layerName = null;
		
		
		logger.debug("IN");
		
		try {
			
			try {
				featureSourceType = servletIOManager.getParameterAsString(FEATURE_SOURCE_TYPE); 
				logger.debug("Parameter [" + FEATURE_SOURCE_TYPE + "] is equal to [" + featureSourceType + "]");
				Assert.assertTrue(StringUtilities.isNotEmpty(featureSourceType), "Input parameter [" + FEATURE_SOURCE_TYPE + "] cannot be empty");
				
				featureSource = servletIOManager.getParameterAsString(FEATURE_SOURCE); 
				logger.debug("Parameter [" + FEATURE_SOURCE + "] is equal to [" + featureSourceType + "]");
				Assert.assertTrue(StringUtilities.isNotEmpty(featureSource), "Input parameter [" + FEATURE_SOURCE + "] cannot be empty");
				
				layerName = servletIOManager.getParameterAsString(LAYER_NAME); 
				logger.debug("Parameter [" + LAYER_NAME + "] is equal to [" + layerName + "]");
				Assert.assertTrue(StringUtilities.isNotEmpty(layerName), "Input parameter [" + LAYER_NAME + "] cannot be empty");
			} catch(Throwable t1) {
				logger.error("Impossible to load target layer because input parameter are not correct: " + t1.getMessage());
				servletIOManager.writeBackToClient(500, 
						"Impossible to load target layer because input parameter are not correct: " + t1.getMessage(),
						true ,"service-response", "text/plain");
				return;
			}
			
			FeatureCollection outputFeatureCollection = null;
			try {
				IFeaturesProviderDAO featuresProvider = FeaturesProviderDAOFactory.getFeaturesProviderDAO(featureSourceType);
				outputFeatureCollection = featuresProvider.getAllFeatures(featureSource, layerName);	
				Assert.assertNotNull(outputFeatureCollection, "The feature source returned a null object");
			} catch(Throwable t2) {
				logger.error("Impossible to load layer [" + layerName + "] " +
						"from source of type [" + featureSourceType + "] " +
						"whose endpoint is [" + featureSource + "]", t2
						);
				
				Throwable root = t2;
			    while (root.getCause() != null) root = root.getCause();
			    
				String message = "Impossible to load layer [" + layerName + "] " +
				"from source of type [" + featureSourceType + "] " +
				"whose endpoint is [" + featureSource + "]: " 
				+ root.getMessage();
				
				servletIOManager.writeBackToClient(500,  message, 
						true ,"service-response", "text/plain");
				return;
			}
		    
			FeatureJSON featureJSON = new FeatureJSON();				
			String responseFeature = featureJSON.toString(outputFeatureCollection);
			servletIOManager.tryToWriteBackToClient(responseFeature);
			
		} catch(Throwable t) {
			logger.error("An unexpected error occured while loading target layer", t);
			try {
				servletIOManager.writeBackToClient(500, "An unexpected error occured while loading target layer" ,true, "service-response", "text/plain");
			} catch (IOException x) {
				x.printStackTrace();
			}
		} finally {
			logger.debug("OUT");
		}
	}

	public void handleException(BaseServletIOManager servletIOManager,
			Throwable t) {
		t.printStackTrace();		
	}
}

