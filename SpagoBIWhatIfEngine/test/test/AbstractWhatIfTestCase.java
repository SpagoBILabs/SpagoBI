/**
 * 
 */
package test;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.engines.whatif.WhatIfEngineInstance;
import it.eng.spagobi.utilities.engines.EngineConstants;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import junit.framework.TestCase;

import org.olap4j.OlapDataSource;

import com.eyeq.pivot4j.datasource.SimpleOlapDataSource;

/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

/**
 * @author ghedin
 *
 */
public class AbstractWhatIfTestCase extends TestCase {

	
	private static final String mdx = ( "SELECT {[Measures].[Store Sales]} ON COLUMNS, {[Product].[Food]} ON ROWS FROM [Sales_V] WHERE [Version].[1]");
	

	
	public OlapDataSource getOlapDataSource() {
		SourceBean sb;
		Properties connectionProps = new Properties();
				
		String usr = "root";
		String pwd = "root";
		String catalog = "D:/Sviluppo/SpagoBI/progetti/Trunk_40/runtime/resources/Olap/FoodMartMySQLTest.xml";
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
	
	public WhatIfEngineInstance getWhatifengineiEngineInstance(it.eng.spagobi.tools.datasource.bo.DataSource ds, String c){
		return  new WhatIfEngineInstance(getEnv( ds, c) );
	}
	
	public Map getEnv(it.eng.spagobi.tools.datasource.bo.DataSource ds, String catalog) {
		

		
		
		Map env = new HashMap();
		
		
		env.put(EngineConstants.ENV_DATASOURCE, ds);
		env.put(EngineConstants.ENV_LOCALE, Locale.ITALIAN);
		env.put(EngineConstants.ENV_OLAP_SCHEMA, catalog);
		env.put("ENV_INITIAL_MDX_QUERY", mdx);

		return env;
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
	
	
	public String getMdx(){
		return mdx;
	}
	
}
