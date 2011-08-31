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

import it.eng.qbe.datasource.ConnectionDescriptor;
import it.eng.qbe.query.Query;
import it.eng.qbe.query.serializer.json.LookupStoreJSONSerializer;
import it.eng.qbe.statement.IStatement;
import it.eng.spago.base.SourceBean;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.engines.qbe.services.core.AbstractQbeEngineAction;
import it.eng.spagobi.engines.qbe.services.formviewer.ExecuteMasterQueryAction;
import it.eng.spagobi.engines.qbe.utils.crosstab.CrosstabQueryCreator;
import it.eng.spagobi.engines.qbe.utils.temporarytable.TemporaryTableManager;
import it.eng.spagobi.tools.dataset.bo.JDBCDataSet;
import it.eng.spagobi.tools.dataset.common.datastore.DataStore;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.datasource.bo.DataSource;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceExceptionHandler;
import it.eng.spagobi.utilities.service.JSONSuccess;
import it.eng.spagobi.utilities.sql.SqlUtils;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

public class GetValuesForCrosstabAttributesAction extends AbstractQbeEngineAction {	
	
	// INPUT PARAMETERS
	public static final String ALIAS = "ALIAS";
	
	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(GetValuesForCrosstabAttributesAction.class);
    
	
	public void service(SourceBean request, SourceBean response)  {				
				
		IDataStore dataStore = null;
		Query query = null;
		IStatement statement = null;
		JSONObject jsonFormState = null;
		LookupStoreJSONSerializer serializer = null;
		JSONObject gridDataFeed = null;
		Integer resultNumber = null;
		
		Monitor totalTimeMonitor = null;
		Monitor errorHitsMonitor = null;
					
		logger.debug("IN");
		
		try {
		
			super.service(request, response);
			
			totalTimeMonitor = MonitorFactory.start("QbeEngine.getValuesForCrosstabAttributesAction.totalTime");
			
			Assert.assertNotNull(getEngineInstance(), "It's not possible to execute " + this.getActionName() + " service before having properly created an instance of EngineInstance class");
			
			// retrieving first QbE query and setting it as active query
			query = getEngineInstance().getQueryCatalogue().getFirstQuery();
			
			//build the query filtered for the smart filter
			jsonFormState = loadSmartFilterFormValues();
			logger.debug("Form state retrieved as a string: " + jsonFormState);
			if ( jsonFormState != null ) {
				query = getFilteredQuery(query, jsonFormState);
			}
			
			getEngineInstance().setActiveQuery(query);
			
			statement = getEngineInstance().getStatment();	
			statement.setParameters( getEnv() );

			String sqlQuery = statement.getSqlQueryString();
			UserProfile userProfile = (UserProfile)getEnv().get(EngineConstants.ENV_USER_PROFILE);
			
			ConnectionDescriptor connection = (ConnectionDescriptor)getDataSource().getConfiguration().loadDataSourceProperties().get("connection");
			DataSource dataSource = getDataSource(connection);
			
			String sqlStatement = buildSqlStatement(query, sqlQuery);
			logger.debug("Querying temporary table: user [" + userProfile.getUserId() + "] (SQL): [" + sqlStatement + "]");
			
			if (!TemporaryTableManager.isEnabled()) {
				logger.warn("TEMPORARY TABLE STRATEGY IS DISABLED!!! " +
						"Using inline view construct, therefore performance will be very low");
				int beginIndex = sqlStatement.toUpperCase().indexOf(" FROM ") + " FROM ".length(); 
				int endIndex = sqlStatement.indexOf(" ", beginIndex);
				String inlineSQLQuery = sqlStatement.substring(0, beginIndex) + " ( " + sqlQuery + " ) TEMP " + sqlStatement.substring(endIndex);
				logger.debug("Executable query for user [" + userProfile.getUserId() + "] (SQL): [" + inlineSQLQuery + "]");
				JDBCDataSet dataSet = new JDBCDataSet();
				dataSet.setDataSource(dataSource);
				dataSet.setQuery(inlineSQLQuery);
				dataSet.loadData();
				dataStore = (DataStore) dataSet.getDataStore();
			} else {
				logger.debug("Using temporary table strategy....");
				logger.debug("Temporary table definition for user [" + userProfile.getUserId() + "] (SQL): [" + sqlQuery + "]");
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
			
			serializer = new LookupStoreJSONSerializer();
			gridDataFeed = (JSONObject)serializer.serialize(dataStore);
			
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

	protected JSONObject loadSmartFilterFormValues() throws JSONException{
		String jsonEncodedFormState = getAttributeAsString( ExecuteMasterQueryAction.FORM_STATE );
		if(jsonEncodedFormState!=null){
			return new JSONObject(jsonEncodedFormState);
		}
		return null;
	}
	

	protected String buildSqlStatement(Query baseQuery, String sqlQuery) throws JSONException{
		logger.debug("IN");
		StringBuffer buffer = new StringBuffer();
		List baseQuerySelectedFields = SqlUtils.getSelectFields(sqlQuery);
		String alias = getAttributeAsString( ALIAS );
		Assert.assertNotNull(alias, "Parameter [" + ALIAS + "] cannot be null in oder to execute " + this.getActionName() + " service");
		logger.debug("Qbe statement alias = [" + alias + "]");
		alias = CrosstabQueryCreator.getSQLAlias(alias, baseQuery, baseQuerySelectedFields);
		logger.debug("SQL alias = [" + alias + "]");
		buffer.append("SELECT DISTINCT " + alias + " FROM TEMPORARY_TABLE ");
		String toReturn = buffer.toString();
		logger.debug("OUT: returning " + toReturn);
		return toReturn;
	}
	
}
