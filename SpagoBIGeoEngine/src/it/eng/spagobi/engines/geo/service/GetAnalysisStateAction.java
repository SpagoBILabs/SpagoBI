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
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceExceptionHandler;
import it.eng.spagobi.utilities.service.JSONAcknowledge;
import it.eng.spagobi.utilities.service.JSONSuccess;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletOutputStream;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;


// TODO: Auto-generated Javadoc
/**
 * The Class MapDrawAction.
 */
public class GetAnalysisStateAction extends AbstractGeoEngineAction {
	
	// REQUEST PARAMETERS	
	// ...

	// RESPONSE PARAMETERS
	// ...
	
	// DEFAULT VALUES
	public static final String HIERARCHY = "hierarchy";
	public static final String HIERARCHY_LEVEL = "level";
	public static final String MAP = "map";
	public static final String FEATURES = "features";	
	
	
	// Logger component
    public static transient Logger logger = Logger.getLogger(GetAnalysisStateAction.class);

  
	public void service(SourceBean serviceRequest, SourceBean serviceResponse) {		
		JSONObject responseJSON;
		JSONObject analysisStateJSON;
		GeoEngineAnalysisState analysisState;
		
		logger.debug("IN");		
		
		try {			
			super.service(serviceRequest, serviceResponse);			
			
			Assert.assertNotNull(getEngineInstance(), "It's not possible to execute " + this.getActionName() + " service before having properly created an instance of GeoInstance class");
						
			analysisState =  (GeoEngineAnalysisState)getGeoEngineInstance().getAnalysisState();
			
			analysisStateJSON = new JSONObject();
			analysisStateJSON.put(HIERARCHY, analysisState.getSelectedHierarchy());
			analysisStateJSON.put(HIERARCHY_LEVEL, analysisState.getSelectedHierarchyLevel());
			analysisStateJSON.put(MAP, analysisState.getSelectedMapName());			
			analysisStateJSON.put(FEATURES, new JSONArray( Arrays.asList( analysisState.getSelectedLayers().split(",") ) ) );
			
			responseJSON = analysisStateJSON;
			
			logger.debug("Genarated response: " + responseJSON.toString());
			
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