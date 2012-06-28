/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.geo.datamart.provider.configurator;

import it.eng.spago.base.SourceBean;
import it.eng.spago.base.SourceBeanException;
import it.eng.spagobi.engines.geo.GeoEngineConstants;
import it.eng.spagobi.engines.geo.GeoEngineException;
import it.eng.spagobi.engines.geo.datamart.provider.DataMartProvider;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.datasource.bo.DataSource;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.engines.EngineConstants;

import org.apache.log4j.Logger;

// TODO: Auto-generated Javadoc
/**
 * The Class SQLDatasetProviderConfigurator.
 * 
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class DataMartProviderConfigurator {
	
	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(DataMartProviderConfigurator.class);
	
	
	/**
	 * Configure.
	 * 
	 * @param datamartProvider the sql dataset provider
	 * @param conf the conf
	 * 
	 * @throws GeoEngineException the geo engine exception
	 */
	public static void configure(DataMartProvider datamartProvider, Object conf) throws GeoEngineException {
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
			IDataSource dataSource = null;
			String query = null;
			IDataSet dataSet = null;
			
			dataSet = (IDataSet)datamartProvider.getEnv().get(EngineConstants.ENV_DATASET);
			if(dataSet != null) {
				datamartProvider.setDs(dataSet);
				return;
			}
			
			SourceBean dataSetSB = (SourceBean)confSB.getAttribute(GeoEngineConstants.DATASET_TAG);
			if(dataSetSB == null) {
				logger.warn("Cannot find dataset configuration settings: tag name " + GeoEngineConstants.DATASET_TAG);
				logger.info("Dataset configuration settings must be injected at execution time");
			} else {
				dataSource = getDataSource( dataSetSB );
				query = getQuery( dataSetSB );	
				
				if(datamartProvider.getEnv().get(EngineConstants.ENV_DATASOURCE) != null) {
					dataSource = (DataSource)datamartProvider.getEnv().get(EngineConstants.ENV_DATASOURCE);
				}
				
				datamartProvider.setDataSource(dataSource);
				datamartProvider.setQuery(query);				
			}
			
					
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
	public static DataSource getDataSource(SourceBean confSB) throws GeoEngineException {
		DataSource dataSource = null;
		
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
