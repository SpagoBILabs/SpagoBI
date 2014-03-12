/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
package it.eng.spagobi.engines.whatif.services.serializer;

import it.eng.spagobi.utilities.exceptions.SpagoBIRuntimeException;

import org.apache.log4j.Logger;
import org.olap4j.OlapConnection;
import org.olap4j.metadata.Member;

import com.eyeq.pivot4j.PivotModel;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;


public class PivotJsonSerializer implements ISerializer {

	public static transient Logger logger = Logger.getLogger(PivotJsonSerializer.class);
	private static final String mimeType = "application/json";
	
	ObjectMapper mapper;

	public PivotJsonSerializer(OlapConnection connection) {
		mapper = new ObjectMapper();
		SimpleModule simpleModule = new SimpleModule("SimpleModule", new Version(1,0,0,null));
		simpleModule.addSerializer(Member.class, new MemberJsonSerializer());
		simpleModule.addSerializer(PivotModel.class, new PivotJsonHTMLSerializer(connection));
		mapper.registerModule(simpleModule);
	}

	public String serialize(Object object) throws SerializationException {
		try {
			return mapper.writeValueAsString(object);
		} catch (Exception e) {
			logger.error("Error serializing the MemberEntry",e);
			throw new SpagoBIRuntimeException("Error serializing the MemberEntry",e);
		}
	}

	public static String getMimetype() {
		return mimeType;
	}   
	
}
