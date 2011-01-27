/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2009 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

**/
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
