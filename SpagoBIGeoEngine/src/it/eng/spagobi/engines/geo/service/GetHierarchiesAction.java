/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.
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
import it.eng.spago.base.SourceBeanException;
import it.eng.spagobi.engines.geo.GeoEngineConstants;
import it.eng.spagobi.engines.geo.GeoEngineException;
import it.eng.spagobi.engines.geo.dataset.provider.Hierarchy;
import it.eng.spagobi.engines.geo.dataset.provider.Hierarchy.Level;
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceExceptionHandler;
import it.eng.spagobi.utilities.service.JSONResponse;
import it.eng.spagobi.utilities.service.JSONSuccess;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;


/**
 * The Class GetMapsAction.
 * 
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class GetHierarchiesAction extends AbstractGeoEngineAction {
	
	// REQUEST PARAMETERS	
	//...

	// RESPONSE PARAMETERS
	// ...
	
	// DEFAULT VALUES
	//...
	
	// Default serial version number (just to keep eclipse happy).
	private static final long serialVersionUID = 1L;
	
	// Logger component
    public static transient Logger logger = Logger.getLogger(GetHierarchiesAction.class);
	
	
	public void service(SourceBean request, SourceBean response) {		
		
		logger.debug("IN");
		
		try {
			super.service(request, response);
			
			JSONObject responseJSON = new JSONObject();
			
			String[] hierachieNames = (String[])getGeoEngineInstance().getDataMartProvider().getHierarchyNames().toArray(new String[0]);
			JSONArray hierarchiesJSON = new JSONArray();
			for(int j = 0; j < hierachieNames.length; j++) {
				Hierarchy hierarchy = getGeoEngineInstance().getDataMartProvider().getHierarchy(hierachieNames[j]);
				JSONObject hierarchyJSON = new JSONObject();
				hierarchyJSON.put("id", hierarchy.getName());
				hierarchyJSON.put("name", hierarchy.getName());
				hierarchyJSON.put("description", hierarchy.getName());
				List levels = hierarchy.getLevels();
				JSONArray levelsJSON = new JSONArray();
				for(int i = 0; i < levels.size(); i++) {
					Level level = (Level)levels.get(i);
					JSONObject levelJSON = new JSONObject();
					levelJSON.put("id", level.getName());
					levelJSON.put("name", level.getName());
					levelJSON.put("description", level.getName());
					levelJSON.put("feature", level.getFeatureName());
					levelsJSON.put(levelJSON);
				}
				
				hierarchyJSON.put("levels", levelsJSON);
				hierarchiesJSON.put(hierarchyJSON);
			}
			
			responseJSON.put("hierarchies", hierarchiesJSON);
			
			logger.info(responseJSON.toString());
			
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
