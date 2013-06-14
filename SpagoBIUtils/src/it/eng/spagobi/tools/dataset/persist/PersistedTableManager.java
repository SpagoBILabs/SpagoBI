/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.tools.dataset.persist;

import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IField;
import it.eng.spagobi.tools.dataset.common.datastore.IRecord;
import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData;
import it.eng.spagobi.tools.dataset.common.metadata.IMetaData;
import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData.FieldType;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.temporarytable.TemporaryTableManager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;



/** Functions that manage the persistence of the dataset
 * 
 * @author Antonella Giachino (antonella.giachino@eng.it)
 *
 */

public class PersistedTableManager {

	private static transient Logger logger = Logger.getLogger(PersistedTableManager.class);
	public static final String DIALECT_MYSQL = "MySQL";
	public static final String DIALECT_POSTGRES = "PostgreSQL";
	public static final String DIALECT_ORACLE = "OracleDialect";
	public static final String DIALECT_HSQL = "HSQL";
	public static final String DIALECT_HSQL_PRED = "Predefined hibernate dialect";
	public static final String DIALECT_ORACLE9i10g = "Oracle9Dialect";
	public static final String DIALECT_SQLSERVER = "SQLServer";
	public static final String DIALECT_DB2 = "DB2";
	public static final String DIALECT_INGRES = "Ingres";
	public static final String DIALECT_TERADATA = "Teradata";
	
	private String tableName = new String();
	private String dialect = new String();
	private Map<String, Integer> columnSize =  new HashMap<String, Integer>();
	private IEngUserProfile profile = null;

	public PersistedTableManager(){
		
	}
	public PersistedTableManager(IEngUserProfile profile){
		this.profile = profile;
	}
	public void persistDataSet(IDataSet dataset, IDataSource dsPersist) throws Exception {
		logger.debug("IN");

		String tableName = this.getTableName();
		logger.debug("Table name set is [" + tableName + "]");
		if (tableName == null || tableName.trim().equals("")) {
			logger.debug("Table name not set. Using dataset's label ...");
			this.setTableName(dataset.getLabel());
		}
		logger.debug("Persisted table name is [" + getTableName() + "]");
		// set dialect of db
		this.setDialect(dsPersist.getHibDialectClass());
		logger.debug("DataSource target dialect is [" + getDialect() + "]");
		
		if (getDialect().contains(DIALECT_SQLSERVER) || getDialect().contains(DIALECT_DB2) ||
			getDialect().contains(DIALECT_INGRES) ||  getDialect().contains(DIALECT_TERADATA)){
			logger.debug("Persistence management isn't able for " +  getDialect() + ".");
			throw new SpagoBIServiceException("","sbi.ds.dsCannotPersistDialect");
		}
		String signature = dataset.getSignature();
		logger.debug("Dataset signature : " + signature);
		if (signature != null && signature.equals(TemporaryTableManager.getLastDataSetSignature(tableName))) {
			// signature matches: no need to create a Persistent Table
			logger.debug("Signature matches: no need to create a Persistent Table");
			return;
		}
		/*
		if (dataset.getDsType().equalsIgnoreCase("QUERY")){			
			//for dataset of query type uses a "create table as select ..." statement.
			Connection connection = null;
			try{
				connection = dsPersist.getConnection();
			} catch (Exception e) {
				logger.error("Cannot get connection to target datasource. " , e);
				throw new SpagoBIEngineRuntimeException("Cannot get connection to datasource", e);
			}						
			dataset.persist(tableName, connection);
		}else{
			//for dataset not query type uses a batch statement
			dataset.loadData();
			IDataStore datastore = dataset.getDataStore();
			perstistNoQueryDatasetType(tableName, datastore, dsPersist);
		}	*/	
		dataset.loadData();
		IDataStore datastore = dataset.getDataStore();		
		persistDataset(datastore, dsPersist);
	}
	
	private void persistDataset(IDataStore datastore, IDataSource datasource)throws Exception {
		logger.debug("IN");
		Connection connection = null;
		try{			
			
			connection = getConnection(datasource);
			connection.setAutoCommit(false);
					
			//Steps #1: define create table statement
		    String createStmtQuery = getCreateTableQuery(datastore);
		    dropTableIfExists(datasource);
		    //Step #2: execute create table statament
			executeStatement(createStmtQuery, datasource);
			//Steps #3: define prepared statement (and max column size for strings type)
			PreparedStatement statement = defineStatements(datastore, datasource, connection);	
			//Step #4: execute batch with insert statements
			statement.executeBatch();	
			statement.close();
			connection.commit();
			logger.debug("Insertion of records on persistable table executed successfully!");
		} catch (Exception e) {
			logger.error("Error persisting the dataset into table", e);
			if (connection != null) {
				connection.rollback();
			}
			throw new SpagoBIEngineRuntimeException("Error persisting the dataset into table", e);
		} finally {
		if (connection != null && !connection.isClosed()) {
			connection.close();
		}
		logger.debug("OUT");
	}
		logger.debug("OUT");
	}
	
