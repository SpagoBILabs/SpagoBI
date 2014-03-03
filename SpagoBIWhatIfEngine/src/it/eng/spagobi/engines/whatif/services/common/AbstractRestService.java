/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.engines.whatif.services.common;

import it.eng.spagobi.engines.whatif.services.AbstractWhatIfEngineService;
import it.eng.spagobi.engines.whatif.services.serializer.PivotJsonHTMLSerializer;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.eyeq.pivot4j.PivotModel;

/**
 * 
 * @author Zerbetto Davide (davide.zerbetto@eng.it), Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public abstract class AbstractRestService extends AbstractWhatIfEngineService{

	private static final String OUTPUTFORMAT = "OUTPUTFORMAT";
	private static final String OUTPUTFORMAT_JSONHTML = "JSONHTML";
	
	public static transient Logger logger = Logger.getLogger(AbstractRestService.class);

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


}
