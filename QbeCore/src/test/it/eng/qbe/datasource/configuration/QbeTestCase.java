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
package it.eng.qbe.datasource.configuration;

import it.eng.qbe.datasource.DBConnection;
import it.eng.qbe.datasource.DriverManager;
import it.eng.qbe.datasource.IDataSource;
import it.eng.qbe.datasource.hibernate.HibernateDriver;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class QbeTestCase extends TestCase {
	
	protected IDataSource hibernateSimpleDataSource; 
	
	private static final String HIBERNATE_SIMPLE_QBE_FILE = "models/foodmart/datamart.jar";
	
	public static String CONNECTION_DIALECT = "org.hibernate.dialect.MySQLDialect";
	public static String CONNECTION_DRIVER = "com.mysql.jdbc.Driver";
	public static String CONNECTION_URL = "jdbc:mysql://localhost:3306/foodmart";
	public static String CONNECTION_USER = "root";
	public static String CONNECTION_PWD = "mysql";
	

	protected void setUp() throws Exception {
		super.setUp();
		IDataSourceConfiguration configuration;
		
		File file = new File(HIBERNATE_SIMPLE_QBE_FILE);
		
		configuration = new FileDataSourceConfiguration("foodmart", file);
		
		DBConnection connection = new DBConnection();			
		connection.setName( "foodmart" );
		connection.setDialect(CONNECTION_DIALECT );			
		connection.setDriverClass( CONNECTION_DRIVER  );	
		connection.setUrl( CONNECTION_URL );
		connection.setUsername( CONNECTION_USER );		
		connection.setPassword( CONNECTION_PWD );
		configuration.loadDataSourceProperties().put("connection", connection);
		
		hibernateSimpleDataSource = DriverManager.getDataSource(HibernateDriver.DRIVER_ID, configuration);
		/*
		Map<String, Object> dataSourceProperties = new HashMap<String, Object>();
		dataSourceProperties.put("connection", connection);
		hibernateSimpleDataSource = getDataSource("foodmart", file, dataSourceProperties);
		*/
		
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		hibernateSimpleDataSource.close();
		hibernateSimpleDataSource = null;
	}
	
	public IDataSource getDataSource(String modelName, File modelJarFile, Map<String, Object> dataSourceProperties) {
		
		IDataSource dataSource;
		
		// = getNamingStartegy().getDataSourceName(dataMartNames, connection);
		//dataSource = getDataSourceCache().getDataSource(dataSourceName);
		
		CompositeDataSourceConfiguration compositeConfiguration = new CompositeDataSourceConfiguration();
		Iterator<String> it = dataSourceProperties.keySet().iterator();
		while(it.hasNext()) {
			String propertyName = it.next();
			compositeConfiguration.loadDataSourceProperties().put(propertyName, dataSourceProperties.get(propertyName));
		}
		
		FileDataSourceConfiguration c;
		c = new FileDataSourceConfiguration(modelName, modelJarFile);
		compositeConfiguration.addSubConfiguration(c);
		
		
		dataSource = DriverManager.getDataSource("hibernate", compositeConfiguration);
		
		return dataSource;
	}
}
