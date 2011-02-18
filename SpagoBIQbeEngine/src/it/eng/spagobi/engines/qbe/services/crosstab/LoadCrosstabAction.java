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
package it.eng.spagobi.engines.qbe.services.crosstab;

import it.eng.qbe.commons.serializer.SerializerFactory;
import it.eng.qbe.crosstab.bo.CrossTab;
import it.eng.qbe.crosstab.bo.CrosstabDefinition;
import it.eng.qbe.datasource.hibernate.DBConnection;
import it.eng.qbe.datasource.hibernate.IHibernateDataSource;
import it.eng.qbe.query.Query;
import it.eng.qbe.statment.IStatement;
import it.eng.qbe.statment.hibernate.HQLStatement;
import it.eng.spago.base.SourceBean;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.engines.qbe.QbeEngineConfig;
import it.eng.spagobi.engines.qbe.services.core.AbstractQbeEngineAction;
import it.eng.spagobi.engines.qbe.services.formviewer.ExecuteMasterQueryAction;
import it.eng.spagobi.engines.qbe.services.formviewer.FormViewerQueryTransformer;
import it.eng.spagobi.engines.qbe.utils.crosstab.CrosstabQueryCreator;
import it.eng.spagobi.engines.qbe.utils.temporarytable.TemporaryTableManager;
import it.eng.spagobi.tools.dataset.bo.JDBCStandardDataSet;
import it.eng.spagobi.tools.dataset.common.datastore.DataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.datasource.bo.DataSource;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceExceptionHandler;
import it.eng.spagobi.utilities.service.JSONSuccess;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

public class LoadCrosstabAction extends AbstractQbeEngineAction {	
	
