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
package it.eng.qbe.datasource.hibernate;

import it.eng.qbe.dao.DAOFactory;
import it.eng.qbe.datasource.AbstractDataSource;
import it.eng.qbe.datasource.DBConnection;
import it.eng.qbe.datasource.FileDataSourceConfiguration;
import it.eng.qbe.model.accessmodality.DataMartModelAccessModality;
import it.eng.spago.base.ApplicationContainer;
import it.eng.spagobi.utilities.DynamicClassLoader;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Table;


public class HibernateDataSource extends AbstractDataSource implements IHibernateDataSource {

	protected boolean classLoaderExtended = false;		
	
	protected Configuration compositeConfiguration;	
	protected SessionFactory compositeSessionFactory;
	
	protected Map<String, Configuration> configurationMap = new HashMap<String, Configuration>();	
	protected Map<String, SessionFactory> sessionFactoryMap = new HashMap<String, SessionFactory>();	

	protected Map<String,String> dblinkMap;
	protected DBConnection connection;
	
	private static transient Logger logger = Logger.getLogger(HibernateDataSource.class);

	public HibernateDataSource(String dataSourceName, List<FileDataSourceConfiguration> configurations) {
		setName( dataSourceName );
		dataMartModelAccessModality = new DataMartModelAccessModality();
		this.configurations = configurations;
	}

	public boolean isCompositeDataSource() {
		return configurations.size() > 1;
	}
	
	public synchronized void open() {
		logger.debug("IN");
		
		try {
			if(!isOpen()) {
				compositeConfiguration = buildEmptyConfiguration();
				
				addDatamarts();
				
				if(isCompositeDataSource()) {
					addDbLinks();	
					compositeSessionFactory = compositeConfiguration.buildSessionFactory();
				} else {
					compositeSessionFactory = sessionFactoryMap.get(configurations.get(0).getModelName());
				}
				
				classLoaderExtended = true;
			}
		} catch (Throwable t){
			throw new SpagoBIRuntimeException("Impossible to open connection", t);
		} finally {
			logger.debug("OUT");
		}
	}
	
	public boolean isOpen() {
		return compositeSessionFactory != null && compositeConfiguration != null;
	}
	
	public void close() {
		compositeSessionFactory = null;
		compositeConfiguration = null;
		configurationMap = new HashMap<String, Configuration>();	
		sessionFactoryMap = new HashMap<String, SessionFactory>();
		//classLoaderExtended = false;
	}
		
	
	protected void addDatamarts() {
		
		for(int i = 0; i < configurations.size(); i++) {
			addDatamart(configurations.get(i), !classLoaderExtended);		
		}	
		classLoaderExtended = true;
	}
	
	private void addDatamart(FileDataSourceConfiguration configuration, boolean extendClassLoader) {
		Configuration cfg = null;	
		SessionFactory sf = null;
		
		if(configuration.getFile() == null) return;
		
		cfg = buildEmptyConfiguration();
		configurationMap.put(configuration.getModelName(), cfg);
		
		if (extendClassLoader){
			updateCurrentClassLoader(configuration.getFile());
		}	
		
		cfg.addJar(configuration.getFile());
		
		try {
			compositeConfiguration.addJar(configuration.getFile());
		} catch (Throwable t) {
			throw new RuntimeException("Cannot add datamart", t);
		}
		
		sf = cfg.buildSessionFactory();
		sessionFactoryMap.put(configuration.getModelName(), sf);		
	}
	
	
	
	
	
	protected Configuration buildEmptyConfiguration() {
		Configuration cfg = null;
		
		cfg = new Configuration();
		
		if(connection.isJndiConncetion()) {
			cfg.setProperty("hibernate.connection.datasource", connection.getJndiName());
		} else {
			cfg.setProperty("hibernate.connection.url", connection.getUrl());
			cfg.setProperty("hibernate.connection.password", connection.getPassword());
			cfg.setProperty("hibernate.connection.username", connection.getUsername());
			cfg.setProperty("hibernate.connection.driver_class", connection.getDriverClass());
		}
				
		cfg.setProperty("hibernate.dialect", connection.getDialect());
		
		cfg.setProperty("hibernate.cglib.use_reflection_optimizer", "true");
		cfg.setProperty("hibernate.show_sql", "false");
		
		return cfg;
	}	

	
	/* (non-Javadoc)
	 * @see it.eng.qbe.datasource.IHibernateDataSource#getConfiguration()
	 */
	public Configuration getConfiguration() {
		if(isOpen() == false) {
			open();
		}
		return compositeConfiguration;
	}
	
	/* (non-Javadoc)
	 * @see it.eng.qbe.datasource.IHibernateDataSource#getSessionFactory()
	 */
	public SessionFactory getSessionFactory() {
		if(isOpen() == false) {
			open();
		}
		return compositeSessionFactory;
	}	
	
	/* (non-Javadoc)
	 * @see it.eng.qbe.datasource.IHibernateDataSource#getSessionFactory(java.lang.String)
	 */
	public SessionFactory getSessionFactory(String dmName) {
		if(compositeSessionFactory == null) open();
		return (SessionFactory)sessionFactoryMap.get(dmName);
	}	
	
	
	public Configuration getConfiguration(String dmName) {
		if(compositeConfiguration == null) open();
		return (Configuration)configurationMap.get(dmName);
	}
	
	
	
	
	
	
	
