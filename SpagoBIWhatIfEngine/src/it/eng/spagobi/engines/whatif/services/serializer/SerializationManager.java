/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.whatif.services.serializer;

import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public class SerializationManager {
	
	static Map<String, ISerializer> serializerFactoryMappings;
	static Map<String, IDeserializer> deserializerFactoryMappings;
	
	static {
		serializerFactoryMappings = new HashMap<String, ISerializer>();
		deserializerFactoryMappings = new HashMap<String, IDeserializer>();
	}
	
	public static void registerSerializer(String mimeType, ISerializer serializer) {
		serializerFactoryMappings.put(mimeType, serializer);
	}
	
	public static void registerDeserializerFactory(String mimeType, ISerializer deserializer) {
		serializerFactoryMappings.put(mimeType, deserializer);
	}

	public static ISerializer getSerializer(String mimeType) {
		return  serializerFactoryMappings.get( mimeType );
	}
	
	public static ISerializer getDefaultSerializer() {
		if(serializerFactoryMappings!=null && serializerFactoryMappings.size()>0){
			return  serializerFactoryMappings.get( serializerFactoryMappings.keySet().iterator().next() );
		}
		throw new SpagoBIEngineRuntimeException("No serializer has been registerd");
	}

}

