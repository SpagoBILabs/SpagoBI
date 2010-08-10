/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
package it.eng.spagobi.engines.geo.service;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.engines.geo.GeoEngineAnalysisState;
import it.eng.spagobi.engines.geo.GeoEngineConstants;
import it.eng.spagobi.engines.geo.GeoEngineException;
import it.eng.spagobi.engines.geo.map.utils.SVGMapConverter;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.callbacks.audit.AuditAccessUtils;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.engines.IEngineInstance;
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceExceptionHandler;
import it.eng.spagobi.utilities.json.JSONUtils;
import it.eng.spagobi.utilities.service.JSONAcknowledge;
import it.eng.spagobi.utilities.service.JSONSuccess;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;


// TODO: Auto-generated Javadoc
/**
 * The Class MapDrawAction.
 */
public class SetAnalysisStateAction extends AbstractGeoEngineAction {
	
	// REQUEST PARAMETERS	
	public static final String ANALYSIS_STATE = "analysisState";
	public static final String HIERARCHY = "hierarchy";
	public static final String HIERARCHY_LEVEL = "level";
	public static final String MAP = "map";
	public static final String FEATURES = "features";	

	// RESPONSE PARAMETERS
	// ...
	
	// DEFAULT VALUES
	//...
	
	// Logger component
    public static transient Logger logger = Logger.getLogger(SetAnalysisStateAction.class);

	public void service(SourceBean serviceRequest, SourceBean serviceResponse) {
		JSONObject analysisStateJSON = null;
		String selectedHierarchyName = null;
		String selectedLevelName = null;
		String selectedMapName = null;
		List selectedFeatureNames = null;
	
		logger.debug("IN");		
		
		try {	
		
			super.service(serviceRequest, serviceResponse);			
			
			analysisStateJSON = getAttributeAsJSONObject( ANALYSIS_STATE );		
			logger.debug("Parameter [" + ANALYSIS_STATE + "] is equal to [" + analysisStateJSON + "]");
			
			if(analysisStateJSON == null) {
				selectedHierarchyName = getAttributeAsString( HIERARCHY );		
				logger.debug("Parameter [" + HIERARCHY + "] is equal to [" + selectedHierarchyName + "]");
				
				selectedLevelName = getAttributeAsString( HIERARCHY_LEVEL );
				logger.debug("Parameter [" + HIERARCHY_LEVEL + "] is equal to [" + selectedLevelName + "]");
				
				selectedMapName = getAttributeAsString( MAP );
				logger.debug("Parameter [" + MAP + "] is equal to [" + selectedMapName + "]");
				
				selectedFeatureNames = getAttributeAsCsvStringList( FEATURES, "," );
				logger.debug("Parameter [" + FEATURES + "] is equal to [" + selectedFeatureNames + "]");
				
				analysisStateJSON = new JSONObject();
				analysisStateJSON.put(HIERARCHY, selectedHierarchyName);
				analysisStateJSON.put(HIERARCHY_LEVEL, selectedLevelName);
				analysisStateJSON.put(MAP, selectedMapName);
				analysisStateJSON.put(FEATURES, new JSONArray(selectedFeatureNames));
			}
			
			
			
			// all or nothings
			Assert.assertTrue(!analysisStateJSON.isNull(HIERARCHY), "Attribute [" + HIERARCHY + "] cannot be null");
			Assert.assertNotNull(!analysisStateJSON.isNull(HIERARCHY_LEVEL), "Attribute [" + HIERARCHY_LEVEL + "] cannot be null");
			Assert.assertNotNull(!analysisStateJSON.isNull(MAP), "Attribute [" + MAP + "] cannot be null");
			Assert.assertNotNull(!analysisStateJSON.isNull(FEATURES), "Attribute [" + FEATURES + "] cannot be null");
			Assert.assertTrue(analysisStateJSON.getJSONArray(FEATURES).length()  > 0, "At least one feature need to be selected");
			Assert.assertNotNull(getEngineInstance(), "It's not possible to execute " + this.getActionName() + " service before having properly created an instance of GeoInstance class");
			
			
			GeoEngineAnalysisState analysisState =  (GeoEngineAnalysisState)getGeoEngineInstance().getAnalysisState();;
			analysisState.setSelectedMapName( analysisStateJSON.getString(MAP) );
			analysisState.setSelectedHierarchyName( analysisStateJSON.getString(HIERARCHY) );
			analysisState.setSelectedLevelName( analysisStateJSON.getString(HIERARCHY_LEVEL) );
			analysisState.setSelectedLayers( JSONUtils.asList( analysisStateJSON.getJSONArray(FEATURES) ) );			
			getGeoEngineInstance().setAnalysisState( analysisState );
		
			try {
				writeBackToClient( new JSONAcknowledge() );
			} catch (IOException e) {
				String message = "Impossible to write back the responce to the client";
				throw new SpagoBIEngineServiceException(getActionName(), message, e);
			}		
			
		} catch (Throwable t) {
			throw SpagoBIEngineServiceExceptionHandler.getInstance().getWrappedException(getActionName(), (IEngineInstance)getAttributeFromSession( EngineConstants.ENGINE_INSTANCE ), t);
		} finally {
			// no resources need to be released
		}	
		
		logger.debug("OUT");
	}
}