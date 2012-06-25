/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.chart.services.initializers;

import it.eng.spago.base.SourceBean;
import it.eng.spagobi.engines.chart.ChartEngine;
import it.eng.spagobi.engines.chart.ChartEngineInstance;
import it.eng.spagobi.utilities.ParametersDecoder;
import it.eng.spagobi.utilities.engines.AbstractEngineStartAction;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.engines.SpagoBIEngineStartupException;
import it.eng.spagobi.utilities.json.JSONTemplateUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;


/**
 * Entry point action.
 */
public class ChartEngineEXTJSStartAction extends AbstractEngineStartAction {

	/*private String[] lstEnvVariables = {"SBICONTEXT", "SBI_COUNTRY", "SBI_LANGUAGE", 
			"SBI_SPAGO_CONTROLLER",  "SBI_EXECUTION_ROLE", "SBI_HOST", 
			"DOCUMENT_ID", "isFromCross", "country", "language",  "user_id",
			"DATASET", "NEW_SESSION", "ACTION_NAME", "ROLE"};
	*/
	
	// INPUT PARAMETERS
	
	// OUTPUT PARAMETERS
	public static final String LANGUAGE = "LANGUAGE";
	public static final String COUNTRY = "COUNTRY";
	public static final String DOCUMENT_LABEL = "DOCUMENT_LABEL";
	public static final String DOCUMENT_PARAMETERS = "DOCUMENT_PARAMETERS";



	
	// SESSION PARAMETRES	
	public static final String ENGINE_INSTANCE = EngineConstants.ENGINE_INSTANCE;
	
	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(ChartEngineEXTJSStartAction.class);
    
    private String documentLabel;
    
    private static final String ENGINE_NAME = "SpagoBIChartEngine";
    private static final String REQUEST_DISPATCHER_URL = "/WEB-INF/jsp/chart.jsp";
	

	public void service(SourceBean serviceRequest, SourceBean serviceResponse)  {
		logger.debug("IN");		
		Locale locale;
		ChartEngineInstance chartEngineInstance = null;
		JSONTemplateUtils templateUtil = new JSONTemplateUtils();
		JSONArray parsJSON;
		
		try {
			setEngineName(ENGINE_NAME);
			super.service(serviceRequest, serviceResponse);
			
			logger.debug("User Id: " + getUserId());
			logger.debug("Audit Id: " + getAuditId());
			logger.debug("Document Id: " + getDocumentId());
			logger.debug("Document Label: " + getDocumentLabel());
			logger.debug("Template: " + getTemplateAsString());
						
			if(getAuditServiceProxy() != null) {
				logger.debug("Audit enabled: [TRUE]");
				getAuditServiceProxy().notifyServiceStartEvent();
			} else {
				logger.debug("Audit enabled: [FALSE]");
			}
			
			logger.debug("Creating engine instance ...");
			
			try {
				parsJSON = getParametersAsJSON();
				SourceBean content = SourceBean.fromXMLString(getTemplateAsString());
				JSONObject template = templateUtil.getJSONTemplateFromXml( content, parsJSON); 				
				//System.out.println(template.toString(4));
				
				template.append(DOCUMENT_PARAMETERS, parsJSON);

				chartEngineInstance = ChartEngine.createInstance( template, getEnv() );	
				
				//clean parameters to pass at client (only documents params)
				//Map docPpars = chartEngineInstance.getAnalyticalDrivers();
				
			} catch(Throwable t) {
				SpagoBIEngineStartupException serviceException;
				String msg = "Impossible to create engine instance for document [" + getDocumentId() + "].";
				Throwable rootException = t;
				while(rootException.getCause() != null) {
					rootException = rootException.getCause();
				}
				String str = rootException.getMessage()!=null? rootException.getMessage(): rootException.getClass().getName();
				msg += "\nThe root cause of the error is: " + str;
				serviceException = new SpagoBIEngineStartupException(ENGINE_NAME, msg, t);
				
				
				throw serviceException;
			}
			logger.debug("Engine instance succesfully created");
			//sets the dataset
			chartEngineInstance.setDataSet(getDataSet());
			
			locale = (Locale)chartEngineInstance.getEnv().get(EngineConstants.ENV_LOCALE);
			setAttributeInSession( ENGINE_INSTANCE, chartEngineInstance);		
			setAttribute(ENGINE_INSTANCE, chartEngineInstance);
			documentLabel = getDocumentLabel();
			logger.debug("Parameter [" + DOCUMENT_LABEL + "] is equal to [" + documentLabel + "]");
			setAttribute(DOCUMENT_LABEL, documentLabel);
			setAttribute(DOCUMENT_PARAMETERS, parsJSON);
			
			setAttribute(LANGUAGE, locale.getLanguage());
			setAttribute(COUNTRY, locale.getCountry());
			
		} catch (Exception e) {
			SpagoBIEngineStartupException serviceException = null;
						
			if(e instanceof SpagoBIEngineStartupException) {
				serviceException = (SpagoBIEngineStartupException)e;
			} else {
				Throwable rootException = e;
				while(rootException.getCause() != null) {
					rootException = rootException.getCause();
				}
				String str = rootException.getMessage()!=null? rootException.getMessage(): rootException.getClass().getName();
				String message = "An unpredicted error occurred while executing " + getEngineName() + " service."
								 + "\nThe root cause of the error is: " + str;
				
				serviceException = new SpagoBIEngineStartupException(getEngineName(), message, e);
			}
			
			throw serviceException;
		} finally {
			logger.debug("OUT");
		}
	}
	
