/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engine.cockpit;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.services.common.EnginConf;

import org.apache.log4j.Logger;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class CockpitEngineConfig {
	
	private EnginConf engineConfig;
	
	private static transient Logger logger = Logger.getLogger(CockpitEngineConfig.class);
	
	// -- singleton pattern --------------------------------------------
	private static CockpitEngineConfig instance;
	
	public static CockpitEngineConfig getInstance(){
		if(instance==null) {
			instance = new CockpitEngineConfig();
		}
		return instance;
	}
	
	private CockpitEngineConfig() {
		setEngineConfig( EnginConf.getInstance() );
	}
	// -- singleton pattern  --------------------------------------------
	
	// -- ACCESSOR Methods  -----------------------------------------------
	public EnginConf getEngineConfig() {
		return engineConfig;
	}

	private void setEngineConfig(EnginConf engineConfig) {
		this.engineConfig = engineConfig;
	}
	
	public SourceBean getConfigSourceBean() {
		return getEngineConfig().getConfig();
	}
	
	// -- CORE SETTINGS ACCESSOR Methods---------------------------------
	
	
	
	
	// -- PARSE Methods -------------------------------------------------
	
	
	
	
}
