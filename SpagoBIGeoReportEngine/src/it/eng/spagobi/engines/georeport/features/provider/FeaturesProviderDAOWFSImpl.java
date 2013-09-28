/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.georeport.features.provider;

import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import org.apache.commons.httpclient.URI;
import org.apache.log4j.Logger;
import org.geotools.feature.FeatureCollection;
import org.geotools.geojson.feature.FeatureJSON;
import org.opengis.feature.simple.SimpleFeature;

/**
 * @authors Andrea Gioia (andrea.gioia@eng.it), Fabio D'Ovidio (f.dovidio@inovaos.it)
 */
public class FeaturesProviderDAOWFSImpl implements IFeaturesProviderDAO {
	
	public static final String LAYER_NAME = "layerName";
	public static final String GEOID_PNAME = "geoIdPName";
	public static final String GEOID_PVALUE = "geoIdPValue";

	/** Logger component. */
    private static transient Logger logger = Logger.getLogger(FeaturesProviderDAOWFSImpl.class);
    
    
	public SimpleFeature getFeatureById(Object fetureProviderEndPoint, String layerName, Map parameters) {
		FeatureCollection featureCollection;
		
		String wfsUrl = null;
		String geoIdPName = null;
		String geoIdPValue = null;
		URL url = null;
		URI uri = null;
		URLConnection connection = null;
		
		logger.debug("IN");
		
		try {
			wfsUrl = (String)fetureProviderEndPoint;
			
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
			// we use apache URI to properly encode the url string according to RFC2396
			uri = new URI(wfsUrl, false);
	    	url = new URL(uri.toString());
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
	      
	    	Reader reader = new StringReader( result );
	    	FeatureJSON featureJSON = new FeatureJSON();
		    featureCollection = featureJSON.readFeatureCollection(reader);
	    } catch(Throwable t){
	    	throw new SpagoBIRuntimeException("An unexpected error occured while executing service call [" + wfsUrl + "]", t);
	    }finally {
	    	logger.debug("OUT");
	    }		

	    return (SimpleFeature)featureCollection.features().next();
	}


	/* (non-Javadoc)
	 * @see it.eng.spagobi.engines.georeport.features.provider.IFeaturesProviderDAO#getAllFeatures(java.lang.Object)
	 */
	public FeatureCollection getAllFeatures(Object featureProviderEndPoint, String layerName) {
		// TODO Auto-generated method stub
		return null;
	}

}
