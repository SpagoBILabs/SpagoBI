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
package it.eng.spagobi.engines.qbe.datasource;

import it.eng.qbe.datasource.DriverManager;
import it.eng.qbe.datasource.IDataSource;
import it.eng.qbe.datasource.configuration.CompositeDataSourceConfiguration;
import it.eng.qbe.datasource.configuration.FileDataSourceConfiguration;
import it.eng.qbe.datasource.naming.IDataSourceNamingStrategy;
import it.eng.spagobi.engines.qbe.QbeEngineConfig;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * @author Andrea Gioia
 */
public class QbeDataSourceManager {
	
	private IDataSourceNamingStrategy namingStartegy;
	
	//private DataSourceCache dataSourceCache;
	
	private static QbeDataSourceManager instance;
	
	/**
	 * Gets the single instance of QbeDataSourceManager.
	 * 
	 * @return single instance of QbeDataSourceManager
	 */
	public static QbeDataSourceManager getInstance() {
		if(instance == null) {
			IDataSourceNamingStrategy namingStartegy = QbeEngineConfig.getInstance().getNamingStrategy();
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
	private QbeDataSourceManager(IDataSourceNamingStrategy namingStartegy /*, QbeDataSourceCache dataSourceCache*/) {
		setNamingStartegy(namingStartegy);
		//setDataSourceCache(dataSourceCache);
	}
	
	
	
	
	/* (non-Javadoc)
	 * @see it.eng.qbe.datasource.DataSourceManager#getDataSource(java.util.List, java.util.Map, it.eng.qbe.datasource.DBConnection)
	 */
	public IDataSource getDataSource(List<String> dataMartNames, Map<String, Object> dataSourceProperties) {
		
		IDataSource dataSource;
		
		// = getNamingStartegy().getDataSourceName(dataMartNames, connection);
		//dataSource = getDataSourceCache().getDataSource(dataSourceName);
		
		CompositeDataSourceConfiguration compositeConfiguration = new CompositeDataSourceConfiguration();
		Iterator<String> it = dataSourceProperties.keySet().iterator();
		while(it.hasNext()) {
			String propertyName = it.next();
			compositeConfiguration.loadDataSourceProperties().put(propertyName, dataSourceProperties.get(propertyName));
		}
		
	
		boolean isJPA = false;
		File modelJarFile;
		FileDataSourceConfiguration c;
			
		JarFileRetriever jarFileRetriever = new JarFileRetriever(QbeEngineConfig.getInstance().getQbeDataMartDir());
		List<File> modelJarFiles = new ArrayList<File>();
		for(int i = 0; i < dataMartNames.size(); i++) {
			modelJarFile = jarFileRetriever.loadDatamartJarFile(dataMartNames.get(i));
			modelJarFiles.add(modelJarFile);
			c = new FileDataSourceConfiguration(dataMartNames.get(i), modelJarFile);
			compositeConfiguration.addSubConfiguration(c);
		}
		
		isJPA = jarFileRetriever.isAJPADatamartJarFile(modelJarFiles.get(0));
		if(modelJarFiles.size() > 1) {
			for(int i = 1; i < modelJarFiles.size(); i++) {
				modelJarFile = modelJarFiles.get(i);
				boolean b = jarFileRetriever.isAJPADatamartJarFile(modelJarFile);
				if(isJPA != b) {
					throw new SpagoBIRuntimeException("Impossible to create a composite datasource from different datasource type");
				}
			}
		}
		
		String driverName = isJPA? "jpa": "hibernate";
		dataSource = DriverManager.getDataSource(driverName, compositeConfiguration);
		
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

	private IDataSourceNamingStrategy getNamingStartegy() {
		return namingStartegy;
	}
	

	private void setNamingStartegy(IDataSourceNamingStrategy namingStartegy) {
		this.namingStartegy = namingStartegy;
	}
}
