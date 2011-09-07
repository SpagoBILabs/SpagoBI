package it.eng.spagobi.engines.qbe.worksheet.bo;

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