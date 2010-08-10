/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.tools.datasource.bo;

import it.eng.spagobi.services.datasource.bo.SpagoBiDataSource;

import org.apache.log4j.Logger;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class DataSourceFactory {
	
	private static transient Logger logger = Logger.getLogger(DataSourceFactory.class);
	
	public static IDataSource getDataSource( SpagoBiDataSource dataSourceConfig ) {
		IDataSource dataSource = null;
				
		if (dataSourceConfig == null) {
			throw new IllegalArgumentException("datasource-config parameter cannot be null");
		}
		
		dataSource = new DataSource();
		
		dataSource.setDsId(dataSourceConfig.getId());
		dataSource.setDriver( dataSourceConfig.getDriver() );
		dataSource.setJndi( dataSourceConfig.getJndiName() );
		dataSource.setLabel( dataSourceConfig.getLabel() );
		dataSource.setPwd( dataSourceConfig.getPassword() );
		dataSource.setUrlConnection( dataSourceConfig.getUrl() );
		dataSource.setUser( dataSourceConfig.getUser() );
		dataSource.setHibDialectClass( dataSourceConfig.getHibDialectClass());
		dataSource.setHibDialectName( dataSourceConfig.getHibDialectName());
		dataSource.setMultiSchema(dataSourceConfig.getMultiSchema());
		dataSource.setSchemaAttribute(dataSourceConfig.getSchemaAttribute());
		return dataSource;
	}
}
