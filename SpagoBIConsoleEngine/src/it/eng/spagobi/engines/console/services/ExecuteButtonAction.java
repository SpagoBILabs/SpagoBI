/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.console.services;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.commons.utilities.StringUtilities;
import it.eng.spagobi.engines.console.ConsoleEngineInstance;
import it.eng.spagobi.utilities.assertion.Assert;
import it.eng.spagobi.utilities.engines.SpagoBIEngineServiceExceptionHandler;
import it.eng.spagobi.utilities.exceptions.SpagoBIServiceException;
import it.eng.spagobi.utilities.service.JSONSuccess;




/**
 * This action execute operations available on the toolbar:
 * - refresh (managed here?)
 * - getting error informations
 * - getting warning informations
 * - getting view informations
 * 
 * @author Antonella Giachino (antonella.giachino@eng.it)
 */
public class ExecuteButtonAction extends AbstractConsoleEngineAction {
	
	public static final String SERVICE_NAME = "EXECUTE_BUTTON_ACTION";
	
	// request parameters
	public static String MESSAGE = "message";
	public static String USER_ID = "userId";
	public static String CALLBACK = "callback";
	
	// logger component
	private static Logger logger = Logger.getLogger(ExecuteButtonAction.class);
	
	public void service(SourceBean request, SourceBean response) {
		
		String message;
		String user;
		String callback;
		
		logger.debug("IN");
		Monitor monitor =MonitorFactory.start("SpagoBI_Console.ExecuteButtonAction.service");
		
		try {
			super.service(request,response);
			ConsoleEngineInstance consoleEngineInstance = getConsoleEngineInstance();
		
			message = getAttributeAsString( MESSAGE );
			logger.debug("Parameter [" + MESSAGE + "] is equals to [" + message + "]");			
			Assert.assertTrue(!StringUtilities.isEmpty( message ), "Parameter [" + MESSAGE + "] cannot be null or empty");
			
			user = getAttributeAsString( USER_ID );
			logger.debug("Parameter [" + USER_ID + "] is equals to [" + user + "]");			
			Assert.assertTrue(!StringUtilities.isEmpty( user ), "Parameter [" + USER_ID + "] cannot be null or empty");
			
			callback = getAttributeAsString( CALLBACK );
			logger.debug("Parameter [" + CALLBACK + "] is equals to [" + callback + "]");
			
			
			//for initial test:
			getEmptyResult(message);
			
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
