/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engine.cockpit;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import it.eng.spago.base.SourceBean;
import it.eng.spago.error.EMFUserError;
import it.eng.spagobi.commons.bo.Config;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.dao.IConfigDAO;
import it.eng.spagobi.dataset.cache.CacheConfiguration;
import it.eng.spagobi.dataset.cache.CacheFactory;
import it.eng.spagobi.dataset.cache.ICache;
import it.eng.spagobi.dataset.cache.impl.sqldbcache.SQLDBCache;
import it.eng.spagobi.services.common.EnginConf;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.tools.datasource.dao.IDataSourceDAO;

import org.apache.log4j.Logger;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class CockpitEngineConfig {
	public static final String CACHE_NAME_PREFIX_CONFIG = "SPAGOBI.CACHE.NAMEPREFIX";
	public static final String CACHE_SPACE_AVAILABLE_CONFIG = "SPAGOBI.CACHE.SPACE_AVAILABLE";
	public static final String CACHE_LIMIT_FOR_CLEAN_CONFIG = "SPAGOBI.CACHE.LIMIT_FOR_CLEAN";
	

	
	private static EnginConf engineConfig;

	
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
	public static EnginConf getEngineConfig() {
		return engineConfig;
	}

	private void setEngineConfig(EnginConf engineConfig) {
		this.engineConfig = engineConfig;
	}
	
	public static SourceBean getConfigSourceBean() {
		return getEngineConfig().getConfig();
	}
	
	
	//----- DataSets Cache Singleton -------------------------------------
	private static ICache cache = null;
	private static List<Properties> dimensionTypes = null;
	
	public static ICache getCache(){
		if (cache == null){
			initializeCache();
		}  
		return cache;
	}	
	
	private static void initializeCache(){
		try {
			//Cache configuration parameters
			String tableNamePrefix;
			BigDecimal cacheSpaceAvailable;
			Integer cachePercentageToClean;
			
			
			CacheFactory cacheFactory = new CacheFactory();
			IDataSourceDAO dataSourceDAO = DAOFactory.getDataSourceDAO();
			IDataSource dataSource = dataSourceDAO.loadDataSourceWriteDefault();
			if(dataSource == null) {
				logger.warn("Impossible to initialize cache because there are no datasource defined as defualt write datasource");
			} else {
				
				CacheConfiguration cacheConfiguration = new CacheConfiguration();

				IConfigDAO configDao = DAOFactory.getSbiConfigDAO();
				Config tableNamePrefixConfig = configDao.loadConfigParametersByLabel(CACHE_NAME_PREFIX_CONFIG);
				if ((tableNamePrefixConfig != null) && (tableNamePrefixConfig.isActive())){
					tableNamePrefix = tableNamePrefixConfig.getValueCheck();
					cacheConfiguration.setTableNamePrefix(tableNamePrefix);
				}
				Config cacheSpaceAvailableConfig = configDao.loadConfigParametersByLabel(CACHE_SPACE_AVAILABLE_CONFIG);
				if ((cacheSpaceAvailableConfig != null) && (cacheSpaceAvailableConfig.isActive())){
					cacheSpaceAvailable = BigDecimal.valueOf(Double.valueOf(cacheSpaceAvailableConfig.getValueCheck()));
					cacheConfiguration.setCacheSpaceAvailable(cacheSpaceAvailable);
				}			
				Config cacheSpaceCleanableConfig = configDao.loadConfigParametersByLabel(CACHE_LIMIT_FOR_CLEAN_CONFIG);
				if ((cacheSpaceCleanableConfig != null) && (cacheSpaceAvailableConfig.isActive())){
					cachePercentageToClean = Integer.valueOf(cacheSpaceCleanableConfig.getValueCheck());
					cacheConfiguration.setCachePercentageToClean(cachePercentageToClean);
				}				

				cache = cacheFactory.getCache(dataSource,cacheConfiguration);
				if (cache instanceof SQLDBCache){
					((SQLDBCache)cache).setObjectsTypeDimension(getDimensionTypes());
				}
			}
		} catch (Throwable t){
			logger.error("An unexpected error occured while initializing cache");
		}
	}
	
	
	public static List<Properties> getDimensionTypes() {
		
		if(dimensionTypes == null) {
			initCacheConfiguration();
		}
		
		return dimensionTypes;
	}
	
	//--------------------------------------------------------------------
	
	// -- CORE SETTINGS ACCESSOR Methods---------------------------------
	
	
	
	
	// -- PARSE Methods -------------------------------------------------
	private final static String CACHE_CONFIG_TAG = "CACHE_CONFIG";
	private final static String DATA_TYPES_TAG = "DATA_TYPES";
	private final static String TYPE_TAG = "TYPE";

	public static void initCacheConfiguration(){
		try{ 
			SourceBean configSB = (SourceBean) getConfigSourceBean().getAttribute(CACHE_CONFIG_TAG);
			SourceBean typesSB = (SourceBean) configSB.getAttribute(DATA_TYPES_TAG);
			List<SourceBean> typesList = typesSB.getAttributeAsList(TYPE_TAG);
			dimensionTypes = new ArrayList<Properties>();
			for(SourceBean type : typesList) {
				String name = (String)type.getAttribute("name");
				String bytes = (String)type.getAttribute("bytes");			
				
				Properties props = new Properties();
				if(name != null) props.setProperty("name", name);
				if(bytes != null) props.setProperty("bytes", bytes);
				dimensionTypes.add(props);
			}
		} catch(Throwable t) {
			throw new RuntimeException("An error occured while loading geo dimension levels' properties from file engine-config.xml", t);
		} finally {
			logger.debug("OUT");
		}
	}
	
	
}
