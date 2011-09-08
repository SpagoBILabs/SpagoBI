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
package it.eng.spagobi.engines.qbe.services.worksheet;

import it.eng.qbe.datasource.ConnectionDescriptor;
import it.eng.qbe.query.ISelectField;
import it.eng.qbe.query.Query;
import it.eng.qbe.query.WhereField;
import it.eng.qbe.serializer.SerializationManager;
import it.eng.qbe.statement.IStatement;
import it.eng.spago.base.SourceBean;
import it.eng.spagobi.commons.QbeEngineStaticVariables;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.engines.qbe.FormState;
import it.eng.spagobi.engines.qbe.services.core.AbstractQbeEngineAction;
import it.eng.spagobi.engines.qbe.services.crosstab.LoadCrosstabAction;
import it.eng.spagobi.engines.qbe.utils.crosstab.CrosstabQueryCreator;
import it.eng.spagobi.engines.qbe.utils.temporarytable.TemporaryTableManager;
import it.eng.spagobi.engines.qbe.worksheet.bo.Attribute;
import it.eng.spagobi.tools.dataset.bo.JDBCDataSet;
import it.eng.spagobi.tools.dataset.common.datastore.DataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStoreMetaData;
import it.eng.spagobi.tools.dataset.common.datawriter.JSONDataWriter;
import it.eng.spagobi.tools.datasource.bo.DataSource;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceExceptionHandler;
import it.eng.spagobi.utilities.service.JSONSuccess;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 * 			Davide Zerbetto (davide.zerbetto@eng.it)
 *
 */

public class ExecuteWorksheetQueryAction extends AbstractQbeEngineAction {
	
	private static final long serialVersionUID = -9134072368475124558L;
	
	// INPUT PARAMETERS
	public static final String LIMIT = "limit";
	public static final String START = "start";
	
	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(LoadCrosstabAction.class);
    public static transient Logger auditlogger = Logger.getLogger("audit.query");
	
	public void service(SourceBean request, SourceBean response)  {				
		
		Monitor totalTimeMonitor = null;
		Monitor errorHitsMonitor = null;
		
		JSONObject gridDataFeed = null;
		IDataStore dataStore = null;
		JSONArray jsonVisibleSelectFields = null;
		
		logger.debug("IN");
		
		try {
		
			super.service(request, response);	
			
			totalTimeMonitor = MonitorFactory.start("QbeEngine.executeCrosstabQueryAction.totalTime");
			
			jsonVisibleSelectFields = getAttributeAsJSONArray( QbeEngineStaticVariables.OPTIONAL_VISIBLE_COLUMNS );
			logger.debug("jsonVisibleSelectFields input: " + jsonVisibleSelectFields);
			Assert.assertTrue(jsonVisibleSelectFields != null && jsonVisibleSelectFields.length() > 0, "jsonVisibleSelectFields input not valid");
			
			dataStore = executeQuery(jsonVisibleSelectFields);
			
			gridDataFeed = serializeDataStore(dataStore);
			
			try {
				writeBackToClient( new JSONSuccess(gridDataFeed) );
			} catch (IOException e) {
				String message = "Impossible to write back the responce to the client";
				throw new SpagoBIEngineServiceException(getActionName(), message, e);
			}
			
		} catch(Throwable t) {
			errorHitsMonitor = MonitorFactory.start("QbeEngine.errorHits");
			errorHitsMonitor.stop();
			throw SpagoBIEngineServiceExceptionHandler.getInstance().getWrappedException(getActionName(), getEngineInstance(), t);
		} finally {
			if (totalTimeMonitor != null) totalTimeMonitor.stop();
			logger.debug("OUT");
		}	
	}
	
	public JSONObject serializeDataStore(IDataStore dataStore) {
		JSONDataWriter dataSetWriter = new JSONDataWriter();
		JSONObject gridDataFeed = (JSONObject)dataSetWriter.write(dataStore);
		return gridDataFeed;
	}
	
