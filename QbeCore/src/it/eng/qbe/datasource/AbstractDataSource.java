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
package it.eng.qbe.datasource;

import it.eng.qbe.classloader.ClassLoaderManager;
import it.eng.qbe.datasource.configuration.IDataSourceConfiguration;
import it.eng.qbe.datasource.jpa.JPADataSource;
import it.eng.qbe.model.accessmodality.IModelAccessModality;
import it.eng.qbe.model.properties.IModelProperties;
import it.eng.qbe.model.structure.IModelStructure;
import it.eng.qbe.query.Query;
import it.eng.qbe.statement.IStatement;
import it.eng.qbe.statement.StatementFactory;
import it.eng.spagobi.utilities.DynamicClassLoader;

import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.log4j.Logger;

/**
 * @author Andrea Gioia
 */
public abstract class AbstractDataSource implements IDataSource {
	
	protected String name;
	protected IDataSourceConfiguration configuration;
	
	protected IModelAccessModality dataMartModelAccessModality;
	protected IModelStructure dataMartModelStructure;

	protected Map<String, IModelProperties> modelPropertiesCache;		
	
	private static transient Logger logger = Logger.getLogger(AbstractDataSource.class);
	
	public IDataSourceConfiguration getConfiguration() {
		return configuration;
	}

	
	public IStatement createStatement(Query query) {
		return StatementFactory.createStatement(this, query);
	}
	
	public IModelAccessModality getModelAccessModality() {
		return dataMartModelAccessModality;
	}

	public void setDataMartModelAccessModality(
			IModelAccessModality dataMartModelAccessModality) {
		this.dataMartModelAccessModality = dataMartModelAccessModality;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public IModelProperties getModelI18NProperties(Locale locale) {
		IModelProperties properties;
		
		if(modelPropertiesCache == null) {
			modelPropertiesCache = new HashMap<String, IModelProperties>();
		}
		
		String key = name + ":" + "labels";
		if(locale != null) {
			key += "_" + locale.getLanguage();
		}
		
		properties = modelPropertiesCache.get(key);
		
		if(properties == null) {			
			properties = getConfiguration().loadModelI18NProperties(locale);
			modelPropertiesCache.put(key, properties);
		}
		return properties;
	}
	
	protected static void updateCurrentClassLoader(File jarFile){
		ClassLoaderManager.updateCurrentClassLoader(jarFile);
	}
	
}
