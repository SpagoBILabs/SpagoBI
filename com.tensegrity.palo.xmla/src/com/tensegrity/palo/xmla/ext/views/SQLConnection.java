/*
*
* @file SQLConnection.java
*
* Copyright (C) 2006-2009 Tensegrity Software GmbH
*
* This program is free software; you can redistribute it and/or modify it
* under the terms of the GNU General Public License (Version 2) as published
* by the Free Software Foundation at http://www.gnu.org/copyleft/gpl.html.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
* FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
* more details.
*
* You should have received a copy of the GNU General Public License along with
* this program; if not, write to the Free Software Foundation, Inc., 59 Temple
* Place, Suite 330, Boston, MA 02111-1307 USA
*
* If you are developing and distributing open source applications under the
* GPL License, then you are free to use JPalo Modules under the GPL License.  For OEMs,
* ISVs, and VARs who distribute JPalo Modules with their products, and do not license
* and distribute their source code under the GPL, Tensegrity provides a flexible
* OEM Commercial License.
*
* @author Michael Raue <Michael.Raue@tensegrity-software.com>
*
* @version $Id: SQLConnection.java,v 1.11 2009/04/29 10:35:38 PhilippBouillon Exp $
*
*/

package com.tensegrity.palo.xmla.ext.views;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class SQLConnection {
	private static final String FAV_VTABLE_NAME               = "XMLAFavorites";
	private static final String FAV_VTABLE_COL_HOST_NAME      = "host";
	private static final String FAV_VTABLE_COL_SERVICE_NAME   = "service";
	private static final String FAV_VTABLE_COL_USER_NAME      = "user";
	private static final String FAV_VTABLE_COL_VIEW_DESC_NAME = "favoriteViews";
	private static final int    FAV_VTABLE_COL_VIEW_DESC_IDX  = 4;

	private static final String VTABLE_NAME                   = "XMLAViews";	
	private static final String VTABLE_COL_HOST_NAME          = "host";
	private static final String VTABLE_COL_SERVICE_NAME       = "service";
	private static final String VTABLE_COL_USER_NAME          = "user";
	private static final String VTABLE_COL_DATABASE_ID_NAME   = "databaseId";
	private static final String VTABLE_COL_VIEW_ID_NAME       = "viewId";
	private static final String VTABLE_COL_VIEW_DESC_NAME     = "viewDesc";
	private static final int    VTABLE_COL_HOST_IDX           = 1;
	private static final int    VTABLE_COL_SERVICE_IDX        = 2;
	private static final int    VTABLE_COL_USER_IDX           = 3;
	private static final int    VTABLE_COL_DATABASE_ID_IDX    = 4;
	private static final int    VTABLE_COL_VIEW_ID_IDX        = 5;
	private static final int    VTABLE_COL_VIEW_DESC_IDX      = 6;
		
	private Connection sqlConnection;
	private boolean viewTableExists;
	private boolean favoriteViewTableExists;
	
	private static String jdbcDriverName = null;
	private static String jdbcConnectString = null;
	private static String hostName = null;
	private static String userName = null;
	private static String userPassword = null;
	
    static {
		jdbcDriverName = System.getProperty("jdbcDriverName");
		jdbcConnectString = System.getProperty("jdbcConnectString");
		hostName = System.getProperty("metaDataSqlHost");
		userName = System.getProperty("metaDataSqlUser");
		userPassword = System.getProperty("metaDataSqlPass");
    }	
    
    private final String trimHost(String host) {
		host = host.trim();
		if (host.startsWith("http://") && host.length() > 7) {
			host = host.trim().substring(7);
		}
    	return host;
    }
    
	public SQLConnection() {
//		Enumeration e = DriverManager.getDrivers();
//		while (e.hasMoreElements()) {
//			System.out.println("Available Driver: " + e.nextElement());
//		}
		if (jdbcDriverName != null) {
			initialize(jdbcDriverName);
		}
		if (jdbcConnectString != null && hostName != null && userName != null) {
			connect(jdbcConnectString, hostName, userName, userPassword);
		}									
	}
		
	private void shutdown() throws SQLException {
        if (sqlConnection == null) {
        	return;
        }
		try {
			Statement st = sqlConnection.createStatement();        
			st.execute("SHUTDOWN");
		} catch (Exception e) {
			// e.printStackTrace();
			// Ignore. In that case, SHUTDOWN is not supported and thus does
			// not need to be executed...
		}
        sqlConnection.close(); 
    }	
				
	// Connection
	
	public final boolean connect(String jdbcDriver, String host, 
		String user, String pass) {
		try {
			sqlConnection = doConnect(jdbcDriver, host, user, pass);
			if (sqlConnection != null) {
				viewTableExists = viewTableExists(sqlConnection);
				favoriteViewTableExists = favoriteViewTableExists(sqlConnection);
			}
		} catch (SQLException e) {
//			e.printStackTrace();
			return false;
		}
		return sqlConnection != null;
	}
	
	public final boolean close() {
		if (sqlConnection == null) {
			return true;
		}
		try {
			shutdown();
			sqlConnection = null;
		} catch (SQLException e) {
//			e.printStackTrace();
			return false;
		}
		return true;
	}

	// View Management
	
	public final boolean writeView(String host, String service, String user, String databaseId, 
			String viewId, String viewDescription) {
		if (sqlConnection == null) {
			return false;
		}
		if (!viewTableExists) {
			viewTableExists = createViewTable(sqlConnection);
		}
		if (!viewTableExists) {			
			return false;
		}
		host = trimHost(host);
		return createOrUpdateView(sqlConnection, host, service, user, databaseId, 
				viewId, viewDescription);
	}
	
	public final boolean writeFavoriteViews(String host, String service, String user, 
			String favoriteViews) {
		if (sqlConnection == null) {
			return false;
		}
		if (!favoriteViewTableExists) {
			favoriteViewTableExists = createFavoriteViewsTable(sqlConnection);
		}
		if (!favoriteViewTableExists) {
			return false;
		}
		host = trimHost(host);
		return createOrUpdateFavoriteView(sqlConnection, host, service, user,
				favoriteViews);
	}
	
	public final boolean deleteView(String host, String service, String user,
			String databaseId, String viewId) {
		host = trimHost(host);
		return executeQuery("DELETE FROM " + VTABLE_NAME + " WHERE " +
			VTABLE_COL_HOST_NAME        + "='" + host       + "' AND " +							
			VTABLE_COL_SERVICE_NAME     + "='" + service    + "' AND " +
			VTABLE_COL_DATABASE_ID_NAME + "='" + databaseId + "' AND " +
			VTABLE_COL_VIEW_ID_NAME     + "='" + viewId     + "'");
	}
	
	public final boolean deleteViewTable() {
		boolean result = executeQuery("DROP TABLE " + VTABLE_NAME);
		if (result) {
			viewTableExists = false;
		}
		return result;
	}
	
	public final String loadView(
			String host, String service, String user, String databaseId, String viewId) {
		if (sqlConnection == null) {
			return "";
		}
		host = trimHost(host);
		Statement statement = null;
		ResultSet results = null;
		try {
			statement = sqlConnection.createStatement();
			String command = "SELECT * FROM " + VTABLE_NAME + " WHERE " +
				VTABLE_COL_HOST_NAME        + "='" + host       + "' AND " +
				VTABLE_COL_SERVICE_NAME     + "='" + service    + "' AND " +
				VTABLE_COL_DATABASE_ID_NAME + "='" + databaseId + "' AND " +
				VTABLE_COL_VIEW_ID_NAME     + "='" + viewId     + "'";
//			System.out.println("Command == ");
//			System.out.println(command);
			results = statement.executeQuery(command);
			if (results.next()) {
				return results.getString(VTABLE_COL_VIEW_DESC_IDX);
			}
		} catch (SQLException e) {
//			e.printStackTrace();
			return "";
		} finally {
			if (statement != null) try { statement.close(); } catch (SQLException e) {}
			if (results != null) try { results.close(); } catch (SQLException e) {}
		}		
		return "";
	}
		
	public final String loadFavoriteView(String host, String service, String user) {
		if (sqlConnection == null) {
			return "";
		}
		host = trimHost(host);
		Statement statement = null;
		ResultSet results = null;
		try {
			statement = sqlConnection.createStatement();
			String command = "SELECT * FROM " + FAV_VTABLE_NAME + " WHERE " +
				FAV_VTABLE_COL_HOST_NAME  + "='" + host   + "' AND " +
				FAV_VTABLE_COL_SERVICE_NAME  + "='" + service   + "'";
			results = statement.executeQuery(command);
			if (results.next()) {
				return results.getString(FAV_VTABLE_COL_VIEW_DESC_IDX);
			}
		} catch (SQLException e) {
//			e.printStackTrace();
			return "";
		} finally {
			if (statement != null) try { statement.close(); } catch (SQLException e) {}
			if (results != null) try { results.close(); } catch (SQLException e) {}
		}		
		return "";
	}

	public final String [] getAllViewIds(String host, String service, String user) {
		host = trimHost(host);
		return getSQLResponseFor(
				"SELECT * FROM " + VTABLE_NAME + " WHERE " +
				VTABLE_COL_HOST_NAME + "='" + host + "' AND " +
				VTABLE_COL_SERVICE_NAME + "='" + service + "'", 
				VTABLE_COL_VIEW_ID_IDX);
	}
	
	public final String [] getAllViewIds(String host, String service, String user, String databaseId) {
		host = trimHost(host);
		String [] response = getSQLResponseFor(
				"SELECT * FROM " + VTABLE_NAME + " WHERE " +
				VTABLE_COL_HOST_NAME     + "='" + host   + "' AND " +
				VTABLE_COL_SERVICE_NAME     + "='" + service   + "' AND " +
				VTABLE_COL_DATABASE_ID_NAME    + "='" + databaseId + "'",
				VTABLE_COL_VIEW_ID_IDX);
		return response;
	}
	
	private final String getCubeIdFromId(String id) {
		if (id == null || id.length() == 0) {
			return "";
		}
		String [] fields = id.split("_@_");
		if (fields == null || fields.length < 6) {
			return "";
		}
		return fields[3];		
	}

	public final String [] getAllViewIds(String cubeId, String host, String service, String user, String databaseId) {
		host = trimHost(host);
		String [] response = getSQLResponseFor(
				"SELECT * FROM " + VTABLE_NAME + " WHERE " +
				VTABLE_COL_HOST_NAME     + "='" + host   + "' AND " +
				VTABLE_COL_SERVICE_NAME     + "='" + service   + "' AND " +
				VTABLE_COL_DATABASE_ID_NAME    + "='" + databaseId + "'",
				VTABLE_COL_VIEW_ID_IDX);
		ArrayList <String> allCubeIds = new ArrayList<String>();
		for (String id: response) {
			if (getCubeIdFromId(id).equals(cubeId)) {
				allCubeIds.add(id);
			}
		}
		return allCubeIds.toArray(new String[0]);
	}
	
	public final String [] getAllDatabaseIds(String host, String service, String user) {
		host = trimHost(host);
		return getSQLResponseFor("SELECT " + VTABLE_COL_DATABASE_ID_NAME + 
				" FROM " + VTABLE_NAME + " WHERE " +
				VTABLE_COL_HOST_NAME + "='" + host + "' AND " +
				VTABLE_COL_SERVICE_NAME + "='" + service + "'",
				VTABLE_COL_DATABASE_ID_IDX);
	}
				
	// Bookmark Management
	
	// SQL initialization
	
	private final boolean initialize(String jdbcDriverClass) {
		try {
			Class.forName(jdbcDriverClass);
		} catch (ClassNotFoundException e) {
			return false;
		}
		return true;
	}
	
	private final Connection doConnect(String jdbcDriver, String host,
		String user, String pass) throws SQLException {
		return DriverManager.getConnection(jdbcDriver + ":" + host, user, pass);
	}

	// View Table

	private final boolean viewTableExists(Connection con) { 		
		return executeQuery("SELECT * FROM " + VTABLE_NAME);
	}
	
	private final boolean favoriteViewTableExists(Connection con) {
		return executeQuery("SELECT * FROM " + FAV_VTABLE_NAME);
	}
	
	private final boolean createViewTable(Connection con) {
		// TODO Can view id's be identical for different servers? In that
		// case, attach server key to viewId and be done with it...
		return executeQuery("CREATE TABLE " + VTABLE_NAME + "(" +
			   VTABLE_COL_HOST_NAME   + " VARCHAR, " + 
			   VTABLE_COL_SERVICE_NAME   + " VARCHAR, " +
			   VTABLE_COL_USER_NAME   + " VARCHAR, " +
			   VTABLE_COL_DATABASE_ID_NAME  + " VARCHAR, " +
			   VTABLE_COL_VIEW_ID_NAME      + " VARCHAR(25) PRIMARY KEY, " +
			   VTABLE_COL_VIEW_DESC_NAME    + " VARCHAR)");
	}
	
	private final boolean createFavoriteViewsTable(Connection con) {
		return executeQuery("CREATE TABLE "  + FAV_VTABLE_NAME + "(" +
			   FAV_VTABLE_COL_HOST_NAME   + " VARCHAR, " +
			   FAV_VTABLE_COL_SERVICE_NAME   + " VARCHAR, " +
			   FAV_VTABLE_COL_USER_NAME   + " VARCHAR, " +
			   FAV_VTABLE_COL_VIEW_DESC_NAME + " VARCHAR, " +
			   "PRIMARY KEY (" + FAV_VTABLE_COL_HOST_NAME + ", " + FAV_VTABLE_COL_SERVICE_NAME + ", " + FAV_VTABLE_COL_USER_NAME + "))");
	}
	
	private final boolean createOrUpdateView(Connection con, 
			String host, String service, String user, String databaseId, String viewId, String viewDesc) {
		Statement statement = null;
		host = trimHost(host);
		try {
			statement = con.createStatement();
			String command = "UPDATE " + VTABLE_NAME + " SET " +
				VTABLE_COL_VIEW_DESC_NAME   + "='" + viewDesc   + "' WHERE " + 
				VTABLE_COL_HOST_NAME  + "='" + host   + "' AND " +
				VTABLE_COL_SERVICE_NAME  + "='" + service   + "' AND " +
				VTABLE_COL_USER_NAME  + "='" + user   + "' AND " +
				VTABLE_COL_DATABASE_ID_NAME + "='" + databaseId + "' AND " +
				VTABLE_COL_VIEW_ID_NAME     + "='" + viewId     + "'";
			int rowCount = statement.executeUpdate(command);
			if (rowCount == 0) {
				// viewId did not exist, so create (insert) it:
				command = "INSERT INTO " + VTABLE_NAME + "(" +
					VTABLE_COL_HOST_NAME  + ", " +
					VTABLE_COL_SERVICE_NAME + ", " +
					VTABLE_COL_USER_NAME + ", " +
					VTABLE_COL_DATABASE_ID_NAME + ", " +
					VTABLE_COL_VIEW_ID_NAME     + ", " +
					VTABLE_COL_VIEW_DESC_NAME   + ") VALUES ('" +
					host + "', '" + service + "', '" + user + "', '" + databaseId + "', '" + 
					viewId   + "', '" + viewDesc + "')";
				rowCount = statement.executeUpdate(command);
				return rowCount == 1;
			} else if (rowCount == 1) {
				return true;
			}
		} catch (SQLException e) {
//			e.printStackTrace();
		} finally {
			if (statement != null) try { statement.close(); } catch (SQLException e) {}
		}
		return false;
	}
	
	private final boolean createOrUpdateFavoriteView(Connection con, 
			String host, String service, String user, String favoriteViews) {
		Statement statement = null;
		host = trimHost(host);
		try {
			statement = con.createStatement();
			String command = "UPDATE " + FAV_VTABLE_NAME + " SET " +
				FAV_VTABLE_COL_VIEW_DESC_NAME  + "='" + favoriteViews + "' WHERE " + 
				FAV_VTABLE_COL_HOST_NAME  + "='" + host  + "' AND " +
				FAV_VTABLE_COL_SERVICE_NAME  + "='" + service  + "' AND " +
				FAV_VTABLE_COL_USER_NAME  + "='" + user  + "'";
			int rowCount = statement.executeUpdate(command);
			if (rowCount == 0) {
				// viewId did not exist, so create (insert) it:
				command = "INSERT INTO " + FAV_VTABLE_NAME + "(" +
					FAV_VTABLE_COL_HOST_NAME   + ", " +
					FAV_VTABLE_COL_SERVICE_NAME   + ", " +
					FAV_VTABLE_COL_USER_NAME   + ", " +
					FAV_VTABLE_COL_VIEW_DESC_NAME + ") VALUES ('" +
					host + "', '" + service + "', '" + user + "', '" + favoriteViews + "')";
				rowCount = statement.executeUpdate(command);
				return rowCount == 1;
			} else if (rowCount == 1) {
				return true;
			}
		} catch (SQLException e) {
//			e.printStackTrace();
		} finally {
			if (statement != null) try { statement.close(); } catch (SQLException e) {}
		}
		return false;
	}

	// Private all purpose access methods
	
	private final boolean executeQuery(String command) {
		if (sqlConnection == null) {
			return false;
		}
		Statement statement = null;
		try {
			statement = sqlConnection.createStatement();
			statement.executeQuery(command);
		} catch (SQLException e) {
//			e.printStackTrace();
			return false;
		} finally {
			if (statement != null) try { statement.close(); } catch (SQLException e) {}
		}
		return true;						
	}
	
	private final String [] getSQLResponseFor(String command, int column) {
		if (sqlConnection == null) {
			return new String[0];
		}
		Statement statement = null;
		ResultSet results = null;
		try {
			statement = sqlConnection.createStatement();
			results = statement.executeQuery(command);
			ArrayList <String> ids = new ArrayList <String> ();
			while (results.next()) {
				ids.add(results.getString(column));
			}
			return ids.toArray(new String[0]);
		} catch (SQLException e) {
//			e.printStackTrace();
			return new String[0];
		} finally {
			if (statement != null) try { statement.close(); } catch (SQLException e) {}
			if (results != null) try { results.close(); } catch (SQLException e) {}
		}				
	}
}