	private PreparedStatement defineStatements(IDataStore datastore, IDataSource datasource, Connection conn){
		PreparedStatement toReturn;
		IMetaData md = datastore.getMetaData();
		int filedNo = md.getFieldCount();
		
		String query = "insert into " + getTableName() + " ( ";	
		String values = " values ( ";
		String create = "create table " + getTableName() + " (";
		
		for (int i=0, l=filedNo; i<l; i++){	
			IFieldMetaData fmd = md.getFieldMeta(i);
			query += " " + fmd.getName() + ((i<filedNo-1)?" , " : "");	
			query += ((i==filedNo-1)?" ) " : "");
			values += "?" + ((i<filedNo-1)?" , " : "");
			values += ((i==filedNo-1)?" ) " : "");	
			create +=  " " + fmd.getName() + getDBFieldType(fmd) + ((i<filedNo-1)?" , " : ")");	
		}
		String totalQuery = query + values;
		logger.debug("create table statement: " + create);
		try{
			if (getDialect().contains(DIALECT_HSQL) || getDialect().contains(DIALECT_HSQL_PRED)){
				//WORKAROUND for HQL : it needs the fisical table for define a prepareStatement.
				//So, drop and create an empty target table
				dropTableIfExists(datasource);
				//creates temporary table 
				executeStatement(create, datasource);
			}
			toReturn = conn.prepareStatement(totalQuery);
			logger.debug("Prepared statement for persist dataset as : " + totalQuery);
			for (int i=0, l=Integer.parseInt(String.valueOf(datastore.getRecordsCount())); i<l; i++){	
				IRecord rec = datastore.getRecordAt(i);			
				for (int i2=0, l2=rec.getFields().size(); i2<l2; i2++){		
					IFieldMetaData fmd = md.getFieldMeta(i2);
					IField field = rec.getFieldAt(i2);
					// in case of a measure with String type, convert it into a Double
					if (fmd.getFieldType().equals(FieldType.MEASURE) && fmd.getType().toString().contains("String")) {
						logger.debug("Column type is string but the field is measure: converting it into a double");
						// only for primitive type is necessary to use setNull method if value is null
						if (field.getValue() == null){
							toReturn.setNull(i2+1, java.sql.Types.DOUBLE);
						 }else{
					        toReturn.setDouble(i2+1, Double.parseDouble(field.getValue().toString()));
						 }
					} else if (fmd.getType().toString().contains("String")){	
						Integer lenValue = (field.getValue()==null)?new Integer("0"):new Integer(field.getValue().toString().length());
						Integer prevValue = getColumnSize().get(fmd.getName()) == null? new Integer("0"): getColumnSize().get(fmd.getName());
						if (lenValue > prevValue ){
							getColumnSize().remove(fmd.getName());
							getColumnSize().put(fmd.getName(), lenValue);
						}			
						 toReturn.setString(i2+1,  (String)field.getValue());
					}else if(fmd.getType().toString().contains("Date")) {	
						toReturn.setDate(i2+1,  (Date)field.getValue());
					}else if (fmd.getType().toString().contains("Timestamp")){
						toReturn.setTimestamp(i2+1,  (Timestamp)field.getValue());
					}else if(fmd.getType().toString().contains("Integer")) {
						//only for primitive type is necessary to use setNull method if value is null
						if (field.getValue() == null){
							toReturn.setNull(i2+1, java.sql.Types.INTEGER);
						 }else{
							 toReturn.setInt(i2+1, (Integer)field.getValue());
						 }						
					}else if(fmd.getType().toString().contains("Double")) {
						// only for primitive type is necessary to use setNull method if value is null
						if (field.getValue() == null){
							toReturn.setNull(i2+1, java.sql.Types.DOUBLE);
						 }else{
					        toReturn.setDouble(i2+1, (Double)field.getValue());
						 }
						
					}else if(fmd.getType().toString().contains("Long")) {
						// only for primitive type is necessary to use setNull method if value is null
						if (field.getValue() == null){
							toReturn.setNull(i2+1, java.sql.Types.BIGINT);
						 }else{
							toReturn.setLong(i2+1, (Long)field.getValue());
						 }						
					}else if(fmd.getType().toString().contains("Boolean")) {
						//only for primitive type is necessary to use setNull method if value is null
						if (field.getValue() == null){
							toReturn.setNull(i2+1, java.sql.Types.BOOLEAN);
						 }else{
							toReturn.setBoolean(i2+1, (Boolean)field.getValue());
						 }
						
					}else if(fmd.getType().toString().contains("BigDecimal")) {		
						toReturn.setBigDecimal(i2+1, (BigDecimal)field.getValue());
					}else if(fmd.getType().toString().contains("[B")) {  //BLOB		
						toReturn.setBytes(i2+1, (byte[])field.getValue());
						//ByteArrayInputStream bis = new ByteArrayInputStream((byte[])field.getValue());
						//toReturn.setBinaryStream(1, bis, ((byte[])field.getValue()).length);
					}else if(fmd.getType().toString().contains("[C")) {	 //CLOB							 
						toReturn.setBytes(i2+1, (byte[])field.getValue());
						//toReturn.setAsciiStream(i2+1, new ByteArrayInputStream((byte[])field.getValue()),  ((byte[])field.getValue()).length);
					}else{				
						//toReturn.setString(i2+1, (String)field.getValue());
						logger.debug("Cannot setting the column "+ fmd.getName()+ " with type "+ fmd.getType().toString());
					}
				}
				toReturn.addBatch();
			}
		} catch (Exception e) {
				logger.error("Error persisting the dataset into table", e);
				throw new SpagoBIEngineRuntimeException("Error persisting the dataset into table", e);
		}			
		return toReturn;
	} 
	
