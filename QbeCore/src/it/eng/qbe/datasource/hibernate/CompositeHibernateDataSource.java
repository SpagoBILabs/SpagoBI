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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import it.eng.qbe.bo.DatamartProperties;
import it.eng.qbe.dao.DAOFactory;
import it.eng.qbe.datasource.DBConnection;


/**
 * The Class CompositeHibernateDataSource.
 * 
 * @author Andrea Gioia
 */
public class CompositeHibernateDataSource extends AbstractHibernateDataSource  {
	
	
	
	
		
	/** memebers */
	private boolean classLoaderExtended = false;	
	private List alreadyAddedView = null;	
	private Map configurationMap = new HashMap();	
	private Map sessionFactoryMap = new HashMap();	
	private Configuration compositeConfiguration = null;	
	private SessionFactory compositeSessionFactory = null;
	
	private boolean hibernateInitialized = false;
	
	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(CompositeHibernateDataSource.class);
	
	
	/**
	 * Instantiates a new composite hibernate data source.
	 * 
	 * @param dataSourceName the data source name
	 * @param datamartName the datamart name
	 * @param datamartNames the datamart names
	 * @param connection the connection
	 */
	public CompositeHibernateDataSource(String dataSourceName, String datamartName, List datamartNames, DBConnection connection) {
		this(dataSourceName, datamartName, datamartNames, new HashMap(), connection);
	}
		
	/**
	 * Instantiates a new composite hibernate data source.
	 * 
	 * @param dataSourceName the data source name
	 * @param datamartName the datamart name
	 * @param datamartNames the datamart names
	 * @param dblinkMap the dblink map
	 * @param connection the connection
	 */
	private CompositeHibernateDataSource(String dataSourceName, String datamartName, List datamartNames, Map dblinkMap, DBConnection connection) {
		
		setName( dataSourceName );
		setType(COMPOSITE_HIBERNATE_DS_TYPE);
		
		setDatamartName(datamartName);		
		setDatamartNames(datamartNames);
		setDblinkMap(dblinkMap);
		
		setConnection(connection);
		
		setProperties();

		this.alreadyAddedView = new ArrayList();		
	}
	
	private void setProperties() {
		DatamartProperties properties = new DatamartProperties();
		Iterator it = datamartNames.iterator();
		while (it.hasNext()) {
			String aDatamartName = (String) it.next();
			properties.addDatamartProperties(DAOFactory.getDatamartPropertiesDAO().loadDatamartProperties( aDatamartName ));
		}
		setProperties( properties );
	}
	
	/**
	 * Instantiates a new composite hibernate data source.
	 * 
	 * @param dataSourceName the data source name
	 */
	public CompositeHibernateDataSource(String dataSourceName) {
		setName( dataSourceName );
		setType( COMPOSITE_HIBERNATE_DS_TYPE );
		alreadyAddedView = new ArrayList();
	}
	
	
	/*
	private Properties loadQbeProperties() {
		Properties properties = new Properties();
		for(int i = 0; i < getDatamartNames().size(); i++) {
			String datamartName = (String)getDatamartNames().get(i);
			properties.putAll( loadLabelProperties(datamartName) );
		}
		return properties;
	}
	
	private Properties loadLabelProperties() {
		Properties properties = new Properties();
		for(int i = 0; i < getDatamartNames().size(); i++) {
			String datamartName = (String)getDatamartNames().get(i);
			properties.putAll( loadLabelProperties(datamartName) );
		}
		return properties;
	}
	*/
	
	/**
	 * TODO marge all the formula files in one file *.
	 * 
	 * @return the file
	 */
	private File loadFormulaFile() {		
		return loadFormulaFile( (String)getDatamartNames().get(0) );
	}

	
	
	
	/* (non-Javadoc)
	 * @see it.eng.qbe.datasource.IHibernateDataSource#getConfiguration()
	 */
	public Configuration getConfiguration() {
		if(compositeConfiguration == null) initHibernate();
		return compositeConfiguration;
	}
	
	/* (non-Javadoc)
	 * @see it.eng.qbe.datasource.IHibernateDataSource#getSessionFactory()
	 */
	public SessionFactory getSessionFactory() {
		if(compositeSessionFactory == null) initHibernate();
		return compositeSessionFactory;
	}
	
	
	/* (non-Javadoc)
	 * @see it.eng.qbe.datasource.IHibernateDataSource#getSessionFactory(java.lang.String)
	 */
	public SessionFactory getSessionFactory(String dmName) {
		if(compositeSessionFactory == null) initHibernate();
		return (SessionFactory)sessionFactoryMap.get(dmName);
	}	
	