	private String buildSqlStatement(JSONArray jsonVisibleSelectFields,
			Query query, String sqlQuery, IStatement statement) throws Exception {
		JSONObject optionalUserFilters = getAttributeAsJSONObject( QbeEngineStaticVariables.OPTIONAL_FILTERS );
		List<String> aliases = new ArrayList<String>();
		List<Attribute> onTableAttributes = new ArrayList<Attribute>();
		for (int i = 0; i < jsonVisibleSelectFields.length(); i++) {
			JSONObject jsonVisibleSelectField = jsonVisibleSelectFields.getJSONObject(i);
			Attribute attribute = (Attribute) SerializationManager.deserialize(jsonVisibleSelectField, "application/json", Attribute.class);
			aliases.add(attribute.getAlias());
			onTableAttributes.add(attribute);
		}	
		List<WhereField> whereFields = new ArrayList<WhereField>();
		String sheetName = this.getAttributeAsString(LoadWorksheetCrosstabAction.SHEET);
		whereFields.addAll(LoadWorksheetCrosstabAction.transformIntoWhereClauses(onTableAttributes));
		whereFields.addAll(LoadWorksheetCrosstabAction.getMandatoryFilters(this.getEngineInstance(), sheetName));
		whereFields.addAll(LoadWorksheetCrosstabAction.getOptionalFilters(optionalUserFilters));
		return CrosstabQueryCreator.getTableQuery(aliases, query, whereFields, sqlQuery, statement);	
	}

	protected JSONObject loadSmartFilterFormValues() throws JSONException{
		FormState formState = getEngineInstance().getFormState();
		if (formState == null) {
			return null;
		} else {
			return formState.getFormStateValues();
		}
	}
	
