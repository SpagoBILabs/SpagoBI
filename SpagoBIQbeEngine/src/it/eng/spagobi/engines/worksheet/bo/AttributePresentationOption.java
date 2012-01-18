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
package it.eng.spagobi.engines.worksheet.bo;

import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;

public class AttributePresentationOption extends FieldOption {

	public static final String NAME = "attributePresentation";
	
	public enum AdmissibleValues {code, description, both};
	
	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	public void setValue(Object valueObj) {
		if ( !(valueObj instanceof String) ) {
			throw new SpagoBIEngineRuntimeException("Value for this option must be a string");
		}
		String valueStr = (String) valueObj;
		AdmissibleValues value = null;
		try {
			value = AdmissibleValues.valueOf(valueStr);
		} catch (Exception e) {
			throw new SpagoBIEngineRuntimeException("Value [" + valueStr + "] not valid for this option", e);
		}
		super.setValue(value);
	}
	
	@Override
	public Object getValue() {
		AdmissibleValues value = (AdmissibleValues) super.getValue();
		return value.name();
	}

}
