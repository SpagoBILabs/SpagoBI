/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.
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
import it.eng.qbe.datasource.ConnectionDescriptor;
import it.eng.qbe.datasource.IPersistenceManager;
import it.eng.qbe.datasource.configuration.CompositeDataSourceConfiguration;
import it.eng.qbe.datasource.configuration.FileDataSourceConfiguration;
import it.eng.qbe.datasource.configuration.IDataSourceConfiguration;
import it.eng.qbe.datasource.transaction.ITransaction;
import it.eng.qbe.datasource.transaction.hibernate.HibernateTransaction;
import it.eng.qbe.model.accessmodality.AbstractModelAccessModality;
import it.eng.qbe.model.structure.IModelStructure;
import it.eng.qbe.model.structure.builder.IModelStructureBuilder;
import it.eng.qbe.model.structure.builder.hibernate.HibernateModelStructureBuilder;
import it.eng.spagobi.engines.qbe.registry.bo.RegistryConfiguration;
import it.eng.spagobi.engines.qbe.registry.bo.RegistryConfiguration.Column;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceException;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.property.Setter;
import org.hibernate.type.ManyToOneType;
import org.hibernate.type.Type;
import org.json.JSONObject;


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
	
	protected void addDatamart(FileDataSourceConfiguration configuration, boolean extendClassLoader) {
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
	
	public ConnectionDescriptor getConnection() {
		ConnectionDescriptor connection = (ConnectionDescriptor)configuration.loadDataSourceProperties().get("connection");
		return connection;
	}

	private Map getDbLinkMap() {
		Map dbLinkMap = (Map)configuration.loadDataSourceProperties().get("dblinkMap");
		return dbLinkMap;
	}
	
	protected Configuration buildEmptyConfiguration() {
		Configuration cfg = null;
		
		cfg = new Configuration();
		
		ConnectionDescriptor connection = getConnection();
		
		if(connection.isJndiConncetion()) {
			cfg.setProperty("hibernate.connection.datasource", connection.getJndiName());
		} else {
			cfg.setProperty("hibernate.connection.url", connection.getUrl());
			cfg.setProperty("hibernate.connection.password", connection.getPassword());
			cfg.setProperty("hibernate.connection.username", connection.getUsername());
			cfg.setProperty("hibernate.connection.driver_class", connection.getDriverClass());
		}
				
		cfg.setProperty("hibernate.dialect", connection.getDialect());
		
		// Ingres does not support scrollable result set
		if ("org.hibernate.dialect.IngresDialect".equals(connection.getDialect())) {
			cfg.setProperty("hibernate.jdbc.use_scrollable_resultset", "false");
		}
		
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

	protected void addDbLinks() {
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

	public ITransaction getTransaction(){
		return new HibernateTransaction(this);
	}

	public IPersistenceManager getPersistenceManager() {
		// TODO Auto-generated method stub
		return new HibernatePersistenceManager(this);
	}


}
