/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
package it.eng.spagobi.engines.worksheet.services;

import it.eng.qbe.query.CriteriaConstants;
import it.eng.qbe.query.WhereField;
import it.eng.qbe.query.WhereField.Operand;
import it.eng.qbe.serializer.SerializationManager;
import it.eng.qbe.statement.AbstractStatement;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.engines.qbe.QbeEngineConfig;
import it.eng.spagobi.engines.worksheet.WorksheetEngineInstance;
import it.eng.spagobi.engines.worksheet.bo.Attribute;
import it.eng.spagobi.engines.worksheet.bo.Field;
import it.eng.spagobi.engines.worksheet.bo.Sheet;
import it.eng.spagobi.engines.worksheet.bo.WorkSheetDefinition;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.common.behaviour.FilteringBehaviour;
import it.eng.spagobi.tools.dataset.common.behaviour.SelectableFieldsBehaviour;
import it.eng.spagobi.tools.dataset.common.datastore.DataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.metadata.FieldMetadata;
import it.eng.spagobi.tools.dataset.common.metadata.IFieldMetaData;
import it.eng.spagobi.tools.dataset.common.metadata.IMetaData;
import it.eng.spagobi.tools.dataset.common.metadata.MetaData;
import it.eng.spagobi.tools.dataset.persist.IDataSetTableDescriptor;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.AbstractEngineAction;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceException;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;
import it.eng.spagobi.utilities.temporarytable.TemporaryTableManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.safehaus.uuid.UUIDGenerator;

/**
 * 
 * @author Davide Zerbetto (davide.zerbetto@eng.it)
 */
public abstract class AbstractWorksheetEngineAction extends AbstractEngineAction {
	
	private static final long serialVersionUID = 6446776217192515816L;
	
	/** Logger component. */
    private static transient Logger logger = Logger.getLogger(AbstractWorksheetEngineAction.class);
    public static transient Logger auditlogger = Logger.getLogger("audit.query");
    
    public IDataStore executeWorksheetQuery (String worksheetQuery, Integer start, Integer limit) {
    	
    	IDataStore dataStore = null;
    	
//		if (!TemporaryTableManager.isEnabled()) {
//			logger.warn("TEMPORARY TABLE STRATEGY IS DISABLED!!! " +
//				"Using inline view construct, therefore performance will be very low");			
//			dataStore = useInLineViewStrategy(worksheetQuery, baseQuery, start, limit);
//		} else {
//			logger.debug("Using temporary table strategy....");			
//			dataStore = useTemporaryTableStrategy(worksheetQuery, baseQuery,
//					start, limit);
//		}

		logger.debug("Using temporary table strategy....");			
		dataStore = useTemporaryTableStrategy(worksheetQuery, start, limit);
		
		Assert.assertNotNull(dataStore, "The dataStore cannot be null");
		logger.debug("Query executed succesfully");
		
		Integer resultNumber = (Integer) dataStore.getMetaData().getProperty("resultNumber");
		Assert.assertNotNull(resultNumber, "property [resultNumber] of the dataStore returned by queryTemporaryTable method of the class [" + TemporaryTableManager.class.getName()+ "] cannot be null");
		logger.debug("Total records: " + resultNumber);			
		
		UserProfile userProfile = (UserProfile)getEnv().get(EngineConstants.ENV_USER_PROFILE);
		Integer maxSize = QbeEngineConfig.getInstance().getResultLimit();
		boolean overflow = maxSize != null && resultNumber >= maxSize;
		if (overflow) {
			logger.warn("Query results number [" + resultNumber + "] exceeds max result limit that is [" + maxSize + "]");
			auditlogger.info("[" + userProfile.getUserId() + "]:: max result limit [" + maxSize + "] exceeded with SQL: " + worksheetQuery);
		}
		
		return dataStore;
    }

