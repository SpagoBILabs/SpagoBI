/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.engines.qbe.utils.temporarytable;

import it.eng.qbe.utility.StringUtils;
import it.eng.spago.configuration.ConfigSingleton;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.tools.dataset.bo.JDBCDataSet;
import it.eng.spagobi.tools.dataset.common.datastore.DataStore;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.assertion.Assert;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @author Zerbetto Davide (davide.zerbetto@eng.it)
 *
 */
public class TemporaryTableManager {
	
	/** Logger component. */
    private static transient Logger logger = Logger.getLogger(TemporaryTableManager.class);
    
    private static String DEFAULT_TABLE_NAME_PREFIX = "TMPSBIQBE_";
    
    /**
     * Contains the definition of the existing temporary table.
     * The key is created by a fixed prefix and a suffix that depends on user profile (1 temporary table for each user).
     * The value relevant to a key is the SQL statement that defines the temporary table.
     */
    private static Map<String, String> tables = new HashMap<String, String>();

    public static boolean isEnabled() {
		logger.debug("IN");
		boolean toReturn = true;
		String enabled = (String) ConfigSingleton.getInstance().getAttribute("QBE.QBE_TEMPORARY_TABLE.enabled");
		logger.debug("Configured temporary table strategy enabled: " + enabled);
		if ( enabled == null) {
			logger.warn("Missing temporary table strategy configuration!!! Configure it into qbe.xml, example: <QBE_TEMPORARY_TABLE enabled=\"true\" />");
			logger.debug("Default value is true");
			enabled = "true";
		}
		toReturn = Boolean.parseBoolean(enabled);
		logger.debug("OUT: returning " + toReturn);
		return toReturn;
    }
    
	public static DataStore queryTemporaryTable(UserProfile userProfile, String sqlStatement, String baseQuery, IDataSource dataSource, Integer start, Integer limit)
	 			throws Exception {
		logger.debug("IN");
    	Assert.assertNotNull(sqlStatement, "SQL statement cannot be null");
    	Assert.assertNotNull(userProfile, "User profile cannot be null");
    	Assert.assertNotNull(baseQuery, "SQL base statement cannot be null");
    	Assert.assertNotNull(dataSource, "Data source cannot be null");
		String tableName = getTableName(userProfile);
		logger.debug("Table name is [" + tableName + "]");
		
		// drop table if not suitable according to tables map variable
		if (tables.containsKey(tableName) && !baseQuery.equals(tables.get(tableName))) {
			dropTableIfExists(tableName, dataSource);
			tables.remove(tableName);
		}
		
		// create table if it does not exist in tables map variable
		if (!tables.containsKey(tableName)) {
			dropTableIfExists(tableName, dataSource);
			logger.debug("Table [" + tableName + "] must be created");
			createTable(baseQuery, tableName, dataSource);
			logger.debug("Table [" + tableName + "] created successfully");
			tables.put(tableName, baseQuery);
		}
		
		// may be the table has been dropped in the meanwhile (while the application is still alive), 
		// without restarting the application server,
		// so we check if it exists and in this case we re-create it...
		if (!checkTableExistence(tableName, dataSource)) {
			logger.debug("Table [" + tableName + "] must be created");
			createTable(baseQuery, tableName, dataSource);
			logger.debug("Table [" + tableName + "] created successfully");
		}
		
		DataStore dataStore = queryTemporaryTable(sqlStatement, tableName, dataSource, start, limit);
		
		logger.debug("OUT");
		return dataStore;
	}
	
	private static boolean checkTableExistence(String tableName,
			IDataSource dataSource) throws Exception {
		logger.debug("IN: tableName = " + tableName);
		boolean toReturn = false;
		try {
			executeStatement("select * from " + tableName + " where 1 = 0", dataSource);
			toReturn = true;
		} catch (Exception e) {
			// this should happen when table does not exist, but it's better to log the exception anyway
			logger.debug("Error while checking table [" + tableName + "] existence",  e); 
			toReturn = false;
		}
		logger.debug("OUT: returning " + toReturn);
		return toReturn;
		
		
		/*
		 * The following code does not work as expected: when creating a tables such as:
		 * CREATE TABLE test ...
		 * the actual name can be "test" or "TEST" depending on database server, but 
		 * DROP TABLE test
		 * will work anyway (it is case insensitive), instead DatabaseMetaData.getTables(null, null, tableName, null) is case sensitive!!!
		 * Therefore, if the actual table name is TEST,  DatabaseMetaData.getTables(null, null, "test", null) will no find it!!!
		 */
		/*
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			connection.setAutoCommit(false);
			DatabaseMetaData mtdt = connection.getMetaData();
			ResultSet rs = mtdt.getTables(null, null, tableName, null);  // TODO need to transform the table name into a pattern?
			toReturn = rs.first();
		} finally {
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
			logger.debug("OUT");
		}
		*/

	}