	private String getDBFieldType(IFieldMetaData fieldMetaData){
		String toReturn = "";
		String type = fieldMetaData.getType().toString();
		logger.debug("Column type input: " + type);	
		if (fieldMetaData.getFieldType().equals(FieldType.MEASURE) && type.contains("java.lang.String")) {
			logger.debug("Column type is string but the field is measure: converting it into a double");
			type = "java.lang.Double";
		}

		//if (type.equalsIgnoreCase("java.lang.String")){
		if (type.contains("java.lang.String")){
			toReturn = " VARCHAR ";
			if (getDialect().contains(DIALECT_ORACLE)) { 
				toReturn = " VARCHAR2 ";	
			}
			if (getColumnSize().get(fieldMetaData.getName()) == null){
				toReturn += " (4000)"; //maxvalue for default
			}else{
				toReturn += " (" + getColumnSize().get(fieldMetaData.getName())+ ")";
			}
		}else if (type.contains("java.lang.Integer")){
			toReturn = " INTEGER ";			
		}else if (type.contains("java.lang.Long")){
			toReturn = " NUMERIC ";	
			if (getDialect().contains(DIALECT_ORACLE)) { 
				toReturn = " NUMBER ";	
			}else if (getDialect().contains(DIALECT_MYSQL)) { 
				toReturn = " BIGINT ";	
			}	
		}else if (type.contains("java.lang.BigDecimal") || type.contains("java.math.BigDecimal")){
			toReturn = " NUMERIC ";	
			if (getDialect().contains(DIALECT_ORACLE)) { 
				toReturn = " NUMBER ";	
			}else if (getDialect().contains(DIALECT_MYSQL)) { 
				toReturn = " FLOAT ";	
			}
		}else if (type.contains("java.lang.Double")){
			toReturn = " DOUBLE ";
			if (getDialect().contains(DIALECT_POSTGRES) || getDialect().contains(DIALECT_SQLSERVER) || 
					getDialect().contains(DIALECT_TERADATA)) { 
				toReturn = " NUMERIC ";	
			}else if (getDialect().contains(DIALECT_ORACLE)) { 
				toReturn = " NUMBER ";	
			}
		}else if (type.contains("java.lang.Boolean")){
			toReturn = " BOOLEAN ";
			if (getDialect().contains(DIALECT_ORACLE) || getDialect().contains(DIALECT_TERADATA) || 
					getDialect().contains(DIALECT_DB2)) { 
				toReturn = " SMALLINT ";	
			}else if (getDialect().contains(DIALECT_SQLSERVER)) { 
				toReturn = " BIT ";	
			}
		}else if (type.contains("java.sql.Date")){
			toReturn = " DATE ";
			if (getDialect().contains(DIALECT_SQLSERVER)) { 
				toReturn = " DATETIME ";	
			}
		}else if (type.contains("java.sql.Timestamp")){
			toReturn = " TIMESTAMP ";
			if (getDialect().contains(DIALECT_SQLSERVER)) { 
				toReturn = " DATETIME ";	
			}
		}else if (type.contains("[B")){
			toReturn = " TEXT ";
			if (getDialect().contains(DIALECT_ORACLE)) { 
				toReturn = " BLOB ";	
			}else if (getDialect().contains(DIALECT_MYSQL)) { 
				toReturn = " MEDIUMBLOB ";	
			}else if (getDialect().contains(DIALECT_POSTGRES)) { 
				toReturn = " BYTEA ";	
			}else if (getDialect().contains(DIALECT_HSQL)) { 
				toReturn = " LONGVARBINARY ";	
			}
		}else if (type.contains("[C")){
			toReturn = " TEXT ";
			if (getDialect().contains(DIALECT_ORACLE)) { 
				toReturn = " CLOB ";	
			}
		}else {
			logger.debug("Cannot mapping the column type "+ type);
		}
		logger.debug("Column type output: " + toReturn);
		return toReturn;
	}
	
