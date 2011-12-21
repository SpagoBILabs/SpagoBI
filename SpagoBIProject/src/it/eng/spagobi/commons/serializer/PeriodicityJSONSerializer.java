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

import it.eng.spagobi.kpi.config.bo.Periodicity;
import it.eng.spagobi.kpi.model.bo.Resource;

import java.util.Locale;
import org.json.JSONObject;

public class PeriodicityJSONSerializer implements Serializer {

	public static final String ID = "idPr";
	private static final String NAME = "name";
	private static final String MONTHS = "months";
	private static final String DAYS = "days";
	private static final String HOURS = "hours";
	private static final String MINUTES = "mins";

	
	public Object serialize(Object o, Locale locale) throws SerializationException {
		JSONObject  result = null;
		
		if( !(o instanceof Periodicity) ) {
			throw new SerializationException("PeriodicityJSONSerializer is unable to serialize object of type: " + o.getClass().getName());
		}
		
		try {
			Periodicity per = (Periodicity)o;
			result = new JSONObject();
			
			result.put(ID, per.getIdKpiPeriodicity());
			result.put(NAME,per.getName() );
			result.put(MONTHS, per.getMonths() );
			result.put(DAYS, per.getDays() );
			result.put(HOURS, per.getHours() );
			result.put(MINUTES, per.getMinutes() );			
			
		} catch (Throwable t) {
			throw new SerializationException("An error occurred while serializing object: " + o, t);
		} finally {
			
		}
		
		return result;
	}

}
