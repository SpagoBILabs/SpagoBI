/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.analiticalmodel.execution.bo.analyticaldrivervalues;

public class AnalyticalDriverValue {
	
	private Object value;
	private Object description;

	public AnalyticalDriverValue() {
	}
	
	public AnalyticalDriverValue(Object value, Object description) {
		super();
		this.value = value;
		this.description = description;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Object getDescription() {
		return description;
	}

	public void setDescription(Object description) {
		this.description = description;
	}
	
	@Override
	public String toString() {
		return "AnalyticalDriverValue [value=" + value + ", description=" + description
				+ "]";
	}
}