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
package it.eng.qbe.serializer;


import it.eng.qbe.commons.serializer.SerializationException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class SerializationManager {
	
	static Map<Class<? extends Object>, ISerializerFactory> serializerFactoryMappings;
	static Map<Class<? extends Object>, IDeserializerFactory> deserializerFactoryMappings;
	
	static {
		serializerFactoryMappings = new HashMap<Class<? extends Object>, ISerializerFactory>();
		deserializerFactoryMappings = new HashMap<Class<? extends Object>, IDeserializerFactory>();
	}
	
	public static void registerSerializerFactory(Class<? extends Object> c, ISerializerFactory serializerFactory) {
		serializerFactoryMappings.put(c, serializerFactory);
	}
	
	public static void registerDeserializerFactory(Class<? extends Object> c, IDeserializerFactory deserializerFactory) {
		deserializerFactoryMappings.put(c, deserializerFactory);
	}
	
	public static Object serialize(Object o, String mimeType) throws SerializationException {
		return getSerializer(o.getClass(), mimeType).serialize(o);
	}
	
	public static Object deserialize(Object o, String mimeType, Class<? extends Object> c) throws SerializationException {
		return getDeserializer(c, mimeType).deserialize(o);
	}
	
	public static ISerializer getSerializer(Class<? extends Object> c, String mimeType) {
		return getSerializerFactory(c).getSerializer(mimeType);
	}
	
	public static IDeserializer getDeserializer(Class<? extends Object> c, String mimeType) {
		return getDeserializerFactory(c).getDeserializer(mimeType);
	}
	
	public static ISerializerFactory getSerializerFactory(Class<? extends Object> c) {
		return serializerFactoryMappings.get( c );
	}
	
	public static IDeserializerFactory getDeserializerFactory(Class<? extends Object> c) {
		return deserializerFactoryMappings.get( c );
	}
}

