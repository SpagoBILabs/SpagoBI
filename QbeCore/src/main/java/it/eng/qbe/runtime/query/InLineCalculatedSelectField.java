/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.qbe.runtime.query;

import it.eng.spagobi.tools.dataset.common.query.AggregationFunctions;
import it.eng.spagobi.tools.dataset.common.query.IAggregationFunction;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public class InLineCalculatedSelectField extends AbstractSelectField {

	private String expression;
	private String slots;
	private String type;
	private String nature;
	// private Object initialValue;
	private boolean groupByField;
	private IAggregationFunction function;
	private String orderType;

	// private int resetType;
	// private int incrementType;

	public InLineCalculatedSelectField(String alias, String expression, String slots, String type, String nature, boolean included, boolean visible,
			boolean groupByField, String orderType, String function) {
		super(alias, ISelectField.CALCULATED_FIELD, included, visible);
		this.expression = expression;
		this.slots = slots;
		this.type = type;
		this.nature = nature;
		this.groupByField = groupByField;
		setOrderType(orderType);
		setFunction(AggregationFunctions.get(function));
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getSlots() {
		return slots;
	}

	public void setSlots(String slots) {
		this.slots = slots;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String getNature() {
		return nature;
	}

	@Override
	public void setNature(String nature) {
		this.nature = nature;
	}

	@Override
	public ISelectField copy() {
		return null;
	}

	@Override
	public boolean isInLineCalculatedField() {
		return true;
	}

	@Override
	public boolean isGroupByField() {
		return groupByField;
	}

	@Override
	public void setGroupByField(boolean groupByField) {
		this.groupByField = groupByField;
	}

	@Override
	public boolean isOrderByField() {
		return "ASC".equalsIgnoreCase(getOrderType()) || "DESC".equalsIgnoreCase(getOrderType());
	}

	@Override
	public boolean isAscendingOrder() {
		return "ASC".equalsIgnoreCase(getOrderType());
	}

	@Override
	public String getOrderType() {
		return orderType;
	}

	@Override
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public IAggregationFunction getFunction() {
		return function;
	}

	public void setFunction(IAggregationFunction function) {
		this.function = function;
	}

	@Override
	public String getName() {
		return getAlias();
	}

	@Override
	public void setName(String alias) {
		setAlias(alias);
	}

}
