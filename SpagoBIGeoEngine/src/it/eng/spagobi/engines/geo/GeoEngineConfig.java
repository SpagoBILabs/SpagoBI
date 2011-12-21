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

import org.apache.log4j.Logger;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.services.common.EnginConf;
import it.eng.spagobi.utilities.assertion.Assert;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class GeoEngineConfig {
	
	private EnginConf engineConfig;
	
	private static GeoEngineConfig instance;
	
	public static String SPAGOBI_SERVER_URL = "SPAGOBI_SERVER_URL";
	public static String DEFAULT_SPAGOBI_SERVER_URL = "http://localhost:8080/SpagoBI";
	
	private static transient Logger logger = Logger.getLogger(GeoEngineConfig.class);

	
	public static GeoEngineConfig getInstance() {
		if(instance == null) {
			instance =  new GeoEngineConfig();
		}
		
		return instance;
	}
	
	private GeoEngineConfig() {
		setEngineConfig( EnginConf.getInstance() );
	}
	
	public SourceBean getConfigSourceBean() {
		return getEngineConfig().getConfig();
	}
	
	public String getSpagoBIServerUrl() {
	
		String spagoBIServerURL = null;
		SourceBean sourceBeanConf;
		
		Assert.assertNotNull( getConfigSourceBean(), "Impossible to parse engine-config.xml file");
		
		sourceBeanConf = (SourceBean) getConfigSourceBean().getAttribute(SPAGOBI_SERVER_URL);
		if(sourceBeanConf != null) {
			spagoBIServerURL = (String) sourceBeanConf.getCharacters();
			logger.debug("Configuration attribute [" + SPAGOBI_SERVER_URL + "] is equals to: [" + spagoBIServerURL + "]");
		}
		
		if (spagoBIServerURL == null) {
			logger.warn("Configuration attribute [" + SPAGOBI_SERVER_URL + "] is not defined in file engine-config.xml");
			spagoBIServerURL = DEFAULT_SPAGOBI_SERVER_URL;
			logger.debug("The default value [" + DEFAULT_SPAGOBI_SERVER_URL +"] will be used for configuration attribute [" + SPAGOBI_SERVER_URL + "]");
		} 
		
		return spagoBIServerURL;
	}
	
	
	
	
	
	protected EnginConf getEngineConfig() {
		return engineConfig;
	}

	protected void setEngineConfig(EnginConf engineConfig) {
		this.engineConfig = engineConfig;
	}
	
	
}
