/* SpagoBI, the Open Source Business Intelligence suite

 * Copyright (C) 2012 Engineering Ingegneria Informatica S.p.A. - SpagoBI Competency Center
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0, without the "Incompatible With Secondary Licenses" notice.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package it.eng.qbe.runtime.query;

/**
 * @author Andrea Gioia (andrea.gioia@eng.it)
 */
public abstract class AbstractSelectField implements ISelectField {
	private String alias;
	private String type;
	private boolean visible;
	private boolean included;

	private boolean groupByField;
	private String orderType;

	protected String nature;

	public static final String ORDER_ASC = "ASC";
	public static final String ORDER_DESC = "DESC";

	public AbstractSelectField(String alias, String type, boolean included, boolean visible) {
		setAlias(alias);
		setType(type);
		setIncluded(included);
		setVisible(visible);
		groupByField = false;
		orderType = null;
	}

	@Override
	public String getAlias() {
		return alias;
	}

	@Override
	public void setAlias(String alias) {
		this.alias = alias;
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
	public boolean isSimpleField() {
		return this.SIMPLE_FIELD.equalsIgnoreCase(type);
	}

	@Override
	public boolean isInLineCalculatedField() {
		return this.IN_LINE_CALCULATED_FIELD.equalsIgnoreCase(type);
	}

	@Override
	public boolean isCalculatedField() {
		return this.CALCULATED_FIELD.equalsIgnoreCase(type);
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
		return ORDER_ASC.equalsIgnoreCase(getOrderType()) || ORDER_DESC.equalsIgnoreCase(getOrderType());
	}

	@Override
	public boolean isAscendingOrder() {
		return ORDER_ASC.equalsIgnoreCase(getOrderType());
	}

	@Override
	public String getOrderType() {
		return orderType;
	}

	@Override
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public boolean isIncluded() {
		return included;
	}

	@Override
	public void setIncluded(boolean included) {
		this.included = included;
	}

	@Override
	public String getNature() {
		return this.nature;
	}

	@Override
	public void setNature(String nature) {
		this.nature = nature;
	}

}
