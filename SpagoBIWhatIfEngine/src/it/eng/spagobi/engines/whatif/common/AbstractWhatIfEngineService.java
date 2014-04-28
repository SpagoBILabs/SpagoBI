/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.engines.whatif.common;

import java.util.ArrayList;
import java.util.List;

import it.eng.spagobi.engines.whatif.WhatIfEngineInstance;
import it.eng.spagobi.engines.whatif.member.SbiMember;
import it.eng.spagobi.engines.whatif.model.ModelConfig;
import it.eng.spagobi.engines.whatif.serializer.SerializationException;
import it.eng.spagobi.engines.whatif.serializer.SerializationManager;
import it.eng.spagobi.utilities.engines.EngineConstants;
import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;
import it.eng.spagobi.utilities.engines.rest.AbstractRestService;
import it.eng.spagobi.utilities.engines.rest.ExecutionSession;
import it.eng.spagobi.utilities.exceptions.SpagoBIEngineRestServiceRuntimeException;
import it.eng.spagobi.utilities.rest.RestUtilities;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.apache.log4j.Logger;
import org.olap4j.metadata.Member;

import com.eyeq.pivot4j.PivotModel;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * 
 * @author Zerbetto Davide (davide.zerbetto@eng.it), Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public class AbstractWhatIfEngineService extends AbstractRestService{

	private static final String OUTPUTFORMAT = "OUTPUTFORMAT";
	private static final String OUTPUTFORMAT_JSONHTML = "application/json";
	
	
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
		
		String serializedModel = null;
	

		try {
			serializedModel = (String) serialize(model);
		} catch (SerializationException e) {
			logger.error("Error serializing the pivot", e);
			throw new SpagoBIEngineRuntimeException("Error serializing the pivot",e);
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
	
	public PivotModel getPivotModel(){
		return getWhatIfEngineInstance().getPivotModel();
	}
	
	public ModelConfig getModelConfig(){
		return getWhatIfEngineInstance().getModelConfig();
	}
	
	
	public String getOutputFormat(){
		String outputFormat = servletRequest.getParameter(OUTPUTFORMAT);
		
		if(outputFormat==null  || outputFormat.equals("") ){
			logger.debug("the output format is null.. use the default one"+OUTPUTFORMAT_JSONHTML);
			outputFormat = OUTPUTFORMAT_JSONHTML;
		}

		return outputFormat;
	}
	
	public String serialize(Object obj) throws SerializationException{
		String outputFormat = getOutputFormat();
		return (String) SerializationManager.serialize(outputFormat, obj);
	}

	public Object deserialize(String obj, Class clazz) throws SerializationException{
		String outputFormat = getOutputFormat();
		return SerializationManager.deserialize(outputFormat, obj, clazz);
	}

	public Object deserialize(String obj, TypeReference object) throws SerializationException{
		String outputFormat = getOutputFormat();
		return SerializationManager.deserialize(outputFormat, obj, object);
	}
	
	
	public List<Member> getMembersFromBody(){
		logger.debug("Getting the members from the request");
		List<SbiMember> sbiMembers = null;
		List<Member> members = new ArrayList<Member>();
		String membersString=null;
		
		try {
			membersString = RestUtilities.readBody(getServletRequest());
			TypeReference<List<SbiMember>> type = new TypeReference<List<SbiMember>>() {};
			sbiMembers = (List<SbiMember>)deserialize(membersString, type);
			for (int i = 0; i < sbiMembers.size(); i++) {
				members.add(sbiMembers.get(i).getMember(getPivotModel().getCube()));
			}
		} catch (Exception e) {
			logger.error("Error loading the members from the request ", e);
			throw new SpagoBIEngineRestServiceRuntimeException("generic.error.request.members.getting", getLocale(), e);
		}

		
		return members;
	}
	
	

	
}