	private IDataStore useTemporaryTableStrategy(String worksheetQuery,
			Integer start, Integer limit) {
		
		IDataStore dataStore = null;
		
		UserProfile userProfile = (UserProfile)getEnv().get(EngineConstants.ENV_USER_PROFILE);
//		ConnectionDescriptor connection = (ConnectionDescriptor)getDataSource().getConfiguration().loadDataSourceProperties().get("connection");
//		DataSource dataSource = getDataSource(connection);
		IDataSource dataSource = getDataSource();
		
//		logger.debug("Temporary table definition for user [" + userProfile.getUserId() + "] (SQL): [" + baseQuery + "]");
		logger.debug("Querying temporary table: user [" + userProfile.getUserId() + "] (SQL): [" + worksheetQuery + "]");
		
//		auditlogger.info("Temporary table definition for user [" + userProfile.getUserId() + "]:: SQL: " + baseQuery);
		auditlogger.info("Querying temporary table: user [" + userProfile.getUserId() + "] (SQL): [" + worksheetQuery + "]");
		
		try {
			dataStore = TemporaryTableManager.queryTemporaryTable(worksheetQuery, dataSource, start, limit);
		} catch (Exception e) {
			logger.debug("Query execution aborted because of an internal exception");
			String message = "An error occurred in " + getActionName() + " service while querying temporary table";				
			SpagoBIEngineServiceException exception = new SpagoBIEngineServiceException(getActionName(), message, e);
//			exception.addHint("Check if the base query is properly formed: [" + baseQuery + "]");
			exception.addHint("Check if the crosstab's query is properly formed: [" + worksheetQuery + "]");
			exception.addHint("Check connection configuration: connection's user must have DROP and CREATE privileges");
			throw exception;
		}
		return dataStore;
	}

//	private IDataStore useInLineViewStrategy(String worksheetQuery,
//			String baseQuery, Integer start, Integer limit) {
//
//		IDataStore dataStore = null;
//		
//		UserProfile userProfile = (UserProfile)getEnv().get(EngineConstants.ENV_USER_PROFILE);
//		ConnectionDescriptor connection = (ConnectionDescriptor)getDataSource().getConfiguration().loadDataSourceProperties().get("connection");
//		DataSource dataSource = getDataSource(connection);
//		
//		int beginIndex = worksheetQuery.toUpperCase().indexOf(" FROM ") + " FROM ".length(); 
//		int endIndex = worksheetQuery.indexOf(" ", beginIndex);
//		String inlineSQLQuery = worksheetQuery.substring(0, beginIndex) + " ( " + baseQuery + " ) TEMP " + worksheetQuery.substring(endIndex);
//		logger.debug("Executable query for user [" + userProfile.getUserId() + "] (SQL): [" + inlineSQLQuery + "]");
//		auditlogger.info("[" + userProfile.getUserId() + "]:: SQL: " + inlineSQLQuery);
//		JDBCDataSet dataSet = new JDBCDataSet();
//		dataSet.setDataSource(dataSource);
//		dataSet.setQuery(inlineSQLQuery);
//		if (start != null && limit != null) {
//			dataSet.loadData(start, limit, -1);
//		} else {
//			dataSet.loadData();
//		}
//		dataStore = (DataStore) dataSet.getDataStore();
//		return dataStore;
//	}
	
    public WorksheetEngineInstance getEngineInstance() {
    	return (WorksheetEngineInstance) getAttributeFromSession( WorksheetEngineInstance.class.getName() );
    }
    
    public void setEngineInstance(WorksheetEngineInstance engineInstance) {
    	setAttributeInSession( WorksheetEngineInstance.class.getName() , engineInstance );
    }
    
	public IDataSource getDataSource() {
		WorksheetEngineInstance engineInstance  = getEngineInstance();
    	if (engineInstance == null) {
    		return null;
    	}
    	return engineInstance.getDataSource();
	}