	private String getCreateTableQuery(IDataStore datastore){
		String toReturn = "create table " + tableName + " (" ;
		IMetaData md = datastore.getMetaData();	
		for (int i=0, l=md.getFieldCount(); i<l; i++){				
			 IFieldMetaData fmd = md.getFieldMeta(i);				 
			 toReturn += " " + fmd.getName() + getDBFieldType(fmd);	
			 toReturn += ((i<l-1)?" , " : "");	
		}
		toReturn += " )";
		
		return toReturn;
	}
		

	private Connection getConnection(IDataSource datasource) {
		try {			
			Boolean multiSchema = datasource.getMultiSchema();
			logger.debug("Datasource is multischema: " + multiSchema);
			String schema;
			if (multiSchema == null || !multiSchema.booleanValue()) {
				schema = null;
			} else {
				String attributeName = datasource.getSchemaAttribute();
				logger.debug("Datasource multischema attribute name: " + attributeName);
				
				logger.debug("Looking for attribute " + attributeName + " for user " + profile + " ...");
				Object attributeValue = profile.getUserAttribute(attributeName);
				logger.debug("Attribute " + attributeName + "  is " + attributeValue);
				if (attributeValue == null) {
					throw new RuntimeException("No attribute with name " + attributeName + " found for user " + profile.getUserUniqueIdentifier());
				} else {
					schema = attributeValue.toString();
				}
			}
			return datasource.getConnection(schema);
		} catch (Exception e) {
			throw new SpagoBIEngineRuntimeException("Cannot get connection to datasource", e);
		}
	}

	private void executeStatement(String sql, IDataSource dataSource) throws Exception {
		logger.debug("IN");
		Connection connection = null;
		try {
			//connection = dataSource.getConnection();
			connection = getConnection(dataSource);
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
	
	private void executeBatch(List queryInsert, IDataSource datasource) throws Exception {
		logger.debug("IN");
		Connection connection = null;
		try {
			//connection = datasource.getConnection();
			connection = getConnection(datasource);
			connection.setAutoCommit(false);
			Statement statement = connection.createStatement();
			for (int i=0, l=queryInsert.size(); i<l; i++) {			    
			    statement.addBatch(queryInsert.get(i).toString());
			}
			statement.executeBatch();			
			statement.close();
			connection.commit();
			logger.debug("Insertion of records on persistable table executed successfully!");
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
	
	private void dropTableIfExists(IDataSource datasource){
		//drop the persisted table if one exists
		try {
			logger.debug("Signature does not match: dropping PersistedTable " + getTableName() + " if it exists...");
			TemporaryTableManager.dropTableIfExists(getTableName(), datasource);
		} catch (Exception e) {
			logger.error("Impossible to drop the temporary table with name " + getTableName(), e);
			throw new SpagoBIEngineRuntimeException("Impossible to drop the persisted table with name " + tableName, e);
		}
	}

	
	public String getTableName() {
		return this.tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Map<String, Integer> getColumnSize() {
		return this.columnSize;
	}

	public void setColumnSize(Map<String, Integer> columnSize) {
		this.columnSize = columnSize;
	}
	public IEngUserProfile getProfile() {
		return profile;
	}
	public void setProfile(IEngUserProfile profile) {
		this.profile = profile;
	}
	public String getDialect() {
		return dialect;
	}
	public void setDialect(String dialect) {
		this.dialect = dialect;
	}
	
}
