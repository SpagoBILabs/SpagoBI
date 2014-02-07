/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engine.cockpit;

import it.eng.spago.base.SourceBean;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.dataset.cache.CacheFactory;
import it.eng.spagobi.dataset.cache.ICache;
import it.eng.spagobi.services.common.EnginConf;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.tools.datasource.dao.IDataSourceDAO;

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
	
	
	//----- DataSets Cache Singleton -------------------------------------
	private static ICache cache = null;
	
	public static ICache getCache(){
		
		try{
			if (cache == null){
				CacheFactory cacheFactory = new CacheFactory();
				IDataSourceDAO dataSourceDAO= DAOFactory.getDataSourceDAO();
				IDataSource dataSource = dataSourceDAO.loadDataSourceWriteDefault();
				cache = cacheFactory.initCache(dataSource);
			}  
		} catch (EMFUserError e){
			
		}

		return cache;
	}	
	
	
	//--------------------------------------------------------------------
	
	// -- CORE SETTINGS ACCESSOR Methods---------------------------------
	
	
	
	
	// -- PARSE Methods -------------------------------------------------
	
	
	
	
}
