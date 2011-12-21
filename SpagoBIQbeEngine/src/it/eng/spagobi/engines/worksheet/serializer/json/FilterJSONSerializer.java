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
package it.eng.spagobi.engines.worksheet.serializer.json;

import it.eng.qbe.serializer.ISerializer;
import it.eng.qbe.serializer.SerializationException;
import it.eng.spagobi.engines.worksheet.bo.Filter;
import it.eng.spagobi.utilities.assertion.Assert;

import org.apache.log4j.Logger;
import org.json.JSONObject;

/**
 * @authors Alberto Ghedin (alberto.ghedin@eng.it)
 *
 */
public class FilterJSONSerializer extends AttributeJSONSerializer implements ISerializer {

    public static transient Logger logger = Logger.getLogger(FilterJSONSerializer.class);

	@Override
	public Object serialize(Object o) throws SerializationException {
		JSONObject toReturn = null;
		Filter filter;
				
		Assert.assertNotNull(o, "Input parameter cannot be null");
		Assert.assertTrue(o instanceof Filter, "Unable to serialize objects of type [" + o.getClass().getName() + "]");
		
		try {
			filter = (Filter) o;
			
			toReturn = (JSONObject) super.serialize(o);
			
			String mandatory = filter.isMandatory() ? "yes" : "no";
			String selection = filter.isMultivalue() ? FieldsSerializationConstants.SELECTION_MULTIVALUE : FieldsSerializationConstants.SELECTION_SINGLEVALUE;
			
			toReturn.put(FieldsSerializationConstants.SELECTION, selection);
			toReturn.put(FieldsSerializationConstants.MANDATORY, mandatory);
			
			if(filter.isSplittingFilter()){
				toReturn.put(FieldsSerializationConstants.SPLITTING_FILTER, "on");
			}
			
			

		} catch (Throwable t) {
			throw new SerializationException("An error occurred while serializing object: " + o, t);
		} finally {
			
		}
		
		return toReturn;
	}
    
		
}
