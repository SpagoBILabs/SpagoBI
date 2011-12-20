/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2004 - 2011 Engineering Ingegneria Informatica S.p.A.

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

import it.eng.qbe.serializer.SerializationManager;
import it.eng.spagobi.engines.qbe.crosstable.serializer.json.CrosstabJSONDeserializer;
import it.eng.spagobi.engines.worksheet.widgets.CrosstabDefinition;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class CrosstabDeserializerFactory implements IDeserializerFactory {
	
	static CrosstabDeserializerFactory instance;
	
	static CrosstabDeserializerFactory getIntsnce() {
		return instance;
	}
	
	static {
		instance = new CrosstabDeserializerFactory();
		SerializationManager.registerDeserializerFactory(CrosstabDefinition.class, instance);
		
	}
	
	public static CrosstabDeserializerFactory getInstance() {
		if (instance == null) {
			instance = new CrosstabDeserializerFactory();
		}
		return instance;
	}
	
	private CrosstabDeserializerFactory() {}

	public IDeserializer getDeserializer(String mimeType) {
		return new CrosstabJSONDeserializer();
	}

}
