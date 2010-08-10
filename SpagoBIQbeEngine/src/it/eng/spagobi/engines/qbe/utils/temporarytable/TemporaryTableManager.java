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

import it.eng.spago.configuration.ConfigSingleton;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.tools.dataset.bo.JDBCStandardDataSet;
import it.eng.spagobi.tools.dataset.common.datastore.DataStore;
import it.eng.spagobi.tools.datasource.bo.DataSource;
import it.eng.spagobi.utilities.assertion.Assert;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import sun.text.Normalizer;

/**
 * @author Zerbetto Davide (davide.zerbetto@eng.it)
 *
 */
public class TemporaryTableManager {
	
	/** Logger component. */
    private static transient Logger logger = Logger.getLogger(TemporaryTableManager.class);
    
    private static String DEFAULT_TABLE_NAME_PREFIX = "SBIQBETMP_";
    
    /**
     * Contains the definition of the existing temporary table.
     * The key is created by a fixed prefix and a suffix that depends on user profile (1 temporary table for each user).
     * The value relevant to a key is the SQL statement that defines the temporary table.
     */
    private static Map<String, String> tables = new HashMap<String, String>();

	public static DataStore queryTemporaryTable(UserProfile userProfile, String sqlStatement, String baseQuery, DataSource dataSource)
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
			if (checkTableExistence(tableName, dataSource)) {
				logger.debug("Table [" + tableName + "] must be dropped");
				dropTable(tableName, dataSource);
				logger.debug("Table [" + tableName + "] dropped successfully");
			} else {
				logger.debug("Table [" + tableName + "] does not exist");
			}
			tables.remove(tableName);
		}
		
		// create table if it does not exist in tables map variable
		if (!tables.containsKey(tableName)) {
			if (checkTableExistence(tableName, dataSource)) {
				logger.debug("Table [" + tableName + "] must be dropped");
				dropTable(tableName, dataSource);
				logger.debug("Table [" + tableName + "] dropped successfully");
			}
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
		
		DataStore dataStore = queryTemporaryTable(sqlStatement, tableName, dataSource);
		
		logger.debug("OUT");
		return dataStore;
	}
	
	private static boolean checkTableExistence(String tableName,
			DataSource dataSource) throws Exception {
		logger.debug("IN");
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
			connection.setAutoCommit(false);
			DatabaseMetaData mtdt = connection.getMetaData();
			ResultSet rs = mtdt.getTables(null, null, tableName, null);  // TODO need to transform the table name into a pattern?
			return rs.first();
		} finally {
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
			logger.debug("OUT");
		}
		/*
		try {
			executeStatement("select * from " + tableName + " where 1 = 0", dataSource);
			return true;
		} catch (Exception e) {
			// this should happen when table does not exist, but it's better to log the exception anyway
			logger.debug("Error while checking table [" + tableName + "] existence",  e); 
			return false;
		}
		*/
	}

	private static DataStore queryTemporaryTable(String sqlStatement, String tableName,
			DataSource dataSource) throws Exception {
		
		logger.debug("IN");
		// injecting temporary table name into SQL statement
		int beginIndex = sqlStatement.toUpperCase().indexOf(" FROM ") + " FROM ".length(); 
		int endIndex = sqlStatement.indexOf(" ", beginIndex);
		sqlStatement = sqlStatement.substring(0, beginIndex) + tableName + sqlStatement.substring(endIndex);
		logger.debug("SQL statement is [" + sqlStatement + "]");
		JDBCStandardDataSet dataSet = new JDBCStandardDataSet();
		dataSet.setDataSource(dataSource);
		dataSet.setQuery(sqlStatement);
		dataSet.loadData();
		DataStore dataStore = (DataStore) dataSet.getDataStore();
		logger.debug("Data store retrieved successfully");
		logger.debug("OUT");
		return dataStore;
		
	}

	private static void createTable(String baseQuery, String tableName,
			DataSource dataSource) throws Exception {
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

	private static void dropTable(String tableName, DataSource dataSource) throws Exception {
		logger.debug("IN");
		executeStatement("DROP TABLE " + tableName, dataSource);
		logger.debug("OUT");
	}
	
	private static void executeStatement(String sql, DataSource dataSource) throws Exception {
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

	private static String getTableName(UserProfile userProfile) {
		logger.debug("IN");
		String tableNamePrefix = (String) ConfigSingleton.getInstance().getAttribute("QBE.QBE_TEMPORARY_TABLE.prefix");
		logger.debug("Configured temporary table prefix: " + tableNamePrefix);
		String tableNameSuffix = (String) ConfigSingleton.getInstance().getAttribute("QBE.QBE_TEMPORARY_TABLE.suffix");
		logger.debug("Configured temporary table suffix: " + tableNameSuffix);
		if ( tableNamePrefix == null ) {
			logger.warn("Missing temporary table prefix!!! Configure it into qbe.xml, example: <QBE_TEMPORARY_TABLE prefix=\"SBI_QBE_TEMP_\" />");
			logger.debug("Using default prefix: " + DEFAULT_TABLE_NAME_PREFIX);
			tableNamePrefix = DEFAULT_TABLE_NAME_PREFIX;
		}
		if (tableNameSuffix == null) {
			tableNameSuffix = "";
		}
		String userId = userProfile.getUserId().toString();
		// removes accented letters and replace them by their regular ASCII equivalent.
		//String cleanUserId = Normalizer.normalize(userId, Normalizer, 0);
		// removing non-ASCII characters
		String cleanUserId = userId.replaceAll("[^\\p{ASCII}]","");
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