	public void setDataSource(IDataSource dataSource) {
		WorksheetEngineInstance engineInstance  = getEngineInstance();
    	if (engineInstance == null) {
    		return;
    	}
    	engineInstance.setDataSource(dataSource);
	}
	
	public String getTemporaryTableName() {
		UserProfile userProfile = (UserProfile) getEnv().get(EngineConstants.ENV_USER_PROFILE);
		return TemporaryTableManager.getTableName(userProfile.getUserId().toString());
	}
	
	public IDataSetTableDescriptor persistDataSet() {
		
		WorksheetEngineInstance engineInstance = getEngineInstance();
		
		// get temporary table name
		String tableName = this.getTemporaryTableName();
		logger.debug("Temporary table name is [" + tableName + "]");
		
		// set all filters into dataset, because dataset's getSignature() and persist() methods may depend on them
		IDataSet dataset = engineInstance.getDataSet();
		Assert.assertNotNull(dataset, "The engine instance is missing the dataset!!");
		Map<String, List<String>> filters = getFiltersOnDomainValues();
		if (dataset.hasBehaviour(FilteringBehaviour.ID)) {
			logger.debug("Dataset has FilteringBehaviour.");
			FilteringBehaviour filteringBehaviour = (FilteringBehaviour) dataset.getBehaviour(FilteringBehaviour.ID);
			logger.debug("Setting filters on domain values : " + filters);
			filteringBehaviour.setFilters(filters);
		}
		
		if (dataset.hasBehaviour(SelectableFieldsBehaviour.ID)) {
			logger.debug("Dataset has SelectableFieldsBehaviour.");
			List<String> fields = getAllFields();
			SelectableFieldsBehaviour selectableFieldsBehaviour = (SelectableFieldsBehaviour) dataset.getBehaviour(SelectableFieldsBehaviour.ID);
			logger.debug("Setting list of fields : " + fields);
			selectableFieldsBehaviour.setSelectedFields(fields);
		}
		
		String signature = dataset.getSignature();
		logger.debug("Dataset signature : " + signature);
		if (signature.equals(TemporaryTableManager.getLastDataSetSignature(tableName))) {
			// signature matches: no need to create a TemporaryTable
			logger.debug("Signature matches: no need to create a TemporaryTable");
			return TemporaryTableManager.getLastDataSetTableDescriptor(tableName);
		}
		
		//drop the temporary table if one exists
		try {
			logger.debug("Signature does not match: dropping TemporaryTable " + tableName + " if it exists...");
			TemporaryTableManager.dropTableIfExists(tableName, getEngineInstance().getDataSource());
		} catch (Exception e) {
			logger.error("Impossible to drop the temporary table with name " + tableName, e);
			throw new SpagoBIEngineRuntimeException("Impossible to drop the temporary table with name " + tableName, e);
		}
		
		Connection connection = null;
		IDataSetTableDescriptor td = null;
		
		try {
			connection = getConnection();
			logger.debug("Cheking autocommit ...");
			try {
				if (!connection.getAutoCommit()) {
					logger.debug("Autocommit is false, setting to true ...");
					connection.setAutoCommit(true);
					logger.debug("Autocommit setted to true successfully");
				}
			} catch (SQLException e) {
				logger.error("Cannot set autocommit to true", e);
			}
			logger.debug("Persisting dataset ...");
			td = dataset.persist(tableName, connection);
			
			try {
				if (!connection.getAutoCommit() && !connection.isClosed()) {
					logger.debug("Committing changes ...");
					connection.commit();
					logger.debug("Changes committed successfully");
				}
			} catch (SQLException e) {
				logger.error("Error while committing changes", e);
				throw new SpagoBIRuntimeException("Error while committing changes", e);
			}
		} catch (Throwable t) {
			logger.error("Error while persisting dataset", t);
			throw new SpagoBIRuntimeException("Error while persisting dataset", t);
		} finally {
			if ( connection != null ) {
				try {
					if (!connection.isClosed()) {
						connection.close();
					}
				} catch (SQLException e) {
					logger.error("Error while closing connection", e);
				}
			}
		}
		
		logger.debug("Dataset persisted successfully. Table descriptor : " + td);
		TemporaryTableManager.setLastDataSetSignature(tableName, signature);
		TemporaryTableManager.setLastDataSetTableDescriptor(tableName, td);
		return td;
	}

