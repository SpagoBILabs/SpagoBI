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
package it.eng.spagobi.tools.dataset.cache;

import java.math.BigDecimal;

import it.eng.spagobi.commons.bo.Config;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.commons.dao.IConfigDAO;
import it.eng.spagobi.tools.dataset.cache.impl.sqldbcache.SQLDBCacheConfiguration;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.tools.datasource.dao.IDataSourceDAO;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class SpagoBICacheConfiguration {
	
	public static final String CACHE_NAME_PREFIX_CONFIG = "SPAGOBI.CACHE.NAMEPREFIX";
	public static final String CACHE_SPACE_AVAILABLE_CONFIG = "SPAGOBI.CACHE.SPACE_AVAILABLE";
	public static final String CACHE_LIMIT_FOR_CLEAN_CONFIG = "SPAGOBI.CACHE.LIMIT_FOR_CLEAN";
	
	public static ICacheConfiguration getInstance() {
		SQLDBCacheConfiguration cacheConfiguration = new SQLDBCacheConfiguration();
		cacheConfiguration.setCacheDataSource( getCacheDataSource() );
		cacheConfiguration.setTableNamePrefix(getTableNamePrefix());
		cacheConfiguration.setCacheSpaceAvailable(getCacheSpaceAvailable());
		cacheConfiguration.setCachePercentageToClean(getCachePercentageToClean());
		return cacheConfiguration;
	}
	
	private static IDataSource getCacheDataSource() {
		try {
			IDataSourceDAO dataSourceDAO = DAOFactory.getDataSourceDAO();
			IDataSource dataSource = dataSourceDAO.loadDataSourceWriteDefault();
			return dataSource;
		} catch(Throwable t) {
			throw new SpagoBIRuntimeException("An unexpected exception occured while loading cache datasource", t);
		}
	}

	
	private static String getTableNamePrefix() {
		try {
			String tableNamePrefix = getSpagoBIConfigurationProperty(CACHE_NAME_PREFIX_CONFIG);;
			return tableNamePrefix;
		} catch(Throwable t) {
			throw new SpagoBIRuntimeException("An unexpected exception occured while loading cache configuration property", t);
		}
	}
	
	private static BigDecimal getCacheSpaceAvailable() {
		try {
			BigDecimal cacheSpaceAvailable = null;
			String propertyValue = getSpagoBIConfigurationProperty(CACHE_SPACE_AVAILABLE_CONFIG);
			if(propertyValue != null) {
				cacheSpaceAvailable = BigDecimal.valueOf( Double.valueOf(propertyValue) );
			}
			return cacheSpaceAvailable;
		} catch(Throwable t) {
			throw new SpagoBIRuntimeException("An unexpected exception occured while loading cache configuration property", t);
		}
	}
	
	private static Integer getCachePercentageToClean() {
		try {
			Integer cachePercentageToClean = null;
			String propertyValue = getSpagoBIConfigurationProperty(CACHE_LIMIT_FOR_CLEAN_CONFIG);
			if(propertyValue != null) {
				cachePercentageToClean = Integer.valueOf( propertyValue );
			}
			return cachePercentageToClean;
		} catch(Throwable t) {
			throw new SpagoBIRuntimeException("An unexpected exception occured while loading cache configuration property", t);
		}
	}
	
	private static String getSpagoBIConfigurationProperty(String propertyName) {
		try {
			String propertyValue = null;
			IConfigDAO configDao = DAOFactory.getSbiConfigDAO();
			Config cacheSpaceCleanableConfig = configDao.loadConfigParametersByLabel(propertyName);
			if ((cacheSpaceCleanableConfig != null) && (cacheSpaceCleanableConfig.isActive())){
				propertyValue = cacheSpaceCleanableConfig.getValueCheck();
			}	
			return propertyValue;
		} catch(Throwable t) {
			throw new SpagoBIRuntimeException("An unexpected exception occured while loading spagobi property [" + propertyName + "]", t);
		}
	}
	
}
