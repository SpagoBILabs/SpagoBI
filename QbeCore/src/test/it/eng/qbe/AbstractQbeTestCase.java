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
package it.eng.qbe;

import java.util.List;
import java.util.Set;

import it.eng.qbe.datasource.DBConnection;
import it.eng.qbe.datasource.IDataSource;
import it.eng.qbe.model.structure.IModelEntity;
import it.eng.qbe.model.structure.IModelStructure;
import junit.framework.TestCase;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public abstract class AbstractQbeTestCase extends TestCase {
	
	protected DBConnection connection;
	protected IDataSource dataSource;
	protected ClassLoader classLoader;
	
	public static String CONNECTION_DIALECT = "org.hibernate.dialect.MySQLDialect";
	public static String CONNECTION_DRIVER = "com.mysql.jdbc.Driver";
	public static String CONNECTION_URL = "jdbc:mysql://localhost:3306/foodmart";
	public static String CONNECTION_USER = "root";
	public static String CONNECTION_PWD = "mysql";
	
	protected void setUp() throws Exception {
		super.setUp();
		
		classLoader = Thread.currentThread().getContextClassLoader();
		
		connection = new DBConnection();			
		connection.setName( "foodmart" );
		connection.setDialect(CONNECTION_DIALECT );			
		connection.setDriverClass( CONNECTION_DRIVER  );	
		connection.setUrl( CONNECTION_URL );
		connection.setUsername( CONNECTION_USER );		
		connection.setPassword( CONNECTION_PWD );
		
		setUpDataSource();
	}

	protected abstract void setUpDataSource();

	protected void tearDown() throws Exception {
		super.tearDown();
		connection = null;
		tearDownDataSource();
		
		Thread.currentThread().setContextClassLoader(classLoader);
	}
	
	protected void tearDownDataSource() {
		dataSource.close();
		dataSource  = null;
	}
	
	protected void dumpRootEntities(IModelStructure modelStructure) {
		Set<String> modelNames = modelStructure.getModelNames();
		for(String modelName : modelNames) {
			System.out.println(modelNames + " :");
			List<IModelEntity> rootEntities = modelStructure.getRootEntities(modelName);
			for(IModelEntity rootEntity : rootEntities) {
				System.out.println(" - " + rootEntity.getUniqueName());
			}
		}
	}
}
