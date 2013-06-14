/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.utilities.temporarytable;



import it.eng.spago.configuration.ConfigSingleton;
import it.eng.spagobi.tools.dataset.bo.JDBCDataSet;
import it.eng.spagobi.tools.dataset.common.datastore.DataStore;
import it.eng.spagobi.tools.dataset.persist.DataSetTableDescriptor;
import it.eng.spagobi.tools.dataset.persist.IDataSetTableDescriptor;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.StringUtils;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;
import it.eng.spagobi.utilities.sql.JDBCTypeMapper;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
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
    
	public static final String DIALECT_MYSQL = "MySQL";
	public static final String DIALECT_POSTGRES = "PostgreSQL";
	public static final String DIALECT_ORACLE = "OracleDialect";
	public static final String DIALECT_HSQL = "HSQL";
	public static final String DIALECT_ORACLE9i10g = "Oracle9Dialect";
	public static final String DIALECT_SQLSERVER = "SQLServer";
	public static final String DIALECT_DB2 = "DB2";
	public static final String DIALECT_INGRES = "Ingres";
	public static final String DIALECT_TERADATA = "Teradata";
    
    /**
     * Contains the definition of the existing temporary tables.
     * The key is created by a fixed prefix and a suffix that depends on user profile (1 temporary table for each user).
     * The value relevant to a key is the SQL statement that defines the temporary table.
     */
    private static Map<String, String> tables = new HashMap<String, String>();
    
    /**
     * Contains the descriptors of the existing temporary tables.
     */
    private static Map<String, IDataSetTableDescriptor> tableDescriptors = new HashMap<String, IDataSetTableDescriptor>();

//    public static boolean isEnabled() {
//		logger.debug("IN");
//		boolean toReturn = true;
//		String enabled = (String) ConfigSingleton.getInstance().getAttribute("QBE.QBE_TEMPORARY_TABLE.enabled");
//		logger.debug("Configured temporary table strategy enabled: " + enabled);
//		if ( enabled == null) {
//			logger.warn("Missing temporary table strategy configuration!!! Configure it into qbe.xml, example: <QBE_TEMPORARY_TABLE enabled=\"true\" />");
//			logger.debug("Default value is true");
//			enabled = "true";
//		}
//		toReturn = Boolean.parseBoolean(enabled);
//		logger.debug("OUT: returning " + toReturn);
//		return toReturn;
//    }
    