	/**
	 * Gets the configuration.
	 * 
	 * @param dmName the dm name
	 * 
	 * @return the configuration
	 */
	public Configuration getConfiguration(String dmName) {
		if(compositeConfiguration == null) initHibernate();
		return (Configuration)configurationMap.get(dmName);
	}
	
		
	
	/**
	 * Inits the hibernate.
	 */
	private synchronized void initHibernate(){
		logger.debug("IN");
		
		if (hibernateInitialized) return;
		
		compositeConfiguration = buildEmptyConfiguration();
		
		addDatamarts();
		addSharedViews();
		addDbLinks();	
		
		compositeSessionFactory = compositeConfiguration.buildSessionFactory();
		
		hibernateInitialized = true;
	}	
	
	
	
	/**
	 * Adds the datamarts.
	 */
	private void addDatamarts() {
		
		for(int i = 0; i < getDatamartNames().size(); i++) {
			String dmName = (String)getDatamartNames().get(i);
			addDatamart(dmName, !classLoaderExtended);		
		}	
		classLoaderExtended = true;
	}
	
	/**
	 * Adds the datamart.
	 * 
	 * @param dmName the dm name
	 * @param extendClassLoader the extend class loader
	 */
	private void addDatamart(String dmName, boolean extendClassLoader) {
		Configuration cfg = null;	
		SessionFactory sf = null;
		File jarFile = null;
		
		jarFile = getDatamartJarFile(dmName);
		if(jarFile == null) return;
		
		cfg = buildEmptyConfiguration();
		configurationMap.put(dmName, cfg);
		
		if (extendClassLoader){
			updateCurrentClassLoader(jarFile);
		}	
		
		cfg.addJar(jarFile);
		
		try {
			compositeConfiguration.addJar(jarFile);
		} catch (Throwable t) {
			throw new RuntimeException("Cannot add datamart", t);
		}
		
		addViews(dmName);
		
		sf = cfg.buildSessionFactory();
		sessionFactoryMap.put(dmName, sf);		
	}
	
	/**
	 * Adds the views.
	 * 
	 * @param datamartName the datamart name
	 * 
	 * @return true, if successful
	 */
	private boolean addViews(String datamartName) {
		return addViews(datamartName, datamartName);
	}
	
	/**
	 * Adds the views.
	 * 
	 * @param datamartName the datamart name
	 * @param configurationName the configuration name
	 * 
	 * @return true, if successful
	 */
	private boolean addViews(String datamartName, String configurationName) {		
		boolean result = false;
		
		List viewNames = getViewNames(datamartName);
		if(viewNames.size() > 0) {
			for(int i = 0; i < viewNames.size(); i++) {
				String viewName = (String)viewNames.get(i);
				result = (result || addView(datamartName, viewName, configurationName));
			}
		}
		
		return result;
	}	
	
	/**
	 * Adds the view.
	 * 
	 * @param datamartName the datamart name
	 * @param viewName the view name
	 * @param configurationName the configuration name
	 * 
	 * @return true, if successful
	 */
	private boolean addView(String datamartName, String viewName, String configurationName) {
		
		boolean result = false;
		
		Configuration cfg = null;
		File viewJarFile = null;
		
		viewJarFile = getViewJarFile(datamartName,viewName);
		if(viewJarFile == null) return false;
		
		cfg = (Configuration)configurationMap.get(configurationName);
		if(cfg == null) {
			cfg = buildEmptyConfiguration();
			configurationMap.put(configurationName, cfg);
		}
		
		if (!(alreadyAddedView.contains(viewJarFile.getAbsolutePath()))){ 
			updateCurrentClassLoader(viewJarFile);
			cfg.addJar(viewJarFile);
			compositeConfiguration.addJar(viewJarFile);
			alreadyAddedView.add(viewJarFile.getAbsolutePath());
			result = true;
		}
		
		return result;
	}
	
	
	/**
	 * Adds the shared views.
	 * 
	 * @return true, if successful
	 */
	private boolean addSharedViews() {
		String sharedViewsName = "Views";
		
		Configuration cfg = null;
		SessionFactory sf = null;
		boolean configurationHasChanged = false;
		
		configurationHasChanged = addViews(getDatamartName(), sharedViewsName);
		
		if(configurationHasChanged) {
			cfg = (Configuration)configurationMap.get(sharedViewsName);		
			sf = cfg.buildSessionFactory();	
			sessionFactoryMap.put(sharedViewsName, sf);	
		}
		
		return configurationHasChanged;
	}
	
	
	/**
	 * Adds the db links.
	 */
	private void addDbLinks() {
		Configuration cfg = null;
		
		for(int i = 0; i < getDatamartNames().size(); i++) {
			String dmName = (String)getDatamartNames().get(i);
			cfg = (Configuration)configurationMap.get(dmName);
			addDbLink(dmName, cfg, compositeConfiguration);
		}
	}
	
