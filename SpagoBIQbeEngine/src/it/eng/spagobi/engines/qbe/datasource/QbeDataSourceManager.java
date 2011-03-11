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

import it.eng.qbe.dao.DatamartJarFileDAOFilesystemImpl;
import it.eng.qbe.datasource.DBConnection;
import it.eng.qbe.datasource.DataSourceCache;
import it.eng.qbe.datasource.DriverManager;
import it.eng.qbe.datasource.IDataSourceManager;
import it.eng.qbe.datasource.IDataSource;
import it.eng.qbe.datasource.configuration.CompositeDataSourceConfiguration;
import it.eng.qbe.datasource.configuration.FileDataSourceConfiguration;
import it.eng.qbe.datasource.configuration.IDataSourceConfiguration;
import it.eng.qbe.naming.NamingStrategy;
import it.eng.spagobi.engines.qbe.QbeEngineConfig;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Andrea Gioia
 */
public class QbeDataSourceManager implements IDataSourceManager {
	
	private NamingStrategy namingStartegy;
	
	//private DataSourceCache dataSourceCache;
	
	private static QbeDataSourceManager instance;
	
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
	
		dataSourceName = getNamingStartegy().getDatasourceName(dataMartNames, connection);
		//dataSource = getDataSourceCache().getDataSource(dataSourceName);
		
		CompositeDataSourceConfiguration compositeConfiguration = new CompositeDataSourceConfiguration(dataSourceName);
		compositeConfiguration.getDataSourceProperties().put("dblinkMap", dblinkMap);
		compositeConfiguration.getDataSourceProperties().put("connection", connection);
		if (dataSource == null) {
			boolean isJPA = false;
			File file;
			FileDataSourceConfiguration c;
			
			DatamartJarFileDAOFilesystemImpl jarFileDAO = new DatamartJarFileDAOFilesystemImpl(QbeEngineConfig.getInstance().getQbeDataMartDir());
			
			file = jarFileDAO.loadDatamartJarFile(dataMartNames.get(0));
			c = new FileDataSourceConfiguration(dataMartNames.get(0),file);
			compositeConfiguration.addSubConfiguration(c);
			
			try {				
				isJPA = jarFileDAO.isAJPADatamartJarFile(file);
			} catch (Exception e) {
				throw new SpagoBIRuntimeException("Error loading mapping file associated to datamart [" + c.getModelName()  + "]", e);
			}
			
			if(dataMartNames.size() > 1) {
				for(int i = 1; i < dataMartNames.size(); i++) {
					file = jarFileDAO.loadDatamartJarFile(dataMartNames.get(i));
					c = new FileDataSourceConfiguration(dataMartNames.get(i),file);
					compositeConfiguration.addSubConfiguration(c);
					
					boolean b;
					try {
						b = jarFileDAO.isAJPADatamartJarFile(file);
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

	private NamingStrategy getNamingStartegy() {
		return namingStartegy;
	}
	

	private void setNamingStartegy(NamingStrategy namingStartegy) {
		this.namingStartegy = namingStartegy;
	}
}
