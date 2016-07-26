/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.tools.scheduler.dispatcher;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import it.eng.spago.security.IEngUserProfile;
import it.eng.spagobi.analiticalmodel.document.bo.BIObject;
import it.eng.spagobi.analiticalmodel.document.bo.Snapshot;
import it.eng.spagobi.analiticalmodel.document.dao.ISnapshotDAO;
import it.eng.spagobi.commons.dao.DAOFactory;
import it.eng.spagobi.tools.massiveExport.services.StartMassiveScheduleAction;
import it.eng.spagobi.tools.scheduler.to.DispatchContext;
import it.eng.spagobi.tools.scheduler.utils.SchedulerUtilities;
import it.eng.spagobi.utilities.rest.RestUtilities;
import it.eng.spagobi.utilities.rest.RestUtilities.HttpMethod;
import it.eng.spagobi.utilities.rest.RestUtilities.Response;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Giulio Gavardi
 *
 */
public class ContextBrokerDocumentDispatcherChannel implements IDocumentDispatchChannel {

	private DispatchContext dispatchContext;
	private String contextBrokerUrl;
	private String contextBrokerType;
	
	public static final String SPAGOBI_KPI_CONTEXT_BROKER_TYPE = "__SPAGOBI_KPI_CONTEXT_BROKER_TYPE__";

	// logger component
	private static Logger logger = Logger.getLogger(ContextBrokerDocumentDispatcherChannel.class); 

	public ContextBrokerDocumentDispatcherChannel(DispatchContext dispatchContext) {
		this.dispatchContext = dispatchContext;
		contextBrokerUrl = dispatchContext.getContextBrokerUrl();
		contextBrokerType = dispatchContext.getContextBrokerType();
	}

	public void setDispatchContext(DispatchContext dispatchContext) {
		this.dispatchContext = dispatchContext;
	}

	public void close() {

	}

	public boolean canDispatch(BIObject document)  {
		return true;
	}

	public boolean dispatch(BIObject document, byte[] executionOutput) {
		IEngUserProfile profile;
		String descriptionSuffix; 

		logger.debug("IN");

		profile = dispatchContext.getUserProfile();
		descriptionSuffix = dispatchContext.getDescriptionSuffix();

		if(executionOutput == null || executionOutput.length == 0){
			logger.error("Response is empty, nothing to dispatch");
			return false;
		}
		
		JSONArray array = null;
		try {
			contextBrokerUrl = dispatchContext.getContextBrokerUrl();
			if( (contextBrokerUrl==null) || contextBrokerUrl.trim().equals("")) {
				throw new Exception("Context Broker Url not specified");
			}
			logger.debug("Context Broker Url is "+contextBrokerUrl);

			contextBrokerType = dispatchContext.getContextBrokerType();
			logger.debug("Context Broker Type is "+contextBrokerType);
			
			try{
				String execOutputString = new String(executionOutput);
				
				// if context broker type is defined substitute it otherwise use default value SPAGOBI_KPI
				if(dispatchContext.getContextBrokerType() != null && !dispatchContext.getContextBrokerType().equals("")){
					execOutputString = execOutputString.replaceAll(SPAGOBI_KPI_CONTEXT_BROKER_TYPE, dispatchContext.getContextBrokerType());
				}
				else{
					execOutputString = execOutputString.replaceAll(SPAGOBI_KPI_CONTEXT_BROKER_TYPE, "SPAGOBI_KPI");					
				}
				
				array = new JSONArray(execOutputString);
			}
			catch(Exception e){
				logger.error("Error in converting output response byte array", e);
				throw new Exception("Error in converting output response byte array.");
			}

			logger.debug("Output response byte array converted in Json Array");
			
			if(array == null){
				logger.warn("No response to dispatch");
				return false;
			}
			if(array.length() > 0){
				logger.debug("Response array has "+array.length()+" elements");
			}

			for (int index = 0; index < array.length(); index++ ) {
				JSONObject kpitToSend = (JSONObject) array.get(index);
				logger.debug("Send JSON Object "+kpitToSend);
				
				Map<String, String> headersMap = new HashMap<String, String>();
				headersMap.put("Content-Type", "application/json");
				headersMap.put("Accept", "application/json");
				
				Response response = RestUtilities.makeRequest(HttpMethod.Post, contextBrokerUrl, headersMap, kpitToSend.toString(), null);
				int statusCode = response.getStatusCode();
				logger.debug("KPI object sent, status code returned "+statusCode);
				
			}

		} catch (Exception e) {
			logger.error("Error while sending schedule to context broker", e);
			return false;
		} finally{
			logger.debug("OUT");
		}

		return true;
	}
}