	protected IDataStore executeQuery(JSONArray jsonVisibleSelectFields) throws Exception {
		
		IDataStore dataStore = null;
		Query query = null;
		IStatement statement = null;
				
		JSONObject jsonFormState = null;
		
		Integer limit;
		Integer start;
		Integer maxSize = null;
		Integer resultNumber = null;
		
		start = getAttributeAsInteger( START );	
		logger.debug("Parameter [" + START + "] is equals to [" + start + "]");
		
		limit = getAttributeAsInteger( LIMIT );
		logger.debug("Parameter [" + LIMIT + "] is equals to [" + limit + "]");
		
		jsonFormState = loadSmartFilterFormValues();
		logger.debug("Form state retrieved as a string: " + jsonFormState);
		
		Assert.assertNotNull(getEngineInstance(), "It's not possible to execute " + this.getActionName() + " service before having properly created an instance of EngineInstance class");
		
		// retrieving first QbE query and setting it as active query
		query = getEngineInstance().getQueryCatalogue().getFirstQuery();
		
		//build the query filtered for the smart filter
		if (jsonFormState != null) {
			query = getFilteredQuery(query, jsonFormState);
		}
		
		getEngineInstance().setActiveQuery(query);
		
		statement = getEngineInstance().getStatment();	
		statement.setParameters( getEnv() );
		
		String sqlQuery = statement.getSqlQueryString();
		UserProfile userProfile = (UserProfile)getEnv().get(EngineConstants.ENV_USER_PROFILE);
		
		ConnectionDescriptor connection = (ConnectionDescriptor)getDataSource().getConfiguration().loadDataSourceProperties().get("connection");
		DataSource dataSource = getDataSource(connection);
		
		String sqlStatement = buildSqlStatement(jsonVisibleSelectFields, query, sqlQuery, statement);
		logger.debug("Querying temporary table: user [" + userProfile.getUserId() + "] (SQL): [" + sqlStatement + "]");
		System.out.println(sqlStatement);
		
		if (!TemporaryTableManager.isEnabled()) {
			logger.warn("TEMPORARY TABLE STRATEGY IS DISABLED!!! " +
					"Using inline view construct, therefore performance will be very low");
			int beginIndex = sqlStatement.toUpperCase().indexOf(" FROM ") + " FROM ".length(); 
			int endIndex = sqlStatement.indexOf(" ", beginIndex);
			String inlineSQLQuery = sqlStatement.substring(0, beginIndex) + " ( " + sqlQuery + " ) TEMP " + sqlStatement.substring(endIndex);
			logger.debug("Executable query for user [" + userProfile.getUserId() + "] (SQL): [" + inlineSQLQuery + "]");
			auditlogger.info("[" + userProfile.getUserId() + "]:: SQL: " + inlineSQLQuery);
			JDBCDataSet dataSet = new JDBCDataSet();
			dataSet.setDataSource(dataSource);
			dataSet.setQuery(inlineSQLQuery);
			dataSet.loadData(start, limit, -1);
			dataStore = (DataStore) dataSet.getDataStore();
		} else {
			logger.debug("Using temporary table strategy....");
	
			logger.debug("Temporary table definition for user [" + userProfile.getUserId() + "] (SQL): [" + sqlQuery + "]");
	
			auditlogger.info("Temporary table definition for user [" + userProfile.getUserId() + "]:: SQL: " + sqlQuery);
			auditlogger.info("Querying temporary table: user [" + userProfile.getUserId() + "] (SQL): [" + sqlStatement + "]");

			try {
				dataStore = TemporaryTableManager.queryTemporaryTable(userProfile, sqlStatement, sqlQuery, dataSource, start, limit);
			} catch (Exception e) {
				logger.debug("Query execution aborted because of an internal exception");
				String message = "An error occurred in " + getActionName() + " service while querying temporary table";				
				SpagoBIEngineServiceException exception = new SpagoBIEngineServiceException(getActionName(), message, e);
				exception.addHint("Check if the base query is properly formed: [" + statement.getQueryString() + "]");
				exception.addHint("Check if the crosstab's query is properly formed: [" + sqlStatement + "]");
				exception.addHint("Check connection configuration: connection's user must have DROP and CREATE privileges");
				
				throw exception;
			}
		}

		Assert.assertNotNull(dataStore, "The dataStore cannot be null");
		logger.debug("Query executed succesfully");
		
		// at this moment, the store has "col_0_..." (or something like that) as column aliases: we must put the right aliases 
		IDataStoreMetaData dataStoreMetadata = dataStore.getMetaData();
		for (int i = 0; i < jsonVisibleSelectFields.length(); i++) {
			JSONObject jsonVisibleSelectField = jsonVisibleSelectFields.getJSONObject(i);
			dataStoreMetadata.changeFieldAlias(i, jsonVisibleSelectField.getString("alias"));
		}
		
		resultNumber = (Integer)dataStore.getMetaData().getProperty("resultNumber");
		Assert.assertNotNull(resultNumber, "property [resultNumber] of the dataStore returned by queryTemporaryTable method of the class [" + TemporaryTableManager.class.getName()+ "] cannot be null");
		logger.debug("Total records: " + resultNumber);			
		
		
		boolean overflow = maxSize != null && resultNumber >= maxSize;
		if (overflow) {
			logger.warn("Query results number [" + resultNumber + "] exceeds max result limit that is [" + maxSize + "]");
			auditlogger.info("[" + userProfile.getUserId() + "]:: max result limit [" + maxSize + "] exceeded with SQL: " + sqlQuery);
		}
		
		return dataStore;
	}
	
//	/**
//	 * Gets the active query.. If no active query is specified
//	 * returns the first query in the catalogue..
//	 * The query is filtered: it applies some projection (the visible columns
//	 * are specified in the request variable jsonVisibleSelectFields) and
//	 * some selection (the rows are specified in the variable optionalUserFilters)
//	 * If the worksheet has been built with the smart filter the query must be transformed 
//	 * @return the filtered query
//	 */
//	@Override
//	public Query getQuery() {
//		JSONArray jsonVisibleSelectFields  = null;
//		JSONObject optionalUserFilters= null;
//		QbeEngineInstance engineInstance = getEngineInstance();
//		Query clonedQuery=null;
//		Query activeQuery = engineInstance.getActiveQuery();
//		if (activeQuery == null) {
//			activeQuery = engineInstance.getQueryCatalogue().getFirstQuery();
//		}
//		try {
//			if( getEngineInstance().getFormState()==null || getEngineInstance().getFormState().getFormStateValues()==null){
//				//clone the query
//				String store = ((JSONObject)SerializerFactory.getSerializer("application/json").serialize(activeQuery, getEngineInstance().getDataSource(), getLocale())).toString();
//				clonedQuery = SerializerFactory.getDeserializer("application/json").deserializeQuery(store, getEngineInstance().getDataSource());
//			}else{
//				//the builder engine is the smart filter, so the query must be transformed 
//				clonedQuery = getFilteredQuery(activeQuery,  getEngineInstance().getFormState().getFormStateValues());
//			}
//			
//			if(getEngineInstance().getActiveQuery() == null || !getEngineInstance().getActiveQuery().getId().equals(clonedQuery.getId())) {
//				logger.debug("Query with id [" + activeQuery.getId() + "] is not the current active query. A new statment will be generated");
//				getEngineInstance().setActiveQuery(clonedQuery);
//			}
//			
//			try {
//				jsonVisibleSelectFields = getAttributeAsJSONArray(QbeEngineStaticVariables.OPTIONAL_VISIBLE_COLUMNS);
//			} catch (Exception e) {
//				logger.debug("The optional attribute visibleselectfields is not valued. No visible select field selected.. All fields will be taken..");
//			}
//
//			try {
//				optionalUserFilters = getAttributeAsJSONObject( QbeEngineStaticVariables.OPTIONAL_FILTERS );
//				logger.debug("Found those optional filters "+optionalUserFilters);
//			} catch (Exception e) {
//				logger.debug("Found no optional filters");
//			}
//			
//			
//			applyFilters(clonedQuery, jsonVisibleSelectFields, optionalUserFilters);
//			return clonedQuery;
//		} catch (Exception e) {
//			activeQuery = null;
//		}
//		return activeQuery;
//	}
	
	
//	public static void applyFilters(Query query, JSONArray jsonVisibleSelectFields, JSONObject optionalUserFilters) throws JSONException{
//		List<String> visibleSelectFields = new ArrayList<String>();
//
//		if (jsonVisibleSelectFields != null) {
//			for (int i = 0; i < jsonVisibleSelectFields.length(); i++) {
//				JSONObject jsonVisibleSelectField = jsonVisibleSelectFields.getJSONObject(i);
//				visibleSelectFields.add(jsonVisibleSelectField.getString("alias"));
//			}	
//		}
//
//		if(jsonVisibleSelectFields!=null || optionalUserFilters!=null){
//							
//			//hide the fields not present in the request parameter visibleselectfields
//			if(visibleSelectFields!=null && visibleSelectFields.size()>0){
//				List<AbstractSelectField> selectedField = query.getSelectFields(true);
//				for(int i=0; i<selectedField.size(); i++){
//					String alias = selectedField.get(i).getAlias();
//					if(!visibleSelectFields.contains(alias)){
//						selectedField.get(i).setVisible(false);
//						visibleSelectFields.remove(alias);
//					}else{
//						selectedField.get(i).setVisible(true);
//					}
//				}
//			}
//
//			if(optionalUserFilters!=null){			
//				applyOptionalFilters(query, optionalUserFilters);			
//			}
//		}		
//	}
	
//	/**
//	 * Get the query and add the where fields defined in the optionalUserFilters
//	 * @param query
//	 * @param optionalUserFilters
//	 * @throws JSONException
//	 */
//	public static void applyOptionalFilters(Query query, JSONObject optionalUserFilters) throws JSONException{
//		String[] fields = JSONObject.getNames(optionalUserFilters);
//		ExpressionNode leftExpression = query.getWhereClauseStructure();
//		for(int i=0; i<fields.length; i++){
//			String fieldName = fields[i];
//			JSONArray valuesArray = optionalUserFilters.getJSONArray(fieldName);
//
//			//if the filter has some value
//			if(valuesArray.length()>0){
//
//				String[] values = new String[1];
//				values[0] =fieldName;
//
//				Operand leftOperand = new Operand(values,fieldName, AbstractStatement.OPERAND_TYPE_FIELD, values,values);
//
//				values = new String[valuesArray.length()];
//				for(int j=0; j<valuesArray.length(); j++){
//					values[j] = valuesArray.getString(j);
//				}
//
//				Operand rightOperand = new Operand(values,fieldName, AbstractStatement.OPERAND_TYPE_STATIC, values, values);
//
//				String operator = "NOT EQUALS TO";
//				if(valuesArray.length()>0){
//					operator="IN";
//				}
//
//				query.addWhereField("OptionalFilter"+i, "OptionalFilter"+i, false, leftOperand, operator, rightOperand, "AND");
//
//
//
//				ExpressionNode filterNode = new ExpressionNode("NO_NODE_OP","$F{OptionalFilter"+i+"}");
//
//				//build the where clause tree 
//				if(leftExpression==null){
//					leftExpression = filterNode;
//				}else{
//					ExpressionNode operationNode = new ExpressionNode("NODE_OP", "AND");
//					operationNode.addChild(leftExpression);
//					operationNode.addChild(filterNode);
//					leftExpression = operationNode;
//				}
//			}
//		}
//		query.setWhereClauseStructure(leftExpression);
//	}
	
//	@Override
//	protected IStatement getStatement(Query query){
//		IStatement statement =  getDataSource().createStatement( query );
//		return statement;
//	}
}
