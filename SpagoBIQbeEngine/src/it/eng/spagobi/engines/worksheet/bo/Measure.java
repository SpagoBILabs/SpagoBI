/* SpagoBI, the Open Source Business Intelligence suite

* © 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.spagobi.engines.worksheet.bo;

import it.eng.spagobi.tools.dataset.common.query.AggregationFunctions;
import it.eng.spagobi.tools.dataset.common.query.IAggregationFunction;

public class Measure extends Field {
	IAggregationFunction function = null;
	public Measure(String entityId, String alias, String iconCls, String nature, String function) {
		super(entityId, alias, iconCls, nature);
		this.function = AggregationFunctions.get(function);
	}
	public IAggregationFunction getAggregationFunction() {
		return function;
	}
}