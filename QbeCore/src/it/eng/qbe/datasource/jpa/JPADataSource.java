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

import it.eng.qbe.dao.DAOFactory;
import it.eng.qbe.datasource.DBConnection;
import it.eng.qbe.statment.IStatement;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class JPADataSource extends AbstractJPADataSource {
	/** The entity manager factory. */
	private EntityManagerFactory factory;
	
	/** The entity manager . */
	private EntityManagerFactory entityManager;
	
	/** The class loader extended. */
	private boolean classLoaderExtended = false;	
	

	
	
	/**
	 * Instantiates a new basic hibernate data source.
	 * 
	 * @param dataSourceName the data source name
	 */
	public JPADataSource(String dataSourceName) {
		setName( dataSourceName );
		setType( JPA_DS_TYPE );
	}
	
	
	public void addView(String name, IStatement statement, List columnNames,
			List columnAlias, List columnHibernateTypes) {
		// non implementare questo metodo tanto è deprecato
		
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
			initJPA();
		}
		return factory;
	}	
	
	/* (non-Javadoc)
	 * @see it.eng.qbe.datasource.jpa.IJPAataSource#getEntityManager()
	 */
	public EntityManager getEntityManager() {
		if(factory == null) {
			initJPA();
		}
		return factory.createEntityManager();
	}
	

	/**
	 * Inits the jpa.
	 */
	private void initJPA() {
		File jarFile = null;
		
		jarFile = getDatamartJarFile( getDatamartName() );
		if(jarFile == null) return;
		
		if (!classLoaderExtended){
			updateCurrentClassLoader(jarFile);
		}	
		
		factory = Persistence.createEntityManagerFactory( getDatamartName() );
		
	}
	
	
}
