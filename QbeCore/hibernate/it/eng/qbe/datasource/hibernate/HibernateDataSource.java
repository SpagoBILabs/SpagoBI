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

import it.eng.qbe.datasource.AbstractDataSource;
import it.eng.qbe.datasource.DBConnection;
import it.eng.qbe.datasource.configuration.CompositeDataSourceConfiguration;
import it.eng.qbe.datasource.configuration.FileDataSourceConfiguration;
import it.eng.qbe.datasource.configuration.IDataSourceConfiguration;
import it.eng.qbe.model.accessmodality.AbstractModelAccessModality;
import it.eng.qbe.model.structure.IModelStructure;
import it.eng.qbe.model.structure.ModelStructure;
import it.eng.qbe.model.structure.builder.IModelStructureBuilder;
import it.eng.qbe.model.structure.builder.hibernate.HibernateModelStructureBuilder;
import it.eng.qbe.model.accessmodality.ModelAccessModality;
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
	
	protected Configuration compositeHibernateConfiguration;	
	protected SessionFactory compositeHibernateSessionFactory;
	
	protected Map<String, Configuration> configurationMap = new HashMap<String, Configuration>();	
	protected Map<String, SessionFactory> sessionFactoryMap = new HashMap<String, SessionFactory>();	

	
	private static transient Logger logger = Logger.getLogger(HibernateDataSource.class);

	protected HibernateDataSource(String dataSourceName, IDataSourceConfiguration configuration) {
		setName( dataSourceName );
		dataMartModelAccessModality = new AbstractModelAccessModality();

		// validate & set configuration
		if(configuration instanceof FileDataSourceConfiguration) {
			FileDataSourceConfiguration subConf = (FileDataSourceConfiguration)configuration;
			CompositeDataSourceConfiguration c = new CompositeDataSourceConfiguration(subConf.getModelName());
			c.addSubConfiguration(subConf);
			Iterator<String> it = subConf.loadDataSourceProperties().keySet().iterator();
			while(it.hasNext()) {
				String propertyName = it.next();
				c.loadDataSourceProperties().put(propertyName, subConf.loadDataSourceProperties().get(propertyName));
			}
			this.configuration = c;
		} else if(configuration instanceof CompositeDataSourceConfiguration) {
			CompositeDataSourceConfiguration c = (CompositeDataSourceConfiguration)configuration;
			if(c.getSubConfigurations() == null || c.getSubConfigurations().size() < 1) {
				throw new SpagoBIRuntimeException("Impossible to create HibernateDataSource. Datasource sub-configurations not defined");
			}
			for(int i = 0; i < c.getSubConfigurations().size(); i++) {				
				if( !(c.getSubConfigurations().get(i) instanceof FileDataSourceConfiguration) ) {
					throw new SpagoBIRuntimeException("Impossible to create HibernateDataSource. Unable to manage sub-configuration of type [" + c.getSubConfigurations().get(i).getClass().getName() + "]");
				}
			}
			this.configuration = configuration;
		} else {
			throw new SpagoBIRuntimeException("Impossible to create HibernateDataSource. Unable to manage configuration of type [" + configuration.getClass().getName() + "]");
		}
		
		
		
		
	}

	public List<IDataSourceConfiguration> getSubConfigurations() {
		return ((CompositeDataSourceConfiguration)configuration).getSubConfigurations();
	}
	
	public boolean isCompositeDataSource() {
		boolean isComposite = false;
		if(configuration instanceof CompositeDataSourceConfiguration) {
			isComposite = (((CompositeDataSourceConfiguration)configuration).getSubConfigurations().size() > 1 );
		}
		
		return isComposite;
	}
	
	public synchronized void open() {
		logger.debug("IN");
		
		try {
			if(!isOpen()) {
				compositeHibernateConfiguration = buildEmptyConfiguration();
				
				addDatamarts();
				
				if(isCompositeDataSource()) {
					addDbLinks();	
					compositeHibernateSessionFactory = compositeHibernateConfiguration.buildSessionFactory();
				} else {
					compositeHibernateSessionFactory = sessionFactoryMap.get(getSubConfigurations().get(0).getModelName());
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
		return compositeHibernateSessionFactory != null && compositeHibernateConfiguration != null;
	}
	
	public void close() {
		compositeHibernateSessionFactory = null;
		compositeHibernateConfiguration = null;
		configurationMap = new HashMap<String, Configuration>();	
		sessionFactoryMap = new HashMap<String, SessionFactory>();
		//classLoaderExtended = false;
	}
		
	
	protected void addDatamarts() {
		
		for(int i = 0; i < getSubConfigurations().size(); i++) {
			addDatamart((FileDataSourceConfiguration)getSubConfigurations().get(i), !classLoaderExtended);		
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
			compositeHibernateConfiguration.addJar(configuration.getFile());
		} catch (Throwable t) {
			throw new RuntimeException("Cannot add datamart", t);
		}
		
		sf = cfg.buildSessionFactory();
		sessionFactoryMap.put(configuration.getModelName(), sf);		
	}
	
	private DBConnection getConnection() {
		DBConnection connection = (DBConnection)configuration.loadDataSourceProperties().get("connection");
		return connection;
	}

	private Map getDbLinkMap() {
		Map dbLinkMap = (Map)configuration.loadDataSourceProperties().get("dblinkMap");
		return dbLinkMap;
	}
	
	protected Configuration buildEmptyConfiguration() {
		Configuration cfg = null;
		
		cfg = new Configuration();
		
		if(getConnection().isJndiConncetion()) {
			cfg.setProperty("hibernate.connection.datasource", getConnection().getJndiName());
		} else {
			cfg.setProperty("hibernate.connection.url", getConnection().getUrl());
			cfg.setProperty("hibernate.connection.password", getConnection().getPassword());
			cfg.setProperty("hibernate.connection.username", getConnection().getUsername());
			cfg.setProperty("hibernate.connection.driver_class", getConnection().getDriverClass());
		}
				
		cfg.setProperty("hibernate.dialect", getConnection().getDialect());
		
		cfg.setProperty("hibernate.cglib.use_reflection_optimizer", "true");
		cfg.setProperty("hibernate.show_sql", "false");
		
		return cfg;
	}	

	
	/* (non-Javadoc)
	 * @see it.eng.qbe.datasource.IHibernateDataSource#getConfiguration()
	 */
	public Configuration getHibernateConfiguration() {
		if(isOpen() == false) {
			open();
		}
		return compositeHibernateConfiguration;
	}
	
	/* (non-Javadoc)
	 * @see it.eng.qbe.datasource.IHibernateDataSource#getSessionFactory()
	 */
	public SessionFactory getHibernateSessionFactory() {
		if(isOpen() == false) {
			open();
		}
		return compositeHibernateSessionFactory;
	}	
	
	/* (non-Javadoc)
	 * @see it.eng.qbe.datasource.IHibernateDataSource#getSessionFactory(java.lang.String)
	 */
	public SessionFactory getHibernateSessionFactory(String dmName) {
		if(compositeHibernateSessionFactory == null) open();
		return (SessionFactory)sessionFactoryMap.get(dmName);
	}	
	
	
	public Configuration getConfiguration(String dmName) {
		if(compositeHibernateConfiguration == null) open();
		return (Configuration)configurationMap.get(dmName);
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


	

	protected void addDbLink(String modelName, Configuration srcCfg, Configuration dstCfg) {
		
		String dbLink = null;
		PersistentClass srcPersistentClass = null;
		PersistentClass dstPersistentClass = null;
		String targetEntityName = null;
		Table targetTable = null;
		
		dbLink = (String)getDbLinkMap().get(modelName);
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
		
		for(int i = 0; i < getSubConfigurations().size(); i++) {
			String modelName = getSubConfigurations().get(i).getModelName();
			cfg = (Configuration)configurationMap.get(modelName);
			addDbLink(modelName, cfg, compositeHibernateConfiguration);
		}
	}

	public IModelStructure getModelStructure() {
		IModelStructureBuilder structureBuilder;
		if(dataMartModelStructure == null) {			
			structureBuilder = new HibernateModelStructureBuilder(this);
			dataMartModelStructure = structureBuilder.build();
		}
		
		return dataMartModelStructure;
	}

}
