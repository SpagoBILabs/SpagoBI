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

import it.eng.qbe.datasource.hibernate.DBConnection;
import it.eng.qbe.datasource.hibernate.IHibernateDataSource;
import it.eng.qbe.query.Query;
import it.eng.qbe.statment.IStatement;
import it.eng.qbe.statment.hibernate.HQLStatement;
import it.eng.spago.base.SourceBean;
import it.eng.spagobi.commons.bo.UserProfile;
import it.eng.spagobi.engines.qbe.QbeEngineConfig;
import it.eng.spagobi.engines.qbe.bo.CrosstabDefinition;
import it.eng.spagobi.engines.qbe.services.core.AbstractQbeEngineAction;
import it.eng.spagobi.engines.qbe.utils.crosstab.CrosstabQueryCreator;
import it.eng.spagobi.engines.qbe.utils.temporarytable.TemporaryTableManager;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.tools.dataset.common.datawriter.JSONDataWriter;
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
		JSONDataWriter dataSetWriter;
		
		Query query = null;
		IStatement statement = null;
		
		Integer maxSize = null;
		Integer resultNumber = null;
		CrosstabDefinition crosstabDefinition = null;
		JSONObject gridDataFeed = new JSONObject();
		
		Monitor totalTimeMonitor = null;
		Monitor errorHitsMonitor = null;
					
		logger.debug("IN");
		
		try {
		
			super.service(request, response);	
			
			totalTimeMonitor = MonitorFactory.start("QbeEngine.executeCrosstabQueryAction.totalTime");
			
			JSONObject crosstabDefinitionJSON = getAttributeAsJSONObject( CROSSTAB_DEFINITION );
			Assert.assertNotNull(crosstabDefinitionJSON, "Parameter [" + CROSSTAB_DEFINITION + "] cannot be null in oder to execute " + this.getActionName() + " service");
			logger.debug("Parameter [" + crosstabDefinitionJSON + "] is equals to [" + crosstabDefinitionJSON.toString() + "]");
			crosstabDefinition = new CrosstabDefinition(crosstabDefinitionJSON);
			
			maxSize = QbeEngineConfig.getInstance().getResultLimit();			
			logger.debug("Configuration setting  [" + "QBE.QBE-SQL-RESULT-LIMIT.value" + "] is equals to [" + (maxSize != null? maxSize: "none") + "]");
			
			Assert.assertNotNull(getEngineInstance(), "It's not possible to execute " + this.getActionName() + " service before having properly created an instance of EngineInstance class");
			
			// retrieving first QbE query and setting it as active query
			query = getEngineInstance().getQueryCatalogue().getFirstQuery();
			getEngineInstance().setActiveQuery(query);
			
			statement = getEngineInstance().getStatment();	
			statement.setParameters( getEnv() );
			
			String hqlQuery = statement.getQueryString();
			String sqlQuery = ((HQLStatement)statement).getSqlQueryString();
			UserProfile userProfile = (UserProfile)getEnv().get(EngineConstants.ENV_USER_PROFILE);
			logger.debug("Temporary table definition for user [" + userProfile.getUserId() + "] (HQL): [" + hqlQuery + "]");
			logger.debug("Temporary table definition for user [" + userProfile.getUserId() + "] (SQL): [" + sqlQuery + "]");
			auditlogger.info("Temporary table definition for user [" + userProfile.getUserId() + "]:: HQL: " + hqlQuery);
			auditlogger.info("Temporary table definition for user [" + userProfile.getUserId() + "]:: SQL: " + sqlQuery);
			
			try {
				String sqlStatement = CrosstabQueryCreator.getCrosstabQuery(crosstabDefinition, query, sqlQuery);
				DBConnection connection = ((IHibernateDataSource)getDatamartModel().getDataSource()).getConnection();
				DataSource dataSource = new DataSource();
				dataSource.setJndi(connection.getJndiName());
				dataSource.setHibDialectName(connection.getDialect());
				dataSource.setUrlConnection(connection.getUrl());
				dataSource.setDriver(connection.getDriverClass());
				dataSource.setUser(connection.getUsername());
				dataSource.setPwd(connection.getPassword());
				dataStore = TemporaryTableManager.queryTemporaryTable(userProfile, sqlStatement, sqlQuery, dataSource);
				Assert.assertNotNull(dataStore, "The dataStore returned by queryTemporaryTable method of the class [" + TemporaryTableManager.class.getName()+ "] cannot be null");
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
			
			resultNumber = (Integer)dataStore.getMetaData().getProperty("resultNumber");
			Assert.assertNotNull(resultNumber, "property [resultNumber] of the dataStore returned by queryTemporaryTable method of the class [" + TemporaryTableManager.class.getName()+ "] cannot be null");
			logger.debug("Total records: " + resultNumber);			
			
			
			boolean overflow = maxSize != null && resultNumber >= maxSize;
			if (overflow) {
				logger.warn("Query results number [" + resultNumber + "] exceeds max result limit that is [" + maxSize + "]");
				auditlogger.info("[" + userProfile.getUserId() + "]:: max result limit [" + maxSize + "] exceeded with SQL: " + sqlQuery);
			}
			
			//gridDataFeed = buildGridDataFeed(results, resultNumber.intValue());	
	
			CrossTab crossTab = new CrossTab(dataStore, crosstabDefinition);
			JSONObject crossTabDefinition = crossTab.getJSONCrossTab();

			dataSetWriter = new JSONDataWriter();
			gridDataFeed = (JSONObject)dataSetWriter.write(dataStore);
			//logger.debug("Response object: " + gridDataFeed.toString(3));
			
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
}
