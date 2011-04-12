/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2010 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.qbe.datasource.hibernate;

import it.eng.qbe.classloader.ClassLoaderManager;
import it.eng.qbe.datasource.AbstractDataSourceWithClassLoader;
import it.eng.qbe.datasource.IDataSource;
import it.eng.qbe.datasource.configuration.FileDataSourceConfiguration;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * @author Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */

public class HibernateDataSourceWithClassLoader extends AbstractDataSourceWithClassLoader{

	public HibernateDataSourceWithClassLoader(IDataSource wrappedDataSource) {
		super(wrappedDataSource);
	}

	public synchronized void open() {
		//logger.debug("IN");
		
		HibernateDataSource wrappedHDS =  (HibernateDataSource)wrappedDataSource;
		
		try {
			if(!isOpen()) {
				wrappedHDS.compositeHibernateConfiguration = wrappedHDS.buildEmptyConfiguration();
				
				addDatamarts();
				
				if(wrappedHDS.isCompositeDataSource()) {
					wrappedHDS.addDbLinks();	
					wrappedHDS.compositeHibernateSessionFactory = wrappedHDS.compositeHibernateConfiguration.buildSessionFactory();
				} else {
					wrappedHDS.compositeHibernateSessionFactory = wrappedHDS.sessionFactoryMap.get(wrappedHDS.getSubConfigurations().get(0).getModelName());
				}
				
				wrappedHDS.classLoaderExtended = true;
			}
		} catch (Throwable t){
			throw new SpagoBIRuntimeException("Impossible to open connection", t);
		} finally {
			//logger.debug("OUT");
		}
	}
	
	private void addDatamarts() {
		HibernateDataSource wrappedHDS =  (HibernateDataSource)wrappedDataSource;
		for(int i = 0; i < wrappedHDS.getSubConfigurations().size(); i++) {
			addDatamart((FileDataSourceConfiguration)wrappedHDS.getSubConfigurations().get(i), !wrappedHDS.classLoaderExtended);		
		}	
		wrappedHDS.classLoaderExtended = true;
	}
	
	private void addDatamart(FileDataSourceConfiguration configuration, boolean extendClassLoader) {
		Configuration cfg = null;	
		SessionFactory sf = null;
		HibernateDataSource wrappedHDS =  (HibernateDataSource)wrappedDataSource;
		if(configuration.getFile() == null) return;
		
		cfg = wrappedHDS.buildEmptyConfiguration();
		wrappedHDS.configurationMap.put(configuration.getModelName(), cfg);
		
		if (extendClassLoader){
			myClassLoader = ClassLoaderManager.updateCurrentClassLoader(configuration.getFile());
		}	
		
		cfg.addJar(configuration.getFile());
		
		try {
			wrappedHDS.compositeHibernateConfiguration.addJar(configuration.getFile());
		} catch (Throwable t) {
			throw new RuntimeException("Cannot add datamart", t);
		}
		
		sf = cfg.buildSessionFactory();
		wrappedHDS.sessionFactoryMap.put(configuration.getModelName(), sf);		
	}
	
}