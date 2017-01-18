/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.analiticalmodel.execution.bo.analyticaldrivervalues;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

public class AnalyticalDriverValueList extends ArrayList<AnalyticalDriverValue> {

	private static Logger logger = Logger.getLogger(AnalyticalDriverValueList.class);

	/**
	 * Returns true if the AnalyticalDriverValue' list contains the value specified in input.
	 * The input is compared with the values' value property, i.e. it is not compared with values' description properties.
	 */
	public boolean contains(Object value) {
		Iterator<AnalyticalDriverValue> it = this.iterator();
		while (it.hasNext()) {
			AnalyticalDriverValue analyticalDriverValue = it.next();
			if (analyticalDriverValue.getValue().equals(value)) {
				logger.debug("Value [" + value + "] is a default value");
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the AnalyticalDriverValue object with value property equal to the value specified in input, or null if this object is not found
	 * @param value The value to look for: it is compared with default values' value property
	 * @return The DefaultValue object with value specified in input, or null if this object is not found
	 */
	public AnalyticalDriverValue getAnalyticalDriverValue(Object value) {
		Iterator<AnalyticalDriverValue> it = this.iterator();
		while (it.hasNext()) {
			AnalyticalDriverValue defaultValue = it.next();
			if (defaultValue.getValue().equals(value)) {
				logger.debug("Value [" + value + "] found in this default values' list");
				return defaultValue;
			}
		}
		logger.debug("Value [" + value + "] not found in this default values' list");
		return null;
	}
	
	
	public List<String> getValuesList(){
		logger.debug("IN");
		
		List toReturn = new ArrayList();
		Iterator<AnalyticalDriverValue> it = this.iterator();
		while (it.hasNext()) {
			AnalyticalDriverValue value = it.next();
			toReturn.add(value.getValue());
		}
		logger.debug("OUT");
		return toReturn;
	}
	
	public void printList(String label){
		for (Iterator iterator = this.iterator(); iterator.hasNext();) {
			AnalyticalDriverValue value = (AnalyticalDriverValue)iterator.next();
			logger.debug(label+" - value: "+value.getValue()+ " - description:"+value.getDescription());
		}
	}
	
	
}
