/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2009 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

**/
package it.eng.qbe.datasource.jpa;

import it.eng.qbe.datasource.AbstractDataSource;
import it.eng.qbe.datasource.DBConnection;
import it.eng.qbe.datasource.configuration.CompositeDataSourceConfiguration;
import it.eng.qbe.datasource.configuration.FileDataSourceConfiguration;
import it.eng.qbe.datasource.configuration.IDataSourceConfiguration;
import it.eng.qbe.model.accessmodality.ModelAccessModality;
import it.eng.spago.base.ApplicationContainer;
import it.eng.spagobi.utilities.DynamicClassLoader;
import it.eng.spagobi.utilities.assertion.Assert;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;


/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class JPADataSource extends AbstractDataSource {
	
	private EntityManagerFactory factory;
	
	
	private EntityManagerFactory entityManager;
	
	private boolean classLoaderExtended = false;	
	
	private static transient Logger logger = Logger.getLogger(JPADataSource.class);

	protected JPADataSource(String dataSourceName, IDataSourceConfiguration configuration) {
		setName( dataSourceName );
		dataMartModelAccessModality = new ModelAccessModality();
		
		// validate and set configuration
		if(configuration instanceof FileDataSourceConfiguration){
			this.configuration = configuration;
		} else if(configuration instanceof CompositeDataSourceConfiguration){
			IDataSourceConfiguration subConf = ((CompositeDataSourceConfiguration)configuration).getSubConfigurations().get(0);
			if(subConf instanceof FileDataSourceConfiguration){
				this.configuration  = (FileDataSourceConfiguration)subConf;
			} else {
				Assert.assertUnreachable("Not suitable configuration to create a JPADataSource");
			}
		} else {
			Assert.assertUnreachable("Not suitable configuration to create a JPADataSource");
		}
	}
	
	public FileDataSourceConfiguration getFileDataSourceConfiguration() {
		return (FileDataSourceConfiguration)configuration;
	}


	public void createEntityManager(String name){
		factory = Persistence.createEntityManagerFactory(name);
		EntityManager em = factory.createEntityManager();
	}

	/* (non-Javadoc)
	 * @see it.eng.qbe.datasource.IHibernateDataSource#getSessionFactory(java.lang.String)
	 */
	public EntityManagerFactory getEntityManagerFactory(String dmName) {
		return getEntityManagerFactory();
	}	
	
	/* (non-Javadoc)
	 * @see it.eng.qbe.datasource.jpa.IJPAataSource#getEntityManagerFactory()
	 */
	public EntityManagerFactory getEntityManagerFactory() {
		if(factory == null) {
			open();
		}
		return factory;
	}	
	
	/* (non-Javadoc)
	 * @see it.eng.qbe.datasource.jpa.IJPAataSource#getEntityManager()
	 */
	public EntityManager getEntityManager() {
		if(factory == null) {
			open();
		}
		return factory.createEntityManager();
	}
	


	public void open() {
		File jarFile = null;
		
		FileDataSourceConfiguration configuration = getFileDataSourceConfiguration();
		
		jarFile = configuration.getFile();
		if(jarFile == null) return;
		
		if (!classLoaderExtended){
			updateCurrentClassLoader(jarFile);
		}	
		
		factory = Persistence.createEntityManagerFactory( getName() );
		
	}
	
	public boolean isOpen() {
		return factory != null;
	}
	
	public void close() {
		factory = null;
	}
	
	public DBConnection getConnection() {
		DBConnection connection = (DBConnection)configuration.loadDataSourceProperties().get("connection");
		return connection;
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
			logger.error("Impossible to update current class loader", e);
		}
		
		logger.debug("Jar file [" + jarFile.getName()  + "] already loaded: " + wasAlreadyLoaded);
		
		try {
			/*
			 * TEMPORARY: the next instruction forcing the loading of all classes in the path...
			 * (ie. for some qbe that have in common any classes but not all and that at the moment they aren't loaded correctly)
			 */
			//wasAlreadyLoaded = false;

			if (!wasAlreadyLoaded) {
				
				ClassLoader previous = Thread.currentThread().getContextClassLoader();
				Thread.currentThread().getContextClassLoader();
    		    DynamicClassLoader current = new DynamicClassLoader(jarFile, previous);
			    Thread.currentThread().setContextClassLoader(current);

			    //Thread.currentThread().getContextClassLoader().loadClass("it.eng.spagobi.meta.Customer");
			    
				//ClassLoader current = URLClassLoader.newInstance(new URL[]{jarFile.toURL()}, previous);				
				//Thread.currentThread().setContextClassLoader(current);
				
				if (container != null) container.setAttribute("DATAMART_CLASS_LOADER", current);
			}
			
		} catch (Exception e) {
			logger.error("Impossible to update current class loader", e);
		}
	}

}
