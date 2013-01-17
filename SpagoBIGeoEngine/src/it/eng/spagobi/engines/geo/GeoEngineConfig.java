/* Copyright 2012 Engineering Ingegneria Informatica S.p.A. – SpagoBI Competency Center
 * The original code of this file is part of Spago java framework, Copyright 2004-2007.

 * This Source Code Form is subject to the term of the Mozilla Public Licence, v. 2.0. If a copy of the MPL was not distributed with this file, 
 * you can obtain one at http://Mozilla.org/MPL/2.0/.

 * Alternatively, the contents of this file may be used under the terms of the LGPL License (the “GNU Lesser General Public License”), in which 
 * case the  provisions of LGPL are applicable instead of those above. If you wish to  allow use of your version of this file only under the 
 * terms of the LGPL  License and not to allow others to use your version of this file under  the MPL, indicate your decision by deleting the 
 * provisions above and  replace them with the notice and other provisions required by the LGPL.  If you do not delete the provisions above, 
 * a recipient may use your version  of this file under either the MPL or the GNU Lesser General Public License. 

 * Spago is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the 
 * Free Software Foundation; either version 2.1 of the License, or any later version. Spago is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License 
 * for more details.
 * You should have received a copy of the GNU Lesser General Public License along with Spago. If not, see: http://www.gnu.org/licenses/. The complete text of 
 * Spago license is included in the  COPYING.LESSER file of Spago java framework.
 */
package it.eng.spagobi.engines.geo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private Map<String, Map<String,String>> windowsGuiPropertiesInEmbeddedMode;
	
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
	
	private Map<String, Map<String,String>> getWindowsGuiPropertiesInEmbeddedMode() {
		if(windowsGuiPropertiesInEmbeddedMode == null) {
			windowsGuiPropertiesInEmbeddedMode = new HashMap<String, Map<String,String>>();
			List<SourceBean> windowsConfiguration = getConfigSourceBean().getAttributeAsList("EMBEDDED_MODE.WINDOW");
			
			// parse properties
			for(SourceBean windowConfiguration : windowsConfiguration) {
				String name = (String)windowConfiguration.getAttribute("name");
				Map<String,String> propertyMap = new HashMap<String,String>();
				windowsGuiPropertiesInEmbeddedMode.put(name, propertyMap);
				List<SourceBean> guiProperties = windowConfiguration.getAttributeAsList("PARAM");
				for(SourceBean guiProperty: guiProperties) {
					String pName = (String)guiProperty.getAttribute("name");
					String  pValue  = (String) guiProperty.getCharacters();
					if(pName != null && pValue != null) propertyMap.put(pName, pValue);
				}
			}
		}
		return windowsGuiPropertiesInEmbeddedMode;
	} 
	
	public Map<String,String> getWindowGuiPropertiesInEmbeddedMode(String windowName) {
		return getWindowsGuiPropertiesInEmbeddedMode().get(windowName);
	}
	public boolean isWindowVisibleInEmbeddedMode(String windowName, boolean defaultValue) {
		boolean isVisible = defaultValue;
		Map<String,String> propertyMap = getWindowGuiPropertiesInEmbeddedMode(windowName);
		if(propertyMap != null && propertyMap.get("visible") != null) {
			String visible = propertyMap.get("visible");
			isVisible = visible.equalsIgnoreCase("true");
		} 
		
		return isVisible;
	}
	
	
	protected EnginConf getEngineConfig() {
		return engineConfig;
	}

	protected void setEngineConfig(EnginConf engineConfig) {
		this.engineConfig = engineConfig;
	}
	
	
}
