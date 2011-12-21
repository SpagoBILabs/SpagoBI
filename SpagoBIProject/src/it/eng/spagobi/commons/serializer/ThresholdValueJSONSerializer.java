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

import it.eng.spagobi.kpi.threshold.bo.ThresholdValue;

import java.util.Locale;

import org.json.JSONObject;

public class ThresholdValueJSONSerializer  implements Serializer{
	private static final String THR_VAL_ID = "idThrVal";
	private static final String THR_VAL_LABEL = "label";
	private static final String THR_VAL_POSITION = "position";
	private static final String THR_VAL_MIN = "min";
	private static final String THR_VAL_MIN_INCLUDED = "minIncluded";
	private static final String THR_VAL_MAX = "max";
	private static final String THR_VAL_MAX_INCLUDED = "maxIncluded";
	private static final String THR_VAL_VALUE = "val";
	private static final String THR_VAL_COLOR = "color";
	private static final String THR_VAL_SEVERITY_CD = "severityCd";
	private static final String THR_VAL_SEVERITY_ID = "severityId";
	
	public Object serialize(Object o, Locale locale)
			throws SerializationException {
		JSONObject  result = null;
		
		if( !(o instanceof ThresholdValue) ) {
			throw new SerializationException("ThresholdValueJSONSerializer is unable to serialize object of type: " + o.getClass().getName());
		}
		
		try {
			ThresholdValue thrVal = (ThresholdValue)o;
			result = new JSONObject();
			result.put(THR_VAL_ID,  thrVal.getId());
			result.put(THR_VAL_LABEL,  thrVal.getLabel());
			result.put(THR_VAL_POSITION,  thrVal.getPosition());
			result.put(THR_VAL_MIN,  thrVal.getMinValue());
			result.put(THR_VAL_MIN_INCLUDED,  thrVal.getMinClosed());
			result.put(THR_VAL_MAX,  thrVal.getMaxValue());
			result.put(THR_VAL_MAX_INCLUDED,  thrVal.getMaxClosed());
			result.put(THR_VAL_VALUE,  thrVal.getValue());
			result.put(THR_VAL_COLOR,  thrVal.getColourString());
			result.put(THR_VAL_SEVERITY_ID,  thrVal.getSeverityId());
			result.put(THR_VAL_SEVERITY_CD,  thrVal.getSeverityCd());

		} catch (Throwable t) {
			throw new SerializationException("An error occurred while serializing object: " + o, t);
		} finally {
			
		}
		
		return result;
	}
}