	 /**
	  * Gets the document label.
	  * 
	  * @return the document label
	  */
	 private String getDocumentLabel() {
		 if(documentLabel == null) {

			 logger.debug("IN");

			 documentLabel = getAttributeAsString( DOCUMENT_LABEL );			 
			 logger.debug("Document Label parameter received: documentLabel = [" + documentLabel + "]");
			 
			 logger.debug("OUT");
		 }

		 return documentLabel;
	 }

	/** Returns a JSONArray with all params of documents
	 * 
	 * @param obj
	 * @return
	 */

	public JSONArray getParametersAsJSON(){
		JSONArray JSONPars = new JSONArray();
		
		HashMap requestParameters = ParametersDecoder.getDecodedRequestParameters(this.getHttpRequest());		
		
		for (Iterator iterator = requestParameters.keySet().iterator(); iterator.hasNext();) {
			 String key = (String) iterator.next();
			// if (!isAnalyticalDriver(key)) continue;
			 String value = "";
			 if (requestParameters.get(key) instanceof String){
				 value = (String)requestParameters.get(key);
			 }else {
				 //it's a list
				Object[] values =  (Object[]) requestParameters.get(key);
				for (int i=0, l=values.length; i<l; i++ ){
					 value  += (String) values[i];
					 if (i < values.length-1) value += ",";
				}
			 }
			 try{
					JSONObject JSONObj = new JSONObject();						
					JSONObj.put("name",key);
					JSONObj.put("value",value);
					JSONPars.put(JSONObj);				
				} catch (Exception e) {
					logger.warn("Impossible to load parameter object " + key 
							+ " whose value is " + value
							+ " to JSONObject", e);
				}
		}	

		return JSONPars;
	}
	/*
	public Map getAnalyticalDrivers() {
		Map toReturn = new HashMap();
		Iterator it = getEnv().keySet().iterator();
		while(it.hasNext()) {
			String parameterName = (String)it.next();
			Object parameterValue = (Object) getEnv().get(parameterName);

			if (parameterValue != null && 
				parameterValue.getClass().getName().equals("java.lang.String") && isAnalyticalDriver(parameterName)){
				toReturn.put(parameterName, parameterValue);
			}
		}
		return toReturn;
	}
	
	private boolean isAnalyticalDriver (String parName){
		for (int i=0; i < lstEnvVariables.length; i++){
			if (lstEnvVariables[i].equalsIgnoreCase(parName)){
				return false;
			}
		}
		return true;
	}
	*/
}