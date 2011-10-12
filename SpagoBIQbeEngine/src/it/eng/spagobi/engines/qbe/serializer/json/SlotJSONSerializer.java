/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2009 Engineering Ingegneria Informatica S.p.A.

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
package it.eng.spagobi.engines.qbe.serializer.json;

import java.util.Collection;
import java.util.List;

import it.eng.qbe.model.structure.ModelCalculatedField.Slot;
import it.eng.qbe.model.structure.ModelCalculatedField.Slot.MappedValuesDescriptor;
import it.eng.qbe.model.structure.ModelCalculatedField.Slot.MappedValuesPunctualDescriptor;
import it.eng.qbe.model.structure.ModelCalculatedField.Slot.MappedValuesRangeDescriptor;
import it.eng.qbe.serializer.ISerializer;
import it.eng.qbe.serializer.SerializationException;
import it.eng.spagobi.engines.worksheet.bo.Measure;
import it.eng.spagobi.engines.worksheet.serializer.json.FieldsSerializationConstants;
import it.eng.spagobi.utilities.assertion.Assert;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 *
 */
public class SlotJSONSerializer implements ISerializer {
	public static transient String NAME = "name";
	public static transient String VALUESET = "valueset";
	
	public static transient String TYPE = "type";
	public static transient String VALUES = "values";
	
	public static transient String FROM = "type";
	public static transient String INCLUDE_FROM = "includeFrom";
	public static transient String TO = "to";
	public static transient String INCLUDE_TO = "includeTo";

		
    public static transient Logger logger = Logger.getLogger(SlotJSONSerializer.class);

	//@Override
	public Object serialize(Object o) throws SerializationException {
		JSONObject toReturn = null;
		Slot slot;
				
		Assert.assertNotNull(o, "Input parameter cannot be null");
		Assert.assertTrue(o instanceof Slot, "Unable to serialize objects of type [" + o.getClass().getName() + "]");
		
		try {
			toReturn = new JSONObject();
			
			slot = (Slot) o;
			
			toReturn.put(NAME, slot.getName());
			toReturn.put(VALUESET, serializeMappedValuesDescriptors(slot.getMappedValuesDescriptors()));
			

		} catch (Throwable t) {
			throw new SerializationException("An error occurred while serializing object: " + o, t);
		} finally {
			
		}
		
		return toReturn;
	}
    
	private JSONArray serializeMappedValuesDescriptors(List<MappedValuesDescriptor> descriptors) throws SerializationException {
		JSONArray descriptorsJSON;
		
		descriptorsJSON = new JSONArray();
		for(MappedValuesDescriptor descriptor : descriptors) {
			descriptorsJSON.put( serializeMappedValuesDescriptor(descriptor) );
		}
		
		return descriptorsJSON;
	}

	private JSONObject serializeMappedValuesDescriptor(MappedValuesDescriptor descriptor) throws SerializationException {
		JSONObject descriptorJSON;
		
		descriptorJSON = new JSONObject();
		
		try {
			if(descriptor instanceof MappedValuesPunctualDescriptor) {
				MappedValuesPunctualDescriptor punctualDescriptor = (MappedValuesPunctualDescriptor)descriptor;
				descriptorJSON.put(TYPE, "punctual");
				descriptorJSON.put(VALUES, new JSONArray(punctualDescriptor.getValues()));
			} else if(descriptor instanceof MappedValuesRangeDescriptor) {
				MappedValuesRangeDescriptor rangeDescriptor = (MappedValuesRangeDescriptor)descriptor;
				descriptorJSON.put(TYPE, "range");
				descriptorJSON.put(FROM, rangeDescriptor.getMinValue());
				descriptorJSON.put(INCLUDE_FROM, rangeDescriptor.isIncludeMinValue() );
				descriptorJSON.put(TO, rangeDescriptor.getMaxValue());
				descriptorJSON.put(INCLUDE_TO, rangeDescriptor.isIncludeMaxValue());
			} else {
				throw new SerializationException("Impossible to serialize a descriptor of class: " + descriptor.getClass().getName());
			}
		} catch (Throwable t) {
			throw new SerializationException("An error occurred while serializing object: " + descriptor, t);
		} finally {
			
		}
		
		return descriptorJSON;
	}
}
