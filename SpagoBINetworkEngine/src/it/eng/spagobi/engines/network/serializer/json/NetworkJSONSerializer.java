/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package it.eng.spagobi.engines.network.serializer.json;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.eng.spagobi.engines.network.bean.JSONNetwork;
import it.eng.spagobi.engines.network.serializer.ISerializer;
import it.eng.spagobi.engines.network.serializer.SerializationException;




/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public class NetworkJSONSerializer implements ISerializer {
	
	public static transient Logger logger = Logger.getLogger(NetworkJSONSerializer.class);
	/* (non-Javadoc)
	 * @see it.eng.spagobi.engines.network.serializer.ISerializer#serialize(java.lang.Object)
	 */
	public Object serialize(Object object) throws SerializationException {
		ObjectMapper mapper = new ObjectMapper();
		String string ="";
		try {
			string = mapper.writeValueAsString((JSONNetwork)object);
		} catch (Exception e) {
			logger.error("Error serializing the network",e);
			throw new SerializationException("Error serializing the network",e);
		}
		return  string; 
	}
	
	

}
