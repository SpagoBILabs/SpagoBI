/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.engines.whatif;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONObject;
import org.olap4j.OlapDataSource;

import it.eng.spagobi.services.proxy.EventServiceProxy;
import it.eng.spagobi.tools.dataset.bo.IDataSet;
import it.eng.spagobi.tools.datasource.bo.IDataSource;
import it.eng.spagobi.utilities.engines.AbstractEngineInstance;
import it.eng.spagobi.utilities.engines.AuditServiceProxy;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.engines.IEngineAnalysisState;
import it.eng.spagobi.utilities.engines.SpagoBIEngineException;
import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

/**
 * @author ...
 */
public class WhatIfEngineInstance extends AbstractEngineInstance {
	private JSONObject guiSettings;
	private List<String> includes;
	private OlapDataSource olapDataSource;

	public WhatIfEngineInstance(Object template, Map env) {
		super( env );	
		try {
			this.guiSettings = new JSONObject(template);
		} catch (Throwable t) {
			throw new SpagoBIRuntimeException("Impossible to parse template", t);
		}
		
		includes = WhatIfEngine.getConfig().getIncludes();
		
		// TODO initialize olapDataSource
	}
	
	public OlapDataSource getOlapDataSource () {
		return olapDataSource;
	}

	public JSONObject getGuiSettings() {
		return guiSettings;
	}
	
	public List getIncludes() {
		return includes;
	}
	
	public IDataSource getDataSource() {
		return (IDataSource)this.getEnv().get(EngineConstants.ENV_DATASOURCE);
	}
	
	public IDataSet getDataSet() {
		return (IDataSet)this.getEnv().get(EngineConstants.ENV_DATASET);
	}
	
	public Locale getLocale() {
		return (Locale)this.getEnv().get(EngineConstants.ENV_LOCALE);
	}
	
	public AuditServiceProxy getAuditServiceProxy() {
		return (AuditServiceProxy)this.getEnv().get(EngineConstants.ENV_AUDIT_SERVICE_PROXY);
	}
	
	public EventServiceProxy getEventServiceProxy() {
		return (EventServiceProxy)this.getEnv().get(EngineConstants.ENV_EVENT_SERVICE_PROXY);
	}

	
	// -- unimplemented methods ------------------------------------------------------------

	public IEngineAnalysisState getAnalysisState() {
		throw new WhatIfEngineRuntimeException("Unsupported method [getAnalysisState]");
	}


	public void setAnalysisState(IEngineAnalysisState analysisState) {
		throw new WhatIfEngineRuntimeException("Unsupported method [setAnalysisState]");		
	}


	public void validate() throws SpagoBIEngineException {
		throw new WhatIfEngineRuntimeException("Unsupported method [validate]");		
	}
	
	
	
	
}
