/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 * 
 * LICENSE: see JPALO.LICENSE.txt file
 * 
 */

/*
 * (c) Tensegrity Software 2008
 * All rights reserved
 */
package com.tensegrity.wpalo.server.dbconnection;

import java.sql.SQLException;
import java.sql.Statement;
/**
 * <code>MySqlDbConnection</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: MySqlDbConnection.java,v 1.3 2009/12/17 16:14:20 PhilippBouillon Exp $
 **/
public class MySqlDbConnection extends SqlDbConnection {
	
	//only one instance:
//	private static final MySqlDbConnection instance = new MySqlDbConnection();
	public static MySqlDbConnection newInstance() {
		return new MySqlDbConnection();
	}
	
	
	private static final String DEFAULT_SQL_DB = "/sql/mysql";

	private MySqlDbConnection() {		
	}
	
	protected final String getSqlHomeDir() {
		return DEFAULT_SQL_DB;
	}
	
	protected final void initialize() {
		// do some mysql specific initialisation stuff...
		String dbName = credentials.getProperty("databaseName");
		if (connection != null && dbName != null) {
			Statement statement = null;
			try {
				statement = connection.createStatement();
				statement
						.execute(buildQuery(commands, "createDatabase", dbName));
				statement = connection.createStatement();
				statement.execute(buildQuery(commands, "useDatabase", dbName));
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				if (statement != null)
					try {
						statement.close();
					} catch (SQLException e) {
					}
			}
		}
	}
}
