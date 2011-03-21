/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2010 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

**/
package it.eng.spagobi.engines.qbe.crosstable.serializer;

import it.eng.qbe.serializer.IDeserializer;
import it.eng.qbe.serializer.IDeserializerFactory;
import it.eng.qbe.serializer.ISerializer;
import it.eng.qbe.serializer.ISerializerFactory;
import it.eng.qbe.serializer.SerializationManager;
import it.eng.spagobi.engines.qbe.crosstable.CrosstabDefinition;
import it.eng.spagobi.engines.qbe.crosstable.serializer.json.CrosstabJSONDeserializer;
import it.eng.spagobi.engines.qbe.crosstable.serializer.json.CrosstabJSONSerializer;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class CrosstabJSONSerializerFactory implements ISerializerFactory{

	static CrosstabJSONSerializerFactory instance;
	
	static CrosstabJSONSerializerFactory getIntsnce() {
		return instance;
	}
	
	static {
		instance = new CrosstabJSONSerializerFactory();
		SerializationManager.registerSerializerFactory(CrosstabDefinition.class, instance);
		
	}
	
	private CrosstabJSONSerializerFactory() {}

	
	public ISerializer getSerializer(String mimeType) {
		return new CrosstabJSONSerializer();
	}

}
