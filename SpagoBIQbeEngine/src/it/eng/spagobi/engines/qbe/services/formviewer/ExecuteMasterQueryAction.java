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
package it.eng.spagobi.engines.qbe.services.formviewer;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import it.eng.qbe.commons.serializer.SerializerFactory;
import it.eng.qbe.datasource.hibernate.DBConnection;
import it.eng.qbe.datasource.hibernate.IHibernateDataSource;
import it.eng.qbe.query.DataMartSelectField;
import it.eng.qbe.query.HavingField;
import it.eng.qbe.query.Query;
import it.eng.qbe.query.WhereField;
import it.eng.qbe.statment.IStatement;
import it.eng.qbe.statment.hibernate.HQLStatement;
import it.eng.spago.base.SourceBean;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.engines.qbe.QbeEngineConfig;
import it.eng.spagobi.engines.qbe.services.core.AbstractQbeEngineAction;
import it.eng.spagobi.tools.dataset.bo.JDBCStandardDataSet;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datawriter.JSONDataWriter;
import it.eng.spagobi.tools.dataset.common.query.AggregationFunctions;
import it.eng.spagobi.tools.dataset.common.query.GroupByQueryTransformer;
import it.eng.spagobi.tools.dataset.common.query.IAggregationFunction;
import it.eng.spagobi.tools.datasource.bo.DataSource;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceExceptionHandler;
import it.eng.spagobi.utilities.service.JSONSuccess;
import it.eng.spagobi.utilities.sql.SqlUtils;


/**
 * The Class ExecuteQueryAction.
 */
public class ExecuteMasterQueryAction extends AbstractQbeEngineAction {	
	
	// INPUT PARAMETERS
	public static final String LIMIT = "limit";
	public static final String START = "start";
	
	public static final String SORT = "sort";
	public static final String DIR = "dir";

