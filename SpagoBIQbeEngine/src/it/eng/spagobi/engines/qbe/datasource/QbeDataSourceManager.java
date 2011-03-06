/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
package it.eng.spagobi.engines.qbe.datasource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.eng.qbe.datasource.DBConnection;
import it.eng.qbe.datasource.DataSourceCache;
import it.eng.qbe.datasource.DataSourceFactory;
import it.eng.qbe.datasource.DataSourceManager;
import it.eng.qbe.datasource.IDataSource;
import it.eng.qbe.datasource.hibernate.CompositeHibernateDataSource;
import it.eng.qbe.naming.NamingStrategy;
import it.eng.spagobi.engines.qbe.QbeEngineConfig;

// TODO: Auto-generated Javadoc
/**
 * The Class QbeDataSourceManager.
 * 
 * @author Andrea Gioia
 */
public class QbeDataSourceManager implements DataSourceManager {
	
	/** The naming startegy. */
	private NamingStrategy namingStartegy = null;
	
	/** The data source cache. */
	private DataSourceCache dataSourceCache = null;
		
	/** The instance. */
	private static QbeDataSourceManager instance = null;
	
	/**
	 * Gets the single instance of QbeDataSourceManager.
	 * 
	 * @return single instance of QbeDataSourceManager
	 */
	public static QbeDataSourceManager getInstance() {
		if(instance == null) {
			NamingStrategy namingStartegy = QbeEngineConfig.getInstance().getNamingStrategy();
			DataSourceCache dataSourceCache = QbeEngineConfig.getInstance().getDataSourceCache();
			instance = new QbeDataSourceManager(namingStartegy, dataSourceCache);
		}
		
		return instance;
	}
	
	/**
	 * Instantiates a new qbe data source manager.
	 * 
	 * @param namingStartegy the naming startegy
	 * @param dataSourceCache the data source cache
	 */
	private QbeDataSourceManager(NamingStrategy namingStartegy, DataSourceCache dataSourceCache) {
		setNamingStartegy(namingStartegy);
		setDataSourceCache(dataSourceCache);
	}
	
	
	
	/* (non-Javadoc)
	 * @see it.eng.qbe.datasource.DataSourceManager#getDataSource(java.util.List, it.eng.qbe.datasource.DBConnection)
	 */
	public IDataSource getDataSource(List dataMartNames, DBConnection connection) {
		return getDataSource(dataMartNames, new HashMap(), connection);
	}
	
	/* (non-Javadoc)
	 * @see it.eng.qbe.datasource.DataSourceManager#getDataSource(java.util.List, java.util.Map, it.eng.qbe.datasource.DBConnection)
	 */
	public IDataSource getDataSource(List dataMartNames, Map dblinkMap, DBConnection connection) {
		
		IDataSource dataSource = null;
		String dataSourceName = null;
		String dataMartName = null;
		
		
		dataSourceName = getNamingStartegy().getDatasourceName(dataMartNames, connection);
		dataMartName = getNamingStartegy().getDatamartName(dataMartNames);
		
		dataSource = getDataSourceCache().getDataSource(dataSourceName);

		if (dataSource == null) {
			dataSource = DataSourceFactory.buildDataSource(dataSourceName, dataMartName, dataMartNames, dblinkMap, connection);
			getDataSourceCache().addDataSource(dataSourceName, dataSource);
		} else if(dataSource instanceof CompositeHibernateDataSource) {
			CompositeHibernateDataSource compositeHibernateDataSource = (CompositeHibernateDataSource)dataSource;
		}
		
		return dataSource;
	}

	/**
	 * Gets the data source cache.
	 * 
	 * @return the data source cache
	 */
	private DataSourceCache getDataSourceCache() {
		return dataSourceCache;
	}

	/**
	 * Sets the data source cache.
	 * 
	 * @param dataSourceCache the new data source cache
	 */
	private void setDataSourceCache(DataSourceCache dataSourceCache) {
		this.dataSourceCache = dataSourceCache;
	}

	/**
	 * Gets the naming startegy.
	 * 
	 * @return the naming startegy
	 */
	private NamingStrategy getNamingStartegy() {
		return namingStartegy;
	}

	/**
	 * Sets the naming startegy.
	 * 
	 * @param namingStartegy the new naming startegy
	 */
	private void setNamingStartegy(NamingStrategy namingStartegy) {
		this.namingStartegy = namingStartegy;
	}
}
