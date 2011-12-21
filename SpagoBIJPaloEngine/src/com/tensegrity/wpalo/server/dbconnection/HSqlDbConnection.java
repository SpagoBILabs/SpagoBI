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

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.palo.viewapi.internal.DbService;



/**
 * <code>HSqlDbConnection</code>
 * TODO DOCUMENT ME
 *
 * @version $Id: HSqlDbConnection.java,v 1.5 2009/12/17 16:14:20 PhilippBouillon Exp $
 **/
public class HSqlDbConnection extends SqlDbConnection {

	
	//sole one instance:
//	private static final HSqlDbConnection instance = new HSqlDbConnection();
	public static HSqlDbConnection newInstance() {
		return new HSqlDbConnection();
	}

	
	private static final String DEFAULT_SQL_DB = "/sql/hsql";

	private HSqlDbConnection() {		
	}
	
	protected final String getSqlHomeDir() {
		return DEFAULT_SQL_DB;
	}

	protected final void initialize() {
		// nothing to do		
	}
	
	public void disconnect() {		
		try {
			shutdown();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		super.disconnect();
	}

	private void shutdown() throws SQLException {
		String shutdown = DbService.getQuery("shutdown");
		PreparedStatement stmt = null;
		try {
			try {
				stmt = connection.prepareStatement(shutdown);
				stmt.execute();
			} catch (SQLException e) {				
			}			
		} finally {
			if(stmt != null)
				stmt.close();
		}

	}
	
	
}
