/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */
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
	

	// INPUT PARAMETERS
	
	// OUTPUT PARAMETERS
	public static final String LANGUAGE = "LANGUAGE";
	public static final String COUNTRY = "COUNTRY";

	
	// SESSION PARAMETRES	
	public static final String ENGINE_INSTANCE = EngineConstants.ENGINE_INSTANCE;
	
	/** Logger component. */
    public static transient Logger logger = Logger.getLogger(ChartEngineEXTJSStartAction.class);
    
    private static final String ENGINE_NAME = "SpagoBIChartEngine";
    private static final String REQUEST_DISPATCHER_URL = "/WEB-INF/jsp/chart.jsp";
	

	public void service(SourceBean serviceRequest, SourceBean serviceResponse)  {
		logger.debug("IN");		
		Locale locale;
		ChartEngineInstance chartEngineInstance = null;
		JSONTemplateUtils templateUtil = new JSONTemplateUtils();

		
		try {
			setEngineName(ENGINE_NAME);
			super.service(serviceRequest, serviceResponse);
			
			logger.debug("User Id: " + getUserId());
			logger.debug("Audit Id: " + getAuditId());
			logger.debug("Document Id: " + getDocumentId());
			logger.debug("Template: " + getTemplateAsString());
						
			if(getAuditServiceProxy() != null) {
				logger.debug("Audit enabled: [TRUE]");
				getAuditServiceProxy().notifyServiceStartEvent();
			} else {
				logger.debug("Audit enabled: [FALSE]");
			}
			
			logger.debug("Creating engine instance ...");
			
			try {
				JSONArray parsJSON = getParametersAsJSON();
				SourceBean content = SourceBean.fromXMLString(getTemplateAsString());
				JSONObject template = templateUtil.getJSONTemplateFromXml( content, parsJSON); 				
				System.out.println(template.toString(4));

				chartEngineInstance = ChartEngine.createInstance( template, getEnv() );				
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
	
}