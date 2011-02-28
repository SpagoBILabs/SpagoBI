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
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import it.eng.qbe.dao.DAOFactory;
import it.eng.qbe.datasource.DBConnection;


/**
 * The Class BasicHibernateDataSource.
 * 
 * @author Andrea Gioia
 * 
 * TODO BasicHibernateDataSource is just a particular type of composite-data source (datamartNum = 1).
 * Use only CompositeDatasource for handle both cases. Problems: the persistance of object related to datamart like
 * views that is different in the two cases.
 */
public class BasicHibernateDataSource extends AbstractHibernateDataSource  {
	
	
	/** The configuration. */
	private Configuration configuration = null;
	
	/** The session factory. */
	private SessionFactory sessionFactory = null;
	
	/** The class loader extended. */
	private boolean classLoaderExtended = false;	
	
	/** The already added view. */
	private List alreadyAddedView = null;
	
	/** Logger component. */
    private static transient Logger logger = Logger.getLogger(BasicHibernateDataSource.class);
	
	
	/**
	 * Instantiates a new basic hibernate data source.
	 * 
	 * @param dataSourceName the data source name
	 * @param datamartName the datamart name
	 * @param datamartNames the datamart names
	 * @param connection the connection
	 */
	private BasicHibernateDataSource(String dataSourceName, String datamartName, List datamartNames, DBConnection connection) {
		this(dataSourceName, datamartName, datamartNames, new HashMap(), connection);
	}
	
	/**
	 * Instantiates a new basic hibernate data source.
	 * 
	 * @param dataSourceName the data source name
	 * @param datamartName the datamart name
	 * @param datamartNames the datamart names
	 * @param dblinkMap the dblink map
	 * @param connection the connection
	 */
	private BasicHibernateDataSource(String dataSourceName, 
									String datamartName, 
									List datamartNames, 
									Map dblinkMap, 
									DBConnection connection) {
		
		setName( dataSourceName );
		setType( HIBERNATE_DS_TYPE );
		
		setDatamartName(datamartName);		
		setDatamartNames(datamartNames);
		setConnection(connection);	
		
		setProperties( DAOFactory.getDatamartPropertiesDAO().loadDatamartProperties( datamartName ) );
		
		this.alreadyAddedView = new ArrayList();		
	}
	
	/**
	 * Instantiates a new basic hibernate data source.
	 * 
	 * @param dataSourceName the data source name
	 */
	public BasicHibernateDataSource(String dataSourceName) {
		setName( dataSourceName );
		setType( HIBERNATE_DS_TYPE );
		alreadyAddedView = new ArrayList();
	}	
	
	/* (non-Javadoc)
	 * @see it.eng.qbe.datasource.IHibernateDataSource#getConfiguration()
	 */
	public Configuration getConfiguration() {
		if(configuration == null) {
			initHibernate();
		}
		return configuration;
	}
	
	/* (non-Javadoc)
	 * @see it.eng.qbe.datasource.IHibernateDataSource#getSessionFactory()
	 */
	public SessionFactory getSessionFactory() {
		if(sessionFactory == null) {
			initHibernate();
		}
		return sessionFactory;
	}	
	
	/* (non-Javadoc)
	 * @see it.eng.qbe.datasource.IHibernateDataSource#getSessionFactory(java.lang.String)
	 */
	public SessionFactory getSessionFactory(String dmName) {
		return getSessionFactory();
	}	
	
	/**
	 * Inits the hibernate.
	 */
	private void initHibernate() {
		File jarFile = null;
		
		jarFile = getDatamartJarFile( getDatamartName() );
		if(jarFile == null) return;
		
		configuration = buildEmptyConfiguration();
		
		if (!classLoaderExtended){
			updateCurrentClassLoader(jarFile);
		}	
		
		try {
			configuration.addJar(jarFile);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		
		addViews();
		
		
		sessionFactory = configuration.buildSessionFactory();
		
		/*
		try {
			sessionFactory = configuration.buildSessionFactory();
			logger.info("Hibernate session factory built succesfully");
		} catch(Throwable t) {
			logger.info("Hibernate session factory built with some errors");
		}
		*/
	}
	
	/**
	 * Adds the views.
	 * 
	 * @return true, if successful
	 */
	private boolean addViews() {		
		boolean result = false;
		
		List viewNames = getViewNames( getDatamartName() );
		if(viewNames.size() > 0) {
			for(int i = 0; i < viewNames.size(); i++) {
				String viewName = (String)viewNames.get(i);
				result = addView(viewName) && result;
			}
		}
		
		return result;
	}	
	
	/**
	 * Adds the view.
	 * 
	 * @param viewName the view name
	 * 
	 * @return true, if successful
	 */
	private boolean addView(String viewName) {
		
		boolean result = false;
		
		File viewJarFile = null;
		
		viewJarFile = getViewJarFile(getDatamartName(), viewName);
		if(viewJarFile == null) {
			return false;
		}
		
		if(configuration == null) {
			configuration = buildEmptyConfiguration();
		}		
		
		if (!(alreadyAddedView.contains(viewJarFile.getAbsolutePath()))){ 
			updateCurrentClassLoader(viewJarFile);
			configuration.addJar(viewJarFile);
			alreadyAddedView.add(viewJarFile.getAbsolutePath());
			result = true;
		}
		
		return result;
	}
	
	
	/**
	 * Adds the db links.
	 */
	private void addDbLinks() {
		addDbLink(getDatamartName(), getConfiguration(), getConfiguration());
	}	

	
	

	/* (non-Javadoc)
	 * @see it.eng.qbe.datasource.IHibernateDataSource#refresh()
	 */
	public void refresh() {
		configuration = null;
		sessionFactory = null;
		classLoaderExtended = false;
		alreadyAddedView = new ArrayList();		
	}	
	

	/**
	 * Gets the composite datamart name.
	 * 
	 * @return the composite datamart name
	 */
	public String getCompositeDatamartName() {
		return getDatamartName();
	}
	
	/**
	 * Gets the composite datamart description.
	 * 
	 * @return the composite datamart description
	 */
	public String getCompositeDatamartDescription() {
		return getDatamartName();
	}

	/* (non-Javadoc)
	 * @see it.eng.qbe.datasource.IHibernateDataSource#refreshDatamartViews()
	 */
	public void refreshDatamartViews() {
		refresh();
	}

	/* (non-Javadoc)
	 * @see it.eng.qbe.datasource.IHibernateDataSource#refreshSharedView(java.lang.String)
	 */
	public void refreshSharedView(String sharedViewName) {
		refreshDatamartViews();
	}

	/* (non-Javadoc)
	 * @see it.eng.qbe.datasource.IHibernateDataSource#refreshSharedViews()
	 */
	public void refreshSharedViews() {
		refreshDatamartViews();
	}

	
}