	private static DataStore queryTemporaryTable(String sqlStatement, String tableName,
			IDataSource dataSource, Integer start, Integer limit) throws Exception {
		
		logger.debug("IN");
		// injecting temporary table name into SQL statement
		int beginIndex = sqlStatement.toUpperCase().indexOf(" FROM ") + " FROM ".length(); 
		int endIndex = sqlStatement.indexOf(" ", beginIndex);
		sqlStatement = sqlStatement.substring(0, beginIndex) + tableName + sqlStatement.substring(endIndex);
		logger.debug("SQL statement is [" + sqlStatement + "]");
		JDBCDataSet dataSet = new JDBCDataSet();
		dataSet.setDataSource(dataSource);
		dataSet.setQuery(sqlStatement);
		if (start == null && limit == null) {
			dataSet.loadData();
		} else {
			dataSet.loadData(start, limit, -1);
		}
		DataStore dataStore = (DataStore) dataSet.getDataStore();
		logger.debug("Data store retrieved successfully");
		logger.debug("OUT");
		return dataStore;
		
	}

	private static void createTable(String baseQuery, String tableName,
			IDataSource dataSource) throws Exception {
		logger.debug("IN");
		String sql = null;
		String dialect = dataSource.getHibDialectName();
		if (dialect.contains("HSQL") || dialect.contains("SQLServer")) {
			// command in SELECT .... INTO table_name FROM ....
			// since QbE query cannot contains sub-queries into the SELECT clause,
			// we simply look for the first " FROM " occurrence
			int index = baseQuery.toUpperCase().indexOf(" FROM "); 
			sql = baseQuery.substring(0, index) + " INTO " + tableName + " "  + baseQuery.substring(index + 1);
		} else {
			// command CREATE TABLE table_name AS SELECT ....
			sql = "CREATE TABLE " + tableName + " AS " + baseQuery;
		}
		executeStatement(sql, dataSource);
		logger.debug("OUT");
	}

	private static void dropTableIfExists(String tableName, IDataSource dataSource) throws Exception {
		logger.debug("IN: dropping table " + tableName + " if exists");
		String dialect = dataSource.getHibDialectName();
		if (dialect.contains("Oracle")) { // ORACLE does not support DROP TABLE IF EXISTS command
			try {
				executeStatement("DROP TABLE " + tableName, dataSource);
			} catch (SQLException e) {
				if (e.getErrorCode() == 942) { // ORA-00942: table or view does not exist
					logger.debug("Table " + tableName + "does not exists.");
				} else {
					throw e;
				}
			}
		} else if (dialect.contains("SQLServer")) { // SQLServer has a different command 
			// see http://www.webdevblog.info/database/drop-table-if-exists-in-oracle-nd-sql-server/
			// TODO test it!!!
			executeStatement("IF EXISTS (SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES " +
					" WHERE TABLE_NAME = '" + tableName + "') " +
					" DROP TABLE " + tableName, dataSource);
		} else {
			executeStatement("DROP TABLE IF EXISTS " + tableName, dataSource);
		}
		logger.debug("OUT");
	}
	
	private static void executeStatement(String sql, IDataSource dataSource) throws Exception {
		logger.debug("IN");
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			connection.setAutoCommit(false);
			Statement stmt = connection.createStatement();
			logger.debug("Executing sql " + sql);
			stmt.execute(sql);
			connection.commit();
			logger.debug("Sql " + sql + " executed successfully");
		} catch (Exception e ) {
			if (connection != null) {
				connection.rollback();
			}
			throw e;
		} finally {
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
			logger.debug("OUT");
		}
	}
	
	public static String getTableNamePrefix() {
		String tableNamePrefix = (String) ConfigSingleton.getInstance().getAttribute("QBE.QBE_TEMPORARY_TABLE.prefix");
		logger.debug("Configured temporary table prefix: " + tableNamePrefix);
		if ( tableNamePrefix == null ) {
			logger.warn("Missing temporary table prefix!!! Configure it into qbe.xml, example: <QBE_TEMPORARY_TABLE prefix=\"TMPSBIQBE_\" />");
			logger.debug("Using default prefix: " + DEFAULT_TABLE_NAME_PREFIX);
			tableNamePrefix = DEFAULT_TABLE_NAME_PREFIX;
		}
		return tableNamePrefix;
	}
	
	public static String getTableNameSuffix() {
		String tableNameSuffix = (String) ConfigSingleton.getInstance().getAttribute("QBE.QBE_TEMPORARY_TABLE.suffix");
		logger.debug("Configured temporary table suffix: " + tableNameSuffix);
		if (tableNameSuffix == null) {
			tableNameSuffix = "";
		}
		return tableNameSuffix;
	}

	private static String getTableName(UserProfile userProfile) {
		logger.debug("IN");

		String tableNamePrefix = getTableNamePrefix();
		String tableNameSuffix = getTableNameSuffix();
		String userId = userProfile.getUserId().toString();
		String cleanUserId = StringUtils.convertNonAscii(userId);
		// removing non letters
	    StringBuilder sb = new StringBuilder();
	    int n = cleanUserId.length();
	    for (int i = 0; i < n; i++) {
	        char c = cleanUserId.charAt(i);
	        if (Character.isLetter(c)) {
	        	sb.append(c);
	        }
	    }
	    cleanUserId = sb.toString();
		
		/*
		// removing non-ASCII characters
		String cleanUserId = userId.replaceAll("[^\\p{ASCII}]","");
		*/
		
		logger.debug("Cleaned user id : " + cleanUserId);
		String tableName = tableNamePrefix + cleanUserId + tableNameSuffix;
		// in most cases, table name length is lower than 30 characters
		if (tableName.length() > 30) {
			tableName = tableName.substring(0, 30);
		}
		logger.debug("OUT: tableName = " + tableName);
		return tableName;
	}
	
}
