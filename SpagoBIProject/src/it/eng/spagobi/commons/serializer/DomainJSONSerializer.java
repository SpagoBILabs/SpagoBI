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

import java.util.Locale;

import org.json.JSONObject;

import it.eng.spagobi.commons.bo.Domain;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class DomainJSONSerializer implements Serializer {
	
	public static final String DOMAIN_CODE = "DOMAIN_CD";
	public static final String DOMAIN_NAME = "DOMAIN_NM";
	
	public static final String VALUE_ID = "VALUE_ID";
	public static final String VALUE_CODE = "VALUE_CD";
	public static final String VALUE_NAME = "VALUE_NM";
	public static final String VALUE_DECRIPTION = "VALUE_DS";
	
	
	public Object serialize(Object o, Locale locale) throws SerializationException {
		JSONObject  result = null;
		
		if( !(o instanceof Domain) ) {
			throw new SerializationException("DomainJSONSerializer is unable to serialize object of type: " + o.getClass().getName());
		}
		
		try {
			Domain domain = (Domain)o;
			result = new JSONObject();
			result.put(DOMAIN_CODE, domain.getDomainCode() ); // BIOBJ_TYPE
			result.put(DOMAIN_NAME, domain.getDomainName() ); // BI Object types
			
			result.put(VALUE_ID, domain.getValueId() ); // ex. 1
			result.put(VALUE_CODE, domain.getValueCd() ); // REPORT
			result.put(VALUE_NAME, domain.getValueName() ); // ex. Report
			result.put(VALUE_DECRIPTION, domain.getValueDescription() ); // Basic business intelligence objects type
		} catch (Throwable t) {
			throw new SerializationException("An error occurred while serializing object: " + o, t);
		} finally {
			
		}
		
		return result;
	}

}
