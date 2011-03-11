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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.eng.qbe.datasource.IDataSource;
import it.eng.qbe.datasource.IDriver;
import it.eng.qbe.datasource.configuration.IDataSourceConfiguration;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class HibernateDriver implements IDriver {

	
	protected boolean dataSourceCacheEnabled; 
	protected int openedDataSource;
	protected int maxDataSource;
	
	
	public static final String DRIVER_ID = "hibernate";
	protected static final Map<String, IDataSource> cache = new HashMap<String, IDataSource>();
	
	public HibernateDriver() {
		dataSourceCacheEnabled = true;
		openedDataSource = 0;
		maxDataSource = -1;
	}
	
	public String getName() {
		return "hibernate";
	}

	public IDataSource getDataSource(String dataSourceName, IDataSourceConfiguration configuration) {
		IDataSource dataSource;

		if(maxDataSource > 0 && openedDataSource == maxDataSource) {
			throw new SpagoBIRuntimeException("Maximum  number of open data source reached");
		}
		
		dataSource = null;
		if(dataSourceCacheEnabled) {
			dataSource = cache.containsKey(dataSourceName)? 
						 cache.get(dataSourceName): 
					     new HibernateDataSource(dataSourceName, configuration);
		} else {
			dataSource = new HibernateDataSource(dataSourceName, configuration);
		}
		
		openedDataSource++;
		
		return dataSource;
	}

	public void setDataSourceCacheEnabled(boolean enabled) {
		dataSourceCacheEnabled = enabled;	
	}

	public boolean isDataSourceCacheEnabled() {
		return dataSourceCacheEnabled;
	}

	public void setMaxDataSource(int n) {
		maxDataSource = n;		
	}

	public boolean acceptDataSourceConfiguration() {
		// TODO Auto-generated method stub
		return true;
	}

}
