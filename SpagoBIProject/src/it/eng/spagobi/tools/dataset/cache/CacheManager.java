/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2010 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

**/
package it.eng.spagobi.tools.dataset.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import it.eng.spago.base.SourceBean;
import it.eng.spago.configuration.ConfigSingleton;

import it.eng.spagobi.tools.dataset.cache.CacheManager;
import it.eng.spagobi.tools.dataset.cache.ICache;
import it.eng.spagobi.tools.dataset.cache.impl.sqldbcache.SQLDBCache;

/**
 * The object that manage application cache. It's implemented as a singelton. If you dont want to
 * use a global cache use CacheFactory instead to control the instatiation of the cache.
 * 
 * 
 * @authors Marco Cortella (marco.cortella@eng.it), Andrea Gioia (andrea.gioia@eng.it)
 */
public class CacheManager {

	/**
	 * The global cache shared by all application's modules
	 */
	private static ICache cache = null;
	
	
	private static List<Properties> dimensionTypes = null;

	
	private static transient Logger logger = Logger.getLogger(CacheManager.class);
	
	/**
	 * 
	 * @return The application cache.
	 */
	public static ICache getCache(){
		if (cache == null){
			initializeCache();
		}  
		return cache;
	}	
	
	private static void initializeCache() {
		try {		
			ICacheConfiguration cacheConfiguration = SpagoBICacheConfiguration.getInstance();
			if(cacheConfiguration.getCacheDataSource() == null) {
				logger.warn("Impossible to initialize cache because there are no datasource defined as defualt write datasource");
			} else {
				CacheFactory cacheFactory = new CacheFactory();
				cache = cacheFactory.getCache(cacheConfiguration );
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
	
	// -- PARSE Methods -------------------------------------------------
	private final static String CACHE_CONFIG_TAG = "CACHE_CONFIG";
	private final static String DATA_TYPES_TAG = "DATA_TYPES";
	private final static String TYPE_TAG = "TYPE";

	public static void initCacheConfiguration(){
		logger.trace("IN");
		try{ 
			SourceBean configSB = (SourceBean) ConfigSingleton.getInstance().getAttribute(CACHE_CONFIG_TAG);
			if(configSB == null) {
				throw new CacheException("Impossible to find configuartion block [" + CACHE_CONFIG_TAG + "]");
			}
			
			SourceBean typesSB = (SourceBean) configSB.getAttribute(DATA_TYPES_TAG);
			if(typesSB == null) {
				throw new CacheException("Impossible to find configuartion block [" + CACHE_CONFIG_TAG + "." + DATA_TYPES_TAG + "]");
			}
			
			List<SourceBean> typesList = (List<SourceBean>)typesSB.getAttributeAsList(TYPE_TAG);
			if(typesSB == null) {
				throw new CacheException("Impossible to find configuartion blocks [" + CACHE_CONFIG_TAG + "." + DATA_TYPES_TAG + "." + TYPE_TAG + "]");
			}
			
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
