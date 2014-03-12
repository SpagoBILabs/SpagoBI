/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.engines.whatif.services.common;

import it.eng.spagobi.engines.whatif.WhatIfEngineInstance;
import it.eng.spagobi.engines.whatif.services.serializer.PivotJsonHTMLSerializer;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.engines.rest.AbstractRestService;
import it.eng.spagobi.utilities.engines.rest.ExecutionSession;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.apache.log4j.Logger;

import com.eyeq.pivot4j.PivotModel;

/**
 * 
 * @author Zerbetto Davide (davide.zerbetto@eng.it), Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public class AbstractWhatIfEngineService extends AbstractRestService{

	private static final String OUTPUTFORMAT = "OUTPUTFORMAT";
	private static final String OUTPUTFORMAT_JSONHTML = "JSONHTML";
	
	public static transient Logger logger = Logger.getLogger(AbstractWhatIfEngineService.class);

	@Context
	protected HttpServletRequest servletRequest;
	
	/**
	 * Renders the model and return the HTML table
	 * @param request
	 * @return the String that contains the HTML table
	 */
	public String renderModel(PivotModel model){
		logger.debug("IN");
		
		HttpServletRequest servletRequest = getServletRequest();
		String serializedModel = null;
		
		String outputFormat = servletRequest.getParameter(OUTPUTFORMAT);
		
		if(outputFormat!=null && !outputFormat.equals("") && !outputFormat.equals(OUTPUTFORMAT_JSONHTML)){
			logger.debug("Serializing the model in "+outputFormat);
			//TODO: implement the other serializers
		}else{
			logger.debug("Serializing the model in "+OUTPUTFORMAT_JSONHTML);
			serializedModel = PivotJsonHTMLSerializer.renderModel(model);
		}
		
		logger.debug("OUT: table correctly serialized");
		return serializedModel;

	}

	public HttpServletRequest getServletRequest(){
		return servletRequest;
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

}