	// INPUT PARAMETERS
	public static final String CROSSTAB_DEFINITION = "crosstabDefinition";

	
	
	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(LoadCrosstabAction.class);
    public static transient Logger auditlogger = Logger.getLogger("audit.query");
    
	
	public void service(SourceBean request, SourceBean response)  {				
				
		IDataStore dataStore = null;
		
		Query query = null;
		IStatement statement = null;
		
		Integer maxSize = null;
		Integer resultNumber = null;
		CrosstabDefinition crosstabDefinition = null;
		
		Monitor totalTimeMonitor = null;
		Monitor errorHitsMonitor = null;
					
		logger.debug("IN");
		
		try {
		
			super.service(request, response);	
			
			totalTimeMonitor = MonitorFactory.start("QbeEngine.executeCrosstabQueryAction.totalTime");
			
			JSONObject crosstabDefinitionJSON = getAttributeAsJSONObject( CROSSTAB_DEFINITION );
			String jsonEncodedFormState = getAttributeAsString( ExecuteMasterQueryAction.FORM_STATE );
			logger.debug("Form state retrieved as a string: " + jsonEncodedFormState);
			
			Assert.assertNotNull(crosstabDefinitionJSON, "Parameter [" + CROSSTAB_DEFINITION + "] cannot be null in oder to execute " + this.getActionName() + " service");
			logger.debug("Parameter [" + crosstabDefinitionJSON + "] is equals to [" + crosstabDefinitionJSON.toString() + "]");
			crosstabDefinition = SerializerFactory.getDeserializer("application/json").deserializeCrosstabDefinition(crosstabDefinitionJSON);;
			
			maxSize = QbeEngineConfig.getInstance().getResultLimit();			
			logger.debug("Configuration setting  [" + "QBE.QBE-SQL-RESULT-LIMIT.value" + "] is equals to [" + (maxSize != null? maxSize: "none") + "]");
			
			Assert.assertNotNull(getEngineInstance(), "It's not possible to execute " + this.getActionName() + " service before having properly created an instance of EngineInstance class");
			
			// retrieving first QbE query and setting it as active query
			query = getEngineInstance().getQueryCatalogue().getFirstQuery();
			
			if (jsonEncodedFormState != null) {
				logger.debug("Making a deep copy of the original query...");
				String store = ((JSONObject)SerializerFactory.getSerializer("application/json").serialize(query, getEngineInstance().getDatamartModel(), getLocale())).toString();
				Query copy = SerializerFactory.getDeserializer("application/json").deserializeQuery(store, getEngineInstance().getDatamartModel());
				logger.debug("Deep copy of the original query produced");
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
			}
			
			getEngineInstance().setActiveQuery(query);
			
			statement = getEngineInstance().getStatment();	
			statement.setParameters( getEnv() );
			
			String hqlQuery = statement.getQueryString();
			String sqlQuery = ((HQLStatement)statement).getSqlQueryString();
			UserProfile userProfile = (UserProfile)getEnv().get(EngineConstants.ENV_USER_PROFILE);
			
			DBConnection connection = ((IHibernateDataSource)getDatamartModel().getDataSource()).getConnection();
			DataSource dataSource = getDataSource(connection);
			
			String sqlStatement = CrosstabQueryCreator.getCrosstabQuery(crosstabDefinition, query, sqlQuery);
			logger.debug("Querying temporary table: user [" + userProfile.getUserId() + "] (SQL): [" + sqlStatement + "]");
			
			if (!TemporaryTableManager.isEnabled()) {
				logger.warn("TEMPORARY TABLE STRATEGY IS DISABLED!!! " +
						"Using inline view construct, therefore performance will be very low");
				int beginIndex = sqlStatement.toUpperCase().indexOf(" FROM ") + " FROM ".length(); 
				int endIndex = sqlStatement.indexOf(" ", beginIndex);
				String inlineSQLQuery = sqlStatement.substring(0, beginIndex) + " ( " + sqlQuery + " ) TEMP " + sqlStatement.substring(endIndex);
				logger.debug("Executable query for user [" + userProfile.getUserId() + "] (SQL): [" + inlineSQLQuery + "]");
				auditlogger.info("[" + userProfile.getUserId() + "]:: SQL: " + inlineSQLQuery);
				JDBCStandardDataSet dataSet = new JDBCStandardDataSet();
				dataSet.setDataSource(dataSource);
				dataSet.setQuery(inlineSQLQuery);
				dataSet.loadData();
				dataStore = (DataStore) dataSet.getDataStore();
			} else {
				logger.debug("Using temporary table strategy....");
				logger.debug("Temporary table definition for user [" + userProfile.getUserId() + "] (HQL): [" + hqlQuery + "]");
				logger.debug("Temporary table definition for user [" + userProfile.getUserId() + "] (SQL): [" + sqlQuery + "]");
				auditlogger.info("Temporary table definition for user [" + userProfile.getUserId() + "]:: HQL: " + hqlQuery);
				auditlogger.info("Temporary table definition for user [" + userProfile.getUserId() + "]:: SQL: " + sqlQuery);
				auditlogger.info("Querying temporary table: user [" + userProfile.getUserId() + "] (SQL): [" + sqlStatement + "]");

				try {
					dataStore = TemporaryTableManager.queryTemporaryTable(userProfile, sqlStatement, sqlQuery, dataSource);
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
			
			resultNumber = (Integer)dataStore.getMetaData().getProperty("resultNumber");
			Assert.assertNotNull(resultNumber, "property [resultNumber] of the dataStore returned by queryTemporaryTable method of the class [" + TemporaryTableManager.class.getName()+ "] cannot be null");
			logger.debug("Total records: " + resultNumber);			
			
			
			boolean overflow = maxSize != null && resultNumber >= maxSize;
			if (overflow) {
				logger.warn("Query results number [" + resultNumber + "] exceeds max result limit that is [" + maxSize + "]");
				auditlogger.info("[" + userProfile.getUserId() + "]:: max result limit [" + maxSize + "] exceeded with SQL: " + sqlQuery);
			}
			
			CrossTab crossTab = new CrossTab(dataStore, crosstabDefinition);
			JSONObject crossTabDefinition = crossTab.getJSONCrossTab();
			
			try {
				writeBackToClient( new JSONSuccess(crossTabDefinition) );
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

	private DataSource getDataSource(DBConnection connection) {
		DataSource dataSource = new DataSource();
		dataSource.setJndi(connection.getJndiName());
		dataSource.setHibDialectName(connection.getDialect());
		dataSource.setUrlConnection(connection.getUrl());
		dataSource.setDriver(connection.getDriverClass());
		dataSource.setUser(connection.getUsername());
		dataSource.setPwd(connection.getPassword());
		return dataSource;
	}
}