	/*
	 private void addDbLinks() {
		Configuration cfg = null;
		
		for(int i = 0; i < getDatamartNames().size(); i++) {
			String dmName = (String)getDatamartNames().get(i);
			String dbLink = (String)dblinkMap.get(dmName);
			if(dbLink != null) {
				cfg = (Configuration)configurationMap.get(dmName);
				Iterator it = cfg.getClassMappings();
				while(it.hasNext()) {
					PersistentClass persistentClass = (PersistentClass)it.next();
					String entityName = persistentClass.getEntityName();
					persistentClass = compositeConfiguration.getClassMapping(entityName);
					Table table = persistentClass.getTable();
					table.setName(table.getName() + "@" + dbLink);
				}
			}
		}
	}
	 */
	
	
	
	
	
	
	
	/* (non-Javadoc)
	 * @see it.eng.qbe.datasource.IHibernateDataSource#refreshDatamartViews()
	 */
	public void refreshDatamartViews() {
		if(compositeConfiguration == null) {
			initHibernate();
			return;
		}
		
		boolean compositeConfigurationHasChanged = false;
		boolean datamartConfigurationHasChanged = false;
		Configuration cfg = null;
		SessionFactory sf = null;
		
		for(int i = 0; i < getDatamartNames().size(); i++) {
			String datamartName = (String)getDatamartNames().get(i);
			datamartConfigurationHasChanged = addViews(datamartName);
			if(datamartConfigurationHasChanged) {
				cfg = (Configuration)configurationMap.get(datamartName);
				sf = cfg.buildSessionFactory();
				sessionFactoryMap.put(datamartName, sf);
			}
			compositeConfigurationHasChanged = (compositeConfigurationHasChanged || datamartConfigurationHasChanged);
		}
		
		if(compositeConfigurationHasChanged) {
			
			compositeSessionFactory = compositeConfiguration.buildSessionFactory();
		}
	}
	
	
	/* (non-Javadoc)
	 * @see it.eng.qbe.datasource.IHibernateDataSource#refreshSharedViews()
	 */
	public void refreshSharedViews() {
		boolean configurationHasChanged = false;
		String sharedViewsConfiguration = "Views";
		
		Configuration cfg = null;
		SessionFactory sf = null;
		
		if(compositeConfiguration == null) {
			initHibernate();
			return;
		}
		
		configurationHasChanged = addViews(getDatamartName(), sharedViewsConfiguration);
				
		if(configurationHasChanged) {
			cfg = (Configuration)configurationMap.get(sharedViewsConfiguration);
			sf = cfg.buildSessionFactory();	
			sessionFactoryMap.put(sharedViewsConfiguration, sf);	
			compositeSessionFactory = compositeConfiguration.buildSessionFactory();
		}		
	}
	
	/* (non-Javadoc)
	 * @see it.eng.qbe.datasource.IHibernateDataSource#refreshSharedView(java.lang.String)
	 */
	public void refreshSharedView(String sharedViewName) {
		boolean configurationHasChanged = false;
		String sharedViewsConfiguration = "Views";
		
		Configuration cfg = null;
		SessionFactory sf = null;
		
		if(compositeConfiguration == null) {
			initHibernate();
			return;
		}
		
		configurationHasChanged = addView(getDatamartName(), sharedViewName, sharedViewsConfiguration);
				
		if(configurationHasChanged) {
			cfg = (Configuration)configurationMap.get(sharedViewsConfiguration);
			sf = cfg.buildSessionFactory();	
			sessionFactoryMap.put(sharedViewsConfiguration, sf);	
			compositeSessionFactory = compositeConfiguration.buildSessionFactory();
		}	
	}
	
	
	/* (non-Javadoc)
	 * @see it.eng.qbe.datasource.IHibernateDataSource#refresh()
	 */
	public void refresh() {
		compositeConfiguration = null;
		compositeSessionFactory = null;
		classLoaderExtended = false;
		alreadyAddedView = new ArrayList();	
		configurationMap = new HashMap();	
		sessionFactoryMap = new HashMap();	
	}

	

	

	

	
	
}
