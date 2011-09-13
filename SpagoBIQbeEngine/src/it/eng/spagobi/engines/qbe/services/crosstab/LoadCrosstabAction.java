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

import it.eng.qbe.query.Query;
import it.eng.qbe.serializer.SerializationManager;
import it.eng.qbe.statement.IStatement;
import it.eng.spago.base.SourceBean;
import it.eng.spago.configuration.ConfigSingleton;
import it.eng.spagobi.commons.QbeEngineStaticVariables;
import it.eng.spagobi.engines.qbe.QbeEngineConfig;
import it.eng.spagobi.engines.qbe.crosstable.CrossTab;
import it.eng.spagobi.engines.qbe.crosstable.CrosstabDefinition;
import it.eng.spagobi.engines.qbe.services.formviewer.ExecuteMasterQueryAction;
import it.eng.spagobi.engines.qbe.services.worksheet.AbstractWorksheetEngineAction;
import it.eng.spagobi.engines.qbe.utils.crosstab.CrosstabQueryCreator;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceExceptionHandler;
import it.eng.spagobi.utilities.service.JSONSuccess;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

public class LoadCrosstabAction extends AbstractWorksheetEngineAction {	
	
	// INPUT PARAMETERS


	private static final long serialVersionUID = -5780454016202425492L;

	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(LoadCrosstabAction.class);
    public static transient Logger auditlogger = Logger.getLogger("audit.query");
    
	
	public void service(SourceBean request, SourceBean response)  {				
				
		IDataStore dataStore = null;
		
		Query query = null;
		IStatement statement = null;
				
		JSONObject jsonFormState=null;
		
		Integer maxSize = null;
		CrosstabDefinition crosstabDefinition = null;
		
		Monitor totalTimeMonitor = null;
		Monitor errorHitsMonitor = null;
					
		logger.debug("IN");
		
		try {
		
			super.service(request, response);	
			
			totalTimeMonitor = MonitorFactory.start("QbeEngine.executeCrosstabQueryAction.totalTime");
			
			JSONObject crosstabDefinitionJSON = getAttributeAsJSONObject( QbeEngineStaticVariables.CROSSTAB_DEFINITION );
			jsonFormState = loadSmartFilterFormValues();
			logger.debug("Form state retrieved as a string: " + jsonFormState);
			
			Assert.assertNotNull(crosstabDefinitionJSON, "Parameter [" + QbeEngineStaticVariables.CROSSTAB_DEFINITION + "] cannot be null in oder to execute " + this.getActionName() + " service");
			logger.debug("Parameter [" + crosstabDefinitionJSON + "] is equals to [" + crosstabDefinitionJSON.toString() + "]");
			//crosstabDefinition = SerializerFactory.getDeserializer("application/json").deserializeCrosstabDefinition(crosstabDefinitionJSON);;
			crosstabDefinition = (CrosstabDefinition)SerializationManager.deserialize(crosstabDefinitionJSON, "application/json", CrosstabDefinition.class);
			crosstabDefinition.setCellLimit( new Integer((String)ConfigSingleton.getInstance().getAttribute("QBE.QBE-CROSSTAB-CELLS-LIMIT.value")) );
			maxSize = QbeEngineConfig.getInstance().getResultLimit();			
			logger.debug("Configuration setting  [" + "QBE.QBE-SQL-RESULT-LIMIT.value" + "] is equals to [" + (maxSize != null? maxSize: "none") + "]");
			
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
			
			String baseQuery = statement.getSqlQueryString();
			
			String worksheetQuery = buildSqlStatement(crosstabDefinition, query, baseQuery, statement);

			dataStore = this.executeWorksheetQuery(worksheetQuery, baseQuery, null, null);
			
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

	/**
	 * Loads the values of the form if the calling engine is smart filter
	 * @return
	 * @throws JSONException
	 */
	protected JSONObject loadSmartFilterFormValues() throws JSONException{
		String jsonEncodedFormState = getAttributeAsString( ExecuteMasterQueryAction.FORM_STATE );
		if(jsonEncodedFormState!=null){
			return new JSONObject(jsonEncodedFormState);
		}
		return null;
	}
	
	/**
	 * Build the sql statement for the temporary table 
	 * @param crosstabDefinition definition of the crosstab
	 * @param baseQuery base query
	 * @param sqlQuery the sql rappresentation of the base query
	 * @param stmt the qbe statement
	 * @return
	 * @throws JSONException
	 */
	protected String buildSqlStatement(CrosstabDefinition crosstabDefinition, Query baseQuery, String sqlQuery, IStatement stmt) throws JSONException{
		return CrosstabQueryCreator.getCrosstabQuery(crosstabDefinition, baseQuery, null, sqlQuery, stmt);
	}
	
}
