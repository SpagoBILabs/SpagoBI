/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
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
