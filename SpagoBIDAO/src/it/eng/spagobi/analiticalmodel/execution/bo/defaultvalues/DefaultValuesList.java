/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice. 
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.analiticalmodel.execution.bo.defaultvalues;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;

public class DefaultValuesList extends ArrayList<DefaultValue> {

	private static Logger logger = Logger.getLogger(DefaultValuesList.class);

	public boolean contains(Object value) {
		Iterator<DefaultValue> it = this.iterator();
		while (it.hasNext()) {
			DefaultValue defaultValue = it.next();
			if (defaultValue.getValue().equals(value)) {
				logger.debug("Value [" + value + "] is a default value");
				return true;
			}
		}
		return false;
	}
	
}