	protected File getDatamartJarFile(String datamartName){
		File datamartJarFile = null;
		
		try{
			datamartJarFile = DAOFactory.getDatamartJarFileDAO().loadDatamartJarFile(datamartName);
		}catch (Exception e) {
			logger.error("Impossible to find mapping file for datamart [" + datamartName + "]", e);
		}
		
		return datamartJarFile;
	}

	
	
	/**
	 * Update current class loader.
	 * 
	 * @param jarFile the jar file
	 */
	protected static void updateCurrentClassLoader(File jarFile){
		
		boolean wasAlreadyLoaded = false;
		ApplicationContainer container = null;
		
		logger.debug("IN");
		
		try {
			
			logger.debug("jar file to be loaded: " + jarFile.getAbsoluteFile());
			
			container = ApplicationContainer.getInstance();
			if (container != null) {
				ClassLoader cl = (ClassLoader) container.getAttribute("DATAMART_CLASS_LOADER");
				if (cl != null) {
					logger.debug("Found a cached loader of type: " + cl.getClass().getName());
					logger.debug("Set as current loader the one previusly cached");
					Thread.currentThread().setContextClassLoader(cl);
				}
			}
			
			JarFile jar = new JarFile(jarFile);
			Enumeration entries = jar.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = (JarEntry) entries.nextElement();
				if (entry.getName().endsWith(".class")) {
					String entryName = entry.getName();
					String className = entryName.substring(0, entryName.lastIndexOf(".class"));
					className = className.replaceAll("/", ".");
					className = className.replaceAll("\\\\", ".");
					try {
						logger.debug("loading class [" + className  + "]" + " with class loader [" + Thread.currentThread().getContextClassLoader().getClass().getName()+ "]");
						Thread.currentThread().getContextClassLoader().loadClass(className);
						wasAlreadyLoaded = true;
						logger.debug("Class [" + className  + "] has been already loaded (?");
						break;
					} catch (Exception e) {
						wasAlreadyLoaded = false;
						logger.debug("Class [" + className  + "] hasn't be loaded yet (?)");
						break;
					}
				}
			}
			
		} catch (Exception e) {
			logger.error("Impossible to update current clas loader", e);
		}
		
		logger.debug("Jar file [" + jarFile.getName()  + "] already loaded: " + wasAlreadyLoaded);
		
		try {
			/*
			 * TEMPORARY: the next instruction forcing the loading of all classes in the path...
			 * (ie. for some qbe that have in common any classes but not all and that at the moment they aren't loaded corretly)
			 */
			wasAlreadyLoaded = false;

			if (!wasAlreadyLoaded) {
				
				ClassLoader previous = Thread.currentThread().getContextClassLoader();
    		    DynamicClassLoader current = new DynamicClassLoader(jarFile, previous);
			    Thread.currentThread().setContextClassLoader(current);

				//ClassLoader current = URLClassLoader.newInstance(new URL[]{jarFile.toURL()}, previous);				
				//Thread.currentThread().setContextClassLoader(current);
				
				if (container != null) container.setAttribute("DATAMART_CLASS_LOADER", current);

			}
			
		} catch (Exception e) {
			logger.error("Impossible to update current clas loader", e);
		}
	}


	/* (non-Javadoc)
	 * @see it.eng.qbe.datasource.IHibernateDataSource#getConnection()
	 */
	public DBConnection getConnection() {
		return connection;
	}


	/**
	 * Sets the connection.
	 * 
	 * @param connection the new connection
	 */
	public void setConnection(DBConnection connection) {
		this.connection = connection;
	}


	/**
	 * Gets the dblink map.
	 * 
	 * @return the dblink map
	 */
	public Map getDblinkMap() {
		return dblinkMap;
	}


	/**
	 * Sets the dblink map.
	 * 
	 * @param dblinkMap the new dblink map
	 */
	public void setDblinkMap(Map dblinkMap) {
		this.dblinkMap = dblinkMap;
	}

		

	protected void addDbLink(File datamartFile, Configuration srcCfg, Configuration dstCfg) {
		
		String dbLink = null;
		PersistentClass srcPersistentClass = null;
		PersistentClass dstPersistentClass = null;
		String targetEntityName = null;
		Table targetTable = null;
		
		dbLink = dblinkMap.get(datamartFile);
		if (dbLink != null) {
			Iterator it = srcCfg.getClassMappings();
			while(it.hasNext()) {
				srcPersistentClass = (PersistentClass)it.next();
				targetEntityName = srcPersistentClass.getEntityName();
				dstPersistentClass = dstCfg.getClassMapping(targetEntityName);
				targetTable = dstPersistentClass.getTable();
				targetTable.setName(targetTable.getName() + "@" + dbLink);
			}
		}
		
	}

	
	private void addDbLinks() {
		Configuration cfg = null;
		
		for(int i = 0; i < configurations.size(); i++) {
			File datamartFile = configurations.get(i).getFile();
			cfg = (Configuration)configurationMap.get(datamartFile);
			addDbLink(datamartFile, cfg, compositeConfiguration);
		}
	}
	
	
	protected File loadFormulaFile(File datamartFile) {
		String formulaFile = datamartFile.getParent() + "/formula.xml";
		return new File(formulaFile);
	}
}
