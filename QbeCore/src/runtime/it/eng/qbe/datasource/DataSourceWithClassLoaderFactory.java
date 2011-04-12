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
package it.eng.qbe.datasource;

import org.apache.log4j.Logger;

import it.eng.qbe.datasource.hibernate.HibernateDataSource;
import it.eng.qbe.datasource.hibernate.HibernateDataSourceWithClassLoader;
import it.eng.qbe.datasource.jpa.JPADataSource;
import it.eng.qbe.datasource.jpa.JPADataSourceWithClassLoader;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

/**
 * @author Alberto Ghedin (alberto.ghedin@eng.it)
 * Factory for the data source with a class loader encapsulated
 */

public class DataSourceWithClassLoaderFactory {

	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(DataSourceWithClassLoaderFactory.class);
	
	public static IDataSource getDataSource(IDataSource dataSource){
		if(dataSource instanceof JPADataSource){
			return new JPADataSourceWithClassLoader(dataSource);
		}
		if(dataSource instanceof HibernateDataSource){
			return new HibernateDataSourceWithClassLoader(dataSource);
		}
		logger.error("No DataSourceWithClassLoader for fata source "+dataSource);
		throw new SpagoBIRuntimeException("No DataSourceWithClassLoader for fata source "+dataSource);
	}
	
}
