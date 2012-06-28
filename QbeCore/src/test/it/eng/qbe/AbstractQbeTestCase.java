/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.qbe;

import it.eng.qbe.datasource.ConnectionDescriptor;
import it.eng.qbe.datasource.IDataSource;
import it.eng.qbe.model.structure.IModelEntity;
import it.eng.qbe.model.structure.IModelStructure;

import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public abstract class AbstractQbeTestCase extends TestCase {
	
	protected ConnectionDescriptor connection;
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
		
		connection = new ConnectionDescriptor();			
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
