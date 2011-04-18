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

import it.eng.qbe.datasource.configuration.IDataSourceConfiguration;
import it.eng.qbe.datasource.hibernate.HibernateDriver;
import it.eng.qbe.datasource.hibernate.HibernateDriverWithClassLoader;
import it.eng.qbe.datasource.jpa.JPADriver;
import it.eng.qbe.datasource.jpa.JPADriverWithClassLoader;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class DriverManager {
	private static Map<String, IDriver> drivers;
	
	static {
		drivers = new HashMap<String, IDriver>();
		drivers.put(JPADriver.DRIVER_ID, new JPADriver());
		drivers.put(HibernateDriver.DRIVER_ID, new HibernateDriver());
		drivers.put(JPADriverWithClassLoader.DRIVER_ID, new JPADriverWithClassLoader());
		drivers.put(HibernateDriverWithClassLoader.DRIVER_ID, new HibernateDriverWithClassLoader());
	}

	/**
	 * Get the datasource for the passed driver and with the passed configuration..
	 * If the datasource live in the cache it will not be rebuild
	 * @param driverName name of the driver jpa/hibernate
	 * @param configuration configuration of the datasource
	 * @return
	 */
	public static IDataSource getDataSource(String driverName, IDataSourceConfiguration configuration) {
		return getDataSource(driverName, configuration, true);
	}
	
	/**
	 * Get the datasource for the passed driver and with the passed configuration..
	 * @param driverName name of the driver jpa/hibernate
	 * @param configuration configuration of the datasource
	 * @param cache if true the datasources cache will be enabled
	 * @return
	 */
	public static IDataSource getDataSource(String driverName, IDataSourceConfiguration configuration, boolean cache) {
		
		IDataSource dataSource;
		IDriver driver;
		
		driver = drivers.get(driverName);
		if(driver == null) {
			throw new SpagoBIRuntimeException("No suitable driver for id [" + driverName + "]");
		}
		
		driver.setDataSourceCacheEnabled(cache);
		
		dataSource = driver.getDataSource(configuration);
	
		return dataSource;
	}
}
