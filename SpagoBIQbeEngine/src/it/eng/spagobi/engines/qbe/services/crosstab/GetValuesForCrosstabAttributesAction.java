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

import it.eng.qbe.query.serializer.json.LookupStoreJSONSerializer;
import it.eng.spago.base.SourceBean;
import it.eng.spagobi.engines.qbe.services.worksheet.AbstractWorksheetEngineAction;
import it.eng.spagobi.engines.worksheet.WorksheetEngineInstance;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.dataset.common.datastore.IDataStore;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceExceptionHandler;
import it.eng.spagobi.utilities.service.JSONSuccess;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

public class GetValuesForCrosstabAttributesAction extends AbstractWorksheetEngineAction {	
	
	private static final long serialVersionUID = 4830778339451786311L;

	// INPUT PARAMETERS
	public static final String ALIAS = "ALIAS";
	public static final String LIMIT = "limit";
	public static final String START = "start";
	
	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(GetValuesForCrosstabAttributesAction.class);
    
	
	public void service(SourceBean request, SourceBean response)  {				
				
		IDataStore dataStore = null;
		JSONObject gridDataFeed = null;
		
		Monitor totalTimeMonitor = null;
		Monitor errorHitsMonitor = null;
					
		logger.debug("IN");
		
		try {
		
			super.service(request, response);
			
			totalTimeMonitor = MonitorFactory.start("QbeEngine.getValuesForCrosstabAttributesAction.totalTime");
			
			String alias = getAttributeAsString( ALIAS );
			Assert.assertNotNull(alias, "Parameter [" + ALIAS + "] cannot be null in oder to execute " + this.getActionName() + " service");
			
			Integer start = getAttributeAsInteger( START );	
			logger.debug("Parameter [" + START + "] is equals to [" + start + "]");
			Integer limit = getAttributeAsInteger( LIMIT );
			logger.debug("Parameter [" + LIMIT + "] is equals to [" + limit + "]");
			
			WorksheetEngineInstance engineInstance = this.getEngineInstance();
			Assert.assertNotNull(engineInstance, "It's not possible to execute " + this.getActionName() + " service before having properly created an instance of WorksheetEngineInstance class");
			
			IDataSet dataset = engineInstance.getDataSet();
			// TODO manage filter
			dataStore = dataset.getDomainValues(alias, start, limit, null);
			
			
//			// retrieving first QbE query and setting it as active query
//			query = getEngineInstance().getQueryCatalogue().getFirstQuery();
//			
//			//build the query filtered for the smart filter
//			jsonFormState = loadSmartFilterFormValues();
//			logger.debug("Form state retrieved as a string: " + jsonFormState);
//			if ( jsonFormState != null ) {
//				query = getFilteredQuery(query, jsonFormState);
//			}
//			
//			getEngineInstance().setActiveQuery(query);
//			
//			statement = getEngineInstance().getStatment();	
//			statement.setParameters( getEnv() );
//
//			String baseQuery = statement.getSqlQueryString();
//			
//			String worksheetQuery = buildSqlStatement(query, baseQuery);
//			
//			dataStore = this.executeWorksheetQuery(worksheetQuery, baseQuery, null, null);
//			
//			serializer = new LookupStoreJSONSerializer();
			
			LookupStoreJSONSerializer serializer = new LookupStoreJSONSerializer();
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

//	protected JSONObject loadSmartFilterFormValues() throws JSONException{
//		String jsonEncodedFormState = getAttributeAsString( ExecuteMasterQueryAction.FORM_STATE );
//		if(jsonEncodedFormState!=null){
//			return new JSONObject(jsonEncodedFormState);
//		}
//		return null;
//	}
	

//	protected String buildSqlStatement(Query baseQuery, String sqlQuery) throws JSONException{
//		logger.debug("IN");
//		StringBuffer buffer = new StringBuffer();
//		List baseQuerySelectedFields = SqlUtils.getSelectFields(sqlQuery);
//		String alias = getAttributeAsString( ALIAS );
//		Assert.assertNotNull(alias, "Parameter [" + ALIAS + "] cannot be null in oder to execute " + this.getActionName() + " service");
//		logger.debug("Qbe statement alias = [" + alias + "]");
//		alias = CrosstabQueryCreator.getSQLAlias(alias, baseQuery, baseQuerySelectedFields);
//		logger.debug("SQL alias = [" + alias + "]");
//		buffer.append("SELECT DISTINCT " + alias + " FROM TEMPORARY_TABLE ");
//		String toReturn = buffer.toString();
//		logger.debug("OUT: returning " + toReturn);
//		return toReturn;
//	}
	
}
