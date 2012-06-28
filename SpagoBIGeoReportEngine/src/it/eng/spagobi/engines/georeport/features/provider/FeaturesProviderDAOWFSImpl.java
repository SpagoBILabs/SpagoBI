/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.georeport.features.provider;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.mapfish.geo.MfFeatureCollection;
import org.mapfish.geo.MfGeoJSONReader;

import it.eng.spagobi.engines.georeport.features.SbiFeatureFactory;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

/**
 * @authors Andrea Gioia (andrea.gioia@eng.it), Fabio D'Ovidio (f.dovidio@inovaos.it)
 */
public class FeaturesProviderDAOWFSImpl implements IFeaturesProviderDAO {
	
	public static final String LAYER_NAME = "layerName";
	public static final String GEOID_PNAME = "geoIdPName";
	public static final String GEOID_PVALUE = "geoIdPValue";

	/** Logger component. */
    private static transient Logger logger = Logger.getLogger(FeaturesProviderDAOWFSImpl.class);
    
    
	public MfFeatureCollection getFeatures(Object fetureProviderEndPoint, Map parameters) {
		MfFeatureCollection featureCollection;
		
		String wfsUrl;
		String layerName;
		String geoIdPName;
		String geoIdPValue;
		URL url;
		URLConnection connection;
		
		
		try {
			wfsUrl = (String)fetureProviderEndPoint;
			
			layerName = (String)parameters.get(LAYER_NAME);
			logger.debug("Parameter [" + LAYER_NAME + "] is equal to [" + layerName + "]");
			
			geoIdPName = (String)parameters.get(GEOID_PNAME);
			logger.debug("Parameter [" + GEOID_PNAME + "] is equal to [" + geoIdPName + "]");
			
			geoIdPValue = (String)parameters.get(GEOID_PVALUE);
			logger.debug("Parameter [" + GEOID_PVALUE + "] is equal to [" + geoIdPValue + "]");
			
			wfsUrl += "?request=GetFeature" +
					  "&typename=" + layerName + 
					  "&Filter=<Filter><PropertyIsEqualTo><PropertyName>"+ geoIdPName +"</PropertyName><Literal>"+ geoIdPValue +"</Literal></PropertyIsEqualTo></Filter>" +
					  "&outputformat=json" +
					  "&version=1.0.0";
			
			String result = null;
		    
			
			// wfs call
	    
	    	url = new URL(wfsUrl);
	    	connection = url.openConnection();
	        
	    	// Get the response
	        BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        StringBuffer sb = new StringBuffer();
	        String line;
	        while ((line = rd.readLine()) != null) {
	        	sb.append(line);        
	        }
	        rd.close();
	        result = sb.toString();
	        logger.debug("Result for query [" + geoIdPName + "=" + geoIdPValue+ "]is equal to [" + result + "]");
	      
	        MfGeoJSONReader jsonReader = new MfGeoJSONReader(SbiFeatureFactory.getInstance()); 
	        featureCollection = (MfFeatureCollection) jsonReader.decode(result);
	    } catch(Throwable t){
	    	throw new SpagoBIRuntimeException(t);
	    }finally {
	    	
	    }
		
		return featureCollection;
	}

}
