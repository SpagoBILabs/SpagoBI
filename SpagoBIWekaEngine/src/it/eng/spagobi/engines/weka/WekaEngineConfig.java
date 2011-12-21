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
package it.eng.spagobi.engines.weka;

import java.io.File;

import org.apache.log4j.Logger;

import it.eng.spago.base.SourceBean;
import it.eng.spago.configuration.ConfigSingleton;
import it.eng.spagobi.services.common.EnginConf;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class WekaEngineConfig {
	
	private EnginConf engineConfig;
	
	public static final String WEKA_ROLES_HANDLER_CLASS_NAME = "it.eng.spagobi.engines.drivers.weka.events.handlers.WekaRolesHandler";
	public static final String WEKA_PRESENTAION_HANDLER_CLASS_NAME = "it.eng.spagobi.engines.drivers.weka.events.handlers.WekaEventPresentationHandler";
	
	
	private static transient Logger logger = Logger.getLogger(WekaEngineConfig.class);

	
	// -- singleton pattern --------------------------------------------
	private static WekaEngineConfig instance;
	
	public static WekaEngineConfig getInstance(){
		if(instance==null) {
			instance = new WekaEngineConfig();
		}
		return instance;
	}
	
	private WekaEngineConfig() {
		setEngineConfig( EnginConf.getInstance() );
	}
	// -- singleton pattern  --------------------------------------------
	
	private File getWebappRootDir() {
		File webinfDir = new File(ConfigSingleton.getRootPath());		
		return webinfDir.getParentFile();
	}
	
	public File getEngineResourceDir() {
		File resourceDir;
		
		resourceDir = null;
		if(getEngineConfig().getResourcePath() != null) {
			resourceDir = new File(getEngineConfig().getResourcePath());
		} else {
			resourceDir = new File(getWebappRootDir(), "resources");
		}
		resourceDir = new File(resourceDir, "weka");
		resourceDir.mkdirs();
		
		return resourceDir;
	}
	
	public File getEngineOutputFilesDir() {
		File outputFilesDir;
		
		outputFilesDir =  new File( getEngineResourceDir(), "outputfiles");
		outputFilesDir.mkdirs();
		
		return outputFilesDir;
	}
	
	public String getRolesHandler() {
		// TODO try to read this value first from enging-config.xml
		return WEKA_ROLES_HANDLER_CLASS_NAME;
	}
	
	public String getPresentationHandler() {
		// TODO try to read this value first from enging-config.xml
		return WEKA_PRESENTAION_HANDLER_CLASS_NAME;
	}
	
	
	
	// -- ACCESS Methods  -----------------------------------------------
	public EnginConf getEngineConfig() {
		return engineConfig;
	}

	private void setEngineConfig(EnginConf engineConfig) {
		this.engineConfig = engineConfig;
	}
	
	public SourceBean getConfigSourceBean() {
		return getEngineConfig().getConfig();
	}
}
