/**
 * SpagoBI - The Business Intelligence Free Platform
 *
 * Copyright (C) 2004 - 2008 Engineering Ingegneria Informatica S.p.A.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 **/
package it.eng.qbe.query.serializer;

import it.eng.qbe.query.serializer.json.QueryJSONDeserializer;
import it.eng.qbe.query.serializer.json.QueryJSONSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class SerializerFactory {
	
	static Map<String, IQuerySerializer> serializerMappings;
	static Map<String, IQueryDeserializer> deserializerMappings;
	
	static {
		serializerMappings = new HashMap();
		serializerMappings.put( "application/json", new QueryJSONSerializer() );
		
		deserializerMappings = new HashMap();
		deserializerMappings.put( "application/json", new QueryJSONDeserializer() );
	}
	
	public static IQuerySerializer getSerializer(String mimeType) {
		return serializerMappings.get( mimeType );
	}
	
	public static IQueryDeserializer getDeserializer(String mimeType) {
		return deserializerMappings.get( mimeType );
	}
}