	public Connection getConnection() {
		try {
			return this.getDataSource().getConnection();
		} catch (Exception e) {
			throw new SpagoBIEngineRuntimeException("Cannot get connection to datasource", e);
		}
	}
	
	public Map<String, List<String>> getFiltersOnDomainValues() {
		WorksheetEngineInstance engineInstance = this.getEngineInstance();
		WorkSheetDefinition workSheetDefinition = (WorkSheetDefinition) engineInstance.getAnalysisState();
		Map<String, List<String>> toReturn = workSheetDefinition.getFiltersOnDomainValues();
		return toReturn;
	}
	
	public Map<String, List<String>> getSheetFiltersOnDomainValues(String sheetName) {
		WorksheetEngineInstance engineInstance = this.getEngineInstance();
		WorkSheetDefinition workSheetDefinition = (WorkSheetDefinition) engineInstance.getAnalysisState();
		Sheet sheet = workSheetDefinition.getSheet(sheetName);
		List<Attribute> sheetFilters = sheet.getFiltersOnDomainValues();
		Map<String, List<String>> toReturn = new HashMap<String, List<String>>();
		Iterator<Attribute> it = sheetFilters.iterator();
		while (it.hasNext()) {
			Attribute attribute = it.next();
			toReturn.put(attribute.getEntityId(), attribute.getValuesAsList());
		}
		return toReturn;
	}
	
	public Map<String, List<String>> getGlobalFiltersOnDomainValues() {
		WorksheetEngineInstance engineInstance = this.getEngineInstance();
		WorkSheetDefinition workSheetDefinition = (WorkSheetDefinition) engineInstance.getAnalysisState();
		Map<String, List<String>> toReturn = workSheetDefinition.getGlobalFiltersAsMap();
		return toReturn;
	}
	
	public List<String> getAllFields() {
		WorksheetEngineInstance engineInstance = this.getEngineInstance();
		WorkSheetDefinition workSheetDefinition = (WorkSheetDefinition) engineInstance.getAnalysisState();
		List<Field> fields = workSheetDefinition.getAllFields();
		Iterator<Field> it = fields.iterator();
		List<String> toReturn = new ArrayList<String>();
		while (it.hasNext()) {
			Field field = it.next();
			toReturn.add(field.getEntityId());
		}
		return toReturn;
	}
	
	public List<WhereField> transformIntoWhereClauses(
			Map<String, List<String>> filters) throws JSONException {
		
		List<WhereField> whereFields = new ArrayList<WhereField>();
		
		Set<String> keys = filters.keySet();
		Iterator<String> it = keys.iterator();
		while (it.hasNext()) {
			String aFilterName = it.next();
			List<String> values = filters.get(aFilterName);
			if (values != null && values.size() > 0) {
				String operator = values.size() > 1 ? CriteriaConstants.IN : CriteriaConstants.EQUALS_TO;
				Operand leftOperand = new Operand(new String[] {aFilterName}, null, AbstractStatement.OPERAND_TYPE_SIMPLE_FIELD, null, null);
				String[] valuesArray = values.toArray(new String[0]);
				Operand rightOperand = new Operand(valuesArray, null, AbstractStatement.OPERAND_TYPE_STATIC, null, null);
				WhereField whereField = new WhereField(UUIDGenerator.getInstance().generateRandomBasedUUID().toString(), 
						aFilterName, false, leftOperand, operator, rightOperand, "AND");

				whereFields.add(whereField);
			}
		}
		
		return whereFields;
	}
	
