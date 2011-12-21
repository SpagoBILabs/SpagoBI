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

import it.eng.spagobi.tools.dataset.metadata.SbiDataSetConfig;

import java.util.Locale;

import org.json.JSONObject;

public class SbiDataSetConfigJSONSerializer implements Serializer {

	public static final String DATASET_ID = "id";
	private static final String DATASET_NAME = "name";
	private static final String DATASET_DESCRIPTION = "description";
	private static final String DATASET_LABEL = "label";
	
	public Object serialize(Object o, Locale locale) throws SerializationException {
		JSONObject  result = null;
		
		if( !(o instanceof SbiDataSetConfig) ) {
			throw new SerializationException("SbiDataSetConfigJSONSerializer is unable to serialize object of type: " + o.getClass().getName());
		}
		
		try {
			SbiDataSetConfig ds = (SbiDataSetConfig)o;
			result = new JSONObject();
			
			result.put(DATASET_ID, ds.getDsId() );
			result.put(DATASET_NAME, ds.getName() );
			result.put(DATASET_DESCRIPTION, ds.getDescription() );
			result.put(DATASET_LABEL, ds.getLabel() );		
			
		} catch (Throwable t) {
			throw new SerializationException("An error occurred while serializing object: " + o, t);
		} finally {
			
		}
		
		return result;
	}

}