//	public static DataStore queryTemporaryTable(UserProfile userProfile, String sqlStatement, String baseQuery, IDataSource dataSource, Integer start, Integer limit)
//	 			throws Exception {
//		logger.debug("IN");
//    	Assert.assertNotNull(sqlStatement, "SQL statement cannot be null");
//    	Assert.assertNotNull(userProfile, "User profile cannot be null");
//    	Assert.assertNotNull(baseQuery, "SQL base statement cannot be null");
//    	Assert.assertNotNull(dataSource, "Data source cannot be null");
//		String tableName = getTableName(userProfile);
//		logger.debug("Table name is [" + tableName + "]");
//		
//		// drop table if not suitable according to tables map variable
//		if (tables.containsKey(tableName) && !baseQuery.equals(tables.get(tableName))) {
//			dropTableIfExists(tableName, dataSource);
//			tables.remove(tableName);
//		}
//		
//		// create table if it does not exist in tables map variable
//		if (!tables.containsKey(tableName)) {
//			dropTableIfExists(tableName, dataSource);
//			logger.debug("Table [" + tableName + "] must be created");
//			createTable(baseQuery, tableName, dataSource);
//			logger.debug("Table [" + tableName + "] created successfully");
//			tables.put(tableName, baseQuery);
//		}
//		
//		// may be the table has been dropped in the meanwhile (while the application is still alive), 
//		// without restarting the application server,
//		// so we check if it exists and in this case we re-create it...
//		if (!checkTableExistence(tableName, dataSource)) {
//			logger.debug("Table [" + tableName + "] must be created");
//			createTable(baseQuery, tableName, dataSource);
//			logger.debug("Table [" + tableName + "] created successfully");
//		}
//		
//		DataStore dataStore = queryTemporaryTable(sqlStatement, tableName, dataSource, start, limit);
//		
//		logger.debug("OUT");
//		return dataStore;
//	}
	
	public static IDataSetTableDescriptor createTable(
			List<String> fields, String sqlStatement, String tableName, IDataSource dataSource)
			throws Exception {
		
		logger.debug("IN");
		Assert.assertNotNull(sqlStatement, "SQL statement cannot be null");
		Assert.assertNotNull(tableName, "Table name cannot be null");
		Assert.assertNotNull(dataSource, "Data source cannot be null");
		logger.debug("Table name is [" + tableName + "]");
		logger.debug("SQL statement is [" + sqlStatement + "]");

		// drop table if not suitable according to tables map variable
		if (tables.containsKey(tableName)
				&& !sqlStatement.equals(tables.get(tableName))) {
			dropTableIfExists(tableName, dataSource);
			tables.remove(tableName);
		}

		// create table if it does not exist in tables map variable
		if (!tables.containsKey(tableName)) {
			dropTableIfExists(tableName, dataSource);
			logger.debug("Table [" + tableName + "] must be created");
			createTableInternal(sqlStatement, tableName, dataSource);
			logger.debug("Table [" + tableName + "] created successfully");
			setLastDataSetSignature(tableName, sqlStatement);
		}

		// may be the table has been dropped in the meanwhile (while the
		// application is still alive),
		// without restarting the application server,
		// so we check if it exists and in this case we re-create it...
		if (!checkTableExistence(tableName, dataSource)) {
			logger.debug("Table [" + tableName + "] must be created");
			createTableInternal(sqlStatement, tableName, dataSource);
			logger.debug("Table [" + tableName + "] created successfully");
		}
		
		IDataSetTableDescriptor tableDescriptor = getTableDescriptor(fields, tableName, dataSource);
		setLastDataSetTableDescriptor(tableName, tableDescriptor);

		logger.debug("OUT");
		
		return tableDescriptor;
	}
    
	public static IDataSetTableDescriptor getTableDescriptor(List<String> fields,
		String tableName, IDataSource dataSource) throws Exception {
		DataSetTableDescriptor tableDescriptor = null;
		Connection connection = null;
		ResultSet resultSet = null;
		try {
			connection = dataSource.getConnection();
			DatabaseMetaData dbMeta = connection.getMetaData();
			String driverName = connection.getMetaData().getDriverName();
			String schema = null;
			String actualTableName = null;
			if (tableName.indexOf(".") != -1) {
				schema = tableName.substring(0, tableName.indexOf("."));
				actualTableName = tableName.substring( tableName.indexOf(".") + 1 );
			} else {
				actualTableName = tableName;
			}
			logger.debug("Looking for table [" + actualTableName + "] in schema [" + schema + "] ....");
			resultSet = dbMeta.getColumns(null, schema, actualTableName, null);
			if (resultSet.next()) {
				logger.debug("Found table [" + actualTableName + "] in schema [" + schema + "].");
				tableDescriptor = new DataSetTableDescriptor();
				tableDescriptor.setTableName(getCompleteTableName(actualTableName, schema));
				readColumns(resultSet, fields, tableDescriptor, dbMeta, schema);
			} else {
				logger.debug("Table [" + actualTableName + "] in schema [" + schema + "] was not found at firts attempt.");
				logger.debug("Driver name is [" + driverName + "]");
				if (driverName.contains("HSQL") || driverName.contains("Oracle")  || driverName.contains("DB2")) {
					logger.debug("Driver name recognized as for HSQL or Oracle or DB2.");
					/*
					 * HSQL, Oracle and DB2 have this problem: when creating a table with name, for example, "TMPSBIQBE_biadmin", 
					 * it creates a table with actual name "TMPSBIQBE_BIADMIN" (all upper case) but the getColumns method 
					 * is case sensitive, therefore we try also with putting the table name upper case
					 */
					String tableNameUpperCase = actualTableName.toUpperCase();
					logger.debug("Looking for table [" + tableNameUpperCase + "] in schema [" + schema + "] ....");
					resultSet = dbMeta.getColumns(null, schema, tableNameUpperCase, null);
					//if (resultSet.first()) {
					if (resultSet.next()) {
						logger.debug("Found table [" + tableNameUpperCase + "] in schema [" + schema + "].");
						tableDescriptor = new DataSetTableDescriptor();
						tableDescriptor.setTableName(getCompleteTableName(tableNameUpperCase, schema));
						readColumns(resultSet, fields, tableDescriptor, dbMeta, schema);
					} else {
						throw new SpagoBIRuntimeException("Cannot find metadata for table [" + tableName + "]");
					}
				} else if (driverName.toLowerCase().contains("postgresql")) {
					logger.debug("Driver name recognized as for PostgreSQL.");
					/*
					 * PostgreSQL has this problem: when creating a table with name, for example, "TMPSBIQBE_biadmin", 
					 * it creates a table with actual name "tmpsbiqbe_biadmin" (all lower case) but the getColumns method 
					 * is case sensitive, therefore we try also with putting the table name lower case
					 */
					String tableNameLowerCase = actualTableName.toLowerCase();
					logger.debug("Looking for table [" + tableNameLowerCase + "] in schema [" + schema + "] ....");
					resultSet = dbMeta.getColumns(null, schema, tableNameLowerCase, null);
					if (resultSet.next()) {
						logger.debug("Found table [" + tableNameLowerCase + "] in schema [" + schema + "].");
						tableDescriptor = new DataSetTableDescriptor();
						tableDescriptor.setTableName(getCompleteTableName(tableNameLowerCase, schema));
						readColumns(resultSet, fields, tableDescriptor, dbMeta, schema);
					} else {
						throw new SpagoBIRuntimeException("Cannot find metadata for table [" + tableName + "]");
					}
				}  else {
					throw new SpagoBIRuntimeException("Cannot find metadata for table [" + tableName + "]");
				}
			}
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					throw new SpagoBIRuntimeException("Impossible to release [resultSet]", e);
				}
				logger.debug("[resultSet] released succesfully");
			}
			if (connection != null) {
				try {
					if (!connection.isClosed()) {
					    connection.close();
					}
				} catch (SQLException e) {
					throw new SpagoBIRuntimeException("Impossible to release [connection]", e);
				}
				logger.debug("[connection] released succesfully");
			}	
		}
		return tableDescriptor;
	}

	private static String getCompleteTableName(String actualTableName,
			String schema) {
		logger.debug("IN : actualTableName = [" + actualTableName + "], schema = [" + schema + "]");
		String toReturn = null;
		if (schema != null && !schema.trim().equals("")) {
			toReturn = schema + "." + actualTableName;
		} else {
			toReturn = actualTableName;
		}
		logger.debug("OUT : returning [" + toReturn + "]");
		return toReturn;
	}

	private static void readColumns(ResultSet resultSet, List<String> fields,
			DataSetTableDescriptor tableDescriptor, DatabaseMetaData dbMetadata, String schema) throws SQLException {
		int index = 0;
		do {
			// For oracle we have to check if the table exists in the right schema
			// If we set the schema name in the method dbMeta.getColumns in this way
			// dbMeta.getColumns(connection.getCatalog(), dbMeta.getUserName(), tableName, null);
			// the result contains all the tables for which the user has the select grant
			// also if they belong to other schema 
			
			// if the schema is specified in input, it is the schema to be used;
			// in case the schema is not specified, the schema name is the user name
			String actualOracleSchema = schema != null ? schema : dbMetadata.getUserName();
			
			String tableSchema = resultSet.getString("TABLE_SCHEM");
			if (dbMetadata.getDriverName().contains("Oracle")) {
				if (!tableSchema.equalsIgnoreCase(actualOracleSchema)) {
					continue;
				}
			}
			String columnName = resultSet.getString("COLUMN_NAME");
			String fieldName = null;
			if (fields != null) {
				fieldName = fields.get(index);
			} else {
				fieldName = columnName;
			}
			Class type = JDBCTypeMapper.getJavaType(resultSet.getShort("DATA_TYPE"));
			tableDescriptor.addField(fieldName, columnName, type);
			index++;
		} while (resultSet.next());// && (index<fields.size()));
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

	public static DataStore queryTemporaryTable(String sqlStatement, IDataSource dataSource, Integer start, Integer limit) throws Exception {
		
		logger.debug("IN");
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

	private static void createTableInternal(String baseQuery, String tableName, IDataSource dataSource) throws Exception {
		logger.debug("IN");
		String dialect = dataSource.getHibDialectClass().toUpperCase();
		if (dialect.contains("HSQL") || dialect.contains("SQLServer")) {
			// command in SELECT .... INTO table_name FROM ....
			String sql = "SELECT * INTO " + tableName + " FROM ( " + baseQuery + " ) T ";
			executeStatement(sql, dataSource);
		} else if (dialect.contains("Teradata")) {
			// command CREATE TABLE table_name AS ( SELECT .... ) WITH DATA
			String sql = "CREATE TABLE " + tableName + " AS ( " + baseQuery + " ) WITH DATA";
			executeStatement(sql, dataSource);
		} else if (dialect.contains("DB2")) {
			// command CREATE TABLE table_name AS ( SELECT .... ) WITH NO DATA
			String sql = "CREATE TABLE " + tableName + " AS ( " + baseQuery + " ) WITH NO DATA";
			executeStatement(sql, dataSource);
			// command INSERT INTO table_name SELECT ....
			sql = "INSERT INTO " + tableName + " " + baseQuery;
			executeStatement(sql, dataSource);
		} else {
			// command CREATE TABLE table_name AS SELECT ....
			String sql = "CREATE TABLE " + tableName + " AS " + baseQuery;
			executeStatement(sql, dataSource);
		}
		logger.debug("OUT");
	}

	public static void dropTableIfExists(String tableName, IDataSource dataSource) throws Exception {
		logger.debug("IN: dropping table " + tableName + " if exists");
		String dialect = dataSource.getHibDialectClass();
		logger.debug("Dialect : " + dialect );
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
			executeStatement("IF EXISTS (SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES " +
					" WHERE TABLE_NAME = '" + tableName + "') " +
					" DROP TABLE " + tableName, dataSource);
		} else if (dialect.contains("Teradata")) { // Teradata does not support DROP TABLE IF EXISTS command
				try {
					executeStatement("DROP TABLE " + tableName, dataSource);
				} catch (SQLException e) {
					if (e.getErrorCode() == 3807) { // Object does not exist.
						logger.debug("Table " + tableName + "does not exists.");
					} else {
						throw e;
					}
				}
		} else if (dialect.contains("DB2")) { // DB2 does not support DROP TABLE IF EXISTS command
			try {
				executeStatement("DROP TABLE " + tableName, dataSource);
			} catch (SQLException e) {
				if (e.getErrorCode() == -204) { // DB2 SQL Error: SQLCODE=-204, SQLSTATE=42704, Object not defined to DB2
					logger.debug("Table " + tableName + "does not exists.");
				} else {
					throw e;
				}
			}
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

	public static String getLastDataSetSignature(String tableName) {
		logger.debug("Returning " + tables.get(tableName));
		return tables.get(tableName);
	}
	
	public static void setLastDataSetSignature(String tableName, String signature) {
		logger.debug("Table name : [" + tableName + "], signature : [" + signature + "]");
		tables.put(tableName, signature);
	}
	
	public static void removeLastDataSetSignature(String tableName) {
		logger.debug("Removing table name : [" + tableName + "]");
		tables.remove(tableName);
	}
	
	public static String getTableName(String root) {
		logger.debug("IN");
		
		Assert.assertNotNull(root, "Root is null!!");
		
		String tableNamePrefix = getTableNamePrefix();
		String schema = null;
		int dotIndex = tableNamePrefix.indexOf(".");
		if ( dotIndex != -1 ) {
			schema = tableNamePrefix.substring(0, dotIndex);
			logger.debug("Recognized schema in table prefix : [" + schema + "]");
			tableNamePrefix = tableNamePrefix.substring( dotIndex + 1 );
			logger.debug("Actual table prefix : [" + schema + "]");
		}
		String tableNameSuffix = getTableNameSuffix();
		String cleanRoot = StringUtils.convertNonAscii(root);
		// removing non letters
	    StringBuilder sb = new StringBuilder();
	    int n = cleanRoot.length();
	    for (int i = 0; i < n; i++) {
	        char c = cleanRoot.charAt(i);
	        if (Character.isLetter(c)) {
	        	sb.append(c);
	        }
	    }
	    cleanRoot = sb.toString();
		
		/*
		// removing non-ASCII characters
		String cleanUserId = userId.replaceAll("[^\\p{ASCII}]","");
		*/
		
		logger.debug("Cleaned root : " + cleanRoot);
		String tableName = tableNamePrefix + cleanRoot + tableNameSuffix;
		// in most cases, table name length is lower than 30 characters
		if (tableName.length() > 30) {
			tableName = tableName.substring(0, 30);
		}
		if (schema != null && !schema.trim().equals("")) {
			tableName = schema + "." + tableName;
		}
		
		logger.debug("OUT: tableName = " + tableName);
		return tableName;
	}
	
	public static void setLastDataSetTableDescriptor(String tableName,
			IDataSetTableDescriptor tableDescriptor) {
		tableDescriptors.put(tableName, tableDescriptor);
	}
	
	public static void removeLastDataSetTableDescriptor(String tableName) {
		tableDescriptors.remove(tableName);
	}
	
	public static IDataSetTableDescriptor getLastDataSetTableDescriptor(String tableName) {
		return tableDescriptors.get(tableName);
	}
	
	public static String getAliasDelimiter(IDataSource dataSource) {
		String dialect = dataSource.getHibDialectClass();
		if(dialect ==null){
			dialect = dataSource.getHibDialectName();
		}
		if(dialect != null){
			if (dialect.contains(DIALECT_MYSQL)) {
				return "`";
			} else if (dialect.contains(DIALECT_HSQL)) {
				return "\"";
			} else if (dialect.contains(DIALECT_INGRES)) {
				return "\""; // TODO check it!!!!
			} else if (dialect.contains(DIALECT_ORACLE)) {
				return "\"";
			} else if (dialect.contains(DIALECT_ORACLE9i10g)) {
				return "\"";
			} else if (dialect.contains(DIALECT_POSTGRES)) {
				return "\"";
			} else if (dialect.contains(DIALECT_SQLSERVER)) {
				return "\""; // TODO not tested yet!!!!
			} else if (dialect.contains(DIALECT_DB2)) {
				return "\"";
			} else if (dialect.contains(DIALECT_TERADATA)) {
				return "\"";
			} 
		}
		return "";
	}
	
}
