/**
 * 
 */
package test;

import it.eng.spago.base.SourceBean;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.olap4j.OlapDataSource;

import com.eyeq.pivot4j.datasource.SimpleOlapDataSource;

import junit.framework.TestCase;

/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

/**
 * @author ghedin
 *
 */
public class AbstractWhatIfTestCase extends TestCase {

	protected OlapDataSource connection;
	
	public void setUp() throws Exception {
		connection = getOlapDataSource() ;
	}
	
	public OlapDataSource getOlapDataSource() {
		SourceBean sb;
		Properties connectionProps = new Properties();
				
		String usr = "root";
		String pwd = "root";
		String catalog = "D:/Sviluppo/SpagoBI/progetti/Trunk_40/runtime/resources/Olap/FoodMartMySQL.xml";
		String connectionString =  "jdbc:mondrian:Jdbc=jdbc:mysql://localhost:3306/foodmart_key";
		String driver =  "com.mysql.jdbc.Driver";

		
		connectionProps.put("JdbcUser", usr);
		connectionProps.put("JdbcPassword", pwd);
		
		connectionProps.put("Catalog",catalog);
		connectionProps.put("JdbcDrivers", driver);
		
		connectionProps.put("Provider","Mondrian");

		OlapDataSource olapDataSource = new SimpleOlapDataSource();
		((SimpleOlapDataSource)olapDataSource).setConnectionString( connectionString);

		
		((SimpleOlapDataSource)olapDataSource).setConnectionProperties(connectionProps);
		
		return olapDataSource;
	}
	
	protected void executeQuery(String sql){
		try {
			java.sql.Connection  connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/foodmart_key?user=root&password=root");
			Statement statement = connection.createStatement();
			statement.executeUpdate(sql);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