	public List<WhereField> getOptionalFilters(JSONObject optionalUserFilters) throws JSONException {
		if (optionalUserFilters != null) {
			return transformIntoWhereClauses(optionalUserFilters);
		} else {
			return new ArrayList<WhereField>();
		}
	}
	
	private List<WhereField> transformIntoWhereClauses(
			JSONObject optionalUserFilters) throws JSONException {
		String[] fields = JSONObject.getNames(optionalUserFilters);
		List<WhereField> whereFields = new ArrayList<WhereField>();
		for (int i = 0; i < fields.length; i++) {
			String fieldName = fields[i];
			JSONArray valuesArray = optionalUserFilters.getJSONArray(fieldName);

			// if the filter has some value
			if (valuesArray.length() > 0) {
				String[] values = new String[1];
				values[0] = fieldName;

				Operand leftOperand = new Operand(values, fieldName,
						AbstractStatement.OPERAND_TYPE_SIMPLE_FIELD, values, values);

				values = new String[valuesArray.length()];
				for (int j = 0; j < valuesArray.length(); j++) {
					values[j] = valuesArray.getString(j);
				}

				Operand rightOperand = new Operand(values, fieldName,
						AbstractStatement.OPERAND_TYPE_STATIC, values, values);

				String operator = "EQUALS TO";
				if (valuesArray.length() > 1) {
					operator = "IN";
				}

				whereFields.add(new WhereField("OptionalFilter" + i,
						"OptionalFilter" + i, false, leftOperand, operator,
						rightOperand, "AND"));
			}
		}
		return whereFields;
	}
	
	/**
	 * Sets the worksheet definition into the worksheet engine instance
	 * @param worksheetDefinitionJSON The worksheet definition in JSON format
	 * @throws Exception
	 */
	public void updateWorksheetDefinition(JSONObject worksheetDefinitionJSON) throws Exception {

		WorkSheetDefinition workSheetDefinition = (WorkSheetDefinition) SerializationManager
				.deserialize(worksheetDefinitionJSON, "application/json",
						WorkSheetDefinition.class);

		WorksheetEngineInstance worksheetEngineInstance = getEngineInstance();
		worksheetEngineInstance.setAnalysisState(workSheetDefinition);
	}
	
	
	protected void adjustMetadata(DataStore dataStore,
			IDataSet dataset,
			IDataSetTableDescriptor descriptor) {
		
		IMetaData dataStoreMetadata = dataStore.getMetaData();
		IMetaData dataSetMetadata = dataset.getMetadata();
		MetaData newdataStoreMetadata = new MetaData();
		int fieldCount = dataStoreMetadata.getFieldCount();
		for (int i = 0; i < fieldCount; i++) {
			IFieldMetaData dataStoreFieldMetadata = dataStoreMetadata.getFieldMeta(i);
			String columnName = dataStoreFieldMetadata.getName();
			logger.debug("Column name : " + columnName);
			String fieldName = descriptor.getFieldName(columnName);
			logger.debug("Field name : " + fieldName);
			int index = dataSetMetadata.getFieldIndex(fieldName);
			logger.debug("Field index : " + index);
			IFieldMetaData dataSetFieldMetadata = dataSetMetadata.getFieldMeta(index);
			logger.debug("Field metadata : " + dataSetFieldMetadata);
			FieldMetadata newFieldMetadata = new FieldMetadata();
			newFieldMetadata.setAlias(dataSetFieldMetadata.getAlias());
			newFieldMetadata.setFieldType(dataSetFieldMetadata.getFieldType());
			newFieldMetadata.setName(dataSetFieldMetadata.getName());
			newFieldMetadata.setType(dataStoreFieldMetadata.getType());
			newdataStoreMetadata.addFiedMeta(newFieldMetadata);
		}
		newdataStoreMetadata.setProperties(dataStoreMetadata.getProperties());
		dataStore.setMetaData(newdataStoreMetadata);
	}
	

    
}