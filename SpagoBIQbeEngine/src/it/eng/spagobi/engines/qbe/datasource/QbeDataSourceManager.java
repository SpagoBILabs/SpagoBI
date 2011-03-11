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

import it.eng.qbe.datasource.DBConnection;
import it.eng.qbe.datasource.DriverManager;
import it.eng.qbe.datasource.IDataSource;
import it.eng.qbe.datasource.configuration.CompositeDataSourceConfiguration;
import it.eng.qbe.datasource.configuration.FileDataSourceConfiguration;
import it.eng.qbe.datasource.naming.DataSourceNamingStrategy;
import it.eng.spagobi.engines.qbe.QbeEngineConfig;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Andrea Gioia
 */
public class QbeDataSourceManager {
	
	private DataSourceNamingStrategy namingStartegy;
	
	//private DataSourceCache dataSourceCache;
	
	private static QbeDataSourceManager instance;
	
	/**
	 * Gets the single instance of QbeDataSourceManager.
	 * 
	 * @return single instance of QbeDataSourceManager
	 */
	public static QbeDataSourceManager getInstance() {
		if(instance == null) {
			DataSourceNamingStrategy namingStartegy = QbeEngineConfig.getInstance().getNamingStrategy();
			//QbeDataSourceCache dataSourceCache = QbeDataSourceCache.getInstance();
			instance = new QbeDataSourceManager(namingStartegy/*, dataSourceCache*/);
		}
		
		return instance;
	}
	
	/**
	 * Instantiates a new qbe data source manager.
	 * 
	 * @param namingStartegy the naming startegy
	 * @param dataSourceCache the data source cache
	 */
	private QbeDataSourceManager(DataSourceNamingStrategy namingStartegy /*, QbeDataSourceCache dataSourceCache*/) {
		setNamingStartegy(namingStartegy);
		//setDataSourceCache(dataSourceCache);
	}
	
	
	
	/* (non-Javadoc)
	 * @see it.eng.qbe.datasource.DataSourceManager#getDataSource(java.util.List, it.eng.qbe.datasource.DBConnection)
	 */
	public IDataSource getDataSource(List<String> dataMartNames, DBConnection connection) {
		return getDataSource(dataMartNames, new HashMap(), connection);
	}
	
	/* (non-Javadoc)
	 * @see it.eng.qbe.datasource.DataSourceManager#getDataSource(java.util.List, java.util.Map, it.eng.qbe.datasource.DBConnection)
	 */
	public IDataSource getDataSource(List<String> dataMartNames, Map dblinkMap, DBConnection connection) {
		
		IDataSource dataSource = null;
		String dataSourceName = null;
	
		dataSourceName = getNamingStartegy().getDataSourceName(dataMartNames, connection);
		//dataSource = getDataSourceCache().getDataSource(dataSourceName);
		
		CompositeDataSourceConfiguration compositeConfiguration = new CompositeDataSourceConfiguration(dataSourceName);
		compositeConfiguration.loadDataSourceProperties().put("dblinkMap", dblinkMap);
		compositeConfiguration.loadDataSourceProperties().put("connection", connection);
		if (dataSource == null) {
			boolean isJPA = false;
			File file;
			FileDataSourceConfiguration c;
			
			JarFileRetriever jarFileRetriever = new JarFileRetriever(QbeEngineConfig.getInstance().getQbeDataMartDir());
			
			file = jarFileRetriever.loadDatamartJarFile(dataMartNames.get(0));
			c = new FileDataSourceConfiguration(dataMartNames.get(0),file);
			compositeConfiguration.addSubConfiguration(c);
			
			try {				
				isJPA = jarFileRetriever.isAJPADatamartJarFile(file);
			} catch (Exception e) {
				throw new SpagoBIRuntimeException("Error loading mapping file associated to datamart [" + c.getModelName()  + "]", e);
			}
			
			if(dataMartNames.size() > 1) {
				for(int i = 1; i < dataMartNames.size(); i++) {
					file = jarFileRetriever.loadDatamartJarFile(dataMartNames.get(i));
					c = new FileDataSourceConfiguration(dataMartNames.get(i),file);
					compositeConfiguration.addSubConfiguration(c);
					
					boolean b;
					try {
						b = jarFileRetriever.isAJPADatamartJarFile(file);
					} catch (Exception e) {
						throw new SpagoBIRuntimeException("Error loading mapping file associated to datamart [" + c.getModelName() + "]", e);
					}
					if(isJPA != b) {
						throw new SpagoBIRuntimeException("Impossible to create a composite datasource from different datasource type");
					}
				}
			}
			String driverName = isJPA? "jpa": "hibernate";
			dataSource = DriverManager.getDataSource(driverName, dataSourceName, compositeConfiguration);
			
			//getDataSourceCache().addDataSource(dataSourceName, dataSource);
		} 
		
		return dataSource;
	}

	/*
	private DataSourceCache getDataSourceCache() {
		return dataSourceCache;
	}

	
	private void setDataSourceCache(DataSourceCache dataSourceCache) {
		this.dataSourceCache = dataSourceCache;
	}
	*/

	private DataSourceNamingStrategy getNamingStartegy() {
		return namingStartegy;
	}
	

	private void setNamingStartegy(DataSourceNamingStrategy namingStartegy) {
		this.namingStartegy = namingStartegy;
	}
}
