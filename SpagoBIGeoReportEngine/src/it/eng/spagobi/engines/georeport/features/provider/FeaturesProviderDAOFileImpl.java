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
package it.eng.spagobi.engines.georeport.features.provider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mapfish.geo.MfFeature;
import org.mapfish.geo.MfFeatureCollection;
import org.mapfish.geo.MfGeoJSONReader;

import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.engines.georeport.GeoReportEngine;
import it.eng.spagobi.engines.georeport.features.SbiFeatureFactory;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

/**
 * @authors Andrea Gioia (andrea.gioia@eng.it)
 */
public class FeaturesProviderDAOFileImpl implements IFeaturesProviderDAO {

	String indexOnAttribute;
	Map<Object, JSONObject> lookupTable;
	
	public static final String GEOID_PNAME = "geoIdPName";
	public static final String GEOID_PVALUE = "geoIdPValue";

	private static final MfGeoJSONReader JSON_READER = new MfGeoJSONReader(SbiFeatureFactory.getInstance());
	
	/** Logger component. */
    private static transient Logger logger = Logger.getLogger(FeaturesProviderDAOFileImpl.class);
    
	
	public MfFeatureCollection getFeatures(Object fetureProviderEndPoint, Map parameters) {
		MfFeatureCollection featureCollection;
		
		String geoIdPName;
		String geoIdPValue;
		JSONObject fetaure;
		
		logger.debug("IN");
		
		featureCollection = null;
		
		try {
			geoIdPName = (String)parameters.get(GEOID_PNAME);
			logger.debug("Parameter [" + GEOID_PNAME + "] is equal to [" + geoIdPName + "]");
			
			geoIdPValue = (String)parameters.get(GEOID_PVALUE);
			logger.debug("Parameter [" + GEOID_PVALUE + "] is equal to [" + geoIdPValue + "]");
	
			if(!geoIdPName.equalsIgnoreCase(indexOnAttribute)) {
				createIndex((String)fetureProviderEndPoint, geoIdPName);
			}
			
			fetaure = lookupTable.get(geoIdPValue);
			logger.debug("Feature [" + geoIdPValue +"] is equal to [" + fetaure + "]");
			if(fetaure != null) { 
				Object x = JSON_READER.decode(fetaure);
				logger.debug("Decoded object is of type [" + x.getClass().getName() + "]");
				MfFeature mfFeature = (MfFeature)x;
				Collection<MfFeature> mfFeatures = new ArrayList();
				mfFeatures.add(mfFeature);
				featureCollection = new MfFeatureCollection(mfFeatures);;
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
			
			JSONObject o = loadFile( targetFile );
			JSONArray a = o.getJSONArray("features");
			Assert.assertNotNull(a, "Impossible to find attribute [features in file [" + filename +"]");
			
			logger.debug("Target file contains [" + a.length() + "] features to index");
			for(int i = 0; i < a.length(); i++) {
				JSONObject f = a.getJSONObject(i);
				JSONObject p = f.getJSONObject("properties");
				lookupTable.put(p.get(geoIdPName), f);
				logger.debug("Feature [" + p.get(geoIdPName) + "] added to the index");
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
	
	private JSONObject loadFile(File targetFile) {
		
		JSONObject result;
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
	        result = new JSONObject(buffer.toString());
	    } catch(Throwable t) {
	    	throw new SpagoBIRuntimeException(t);
	    }
        
        return result;
	}
	
}
