/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.chart;

import it.eng.spagobi.services.proxy.DataSetServiceProxy;
import it.eng.spagobi.services.proxy.EventServiceProxy;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.engines.AbstractEngineInstance;
import it.eng.spagobi.utilities.engines.AuditServiceProxy;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.engines.IEngineAnalysisState;
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import org.json.JSONObject;

/**
 * @author Antonella Giachino (antonella.giachino@eng.it)
 */
public class ChartEngineInstance extends AbstractEngineInstance {
	
	private String[] lstEnvVariables = {"SBI_EXECUTION_ID", "SBICONTEXT", "SBI_COUNTRY", "SBI_LANGUAGE", 
			"SBI_SPAGO_CONTROLLER",  "SBI_EXECUTION_ROLE", "SBI_HOST", 
			"DOCUMENT_ID", "isFromCross", "country", "language",  "user_id",
			"DATASET", "NEW_SESSION", "ACTION_NAME", "ROLE"};
	
	private JSONObject template;
	
	private IDataSet dataset;

	public ChartEngineInstance(Object template, Map env) {
		super( env );	
		
		JSONObject templateJSON;
		
		templateJSON = null;
		if(template instanceof JSONObject) {
			templateJSON = (JSONObject)template;
		} else {
			try {
				templateJSON = new JSONObject(template);
			} catch (Throwable t) {
				throw new SpagoBIRuntimeException("Impossible to parse template", t);
			}
		}
				
		setTemplate(templateJSON);		
	}
	
	public JSONObject getTemplate() {
		return template;
	}
	
	public void setTemplate(JSONObject template) {
		this.template = template;
	}
	
	public IDataSource getDataSource() {
		return (IDataSource)this.getEnv().get(EngineConstants.ENV_DATASOURCE);
	}
	
	public IDataSet getDataSet() {
		//return (IDataSet)this.getEnv().get(EngineConstants.ENV_DATASET);
		return this.dataset;
	}
	
	public void setDataSet(IDataSet dataset) {
		this.dataset = dataset;
	}
	
	public Locale getLocale() {
		return (Locale)this.getEnv().get(EngineConstants.ENV_LOCALE);
	}

	public DataSetServiceProxy getDataSetServiceProxy() {
		return (DataSetServiceProxy)this.getEnv().get(EngineConstants.ENV_DATASET_PROXY);
	}

	public AuditServiceProxy getAuditServiceProxy() {
		return (AuditServiceProxy)this.getEnv().get(EngineConstants.ENV_AUDIT_SERVICE_PROXY);
	}
	
	public EventServiceProxy getEventServiceProxy() {
		return (EventServiceProxy)this.getEnv().get(EngineConstants.ENV_EVENT_SERVICE_PROXY);
	}
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
	
	
	// -- unimplemented methods ------------------------------------------------------------

	public IEngineAnalysisState getAnalysisState() {
		throw new ChartEngineRuntimeException("Unsupported method [getAnalysisState]");
	}


	public void setAnalysisState(IEngineAnalysisState analysisState) {
		throw new ChartEngineRuntimeException("Unsupported method [setAnalysisState]");		
	}


	public void validate() throws SpagoBIEngineException {
		throw new ChartEngineRuntimeException("Unsupported method [validate]");		
	}
	
	
	
	
}
