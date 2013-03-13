/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.qbe.datasource;

import it.eng.qbe.datasource.DriverManager;
import it.eng.qbe.datasource.IDataSource;
import it.eng.qbe.datasource.configuration.CompositeDataSourceConfiguration;
import it.eng.qbe.datasource.configuration.FileDataSourceConfiguration;
import it.eng.qbe.datasource.naming.IDataSourceNamingStrategy;
import it.eng.spagobi.engines.qbe.QbeEngineConfig;
import it.eng.spagobi.services.proxy.MetamodelServiceProxy;
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
		
	private static QbeDataSourceManager instance;
	
	/**
	 * Gets the single instance of QbeDataSourceManager.
	 * 
	 * @return single instance of QbeDataSourceManager
	 */
	public static QbeDataSourceManager getInstance() {
		if(instance == null) {
			instance = new QbeDataSourceManager();
		}
		
		return instance;
	}
	
	public IDataSource getDataSource(List<String> dataMartNames, Map<String, Object> dataSourceProperties, boolean useCache) {
		
		IDataSource dataSource;
		
		CompositeDataSourceConfiguration compositeConfiguration = new CompositeDataSourceConfiguration();
		Iterator<String> it = dataSourceProperties.keySet().iterator();
		while(it.hasNext()) {
			String propertyName = it.next();
			compositeConfiguration.loadDataSourceProperties().put(propertyName, dataSourceProperties.get(propertyName));
		}
		
	
		boolean isJPA = false;
		File modelJarFile;
		FileDataSourceConfiguration c;
			
		MetamodelServiceProxy metamodelProxy = (MetamodelServiceProxy)dataSourceProperties.get("metadataServiceProxy");
		MetamodelJarFileRetriever jarFileRetriever = new MetamodelJarFileRetriever(metamodelProxy, QbeEngineConfig.getInstance().getQbeDataMartDir());
		List<File> modelJarFiles = new ArrayList<File>();
		for(int i = 0; i < dataMartNames.size(); i++) {
			modelJarFile = jarFileRetriever.loadMetamodelJarFile(dataMartNames.get(i));
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
		dataSource = DriverManager.getDataSource(driverName, compositeConfiguration, useCache);
		
		return dataSource;
	}
}
