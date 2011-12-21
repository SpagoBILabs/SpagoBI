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
package it.eng.spagobi.engines.geo;

import it.eng.spago.base.SourceBean;

import java.util.Map;

import org.apache.log4j.Logger;


/**
 * The Class GeoEngine.
 * 
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class GeoEngine {
	
	private static GeoEngineVersion version;
	private static GeoEngineConfig config;	
	
	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(GeoEngine.class);
	
    
	/**
	 * Creates the instance.
	 * 
	 * @param template the template
	 * @param env the env
	 * 
	 * @return the geo engine instance
	 * 
	 * @throws GeoEngineException the geo engine exception
	 */
	public static GeoEngineInstance createInstance(SourceBean template, Map env) throws GeoEngineException {
		GeoEngineInstance geoEngineInstance = null;
		logger.debug("IN");
		geoEngineInstance = new GeoEngineInstance(template, env);
		logger.debug("OUT");
		return geoEngineInstance;
	}


	public static GeoEngineVersion getVersion() {
		return version;
	}


	public static GeoEngineConfig getConfig() {
		return config;
	}
}
