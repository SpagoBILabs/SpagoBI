/*
 * SpagoBI, the Open Source Business Intelligence suite
 * © 2005-2015 Engineering Group
 *
 * This file is part of SpagoBI. SpagoBI is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 2.1 of the License, or any later version. 
 * SpagoBI is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. You should have received
 * a copy of the GNU Lesser General Public License along with SpagoBI. If not, see: http://www.gnu.org/licenses/.
 * The complete text of SpagoBI license is included in the COPYING.LESSER file. 
 */
package it.eng.spagobi.commons.serializer;

import it.eng.spagobi.profiling.bean.SbiAttribute;

import java.util.Locale;

import org.json.JSONObject;

public class AttributeJSONSerializer implements Serializer{
	
	public static final String ID = "id";
	public static final String NAME = "name";
	public static final String DESCRIPTION = "description";
	
	public Object serialize(Object o, Locale locale)
			throws SerializationException {
		JSONObject  result = null;
		
		if( !(o instanceof SbiAttribute) ) {
			throw new SerializationException("AttributeJSONSerializer is unable to serialize object of type: " + o.getClass().getName());
		}
		
		try {
			SbiAttribute sbiAttribute = (SbiAttribute)o;
			result = new JSONObject();
			result.put(ID, sbiAttribute.getAttributeId());
			result.put(NAME, sbiAttribute.getAttributeName());
			result.put(DESCRIPTION, sbiAttribute.getDescription() );			
		} catch (Throwable t) {
			throw new SerializationException("An error occurred while serializing object: " + o, t);
		} finally {
			
		}
		
		return result;
	}

}