	public static final String GROUPBY_FIELDS = "groupFields";
	public static final String FORM_STATE = "formState";
	
	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(ExecuteMasterQueryAction.class);
    public static transient Logger auditlogger = Logger.getLogger("audit.query");
    
	
	public void service(SourceBean request, SourceBean response)  {				
				
		
		String queryId;
		Integer limit;
		Integer start;
		JSONArray groupFields;
		Integer maxSize;
		boolean isMaxResultsLimitBlocking;
		IDataStore dataStore;
		JDBCStandardDataSet dataSet;
		JSONDataWriter dataSetWriter;
		
		Query query;
		IStatement statement;
		
		Integer resultNumber;
		JSONObject gridDataFeed = new JSONObject();
		
		Monitor totalTimeMonitor = null;
		Monitor errorHitsMonitor;
					
		logger.debug("IN");
		
		try {
		
			super.service(request, response);	
			
			totalTimeMonitor = MonitorFactory.start("QbeEngine.executeQueryAction.totalTime");
			
			start = getAttributeAsInteger( START );	
			logger.debug("Parameter [" + START + "] is equals to [" + start + "]");
			
			limit = getAttributeAsInteger( LIMIT );
			logger.debug("Parameter [" + LIMIT + "] is equals to [" + limit + "]");
			
			groupFields = getAttributeAsJSONArray( GROUPBY_FIELDS );
			logger.debug("Parameter [" + GROUPBY_FIELDS + "] is equals to [" + groupFields + "]");
			Assert.assertNotNull(groupFields, "Parameter [" + GROUPBY_FIELDS + "] cannot be null");
			Assert.assertTrue(groupFields.length() > 0, "GroupBy fileds list cannot be empty");
						
			maxSize = QbeEngineConfig.getInstance().getResultLimit();			
			logger.debug("Configuration setting  [" + "QBE.QBE-SQL-RESULT-LIMIT.value" + "] is equals to [" + (maxSize != null? maxSize: "none") + "]");
			isMaxResultsLimitBlocking = QbeEngineConfig.getInstance().isMaxResultLimitBlocking();
			logger.debug("Configuration setting  [" + "QBE.QBE-SQL-RESULT-LIMIT.isBlocking" + "] is equals to [" + isMaxResultsLimitBlocking + "]");
			
			Assert.assertNotNull(getEngineInstance(), "It's not possible to execute " + this.getActionName() + " service before having properly created an instance of EngineInstance class");
			
			
			// STEP 1: modify the query according to the input that come from the form
			query = getEngineInstance().getQueryCatalogue().getFirstQuery();
			
			// ... query transformation goes here	
			logger.debug("Making a deep copy of the original query...");
			String store = ((JSONObject)SerializerFactory.getSerializer("application/json").serialize(query, getEngineInstance().getDatamartModel(), getLocale())).toString();
			Query copy = SerializerFactory.getDeserializer("application/json").deserializeQuery(store, getEngineInstance().getDatamartModel());
			logger.debug("Deep copy of the original query produced");
			
			String jsonEncodedFormState = getAttributeAsString( FORM_STATE );
			logger.debug("Form state retrieved as a string: " + jsonEncodedFormState);
			JSONObject formState = new JSONObject(jsonEncodedFormState);
			logger.debug("Form state converted into a valid JSONObject: " + formState.toString(3));
			JSONObject template = (JSONObject) getEngineInstance().getFormState().getConf();
			logger.debug("Form viewer template retrieved.");
			
			FormViewerQueryTransformer formViewerQueryTransformer = new FormViewerQueryTransformer();
			formViewerQueryTransformer.setFormState(formState);
			formViewerQueryTransformer.setTemplate(template);
			logger.debug("Applying Form Viewer query transformation...");
			query = formViewerQueryTransformer.execTransformation(copy);
			logger.debug("Applying Form Viewer query transformation...");
			
			updatePromptableFiltersValue(query);
			getEngineInstance().setActiveQuery(query);
			
			// STEP 2: prepare statment and obtain the corresponding sql query
			statement = getEngineInstance().getStatment();	
			statement.setParameters( getEnv() );
			
			String hqlQuery = statement.getQueryString();
			String sqlQuery = ((HQLStatement)statement).getSqlQueryString();
			
			UserProfile userProfile = (UserProfile)getEnv().get(EngineConstants.ENV_USER_PROFILE);
			
			
			// STEP 3: transform the sql query
			GroupByQueryTransformer transformer = new GroupByQueryTransformer();
			List selectFields = SqlUtils.getSelectFields(sqlQuery);
			
			for(int i = 0; i < groupFields.length(); i++) {
				String groupByField = groupFields.getString(i);
				int fieldIndex = query.getSelectFieldIndex(groupByField);		
				Assert.assertTrue(fieldIndex >= 0 && fieldIndex <selectFields.size(), "Impossible to group on field [" + groupByField + "]");
				String[] f = (String[])selectFields.get(fieldIndex);				
				transformer.addGrouByColumn(f[1]!=null? f[1]:f[0], query.getSelectFieldByIndex(fieldIndex).getAlias());
			}
			
			// count column
			transformer.addAggregateColumn("*"/*f[1]!=null? f[1]:f[0]*/, "COUNT", "Records");
			
			// aggregate measures
			List dataMartSelectFields = query.getDataMartSelectFields(true);
			Iterator it = dataMartSelectFields.iterator();
			while (it.hasNext()) {
				DataMartSelectField field = (DataMartSelectField) it.next();
				int fieldIndex = query.getSelectFieldIndex(field.getUniqueName());				
				String[] f = (String[])selectFields.get(fieldIndex);
				IAggregationFunction aggregationFunction = field.getFunction();
				if (aggregationFunction != null && aggregationFunction != AggregationFunctions.NONE_FUNCTION) {
					transformer.addAggregateColumn(f[1]!=null? f[1]:f[0], aggregationFunction.getName(), field.getAlias());
				}
			}
			
			sqlQuery = (String)transformer.transformQuery(sqlQuery);
			
			// STEP 4: execute the query
			
			try {
				logger.debug("Executing query: [" + sqlQuery + "]");
				auditlogger.info("[" + userProfile.getUserId() + "]:: SQL: " + sqlQuery);
				
				dataSet = new JDBCStandardDataSet();
				//Session session = getDatamartModel().getDataSource().getSessionFactory().openSession();
				DBConnection connection = ((IHibernateDataSource)getDatamartModel().getDataSource()).getConnection();
				DataSource dataSource = new DataSource();
				dataSource.setJndi(connection.getJndiName());
				dataSource.setHibDialectName(connection.getDialect());
				dataSource.setUrlConnection(connection.getUrl());
				dataSource.setDriver(connection.getDriverClass());
				dataSource.setUser(connection.getUsername());
				dataSource.setPwd(connection.getPassword());
				dataSet.setDataSource(dataSource);
				dataSet.setQuery(sqlQuery);
				dataSet.loadData(start, limit, -1);
				dataStore = dataSet.getDataStore();
			} catch (Exception e) {
				logger.debug("Query execution aborted because of an internal exceptian");
				SpagoBIEngineServiceException exception;
				String message;
				
				message = "An error occurred in " + getActionName() + " service while executing query: [" +  statement.getQueryString() + "]";				
				exception = new SpagoBIEngineServiceException(getActionName(), message, e);
				exception.addHint("Check if the query is properly formed: [" + statement.getQueryString() + "]");
				exception.addHint("Check connection configuration");
				exception.addHint("Check the qbe jar file");
				
				throw exception;
			}
			logger.debug("Query executed succesfully");
			
			
			//dataStore.getMetaData().setProperty("resultNumber", new Integer( (int)dataStore.getRecordsCount() ));
			
			resultNumber = (Integer)dataStore.getMetaData().getProperty("resultNumber");
			Assert.assertNotNull(resultNumber, "property [resultNumber] of the dataStore returned by loadData method of the class [" + dataSet.getClass().getName()+ "] cannot be null");
			logger.debug("Total records: " + resultNumber);			
			
			
			boolean overflow = maxSize != null && resultNumber >= maxSize;
			if (overflow) {
				logger.warn("Query results number [" + resultNumber + "] exceeds max result limit that is [" + maxSize + "]");
				auditlogger.info("[" + userProfile.getUserId() + "]:: max result limit [" + maxSize + "] exceeded with SQL: " + sqlQuery);
			}
						
			dataSetWriter = new JSONDataWriter();
			gridDataFeed = (JSONObject)dataSetWriter.write(dataStore);
			
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
			if(totalTimeMonitor != null) totalTimeMonitor.stop();
			logger.debug("OUT");
		}	
		
		
	}
	
