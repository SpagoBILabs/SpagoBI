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

import it.eng.qbe.datasource.DBConnection;
import it.eng.qbe.datasource.configuration.CompositeDataSourceConfiguration;
import it.eng.qbe.datasource.configuration.FileDataSourceConfiguration;
import it.eng.qbe.datasource.configuration.IDataSourceConfiguration;
import it.eng.qbe.model.accessmodality.DataMartModelAccessModality;
import it.eng.spagobi.utilities.assertion.Assert;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class JPADataSource extends AbstractJPADataSource {
	
	private EntityManagerFactory factory;
	
	
	private EntityManagerFactory entityManager;
	
	private boolean classLoaderExtended = false;	
	
	
	public JPADataSource(String dataSourceName, IDataSourceConfiguration configuration) {
		setName( dataSourceName );
		dataMartModelAccessModality = new DataMartModelAccessModality();
		
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
		DBConnection connection = (DBConnection)configuration.getDataSourceProperties().get("connection");
		return connection;
	}
}
