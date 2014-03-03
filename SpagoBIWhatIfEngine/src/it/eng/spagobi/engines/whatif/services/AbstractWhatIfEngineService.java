/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.engines.whatif.services;

import it.eng.spagobi.engines.whatif.WhatIfEngineInstance;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.engines.rest.ExecutionSession;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

/**
 * The Class AbstractConsoleEngineAction.
 * 
 * @author Alberto Ghedin (alberto.ghedin@eng.it)
 */
public class AbstractWhatIfEngineService {

	public ExecutionSession es;

	@Context
	protected HttpServletRequest servletRequest;

	/**
	 * Creates the context manager
	 * @return ExecutionSession container of the execution manager
	 */
	public ExecutionSession getExecutionSession(){
		if(es==null){
			es = new ExecutionSession(servletRequest, servletRequest.getSession());
		}
		return es;
	}

	/**
	 * Gets the what if engine instance.
	 * 
	 * @return the console engine instance
	 */
	public WhatIfEngineInstance getWhatIfEngineInstance() {
		ExecutionSession es = getExecutionSession();
		return (WhatIfEngineInstance)es.getAttributeFromSession( EngineConstants.ENGINE_INSTANCE );

	}

	/**
	 * Check if the number is null
	 * @param value the value to check
	 * @return true if the value is null
	 */
	public boolean isNull(Number value){
		return value==null ;
	}

	/**
	 * Check if the string is null
	 * @param value the value to check
	 * @return true if the value is null
	 */
	public boolean isNull(String value){
		return value==null || value.equals("null") || value.equals("undefined");
	}

	/**
	 * Check if the string is null or ""
	 * @param value the value to check
	 * @return true if the value is null or ""
	 */
	public boolean isNullOrEmpty(String value){
		return isNull(value) || value.equals("");
	}

	public HttpServletRequest getServletRequest() {
		return servletRequest;
	}



}
