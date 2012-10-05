/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.georeport.features.provider;

import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.engines.georeport.GeoReportEngine;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.assertion.NullReferenceException;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.FeatureIterator;
import org.geotools.geojson.feature.FeatureJSON;
import org.opengis.feature.simple.SimpleFeature;

/**
 * @authors Andrea Gioia (andrea.gioia@eng.it)
 */
public class FeaturesProviderDAOFileImpl implements IFeaturesProviderDAO {

	String indexOnAttribute;
	Map<Object, SimpleFeature> lookupTable;
	
	public static final String GEOID_PNAME = "geoIdPName";
	public static final String GEOID_PVALUE = "geoIdPValue";

	/** Logger component. */
    private static transient Logger logger = Logger.getLogger(FeaturesProviderDAOFileImpl.class);
    
	
	public FeatureCollection getFeatures(Object featureProviderEndPoint, Map parameters) {
		SimpleFeatureCollection featureCollection = FeatureCollections.newCollection();
		
		String geoIdPName;
		String geoIdPValue;
		SimpleFeature feature;
		
		logger.debug("IN");
		
		try {
			geoIdPName = (String)parameters.get(GEOID_PNAME);
			logger.debug("Parameter [" + GEOID_PNAME + "] is equal to [" + geoIdPName + "]");
			
			geoIdPValue = (String)parameters.get(GEOID_PVALUE);
			logger.debug("Parameter [" + GEOID_PVALUE + "] is equal to [" + geoIdPValue + "]");
	
			if(!geoIdPName.equalsIgnoreCase(indexOnAttribute)) {
				createIndex((String)featureProviderEndPoint, geoIdPName);
			}
			
			feature = lookupTable.get(geoIdPValue);
			logger.debug("Feature [" + geoIdPValue +"] is equal to [" + feature + "]");
			
			
			if(feature != null) { 
				featureCollection.add(feature);
				logger.debug("Decoded object is of type [" + feature.getClass().getName() + "]");
				logger.debug("Feature [" + geoIdPValue + "] added to result features' collection");
			} else {
				logger.warn("Impossible to find feature [" + geoIdPValue + "]");
			}
						
		} catch(Throwable t) {
			throw new SpagoBIRuntimeException(t);
		} finally {
			logger.debug("OUT");
		}
		
		return featureCollection;
	}
	
	private void createIndex(String filename, String geoIdPName) {
		
		String resourcesDir;
		File targetFile;
		
		logger.debug("IN");
		
		try {
			
			Assert.assertTrue(!StringUtilities.isEmpty(filename), "Input parameter [filename] cannot be null or empty");
			Assert.assertTrue(!StringUtilities.isEmpty(geoIdPName), "Input parameter [filename] cannot be null or empty");
			
			logger.debug("Indexing file [" + filename + "] on attribute [" + geoIdPName + "] ...");
			
			indexOnAttribute = geoIdPName;
			lookupTable = new HashMap();
			
			resourcesDir = GeoReportEngine.getConfig().getEngineConfig().getResourcePath() + "/georeport";
			logger.debug("Resource dir is equal to [" + resourcesDir + "]");
			
			targetFile = new File(resourcesDir, filename);
			logger.debug("Target file full name is equal to [" + targetFile + "]");
			
			FeatureCollection fc = loadFile( targetFile );

			logger.debug("Target file contains [" + fc.size() + "] features to index");
			if ( fc.size() == 0){
				throw new NullReferenceException("Impossible to find attribute [features in file [" + filename +"]");
			}
			
			FeatureIterator iterator = fc.features();
	    	while (iterator.hasNext()) {
	    		SimpleFeature feature = (SimpleFeature) iterator.next();
	    		Object idx = feature.getProperty(geoIdPName).getValue();
	    		lookupTable.put(idx, feature);
				logger.debug("Feature [" + idx + "] added to the index");
	    	}
			
			logger.debug("File [" + filename + "] indexed succesfully on attribute [" + geoIdPName + "]");
		} catch(Throwable t) {
			indexOnAttribute = null;
			lookupTable = null;
			throw new SpagoBIRuntimeException(t);
		} finally {
			logger.debug("OUT");
		}
	}
	
	private FeatureCollection loadFile(File targetFile) {
		
		FeatureCollection result;
		BufferedReader reader;
		StringBuffer buffer;
		String line;
		
		try {
			reader = new BufferedReader(new FileReader( targetFile ));
	        buffer = new StringBuffer();
	
	        while ((line = reader.readLine()) != null) {
	        	buffer.append(line);        
	        }
	        reader.close();
	        String featureStr = buffer.toString();		        

	        Reader strReader = new StringReader( featureStr );
	        FeatureJSON featureJ = new FeatureJSON();
	        result = featureJ.readFeatureCollection(strReader);
	    } catch(Throwable t) {
	    	throw new SpagoBIRuntimeException(t);
	    }
         
        return result;
	}

}
