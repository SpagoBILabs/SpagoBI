/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.console.services;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.engines.console.ConsoleEngineInstance;
import it.eng.spagobi.engines.console.ConsoleEngineRuntimeException;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceException;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceExceptionHandler;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.service.JSONAcknowledge;
import it.eng.spagobi.utilities.service.JSONSuccess;

import java.io.IOException;
import java.util.LinkedHashMap;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utilities.DataSourceUtilities;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;




/**
 * @author Antonella Giachino (antonella.giachino@eng.it)
 */
public class ExecuteUpdateAction extends AbstractConsoleEngineAction {
	
	
	public static final String SERVICE_NAME = "MONITOR";
	
	// request parameters
	public static String DATASET_LABEL = "ds_label";
	public static String USER_ID = "userId";
	public static String CALLBACK = "callback";
	public static String MESSAGE = "message";
	public static String SCHEMA = "schema";
	public static String STMT = "stmt";

	
	// logger component
	private static Logger logger = Logger.getLogger(ExecuteUpdateAction.class);
	ConsoleEngineInstance consoleEngineInstance;
	
	public void service(SourceBean request, SourceBean response) {
		
		String message;
		String user;
		String callback;

		logger.debug("IN");
		Monitor monitor =MonitorFactory.start("SpagoBI_Console.ExecuteUpdateAction.service");	
				
		try {
			super.service(request,response);
			consoleEngineInstance = getConsoleEngineInstance();
			
			//check for mandatory parameters 						
			user = getAttributeAsString( USER_ID );
			logger.debug("Parameter [" + USER_ID + "] is equals to [" + user + "]");			
			Assert.assertTrue(!StringUtilities.isEmpty( user ), "Parameter [" + USER_ID + "] cannot be null or empty");
			
			callback = getAttributeAsString( CALLBACK );
			logger.debug("Parameter [" + CALLBACK + "] is equals to [" + callback + "]");
			
			IDataSource ds = consoleEngineInstance.getDataSource();						
			DataSourceUtilities utility = new DataSourceUtilities(ds);
			//gets hashmap with all parameters			
			LinkedHashMap<String , Object> params;
			params = getAttributesAsLinkedMap();	
			JSONObject metaParams = getAttributeAsJSONObject( "metaParams" );
			logger.debug("Parameter [metaParams] is equals to [" + metaParams + "]");
			
			boolean result = utility.executeUpdateQuery(params, metaParams);
			if ( !result ){
				throw new ConsoleEngineRuntimeException("Impossible to perform update statement");		
			}
			
			try {
				writeBackToClient( new JSONAcknowledge() );
			} catch (IOException e) {
				String msg = "Impossible to write back the responce to the client";
				throw new SpagoBIEngineServiceException(getActionName(), msg, e);
			}
			
		} catch (Throwable t) {
			throw SpagoBIEngineServiceExceptionHandler.getInstance().getWrappedException(getActionName(), getEngineInstance(), t);
		} finally {
			monitor.stop();
			logger.debug("OUT");
		}
	}
	
	private void getEmptyResult(String message){
		try {
			JSONArray emptyListJSON = new JSONArray();
			JSONObject results = new JSONObject();
			results.put("message", message);
			results.put("results", emptyListJSON);
			results.put("totalCount", emptyListJSON.length());
			writeBackToClient( new JSONSuccess( results ) ); 
		} catch (IOException e) {
			throw new SpagoBIServiceException(SERVICE_NAME, "Impossible to write back the responce to the client", e);
		} catch (JSONException e) {
			throw new SpagoBIServiceException(SERVICE_NAME, "Cannot serialize objects into a JSON object", e);
		}
	}
	


}
