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
package it.eng.spagobi.engines.geo.map.provider.configurator;

import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanException;
import it.eng.spagobi.engines.geo.GeoEngineConstants;
import it.eng.spagobi.engines.geo.GeoEngineException;
import it.eng.spagobi.engines.geo.map.provider.SOMapProvider;
import it.eng.spagobi.tools.datasource.bo.DataSource;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.callbacks.mapcatalogue.MapCatalogueAccessUtils;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class SOMapProviderConfigurator.
 * 
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class SOMapProviderConfigurator {
	
	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(SOMapProviderConfigurator.class);
	
	
	/**
	 * Configure.
	 * 
	 * @param soMapProvider the so map provider
	 * @param conf the conf
	 * 
	 * @throws GeoEngineException the geo engine exception
	 */
	public static void configure(SOMapProvider soMapProvider, Object conf) throws GeoEngineException {
		SourceBean confSB = null;
		
		if(conf instanceof String) {
			try {
				confSB = SourceBean.fromXMLString( (String)conf );
			} catch (SourceBeanException e) {
				logger.error("Impossible to parse configuration block for DataSetProvider", e);
				throw new GeoEngineException("Impossible to parse configuration block for DataSetProvider", e);
			}
		} else {
			confSB = (SourceBean)conf;
		}
		
		if(confSB != null) {
			MapCatalogueAccessUtils mapCatalogueServiceProxy = (MapCatalogueAccessUtils)soMapProvider.getEnv().get(GeoEngineConstants.ENV_MAPCATALOGUE_SERVICE_PROXY);
			soMapProvider.setMapCatalogueServiceProxy(mapCatalogueServiceProxy);		
		}
	}

	/**
	 * Gets the data source.
	 * 
	 * @param confSB the conf sb
	 * 
	 * @return the data source
	 * 
	 * @throws GeoEngineException the geo engine exception
	 */
	public static IDataSource getDataSource(SourceBean confSB) throws GeoEngineException {
		IDataSource dataSource = null;
		
		SourceBean datasourceSB = (SourceBean)confSB.getAttribute(GeoEngineConstants.DATASOURCE_TAG);
		if(datasourceSB == null) {
			logger.warn("Cannot find datasource configuration settings: tag name " + GeoEngineConstants.DATASOURCE_TAG);
			logger.info("Datasource configuration settings must be injected at execution time");
			return null;
		}
		
		dataSource = new DataSource();
		
		String type = (String)datasourceSB.getAttribute(GeoEngineConstants.DATASET_TYPE_ATTRIBUTE);				
		if("connection".equalsIgnoreCase(type)) {
			dataSource.setJndi( (String)datasourceSB.getAttribute(GeoEngineConstants.DATASET_NAME_ATTRIBUTE) );
			dataSource.setDriver( (String)datasourceSB.getAttribute(GeoEngineConstants.DATASET_DRIVER_ATTRIBUTER) );
			dataSource.setPwd( (String)datasourceSB.getAttribute(GeoEngineConstants.DATASET_PWD_ATTRIBUTE) );
			dataSource.setUser( (String)datasourceSB.getAttribute(GeoEngineConstants.DATASET_USER_ATTRIBUTE) );
			dataSource.setUrlConnection( (String)datasourceSB.getAttribute(GeoEngineConstants.DATASET_URL_ATTRIBUTE) );
		}
		
		logger.debug("Datasource jndi name: " + dataSource.getJndi());
		logger.debug("Datasource driver: " + dataSource.getDriver());
		logger.debug("Datasource password: " + dataSource.getPwd());		
		logger.debug("Datasource user: " + dataSource.getUser());
		logger.debug("Datasource url: " + dataSource.getUrlConnection());
		
		if(dataSource.getJndi() != null) {
			logger.info("Datasource is of type jndi connection. Referenced jndi resource is " + dataSource.getJndi());
		} else if (dataSource.getDriver() == null || dataSource.getUrlConnection() == null){
			logger.error("Missing driver name or url in datasource configuration settings");
			throw new GeoEngineException("Missing driver name or url in datasource configuration settings");
		}
		
		return dataSource;
	}
	
	/**
	 * Gets the query.
	 * 
	 * @param dataSetSB the data set sb
	 * 
	 * @return the query
	 */
	private static String getQuery(SourceBean dataSetSB) {
		String query = null;
		
		SourceBean querySB = (SourceBean)dataSetSB.getAttribute(GeoEngineConstants.QUERY_TAG);
		if(querySB == null) {
			logger.warn("Cannot find query configuration settings: tag name " + GeoEngineConstants.QUERY_TAG);
			logger.info("Datasource configuration settings must be injected at execution time");
			return null;
		}
		
		query = querySB.getCharacters();
		
		return query;
	}
}