	private void updatePromptableFiltersValue(Query query) {
		logger.debug("IN");
		List whereFields = query.getWhereFields();
		Iterator whereFieldsIt = whereFields.iterator();
		while (whereFieldsIt.hasNext()) {
			WhereField whereField = (WhereField) whereFieldsIt.next();
			if (whereField.isPromptable()) {
				// getting filter value on request
				String promptValue = this.getAttributeAsString(whereField.getName());
				logger.debug("Read prompt value [" + promptValue + "] for promptable filter [" + whereField.getName() + "].");
				if (promptValue != null) {
					whereField.getRightOperand().lastValues = new String[] {promptValue}; // TODO how to manage multi-values prompts?;
				}
			}
		}
		List havingFields = query.getHavingFields();
		Iterator havingFieldsIt = havingFields.iterator();
		while (havingFieldsIt.hasNext()) {
			HavingField havingField = (HavingField) havingFieldsIt.next();
			if (havingField.isPromptable()) {
				// getting filter value on request
				String promptValue = this.getAttributeAsString(havingField.getName());
				logger.debug("Read prompt value [" + promptValue + "] for promptable filter [" + havingField.getName() + "].");
				if (promptValue != null) {
					havingField.getRightOperand().lastValues = new String[] {promptValue}; // TODO how to manage multi-values prompts?;
				}
			}
		}
		logger.debug("OUT");
	}

	
}
